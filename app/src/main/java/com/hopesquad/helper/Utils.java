package com.hopesquad.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.hopesquad.R;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    private static ProgressBarDialog pDialog = null;
    /**
     * The alertbox.
     */
    public static AlertDialog.Builder alertbox;

    /**
     * The alert dialog.
     */
    public static AlertDialog alertDialog;


    public static boolean isOnline(Context ctx) {
        boolean status = false;

        ConnectivityManager connMgr = (ConnectivityManager) ctx.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        status = (networkInfo != null && networkInfo.isConnected());
        if (status) {
            return status;
        } else {
            Toast.makeText(ctx, "No network connection...", Toast.LENGTH_SHORT).show();
            // showAlertDialogForInternet(ctx);
            return status;
        }
    }

    public static void showAlertDialogForInternet(final Context context) {
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                .setCancelable(false)
                .setTitle("No Internet Connection")
                .setMessage("Sorry, no internet connectivity detected. Please reconnect and try again")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        ((Activity) context).finish();
                    }
                }).create();
        try {
            if (!dialog.isShowing())
                dialog.show();
            else
                dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Show Alert Message
     *
     * @param context
     * @param title
     * @param message
     */

    public static void showAlertMessage(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    public static void showAlertMessage(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton("OK", onClickListener)
                .show();

    }

    /**
     * Show alert dialog.
     *
     * @param title                    the title
     * @param message                  the message
     * @param context                  the context
     * @param redirectToPreviousScreen the redirect to previous screen
     */
    public static void showAlertDialog(final String title, String message, final Activity context,
                                       final boolean redirectToPreviousScreen) {
        try {
            if (alertDialog != null && alertDialog.isShowing()) {
            } else {
                alertbox = new AlertDialog.Builder(context);
                alertbox.setMessage(message);
                alertbox.setTitle(title);
                alertbox.setCancelable(false);
                alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        alertDialog.dismiss();
                        if (redirectToPreviousScreen) {
                            context.finish();
                        }
                    }
                });
                alertDialog = alertbox.create();
                alertDialog.show();
            }
        } catch (NullPointerException e) {
        }
    }


    /**
     * Show Progress Dialog
     *
     * @param activity    Activity
     * @param cancellable Cancelled when touch outside
     */
    public static void showProgressDialog(Activity activity, boolean cancellable) {
        pDialog = new ProgressBarDialog();
        pDialog.setCanceledOnTouchOutside(cancellable);
        try {
            pDialog.show(activity.getFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ProgressBarDialog getProgressDailog(Activity activity, boolean cancellable) {
        ProgressBarDialog pDialog = new ProgressBarDialog();
        pDialog.setCanceledOnTouchOutside(cancellable);
        try {
            pDialog.show(activity.getFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pDialog;
    }


    /**
     * Close progress pDialog
     */
    public static void closeProgressDialog() {
        try {
            if (pDialog != null && pDialog.isVisible()) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Check Json String For Null Values
     *
     * @param jsonObject   JsonObject
     * @param KEY_VARIABLE Variable to fetch from JsonObject
     */
    public static String checkJsonStringForNull(JSONObject jsonObject, String KEY_VARIABLE) {
        String data = "";
        try {
            if (!jsonObject.isNull(KEY_VARIABLE)) {
                data = jsonObject.optString(KEY_VARIABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                data = jsonObject.optInt(KEY_VARIABLE, 0) + "";
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return data;
    }

    /**
     * Hide Soft Keyboard
     *
     * @param activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focusView = activity.getCurrentFocus();
            if (focusView != null) {
                inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    /**
     * Show Soft Keyboard
     *
     * @param activity
     */
    public static void showSoftKeyboard(Activity activity) {
        if (activity != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focusView = activity.getCurrentFocus();
            if (focusView != null) {
                inputMethodManager.showSoftInput(focusView, 0);
            }
        }
    }

    /**
     * Validate Email ID
     *
     * @param target
     */
    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    /**
     * Validate Mobile Number
     *
     * @param phone
     */
    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    /**
     * Get Device ID
     *
     * @param context
     */
    public static String getSecureDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    /**
     * Open Setting for Marshmallow permissions
     *
     * @param context
     */
    public static void openSetting(Context context) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + context.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(myAppSettings);
    }

    /*
     * Gets the file path of the given Uri.
     */
    @SuppressLint("NewApi")
    public static String getPath(Uri uri, Context context) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get MD5 for list of strings
     *
     * @param stringArrayList
     * @return String Md5
     */
    public static String getMD5OfArray(ArrayList<String> stringArrayList) {
        if (stringArrayList.size() > 0) {
            try {
                Collections.sort(stringArrayList, String.CASE_INSENSITIVE_ORDER);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        String hash = TextUtils.join("", stringArrayList);
        return Utils.MD5(hash);
    }

    /**
     * Get MD5 hash of String
     *
     * @param md5
     */
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * Get Device screen display size
     */
    public static Point getDisplaySize(WindowManager windowManager) {
        try {
            if (Build.VERSION.SDK_INT > 16) {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
            } else {
                return new Point(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    /**
     * Convert dp To px
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }


    /**
     * Add Alpha Digits Input Filter on EditText
     *
     * @param editText editText
     */
    public static void addAlphaDigitsFilterOnEditText(AppCompatEditText editText) {
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.toString().matches("[a-zA-Z ]+")) {
                            return src;
                        }
                        return "";
                    }
                }
        });

    }


    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        return vId;
    }

}