package com.tafavotco.samarapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.adapter.EventAdapter;
import com.tafavotco.samarapp.data.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventList extends AppCompatActivity {

    private RecyclerView list;
    List<Map<String , Object>> data = new ArrayList<>();
    private EventAdapter adapter;
    PreferencesHelper preferenceshelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.event_list);
        init();

        Integer page = 0;

        WebserviceHelper.getInstancePost().getEvents(page).enqueue(new Callback<List<Map<String , Object>>>() {
            @Override
            public void onResponse(@NonNull Call<List<Map<String , Object>>> call, @NonNull Response<List<Map<String , Object>>> response) {
                Log.w("1response--------------------", response.message());
                if (response.body() != null) {
                    data = (List<Map<String , Object>>) response.body();
                    adapter = new EventAdapter(data , EventList.this);
                    list.setLayoutManager(new LinearLayoutManager(EventList.this));
                    list.setAdapter(adapter);
                }else {
                    Toast.makeText(EventList.this, "there is no event" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Map<String , Object>>> call, @NonNull Throwable t) {
                Log.w("response----------------------------", t.getMessage());
                Toast.makeText(EventList.this, R.string.serverError , Toast.LENGTH_LONG).show();
            }
        });

    }

    private void init() {
        list = findViewById(R.id.layout_activity_list);
        preferenceshelper = new PreferencesHelper(this);
    }
}