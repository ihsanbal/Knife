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

import java.util.ArrayList;

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
                break;
        }
    }

    @Override
    public int getItemCount() {
        int plus = items.size() / 20;
        return items.size() + plus;
    }

    protected class ViewAdHolder extends RecyclerView.ViewHolder {

        public ViewAdHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
