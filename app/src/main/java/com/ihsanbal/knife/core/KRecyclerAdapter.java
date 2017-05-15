/*
 * Created by ihsan on 5/14/17 6:39 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/7/17 11:27 PM
 *
 */

package com.ihsanbal.knife.core;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoOptions;
import com.ihsanbal.knife.BuildConfig;
import com.ihsanbal.knife.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ihsan on 21/03/2017.
 */
public abstract class KRecyclerAdapter<T extends RecyclerView.ViewHolder, E> extends RecyclerView.Adapter<T> {

    private final ArrayList<E> items;
    private static final int AD = 1;
    private static final int ITEM = 0;

    protected KRecyclerAdapter(@NonNull ArrayList<E> items) {
        this.items = items;
    }

    protected enum Type {
        ITEM, AD
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 0) {
            return position % 20 == 0 ? AD : ITEM;
        } else {
            return ITEM;
        }
    }

    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM:
                return onCreateViewHolder(parent, Type.ITEM);
            default:
                return onCreateViewHolder(parent, Type.AD);
        }
    }

    protected abstract T onCreateViewHolder(ViewGroup parent, Type viewType);

    protected abstract void onBindViewHolder(T holder, E item);

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(T holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                int plus = position / 19;
                onBindViewHolder(holder, items.get(position - plus));
                break;
            default:
                ViewAdHolder h = (ViewAdHolder) holder;
                AdRequest.Builder request = new AdRequest.Builder();
                if (BuildConfig.DEBUG) {
                    request.addTestDevice("7D376E3F676EDD395AB09C5FB3940F34");
                    request.addTestDevice("0C0FC69324CCBEDFE5E27CCD8B6739B9");
                    request.addTestDevice("F2EC702D97E2FC94DDABB269E40744B1");
                }
                h.mAdView.setVideoOptions(new VideoOptions.Builder()
                        .setStartMuted(true)
                        .build());
                h.mAdView.loadAd(request.build());
                break;
        }
    }

    @Override
    public int getItemCount() {
        int plus = items.size() / 20;
        return items.size() + plus;
    }

    protected class ViewAdHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.adView)
        NativeExpressAdView mAdView;

        public ViewAdHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
