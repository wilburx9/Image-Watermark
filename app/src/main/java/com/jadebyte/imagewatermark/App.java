package com.jadebyte.imagewatermark;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

/**
 * Created by Wilberforce on 11/25/17 at 12:59 PM.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
