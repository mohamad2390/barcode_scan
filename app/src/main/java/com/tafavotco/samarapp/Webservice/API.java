package com.tafavotco.samarapp.Webservice;

import com.tafavotco.samarapp.model.ActivityModel;
import com.tafavotco.samarapp.model.EventModel;
import com.tafavotco.samarapp.ui.Login;
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

    @POST("events/registration")
    Call<Map<String , Object>> registration(@Header("authorization") String token , @Path("participationHash") String participationHash);

    @POST("panel/participants/check-in/{participationHash}")
    Call<Map<String , Object>> checkin(@Header("authorization") String token , @Path("participationHash") String participationHash);

    @POST("panel/participants/check-out/{participationHash}")
    Call<Map<String , Object>> checkout(@Header("authorization") String token , @Path("participationHash") String participationHash);

    @GET("panel/activities/all/{eventHash}")
    Call<List<ActivityModel>> getActivities(@Header("authorization") String token , @Path("eventHash") String eventHash);

    @GET("events/all/{page}")
    Call<List<EventModel>> getEvents(@Path("page") Integer page);

    @POST("activities/checkout")
    Call<Map<String , Object>> activityCheckIn(@Header("authorization") String token , @Path("participationHash") String participationHash);

    @POST("activities/checkout")
    Call<Map<String , Object>> activityCheckOut(@Header("authorization") String token , @Path("participationHash") String participationHash);

}
