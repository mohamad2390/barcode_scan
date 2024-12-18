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
import com.tafavotco.samarapp.adapter.ActivityAdapter;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.model.ActivityModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityList extends AppCompatActivity {

    private RecyclerView list;
    List<ActivityModel> data = new ArrayList<>();
    private ActivityAdapter adapter;
    PreferencesHelper preferenceshelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);
        init();

        String eventHash = "nbvvn";

        WebserviceHelper.getInstancePost().getActivities("Bearer "+preferenceshelper.getToken() , eventHash).enqueue(new Callback<ActivityModel>() {
            @Override
            public void onResponse(@NonNull Call<ActivityModel> call, @NonNull Response<ActivityModel> response) {
                Log.w("1response--------------------", response.message());
                if (response.body().getId() != null) {
                    data = (List<ActivityModel>) response.body();
                    adapter = new ActivityAdapter(data , ActivityList.this);
                    list.setLayoutManager(new LinearLayoutManager(ActivityList.this));
                    list.setAdapter(adapter);
                }else {
                    Toast.makeText(ActivityList.this, "there is no activity" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ActivityModel> call, Throwable t) {
                Log.w("response", t.getMessage());
//                            pDialog.cancel();
                Toast.makeText(ActivityList.this, R.string.serverError , Toast.LENGTH_LONG).show();
            }
        });

    }

    private void init() {
        list = findViewById(R.id.layout_activity_list);
        preferenceshelper = new PreferencesHelper(this);
    }
}