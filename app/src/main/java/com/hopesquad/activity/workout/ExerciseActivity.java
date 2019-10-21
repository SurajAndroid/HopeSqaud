package com.hopesquad.activity.workout;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hopesquad.R;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.UserExercise;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import info.hoang8f.android.segmented.SegmentedGroup;

public class ExerciseActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private ImageView exerciseImg;
    private ImageView muscleImg;
    private UserExercise userExercise;
    private Button completeBtn;
    private EditText commentEdtTxt;
    private TextView repsTxt, setsTxt, restTxt;
    private EditText startWeightEdtTxt;
    private EditText endWeightEdtTxt;
    private LinearLayout detailLayout;
    private SessionManager sessionManager;
    private RadioButton imageRadioBtn, muscleRadioBtn, detailRadioBtn;
    private SegmentedGroup segmentedGroup;
    private ProgressBar progressBar;
    private RelativeLayout videoLayout;
    private ImageView videoImg;
    private TextView noVideoAvailableTxt;
    private ImageView youTubeImg;
    private LinearLayout updatLayout;
    private TextView updateTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excercise);

        sessionManager = new SessionManager(this);
        setToolBar();
        segmentedGroup = (SegmentedGroup) findViewById(R.id.segmentedGroup);
        imageRadioBtn = (RadioButton) findViewById(R.id.imageRadioBtn);
        muscleRadioBtn = (RadioButton) findViewById(R.id.muscleRadioBtn);
        detailRadioBtn = (RadioButton) findViewById(R.id.detailRadioBtn);

        updatLayout = (LinearLayout) findViewById(R.id.updatLayout);
        updateTxt = (TextView) findViewById(R.id.updateTxt);

        exerciseImg = (ImageView) findViewById(R.id.exerciseImg);
        muscleImg = (ImageView) findViewById(R.id.muscleImg);
        videoImg = (ImageView) findViewById(R.id.videoImg);
        youTubeImg = (ImageView) findViewById(R.id.youTubeImg);
        completeBtn = (Button) findViewById(R.id.completeBtn);
        commentEdtTxt = (EditText) findViewById(R.id.commentEdtTxt);
        startWeightEdtTxt = (EditText) findViewById(R.id.startWeightEdtTxt);
        endWeightEdtTxt = (EditText) findViewById(R.id.endWeightEdtTxt);
        detailLayout = (LinearLayout) findViewById(R.id.detailLayout);
        videoLayout = (RelativeLayout) findViewById(R.id.videoLayout);
        noVideoAvailableTxt = (TextView) findViewById(R.id.noVideoAvailableTxt);
        repsTxt = (TextView) findViewById(R.id.repsTxt);
        setsTxt = (TextView) findViewById(R.id.setsTxt);
        restTxt = (TextView) findViewById(R.id.restTxt);
        progressBar = (ProgressBar) findViewById(R.id.progress);


        segmentedGroup.setOnCheckedChangeListener(this);
        completeBtn.setOnClickListener(this);
        videoLayout.setOnClickListener(this);
        updateTxt.setOnClickListener(this);
