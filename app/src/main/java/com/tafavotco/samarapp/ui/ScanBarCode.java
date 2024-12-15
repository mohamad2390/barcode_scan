package com.tafavotco.samarapp.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.annotation.CameraExecutor;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.zxing.ResultPoint;

import com.tafavotco.samarapp.R;
import com.journeyapps.barcodescanner.BarcodeView;

import com.journeyapps.barcodescanner.BarcodeResult;
import com.tafavotco.samarapp.Webservice.WebserviceHelper;
import com.tafavotco.samarapp.model.loginResponseModel;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ScanBarCode extends AppCompatActivity {

    private TextView barcodeTextView;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private final List<String> processedBarcodes = new ArrayList<>();
    private FrameLayout feedbackOverlay;
    private String serviceKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_bar_code);

        barcodeTextView = findViewById(R.id.barcodeTextView);
        previewView = findViewById(R.id.previewView);
        feedbackOverlay = findViewById(R.id.feedback_overlay);

        serviceKey = getIntent().getStringExtra("method");

        cameraExecutor = new ExecutorService() {
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

            @Override
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }


    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
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


//                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
//                    @androidx.annotation.OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
//                    android.media.Image mediaImage = imageProxy.getImage();
//                    if (mediaImage != null) {
//                        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//                        BarcodeScanner scanner = BarcodeScanning.getClient();
//                        scanner.process(image)
//                                .addOnSuccessListener(barcodes -> {
//                                    for (Barcode barcode : barcodes) {
//                                        String rawValue = barcode.getRawValue();
//                                        barcodeTextView.setText(rawValue);
//                                        sendBarcodeToWebService(rawValue);
//                                        Log.w("1response--------------------" , rawValue);
//
//                                        break;
//                                    }
//                                })
//                                .addOnFailureListener(e -> barcodeTextView.setText("Error: " + e.getMessage()))
//                                .addOnCompleteListener(task -> imageProxy.close());
//                    }
//                });

                // اتصال دوربین به لایف‌سایکل
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
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
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    private void sendBarcodeToWebService(String barcodeValue) {
        if (processedBarcodes.contains(barcodeValue)) {
            return;
        }
        processedBarcodes.add(barcodeValue);

        Log.w("1response--------------------" , barcodeValue);

        Call<String> call;
        BarcodeRequest barcodeRequest = new BarcodeRequest(barcodeValue);

        // انتخاب تابع مناسب بر اساس serviceKey
        switch (serviceKey) {
            case "registration":
                call = WebserviceHelper.getInstancePost().registration(barcodeRequest);
                break;
            case "checkIn":
                call = WebserviceHelper.getInstancePost().checkin(barcodeRequest);
                break;
            case "checkOut":
                call = WebserviceHelper.getInstancePost().checkout(barcodeRequest);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + serviceKey);
        }

//        Toast.makeText(ScanBarCode.this, "barcode send" , Toast.LENGTH_LONG).show();


        new Thread(() -> {
            try {

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
//                            Log.w("1response--------------------", response.body().toString());
//                if (response.body().getSuccess()) {
////                                pDialog.cancel();
//                    Intent myIntent = new Intent(Login.this, verifying.class);
//                    myIntent.putExtra("username", cell);
//                    startActivity(myIntent);
//                    finish();
//                }else {
////                                pDialog.cancel();
                        Toast.makeText(ScanBarCode.this, "barcode send" , Toast.LENGTH_LONG).show();

//                }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.w("response", Objects.requireNonNull(t.getMessage()));
                        Toast.makeText(ScanBarCode.this, R.string.serverError , Toast.LENGTH_LONG).show();
                    }
                });

                showSuccessFeedback();

            } catch (Exception e) {
                runOnUiThread(() -> {
                    barcodeTextView.setText("Error: " + e.getMessage());
                });
            } finally {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    processedBarcodes.remove(barcodeValue);
                }, 10000);
            }
        }).start();

    }

    public class BarcodeRequest{
        private String barcode;
        public BarcodeRequest(String barcode) {
            this.barcode = barcode;
        }
    }

    private void showSuccessFeedback() {
        runOnUiThread(() -> {
            // نمایش رنگ سبز
            feedbackOverlay.setVisibility(View.VISIBLE);

            // مخفی کردن رنگ سبز بعد از 500 میلی‌ثانیه
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                feedbackOverlay.setVisibility(View.GONE);
            }, 500);
        });
    }

}