package com.tafavotco.samarapp.ui;

import static com.tafavotco.samarapp.data.constValue.server_URL;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.model.ActivityRequestModel;
import com.tafavotco.samarapp.model.EventRequestModel;
import com.tafavotco.samarapp.utils.Convert;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomDialog {

    private Context context;
    private PreferencesHelper preferencesHelper;

    public CustomDialog(Context context, PreferencesHelper preferencesHelper) {
        this.context = context;
        this.preferencesHelper = preferencesHelper;
    }

    public void showBottomDialog(String eventHash , String participantHash , String method , String activityHash) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheet_dialog);

        ImageView img_person_avatar = dialog.findViewById(R.id.img_person_avatar);
        TextView txt_status = dialog.findViewById(R.id.txt_status);
        TextView txt_fName = dialog.findViewById(R.id.txt_fName);
        TextView txt_lName = dialog.findViewById(R.id.txt_lName);
        TextView txt_national_code = dialog.findViewById(R.id.txt_national_code);
        Button dialog_button = dialog.findViewById(R.id.dialog_button);

        String token = "Bearer " + preferencesHelper.getToken();
        EventRequestModel eventRequest = new EventRequestModel(eventHash , participantHash);

        WebserviceHelper.getInstancePost().inquiryParticipant(token , eventRequest).enqueue(new Callback<Map<String , Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {
                if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                    if (response.body() != null && response.body().containsKey("warningMessage")){
                        Toast.makeText(context, Objects.requireNonNull(response.body().get("warningMessage")).toString() , Toast.LENGTH_LONG).show();
                    }
                    if (response.body().containsKey("id")){
                        String imageUrl = server_URL + "events/avatars/" + Objects.requireNonNull(response.body().get("id")).toString();

                        GlideUrl glideUrl = new GlideUrl(imageUrl,
                                new LazyHeaders.Builder()
                                        .addHeader("Authorization", token)
                                        .build());

                        Glide.with(context)
                                .load(glideUrl)
                                .into(img_person_avatar);
                    }
                    if (response.body().containsKey("firstName") && response.body().containsKey("lastName")){
                        txt_fName.setText(Objects.requireNonNull(response.body().get("firstName")).toString());
                        txt_lName.setText(Objects.requireNonNull(response.body().get("lastName")).toString());
                    }
                    if (response.body().containsKey("status")){
                        txt_status.setText(Objects.requireNonNull(response.body().get("status")).toString());
                    }
                    if (response.body().containsKey("nationalCode")){
                        txt_national_code.setText(Objects.requireNonNull(response.body().get("nationalCode")).toString());
                    }
                }else {
                    if (response.body() != null && response.body().containsKey("warningMessage")){
                        Toast.makeText(context, Objects.requireNonNull(response.body().get("warningMessage")).toString() , Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String , Object>> call, @NonNull Throwable t) {
                Log.w("response1", Objects.requireNonNull(t.getMessage()));
                Toast.makeText(context, R.string.serverError , Toast.LENGTH_LONG).show();
            }
        });


        dialog_button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View v) {

                ActivityRequestModel activityRequest = new ActivityRequestModel(eventHash , activityHash , participantHash);
                Call<Map<String , Object>> call;

                if (method.equals("registration") && activityHash.isEmpty()){
                    call = WebserviceHelper.getInstancePost().registration(token , eventRequest);
                }else if (method.equals("checkIn") && activityHash.isEmpty()){
                    call = WebserviceHelper.getInstancePost().checkin(token , eventRequest);
                }else if (method.equals("checkOut") && activityHash.isEmpty()){
                    call = WebserviceHelper.getInstancePost().checkout(token , eventRequest);
                }else if (method.equals("checkIn")){
                    call = WebserviceHelper.getInstancePost().activityCheckIn(token , activityRequest);
                }else if (method.equals("checkOut")){
                    call = WebserviceHelper.getInstancePost().activityCheckOut(token , activityRequest);
                }else {
                    return;
                }

                    call.enqueue(new Callback<Map<String , Object>>() {
                        @Override
                        public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {
                            if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                                Toast.makeText(context, "ثبت شد" , Toast.LENGTH_LONG).show();
                                if (response.body() != null && response.body().containsKey("warningMessage")){
                                    Toast.makeText(context, Objects.requireNonNull(response.body().get("warningMessage")).toString() , Toast.LENGTH_LONG).show();
                                }
                            }else {
                                Toast.makeText(context, "ثبت نشد" , Toast.LENGTH_LONG).show();
                                if (response.body() != null && response.body().containsKey("warningMessage")){
                                    Toast.makeText(context, Objects.requireNonNull(response.body().get("warningMessage")).toString() , Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Map<String , Object>> call, @NonNull Throwable t) {
                            Log.w("response1", Objects.requireNonNull(t.getMessage()));
                            Toast.makeText(context, R.string.serverError , Toast.LENGTH_LONG).show();
                        }
                    });


                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

}
