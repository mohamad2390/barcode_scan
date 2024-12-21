package com.tafavotco.samarapp.ui;

import static com.tafavotco.samarapp.data.constValue.server_URL;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
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
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanBarCodeFragment extends Fragment {

    private TextView barcodeTextView;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private final List<String> processedBarcodes = new ArrayList<>();
    private FrameLayout feedbackOverlay;
    private String method;
    private String activityId;
    private PreferencesHelper preferencesHelper;
    private Context context;
    private EditText edit_txt_phone;
    private Button send;
    private ImageView person_avatar;
    private TextView txt_fName , txt_lName , txt_national_code;

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

//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
//        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            startCamera();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
        }

        Bundle bundle = this.getArguments();
        if (bundle != null) {
             method = Objects.requireNonNull(bundle.getBundle("bundle_method")).getString("method" , "");
             activityId = Objects.requireNonNull(bundle.getBundle("bundle_activity_id")).getString("activity_id" , "");
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

//        cameraExecutor = new ExecutorService() {
//            @Override
//            public void execute(Runnable runnable) {
//
//            }
//
//            @Override
//            public void shutdown() {
//
//            }
//
//            @Override
//            public List<Runnable> shutdownNow() {
//                return Collections.emptyList();
//            }
//
//            @Override
//            public boolean isShutdown() {
//                return false;
//            }
//
//            @Override
//            public boolean isTerminated() {
//                return false;
//            }
//
//            @Override
//            public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
//                return false;
//            }
//
//            @Override
//            public <T> Future<T> submit(Callable<T> callable) {
//                return null;
//            }
//
//            @Override
//            public <T> Future<T> submit(Runnable runnable, T t) {
//                return null;
//            }
//
//            @Override
//            public Future<?> submit(Runnable runnable) {
//                return null;
//            }
//
//            @Override
//            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection) throws InterruptedException {
//                return Collections.emptyList();
//            }
//
//            @Override
//            public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws InterruptedException {
//                return Collections.emptyList();
//            }
//
//            @Override
//            public <T> T invokeAny(Collection<? extends Callable<T>> collection) throws ExecutionException, InterruptedException {
//                return null;
//            }
//
//            @Override
//            public <T> T invokeAny(Collection<? extends Callable<T>> collection, long l, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
//                return null;
//            }
//        };

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
//            startCamera();
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

    private void sendBarcodeToWebService(String participationHash) {
        if (processedBarcodes.contains(participationHash)) {
            return;
        }
        processedBarcodes.add(participationHash);

        Log.w("response2" , participationHash);

        Call<Map<String , Object>> call;

        if (method.equals("registration") && activityId.isEmpty()){
            call = WebserviceHelper.getInstancePost().registration("Bearer "+preferencesHelper.getToken() , participationHash);
        }else if (method.equals("checkIn") && activityId.isEmpty()){
            call = WebserviceHelper.getInstancePost().checkin("Bearer "+preferencesHelper.getToken() , participationHash);
        }else if (method.equals("checkOut") && activityId.isEmpty()){
            call = WebserviceHelper.getInstancePost().checkout("Bearer "+preferencesHelper.getToken() , participationHash);
        }else if (method.equals("checkIn")){
            call = WebserviceHelper.getInstancePost().activityCheckIn("Bearer "+preferencesHelper.getToken() , participationHash);
        }else if (method.equals("checkOut")){
            call = WebserviceHelper.getInstancePost().activityCheckOut("Bearer "+preferencesHelper.getToken() , participationHash);
        }else call = WebserviceHelper.getInstancePost().registration("Bearer "+preferencesHelper.getToken() , participationHash);


        try {

            call.enqueue(new Callback<Map<String , Object>>() {
                @Override
                public void onResponse(@NonNull Call<Map<String , Object>> call, @NonNull Response<Map<String , Object>> response) {
                    if (response.body() != null && response.body().containsKey("success") && Convert.toBoolean(response.body().get("success"))) {
                        showSuccessFeedback();
                        if (response.body() != null && response.body().containsKey("message")){
                            Toast.makeText(context, Objects.requireNonNull(response.body().get("message")).toString() , Toast.LENGTH_LONG).show();
                        }
                        if (response.body().containsKey("id")){
                            Glide.with(context)
                                    .load(server_URL+"Participants/cover/"+ Objects.requireNonNull(response.body().get("id")).toString())
                                    .placeholder(R.drawable.ic_launcher_foreground)
                                    .into(person_avatar);
                        }
                        if (response.body().containsKey("fName") && response.body().containsKey("lName")){
                            txt_fName.setText(Objects.requireNonNull(response.body().get("fName")).toString());
                            txt_lName.setText(Objects.requireNonNull(response.body().get("lName")).toString());
                        }
                        if (response.body().containsKey("nCode")){
                            txt_national_code.setText(Objects.requireNonNull(response.body().get("nCode")).toString());
                        }
                    }else {
                        if (response.body() != null && response.body().containsKey("message")){
                            Toast.makeText(context, Objects.requireNonNull(response.body().get("message")).toString() , Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Map<String , Object>> call, @NonNull Throwable t) {
                    Log.w("response1", Objects.requireNonNull(t.getMessage()));
                    Toast.makeText(context, R.string.serverError , Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            getActivity().runOnUiThread(() -> {
                barcodeTextView.setText("Error: " + e.getMessage());
            });
        } finally {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                processedBarcodes.remove(participationHash);
            }, 10000);
        }

    }

    private void showSuccessFeedback() {
            feedbackOverlay.setVisibility(View.VISIBLE);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                feedbackOverlay.setVisibility(View.GONE);
            }, 1000);
    }

    private void init(View v) {
        edit_txt_phone = v.findViewById(R.id.edit_txt_phone);
        send = v.findViewById(R.id.btn_send);
        person_avatar = v.findViewById(R.id.img_person_avatar);
        txt_fName = v.findViewById(R.id.txt_fName);
        txt_lName = v.findViewById(R.id.txt_lName);
        txt_national_code = v.findViewById(R.id.txt_national_code);
        previewView = v.findViewById(R.id.previewView);
        feedbackOverlay = v.findViewById(R.id.feedback_overlay);
        preferencesHelper = new PreferencesHelper(context);
    }

}