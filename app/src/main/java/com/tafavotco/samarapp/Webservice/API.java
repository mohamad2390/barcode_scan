package com.tafavotco.samarapp.Webservice;

import com.tafavotco.samarapp.ui.Login;
import com.tafavotco.samarapp.ui.ScanBarCodeFragment;
import com.tafavotco.samarapp.ui.Verifying;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface API {

    @GET("panel/eventusers/role")
    Call<Map<String , Object>> checkToken(@Header("authorization") String token);

    @POST("auth/verify")
    Call<Map<String , Object>> verify(@Body Verifying.RequestToken requestCodes);

    @POST("auth/login")
    Call<Map<String , Object>> sendCode(@Body Login.requestCode requestCodes);

    @POST("panel/participants/register")
    Call<Map<String , Object>> registration(@Header("authorization") String token , @Body ScanBarCodeFragment.ParticipantHashRequest participantHashRequest);

    @POST("panel/participants/check-in")
    Call<Map<String , Object>> checkin(@Header("authorization") String token , @Body ScanBarCodeFragment.ParticipantHashRequest participantHashRequest);

    @POST("panel/participants/check-out")
    Call<Map<String , Object>> checkout(@Header("authorization") String token , @Body ScanBarCodeFragment.ParticipantHashRequest participantHashRequest);

    @GET("panel/activities/all/{eventHash}")
    Call<List<Map<String , Object>>> getActivities(@Header("authorization") String token , @Path("eventHash") String eventHash);

    @GET("events/all/{page}")
    Call<List<Map<String , Object>>> getEvents(@Path("page") Integer page);

    @POST("panel/activities/check-in")
    Call<Map<String , Object>> activityCheckIn(@Header("authorization") String token , @Body ScanBarCodeFragment.ParticipantHashRequest participantHashRequest);

    @POST("panel/activities/check-out")
    Call<Map<String , Object>> activityCheckOut(@Header("authorization") String token , @Body ScanBarCodeFragment.ParticipantHashRequest participantHashRequest);

    @POST("panel/participants/inquiry")
    Call<Map<String , Object>> inquiryParticipant(@Header("authorization") String token , @Body ScanBarCodeFragment.ParticipantHashRequest participantHashRequest);

}
