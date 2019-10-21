package com.hopesquad.onboarding;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.hopesquad.R;
import com.hopesquad.activity.HomeScreenActivity;
import com.hopesquad.activity.chat.ChatStatusActivity;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.DeviceType;
import com.hopesquad.models.LoginSuccess;
import com.hopesquad.models.TargetGoal;
import com.hopesquad.models.UserStatus;
import com.hopesquad.models.UserType;
import com.hopesquad.server.HTTPRequest;
import com.hopesquad.server.HttpConnector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, ClickInterfaceBottomSheet, RadioGroup.OnCheckedChangeListener {

    private EditText firstNameEdtTxt;
    private EditText lastNameEdtTxt;
    private EditText emailEdtTxt;
    private EditText passwordEdtTxt;
    private EditText confirmPasswordEdtTxt;
    private EditText phoneNumberEdtTxt;
    private EditText targetGoalEdtTxt;
    private EditText trainerEmailIdEdtTxt;
    private TextInputLayout confirmPasswordLayout;
    private TextInputLayout passwordLayout;
    private RelativeLayout backBtn;
    private RelativeLayout nextBtn;
    private LinearLayout initialSignUpLilay;
    private LinearLayout finalSignUpLilay;
    private boolean finalSignUp = false;
    private String userFirstName, userLastName, userEmail, userPassword;
    private TextView signUpBtn;
    private RadioButton radioBtnGenderMale, radioBtnGenderFemale, radioBtnTrainerYes, radioBtnTrainerNo;
    private int targetGoalIndex = 0;
    private RadioGroup trainerRadioGroup;
    private TextInputLayout trainerEmailInputLayout;
    private boolean isFbLogin = false;
    private String fbId = "", fbUserName = "", fbEmailId = "";
    private ImageView mAddProfileImageIV;
    public Context mContext;
    private File imageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_sign_up);

        mAddProfileImageIV  = (ImageView) findViewById(R.id.user_image);
        mAddProfileImageIV.setOnClickListener(this);
        firstNameEdtTxt     = (EditText) findViewById(R.id.firstNameEdtTxt);
        lastNameEdtTxt      = (EditText) findViewById(R.id.lastNameEdtTxt);
        emailEdtTxt         = (EditText) findViewById(R.id.emailEdtTxt);
        passwordEdtTxt      = (EditText) findViewById(R.id.passwordEdtTxt);
        confirmPasswordEdtTxt = (EditText) findViewById(R.id.confirmPasswordEdtTxt);
        phoneNumberEdtTxt   = (EditText) findViewById(R.id.phoneNumberEdtTxt);
        targetGoalEdtTxt    = (EditText) findViewById(R.id.targetGoalEdtTxt);
        trainerEmailIdEdtTxt= (EditText) findViewById(R.id.trainerEmailIdEdtTxt);
        backBtn             = (RelativeLayout) findViewById(R.id.backBtn);
        nextBtn             = (RelativeLayout) findViewById(R.id.nextBtn);
        finalSignUpLilay    = (LinearLayout) findViewById(R.id.finalSignUpLilay);
        initialSignUpLilay  = (LinearLayout) findViewById(R.id.initialSignUpLilay);
        signUpBtn           = (TextView) findViewById(R.id.signUpBtn);
        radioBtnGenderMale  = (RadioButton) findViewById(R.id.radioBtnGenderMale);
        radioBtnGenderFemale= (RadioButton) findViewById(R.id.radioBtnGenderFemale);
        radioBtnTrainerYes  = (RadioButton) findViewById(R.id.radioBtnTrainerYes);
        radioBtnTrainerNo   = (RadioButton) findViewById(R.id.radioBtnTrainerNo);
        trainerRadioGroup   = (RadioGroup) findViewById(R.id.trainerRadioGroup);
        trainerEmailInputLayout = (TextInputLayout) findViewById(R.id.trainerEmailInputLayout);
        confirmPasswordLayout = (TextInputLayout) findViewById(R.id.confirmPasswordLayout);
        passwordLayout      = (TextInputLayout) findViewById(R.id.passwordLayout);

        trainerRadioGroup.setOnCheckedChangeListener(this);
        backBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        targetGoalEdtTxt.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

        if (getIntent().hasExtra("fb_id")) {
            fbId = getIntent().getStringExtra("fb_id");
            fbUserName = getIntent().getStringExtra("fb_user_name");
            fbEmailId = getIntent().getStringExtra("fb_email");
            isFbLogin = true;
            passwordLayout.setVisibility(View.GONE);
            confirmPasswordLayout.setVisibility(View.GONE);

            try{
                String[] s = fbUserName.split(" ");
                firstNameEdtTxt.setText(s[0]);
                lastNameEdtTxt.setText(s[1]);
            }catch (Exception e){
                e.printStackTrace();
            }


            emailEdtTxt.setText(fbEmailId);
        }

    }

    @Override
    public void onBackPressed() {
        if (finalSignUp) {
            showInitialScreen();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_image: {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.
                            permission.READ_EXTERNAL_STORAGE) !=
                            PermissionChecker
                                    .PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(mContext, Manifest.
                            permission.CAMERA) !=
                            PermissionChecker.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]
                                {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 11);
                    } else {
                        getDialog();
                    }
                } else {
                    getDialog();
                }
            }
            break;
            case R.id.nextBtn:
                if (!finalSignUp) {
                    userFirstName = firstNameEdtTxt.getText().toString().trim();
                    userLastName = lastNameEdtTxt.getText().toString().trim();
                    userEmail = emailEdtTxt.getText().toString().trim();
                    userPassword = passwordEdtTxt.getText().toString().trim();
                    String confirmPassword = confirmPasswordEdtTxt.getText().toString().trim();
                    validateInitialSignUp(userFirstName, userLastName, userEmail, userPassword, confirmPassword);
                } /*else if (!finalSignUp && !isFbLogin) {

                }*/

                break;
            case R.id.backBtn:
                onBackPressed();
                break;
            case R.id.targetGoalEdtTxt:
                TargetGoalBottomSheet targetGoalBottomSheet = new TargetGoalBottomSheet();
                targetGoalBottomSheet.show(getSupportFragmentManager(), "dialog_delete");
                break;
            case R.id.signUpBtn:
                String phoneNumber = phoneNumberEdtTxt.getText().toString().trim();
                String targetGoal = targetGoalEdtTxt.getText().toString().trim();
                String trainerEmailId = trainerEmailIdEdtTxt.getText().toString().trim();

                validateFinalSignUp(phoneNumber, targetGoal, trainerEmailId);

                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDialog();
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getDialog() {
        {
            final Dialog dialog = new Dialog(mContext, R.style.DialogSlideAnim);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            getWindow().setGravity(Gravity.BOTTOM);

            Window window = dialog.getWindow();
            window.setGravity(Gravity.BOTTOM);


            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.check_option_dialog);
            dialog.setCancelable(false);
            TextView takeMedia = (TextView) dialog.findViewById(R.id.take_media);
            TextView SelectMedia = (TextView) dialog.findViewById(R.id.select_media);

            takeMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    EasyImage.openCamera(SignUpActivity.this, 0);
                }
            });
            SelectMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    EasyImage.openGallery(SignUpActivity.this, 0);
                }
            });

            Button mcancelBtn = (Button) dialog.findViewById(R.id.cancel);
            mcancelBtn.setTag(dialog);
            mcancelBtn.setText("Cancel");
            mcancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            if (((Activity) mContext).isFinishing()) {
                return;
            }

            dialog.show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {


            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                onPhotosReturned(imageFile);
            }


            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(mContext);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
    }

    /**/
    private void onPhotosReturned(File returnedPhotos) {
        this.imageFile = returnedPhotos;
        Picasso.with(mContext)
                .load(returnedPhotos)
                .fit()
                .centerCrop()
                .into(mAddProfileImageIV);
    }

    private void uploadImage(File returnedPhotos, String userID) {
        ArrayList<BasicNameValuePair> extraFormData = new ArrayList<>();
//        extraFormData.add(new BasicNameValuePair("fileName", returnedPhotos.getAbsolutePath().substring(returnedPhotos.getAbsolutePath().lastIndexOf("/") + 1)));
        extraFormData.add(new BasicNameValuePair("files", returnedPhotos.getAbsolutePath()));
//        extraFormData.add(new BasicNameValuePair("UserID", sessionManager.getUserDetail().getUserID() + ""));
////        extraFormData.add(new BasicNameValuePair("AddedFrom", "2"));
//        extraFormData.add(new BasicNameValuePair("FileType", "1"));
//        extraFormData.add(new BasicNameValuePair("status", "1"));

        String fileNAme = returnedPhotos.getAbsolutePath().substring(returnedPhotos.getAbsolutePath().lastIndexOf("/") + 1);

        HttpConnector httpConnector = new HttpConnector(mContext, null,
                null);
        httpConnector.setHttpResponseListener(new HttpConnector.HttpResponseListener() {
            @Override
            public void onResponse(int reqCode, int statusCode, String json) {

            }

            @Override
            public void onCancel(boolean canceled) {

            }
        });//&AddedFrom=1
        httpConnector.executeAsync("UploadProfilePhoto"+
                        "?fileName=" + fileNAme + "&UserID=" +userID + "&FileType=1&status=1"
                , 1,
                "multipost", false, null,
                extraFormData, HTTPRequest.URLTYPE_SERVICE,
                HTTPRequest.ADD_HEADER);
    }

    private void validateFinalSignUp(String phoneNumber, String targetGoal, String trainerEmailId) {
        if (phoneNumber.isEmpty()) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please enter your phone number.");
        } else if (!Utils.isValidMobile(phoneNumber)) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Invalid Phone Number.");
        } else if (targetGoal.isEmpty() || targetGoalIndex == 0) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please select your target goal.");
        } else if (!radioBtnTrainerYes.isChecked() && trainerEmailId.isEmpty()) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please enter the trainer email Id.");
        } else if (!radioBtnTrainerYes.isChecked() && !Utils.isValidEmail(trainerEmailId)) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Invalid Email.");
        } else {
            String gender = "male";
            if (radioBtnGenderFemale.isChecked()) {
                gender = "female";
            }

            callWsForSignUp(phoneNumber, targetGoalIndex, trainerEmailId, gender);
        }
    }


    /**
     * Validate Initial SignUp
     */
    private void validateInitialSignUp(String userFirstName, String userLastName, String userEmail,
                                       String userPassword, String confirmPassword) {
        if (userFirstName.isEmpty()) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please enter your first name.");
        } else if (userLastName.isEmpty()) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please enter your last name.");
        } else if (userEmail.isEmpty()) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please enter your email.");
        } else if (userPassword.isEmpty() && !isFbLogin) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please enter your password.");
        } else if (confirmPassword.isEmpty() && !isFbLogin) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please confirm your password.");
        } else if (!Utils.isValidEmail(userEmail) && !isFbLogin) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Invalid Email.");
        } else if (userPassword.length() < 6 && userPassword.length() > 10 && !isFbLogin) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Password length should be between 6-10 characters.");
        } else if (!userPassword.equalsIgnoreCase(confirmPassword) && !isFbLogin) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Confirm password is incorrect!");
        } else {
            showFinalScreen();
        }

    }


    private void showInitialScreen() {
        finalSignUp = false;
        initialSignUpLilay.setVisibility(View.VISIBLE);
        finalSignUpLilay.setVisibility(View.GONE);
    }

    private void showFinalScreen() {
        finalSignUp = true;
        initialSignUpLilay.setVisibility(View.GONE);
        finalSignUpLilay.setVisibility(View.VISIBLE);
    }

    /**
     * Call Ws For SignUp
     *
     * @param phoneNumber    User Phone Number
     * @param targetGoal
     * @param trainerEmailId
     * @param gender
     */
    private void callWsForSignUp(String phoneNumber, int targetGoal, String trainerEmailId, String gender) {

        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        Utils.showProgressDialog(this, false);
        int userType = UserType.CLIENT;
        if (radioBtnTrainerYes.isChecked()) {
            userType = UserType.TRAINER;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("GymId", "18");
        params.put("FirstName", userFirstName);
        params.put("LastName", userLastName);
        params.put("Email", userEmail);
        params.put("EmailTrainer", trainerEmailId);
        params.put("Password", userPassword);
        params.put("Gender", gender);
        params.put("GroupId", targetGoal);
        params.put("PhoneNo", phoneNumber);
        params.put("UserTypes", userType);
        params.put("DeviceType", DeviceType.ANDROID);
        params.put("DeviceToken", FirebaseInstanceId.getInstance().getToken());
        params.put("FbId", fbId);


        System.out.println("Param : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.INSERT_USER_DETAIL);
        System.out.println("url : "+url);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    System.out.println("RES: "+response.toString());
                    final String message = response.getString("message");
                    int responseCode = Integer.parseInt(response.getString("code"));
                    if (responseCode == 200) {
                        Gson gson = new Gson();
                        LoginSuccess loginSuccess = gson.fromJson(response.toString(), LoginSuccess.class);
                        if (imageFile != null) {
                            uploadImage(imageFile, loginSuccess.getResult().getUserID());
                        }
                        Utils.showAlertDialog(getResources().getString(R.string.app_name),
                                "You have successfully registered with Hope Squad, Hope Squad will contact you soon for activation of your account."
                                , SignUpActivity.this, true);
                    } else if (responseCode == 400) {
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setCancelable(false)
                                .setTitle(getResources().getString(R.string.app_name))
                                .setMessage(message)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        Gson gson = new Gson();
                        LoginSuccess loginSuccess = gson.fromJson(response.toString(), LoginSuccess.class);
                        Utils.showAlertMessage(SignUpActivity.this, getResources().getString(R.string.app_name),
                                loginSuccess.getMessage());
                    }
                    /*
                    if (code == 100) {
                        if (imageFile != null) {
                            uploadImage(imageFile, response.optString("UserID"));
                        }
                        Utils.showAlertDialog(getResources().getString(R.string.app_name),
                                "You have successfully registered with Hope Squad, Hope Squad will contact you soon for activation of your account."
                                , SignUpActivity.this, true);
                    } else {
                        Utils.showAlertMessage(SignUpActivity.this, getResources().getString(R.string.app_name),
                                message);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Utils.closeProgressDialog();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.closeProgressDialog();
                Utils.showAlertMessage(SignUpActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

    @Override
    public void onClickBottomSheetItem(int goalType) {
        switch (goalType) {
            case TargetGoal.BODY_FIT:
                targetGoalEdtTxt.setText("Body Fit");
                targetGoalIndex = TargetGoal.BODY_FIT;
                break;
            case TargetGoal.WEIGHT_GAIN:
                targetGoalEdtTxt.setText("Weight Gain");
                targetGoalIndex = TargetGoal.WEIGHT_GAIN;
                break;
            case TargetGoal.WEIGHT_LOSS:
                targetGoalEdtTxt.setText("Weight Loss");
                targetGoalIndex = TargetGoal.WEIGHT_LOSS;
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.radioBtnTrainerYes:
                if (radioBtnTrainerYes.isChecked()) {
                    trainerEmailIdEdtTxt.setText("");
                    trainerEmailInputLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.radioBtnTrainerNo:
                if (radioBtnTrainerNo.isChecked()) {
                    trainerEmailInputLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
