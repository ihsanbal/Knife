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
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.perf.metrics.AddTrace;
import com.ihsanbal.knife.BuildConfig;
import com.ihsanbal.knife.R;
import com.ihsanbal.knife.adapter.TimelineAdapter;
import com.ihsanbal.knife.api.ApiClient;
import com.ihsanbal.knife.base.CompatBaseActivity;
import com.ihsanbal.knife.core.CircularTransformation;
import com.ihsanbal.knife.core.Constant;
import com.ihsanbal.knife.core.EndlessScrollListener;
import com.ihsanbal.knife.injector.Injector;
import com.ihsanbal.knife.model.TypeText;
import com.ihsanbal.knife.tools.TweetUtils;
import com.ihsanbal.knife.tools.billing.IabBroadcastReceiver;
import com.ihsanbal.knife.tools.billing.IabHelper;
import com.ihsanbal.knife.tools.billing.IabResult;
import com.ihsanbal.knife.tools.billing.Inventory;
import com.ihsanbal.knife.tools.billing.Purchase;
import com.ihsanbal.knife.widget.KTextView;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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
public class DashboardActivity extends CompatBaseActivity implements SwipeRefreshLayout.OnRefreshListener, NavigationView.OnNavigationItemSelectedListener, IabBroadcastReceiver.IabBroadcastListener {

    private static final String SKU_PREMIUM = "premium";
    private String payload;
    private ArrayList<Tweet> mTweetList = new ArrayList<>();
    private TimelineAdapter mAdapter;
    private User mUser;
    private boolean isRewarded;
    private boolean isAdsShow;
    private IabHelper mHelper;
    private IabBroadcastReceiver mBroadcastReceiver;
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener;
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener;

    private KTextView mScreenName;
    private KTextView mDisplayName;
    private KTextView mDisplayPremium;
    private AppCompatImageView mCover;

    @Inject
    TwitterSession session;

    @Inject
    ApiClient.Api api;

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

    @BindView(R.id.version_name)
    KTextView mVersionName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance(this).inject(this);
        init();
        initTweetList();
        User user = Paper.book().read(Constant.USER);
        loadProfile(user);
        showProfile();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_dashboard;
    }

    @AddTrace(name = "profile")
    private void showProfile() {
        logEvent(session.getUserName(), "profile", "start");
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
                        logEvent(session.getUserName(), "error", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        logEvent(session.getUserName(), "profile", "complete");
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
                openProfile();
                break;
        }
    }

    private void openProfile() {
        if (mUser != null)
            TweetUtils.showProfile(this, mUser.screenName);
    }

    private void loadProfile(User data) {
        if (data != null) {
            mScreenName.setText("@");
            mScreenName.append(data.screenName);
            mDisplayName.setText(data.name);
            Picasso.with(DashboardActivity.this)
                    .load(data.profileImageUrl.replace("_normal", ""))
                    .fit()
                    .into(mCover);
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
        mHelper = new IabHelper(this, BuildConfig.RSA_KEY);
        mHelper.enableDebugLogging(BuildConfig.DEBUG);
        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
                boolean mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
                if (mIsPremium) {
                    mDisplayPremium.setVisibility(View.VISIBLE);
                }
            }
        };
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    mBroadcastReceiver = new IabBroadcastReceiver(DashboardActivity.this);
                    IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    registerReceiver(mBroadcastReceiver, broadcastFilter);
                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                Snackbar.make(mRecyclerView, info.getDeveloperPayload(), Snackbar.LENGTH_INDEFINITE).show();
            }
        };
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
        View headerView = mNavigationView.getHeaderView(0);
        mNavigationView.setNavigationItemSelectedListener(this);
        mScreenName = (KTextView) headerView.findViewById(R.id.screen_name);
        mDisplayName = (KTextView) headerView.findViewById(R.id.display_name);
        mDisplayPremium = (KTextView) headerView.findViewById(R.id.display_premium);
        mCover = (AppCompatImageView) headerView.findViewById(R.id.profile_cover);
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

    private boolean verifyDeveloperPayload(Purchase purchase) {
        payload = purchase.getDeveloperPayload();
        return payload != null;
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
        logEvent(session.getUserName(), "updates", "start");
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
                        logEvent(session.getUserName(), "updates", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        logEvent(session.getUserName(), "updates", "complete");
                    }
                });
    }

    @AddTrace(name = "paging-more")
    private void loadMore(long id) {
        logEvent(session.getUserName(), "paging-more", "start");
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
                        logEvent(session.getUserName(), "paging-more", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        logEvent(session.getUserName(), "paging-more", "complete");
                    }
                });
    }

    @AddTrace(name = "paging-news")
    private void loadNews(long id) {
        postRefresh(true);
        logEvent(session.getUserName(), "paging-news", "start");
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
                        logEvent(session.getUserName(), "paging-news", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        logEvent(session.getUserName(), "paging-news", "complete");
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawers();
        toggleDrawer(item);
        return false;
    }

    private void toggleDrawer(final MenuItem item) {
        logEvent(String.valueOf(item.getItemId()), "menu", item.getTitle().toString());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (item.getItemId()) {
                    case R.id.menu_profile:
                        openProfile();
                        break;
                    case R.id.menu_collection:
                        CollectionActivity.start(DashboardActivity.this);
                        break;
                    default:
                        try {
                            mHelper.flagEndAsync();
                            mHelper.launchPurchaseFlow(DashboardActivity.this, SKU_PREMIUM, 10001,
                                    mPurchaseFinishedListener, payload);
                        } catch (IabHelper.IabAsyncInProgressException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        }, 300);
    }

    @Override
    public void receivedBroadcast() {
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }
}
