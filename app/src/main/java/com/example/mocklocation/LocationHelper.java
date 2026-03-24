package com.example.mocklocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;

public class LocationHelper {
    private static final String MOCK_PROVIDER = LocationManager.MOCK_PROVIDER;
    private LocationManager locationManager;
    private boolean isMocking = false;

    public LocationHelper(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean startMockLocation(double latitude, double longitude) {
        if (isMocking) return false;

        try {
            if (!locationManager.getProviders(true).contains(MOCK_PROVIDER)) {
                locationManager.addTestProvider(
                        MOCK_PROVIDER,
                        false, false, false, true, false, false, false,
                        android.location.Criteria.POWER_LOW,
                        android.location.Criteria.ACCURACY_FINE
                );
                locationManager.setTestProviderEnabled(MOCK_PROVIDER, true);
            }

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
            return true;

        } catch (SecurityException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMockLocation(double latitude, double longitude) {
        if (!isMocking) return false;
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void stopMockLocation() {
        if (!isMocking) return;
        try {
            locationManager.removeTestProvider(MOCK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isMocking = false;
    }

    public boolean isMocking() {
        return isMocking;
    }
}
