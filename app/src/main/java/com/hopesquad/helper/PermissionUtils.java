package com.hopesquad.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by shubhangi on 11/2/17.
 */

public class PermissionUtils {

    public static final int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 4;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_ASK_PERMISSIONS_LOCATION = 2;
    private static final int REQUEST_CODE_FOR_AUDIO_RECORD = 3;


    /*Location Permission*/
    public static void askForLocationPermissionIfNecessary(Activity activity) {
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
                }
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_ASK_PERMISSIONS_LOCATION);
                }
            }
            return;
        }
    }


    public static boolean askForAudioRecorderPermissionIfNecessary(Activity activity) {
        boolean permit = false;
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasCameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        int hasReadExternalPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED || hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED || hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECORD_AUDIO)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_FOR_AUDIO_RECORD);
                    permit = true;
                }
                return permit;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_FOR_AUDIO_RECORD);
                }
            }
            permit = true;
            return permit;
        } else {
            permit = true;
        }
        // insert the working code here
        return permit;
    }


    public static boolean askForCameraPermissionIfNecessary(Activity activity) {
        boolean permit = false;
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasCameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int hasReadExternalPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED || hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED || hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                     activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
                    permit = true;
                }
                return permit;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                     activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
                }
            }
            permit = true;
            return permit;
        } else {
            permit = true;
        }
        // insert the working code here
        return permit;
    }

    public static boolean askForStoragePermissionIfNecessary(Activity activity) {
        boolean permit = false;
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE);
                    permit = true;
                }
                return permit;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS_READ_EXTERNAL_STORAGE);
                }
            }
            permit = true;
            return permit;
        }else {
            permit = true;
        }
        // insert the working code here
        return permit;
    }

}
