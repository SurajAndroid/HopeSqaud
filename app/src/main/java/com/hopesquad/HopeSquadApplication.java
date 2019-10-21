package com.hopesquad;

import android.app.Application;
import android.content.Context;
import android.content.ServiceConnection;

import com.splunk.mint.Mint;

/**
 * Created by Husain on 07-03-2016.
 */
public class HopeSquadApplication extends Application {
    public static Context context;
    private static HopeSquadApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        HopeSquadApplication.context = getApplicationContext();
        Mint.initAndStartSession(this, "3627c228");
    }


    public static synchronized HopeSquadApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return HopeSquadApplication.context;
    }


    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    @Override
    public Context getBaseContext() {
        return super.getBaseContext();
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }
}
