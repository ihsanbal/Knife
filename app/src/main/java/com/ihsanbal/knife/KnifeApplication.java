/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/13/17 8:38 PM
 *
 */

package com.ihsanbal.knife;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.ads.MobileAds;
import com.ihsanbal.knife.core.Constant;
import com.ihsanbal.knife.injector.AppComponent;
import com.ihsanbal.knife.injector.AppModule;
import com.ihsanbal.knife.injector.DaggerAppComponent;
import com.ihsanbal.knife.ui.SplashActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;

/**
 * @author ihsan on 03/04/2017.
 */
public class KnifeApplication extends MultiDexApplication {

    private AppComponent mDaggerComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Answers(), new Crashlytics());
        mDaggerComponent = DaggerAppComponent.builder().appModule(new AppModule()).build();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (e instanceof NullPointerException) {
                    Twitter.getSessionManager().clearActiveSession();
                    Paper.book().delete(Constant.USER);
                    SplashActivity.start(getApplicationContext());
                }
            }
        });
    }

    public AppComponent getDaggerComponent() {
        return mDaggerComponent;
    }
}
