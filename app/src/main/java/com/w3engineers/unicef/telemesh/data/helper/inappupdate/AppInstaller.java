package com.w3engineers.unicef.telemesh.data.helper.inappupdate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.net.Network;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import android.util.Pair;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.remote.RetrofitInterface;
import com.w3engineers.unicef.telemesh.data.remote.RetrofitService;
import com.w3engineers.unicef.telemesh.databinding.DialogAppInstallProgressBinding;
import com.w3engineers.unicef.util.helper.LanguageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


public class AppInstaller {
    @SuppressLint("StaticFieldLeak")
    private static DownloadZipFileTask downloadZipFileTask;
    private static final String TAG = "InAppUpdateTest";
    public static boolean isAppUpdating;
    @SuppressLint("StaticFieldLeak")
    private static DialogAppInstallProgressBinding binding;
    private static AlertDialog dialog;
    private static Context mContext;

    public static void downloadApkFile(String baseUrl, Context context, Network network) {

        if (isAppUpdating) return;

        isAppUpdating = true;
        mContext = context;
        if (baseUrl.contains("@")) {
            String[] url = baseUrl.split("@");
            baseUrl = "https://" + url[1];
        }

        RetrofitInterface downloadService = RetrofitService.createService(RetrofitInterface.class, baseUrl, network);
        Call<ResponseBody> call = downloadService.downloadFileByUrl("updatedApk.apk");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    //Toast.makeText(TeleMeshApplication.getContext(), "Downloading...", Toast.LENGTH_SHORT).show();

                    downloadZipFileTask = new DownloadZipFileTask(context);
                    downloadZipFileTask.execute(response.body());

                } else {
                    Timber.tag(TAG).d("Connection failed %s", response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
                isAppUpdating = false;
                InAppUpdate.getInstance(TeleMeshApplication.getContext()).setAppUpdateProcess(false);
            }
        });
    }


    private static class DownloadZipFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, Boolean> {
        @SuppressLint("StaticFieldLeak")
        private final Context context;

        public DownloadZipFileTask(Context context) {
            this.context = context;

            showDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call
            boolean isDownloadSuccess = saveToDisk(urls[0]);
            return isDownloadSuccess;
        }

        protected void onProgressUpdate(Pair<Integer, Long>... progress) {

            if (progress[0].first >= 100) {
                Toast.makeText(context, "Internet connection not available", Toast.LENGTH_SHORT).show();
            }


            if (progress[0].second > 0) {
                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
                //progressBar.setProgress(currentProgress);
                binding.progressBar.setProgress(currentProgress);

                // txtProgressPercent.setText("Progress " + currentProgress + "%");

            }

            if (progress[0].first == -1) {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

        }

        public void doProgress(Pair<Integer, Long> progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(Boolean isDownloadSuccess) {

            if(!isDownloadSuccess){
                return;
            }

            File destinationFile = getDownloadFileUrl(mContext);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                destinationFile = new File(context.getExternalFilesDir(""), "updatedApk.apk");
            } else {
                destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "updatedApk.apk");
            }*/

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                Uri apkUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", destinationFile);
                intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                Uri apkUri = Uri.fromFile(destinationFile);
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            context.startActivity(intent);

            isAppUpdating = false;
            InAppUpdate.getInstance(TeleMeshApplication.getContext()).setAppUpdateProcess(false);
        }
    }

    public static boolean saveToDisk(ResponseBody body) {
        try {


            File destinationFile = getDownloadFileUrl(mContext);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                destinationFile = new File(mContext.getExternalFilesDir(""), filename);
            } else {
                destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);
            }*/

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte[] data = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadZipFileTask.doProgress(pairs);
                }

                outputStream.flush();

                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadZipFileTask.doProgress(pairs);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Pair<Integer, Long> pairs = new Pair<>(-1, (long) -1);
                downloadZipFileTask.doProgress(pairs);
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private static File getDownloadFileUrl(Context context){
        File destinationFile = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            destinationFile = new File(context.getExternalFilesDir(""), "updatedApk.apk");
        } else {
            destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "updatedApk.apk");
        }
        return destinationFile;
    }

    private static void showDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_app_install_progress, null, false);
        builder.setView(binding.getRoot());
        binding.textViewTitle.setText(LanguageUtil.getString(R.string.app_updating_please_wait));
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
