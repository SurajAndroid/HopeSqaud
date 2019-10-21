package com.hopesquad.onboarding;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.hopesquad.API.APIClient;
import com.hopesquad.ManageSession;
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

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;


public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private Button emailLoginBtn, facebookLoginBtn;
    private TextView signUpBtn;
    private SessionManager sessionManager;
    private LinearLayout onBoardingLilay;
    private ImageView logoImg;
    private static Context context;

    private CallbackManager callbackmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_splash);
        context = this;
        callbackmanager = CallbackManager.Factory.create();

        sessionManager = new SessionManager(this);

        emailLoginBtn = (Button) findViewById(R.id.emailLoginBtn);
        facebookLoginBtn = (Button) findViewById(R.id.facebookLoginBtn);
        signUpBtn = (TextView) findViewById(R.id.signUpBtn);
        logoImg = (ImageView) findViewById(R.id.logoImg);
        onBoardingLilay = (LinearLayout) findViewById(R.id.onBoardingLilay);

        emailLoginBtn.setOnClickListener(this);
        facebookLoginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

        // FadeIn Logo
        logoImg.setAlpha(0f);
        logoImg.setVisibility(View.VISIBLE);
        logoImg.animate()
                .alpha(1f)
                .setDuration(1500l)
                .setListener(null);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    animate();
                }
            }
        }, 2000);


        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.hopesquad", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

       System.out.println("Hash Key : "+ManageSession.printHashKey(context));

    }



    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.emailLoginBtn:
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.signUpBtn:
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.facebookLoginBtn:
                callFacebookLogin();
                break;
        }
    }

    private void animate() {

        // Translate logo to top
        float i = Utils.getDisplaySize(getWindowManager()).y - Utils.dpToPx(210);
        logoImg.animate()
                .translationY(-i)
                .setDuration(2500l)
                .setListener(null);

        // FadeIn Bottom Layouts
        onBoardingLilay.setAlpha(0f);
        onBoardingLilay.setVisibility(View.VISIBLE);
        onBoardingLilay.animate()
                .alpha(1f)
                .setDuration(3000l)
                .setListener(null);

    }



    /**
     * Call Facebook Login
     */
    private void callFacebookLogin() {

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(SplashActivity.this, Arrays.asList("public_profile", "email"));

        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // LoginManager.getInstance().logOut();
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject data,
                                            GraphResponse response) {
                                        try {
                                            String fbID = data.getString("id");
                                            URL img_value = null;
                                            img_value = new URL("http://graph.facebook.com/" + fbID + "/picture?type=large");
                                            String personName = data.getString("name");
                                            String fbEmail = "";
                                            if (data.has("email")) {
                                                fbEmail = data.getString("email");
                                            }
//                                            callWsForLogin(fbID, personName, fbEmail);
                                            callFBWsForLogin(fbID,fbEmail, personName);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Log.v("SplashActivity", response.toString());
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("Cancel Fb", "On cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("Fb error", error.toString());
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (callbackmanager != null)
                callbackmanager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Call Ws For Login
     * <p>
     * NOTE:
     * A. UserStatus: 1 is for Active account and 0 for inactive.
     * B.  UserType: 1 = SuperAdmin ,  UserType: 2 = Trainer,  UserType:3 = Client
     *
     * @param fb_id      Facebook ID
     * @param personName Facebook User Name
     * @param fbEmail    Facebook Email
     */
    private void callWsForLogin(final String fb_id, final String personName, final String fbEmail) {
        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        Utils.showProgressDialog(this, false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("IsAdminLogin", 0);
        params.put("DeviceToken", FirebaseInstanceId.getInstance().getToken());
        params.put("fbid", fb_id);
        params.put("email", "");
        params.put("password", "");
        params.put("DeviceType", DeviceType.ANDROID);

        String url = AppConstants.getMethodUrl(AppConstants.LOGIN);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {

                    int responseCode = response.getInt("ResponseCode");
                    if (responseCode == 202) {
                        initSignUpWithFbID(fb_id, fbEmail, personName);
                    } else if (responseCode == 200) {
                        Gson gson = new Gson();
                        UserDetail userDetail = gson.fromJson(String.valueOf(response), UserDetail.class);

                        if (userDetail.getUserStatus() == UserStatus.IN_ACTIVE) {
                            Utils.showAlertMessage(SplashActivity.this, getResources().getString(R.string.app_name),
                                    userDetail.getMessage());
                        } else {
                            sessionManager.createLoginSession(userDetail);
                            Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else if (responseCode == 203 || responseCode == 204) {
                        final String message = response.getString("Message");
                        new AlertDialog.Builder(SplashActivity.this)
                                .setCancelable(false)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage(message)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!message.toLowerCase().contains("your account is on hold"))
                                            startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    } else {
                        Utils.showAlertMessage(SplashActivity.this, getResources().getString(R.string.app_name),
                                response.getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Utils.closeProgressDialog();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
                Utils.closeProgressDialog();
                Utils.showAlertMessage(SplashActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }


    private void callFBWsForLogin(final String fbid, final String email, final String personName) {

        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        Utils.showProgressDialog(this, false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("GymId", "18");
        params.put("Email", email);
        params.put("Password", "");
        params.put("DeviceType", DeviceType.ANDROID);
        params.put("DeviceToken", FirebaseInstanceId.getInstance().getToken());
        params.put("FbId", fbid);

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
                            Utils.showAlertMessage(SplashActivity.this, getResources().getString(R.string.app_name),
                                    loginSuccess.getMessage());
                        } else {
                            sessionManager.createUserLoginSession(loginSuccess.getResult());
                            Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } else if (responseCode == 400) {

                        final String message = response.getString("message");
                        if(message.equals("Your account is on hold, Please contact your HopeSquad admin.")){
                            new AlertDialog.Builder(SplashActivity.this)
                                    .setCancelable(false)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage(message)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                           /* if (!message.toLowerCase().contains("your account is on hold"))
                                                startActivity(new Intent(SplashActivity.this, SignUpActivity.class));*/
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }else {
                            initSignUpWithFbID(fbid, email, personName);
                        }
                    } else {
                        Gson gson = new Gson();
                        LoginSuccess loginSuccess = gson.fromJson(response.toString(), LoginSuccess.class);
                        Utils.showAlertMessage(SplashActivity.this, getResources().getString(R.string.app_name),
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
                Utils.showAlertMessage(SplashActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

    private void initSignUpWithFbID(String fb_id, String fbEmail, String personName) {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("fb_id", fb_id);
        intent.putExtra("fb_email", fbEmail);
        intent.putExtra("fb_user_name", personName);
        startActivity(intent);
    }

}
