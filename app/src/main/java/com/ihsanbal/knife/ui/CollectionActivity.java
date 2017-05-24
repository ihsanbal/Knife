/*
 * Created by ihsan on 5/23/17 8:52 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/23/17 8:52 PM
 *
 */

package com.ihsanbal.knife.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ihsanbal.knife.R;
import com.ihsanbal.knife.adapter.CollectionAdapter;
import com.ihsanbal.knife.api.ApiClient;
import com.ihsanbal.knife.base.CompatBaseActivity;
import com.ihsanbal.knife.injector.Injector;
import com.ihsanbal.knife.model.FloodCollection;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * @author ihsan on 23/05/2017.
 */
public class CollectionActivity extends CompatBaseActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private CollectionAdapter mAdapter;

    @Inject
    ApiClient.Api api;

    @Inject
    ArrayList<FloodCollection> collections;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Override
    protected int getLayout() {
        return R.layout.activity_collection;
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, CollectionActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.getInstance(this).inject(this);
        init();
    }

    private void init() {
        mAdapter = new CollectionAdapter(collections);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        mToolbar.setNavigationIcon(R.drawable.ic_home);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setTitle(R.string.collection);
        mToolbar.inflateMenu(R.menu.menu_collection);
        mToolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        for (FloodCollection collection : collections) {
            collection.setCheckable(!collection.isCheckable());
        }
        mAdapter.notifyDataSetChanged();
        return false;
    }
}
