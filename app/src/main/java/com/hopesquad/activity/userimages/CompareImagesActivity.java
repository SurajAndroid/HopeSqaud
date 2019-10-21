package com.hopesquad.activity.userimages;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hopesquad.API.APIClient;
import com.hopesquad.API.APIInterface;
import com.hopesquad.R;
import com.hopesquad.activity.chat.ChatStatusActivity;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.server.HTTPRequest;
import com.hopesquad.server.HttpConnector;
import com.squareup.picasso.Picasso;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompareImagesActivity extends AppCompatActivity implements View.OnClickListener {

    private SessionManager sessionManager;

    private ImageView mAddOneIv, mAddTwoIv;
    private TextView mCancelTv;
    private Button mDownloadBtn, mUploadBtn;
    private Context context = this;
    private ImageView mCloseOneIv, mCloseTwoIv, mAddOneBtn, mAddTwoBtn;
    private int mCode = 0;
    private File mFileOne, mFileTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_screen);
//        setToolBar();
        sessionManager = new SessionManager(this);
        init();
    }

    private void init() {
        mAddOneIv = (ImageView) findViewById(R.id.add_one_iv);
        mAddTwoIv = (ImageView) findViewById(R.id.add_two_iv);
        mAddOneBtn = (ImageView) findViewById(R.id.add_one_btn);
        mAddTwoBtn = (ImageView) findViewById(R.id.add_two_btn);
        mCloseOneIv = (ImageView) findViewById(R.id.close_one_iv);
        mCloseTwoIv = (ImageView) findViewById(R.id.close_two_iv);
        mDownloadBtn = (Button) findViewById(R.id.download_btn);
        mUploadBtn = (Button) findViewById(R.id.upload_btn);
        mCancelTv = (TextView) findViewById(R.id.cancel_tv);

        mAddOneBtn.setOnClickListener(this);
        mAddTwoBtn.setOnClickListener(this);
        mCloseOneIv.setOnClickListener(this);
        mCloseTwoIv.setOnClickListener(this);
        mDownloadBtn.setOnClickListener(this);
        mUploadBtn.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);

    }


    private void setToolBar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setNavigationIcon(R.drawable.ic_arrow_yellow);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_one_btn:
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
                        getDialog(111);
                    }
                } else {
                    getDialog(111);
                }
                break;

            case R.id.add_two_btn:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.
                            permission.READ_EXTERNAL_STORAGE) !=
                            PermissionChecker
                                    .PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.
                            permission.CAMERA) !=
                            PermissionChecker.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]
                                {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 12);
                    } else {
                        getDialog(112);
                    }
                } else {
                    getDialog(112);
                }
                break;

            case R.id.upload_btn: {
                if (mFileOne != null && mFileTwo != null) {
                    Bitmap bitmap = createSingleImageFromMultipleImages(getBitmapFromFile(mFileOne), getBitmapFromFile(mFileTwo));
                    String path = "";
                    if ((android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) && (Environment.isExternalStorageRemovable())) {
                        // yes SD-card is present
                        path = Environment.getExternalStorageDirectory().toString();
                    } else {
                        // Sorry
                        path = getApplicationContext().getFilesDir().getAbsolutePath();
                    }
                    File mainfile = new File(path + "/HopeSquad/");
                    if (!mainfile.exists()) {
                        mainfile.mkdirs();
                    }
                    File file = new File(mainfile, System.currentTimeMillis() + ".jpg");
                    try {///storage/emulated/0  /storage/emulated/0/1545208111200.jpg
                        FileOutputStream out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    upUserPic("18",sessionManager.getUserInfo().getUserID(),file);
                }
            }
            break;

            case R.id.download_btn: {
                if (mFileOne != null && mFileTwo != null) {
                    Bitmap bitmap = createSingleImageFromMultipleImages(getBitmapFromFile(mFileOne), getBitmapFromFile(mFileTwo));
                    int w = bitmap.getWidth();
                    SaveBitmapToExternalStorage(bitmap);
                }
            }
            break;
            case R.id.close_one_iv:
                mFileOne = null;
                mAddOneIv.setVisibility(View.GONE);
                mCloseOneIv.setVisibility(View.GONE);
                mAddOneIv.setImageResource(R.mipmap.plus);
                break;

            case R.id.close_two_iv:
                mFileTwo = null;
                mAddTwoIv.setVisibility(View.GONE);
                mCloseTwoIv.setVisibility(View.GONE);
                mAddTwoIv.setImageResource(R.mipmap.plus);
                break;

            case R.id.cancel_tv:
                onBackPressed();
                break;
        }
    }

    private void SaveBitmapToExternalStorage(Bitmap capturedBitmap) {
        String path = "";
        if ((android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) && (Environment.isExternalStorageRemovable())) {
            // yes SD-card is present
            path = Environment.getExternalStorageDirectory().toString();
        } else {
            // Sorry
            path = getApplicationContext().getFilesDir().getAbsolutePath();
        }

        OutputStream fOutputStream = null;
        File mainfile = new File(path + "/HopeSquad/");
        if (!mainfile.exists()) {
            mainfile.mkdirs();
        }//storage/emulated/0/HopeSquad
        File file = new File(path + "/HopeSquad/", System.currentTimeMillis() + ".jpg");
        try {
            fOutputStream = new FileOutputStream(file);
            capturedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOutputStream);
            fOutputStream.flush();
            fOutputStream.close();
            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Save Failed", Toast.LENGTH_SHORT).show();
            return;
        }
        Utils.showAlertMessage(context, getString(R.string.app_name), "Image Downloaded Successfully.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setResult(RESULT_FIRST_USER);
                finish();
            }
        });
    }

    private Bitmap getBitmapFromFile(File mFileOne) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(mFileOne.getAbsolutePath(), options);
        return bitmap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 11) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDialog(111);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 12) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDialog(112);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**/

    private void getDialog(final int code) {
        mCode = code;
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
                EasyImage.openCamera(CompareImagesActivity.this, 0);
            }
        });
        SelectMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                EasyImage.openGallery(CompareImagesActivity.this, 0);
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


    private Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage) {
        Bitmap result = Bitmap.createBitmap((firstImage.getWidth() > secondImage.getWidth()) ? firstImage.getWidth() : secondImage.getWidth(),
                firstImage.getHeight() + secondImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
//        canvas.drawRect(center_x - radius, center_y - radius, center_x + radius, center_y + radius, mPaint);
        canvas.drawColor(Color.WHITE);
        if ((firstImage.getWidth() > secondImage.getWidth())) {
            canvas.drawBitmap(firstImage, 0, 0, null);
            canvas.drawBitmap(secondImage, ((firstImage.getWidth() / 2) - (secondImage.getWidth() / 2)), firstImage.getHeight(), null);
        } else {
            canvas.drawBitmap(firstImage, ((secondImage.getWidth() / 2) - (firstImage.getWidth() / 2)), 0, null);
            canvas.drawBitmap(secondImage, 0, firstImage.getHeight(), null);
        }
        return result;
    }
// Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                e.printStackTrace();
            }

            ///storage/emulated/0/Android/data/com.hopesquad/cache/EasyImage/2dd35b70-b6c8-4b61-a4a1-299e4290ebea1652435504454231650.jpg
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                if (mCode == 111) {
                    Picasso.with(context)
                            .load(imageFile)
                            .into(mAddOneIv);
                    mCloseOneIv.setVisibility(View.VISIBLE);
                    mAddOneIv.setVisibility(View.VISIBLE);
                    mFileOne = imageFile;
                } else {
                    Picasso.with(context)
                            .load(imageFile)
                            .into(mAddTwoIv);
                    mCloseTwoIv.setVisibility(View.VISIBLE);
                    mAddTwoIv.setVisibility(View.VISIBLE);
                    mFileTwo = imageFile;
                }
            }


            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                //Cancel handling, you might wanna remove taken photo if it was canceled
                if (source == EasyImage.ImageSource.CAMERA) {
                    File photoFile = EasyImage.lastlyTakenButCanceledPhoto(context);
                    if (photoFile != null) photoFile.delete();
                }
            }
        });
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

                        Utils.showAlertMessage(context, getString(R.string.app_name), "Image Uploaded Successfully.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
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
                            new AlertDialog.Builder(CompareImagesActivity.this)
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
                            new AlertDialog.Builder(CompareImagesActivity.this)
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
                    new AlertDialog.Builder(CompareImagesActivity.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Server down or no internet connection")
                            .show();
                } else {
                    new AlertDialog.Builder(CompareImagesActivity.this)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("oops something went wrong")
                            .show();
                }
            }
        });
    }



    private void uploadImage(File returnedPhotos) {
        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        Utils.showProgressDialog(this, false);
        ArrayList<BasicNameValuePair> extraFormData = new ArrayList<>();
        extraFormData.add(new BasicNameValuePair("files", returnedPhotos.getAbsolutePath()));
        String fileNAme = returnedPhotos.getAbsolutePath().substring(returnedPhotos.getAbsolutePath().lastIndexOf("/") + 1);
        HttpConnector httpConnector = new HttpConnector(context, null,
                null);
        httpConnector.setHttpResponseListener(new HttpConnector.HttpResponseListener() {
            @Override
            public void onResponse(int reqCode, int statusCode, String json) {
                Utils.closeProgressDialog();
                Utils.showAlertMessage(context, getString(R.string.app_name), "Image Uploaded Successfully.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }

            @Override
            public void onCancel(boolean canceled) {
                Utils.closeProgressDialog();
            }
        });//&status=1
        httpConnector.executeAsync("UploadImagesVideosStatus" +
                        "?fileName=" + fileNAme + "&UserID=" + sessionManager.getUserDetail().getUserID() + "&AddedFrom=3&FileType=3", 1,
                "multipost", false, null,
                extraFormData, HTTPRequest.URLTYPE_SERVICE,
                HTTPRequest.ADD_HEADER);
    }
}
