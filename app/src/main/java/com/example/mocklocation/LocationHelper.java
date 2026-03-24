package com.example.mocklocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class LocationHelper {
    private static final String TAG = "LocationHelper";
    private static final String MOCK_PROVIDER = LocationManager.GPS_PROVIDER;
    private LocationManager locationManager;
    private boolean isMocking = false;

    public LocationHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * 开始模拟位置
     * @param latitude  纬度
     * @param longitude 经度
     * @return 是否成功
     */
    public boolean startMockLocation(double latitude, double longitude) {
        if (isMocking) {
            Log.w(TAG, "Already mocking, updating location instead");
            return updateMockLocation(latitude, longitude);
        }

        try {
            // 1. 检查是否已有测试提供者，若没有则添加
            if (!locationManager.getProviders(true).contains(MOCK_PROVIDER)) {
                locationManager.addTestProvider(
                        MOCK_PROVIDER,
                        false,      // requiresNetwork
                        false,      // requiresSatellite
                        false,      // requiresCell
                        true,       // hasMonetaryCost
                        false,      // supportsAltitude
                        false,      // supportsSpeed
                        false,      // supportsBearing
                        android.location.Criteria.POWER_LOW,
                        android.location.Criteria.ACCURACY_FINE
                );
                locationManager.setTestProviderEnabled(MOCK_PROVIDER, true);
                Log.i(TAG, "Test provider added and enabled");
            }

            // 2. 创建并设置模拟位置
            Location mockLocation = new Location(MOCK_PROVIDER);
            mockLocation.setLatitude(latitude);
            mockLocation.setLongitude(longitude);
            mockLocation.setAccuracy(5.0f);
            mockLocation.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }

            locationManager.setTestProviderLocation(MOCK_PROVIDER, mockLocation);
            isMocking = true;
            Log.i(TAG, "Mock location set to: " + latitude + ", " + longitude);
            return true;

        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新模拟位置（例如移动时调用）
     */
    public boolean updateMockLocation(double latitude, double longitude) {
        if (!isMocking) {
            Log.w(TAG, "Not mocking, call startMockLocation first");
            return false;
        }
        try {
            Location mockLocation = new Location(MOCK_PROVIDER);
            mockLocation.setLatitude(latitude);
            mockLocation.setLongitude(longitude);
            mockLocation.setAccuracy(5.0f);
            mockLocation.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            locationManager.setTestProviderLocation(MOCK_PROVIDER, mockLocation);
            Log.i(TAG, "Mock location updated to: " + latitude + ", " + longitude);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to update mock location", e);
            return false;
        }
    }

    /**
     * 停止模拟
     */
    public void stopMockLocation() {
        if (!isMocking) return;
        try {
            locationManager.removeTestProvider(MOCK_PROVIDER);
            Log.i(TAG, "Test provider removed");
        } catch (Exception e) {
            Log.e(TAG, "Error removing test provider", e);
        }
        isMocking = false;
    }

    public boolean isMocking() {
        return isMocking;
    }
}
