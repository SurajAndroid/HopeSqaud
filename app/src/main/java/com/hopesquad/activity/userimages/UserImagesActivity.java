package com.hopesquad.activity.userimages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hopesquad.API.APIClient;
import com.hopesquad.API.APIInterface;
import com.hopesquad.R;
import com.hopesquad.activity.HomeScreenActivity;
import com.hopesquad.activity.chat.ChatStatusActivity;
import com.hopesquad.adapters.GalleryAdapter;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.LoginSuccess;
import com.hopesquad.models.UserImage;
import com.hopesquad.models.UserImageSuccess;
import com.hopesquad.models.UserImages;
import com.hopesquad.models.UserStatus;
import com.hopesquad.onboarding.LoginActivity;
import com.hopesquad.onboarding.SignUpActivity;
import com.hopesquad.server.HTTPRequest;
import com.hopesquad.server.HttpConnector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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

public class UserImagesActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager sessionManager;
    private ArrayList<UserImages> userImagesList;
    private RecyclerView mRecyclerView;
    private ImageView mAddIv;
    private TextView mCompareTv;
    public static int MyVersion = Build.VERSION.SDK_INT;
    public Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_images);
        setToolBar();
        sessionManager = new SessionManager(this);
        init();
//        setLayout();
        getAllUserImages();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setLayout() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        TypedValue tv = new TypedValue();
        int actionBarHeight = (int) getResources().getDimension(R.dimen.toolbar);
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        RelativeLayout head_rl = findViewById(R.id.head_rl);
        head_rl.setLayoutParams(new Toolbar.LayoutParams(width, actionBarHeight));
    }

    private void init() {

        mAddIv = (ImageView) findViewById(R.id.add_iv);
        mAddIv.setOnClickListener(this);
        mCompareTv = (TextView) findViewById(R.id.compare_tv);
        mCompareTv.setOnClickListener(this);
        mCompareTv.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        /*
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), mRecyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", userImagesList);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/
    }


    private void setToolBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_arrow_yellow);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.text_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.compares) {
            Intent intent = new Intent(this, CompareImagesActivity.class);
            startActivityForResult(intent, 101);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllUserImages() {

        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        Utils.showProgressDialog(this, false);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(3, 10000);
        RequestParams params = new RequestParams();

        params.put("GymId", "18");
        params.put("UserId", sessionManager.getUserInfo().getUserID());
        params.put("AddedFrom", "1");
        params.put("recordStartIndex", "1");
        params.put("totalRecordFetch", "100");
        System.out.println("Param : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_ALL_GALLERY_IMAGES);
        System.out.println("url : "+url);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("RES : "+response.toString());
                try {

                    int responseCode = Integer.parseInt(response.getString("code"));
                    if (responseCode == 200) {
                        Gson gson = new Gson();
                        UserImageSuccess userImageSuccess = gson.fromJson(response.toString(), UserImageSuccess.class);
                        if(userImageSuccess.result.size()>0){
                            showImages(userImageSuccess.result);
                        }else {
                            Utils.showAlertMessage(UserImagesActivity.this, getResources().getString(R.string.app_name),
                                    "No image found.");
                        }
                    } else if (responseCode == 400) {
                        final String message = response.getString("message");
                        new AlertDialog.Builder(UserImagesActivity.this)
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
                Utils.showAlertMessage(UserImagesActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

    private void showImages(final ArrayList<UserImage> userImagesList) {
        //UserImagesAdapter imagesAdapter = new UserImagesAdapter(this, userImagesList);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(layoutManager);

        GalleryAdapter imagesAdapter = new GalleryAdapter(this, userImagesList, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", userImagesList);
                bundle.putInt("position", position);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowImageDialogFragment newFragment = SlideshowImageDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }
        });

        mRecyclerView.setAdapter(imagesAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_iv:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.
                            permission.READ_EXTERNAL_STORAGE) !=
                            PermissionChecker
                                    .PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.
                            permission.CAMERA) !=
                            PermissionChecker.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]
                                {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 11);
                    } else {
                        getDialog();
                    }
                } else {

                    getDialog();
                }
                break;

            case R.id.compare_tv:
                Intent intent = new Intent(this, CompareImagesActivity.class);
                startActivityForResult(intent, 101);
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
            final Dialog dialog = new Dialog(context, R.style.DialogSlideAnim);
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
                    EasyImage.openCamera(UserImagesActivity.this, 0);
                }
            });
            SelectMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    EasyImage.openGallery(UserImagesActivity.this, 0);
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
            if (((Activity) context).isFinishing()) {
                return;
            }

            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            getAllUserImages();
        } else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    //Some error handling
                    e.printStackTrace();
                }

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
//                    uploadImage(imageFile);
                    upUserPic("18",sessionManager.getUserInfo().getUserID(),imageFile);
                }
///storage/emulated/0/Android/data/com.hopesquad/cache/EasyImage/2fc9f6d0-9517-4947-b6bf-6733e3dfc3e97407724114377817805.jpg
                @Override
                public void onCanceled(EasyImage.ImageSource source, int type) {
                    //Cancel handling, you might wanna remove taken photo if it was canceled
                    if (source == EasyImage.ImageSource.CAMERA) {

                    }
                }
            });
        }
    }


    /**
     *
     */
    private void uploadImage(File returnedPhotos) {
        ArrayList<BasicNameValuePair> extraFormData = new ArrayList<>();
        extraFormData.add(new BasicNameValuePair("files", returnedPhotos.getAbsolutePath()));
        String fileNAme = returnedPhotos.getAbsolutePath().substring(returnedPhotos.getAbsolutePath().lastIndexOf("/") + 1);
        HttpConnector httpConnector = new HttpConnector(context, null,
                null);
        httpConnector.setHttpResponseListener(new HttpConnector.HttpResponseListener() {
            @Override
            public void onResponse(int reqCode, int statusCode, String json) {
                getAllUserImages();
            }

            @Override
            public void onCancel(boolean canceled) {

            }
        });//&status=1
        httpConnector.executeAsync("UploadImagesVideosStatus" +
                        "?fileName=" + fileNAme + "&UserID=" + sessionManager.getUserDetail().getUserID() + "&AddedFrom=3&FileType=3", 1,
                "multipost", false, null,
                extraFormData, HTTPRequest.URLTYPE_SERVICE,
                HTTPRequest.ADD_HEADER);
    }


    public void upUserPic(String GymId, String UserId, File Picture) {

        Utils.showProgressDialog(this, false);
        RequestBody gymId = RequestBody.create(MediaType.parse("text/plain"), GymId);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), UserId);
        RequestBody picture;
        MultipartBody.Part body;
        if (Picture != null) {
            picture = RequestBody.create(MediaType.parse("image/*"), Picture);
            body = MultipartBody.Part.createFormData("FileName", Picture.getName(), picture);
        } else {
            picture = RequestBody.create(MediaType.parse("image/*"), "");
            body = MultipartBody.Part.createFormData("FileName", "", picture);
        }

        APIInterface api = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> changePasswordResponseCall = api.UploadUserGalleryImage(gymId,userId, body);
        changePasswordResponseCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        JSONObject  object = new JSONObject(response.body().string());
                        getAllUserImages();
                        System.out.println("RES: "+object.getJSONObject("result").getString("ProfilePhoto"));

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
                            new AlertDialog.Builder(UserImagesActivity.this)
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
                            new AlertDialog.Builder(UserImagesActivity.this)
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
                    new AlertDialog.Builder(UserImagesActivity.this)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Server down or no internet connection")
                            .show();
                } else {
                    new AlertDialog.Builder(UserImagesActivity.this)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("oops something went wrong")
                            .show();
                }
            }
        });
    }

}
