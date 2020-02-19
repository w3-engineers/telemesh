package com.w3engineers.unicef.telemesh.data.helper.inappupdate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.net.Network;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;

import com.google.gson.JsonObject;
import com.w3engineers.ext.strom.util.helper.Toaster;
import com.w3engineers.ext.strom.util.helper.data.local.SharedPref;
import com.w3engineers.unicef.TeleMeshApplication;
import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.R;
import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;
import com.w3engineers.unicef.telemesh.data.remote.RetrofitInterface;
import com.w3engineers.unicef.telemesh.data.remote.RetrofitService;
import com.w3engineers.unicef.telemesh.databinding.DialogAppUpdateWarningBinding;
import com.w3engineers.unicef.util.helper.LanguageUtil;
import com.w3engineers.unicef.util.helper.StorageUtil;
import com.w3engineers.unicef.util.helper.UpdateAppConfigDownloadTask;
import com.we3ngineers.localserver.NanoHTTPD.NanoHTTPD;
import com.we3ngineers.localserver.NanoHTTPD.SimpleWebServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.nio.charset.StandardCharsets.UTF_8;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */

public class InAppUpdate {


    private static final String MAIN_JSON = "updatedJSon.json";
    public static final String LIVE_JSON_URL = AppCredentials.getInstance().getFileRepoLink() + MAIN_JSON; // Configure json file that was uploaded in Main server
    public static final String MAIN_APK = "updatedApk.apk";
    private final String LOCAL_IP_FIRST_PORTION = "/192";
    private static File rootFile;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;
    private static final int PORT = 8990;
    private NanoHTTPD mServer;
    private static boolean isServerRunning;
    private static boolean isAppUpdateProcessStart;

    // lock the default constructor
    private InAppUpdate() {
        // we can do initial stuff or first time stuff in here
    }

    public static InAppUpdate getInstance(Context context) {
        mContext = context;
        rootFile = mContext.getApplicationContext().getFilesDir();
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        @SuppressLint("StaticFieldLeak")
        private static InAppUpdate INSTANCE = new InAppUpdate();
    }

    public void showAppInstallDialog(String json, Context context) {
        try {
            if (json == null) return;
            JSONObject jsonObject = new JSONObject(json);
            long versionCode = jsonObject.optLong(Constants.InAppUpdate.LATEST_VERSION_CODE_KEY);

            if (versionCode <= getAppVersion().getVersionCode()) return;

            String version = jsonObject.optString(Constants.InAppUpdate.LATEST_VERSION_KEY);
            String releaseNote = jsonObject.optString(Constants.InAppUpdate.RELEASE_NOTE_KEY);
            String url = jsonObject.optString(Constants.InAppUpdate.URL_KEY);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            LayoutInflater inflater = LayoutInflater.from(context);
            DialogAppUpdateWarningBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_app_update_warning, null, false);

            builder.setView(binding.getRoot());

            String message = LanguageUtil.getString(R.string.a_new_version) + " " + version + " " + LanguageUtil.getString(R.string.is_available_for_telemesh);

            binding.textViewMessage.setText(message);
            binding.textViewReleaseNote.setText(releaseNote);

            binding.buttonCancel.setText(LanguageUtil.getString(R.string.cancel));
            binding.buttonUpdate.setText(LanguageUtil.getString(R.string.update));
            binding.textViewWarning.setText(LanguageUtil.getString(R.string.do_you_want_to_update));
            binding.checkboxAskMeLater.setText(LanguageUtil.getString(R.string.ask_me_later));
            binding.textViewTitle.setText(LanguageUtil.getString(R.string.app_update));

            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);

            String finalUrl1 = url;
            binding.buttonCancel.setOnClickListener(v -> {
                dialog.dismiss();
                setAppUpdateProcess(false);

                if (finalUrl1.contains("config")) {
                    SharedPref.getSharedPref(context).write(Constants.preferenceKey.UPDATE_APP_VERSION, versionCode);
                    SharedPref.getSharedPref(context).write(Constants.preferenceKey.UPDATE_APP_URL, finalUrl1);
                }

            });

            SharedPref sharedPref = SharedPref.getSharedPref(TeleMeshApplication.getContext());


