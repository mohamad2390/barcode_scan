package com.tafavotco.samarapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tafavotco.samarapp.MainActivity;
import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.utils.Convert;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {

    private TextView image;
    private Animation visibility;
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        init();

        image.startAnimation(visibility);

        start_app();
    }

    private void start_app(){
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(preferencesHelper.getToken().equals("null")){
                    Intent myIntent = new Intent(Splash.this, Login.class);
                    startActivity(myIntent);
                    finish();
                }else {
                    WebserviceHelper.getInstancePost().checkToken("Bearer "+preferencesHelper.getToken()).enqueue(new Callback<Map<String , Object>>() {
                        @Override
                        public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {

                            if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                                Intent myIntent = new Intent(Splash.this, MainActivity.class);
                                startActivity(myIntent);
                                finish();
                            }else {
                                Intent myIntent = new Intent(Splash.this, Login.class);
                                startActivity(myIntent);
                                finish();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Map<String , Object>> call, @NonNull Throwable t) {
                            Intent myIntent = new Intent(Splash.this, Login.class);
                            startActivity(myIntent);
                            finish();
                        }
                    });
                }
            }
        }, 2 * 1000);
    }

    public void init(){
        image = findViewById(R.id.icon);
        visibility = AnimationUtils.loadAnimation(this, R.anim.visiblity_animation);
        preferencesHelper = new PreferencesHelper(Splash.this);
    }

}