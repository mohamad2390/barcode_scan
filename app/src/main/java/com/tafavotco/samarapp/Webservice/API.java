package com.tafavotco.samarapp.Webservice;

import com.tafavotco.samarapp.model.loginResponseModel;
import com.tafavotco.samarapp.model.verifyResponseModel;
import com.tafavotco.samarapp.ui.Login;
import com.tafavotco.samarapp.ui.ScanBarCode;
import com.tafavotco.samarapp.ui.verifying;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface API {

    @POST("auth/verify")
    Call<verifyResponseModel> verify(@Body verifying.RequestToken requestCodes);

    @POST("auth/login")
    Call<loginResponseModel> sendCode(@Body Login.requestCode requestCodes);

    @POST("events/registration")
    Call<String> registration(@Body ScanBarCode.BarcodeRequest barcodeValue);

    @POST("events/checkin")
    Call<String> checkin(@Body ScanBarCode.BarcodeRequest barcodeValue);

    @POST("events/checkout")
    Call<String> checkout(@Body ScanBarCode.BarcodeRequest barcodeValue);


}
