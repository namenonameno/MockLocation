package com.example.mocklocation;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MockLocationService extends Service {
    private static final String CHANNEL_ID = "mock_location_channel";
    private static final int NOTIFICATION_ID = 1001;
    private LocationHelper locationHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        locationHelper = new LocationHelper(this);
        createNotificationChannel();
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            double lat = intent.getDoubleExtra("latitude", 0);
            double lng = intent.getDoubleExtra("longitude", 0);
            if (lat != 0 && lng != 0) {
                boolean success = locationHelper.startMockLocation(lat, lng);
                if (!success) {
                    // 模拟失败时，可以停止服务并通知用户
                    stopSelf();
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        locationHelper.stopMockLocation();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "模拟位置服务",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("模拟位置运行中")
                .setContentText("正在向系统注入模拟位置")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .build();
    }
}
