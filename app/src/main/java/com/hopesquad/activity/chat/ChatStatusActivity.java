package com.hopesquad.activity.chat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.hopesquad.activity.HomeScreenActivity;
import com.hopesquad.activity.userimages.SlideshowDialogFragment;
import com.hopesquad.activity.userimages.UserImagesActivity;
import com.hopesquad.adapters.GalleryAdapter;
import com.hopesquad.adapters.StatusAdapter;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.EqualSpacingItemDecoration;
import com.hopesquad.helper.FileUploadUtil;
import com.hopesquad.helper.ImageUtils;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.ImageStatus;
import com.hopesquad.models.ImageStatusSuccess;
import com.hopesquad.models.LoginSuccess;
import com.hopesquad.models.UserDetail;
import com.hopesquad.models.UserImages;
import com.hopesquad.models.UserInfo;
import com.hopesquad.server.HTTPRequest;
import com.hopesquad.server.HttpConnector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import info.hoang8f.android.segmented.SegmentedGroup;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by pc on 23/01/2018.
 */

public class ChatStatusActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ArrayList<UserImages> userImagesList;
    private SessionManager sessionManager;
    private RadioButton chatRadioBtn, muscleRadioBtn, statucRadioBtn;
    private SegmentedGroup segmentedGroup;
    private LinearLayout mChatLayoutLl, mStatusLayoutLl;
    private RelativeLayout mChatBotRl;
    private TextView mChatBotTv;
    private ImageView mChatBotIv, mAddStatusIv;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private String filePath = "";
    private ProgressBar progressBar;
    StatusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mContext = this;
        sessionManager = new SessionManager(this);
        setToolBar();
        init();
    }

    private void init() {

        segmentedGroup = (SegmentedGroup) findViewById(R.id.segmentedGroup);
        chatRadioBtn = (RadioButton) findViewById(R.id.chatRadioBtn);
        statucRadioBtn = (RadioButton) findViewById(R.id.statusRadioBtn);
        /**/
        mChatLayoutLl = (LinearLayout) findViewById(R.id.chat_layoutLL);
        mChatBotRl = (RelativeLayout) findViewById(R.id.chat_bot_rl);
        mChatBotTv = (TextView) findViewById(R.id.chat_bot_tv);
        mChatBotIv = (ImageView) findViewById(R.id.bot_image_iv);
        /**/
        progressBar = (ProgressBar) findViewById(R.id.progress);
        mStatusLayoutLl = (LinearLayout) findViewById(R.id.status_layoutLL);
        mAddStatusIv = (ImageView) findViewById(R.id.userLogoImg);
        setImage(mAddStatusIv);

        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerGrid);

        segmentedGroup.setOnCheckedChangeListener(this);
        mChatBotRl.setOnClickListener(this);
        mChatBotIv.setOnClickListener(this);
        mAddStatusIv.setOnClickListener(this);

    }

    private void setImage(ImageView mAddStatusIv) {

        progressBar.setVisibility(View.VISIBLE);
        try {
            UserInfo userDetail = new SessionManager(this).getUserInfo();
            if (userDetail.ProfilePhoto.length() > 0 && userDetail.ProfilePhoto.contains("http")) {
                Glide.with(this)
                        .load(userDetail.ProfilePhoto)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("GLIDE", "" + e.toString() + "" + model + " ");
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                Log.d("GLIDE", "" + resource.toString() + "" + model + " ");
                                return false;
                            }
                        })
