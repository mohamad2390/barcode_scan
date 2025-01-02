package com.tafavotco.samarapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.model.EventRequestModel;
import com.tafavotco.samarapp.utils.Convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanBarCodeFragment extends Fragment {

    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private final List<String> processedBarcodes = new ArrayList<>();
    private String method;
    private String activityHash;
    private PreferencesHelper preferencesHelper;
    private Context context;
    private EditText edit_txt_phone;
    private Button send;
    private TextView  activity_title;
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
        return inflater.inflate(R.layout.fragment_scan_bar_code, container, false);
    }

    @ExperimentalGetImage
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        cameraExecutor = Executors.newSingleThreadExecutor();

        if (!Objects.equals(preferencesHelper.getActivityTitle(), "")){
            activity_title.setText(preferencesHelper.getActivityTitle());
        }else activity_title.setText("فعالیتی انتخاب نشده است");

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
             method = Objects.requireNonNull(bundle.getBundle("bundle_method")).getString("method" , "");
             activityHash = Objects.requireNonNull(bundle.getBundle("bundle_activity_id")).getString("activity_id" , "");
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_txt_phone.getText().toString().equals("")) {
                    edit_txt_phone.setError(getString(R.string.login_notInsertUserName));
                }
                sendBarcodeToWebService(edit_txt_phone.getText().toString());
            }
        });

        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (Exception e) {
                Log.e("CameraProvider", "Error: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(requireContext()));


    }

    @ExperimentalGetImage
    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        // ایجاد پیش‌نمایش دوربین
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // تنظیم آنالیز تصویر
        BarcodeScanner barcodeScanner = BarcodeScanning.getClient();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> processImageProxy(barcodeScanner, imageProxy));

        // انتخاب دوربین
        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    @ExperimentalGetImage
    private void processImageProxy(BarcodeScanner barcodeScanner, ImageProxy imageProxy) {
        @androidx.camera.core.ExperimentalGetImage
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            barcodeScanner.process(inputImage)
                    .addOnSuccessListener(barcodes -> {
                        for (Barcode barcode : barcodes) {
                            Log.d("Barcode", "Value: " + barcode.getRawValue());
                            sendBarcodeToWebService(barcode.getRawValue());
                        }
                    })
                    .addOnCompleteListener(task -> imageProxy.close());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    private void sendBarcodeToWebService(String participationHash) {

        if (processedBarcodes.contains(participationHash)) {
            return;
        }
        processedBarcodes.add(participationHash);

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(50 , true);

        String token = "Bearer " + preferencesHelper.getToken();
        EventRequestModel eventRequest = new EventRequestModel(preferencesHelper.getEventHash() , participationHash);

        WebserviceHelper.getInstancePost().inquiryParticipant(token , eventRequest).enqueue(new Callback<Map<String , Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {
                if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                    if (response.body().containsKey("id")){
                        progressBar.setVisibility(View.GONE);
                        customDialog.showBottomDialog(preferencesHelper.getEventHash() , participationHash , method , activityHash , response.body());
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

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                processedBarcodes.remove(participationHash);
                }, 4000);

    }

    private void init(View v) {
        edit_txt_phone = v.findViewById(R.id.edit_txt_phone);
        send = v.findViewById(R.id.btn_send);
        previewView = v.findViewById(R.id.previewView);
        activity_title = v.findViewById(R.id.txt_activity_title_global);
        progressBar = v.findViewById(R.id.scan_barcode_progressBar);
        preferencesHelper = new PreferencesHelper(context);
        customDialog = new CustomDialog(context,preferencesHelper);
    }

}