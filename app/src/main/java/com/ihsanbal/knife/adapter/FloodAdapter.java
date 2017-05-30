/*
 * Created by ihsan on 5/14/17 6:39 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/14/17 6:32 PM
 *
 */

package com.ihsanbal.knife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihsanbal.knife.R;
import com.ihsanbal.knife.model.FloodModel;
import com.ihsanbal.knife.ui.TweetActivity;
import com.ihsanbal.knife.widget.KTextView;
import com.ihsanbal.knife.widget.MediaView;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.tweetui.internal.AspectRatioFrameLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ihsan on 23/04/2017.
 */

public class FloodAdapter extends RecyclerView.Adapter<FloodAdapter.ViewHolder> {

    private final ArrayList<FloodModel> items;
    private final MediaView.OnMediaClickListener mediaClickListener;
    private View.OnClickListener listener;

    public FloodAdapter(ArrayList<FloodModel> items, MediaView.OnMediaClickListener listener) {
        this.items = items;
        this.mediaClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_flood, parent, false), listener, mediaClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FloodModel item = items.get(position);
        Picasso.with(holder.getContext())
                .load(item.getUser().profileImageUrl.replace("_normal", ""))
                .fit()
                .into(holder.mProfile);
        User user = items.get(position).getUser();
        holder.mUserText.setText(item.getTweet());
        holder.mDisplayName.setText(user.screenName);
        holder.mUserName.setText(user.name);
        holder.mActionView.setTag(position);

        if (item.getMedias().size() > 0) {
            holder.mMediaView.setVisibility(View.VISIBLE);
            holder.mAspectRatioContainer.setVisibility(View.VISIBLE);
            @SuppressLint("PrivateResource") int mediaViewRadius =
                    holder.getContext().getResources().getDimensionPixelSize(com.twitter.sdk.android.tweetui.R.dimen.tw__media_view_radius);
            holder.mMediaView.setRoundedCornersRadii(mediaViewRadius, mediaViewRadius,
                    mediaViewRadius, mediaViewRadius);
            holder.mMediaView.setTweetMediaEntities(item.getMediaPaths());
        } else {
            holder.mMediaView.setVisibility(View.GONE);
            holder.mAspectRatioContainer.setVisibility(View.GONE);
        }

        if (item.getType() == TweetActivity.Type.REPLY.ordinal() && items.size() > 1) {
            if (position == 0) {
                holder.mInReplyLineTop.setVisibility(View.VISIBLE);
                holder.mInReplyLineFull.setVisibility(View.GONE);
                holder.mInReplyLineBottom.setVisibility(View.GONE);
            } else if (position == items.size() - 1) {
                holder.mInReplyLineTop.setVisibility(View.GONE);
                holder.mInReplyLineFull.setVisibility(View.GONE);
                holder.mInReplyLineBottom.setVisibility(View.VISIBLE);
            } else {
                holder.mInReplyLineTop.setVisibility(View.GONE);
                holder.mInReplyLineFull.setVisibility(View.VISIBLE);
                holder.mInReplyLineBottom.setVisibility(View.GONE);
            }
        } else {
            holder.mInReplyLineTop.setVisibility(View.GONE);
            holder.mInReplyLineFull.setVisibility(View.GONE);
            holder.mInReplyLineBottom.setVisibility(View.GONE);
        }
    }

    public void setActionClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.user_text)
        KTextView mUserText;

        @BindView(R.id.display_name)
        KTextView mDisplayName;

        @BindView(R.id.user_name)
        KTextView mUserName;

        @BindView(R.id.profile_image)
        AppCompatImageView mProfile;

        @BindView(R.id.in_reply_line_full)
        KTextView mInReplyLineFull;

        @BindView(R.id.in_reply_line_bottom)
        KTextView mInReplyLineBottom;

        @BindView(R.id.in_reply_line_top)
        KTextView mInReplyLineTop;

        @BindView(R.id.knife_action_view)
        CardView mActionView;

        @BindView(R.id.media_view)
        MediaView mMediaView;

        @BindView(R.id.aspect_ratio_media_container)
        AspectRatioFrameLayout mAspectRatioContainer;

        ViewHolder(View itemView, View.OnClickListener listener, MediaView.OnMediaClickListener mediaClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mActionView.setOnClickListener(listener);
            mMediaView.setOnMediaClickListener(mediaClickListener);
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
