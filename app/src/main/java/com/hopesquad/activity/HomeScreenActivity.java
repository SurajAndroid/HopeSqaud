package com.hopesquad.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.hopesquad.API.APIClient;
import com.hopesquad.API.APIInterface;
import com.hopesquad.R;
import com.hopesquad.activity.chat.ChatActivity;
import com.hopesquad.activity.chat.ChatStatusActivity;
import com.hopesquad.activity.foodplan.FoodPlanActivity;
import com.hopesquad.activity.userimages.UserImagesActivity;
import com.hopesquad.activity.workout.WorkoutListActivity;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.LoginSuccess;
import com.hopesquad.models.UserDetail;
import com.hopesquad.models.UserInfo;
import com.hopesquad.models.UserStatus;
import com.hopesquad.models.UserType;
import com.hopesquad.onboarding.LoginActivity;
import com.hopesquad.onboarding.SignUpActivity;
import com.hopesquad.server.HTTPRequest;
import com.hopesquad.server.HttpConnector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreenActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private ImageView foodBtn;
    private ImageView messageBtn;
    private ImageView workoutBtn;
    private ImageView mediaBtn;
    private ImageView logoutBtn;
    private ImageView logoImg;
    private ImageView backgroundImg;
    private SessionManager sessionManager;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private TextView mUsernameTv, mHomeTv, mLogoutTv, mGalleryTv;
    private RelativeLayout header_layout;
    private ImageView mBackGround, mProfileImage;
//    private UserDetail userDetail;
    private UserInfo userInfo;
    private ProgressBar progressBar;
    private Context mContext;

    public static final int PERMISSION_REQUEST_CODE = 1111;
    private static final int REQUEST = 1337;
    public static int SELECT_FROM_GALLERY = 2;
    public static int CAMERA_PIC_REQUEST = 0;
    Uri mImageCaptureUri;
    Bitmap productImageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        userInfo   = new SessionManager(this).getUserInfo();

