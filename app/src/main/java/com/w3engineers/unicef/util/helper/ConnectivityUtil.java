package com.w3engineers.unicef.util.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.functions.BiConsumer;

public class ConnectivityUtil {

    private static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void isInternetAvailable(Context context, BiConsumer<String, Boolean> consumer) {
        new Thread(() -> {
            try{
                if (isNetworkAvailable(context)) {

                    HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    consumer.accept("", urlc.getResponseCode() == 200);

                }else {
                    handleException(consumer);
                }
            }catch(Exception e){
                handleException(consumer);
            }

        }).start();

    }

    public static void handleException(BiConsumer<String, Boolean> consumer){
        try {
            consumer.accept("", false);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
