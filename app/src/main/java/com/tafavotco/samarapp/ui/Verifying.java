package com.tafavotco.samarapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tafavotco.samarapp.MainActivity;
import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.utils.Convert;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Verifying extends AppCompatActivity {

    private EditText txt_verify;
    private Button verify;
    private String verifyCode ;
    public static String username;
    PreferencesHelper preferencesHelper;
    private ProgressBar progressBar;

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

                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(50 , true);

                    verifyCode = txt_verify.getText().toString();
                    RequestToken requestToken = new RequestToken(username , verifyCode);
//                    Log.w("response111", requestToken.toString());
                    WebserviceHelper.getInstancePost().verify(requestToken).enqueue(new Callback<Map<String , Object>>() {
                        @Override
                        public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {
                            if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                                preferencesHelper.setUsername(username);
                                preferencesHelper.setToken(Objects.requireNonNull(response.body().get("token")).toString());
                                if (response.body().get("role").equals("ORGANIZER")){
                                    progressBar.setVisibility(View.GONE);
                                    Intent myIntent = new Intent(Verifying.this, MainActivity.class);
                                    startActivity(myIntent);
                                    finish();
                                }else Toast.makeText(Verifying.this, "you are not a organizer" , Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Verifying.this, "some thing went rung" , Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Map<String , Object>> call, @NonNull Throwable t) {
                            Log.w("response", Objects.requireNonNull(t.getMessage()));
                            Toast.makeText(Verifying.this, R.string.serverError , Toast.LENGTH_LONG).show();
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
        preferencesHelper = new PreferencesHelper(Verifying.this);
        progressBar = findViewById(R.id.verify_progressBar);
    }
}

