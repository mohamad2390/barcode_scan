package com.tafavotco.samarapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tafavotco.samarapp.MainActivity;
import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.model.verifyResponseModel;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class verifying extends AppCompatActivity {

    private EditText txt_verify;
    private Button verify;
    private String verifyCode ;
    public static String username;
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifying);
        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            username = bundle.getString("username");
        }

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_verify.getText().toString().equals("")) {
                    txt_verify.setError(getString(R.string.verifying_notInsertCode));
                } else {

//                    final SweetAlertDialog pDialog = new SweetAlertDialog(verifying.this, SweetAlertDialog.PROGRESS_TYPE);
//                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                    pDialog.setTitleText("Loading");
//                    pDialog.setCancelable(false);
//                    pDialog.show();

                    verifyCode = txt_verify.getText().toString();
                    RequestToken requestToken = new RequestToken(username , verifyCode);
                    WebserviceHelper.getInstancePost().verify(requestToken).enqueue(new Callback<verifyResponseModel>() {
                        @Override
                        public void onResponse(@NonNull Call<verifyResponseModel> call, @NonNull Response<verifyResponseModel> response) {
                            assert response.body() != null;
                            if (response.body().getSuccess()) {
                                preferencesHelper.setUsername(username);
                                preferencesHelper.setToken(response.body().getToken());
                                if (response.body().getRole().equals("ORGANIZER")){
                                    Intent myIntent = new Intent(verifying.this, MainActivity.class);
                                    startActivity(myIntent);
                                    finish();
                                }else Toast.makeText(verifying.this, "you are not a organizer" , Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(verifying.this, "some thing went rung" , Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<verifyResponseModel> call, @NonNull Throwable t) {
                            Log.w("response", Objects.requireNonNull(t.getMessage()));
//                            pDialog.cancel();
                            Toast.makeText(verifying.this, R.string.serverError , Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

    }

    public class RequestToken {
        private String cell;
        private String code;

        public RequestToken(String cell , String code) {
            this.cell = cell;
            this.code = code;
        }
    }

    public void init(){
        txt_verify = findViewById(R.id.txt_verify);
        verify = findViewById(R.id.btn_verify);
        preferencesHelper = new PreferencesHelper(verifying.this);
    }
}

