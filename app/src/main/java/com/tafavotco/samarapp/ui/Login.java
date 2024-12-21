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

import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.utils.Convert;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {
    private EditText text_username;
    private Button send;
    private String cell;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cell = text_username.getText().toString();

                if (cell.equals("")) {
                    text_username.setError(getString(R.string.login_notInsertUserName));
                } else {

                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(50 , true);

                    requestCode requestCodes=new requestCode(cell);
                    WebserviceHelper.getInstancePost().sendCode(requestCodes).enqueue(new Callback<Map<String , Object>>() {
                        @Override
                        public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {
                            if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                                progressBar.setVisibility(View.GONE);
                                Intent myIntent = new Intent(Login.this, Verifying.class);
                                myIntent.putExtra("username", cell);
                                startActivity(myIntent);
                                finish();
                            }else {
                                if (response.body() != null && response.body().containsKey("message")){
                                    Toast.makeText(Login.this, Objects.requireNonNull(response.body().get("message")).toString() , Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Map<String , Object>> call, @NonNull Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            Log.w("response", Objects.requireNonNull(t.getMessage()));
                            Toast.makeText(Login.this, R.string.serverError , Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        });
    }

    public class requestCode{
        private String cell;
        public requestCode(String cell) {
            this.cell = cell;
        }
    }

    public void init() {
        text_username = findViewById(R.id.text_username);
        send = findViewById(R.id.btn_send_code);
        progressBar = findViewById(R.id.login_progressBar);
    }
}