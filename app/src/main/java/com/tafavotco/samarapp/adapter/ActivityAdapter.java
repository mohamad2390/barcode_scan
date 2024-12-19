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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.model.ActivityModel;
import com.tafavotco.samarapp.ui.ScanBarCodeFragment;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<ActivityModel> data;
    private LayoutInflater mInflater;
    Context context;

    public ActivityAdapter(List<ActivityModel> data, Context context) {
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
        holder.item_txt_activity_title.setText(data.get(position).getTitle());
        holder.item_txt_activity_sex.setTag(data.get(position).getSex());
        holder.item_txt_activity_time.setText(data.get(position).getTime().toString());

        holder.btn_activity_checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ScanBarCodeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("activity_id", data.get(position).getId());
                bundle.putString("method", "checkIn");
                fragment.setArguments(bundle);
//                replaceFragment(fragment);

                FragmentManager fragmentManager = fragment.getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

//                Intent myIntent = new Intent(context , ScanBarCode.class);
//                myIntent.putExtra("activity_id" , data.get(position).getId());
//                myIntent.putExtra("method" , "checkIn");
//                context.startActivity(myIntent);
            }
        });

        holder.btn_activity_checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ScanBarCodeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("activity_id", data.get(position).getId());
                bundle.putString("method", "checkOut");
                fragment.setArguments(bundle);
//                replaceFragment(fragment);

                FragmentManager fragmentManager = fragment.getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();

//                Intent myIntent = new Intent(context , ScanBarCode.class);
//                myIntent.putExtra("activity_id" , data.get(position).getId());
//                myIntent.putExtra("method" , "checkOut");
//                context.startActivity(myIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView item_txt_activity_title;
        public TextView item_txt_activity_sex;
        public TextView item_txt_activity_time;
        public Button btn_activity_checkIn;
        public Button btn_activity_checkOut;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_txt_activity_title = itemView.findViewById(R.id.txt_activity_title);
            item_txt_activity_sex = itemView.findViewById(R.id.txt_activity_sex);
            item_txt_activity_time = itemView.findViewById(R.id.txt_activity_time);
            btn_activity_checkIn = itemView.findViewById(R.id.btn_checkIn);
            btn_activity_checkOut = itemView.findViewById(R.id.btn_checkOut);
        }
    }
}
