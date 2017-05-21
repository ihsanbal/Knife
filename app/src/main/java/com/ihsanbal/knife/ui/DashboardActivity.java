/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/9/17 10:50 PM
 *
 */

package com.ihsanbal.knife.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ihsanbal.knife.R;
import com.ihsanbal.knife.adapter.TimelineAdapter;
import com.ihsanbal.knife.api.ApiClient;
import com.ihsanbal.knife.base.CompatBaseActivity;
import com.ihsanbal.knife.core.CircularTransformation;
import com.ihsanbal.knife.core.Constant;
import com.ihsanbal.knife.core.EndlessScrollListener;
import com.ihsanbal.knife.model.TypeText;
import com.ihsanbal.knife.tools.TweetUtils;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ihsan on 03/04/2017.
 */
public class DashboardActivity extends CompatBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<Tweet> mTweetList = new ArrayList<>();
    private TimelineAdapter mAdapter;
    private TwitterSession session;
    private ApiClient.Api api;
    private User mUser;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeLayout;

    @BindView(R.id.profile_image)
    AppCompatImageView circleImageView;

    @BindView(R.id.banner_view)
    AppCompatImageView backgroundImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initTweetList();
        User user = Paper.book().read(Constant.USER);
        loadProfile(user);
        showProfile();
    }

    @Override
    protected void getApi(ApiClient.Api api) {
        this.api = api;
    }

    @Override
    protected void getSession(TwitterSession session) {
        this.session = session;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_dashboard;
    }

    private void showProfile() {
        api.show(session.getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User result) {
                        Paper.book().write(Constant.USER, result);
                        mUser = result;
                        loadProfile(result);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick({R.id.action_tweet, R.id.profile_image})
    void onClicks(View view) {
        switch (view.getId()) {
            case R.id.action_tweet:
                TweetActivity.start(this, new TypeText());
                break;
            case R.id.profile_image:
                if (mUser != null)
                    TweetUtils.showProfile(this, mUser.screenName);
                break;
        }
    }

    private void loadProfile(User data) {
        if (data != null) {
            mToolbar.setTitle(data.screenName);
            Picasso.with(DashboardActivity.this)
                    .load(data.profileImageUrl)
                    .fit()
                    .transform(new CircularTransformation())
                    .into(circleImageView);
            if (data.profileBannerUrl != null)
                Picasso.with(DashboardActivity.this)
                        .load(data.profileBannerUrl)
                        .fit()
                        .into(backgroundImage);
        }
    }

    private void init() {
        setSupportActionBar(mToolbar);
        new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        }.syncState();
        mSwipeLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TimelineAdapter(mTweetList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new EndlessScrollListener(mRecyclerView.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMore(mTweetList.get(mTweetList.size() - 1).getId() - 1);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void postRefresh(final boolean show) {
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(show);
            }
        });
    }

    private void initTweetList() {
        api.tweets(session.getUserId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Tweet>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Tweet> result) {
                        mTweetList.clear();
                        mTweetList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadMore(long id) {
        api.tweets(session.getUserId(), id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Tweet>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Tweet> result) {
                        mTweetList.addAll(result);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadNews(long id) {
        postRefresh(true);
        api.news(session.getUserId(), id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Tweet>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Tweet> result) {
                        mTweetList.addAll(0, result);
                        mAdapter.notifyDataSetChanged();
                        postRefresh(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        postRefresh(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, DashboardActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onRefresh() {
        if (mTweetList.size() > 0) {
            loadNews(mTweetList.get(0).getId());
        }
    }

}
