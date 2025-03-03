package com.acceleronix.app.demo.utils;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtil {

    public static final int REQUEST_CODE = 5;

    private static final String[] permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    } : new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final String[] locationPermission = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final String[] blueToothPermission = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
    };

    public static boolean isPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            for (int i = 0; i < permission.length; i++) {
                int checkPermission = ContextCompat.checkSelfPermission(activity, permission[i]);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public static boolean isLocationPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(context, locationPermission[0]);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasBlueToothPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= 23 && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            int checkPermission = ContextCompat.checkSelfPermission(context, locationPermission[0]);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            return true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            for (int i = 0; i < blueToothPermission.length; i++) {
                int checkPermission = ContextCompat.checkSelfPermission(context, blueToothPermission[i]);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }

    public static boolean checkPermission(Activity activity) {
        if (isPermissionGranted(activity)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(activity, permission, REQUEST_CODE);
            return false;
        }
    }

}