//        progressBar = (ProgressBar) findViewById(R.id.progress);

        mContext = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        getSupportActionBar().setDisplayUseLogoEnabled(false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);
        initNavigationHEader(headerLayout);

        sessionManager = new SessionManager(this);

        foodBtn = (ImageView) findViewById(R.id.foodBtn);
        messageBtn = (ImageView) findViewById(R.id.messageBtn);
        workoutBtn = (ImageView) findViewById(R.id.workoutBtn);
        mediaBtn = (ImageView) findViewById(R.id.mediaBtn);
        logoutBtn = (ImageView) findViewById(R.id.logoutBtn);
        logoImg = (ImageView) findViewById(R.id.logoImg);
        backgroundImg = (ImageView) findViewById(R.id.backgroundImg);

        foodBtn.setOnClickListener(this);
        messageBtn.setOnClickListener(this);
        workoutBtn.setOnClickListener(this);
        mediaBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

        fetchImages();

    }

    private void initNavigationHEader(View guillotineMenu) {
        mUsernameTv = (TextView) guillotineMenu.findViewById(R.id.username_tv);
        header_layout = (RelativeLayout) guillotineMenu.findViewById(R.id.header_layout);
        mHomeTv = (TextView) guillotineMenu.findViewById(R.id.home_tv);
        mLogoutTv = (TextView) guillotineMenu.findViewById(R.id.logout_tv);
        mGalleryTv = (TextView) guillotineMenu.findViewById(R.id.gallery_tv);
        mBackGround = (ImageView) guillotineMenu.findViewById(R.id.background);
        mProfileImage = (ImageView) guillotineMenu.findViewById(R.id.imageView);
        progressBar = (ProgressBar) guillotineMenu.findViewById(R.id.progress);

        mHomeTv.setOnClickListener(this);
        mLogoutTv.setOnClickListener(this);
        mGalleryTv.setOnClickListener(this);
        mProfileImage.setOnClickListener(this);
        setImage();
    }

    private void setImage() {
        mUsernameTv.setText(userInfo.FirstName +" "+userInfo.LastName);
        progressBar.setVisibility(View.VISIBLE);
        try {
            Glide.with(this)
                    .load(userInfo.ProfilePhoto)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .placeholder(R.mipmap.ic_profile_image).into(mProfileImage);
        } catch (Exception e) {
            e.printStackTrace();
            mProfileImage.setImageResource(R.mipmap.ic_profile_image);
        }
        try {
            if (userInfo.ProfilePhoto.length() > 0) {
                Glide.with(this)
                        .load(userInfo.ProfilePhoto)
                        .error(R.mipmap.ic_profile_image) // red cross (Android res)
                        .placeholder(R.mipmap.ic_profile_image) // category's image (SVG)
                        .into(mBackGround);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mBackGround.setImageResource(R.mipmap.ic_profile_image);
        }

    }


    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.imageView: {
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
            case R.id.foodBtn:
                intent = new Intent(this, FoodPlanActivity.class);
                startActivity(intent);
                break;
            case R.id.messageBtn:
                if (sessionManager.getUserInfo().getUserType().equals(""+UserType.CLIENT))
                    intent = new Intent(this, ChatStatusActivity.class);
                else
                    intent = new Intent(this, UserListActivity.class);
                startActivity(intent);
                break;
            case R.id.workoutBtn:
                intent = new Intent(this, WorkoutListActivity.class);
                startActivity(intent);
                break;
            case R.id.home_tv:
                drawer.closeDrawers();
                break;
            case R.id.mediaBtn:
            case R.id.gallery_tv:
                drawer.closeDrawers();
                intent = new Intent(this, UserImagesActivity.class);
                startActivity(intent);
                break;
            case R.id.logoutBtn:
            case R.id.logout_tv:
                drawer.closeDrawers();
                sessionManager.logoutUser();
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
                    cameraIntent();
                }
            });
            SelectMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    galleryIntent();
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

    private void galleryIntent() {
        Intent intent = new Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_FROM_GALLERY);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
            //  Uri cameraURI = data.getData();
            productImageBitmap = (Bitmap) data.getExtras().get("data");
            if (null != productImageBitmap) {
                mProfileImage.setImageBitmap(productImageBitmap);
                mBackGround.setImageBitmap(productImageBitmap);
                File userImageFile = getUserImageFile(productImageBitmap);
                if (null != userImageFile) {

                    upLoadProfile("18",sessionManager.getUserInfo().getUserID(),sessionManager.getUserInfo().getUserType(),userImageFile);
                }
            }
        } else if (requestCode == SELECT_FROM_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            Uri galleryURI = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(HomeScreenActivity.this.getContentResolver(), galleryURI);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != bitmap) {
                mProfileImage.setImageBitmap(bitmap);
                mBackGround.setImageBitmap(bitmap);
                File userImageFile = getUserImageFile(bitmap);
                if (null != userImageFile) {
                    upLoadProfile("18",sessionManager.getUserInfo().getUserID(),sessionManager.getUserInfo().getUserType(),userImageFile);
                }
            }
        }
    }


    private File getUserImageFile(Bitmap bitmap) {
        try {
            File f = new File(getCacheDir(), "images.png");
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return f;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public void upLoadProfile(String GymId, String UserId,String UserType, File Picture) {

        Utils.showProgressDialog(this, false);
        RequestBody gymId = RequestBody.create(MediaType.parse("text/plain"), GymId);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), UserId);
        RequestBody userType = RequestBody.create(MediaType.parse("text/plain"), UserType);
        RequestBody picture;
        MultipartBody.Part body;
        if (Picture != null) {
            picture = RequestBody.create(MediaType.parse("image/*"), Picture);
            body = MultipartBody.Part.createFormData("Picture", Picture.getName(), picture);
        } else {
            picture = RequestBody.create(MediaType.parse("image/*"), "");
            body = MultipartBody.Part.createFormData("Picture", "", picture);
        }

        APIInterface api = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> changePasswordResponseCall = api.AddProfileImage(gymId,userId,userType, body);
        changePasswordResponseCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        JSONObject  object = new JSONObject(response.body().string());
                        System.out.println("RES: "+object.getJSONObject("result").getString("ProfilePhoto"));
                        String fileName = object.getJSONObject("result").getString("ProfilePhoto");
                        userInfo.ProfilePhoto = fileName;
                        sessionManager.createUserLoginSession(userInfo);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
//                    onSuccessAddBloodDonor(response.body());
                } else {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response.errorBody().string());
                        if (object.getString("message").equals("Invalid Token")) {
                            new AlertDialog.Builder(HomeScreenActivity.this)
                                    .setCancelable(false)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage(object.getString("message"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        } else {
                            new AlertDialog.Builder(HomeScreenActivity.this)
                                    .setCancelable(false)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage(object.getString("message"))
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Utils.closeProgressDialog();
                if (t instanceof IOException) {
                    new AlertDialog.Builder(HomeScreenActivity.this)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Server down or no internet connection")
                            .show();
                } else {
                    new AlertDialog.Builder(HomeScreenActivity.this)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("oops something went wrong")
                            .show();
                }
            }
        });
    }



    private void fetchImages() {

        if (!Utils.isOnline(this)) {
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("GymId", "18");
        params.put("UserId", sessionManager.getUserInfo().getUserID());
        params.put("UserType",sessionManager.getUserInfo().getUserType());

        System.out.println("Param : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_USER_STATUS_NEW);

        System.out.println("url : "+url);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    int responseCode = Integer.parseInt(response.getString("code"));
                    if (responseCode == 200) {
                        Gson gson = new Gson();
                        LoginSuccess loginSuccess = gson.fromJson(response.toString(), LoginSuccess.class);
                        String backgorundPath = loginSuccess.getResult().BackgorundPath;
                        String logoPath = loginSuccess.getResult().getLogoPath();
                        int userStatus = Integer.parseInt(loginSuccess.getResult().getUserStatus());
                        if (userStatus != 1) {
                            sessionManager.logoutUser();
                        } else {
                            if (!backgorundPath.isEmpty()) {
                                Glide.with(HomeScreenActivity.this).load(backgorundPath).into(backgroundImg);
                            }
                            if (!logoPath.isEmpty()) {
                                Glide.with(HomeScreenActivity.this).load(logoPath).into(logoImg);
                            }
                        }
                    } else if (responseCode == 400) {
                        final String message = response.getString("message");
                        new AlertDialog.Builder(HomeScreenActivity.this)
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
                        Utils.showAlertMessage(HomeScreenActivity.this, getResources().getString(R.string.app_name),
                                loginSuccess.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
