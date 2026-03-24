package com.example.mocklocation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private EditText etLat, etLng;
    private Button btnStart, btnStop;
    private TextView tvStatus;
    private LocationHelper locationHelper;

    private static final int REQUEST_LOCATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLat = findViewById(R.id.et_latitude);
        etLng = findViewById(R.id.et_longitude);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        tvStatus = findViewById(R.id.tv_status);

        locationHelper = new LocationHelper(this);

        checkPermissions();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMock();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMock();
            }
        });
    }

    private void startMock() {
    // 不再检查 ALLOW_MOCK_LOCATION，直接尝试模拟
    // 模拟位置是否真正可用，会在 LocationHelper 中抛出异常或失败

    String latStr = etLat.getText().toString().trim();
    String lngStr = etLng.getText().toString().trim();
    if (latStr.isEmpty() || lngStr.isEmpty()) {
        Toast.makeText(this, "请输入经纬度", Toast.LENGTH_SHORT).show();
        return;
    }

    double lat, lng;
    try {
        lat = Double.parseDouble(latStr);
        lng = Double.parseDouble(lngStr);
    } catch (NumberFormatException e) {
        Toast.makeText(this, "经纬度格式错误", Toast.LENGTH_SHORT).show();
        return;
    }

    Intent serviceIntent = new Intent(this, MockLocationService.class);
    serviceIntent.putExtra("latitude", lat);
    serviceIntent.putExtra("longitude", lng);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(serviceIntent);
    } else {
        startService(serviceIntent);
    }

    tvStatus.setText("状态：模拟中 (纬度: " + lat + ", 经度: " + lng + ")");
}

    private void stopMock() {
        Intent serviceIntent = new Intent(this, MockLocationService.class);
        stopService(serviceIntent);
        locationHelper.stopMockLocation();
        tvStatus.setText("状态：未模拟");
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        REQUEST_LOCATION_PERMISSION + 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "位置权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要位置权限才能模拟", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
