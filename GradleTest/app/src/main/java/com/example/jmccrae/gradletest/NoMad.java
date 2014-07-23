package com.example.jmccrae.gradletest;


import android.app.Application;
import android.content.Context;


/**
 * Created by jmccrae on 7/3/13.
 */
public class NoMad extends Application {

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
