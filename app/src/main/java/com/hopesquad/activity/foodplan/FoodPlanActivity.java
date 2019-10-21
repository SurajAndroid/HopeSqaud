package com.hopesquad.activity.foodplan;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hopesquad.R;
import com.hopesquad.activity.HomeScreenActivity;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.FoodPlanSuccess;
import com.hopesquad.models.LoginSuccess;
import com.hopesquad.models.PlanType;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import info.hoang8f.android.segmented.SegmentedGroup;

public class FoodPlanActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private SegmentedGroup segmentedGroup;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_plan);
        setToolBar();
        mWebview = (WebView) findViewById(R.id.pdfWebView);
        segmentedGroup = (SegmentedGroup) findViewById(R.id.segmentedGroup);
        segmentedGroup.setOnCheckedChangeListener(this);

        mWebview.getSettings().setJavaScriptEnabled(true);
        callWsToGetFoodPlans(PlanType.FOOD_PLAN);
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
            case R.id.foodPlanRadioBtn:
                callWsToGetFoodPlans(PlanType.FOOD_PLAN);
                break;
            case R.id.supplementalRadioBtn:
                callWsToGetFoodPlans(PlanType.SUPPLEMENTAL);
                break;
        }
    }


    private void callWsToGetFoodPlans(int planType) {
        if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }

        Utils.showProgressDialog(this, false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(20 * 1000);
        RequestParams params = new RequestParams();
        params.put("GymId", "18");
        params.put("UserId", new SessionManager(this).getUserInfo().getUserID());
        params.put("PlanType", planType);


        System.out.println("Param : "+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_FOOD_PLAN);
        System.out.println("url : "+url);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                System.out.println("RES: "+response.toString());
                try {
                    /*JSONArray jsonArray = response.getJSONArray("GetFoodPlanResult");
                    if (jsonArray.length() > 0) {
                        String pdf = jsonArray.getJSONObject(0).getString("FoodFileURL");
                        loadPdf(pdf);
                    } else {
                        Utils.showAlertDialog(getResources().getString(R.string.app_name),
                                "There is no food plan schedule for you.Please ask your trainer to schedule.",
                                FoodPlanActivity.this, false);
                    }*/
                    int responseCode = Integer.parseInt(response.getString("code"));
                    if (responseCode == 200) {
                        Gson gson = new Gson();
                        FoodPlanSuccess foodPlanSuccess = gson.fromJson(response.toString(), FoodPlanSuccess.class);
                        if(foodPlanSuccess.result.size()>0){
                            loadPdf(foodPlanSuccess.result.get(0).getFoodFileURL());
                        }
                    } else if (responseCode == 400) {
                        final String message = response.getString("message");
                        new AlertDialog.Builder(FoodPlanActivity.this)
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
                        Utils.showAlertMessage(FoodPlanActivity.this, getResources().getString(R.string.app_name),
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
                Utils.showAlertMessage(FoodPlanActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

    private void loadPdf(String PDF_FILE_URL) {
        mWebview.loadUrl("https://docs.google.com/gview?embedded=true&url=" + PDF_FILE_URL + "");
    }

}
