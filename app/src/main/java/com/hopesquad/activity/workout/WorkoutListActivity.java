package com.hopesquad.activity.workout;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;
import com.google.gson.Gson;
import com.hopesquad.R;
import com.hopesquad.adapters.WorkoutListAdapter;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.PermissionUtils;
import com.hopesquad.helper.PreferencesKey;
import com.hopesquad.helper.SaveSharedPref;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.Workouts;
import com.hopesquad.models.WorkoutsSuccess;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YtFile;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutListActivity extends AppCompatActivity implements ExpandableListView.OnChildClickListener {

    private SessionManager sessionManager;
    private ExpandableListView workOutListView;
    private List<Workouts> workoutsList;
    private WorkoutListAdapter listAdapter;
    private static final String YOUTUBE_ID = "ea4-5mrpGfE";
    private final YouTubeExtractor mExtractor = YouTubeExtractor.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);
        PermissionUtils.askForStoragePermissionIfNecessary(this);
        setToolBar();
        sessionManager = new SessionManager(this);
        workOutListView =   findViewById(R.id.workOutListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        callWsToGetWorkoutData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workout_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.actionDownload) {
            Toast.makeText(this, "Download", Toast.LENGTH_SHORT).show();
            if (workoutsList != null)
                startDownloading();
        }
        return super.onOptionsItemSelected(item);
    }

    private void startDownloading() {
        for (int i = 0; i < workoutsList.size(); i++) {
            for (int j = 0; j < workoutsList.get(i).getUserExerciseList().size(); j++) {
                getfileStore(workoutsList.get(i).getUserExerciseList().get(j).getWorkOutImageURL());
                getfileStore(workoutsList.get(i).getUserExerciseList().get(j).getExerciseImageURL());
                String youTubeID = Utils.extractYTId(workoutsList.get(i).getUserExerciseList().get(j).getLinkURL());
                String videoUrl = "https://img.youtube.com/vi/" + youTubeID + "/0.jpg";
                getfileStore(videoUrl, youTubeID);
                File newVideoPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), youTubeID + ".mp4");
                if (!newVideoPath.exists()) {
                    ExtractYoutubeVideo(workoutsList, i, j);
                }
            }
        }
    }

    private void ExtractYoutubeVideo(List<Workouts> workoutsList, int i, int j) {
        final String youtubeId = Utils.extractYTId(workoutsList.get(i).getUserExerciseList().get(j).getLinkURL());
        new at.huber.youtubeExtractor.YouTubeExtractor(this) {

            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles == null) {
                    // Something went wrong we got no urls. Always check this.
                    finish();
                    return;
                }
                // Iterate over itags
                for (int i = 0, itag; i < ytFiles.size(); i++) {
                    itag = ytFiles.keyAt(i);
                    // ytFile represents one file with its url and meta data
                    YtFile ytFile = ytFiles.get(itag);

                    // Just add videos in a decent format => height -1 = audio
                    if (ytFile.getFormat().getHeight() == 720 || ytFile.getFormat().getHeight() >= 480) {
                        storeFile(vMeta.getTitle(), ytFile, youtubeId);
                        return;
                    }
                }
            }
        }
                .extract("https://www.youtube.com/watch?v=" + youtubeId, true, false);

    }

    private void getfileStore(String path) {
        final String filename = path.substring(path.lastIndexOf("/") + 1);
        final File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/HopeSquad");
        final File imageFile = new File(storageDir, filename);
        if (!imageFile.exists()) {

            if (filename.contains("gif")) {
                Glide.with(this)
                        .load(path)
                        .downloadOnly(new SimpleTarget<File>() {
                            @Override
                            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                                moveFile(resource.getAbsolutePath(), filename, storageDir.getAbsolutePath());
                            }
                        });

            } else {
                Glide.with(this)
                        .load(path)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                saveImage(resource, filename);
                            }
                        });
            }
        }
    }

    private void moveFile(String inputPath, String inputFile, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath + File.separator + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath).delete();


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void getfileStore(String path, final String filename) {
//        final String filename = path.substring(path.lastIndexOf("/") + 1);
        Glide.with(this)
                .load(path)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        saveImage(resource, filename);
                    }
                });
    }

    private String saveImage(Bitmap image, String filename) {
        String savedImagePath = null;

        String imageFileName = filename;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/HopeSquad");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
//            galleryAddPic(savedImagePath);
            Toast.makeText(this, "IMAGE SAVED", Toast.LENGTH_LONG).show();
        }
        return savedImagePath;
    }

    private void storeFile(final String videoTitle, final YtFile ytfile, String youtubeId) {
        String filename;
        if (videoTitle.length() > 55) {
            filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
        } else {
            filename = videoTitle + "." + ytfile.getFormat().getExt();
        }
        filename = filename.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");
        File newVideoPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                , youtubeId + ".mp4");
        if (!newVideoPath.exists()) {
            downloadFromUrl(ytfile.getUrl(), youtubeId + "." + ytfile.getFormat().getExt(), youtubeId);
        }

    }

    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadTitle);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


    }


    private void onError(Throwable t) {
        t.printStackTrace();
        Toast.makeText(WorkoutListActivity.this, "It failed to extract. So sad", Toast.LENGTH_SHORT).show();
    }


    private void bindVideoResult(YouTubeExtractionResult result) {

//        Here you can get download url link

        Log.d("OnSuccess", "Got a result with the best url: " + result.getBestAvailableQualityVideoUri());

        Toast.makeText(this, "result : " + result.getSd360VideoUri(), Toast.LENGTH_SHORT).show();
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


    private void callWsToGetWorkoutData() {
        if (!Utils.isOnline(this)) {
//            Utils.showAlertDialogForInternet(this);
            String response = SaveSharedPref.getData(WorkoutListActivity.this, PreferencesKey.WORKOUT_LIST);
            try {
                ExtractDataFromJSon(new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        Utils.showProgressDialog(this, false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("UserId", sessionManager.getUserInfo().getUserID());
        params.put("GymId", "18");
        params.put("recordStartIndex", "1");
        params.put("totalRecordFetch", "10");
        System.out.println("params : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_USERS_WORKOUT);
        System.out.println("url : "+url);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("Res : "+response);
                ExtractDataFromJSon(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.closeProgressDialog();
                Utils.showAlertMessage(WorkoutListActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

    private void ExtractDataFromJSon(JSONObject response) {
        try {
            SaveSharedPref.saveData(WorkoutListActivity.this, PreferencesKey.WORKOUT_LIST, response.toString());
            int totalRecordInDatabase = response.getJSONArray("result").length();
            if (totalRecordInDatabase > 0) {
                JSONArray jsonArray = response.getJSONArray("result");
                Gson gson = new Gson();
                WorkoutsSuccess success = gson.fromJson(response.toString(),WorkoutsSuccess.class);

//                workoutsList = Arrays.asList(gson.fromJson(String.valueOf(jsonArray.get(0)), Workouts[].class));
//                        if (workoutsList.get(0).getWorkOutName().equalsIgnoreCase("Day 1 - ")) {
//                            workoutsList.remove();
//                        }
                workoutsList = new ArrayList<>();
                workoutsList.addAll(success.getResult());
                showExpandableList(success.getResult());
            } else {
                Utils.showAlertDialog(getResources().getString(R.string.app_name),
                        "There is no exercise schedule for you.Please ask your trainer to schedule.",
                        WorkoutListActivity.this, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeProgressDialog();
        }
    }

    private void showExpandableList(List<Workouts> workoutsList) {
        listAdapter = new WorkoutListAdapter(this, workoutsList);
        workOutListView.setAdapter(listAdapter);
        workOutListView.setOnChildClickListener(this);
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view,
                                int groupPosition, int childPosition, long id) {
        Intent intent = new Intent(this, ExerciseActivity.class);
        intent.putExtra("Exercise", workoutsList.get(groupPosition).getUserExerciseList().get(childPosition));
        startActivity(intent);
        return false;
    }
}
