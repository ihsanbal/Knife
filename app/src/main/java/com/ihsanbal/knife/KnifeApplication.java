/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/13/17 8:38 PM
 *
 */

package com.ihsanbal.knife;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.google.android.gms.ads.MobileAds;
import com.ihsanbal.knife.api.ApiClient;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterSession;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;

/**
 * @author ihsan on 03/04/2017.
 */
public class KnifeApplication extends Application {

    private ApiClient.Api api;
    private TwitterSession session;

    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        MobileAds.initialize(this, BuildConfig.ADMOB_APP_ID);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Answers(), new Crashlytics());
        initApi();
    }

    public void initApi() {
        session = Twitter.getSessionManager().getActiveSession();
        if (session != null)
            api = new ApiClient(session, new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor
                            .Builder()
                            .loggable(BuildConfig.DEBUG)
                            .setLevel(Level.BASIC)
                            .log(Platform.INFO)
                            .request("Request")
                            .response("Response")
                            .build()).build()).getService();
    }

    public ApiClient.Api getApi() {
        return api;
    }

    public TwitterSession getSession() {
        return session;
    }
}
