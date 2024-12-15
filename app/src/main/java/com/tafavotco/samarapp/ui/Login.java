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

import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.model.loginResponseModel;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {
    private EditText text_username;
    private Button send;
    private String cell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

//        send.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.w("1response--------------------" , "username");
//            }
//        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cell = text_username.getText().toString();
                Log.w("1response--------------------" , cell);

                if (cell.equals("")) {
                    text_username.setError(getString(R.string.login_notInsertUserName));
                } else {

//                    final SweetAlertDialog pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
//                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                    pDialog.setTitleText("Loading");
//                    pDialog.setCancelable(false);
//                    pDialog.show();

                    requestCode requestCodes=new requestCode(cell);
                    WebserviceHelper.getInstancePost().sendCode(requestCodes).enqueue(new Callback<loginResponseModel>() {
                        @Override
                        public void onResponse(@NonNull Call<loginResponseModel> call, @NonNull Response<loginResponseModel> response) {
//                            Log.w("1response--------------------", response.body().toString());
                            if (response.body().getSuccess()) {
//                                pDialog.cancel();
                                Intent myIntent = new Intent(Login.this, verifying.class);
                                myIntent.putExtra("username", cell);
                                startActivity(myIntent);
                                finish();
                            }else {
//                                pDialog.cancel();
                                Toast.makeText(Login.this, response.body().getMessage() , Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<loginResponseModel> call, @NonNull Throwable t) {
                            Log.w("response", Objects.requireNonNull(t.getMessage()));
//                            pDialog.cancel();
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
    }
}