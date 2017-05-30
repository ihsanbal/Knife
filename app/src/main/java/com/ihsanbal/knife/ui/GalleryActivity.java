/*
 * Created by ihsan on 5/27/17 11:49 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/27/17 11:49 PM
 *
 */

package com.ihsanbal.knife.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;

import com.ihsanbal.knife.R;
import com.ihsanbal.knife.adapter.GalleryAdapter;
import com.ihsanbal.knife.base.CompatBaseActivity;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author ihsan on 27/05/2017.
 */
public class GalleryActivity extends CompatBaseActivity {

    private static final String INDEX_KEY = "index:key";
    private static final String ENTITIES_KEY = "entities:key";

    @BindView(R.id.tw__view_pager)
    ViewPager mViewPager;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_gallery;
    }

    public static void start(Context context, int index, List<String> entities) {
        Intent starter = new Intent(context, GalleryActivity.class);
        starter.putExtra(INDEX_KEY, index);
        starter.putStringArrayListExtra(ENTITIES_KEY, new ArrayList<>(entities));
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int index = getIntent().getIntExtra(INDEX_KEY, 0);
        ArrayList<String> entities = getIntent().getStringArrayListExtra(ENTITIES_KEY);
        GalleryAdapter adapter = new GalleryAdapter(this, entities, getSwipeToDismissCallback());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(index);
        mViewPager.setPageMargin(10);
    }

    SwipeToDismissTouchListener.Callback getSwipeToDismissCallback() {
        return new SwipeToDismissTouchListener.Callback() {
            @Override
            public void onDismiss() {
                finish();
            }

            @Override
            public void onMove(float translationY) { /* intentionally blank */ }
        };
    }

}
