package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;

import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

public class UpdateAppConfigDownloadTask extends AsyncTask<String, Void, String> {
    private Context mContext;

    public UpdateAppConfigDownloadTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();

            String userName = AppCredentials.getInstance().getAuthUserName();
            String userPass = AppCredentials.getInstance().getAuthPassword();

            String authString = (userName+":"+userPass);
            byte[] data1 = authString.getBytes(UTF_8);
            String base64 = Base64.encodeToString(data1, Base64.NO_WRAP);


            connection.setRequestProperty("Authorization", "Basic "+base64);

            connection.connect();
            InputStream stream = connection.getInputStream();
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        ViperDataProcessor.getInstance().processUpdateAppConfigJson(s);
    }
}
