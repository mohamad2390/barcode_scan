package com.tafavotco.samarapp.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.ResultPoint;

import com.tafavotco.samarapp.R;
import com.journeyapps.barcodescanner.BarcodeView;

import com.journeyapps.barcodescanner.BarcodeResult;

import java.util.List;

import javax.security.auth.callback.Callback;


public class ScanBarCode extends AppCompatActivity {

    private BarcodeView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan_bar_code);

        scannerView = findViewById(R.id.zxing_barcode_scanner);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
        } else {
            // مجوز قبلاً داده شده است، شروع اسکن

            scannerView.startActionMode(callback);
        }

        // شروع اسکن
        Callback callback = scannerView.decodeContinuous(new DecodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (result.getText() != null) {
                    // دریافت نتیجه اسکن
                    String resultText = result.getText();
                    Log.d("ScanResult", "Scanned value: " + resultText);
                    // می‌توانید اینجا کاری مثل نمایش نتیجه یا ارسال آن به سرور انجام دهید
                }
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {
                // نقطه‌های ممکن اسکن را می‌توان در اینجا پردازش کرد
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        // شروع اسکن وقتی اکتیویتی در وضعیت نمایشی است
        scannerView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // متوقف کردن اسکن وقتی اکتیویتی در وضعیت پس‌زمینه است
        scannerView.pause();
    }

}