//                        .error(R.mipmap.ic_profile_image) // red cross (Android res)
                        //                        .placeholder(R.mipmap.picture) // category's image (SVG)
                        .into(mAddStatusIv);

            } else {
                mAddStatusIv.setImageResource(R.mipmap.picture);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecysclerView(final ArrayList<ImageStatus> result) {
        adapter = new StatusAdapter(getApplicationContext(), result, new StatusAdapter.ClickListener() {
            @Override
            public void onClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", result);
                bundle.putInt("position", position);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }
        });
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new EqualSpacingItemDecoration(8, EqualSpacingItemDecoration.HORIZONTAL));
        mRecyclerView.setAdapter(adapter);
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
    }

    /*Get all images*/
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
        params.put("AddedFrom ", "2");
        params.put("UserID", sessionManager.getUserInfo().getUserID());
        params.put("recordStartIndex", "1");
        params.put("totalRecordFetch", "100");


        System.out.println("Param : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_ALL_USER_IMAGES);
        System.out.println("url : "+url);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("RES : "+response.toString());
                try {

                    JSONArray jsonArray = response.getJSONArray("GetAllUserImagesResult");
                    if (jsonArray.length() > 0) {
                        Gson gson = new Gson();
                        userImagesList = new ArrayList<UserImages>(Arrays.asList(gson.fromJson(String.valueOf(jsonArray), UserImages[].class)));
//                        showImages(userImagesList);
                    } else {
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
                Utils.showAlertMessage(ChatStatusActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

   /* private void showImages(ArrayList<UserImages> userImagesList) {
        //UserImagesAdapter imagesAdapter = new UserImagesAdapter(this, userImagesList);
        GalleryAdapter imagesAdapter = new GalleryAdapter(this, userImagesList);
        mRecyclerView.setAdapter(imagesAdapter);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_bot_rl:
            case R.id.bot_image_iv: {
                Intent intent = new Intent(this, ChatActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.userLogoImg: {
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.chatRadioBtn:
                mStatusLayoutLl.setVisibility(View.GONE);
                mChatLayoutLl.setVisibility(View.VISIBLE);
                break;
            case R.id.statusRadioBtn:
                GetAllUserStatus();
                mChatLayoutLl.setVisibility(View.GONE);
                mStatusLayoutLl.setVisibility(View.VISIBLE);
                break;
        }
    }
    /*Dialog*/

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
                    EasyImage.openCamera(ChatStatusActivity.this, 0);
                }
            });
            SelectMedia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    EasyImage.openGallery(ChatStatusActivity.this, 0);
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
        Picasso.with(mContext)
                .load(returnedPhotos)
                .fit()
                .centerCrop()
                .into(mAddStatusIv);
//        uploadImage(returnedPhotos);
        uploadStatusImage("18",sessionManager.getUserInfo().getUserID(),"1","1",returnedPhotos);
    }

    private void uploadImage(File returnedPhotos) {
        ArrayList<BasicNameValuePair> extraFormData = new ArrayList<>();
        extraFormData.add(new BasicNameValuePair("files", returnedPhotos.getAbsolutePath()));
//        extraFormData.add(new BasicNameValuePair("fileName", returnedPhotos.getAbsolutePath().substring(returnedPhotos.getAbsolutePath().lastIndexOf("/") + 1)));
//        extraFormData.add(new BasicNameValuePair("UserID", sessionManager.getUserDetail().getUserID() + ""));
//        extraFormData.add(new BasicNameValuePair("AddedFrom", "1"));
//        extraFormData.add(new BasicNameValuePair("FileType", "1"));
//        extraFormData.add(new BasicNameValuePair("status", "1"));

        String fileNAme = returnedPhotos.getAbsolutePath().substring(returnedPhotos.getAbsolutePath().lastIndexOf("/") + 1);
        HttpConnector httpConnector = new HttpConnector(mContext, null,
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
                        "?fileName=" + fileNAme + "&UserID=" + sessionManager.getUserDetail().getUserID() + "&AddedFrom=2&FileType=1", 1,
                "multipost", false, null,
                extraFormData, HTTPRequest.URLTYPE_SERVICE,
                HTTPRequest.ADD_HEADER);
    }



    public void uploadStatusImage(String GymId, String UserID, String FileType, String Status, File FileName) {
        Utils.showProgressDialog(this, false);
        RequestBody gymId = RequestBody.create(MediaType.parse("text/plain"), GymId);
        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), UserID);
        RequestBody fileType = RequestBody.create(MediaType.parse("text/plain"), FileType);
        RequestBody status = RequestBody.create(MediaType.parse("text/plain"), Status);

        RequestBody picture;
        MultipartBody.Part body;
        if (FileName != null) {
            picture = RequestBody.create(MediaType.parse("image/*"), FileName);
            body = MultipartBody.Part.createFormData("FileName", FileName.getName(), picture);
        } else {
            picture = RequestBody.create(MediaType.parse("image/*"), "");
            body = MultipartBody.Part.createFormData("FileName", "", picture);
        }

        APIInterface api = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> changePasswordResponseCall = api.UpLoadStatusImage(gymId,userId,fileType,status,body);
        changePasswordResponseCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        JSONObject  object = new JSONObject(response.body().string());
                        GetAllUserStatus();
                        new AlertDialog.Builder(ChatStatusActivity.this)
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
                            new AlertDialog.Builder(ChatStatusActivity.this)
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
                            new AlertDialog.Builder(ChatStatusActivity.this)
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
                    new AlertDialog.Builder(ChatStatusActivity.this)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Server down or no internet connection")
                            .show();
                } else {
                    new AlertDialog.Builder(ChatStatusActivity.this)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("oops something went wrong")
                            .show();
                }
            }
        });
    }

    /*
    *
    * */
    private void GetAllUserStatus() {

        Utils.showProgressDialog(this, false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("GymId", "18");
        params.put("UserID", sessionManager.getUserInfo().getUserID());
        params.put("AddedFrom", "2");
        params.put("recordStartIndex", "1");
        params.put("totalRecordFetch","100");


        System.out.println("Param : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_ALL_USER_STATUS);

        System.out.println("url : "+url);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("Res : "+response);

                Utils.closeProgressDialog();
                try {
                    int responseCode = Integer.parseInt(response.getString("code"));
                    if (responseCode == 200) {
                        Gson gson = new Gson();
                        try{
                            ImageStatusSuccess loginSuccess = gson.fromJson(response.toString(), ImageStatusSuccess.class);
                            if(loginSuccess.result.size()>0)
                                setRecysclerView(loginSuccess.result);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else if (responseCode == 400) {
                        final String message = response.getString("message");
                        new AlertDialog.Builder(ChatStatusActivity.this)
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
                        Utils.closeProgressDialog();
                        Gson gson = new Gson();
                        LoginSuccess loginSuccess = gson.fromJson(response.toString(), LoginSuccess.class);
                        Utils.showAlertMessage(ChatStatusActivity.this, getResources().getString(R.string.app_name),
                                loginSuccess.getMessage());
                    }
                } catch (Exception e) {
                    Utils.closeProgressDialog();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.closeProgressDialog();
            }
        });

    }

}
