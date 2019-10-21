package com.hopesquad.activity.chat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hopesquad.API.APIClient;
import com.hopesquad.API.APIInterface;
import com.hopesquad.R;
import com.hopesquad.adapters.ChatAdapter;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.ChatMessage;
import com.hopesquad.models.ChatMessageSuccess;
import com.hopesquad.models.UserType;
import com.hopesquad.server.HTTPRequest;
import com.hopesquad.server.HttpConnector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1001;
    private static final int OPEN_GALLERY = 1002;
    private RecyclerView mRecyclerView;
    private ImageView btn_file;
    private EditText messageEdtTxt;
    private Button btn_send;
    private ArrayList<ChatMessage> chatMessages;
    private SessionManager sessionManager;
    private ChatAdapter chatAdapter;
    private String otherUserID = "8";

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            getAllMessages(false);
        }
    };

    private Context mContext;

    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(mMessageReceiver, new IntentFilter("unique_name"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = this;
        setToolBar();
        sessionManager = new SessionManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        btn_file = (ImageView) findViewById(R.id.btn_file);
        messageEdtTxt = (EditText) findViewById(R.id.messageEdtTxt);
        btn_send = (Button) findViewById(R.id.btn_send);

        btn_send.setOnClickListener(this);
        btn_file.setOnClickListener(this);

        if (sessionManager.getUserInfo().getUserType().equals(""+UserType.CLIENT)) {
            otherUserID = sessionManager.getUserDetail().getTrainerUserID() + "";
            getAllMessages(false);
        } else {
            otherUserID = getIntent().getStringExtra("anotherUserID");
            getAllMessages(false);
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }


    @Override
    public void onClick(View view) {
        //  Utils.hideSoftKeyboard(this);
        switch (view.getId()) {
            case R.id.btn_send:
                String message = messageEdtTxt.getText().toString().trim();
                if (message.isEmpty()) {
                    Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                            "Please enter your message to send.");
                } else {
                    sendMessageToServer(message);
                    messageEdtTxt.setText("");
                }
                break;
            case R.id.btn_file:
                checkStoragePermission();
                break;
        }
    }


    /**
     * Check Storage Permission
     */
    private void checkStoragePermission() {
        // request permission to read external storage (explicit necessary since API Level 23)

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(ChatActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(ChatActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else {
            // copy patch to internal storage
            EasyImage.openChooserWithGallery(this, "Media Messages", OPEN_GALLERY);
        }
    }

    /**
     * Find out whether the permission was granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // if granted

                    // copy patch to internal storage
                    EasyImage.openChooserWithGallery(this, "Media Messages", OPEN_GALLERY);

                } else {
                    Toast.makeText(this, "App requires External Storage Permission to work!", Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }


            @Override
            public void onImagePicked(final File imageFile, EasyImage.ImageSource source, int type) {
//                uploadImage(imageFile);
                SendImges("18",sessionManager.getUserInfo().getUserID(),otherUserID,sessionManager.getUserInfo().getUserType(),imageFile);
            }

        });
    }

    private void uploadImage(File returnedPhotos) {

        Utils.showProgressDialog(this, false);
        ArrayList<BasicNameValuePair> extraFormData = new ArrayList<>();
        extraFormData.add(new BasicNameValuePair("files", returnedPhotos.getAbsolutePath()));


//        extraFormData.add(new BasicNameValuePair("fileName", returnedPhotos.getAbsolutePath().substring(returnedPhotos.getAbsolutePath().lastIndexOf("/") + 1)));
//        extraFormData.add(new BasicNameValuePair("UserID", sessionManager.getUserDetail().getUserID() + ""));
//        extraFormData.add(new BasicNameValuePair("SenderID", sessionManager.getUserDetail().getUserID() + ""));
//        extraFormData.add(new BasicNameValuePair("RecevierID", otherUserID));

        String fileNAme = returnedPhotos.getAbsolutePath().substring(returnedPhotos.getAbsolutePath().lastIndexOf("/") + 1);

        HttpConnector httpConnector = new HttpConnector(mContext, null,
                null);
        httpConnector.setHttpResponseListener(new HttpConnector.HttpResponseListener() {
            @Override
            public void onResponse(int reqCode, int statusCode, String json) {
//                Utils.closeProgressDialog();
                if (statusCode == 200) {
                    getAllMessages(true);
                }
            }

            @Override
            public void onCancel(boolean canceled) {
            }
        });

        httpConnector.executeAsync("UploadFile?fileName=" + fileNAme + "+&SenderID=" + sessionManager.getUserDetail().getUserID() + "&RecevierID=" + otherUserID, 1,
                "multipost", false, null,
                extraFormData, HTTPRequest.URLTYPE_SERVICE,
                HTTPRequest.ADD_HEADER);
    }


    public void SendImges(String GymId, String sernderId, String recieverId, String userType, File FileName) {
        Utils.showProgressDialog(this, false);
        RequestBody gymId = RequestBody.create(MediaType.parse("text/plain"), GymId);
        RequestBody sendId = RequestBody.create(MediaType.parse("text/plain"), sernderId);
        RequestBody reciverID = RequestBody.create(MediaType.parse("text/plain"), recieverId);
        RequestBody usertype = RequestBody.create(MediaType.parse("text/plain"), userType);

        RequestBody picture;
        MultipartBody.Part body;
        if (FileName != null) {
            picture = RequestBody.create(MediaType.parse("image/*"), FileName);
            body = MultipartBody.Part.createFormData("Picture", FileName.getName(), picture);
        } else {
            picture = RequestBody.create(MediaType.parse("image/*"), "");
            body = MultipartBody.Part.createFormData("Picture", "", picture);
        }

        APIInterface api = APIClient.getClient().create(APIInterface.class);
        Call<ResponseBody> changePasswordResponseCall = api.InsertImageMessageInfo(gymId,sendId,reciverID,usertype,body);
        changePasswordResponseCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.closeProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        JSONObject  object = new JSONObject(response.body().string());
                        if (object.getString("code").equals("200")) {
                            getAllMessages(true);
                        }
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
                            new AlertDialog.Builder(ChatActivity.this)
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
                            new AlertDialog.Builder(ChatActivity.this)
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
                    new AlertDialog.Builder(ChatActivity.this)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("Server down or no internet connection")
                            .show();
                } else {
                    new AlertDialog.Builder(ChatActivity.this)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.app_name))
                            .setMessage("oops something went wrong")
                            .show();
                }
            }
        });
    }


    private void sendMessageToServer(String message) {
        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }
        Utils.showProgressDialog(this, false);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("GymId", "18");
        params.put("SenderID", sessionManager.getUserInfo().getUserID());
        params.put("RecevierID", otherUserID);
        params.put("UserType", sessionManager.getUserInfo().getUserType());
        params.put("Message", message);

        System.out.println("params : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.INSERT_MESSAGE);

        System.out.println("url : "+url);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Utils.closeProgressDialog();
                System.out.println("RES : "+response);
                getAllMessages(true);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.showAlertMessage(ChatActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }


    private void getAllMessages(boolean isAlreadyLoading) {
        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        if(!isAlreadyLoading) {
            Utils.closeProgressDialog();
            Utils.showProgressDialog(this, false);
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(3, 10000);
        RequestParams params = new RequestParams();

        params.put("GymId", "18");
        params.put("SenderID", sessionManager.getUserInfo().getUserID());
        params.put("RecevierID", otherUserID);
        params.put("recordStartIndex", "1");
        params.put("totalRecordFetch", "100");

        System.out.println("params : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_ALL_USERS_MESSAGES);

        System.out.println("url : "+url);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("response : "+response.toString());
                Utils.closeProgressDialog();
                try {
                    int totalRecordInDatabase = response.getJSONArray("result").length();
                    if (totalRecordInDatabase > 0) {
                        Gson gson = new Gson();
                        ChatMessageSuccess success = gson.fromJson(response.toString(),ChatMessageSuccess.class);
                        showChatMessages(success.getResult());
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
                Utils.showAlertMessage(ChatActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

    private void showChatMessages(ArrayList<ChatMessage> chatMessages) {
        chatAdapter = new ChatAdapter(this, chatMessages,
                sessionManager.getUserInfo().getUserID());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(chatAdapter);
    }


    //Converting Selected Image to Base64Encode String
    private String getImageBase64(Uri selectedImage) {
        Bitmap myImg = null;
        try {
            myImg = decodeUri(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Must compress the Image to reduce image size to make upload easy
        myImg.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byte_arr = stream.toByteArray();
        // Encode Image to String
        return android.util.Base64.encodeToString(byte_arr, 0);
    }

    //Reducing Image Size of a selected Image
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 500;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

    }

    public String multipartRequest(String urlTo, Map<String, Integer> parmas, String filepath, String filefield, String fileMimeType) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        InputStream inputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        String result = "";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        String[] q = filepath.split("/");
        int idx = q.length - 1;

        try {
            File file = new File(filepath);
            FileInputStream fileInputStream = new FileInputStream(file);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

            outputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            // Upload POST Data
            Iterator<String> keys = parmas.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                Integer value = parmas.get(key);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(String.valueOf(value));
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


            if (200 != connection.getResponseCode()) {
                // throw new CustomException("Failed to upload code:" + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();

            result = this.convertStreamToString(inputStream);

            fileInputStream.close();
            inputStream.close();
            outputStream.flush();
            outputStream.close();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            //logger.error(e);
            //throw new CustomException(e);
        }
        return result;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
