/*
 * Created by ihsan on 5/14/17 6:39 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/9/17 10:50 PM
 *
 */

package com.ihsanbal.knife.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ihsanbal.knife.KnifeApplication;
import com.ihsanbal.knife.api.ApiClient;
import com.twitter.sdk.android.core.TwitterSession;

import butterknife.ButterKnife;

/**
 * @author ihsan on 14/04/2017.
 */

public abstract class CompatBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);
        ((KnifeApplication) getApplication()).initApi();
        getSession(((KnifeApplication) getApplication()).getSession());
        getApi(((KnifeApplication) getApplication()).getApi());
    }

    protected abstract void getApi(ApiClient.Api api);

    protected abstract void getSession(TwitterSession session);

    protected abstract @LayoutRes
    int getLayout();

}
