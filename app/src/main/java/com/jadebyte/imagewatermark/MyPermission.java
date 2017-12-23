package com.jadebyte.imagewatermark;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.lang.ref.WeakReference;

/**
 * Created by Wilberforce on 11/27/17 at 10:50 PM.
 */

public class MyPermission {
    public final static int WRITE_EXTERNAL_STORAGE = 1231;

    /**
     * Checks if external storage is readable
     * @return true is readable. Returns false otherwise
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals
                (state);
    }

    /**
     * Checks if this app has eternal storage write permission
     * @return true if granted. Returns false otherwise
     */
    public static boolean isWriteExtStorPermGranted(Context context) {
        WeakReference<Context> weakContext = new WeakReference<>(context);
        return ContextCompat.checkSelfPermission(weakContext.get(), android.Manifest.permission
                .WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests for external storage read permission
     * @param activity the activity to report the result to
     */
    public static void askWriteExtStorPerm(Activity activity) {
        WeakReference<Activity> weakActivity = new WeakReference<>(activity);
        ActivityCompat.requestPermissions(weakActivity.get(),
                new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
    }
}
