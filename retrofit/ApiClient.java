package com.video.download.vidlink.retrofit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
        private  static Retrofit retrofit;

    public static Retrofit getInstance(String baseUrl) {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
    }