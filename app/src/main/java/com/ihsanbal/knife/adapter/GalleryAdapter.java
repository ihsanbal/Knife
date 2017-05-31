/*
 * Created by ihsan on 5/30/17 11:02 AM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/30/17 11:02 AM
 *
 */

package com.ihsanbal.knife.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetui.internal.GalleryImageView;
import com.twitter.sdk.android.tweetui.internal.SwipeToDismissTouchListener;

import java.io.File;
import java.util.List;

/**
 * @author ihsan on 30/05/2017.
 */
public class GalleryAdapter extends PagerAdapter {
    private final List<String> items;
    private final Context context;
    private final SwipeToDismissTouchListener.Callback callback;

    public GalleryAdapter(Context context, List<String> items, SwipeToDismissTouchListener.Callback callback) {
        this.context = context;
        this.callback = callback;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final GalleryImageView root = new GalleryImageView(context);
        root.setSwipeToDismissCallback(callback);

        container.addView(root);

        final String entity = items.get(position);
        Picasso.with(context).load(Uri.fromFile(new File(entity))).into(root);

        return root;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
