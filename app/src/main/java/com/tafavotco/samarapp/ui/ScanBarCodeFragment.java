package com.tafavotco.samarapp.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.tafavotco.samarapp.R;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.data.PreferencesHelper;
import com.tafavotco.samarapp.utils.Convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanBarCodeFragment extends Fragment {

    private TextView barcodeTextView;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private final List<String> processedBarcodes = new ArrayList<>();
    private FrameLayout feedbackOverlay;
    private String method;
    private String activityId;
    private PreferencesHelper preferencesHelper;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_bar_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
             method = bundle.getString("method", "defaulValue");
             activityId = bundle.getString("activityId", "defaulValue");
        }

        cameraExecutor = new ExecutorService() {
            @Override
            public void execute(Runnable runnable) {

            }

            @Override
            public void shutdown() {

            }

            @Override
            public List<Runnable> shutdownNow() {
                return Collections.emptyList();
            }

            @Override
            public boolean isShutdown() {
                return false;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
                return false;
            }

            @Override
            public <T> Future<T> submit(Callable<T> callable) {
                return null;
            }

            @Override
            public <T> Future<T> submit(Runnable runnable, T t) {
                return null;
            }

            @Override
            public Future<?> submit(Runnable runnable) {
                return null;
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
                return Collections.emptyList();
            }

            @Override
            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException {
                return Collections.emptyList();
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws ExecutionException, InterruptedException {
                return null;
            }

            @Override
            public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
                return null;
            }
        };

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // تنظیم پیش‌نمایش دوربین
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // تنظیم تحلیل تصویر برای اسکن بارکد
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    Image mediaImage = imageProxy.getImage();
                    if (mediaImage != null) {
                        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                        BarcodeScanner scanner = BarcodeScanning.getClient();
                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
                                    for (Barcode barcode : barcodes) {
                                        String rawValue = barcode.getRawValue();

                                        if (rawValue != null) {
                                            // ارسال بارکد به وب‌سرویس
                                            sendBarcodeToWebService(rawValue);
                                        }

                                        break; // فقط اولین بارکد پردازش شود
                                    }
                                })
                                .addOnFailureListener(e -> barcodeTextView.setText("Error: " + e.getMessage()))
                                .addOnCompleteListener(task -> imageProxy.close());
                    }
                });

                // اتصال دوربین به لایف‌سایکل
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            barcodeTextView.setText("Camera permission required");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    private void sendBarcodeToWebService(String barcodeValue) {
        if (processedBarcodes.contains(barcodeValue)) {
            return;
        }
        processedBarcodes.add(barcodeValue);

//        Log.w("1response--------------------" , barcodeValue);

        Call<Map<String , Object>> call;

        if (method.equals("registration") && activityId.isEmpty()){
            call = WebserviceHelper.getInstancePost().registration("Bearer "+preferencesHelper.getToken() , barcodeValue);
        }else if (method.equals("checkIn") && activityId.isEmpty()){
            call = WebserviceHelper.getInstancePost().checkin("Bearer "+preferencesHelper.getToken() , barcodeValue);
        }else if (method.equals("checkOut") && activityId.isEmpty()){
            call = WebserviceHelper.getInstancePost().checkout("Bearer "+preferencesHelper.getToken() , barcodeValue);
        }else if (method.equals("checkIn")){
            call = WebserviceHelper.getInstancePost().activityCheckIn("Bearer "+preferencesHelper.getToken() , barcodeValue);
        }else if (method.equals("checkOut")){
            call = WebserviceHelper.getInstancePost().activityCheckOut("Bearer "+preferencesHelper.getToken() , barcodeValue);
        }else call = WebserviceHelper.getInstancePost().registration("Bearer "+preferencesHelper.getToken() , barcodeValue);


        try {

            call.enqueue(new Callback<Map<String , Object>>() {
                @Override
                public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {
                    if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                        showSuccessFeedback();
                        if (response.body() != null && response.body().containsKey("message")){
                            Toast.makeText(context, Objects.requireNonNull(response.body().get("message")).toString() , Toast.LENGTH_LONG).show();
                        }
                    }else {
                        if (response.body() != null && response.body().containsKey("message")){
                            Toast.makeText(context, Objects.requireNonNull(response.body().get("message")).toString() , Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Map<String , Object>> call, @NonNull Throwable t) {
                    Log.w("response", Objects.requireNonNull(t.getMessage()));
                    Toast.makeText(context, R.string.serverError , Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            getActivity().runOnUiThread(() -> {
                barcodeTextView.setText("Error: " + e.getMessage());
            });
        } finally {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                processedBarcodes.remove(barcodeValue);
            }, 10000);
        }

    }

    private void showSuccessFeedback() {
        getActivity().runOnUiThread(() -> {
            feedbackOverlay.setVisibility(View.VISIBLE);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                feedbackOverlay.setVisibility(View.GONE);
            }, 1000);
        });
    }

    private void init(View v) {
        barcodeTextView = v.findViewById(R.id.barcodeTextView);
        previewView = v.findViewById(R.id.previewView);
        feedbackOverlay = v.findViewById(R.id.feedback_overlay);
        preferencesHelper = new PreferencesHelper(context);
    }

}