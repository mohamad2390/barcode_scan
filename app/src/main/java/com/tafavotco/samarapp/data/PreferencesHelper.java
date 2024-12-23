package com.tafavotco.samarapp.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    public SharedPreferences myPref , userName, Token , EventHash , EventTitle , ActivityTitle;
    public static final String name_category="name_category";
    public static final String key_category="key_category";


    public PreferencesHelper(Context context) {
        myPref = context.getSharedPreferences(name_category , Context.MODE_PRIVATE);
        userName = context.getSharedPreferences("userName" , Context.MODE_PRIVATE);
        Token = context.getSharedPreferences("Token" , Context.MODE_PRIVATE);
        EventHash = context.getSharedPreferences("EventHash" , Context.MODE_PRIVATE);
        EventTitle = context.getSharedPreferences("EventTitle" , Context.MODE_PRIVATE);
        ActivityTitle = context.getSharedPreferences("ActivityTitle" , Context.MODE_PRIVATE);

    }

    public String getName_category(){
        return myPref.getString(key_category ,"null");
    }

    public void setName_category(String val){
        myPref.edit().putString(key_category , val).apply();
    }

    public String getUsername(){
        return userName.getString("username" , "null");
    }

    public void setUsername(String username){
        userName.edit().putString("username" , username).apply();
    }

    public String getToken(){
        return Token.getString("token" , "null");
    }

    public void setToken(String token){
        Token.edit().putString("token" , token).apply();
    }

    public String getEventHash(){
        return EventHash.getString("eventHash" , "null");
    }

    public void setEventHash(String eventHash){
        EventHash.edit().putString("eventHash" , eventHash).apply();
    }

    public String getEventTitle(){
        return EventTitle.getString("eventTitle" , "null");
    }

    public void setEventTitle(String eventTitle){
        EventTitle.edit().putString("eventTitle" , eventTitle).apply();
    }

    public String getActivityTitle(){
        return ActivityTitle.getString("activityTitle" , "null");
    }

    public void setActivityTitle(String activityTitle){
        ActivityTitle.edit().putString("activityTitle" , activityTitle).apply();
    }
}
