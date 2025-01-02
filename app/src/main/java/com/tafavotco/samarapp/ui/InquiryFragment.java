package com.tafavotco.samarapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.model.EventRequestModel;
import com.tafavotco.samarapp.utils.Convert;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InquiryFragment extends Fragment {

    PreferencesHelper preferencesHelper;
    Button btn_registration;
    EditText edit_txt_national_code;
    Context context;
    private ProgressBar progressBar;
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

                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(50 , true);

                String token = "Bearer " + preferencesHelper.getToken();
                EventRequestModel eventRequest = new EventRequestModel(preferencesHelper.getEventHash() , nCode);

                WebserviceHelper.getInstancePost().inquiryParticipant(token , eventRequest).enqueue(new Callback<Map<String , Object>>() {
                    @Override
                    public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {
                        if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                            if (response.body().containsKey("id")){
                                progressBar.setVisibility(View.GONE);
                                customDialog.showBottomDialog(preferencesHelper.getEventHash() , nCode , "" , "" , response.body());
                            }
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(context, "شرکت کننده یافت نشد" , Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Map<String , Object>> call, @NonNull Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, R.string.serverError , Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

    private void init(View v) {
        preferencesHelper = new PreferencesHelper(context);
        btn_registration = v.findViewById(R.id.btn_registration);
        edit_txt_national_code = v.findViewById(R.id.edit_txt_national_code);
        progressBar = v.findViewById(R.id.inquiry_progressBar);
        customDialog = new CustomDialog(context,preferencesHelper);
    }
}