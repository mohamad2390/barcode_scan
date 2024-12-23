package com.tafavotco.samarapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.ui.ScanBarCodeFragment;

import java.util.List;
import java.util.Map;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<Map<String , Object>> data;
    private LayoutInflater mInflater;
    Context context;

    public ActivityAdapter(List<Map<String , Object>> data, Context context) {
        this.data = data;
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @NonNull
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =mInflater.inflate(R.layout.item_list_activity, parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (data.get(position).containsKey("title")){
            holder.item_txt_activity_title.setText(data.get(position).get("title").toString());
        }
        if (data.get(position).containsKey("time")){
            holder.item_txt_activity_time.setText(data.get(position).get("time").toString());
        }

        holder.btn_activity_checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PreferencesHelper preferencesHelper = new PreferencesHelper(context);
                if (data.get(position).containsKey("title")){
                    preferencesHelper.setActivityTitle(data.get(position).get("title").toString());
                }
                Fragment fragment = new ScanBarCodeFragment();
                Bundle bundle = new Bundle();
                Bundle bundle_method = new Bundle();
                Bundle bundle_activity_id = new Bundle();
                bundle_method.putString("method", "checkIn");
                if (data.get(position).containsKey("id")){
                    bundle_activity_id.putString("activity_id", data.get(position).get("id").toString());
                }
                bundle.putBundle("bundle_method",bundle_method);
                bundle.putBundle("bundle_activity_id",bundle_activity_id);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

            }
        });

        holder.btn_activity_checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PreferencesHelper preferencesHelper = new PreferencesHelper(context);
                if (data.get(position).containsKey("title")){
                    preferencesHelper.setActivityTitle(data.get(position).get("title").toString());
                }
                Fragment fragment = new ScanBarCodeFragment();
                Bundle bundle = new Bundle();
                Bundle bundle_method = new Bundle();
                Bundle bundle_activity_id = new Bundle();
                bundle_method.putString("method", "checkOut");
                if (data.get(position).containsKey("id")){
                    bundle_activity_id.putString("activity_id", data.get(position).get("id").toString());
                }
                bundle.putBundle("bundle_method",bundle_method);
                bundle.putBundle("bundle_activity_id",bundle_activity_id);
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_txt_activity_title;
        public TextView item_txt_activity_time;
        public Button btn_activity_checkIn;
        public Button btn_activity_checkOut;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_txt_activity_title = itemView.findViewById(R.id.txt_activity_title);
            item_txt_activity_time = itemView.findViewById(R.id.txt_activity_time);
            btn_activity_checkIn = itemView.findViewById(R.id.btn_checkIn);
            btn_activity_checkOut = itemView.findViewById(R.id.btn_checkOut);
        }
    }
}
