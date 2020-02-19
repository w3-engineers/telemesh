package com.w3engineers.unicef.telemesh.data.remote;

import android.net.Network;

import com.w3engineers.unicef.telemesh.BuildConfig;
import com.w3engineers.unicef.telemesh.data.helper.AppCredentials;
import com.w3engineers.unicef.telemesh.data.helper.constants.Constants;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


public class RetrofitService {

    public static <T> T createService(Class<T> serviceClass, String baseUrl, Network network) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(chain -> {
            Request request = chain.request();
            request = request
                    .newBuilder()
                    .addHeader("Authorization", Credentials.basic(AppCredentials.getInstance().getAuthUserName(), AppCredentials.getInstance().getAuthPassword()))
                    .build();
            return chain.proceed(request);
        });

        if (network != null) {
            client.socketFactory(network.getSocketFactory());
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client.build())
                .build();
        return retrofit.create(serviceClass);
    }
}
