package com.hopesquad.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.gson.Gson;
import com.hopesquad.R;
import com.hopesquad.activity.userimages.SlideshowDialogFragment;
import com.hopesquad.adapters.GalleryAdapter;
import com.hopesquad.adapters.HorizontalRecycleViewAdapter;
import com.hopesquad.adapters.UserListAdapter;
import com.hopesquad.helper.AppConstants;
import com.hopesquad.helper.SessionManager;
import com.hopesquad.helper.Utils;
import com.hopesquad.models.ImageStatusSuccess;
import com.hopesquad.models.LoginSuccess;
import com.hopesquad.models.UserListData;
import com.hopesquad.models.UserListResponse;
import com.hopesquad.models.UserMediaList;
import com.hopesquad.models.UserMediaResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

/**
 * Created by rohit on 1/10/17.
 */

public class UserListActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private RecyclerView mRecyclerView;
    private ArrayList<UserListData> userListData;
    private ArrayList<UserMediaList> userMediaListData;
    private RecyclerView horirecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        setToolBar();
        init();
        sessionManager = new SessionManager(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        getUserList();
        GetAllUsersMedia();

    }

    private void GetAllUsersMedia() {

        /*if (!Utils.isOnline(this)) {
            Utils.showAlertDialogForInternet(this);
            return;
        }
        Utils.showProgressDialog(this, false);*/

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(3, 10000);
        RequestParams params = new RequestParams();
        params.put("GymId", "18");
        params.put("UserID", sessionManager.getUserInfo().getUserID());
        params.put("AddedFrom", "2");
        params.put("recordStartIndex", "1");
        params.put("totalRecordFetch", "100");


        System.out.println("params : "+params);

        Log.e("","Param :"+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_ALL_USER_IMAGES);

//        String url = AppConstants.getMethodUrl(AppConstants.GET_ALL_USER_MEDIA);
        Log.e("","url :"+url);
        System.out.println("Url : "+url);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("RES : "+response.toString());
                userMediaListData = new ArrayList<>();
                try {

                    int totalRecordInDatabase = response.getJSONArray("result").length();
                    if (totalRecordInDatabase > 0) {
//                        JSONArray jsonArray = response.getJSONArray("result");
                        Gson gson = new Gson();
                        UserMediaResponse mediaResponse = gson.fromJson(response.toString(), UserMediaResponse.class);
                        userMediaListData.addAll(mediaResponse.getResult());
                        HorizontalRecycleViewAdapter adapter = new HorizontalRecycleViewAdapter(UserListActivity.this, userMediaListData);
                        horirecyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        horirecyclerView.setNestedScrollingEnabled(false);
                        horirecyclerView.setAdapter(adapter);

                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
//                    Utils.closeProgressDialog();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Utils.closeProgressDialog();
                Utils.showAlertMessage(UserListActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

    }

    private void init() {
        horirecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        horirecyclerView.setHasFixedSize(true);

        /*horirecyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), horirecyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", userMediaListData.get(position).UserMediaList);
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

    private void getUserList() {

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

        System.out.println("Param: "+params);

        String url = AppConstants.getMethodUrl(AppConstants.GET_ALL_USERS);
        System.out.println("url: "+url);

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("response   : "+response);
                try {
                    int totalRecordInDatabase = response.getJSONArray("result").length();
                    System.out.println("Size   : "+totalRecordInDatabase);
                    if (totalRecordInDatabase > 0) {
                        userListData = new ArrayList<>();
                        Gson gson = new Gson();
                        UserListResponse userListResponse = gson.fromJson(response.toString(), UserListResponse.class);
                        userListData.addAll(userListResponse.getResult());

                        UserListAdapter adapter = new UserListAdapter(userListData, UserListActivity.this);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(UserListActivity.this);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setAdapter(adapter);
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
                Utils.showAlertMessage(UserListActivity.this, getResources().getString(R.string.app_name),
                        "Something went wrong!");
            }
        });

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

}
