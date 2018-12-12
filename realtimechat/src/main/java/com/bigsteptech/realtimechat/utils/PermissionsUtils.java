package com.bigsteptech.realtimechat.utils;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class PermissionsUtils {

    public static boolean checkManifestPermission(Context context, String manifestPermission) {
        return ContextCompat.checkSelfPermission(context, manifestPermission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestForManifestPermission(Context context, String manifestPermission, int requestCode) {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, manifestPermission)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{manifestPermission}, requestCode);
        }
    }
}
