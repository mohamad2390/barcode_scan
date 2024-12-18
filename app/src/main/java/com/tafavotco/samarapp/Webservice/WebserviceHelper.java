package com.tafavotco.samarapp.Webservice;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tafavotco.samarapp.data.constValue;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebserviceHelper {

    public WebserviceHelper(Context context) {
    }

    public static API getInstancePost(){

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient okHttpClient = new  OkHttpClient.Builder()
                .readTimeout(constValue.RequestTimeOut , TimeUnit.SECONDS)
                .connectTimeout(constValue.RequestTimeOut , TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(constValue.server_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        API api = retrofit.create(API.class);
        return api;

    }

}
