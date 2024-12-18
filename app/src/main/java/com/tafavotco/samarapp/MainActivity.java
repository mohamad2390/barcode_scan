package com.tafavotco.samarapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.ui.ActivityList;
import com.tafavotco.samarapp.ui.ScanBarCode;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private Button btn_registration;
    private Button btn_checkIn;
    private Button btn_checkOut;
    private Button btn_get_activities;
    private Button btn_inquiry;
    private Button btn_about_us;
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init();

        NavigationView navigationView = findViewById(R.id.nav_view);
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this , drawerLayout , toolbar , R.string.open_nav , R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btn_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ScanBarCode.class);
                myIntent.putExtra("method", "registration");
                startActivity(myIntent);
            }
        });

        btn_checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ScanBarCode.class);
                myIntent.putExtra("method", "checkIn");
                startActivity(myIntent);
            }
        });

        btn_checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ScanBarCode.class);
                myIntent.putExtra("method", "checkOut");
                startActivity(myIntent);
            }
        });

        btn_get_activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, ActivityList.class);
                startActivity(myIntent);
            }
        });

    }

    public void init(){
        drawerLayout = findViewById(R.id.drawer_layout);
        btn_registration = findViewById(R.id.btn_registration);
        btn_checkIn = findViewById(R.id.btn_checkIn);
        btn_checkOut = findViewById(R.id.btn_checkOut);
        btn_get_activities = findViewById(R.id.btn_get_activities);
        btn_inquiry = findViewById(R.id.btn_inquiry);
        btn_about_us = findViewById(R.id.btn_about_us);
        preferencesHelper = new PreferencesHelper(MainActivity.this);
    }

}