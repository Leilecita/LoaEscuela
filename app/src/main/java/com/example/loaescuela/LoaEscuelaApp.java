package com.example.loaescuela;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class LoaEscuelaApp extends MultiDexApplication {

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }
}