            binding.checkboxAskMeLater.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sharedPref.write(Constants.preferenceKey.ASK_ME_LATER, isChecked);
                    sharedPref.write(Constants.preferenceKey.ASK_ME_LATER_TIME, System.currentTimeMillis());
                }
            });

            url = url.replace(MAIN_APK, "");
            String finalUrl = url;
            binding.buttonUpdate.setOnClickListener(v -> {
                dialog.dismiss();

                if (StorageUtil.getFreeMemory() > Constants.MINIMUM_SPACE) {
                    AppInstaller.downloadApkFile(finalUrl, context, null);
                } else {
                    Toaster.showShort(LanguageUtil.getString(R.string.phone_storage_not_enough));
                }

            });

            dialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method will be called in every time when main activity open
     * Why? Because we don`t know my apk is updated or not.
     * <p>
     * One solution may have: Save the previous version in shared preference and
     * check the current app version. Then call it
     */
    public void prepareLocalServer() {
        File apkFile = backUpMainApk();
        File jsonFile = createAndUploadJsonFile();

        File f1 = new File(rootFile, MAIN_JSON);
        copyFile(jsonFile, f1);
        File f2 = new File(rootFile, MAIN_APK);
        copyFile(apkFile, f2);

        startLocalServer();
    }

    private void startLocalServer() {

        InetAddress tempAddress;

        final InetAddress myAddress = IpAddressHelper.getMyDeviceInetAddress(true);

        if (myAddress != null && myAddress.toString().contains(LOCAL_IP_FIRST_PORTION)) {
            tempAddress = myAddress;
        } else {
            tempAddress = IpAddressHelper.getLocalIpAddress();

        }

        String myIpAddress = tempAddress.getHostAddress();
        mServer = new SimpleWebServer(myIpAddress, PORT, rootFile, false);

        if (!mServer.isAlive()) {
            try {
                mServer.start();
                isServerRunning = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isServerRunning() {
        return isServerRunning;
    }

  /*  public void stopServer() {
        if (mServer != null) {
            try {
                mServer.stop();
                isServerRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }*/

    public void setAppUpdateProcess(boolean isUpdating) {
        isAppUpdateProcessStart = isUpdating;
    }

    public boolean isAppUpdating() {
        return isAppUpdateProcessStart;
    }

    public InAppUpdateModel getAppVersion() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;
            int versionCode = BuildConfig.VERSION_CODE;
            InAppUpdateModel model = new InAppUpdateModel();
            model.setVersionName(version);
            model.setVersionCode(versionCode);
            return model;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMyLocalServerLink() {
        InetAddress tempAddress;
        final InetAddress myAddress = IpAddressHelper.getMyDeviceInetAddress(true);
        if (myAddress != null && myAddress.toString().contains(LOCAL_IP_FIRST_PORTION)) {
            tempAddress = myAddress;
        } else {
            tempAddress = IpAddressHelper.getLocalIpAddress();
        }
        if (tempAddress != null) {
            String myIpAddress = tempAddress.getHostAddress();
            myIpAddress = "http://" + myIpAddress + ":" + PORT + "/" + MAIN_JSON; // we just replace MAIN_JSON for testing
            return myIpAddress;
        } else {
            return null;
        }
    }

    /**
     * This method is responsible for upload updated apk
     * in local server. for sharing app
     *
     * @return Apk file
     */
    private File backUpMainApk() {

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List pkgAppsList = mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
        String myApplicationPackageName = mContext.getPackageName();
        String myApplicationName = mContext.getResources().getString(R.string.app_name);
        for (Object object : pkgAppsList) {

            ResolveInfo resolveInfo = (ResolveInfo) object;
            File appFile = new File(resolveInfo.activityInfo.applicationInfo.publicSourceDir);

            String file_name = resolveInfo.loadLabel(mContext.getPackageManager()).toString();

            if (file_name.equalsIgnoreCase(myApplicationName) &&
                    appFile.toString().contains(myApplicationPackageName)) {
                return appFile;
            }
        }
        return null;
    }

    private File createAndUploadJsonFile() {
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;

            InetAddress tempAddress;

            final InetAddress myAddress = IpAddressHelper.getMyDeviceInetAddress(true);

            if (myAddress != null && myAddress.toString().contains(LOCAL_IP_FIRST_PORTION)) {
                tempAddress = myAddress;
            } else {
                tempAddress = IpAddressHelper.getLocalIpAddress();

            }

            String myIpAddress = tempAddress.getHostAddress();
            myIpAddress = "http://" + myIpAddress + ":" + PORT + "/";

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(Constants.InAppUpdate.LATEST_VERSION_KEY, version);
            jsonObject.addProperty(Constants.InAppUpdate.LATEST_VERSION_CODE_KEY, "" + BuildConfig.VERSION_CODE);
            jsonObject.addProperty(Constants.InAppUpdate.URL_KEY, myIpAddress + MAIN_APK); // TODO change correct url
            jsonObject.addProperty(Constants.InAppUpdate.RELEASE_NOTE_KEY, "Some feature added and bug fixed");

            File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                    mContext.getString(R.string.app_name));

            file.mkdir();

            File jsonFile = new File(file, MAIN_JSON);
            try {
                Writer output = new BufferedWriter(new FileWriter(jsonFile));
                output.write(jsonObject.toString());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/" +
                mContext.getString(R.string.app_name) + "/" + MAIN_JSON);
        return file;
    }

    private void copyFile(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void downloadAppUpdateConfig(Network network) {
        setAppUpdateProcess(true);

        Log.d("FileDownload","Downloading process server hit");

        RetrofitInterface downloadService = RetrofitService.createService(RetrofitInterface.class, AppCredentials.getInstance().getFileRepoLink(), network);
        Call<ResponseBody> call = downloadService.downloadFileByUrl(MAIN_JSON);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    new UpdateAppConfigDownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("FileDownload","Downloading process Error "+t.getMessage());
            }
        });
    }

    public void checkForUpdate(Context context, String url) {

        Log.e("native_test", "url:: " + url);

        if (isAppUpdating()) return;

        setAppUpdateProcess(true);

        new JsonTask(context).execute(url);
    }

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {
        Context context;

        public JsonTask(Context context) {
            this.context = context;
        }

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();

                String authString = (AppCredentials.getInstance().getAuthUserName() + ":" + AppCredentials.getInstance().getAuthPassword());
                byte[] data1 = authString.getBytes(UTF_8);
                String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);


               /* Log.e("HttpError", "Credential " +userName+" password: "+userPass);
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userName, userPass.toCharArray());
                    }
                });*/

                connection.setRequestProperty("Authorization", "Basic " + base64);

                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    //here u ll get whole response...... :-)
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            showAppInstallDialog(result, context);
        }
    }
}
