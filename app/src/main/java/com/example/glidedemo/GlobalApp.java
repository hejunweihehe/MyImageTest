package com.example.glidedemo;

import android.app.Application;

import timber.log.Timber;

/**
 * @author hejunwei
 * @date 2021/12/1
 */
public class GlobalApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
