/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/13/17 8:52 PM
 *
 */

package com.ihsanbal.knife.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.perf.metrics.AddTrace;
import com.ihsanbal.knife.BuildConfig;
import com.ihsanbal.knife.R;
import com.ihsanbal.knife.adapter.FloodAdapter;
import com.ihsanbal.knife.api.ApiClient;
import com.ihsanbal.knife.base.CompatBaseActivity;
import com.ihsanbal.knife.core.Constant;
import com.ihsanbal.knife.model.FloodCollection;
import com.ihsanbal.knife.model.FloodModel;
import com.ihsanbal.knife.model.TypeText;
import com.ihsanbal.knife.tools.AnimUtils;
import com.ihsanbal.knife.tools.TweetUtils;
import com.ihsanbal.knife.widget.KAppCompatButton;
import com.ihsanbal.knife.widget.KAutoCompleteEditText;
import com.ihsanbal.knife.widget.KTextView;
import com.twitter.Validator;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author ihsan on 09/04/2017.
 */
public class TweetActivity extends CompatBaseActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener, TextWatcher {

    private static final String TYPE = "tweet:type";

    private ApiClient.Api api;
    private FloodAdapter mAdapter;
    private ArrayList<FloodModel> list = new ArrayList<>();
    private BottomSheetBehavior<View> mBehavior;
    private InterstitialAd mInterstitialAd;
    private Long inReplyStatusId = null;
    private Type mType = Type.LIST;
    private User user;
    private ValueAnimator animFit;
    private ValueAnimator animDefault;
    private Long userRecipientsId;
    private TypeText typed;
    private String screenName;
    private int count;
    private Validator validator;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.button_tweet)
    KAppCompatButton mTweetButton;

    @BindView(R.id.tweet_count)
    KTextView mTweetCount;

    @BindView(R.id.auto_complete)
    KAutoCompleteEditText mTweetText;

    @BindView(R.id.bottom_sheet)
    View mBottomSheet;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.line_shadow)
    KTextView mLineShadow;

    public static void start(Context context, TypeText type) {
        Intent starter = new Intent(context, TweetActivity.class);
        starter.putExtra(TYPE, type);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.setNavigationOnClickListener(this);
        user = Paper.book().read(Constant.USER);
        if (getIntent().getExtras() != null) {
            typed = getIntent().getParcelableExtra(TYPE);
            switch (typed.getType()) {
                case SHARED:
                    callDetail(typed.getText());
                    break;
                case REPLY:
                    userRecipientsId = typed.getId();
                    screenName = typed.getScreenName();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
        initMenuItems();
        requestNewInterstitial();
    }

    @AddTrace(name = "interstitial")
    private void requestNewInterstitial() {
        logEvent("interstitial", "request", "start");
        AdRequest.Builder request = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            request.addTestDevice("7D376E3F676EDD395AB09C5FB3940F34");
            request.addTestDevice("0C0FC69324CCBEDFE5E27CCD8B6739B9");
            request.addTestDevice("F2EC702D97E2FC94DDABB269E40744B1");
        }
        mInterstitialAd.loadAd(request.build());
    }

    @AddTrace(name = "share")
    private void callDetail(String sharedText) {
        if (!TextUtils.isEmpty(sharedText)) {
            try {
                String[] splits = sharedText.split("/");
                screenName = splits[3];
                final String id = splits[5].split("\\?")[0];
                if (id != null) {
                    api.status(Long.parseLong(id))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(new Observer<Tweet>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Tweet value) {
                                    mTweetText.setText(value.text);
                                    typed.setType(TypeText.Type.REPLY);
                                    userRecipientsId = Long.valueOf(id);
                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onComplete() {
                                    calculateTweetFlood();
                                }
                            });
                }
            } catch (Exception ignored) {
                mTweetText.setText(sharedText);
                calculateTweetFlood();
            }
        } else {
            Snackbar.make(mBottomSheet, R.string.no_res, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void initMenuItems() {
        toolbar.inflateMenu(R.menu.tweet_menu);
        toolbar.setOnMenuItemClickListener(this);
        toggleItems(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void toggleItems(int newState) {
        switch (newState) {
            case BottomSheetBehavior.STATE_COLLAPSED:
                toggleMenuItems(false);
                break;
            case BottomSheetBehavior.STATE_EXPANDED:
                toggleMenuItems(true);
                break;
        }
    }

    private void toggleMenuItems(boolean visible) {
        toolbar.getMenu().findItem(R.id.menu_list).setVisible(visible);
        toolbar.getMenu().findItem(R.id.menu_number).setVisible(visible);
        toolbar.getMenu().findItem(R.id.menu_reply).setVisible(visible);
        if (typed.getType() == TypeText.Type.REPLY) {
            toolbar.getMenu().findItem(R.id.menu_list).setVisible(false);
            toolbar.getMenu().findItem(R.id.menu_number).setVisible(false);
            onMenuItemClick(toolbar.getMenu().findItem(R.id.menu_reply));
        }
    }

    private void init() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(BuildConfig.AD_UNIT_ID_INTERSTITIAL);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                logEvent("interstitial", "action", "closed");
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                calculateTweetFlood();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                logEvent("interstitial", "request", "failed");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                logEvent("interstitial", "request", "loaded");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                logEvent("interstitial", "action", "opened");
            }
        });
        validator = new Validator();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FloodAdapter(list);
        mRecyclerView.setAdapter(mAdapter);
        mTweetText.addTextChangedListener(this);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                    animateDefault();
                } else {
                    animateFit();
                }
            }
        });
        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                        toggleItems(newState);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        toggleItems(newState);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void animateFit() {
        if (animDefault != null && animDefault.isRunning())
            animDefault.cancel();
        if (animFit != null && animFit.isRunning()) {
            return;
        }
        animFit = AnimUtils.animateFit(mLineShadow, mRecyclerView.getMeasuredWidth());
        animFit.start();
    }

    private void animateDefault() {
        if (animFit != null && animFit.isRunning())
            animFit.cancel();
        if (animDefault != null && animDefault.isRunning()) {
            return;
        }
        animDefault = AnimUtils.animateDefault(mLineShadow, 0);
        animDefault.start();
    }

    @Override
    protected void getApi(ApiClient.Api api) {
        this.api = api;
    }

    @Override
    protected void getSession(TwitterSession session) {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_tweet;
    }

    @OnClick(R.id.button_tweet)
    void onClick() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            calculateTweetFlood();
        } else {
            callUpdateStatus(list);
        }
    }

    private void saveFlood() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") final View view = LayoutInflater.from(this).inflate(R.layout.layout_alert, null);
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        KAutoCompleteEditText title = (KAutoCompleteEditText) view.findViewById(R.id.title_edit);
                        if (!TextUtils.isEmpty(title.getText().toString())) {
                            calculateTweetFlood();
                            ArrayList<FloodCollection> collection = Paper.book().read(Constant.COLLECTION, new ArrayList<FloodCollection>());
                            FloodCollection newCollection = new FloodCollection(list);
                            newCollection.setTitle(title.getText().toString());
                            collection.add(newCollection);
                            Paper.book().write(Constant.COLLECTION, collection);
                            Toast.makeText(getApplicationContext(), R.string.save_message, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Snackbar.make(mRecyclerView, R.string.warn, Snackbar.LENGTH_LONG).show();
                        }
                    }
                })
                .setCancelable(true)
                .show();
    }

    @AddTrace(name = "flood")
    private void calculateTweetFlood() {
        logEvent(user.screenName, "flood", mType.name());
        switch (mType) {
            case LIST:
                inReplyStatusId = userRecipientsId;
                list.clear();
                list.addAll(TweetUtils.formatTweetList(mTweetText.getText().toString(), user, mType.ordinal(), screenName));
                break;
            case NUMBER:
                inReplyStatusId = userRecipientsId;
                list.clear();
                list.addAll(TweetUtils.formatTweetNumber(mTweetText.getText().toString(), user, mType.ordinal()));
                break;
            case REPLY:
                inReplyStatusId = userRecipientsId;
                list.clear();
                list.addAll(TweetUtils.formatTweetList(mTweetText.getText().toString(), user, mType.ordinal(), screenName));
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    @AddTrace(name = "update")
    private void callUpdateStatus(final ArrayList<FloodModel> items) {
        mTweetButton.setEnabled(false);
        count = 0;
        logEvent(user.screenName, "update", "start");
        Observable.fromIterable(items)
                .flatMap(new Function<FloodModel, ObservableSource<Tweet>>() {
                    @Override
                    public ObservableSource<Tweet> apply(FloodModel s) throws Exception {
                        return api.status(s.getTweet(), inReplyStatusId);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Tweet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Tweet value) {
                        if (mType == Type.REPLY) {
                            inReplyStatusId = Long.parseLong(value.idStr);
                        }
                        tickNextOnUIThread();
                    }

                    @Override
                    public void onError(Throwable e) {
                        toastOnIUThread(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        logEvent(user.screenName, "update", "complete : " + count);
                    }
                });
    }

    private void tickNextOnUIThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                count++;
                mTweetCount.setText(String.valueOf(count));
                mTweetCount.append("/");
                mTweetCount.append(String.valueOf(list.size()));
                mTweetCount.append("\t");
                mTweetCount.append(getString(R.string.complete));
                if (count == list.size()) {
                    Toast.makeText(getApplicationContext(), R.string.complete_flood, Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1500);
                }
            }
        });
    }

    private void toastOnIUThread(final String message) {
        logEvent(user.screenName, "update", message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTweetButton.setEnabled(true);
                Toast.makeText(TweetActivity.this, message != null ? message : getString(R.string.warn), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        checkClose();
    }

    private void checkClose() {
        if (mTweetText.getText().length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.warn)
                    .setMessage(R.string.warn_message)
                    .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNeutralButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveFlood();
                        }
                    })
                    .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            }
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mTweetCount.setText(String.valueOf(validator.getTweetLength(mTweetText.getText().toString())));
    }

    public enum Type {
        LIST, NUMBER, REPLY
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_list:
                mType = Type.LIST;
                toolbar.getMenu().findItem(R.id.menu_list).setIcon(R.drawable.ic_menu_list_select);
                toolbar.getMenu().findItem(R.id.menu_number).setIcon(R.drawable.ic_menu_number);
                toolbar.getMenu().findItem(R.id.menu_reply).setIcon(R.drawable.ic_menu_reply);
                break;
            case R.id.menu_number:
                mType = Type.NUMBER;
                toolbar.getMenu().findItem(R.id.menu_list).setIcon(R.drawable.ic_menu_list);
                toolbar.getMenu().findItem(R.id.menu_number).setIcon(R.drawable.ic_menu_number_select);
                toolbar.getMenu().findItem(R.id.menu_reply).setIcon(R.drawable.ic_menu_reply);
                break;
            case R.id.menu_reply:
                mType = Type.REPLY;
                toolbar.getMenu().findItem(R.id.menu_list).setIcon(R.drawable.ic_menu_list);
                toolbar.getMenu().findItem(R.id.menu_number).setIcon(R.drawable.ic_menu_number);
                toolbar.getMenu().findItem(R.id.menu_reply).setIcon(R.drawable.ic_menu_reply_select);
                break;
        }
        calculateTweetFlood();
        return false;
    }
}
