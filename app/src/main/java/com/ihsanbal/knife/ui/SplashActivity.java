/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/1/17 1:14 PM
 *
 */

package com.ihsanbal.knife.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ihsanbal.knife.R;
import com.ihsanbal.knife.core.SVG;
import com.ihsanbal.knife.model.TypeText;
import com.ihsanbal.knife.tools.AnimUtils;
import com.jrummyapps.android.widget.AnimatedSvgView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements AnimatedSvgView.OnStateChangeListener {

    @BindView(R.id.twitter_login_button)
    TwitterLoginButton loginButton;

    @BindView(R.id.animated_svg_view)
    AnimatedSvgView mSvgView;

    public static void start(Context context) {
        Intent starter = new Intent(context, SplashActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initTwitterLogin();
        initSVG();
    }

    private void checkSession() {
        if (Twitter.getSessionManager().getActiveSession() != null) {
            checkActionSendData();
        } else {
            AnimUtils.animateAlpha(loginButton);
        }
    }

    private void initSVG() {
        SVG svg = SVG.values()[0];
        mSvgView.setGlyphStrings(svg.glyphs);
        mSvgView.setFillColors(svg.colors);
        mSvgView.setViewportSize(svg.width, svg.height);
        mSvgView.setTraceResidueColor(0xFFFFFFFF);
        mSvgView.setTraceColors(svg.colors);
        mSvgView.rebuildGlyphData();
        mSvgView.setOnStateChangeListener(this);
        mSvgView.start();
    }

    private void initTwitterLogin() {
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                checkActionSendData();
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
    }

    private void checkActionSendData() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intent);
                }
            } else {
                startDashboardActivity();
            }
        } else {
            startDashboardActivity();
        }
    }

    private void startDashboardActivity() {
        DashboardActivity.start(SplashActivity.this);
        finish();
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            startTweetActivity(sharedText);
        } else {
            startDashboardActivity();
        }
    }

    private void startTweetActivity(String sharedText) {
        TypeText typeText = new TypeText();
        typeText.setText(sharedText);
        typeText.setType(TypeText.Type.SHARED);
        TweetActivity.start(this, typeText);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStateChange(int state) {
        if (state == AnimatedSvgView.STATE_FINISHED) {
            checkSession();
        }
    }
}