//   final String filename = path.substring(path.lastIndexOf("/") + 1);
        userExercise = (UserExercise) getIntent().getSerializableExtra("Exercise");
        if (getIntent().hasExtra("Exercise")) {

            /*if (userExercise.getExerciseStatus().equalsIgnoreCase("1")) {
                updatLayout.setVisibility(View.VISIBLE);
            } else {
                updatLayout.setVisibility(View.GONE);
            }*/

            if (Utils.isOnline(this)) {
                try {
                    Glide.with(this)
                            .load(userExercise.getExerciseImageURL())
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
                            .into(exerciseImg);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Glide.with(this)
                            .load(userExercise.getWorkOutImageURL())
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

                            .into(muscleImg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String youTubeID = getYouTubeId(userExercise.getLinkURL());
                    String videoUrl = "https://img.youtube.com/vi/" + youTubeID + "/0.jpg";

                    if (youTubeID != null) {
                        Glide.with(this)
                                .load(videoUrl)
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

                                .into(videoImg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                {
                    final String filename = userExercise.getExerciseImageURL().substring(userExercise.getExerciseImageURL().lastIndexOf("/") + 1);
                    String imageFileName = filename;
                    File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            + "/HopeSquad");
                    File imageFile = new File(storageDir, imageFileName);
                    Uri imageUri = Uri.fromFile(imageFile);
                    Glide.with(this)
                            .load(imageUri)
                            .into(exerciseImg);
                }
                {
                    final String filename = userExercise.getWorkOutImageURL().substring(userExercise.getWorkOutImageURL().lastIndexOf("/") + 1);
                    String imageFileName = filename;
                    File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            + "/HopeSquad");
                    File imageFile = new File(storageDir, imageFileName);
                    Uri imageUri = Uri.fromFile(imageFile);
                    Glide.with(this)
                            .load(imageUri)
                            .into(muscleImg);
                }
                {
                    String youTubeID = Utils.extractYTId(userExercise.getLinkURL());
                    String imageFileName = youTubeID;
                    File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            + "/HopeSquad");
                    File imageFile = new File(storageDir, imageFileName);
                    Uri imageUri = Uri.fromFile(imageFile);
                    Glide.with(this)
                            .load(imageUri)
                            .into(videoImg);
                }
            }
            try {
                repsTxt.setText(userExercise.getReps() + "");
                setsTxt.setText(userExercise.getSets() + "");
                if(!userExercise.getRestInSec().equals(""))
                restTxt.setText(userExercise.getRestInSec() + "");
                else
                    restTxt.setText("0" + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                startWeightEdtTxt.setText(userExercise.getStartWeight() + "");
                endWeightEdtTxt.setText(userExercise.getEndWeight() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (userExercise.getComment() != null && !userExercise.getComment().trim().isEmpty()) {
                    commentEdtTxt.setText(userExercise.getComment());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.imageRadioBtn:
                detailLayout.setVisibility(View.GONE);
                muscleImg.setVisibility(View.GONE);
                videoLayout.setVisibility(View.GONE);
                exerciseImg.setVisibility(View.VISIBLE);
                break;
            case R.id.muscleRadioBtn:
                detailLayout.setVisibility(View.GONE);
                exerciseImg.setVisibility(View.GONE);
                videoLayout.setVisibility(View.GONE);
                muscleImg.setVisibility(View.VISIBLE);
                break;
            case R.id.detailRadioBtn:
                exerciseImg.setVisibility(View.GONE);
                muscleImg.setVisibility(View.GONE);
                videoLayout.setVisibility(View.GONE);
                detailLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.videoRadioBtn:
                exerciseImg.setVisibility(View.GONE);
                muscleImg.setVisibility(View.GONE);
                detailLayout.setVisibility(View.GONE);
                videoLayout.setVisibility(View.VISIBLE);
                if (!userExercise.getLinkURL().isEmpty()) {
                    noVideoAvailableTxt.setVisibility(View.GONE);
                    youTubeImg.setVisibility(View.VISIBLE);
                } else {
                    noVideoAvailableTxt.setVisibility(View.VISIBLE);
                    youTubeImg.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Utils.hideSoftKeyboard(this);
        switch (view.getId()) {
            case R.id.updateTxt:
                validateCompleteExcercise();
                break;
            case R.id.completeBtn:
                /*if (userExercise.getExerciseStatus().equalsIgnoreCase("1")) {
                    finish();
                } else if (!detailLayout.isShown()) {
                    detailRadioBtn.setChecked(true);
                } else {
                    validateCompleteExcercise();
                }*/
                break;
            case R.id.videoLayout:
                if (userExercise.getLinkURL().isEmpty()) {
                    Utils.showAlertMessage(this, "Oops!", "This exercise doesn't have a video.");
                } else {
                    if (Utils.isOnline(this)) {
//                        String youTubeId = Utils.extractYTId(userExercise.getLinkURL());
                        String youTubeId = getYouTubeId(userExercise.getLinkURL());
                        Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youTubeId));
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=" + youTubeId));
                        try {
                            startActivity(applicationIntent);
                        } catch (ActivityNotFoundException ex) {
                            startActivity(browserIntent);
                        }
                    } else {
                        String youTubeId = Utils.extractYTId(userExercise.getLinkURL());
                        File newVideoPath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                , youTubeId + ".mp4");
                        String videoPath = newVideoPath.getAbsolutePath();
                        new File(videoPath).exists();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoPath));
                        intent.setDataAndType(Uri.parse(videoPath), "video/mp4");
                        startActivity(intent);
                    }

                    //Toast.makeText(this, userExercise.getLinkURL(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void validateCompleteExcercise() {
        String startWeight = startWeightEdtTxt.getText().toString().trim();
        String endWeight = endWeightEdtTxt.getText().toString().trim();

        if (startWeight.isEmpty()) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please enter the start weight.");
        } else if (endWeight.isEmpty()) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Please enter the end weight.");
        } else if (Integer.parseInt(startWeight) <= 0 || Integer.parseInt(endWeight) <= 0) {
            Utils.showAlertMessage(this, getResources().getString(R.string.app_name),
                    "Weights can't be 0.");
        } else {
            callWsForCompleteExercise(Integer.parseInt(startWeight), Integer.parseInt(endWeight));
        }

    }

    private String getYouTubeId (String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if(matcher.find()){
            return matcher.group();
        } else {
            return "error";
        }
    }

    private void callWsForCompleteExercise(int startWeight, int endWeight) {
        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        Utils.showProgressDialog(this, false);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("UserID", sessionManager.getUserDetail().getUserID());
        params.put("EndWeight", endWeight);
        params.put("StartWeight", startWeight);
        params.put("ExerciseID", userExercise.getExerciseID());

        String url = AppConstants.getMethodUrl(AppConstants.UPDATE_USER_EXCERCISE_INFO);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
//                    Toast.makeText(ExerciseActivity.this, response.toString() + "", Toast.LENGTH_SHORT).show();
//                    Log.d("Response : ", response.toString());
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Utils.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.closeProgressDialog();
                Utils.showAlertMessage(ExerciseActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

}
