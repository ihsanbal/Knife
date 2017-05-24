/*
 * Created by ihsan on 5/23/17 8:56 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/23/17 8:56 PM
 *
 */

package com.ihsanbal.knife.injector;

import com.ihsanbal.knife.BuildConfig;
import com.ihsanbal.knife.api.ApiClient;
import com.ihsanbal.knife.core.Constant;
import com.ihsanbal.knife.model.FloodCollection;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.paperdb.Paper;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;

/**
 * @author ihsan on 23/05/2017.
 */

@Module
public class AppModule {

    @Provides
    @Singleton
    User provideUser() {
        return Paper.book().read(Constant.USER);
    }

    @Provides
    @Singleton
    ArrayList<FloodCollection> provideCollection() {
        return Paper.book().read(Constant.COLLECTION, new ArrayList<FloodCollection>());
    }

    @Provides
    @Singleton
    TwitterSession provideSession() {
        return Twitter.getSessionManager().getActiveSession();
    }

    @Provides
    @Singleton
    ApiClient.Api provideApi(TwitterSession session) {
        return new ApiClient(session, new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor
                        .Builder()
                        .loggable(BuildConfig.DEBUG)
                        .setLevel(Level.BASIC)
                        .log(Platform.INFO)
                        .request("Request")
                        .response("Response")
                        .build()).build()).getService();
    }

}
