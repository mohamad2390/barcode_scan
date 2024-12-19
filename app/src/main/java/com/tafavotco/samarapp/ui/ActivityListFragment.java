package com.tafavotco.samarapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class ActivityListFragment extends Fragment {

    private RecyclerView list;
    List<ActivityModel> data = new ArrayList<>();
    private ActivityAdapter adapter;
    PreferencesHelper preferenceshelper;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        String eventHash = "nbvvn";

        WebserviceHelper.getInstancePost().getActivities("Bearer "+preferenceshelper.getToken() , eventHash).enqueue(new Callback<ActivityModel>() {
            @Override
            public void onResponse(@NonNull Call<ActivityModel> call, @NonNull Response<ActivityModel> response) {
                Log.w("1response--------------------", response.message());
                if (response.body().getId() != null) {
                    data = (List<ActivityModel>) response.body();
                    adapter = new ActivityAdapter(data , context);
                    list.setLayoutManager(new LinearLayoutManager(context));
                    list.setAdapter(adapter);
                }else {
                    Toast.makeText(context, "there is no activity" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ActivityModel> call, Throwable t) {
                Log.w("response", t.getMessage());
                Toast.makeText(context, R.string.serverError , Toast.LENGTH_LONG).show();
            }
        });

    }
    private void init(View v) {
        list = v.findViewById(R.id.layout_activity_list);
        preferenceshelper = new PreferencesHelper(context);
    }

}