package com.afanty.utils.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.utils.PermissionsUtils;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class LocationService {

    private static final String TAG = "Location.Service";
    private static volatile LocationService sInstance;
    @Nullable
    private Location mLastKnownLocation;
    private long mLocationLastUpdatedMillis;

    private static final long DEFAULT_LOCATION_REFRESH_TIME_MILLIS = 10 * 60 * 1000;
    private volatile long mMinimumLocationRefreshTimeMillis = DEFAULT_LOCATION_REFRESH_TIME_MILLIS;

    @NonNull
    static LocationService getInstance() {
        LocationService locationService = sInstance;
        if (locationService == null) {
            synchronized (LocationService.class) {
                locationService = sInstance;
                if (locationService == null) {
                    locationService = new LocationService();
                    sInstance = locationService;
                }
            }
        }
        return locationService;
    }

    enum ValidLocationProvider {
        NETWORK(LocationManager.NETWORK_PROVIDER),
        @Deprecated
        GPS(LocationManager.GPS_PROVIDER);

        @NonNull
        final String name;

        ValidLocationProvider(@NonNull String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

        public boolean hasRequiredPermissions(@NonNull Context context) {
            return PermissionsUtils.hasPermission(context, ACCESS_COARSE_LOCATION);
        }
    }

    public void setLocationMinRefreshInterval(long interval) {
        mMinimumLocationRefreshTimeMillis = interval;
    }

    public long getLocationMinRefreshInterval() {
        return mMinimumLocationRefreshTimeMillis;
    }

    @Nullable
    public static Location getLastKnownLocation(@Nullable Context context) {
        final LocationService locationService = getInstance();
        if (!needRefreshLocation()) {
            return locationService.mLastKnownLocation;
        }

        if (context == null)
            return null;

        Location location = getLocationFromProvider(context, ValidLocationProvider.NETWORK);

        if (location != null)
            locationService.setLastLocation(location);

        return locationService.mLastKnownLocation;
    }

    @SuppressLint("MissingPermission")
    @Nullable
    private static Location getLocationFromProvider(@NonNull final Context context, @NonNull final ValidLocationProvider provider) {
        if (!provider.hasRequiredPermissions(context)) {
            return null;
        }

        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return locationManager.getLastKnownLocation(provider.toString());
        } catch (SecurityException e) {
        } catch (IllegalArgumentException e) {
        } catch (NullPointerException e) {
        }
        return null;
    }

    private static boolean needRefreshLocation() {
        LocationService locationService = getInstance();
        if (locationService.mLastKnownLocation == null) {
            return true;
        }

        return SystemClock.elapsedRealtime() - locationService.mLocationLastUpdatedMillis > locationService.getLocationMinRefreshInterval();
    }

    private void setLastLocation(@Nullable Location location) {
        if (location == null)
            return;

        final LocationService locationService = getInstance();
        locationService.mLastKnownLocation = location;
        locationService.mLocationLastUpdatedMillis = SystemClock.elapsedRealtime();
    }
}
