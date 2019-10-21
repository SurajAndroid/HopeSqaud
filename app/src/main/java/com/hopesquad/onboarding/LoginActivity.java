package com.hopesquad.onboarding;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.hopesquad.R;
import com.hopesquad.activity.HomeScreenActivity;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.DeviceType;
import com.hopesquad.models.LoginSuccess;
import com.hopesquad.models.UserDetail;
import com.hopesquad.models.UserStatus;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailEdtTxt;
    private EditText passwordEdtTxt;
    private RelativeLayout backBtn;
    private TextView loginBtn;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        emailEdtTxt = (EditText) findViewById(R.id.emailEdtTxt);
        passwordEdtTxt = (EditText) findViewById(R.id.passwordEdtTxt);
        loginBtn    = (TextView) findViewById(R.id.loginBtn);
        backBtn     = (RelativeLayout) findViewById(R.id.backBtn);

        loginBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

//        loginBtn.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                PendingIntent pendingIntent = PendingIntent.getActivity(LoginActivity.this, 0 /* Request code */, intent,
//                        PendingIntent.FLAG_ONE_SHOT);
//
//                String channelId = getString(R.string.app_name);
//                Uri defaultSoundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.whistle);
////                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                NotificationCompat.Builder notificationBuilder =
//                        new NotificationCompat.Builder(LoginActivity.this, channelId)
//                                .setSmallIcon(R.mipmap.ic_launcher)
//                                .setContentTitle("FCM Message")
//                                .setContentText("messageBody")
//                                .setAutoCancel(true)
//                                .setSound(defaultSoundUri)
//                                .setContentIntent(pendingIntent);
//
//                NotificationManager notificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//
//                return false;
//            }
//        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.loginBtn:

                String email = emailEdtTxt.getText().toString().trim();
                String password = passwordEdtTxt.getText().toString().trim();

                if (email.isEmpty()) {
                    Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                            "Please enter your email.");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                            "Invalid email.");
                } else if (password.isEmpty()) {
                    Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                            "Please enter your password.");
                } else if (password.length() < 6 && password.length() > 10) {
                    Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                            "Password should be between 6-10 characters.");
                } else {
                    callWsForLogin(email, password);
                }

                break;
            case R.id.backBtn:
                finish();
                break;
        }

    }

    /**
     * Call Ws For Login
     * <p>
     * NOTE:
     * A. UserStatus: 1 is for Active account and 0 for inactive.
     * B. UserType: 1 = SuperAdmin ,  UserType: 2 = Trainer,  UserType:3 = Client
     * @param email    Email
     * @param password Password
     */
    private void callWsForLogin(String email, String password) {

        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        Utils.showProgressDialog(this, false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("GymId", "18");
        params.put("Email", email);
        params.put("Password", password);
        params.put("DeviceType", DeviceType.ANDROID);
        params.put("DeviceToken", FirebaseInstanceId.getInstance().getToken());
        params.put("FbId", "");

        System.out.println("Param  : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.LOGIN);
        System.out.println("url  : "+url);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("response   : "+response);
                try {
                    int responseCode = Integer.parseInt(response.getString("code"));
                    if (responseCode == 200) {
                        Gson gson = new Gson();
                        LoginSuccess loginSuccess = gson.fromJson(response.toString(), LoginSuccess.class);
                        if (Integer.parseInt(loginSuccess.getResult().getUserStatus()) == UserStatus.IN_ACTIVE) {
                            Utils.showAlertMessage(LoginActivity.this, getResources().getString(R.string.app_name),
                                    loginSuccess.getMessage());
                        } else {
                            sessionManager.createUserLoginSession(loginSuccess.getResult());
                            Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else if (responseCode == 400) {
                        final String message = response.getString("message");
                        new AlertDialog.Builder(LoginActivity.this)
                                .setCancelable(false)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage(message)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!message.toLowerCase().contains("your account is on hold"))
                                            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        Gson gson = new Gson();
                        LoginSuccess loginSuccess = gson.fromJson(response.toString(), LoginSuccess.class);
                        Utils.showAlertMessage(LoginActivity.this, getResources().getString(R.string.app_name),
                                loginSuccess.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Utils.closeProgressDialog();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.closeProgressDialog();
                Utils.showAlertMessage(LoginActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }
}
