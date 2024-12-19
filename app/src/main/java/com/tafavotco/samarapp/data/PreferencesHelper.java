package com.tafavotco.samarapp.data;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    public SharedPreferences myPref , userName, Token , Event;
    public static final String name_category="name_category";
    public static final String key_category="key_category";


    public PreferencesHelper(Context context) {
        myPref = context.getSharedPreferences(name_category , Context.MODE_PRIVATE);
        userName = context.getSharedPreferences("userName" , Context.MODE_PRIVATE);
        Token = context.getSharedPreferences("Token" , Context.MODE_PRIVATE);
        Event = context.getSharedPreferences("Event" , Context.MODE_PRIVATE);
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

    public String getEvent(){
        return Event.getString("event" , "null");
    }

    public void setEvent(String event){
        Event.edit().putString("event" , event).apply();
    }

}
