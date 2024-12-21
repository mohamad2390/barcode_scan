package com.tafavotco.samarapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tafavotco.samarapp.R;

public class HomeFragment extends Fragment {

    private Button btn_registration;
    private Button btn_checkIn;
    private Button btn_checkOut;
    private Button btn_get_activities;
    private Button btn_inquiry;
    private Button btn_about_us;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        btn_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ScanBarCodeFragment();
                Bundle bundle = new Bundle();
                Bundle bundle_method = new Bundle();
                Bundle bundle_activity_id = new Bundle();
                bundle_method.putString("method", "registration");
                bundle_activity_id.putString("activity_id", "");
                bundle.putBundle("bundle_method",bundle_method);
                bundle.putBundle("bundle_activity_id",bundle_activity_id);
                fragment.setArguments(bundle);
                replaceFragment(fragment);

//                Intent myIntent = new Intent(context, ScanBarCode.class);
//                myIntent.putExtra("method", "registration");
//                startActivity(myIntent);
            }
        });

        btn_checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ScanBarCodeFragment();
                Bundle bundle = new Bundle();
                Bundle bundle_method = new Bundle();
                Bundle bundle_activity_id = new Bundle();
                bundle_method.putString("method", "checkIn");
                bundle_activity_id.putString("activity_id", "");
                bundle.putBundle("bundle_method",bundle_method);
                bundle.putBundle("bundle_activity_id",bundle_activity_id);
                fragment.setArguments(bundle);
                replaceFragment(fragment);

//                Intent myIntent = new Intent(context, ScanBarCode.class);
//                myIntent.putExtra("method", "checkIn");
//                startActivity(myIntent);
            }
        });

        btn_checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new ScanBarCodeFragment();
                Bundle bundle = new Bundle();
                Bundle bundle_method = new Bundle();
                Bundle bundle_activity_id = new Bundle();
                bundle_method.putString("method", "checkOut");
                bundle_activity_id.putString("activity_id", "");
                bundle.putBundle("bundle_method",bundle_method);
                bundle.putBundle("bundle_activity_id",bundle_activity_id);
                fragment.setArguments(bundle);
                replaceFragment(fragment);

//                Intent myIntent = new Intent(context, ScanBarCode.class);
//                myIntent.putExtra("method", "checkOut");
//                startActivity(myIntent);
            }
        });

        btn_get_activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ActivityListFragment();
                replaceFragment(fragment);
            }
        });

    }

    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void init(View v) {
        btn_registration = v.findViewById(R.id.btn_registration);
        btn_checkIn = v.findViewById(R.id.btn_checkIn);
        btn_checkOut = v.findViewById(R.id.btn_checkOut);
        btn_get_activities = v.findViewById(R.id.btn_get_activities);
        btn_inquiry = v.findViewById(R.id.btn_inquiry);
        btn_about_us = v.findViewById(R.id.btn_about_us);
    }

}