package com.img.mybat11.Api;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

  //  public static final String BASE_URL = "https://api.mybat11.com/api/";
    public static final String BASE_URL = "https://api.mybat11.com/appdevelopment/api/";
//    public static final String BASE_URL = "http://146.88.26.111/api/";
//    public static final String BASE_URL = "http://demofantasy.in/mybat11/api/";
//    public static final String BASE_URL = "https://mybat11.com/admin/api/";
//    public static final String BASE_URL = "http://imgglobal.in/new_mybat11/api/";
    private static Retrofit retrofit = null;

    private static OkHttpClient okClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
    }

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}