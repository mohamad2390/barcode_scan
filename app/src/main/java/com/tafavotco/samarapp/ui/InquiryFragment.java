package com.tafavotco.samarapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.data.PreferencesHelper;

public class InquiryFragment extends Fragment {

    PreferencesHelper preferencesHelper;
    Button btn_registration;
    EditText edit_txt_national_code;
    Context context;
    CustomDialog customDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inquiry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        btn_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nCode = edit_txt_national_code.getText().toString();

                customDialog.showBottomDialog(preferencesHelper.getEventHash() , nCode , "" , "");

            }
        });

    }

    private void init(View v) {
        preferencesHelper = new PreferencesHelper(context);
        btn_registration = v.findViewById(R.id.btn_registration);
        edit_txt_national_code = v.findViewById(R.id.edit_txt_national_code);
        customDialog = new CustomDialog(context,preferencesHelper);
    }
}