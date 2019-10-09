package com.w3engineers.unicef.telemesh.data.remote;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * ============================================================================
 */


public class RetrofitService {

    public static <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().build())
                .build();
        return retrofit.create(serviceClass);
    }
}
