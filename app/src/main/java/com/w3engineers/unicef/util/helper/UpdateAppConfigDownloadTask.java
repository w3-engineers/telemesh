package com.w3engineers.unicef.util.helper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;


public class UpdateAppConfigDownloadTask extends AsyncTask<ResponseBody, Void, String> {

    @Override
    protected String doInBackground(ResponseBody... params) {

        BufferedReader reader = null;
        try {
            Log.d("FileDownload","Downloaded complete");
            InputStream stream = params[0].byteStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        ViperDataProcessor.getInstance().processUpdateAppConfigJson(s);


    }
}
