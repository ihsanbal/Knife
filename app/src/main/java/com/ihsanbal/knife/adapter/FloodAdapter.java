/*
 * Created by ihsan on 5/14/17 6:39 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/14/17 6:32 PM
 *
 */

package com.ihsanbal.knife.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ihsanbal.knife.R;
import com.ihsanbal.knife.model.FloodModel;
import com.ihsanbal.knife.ui.TweetActivity;
import com.ihsanbal.knife.widget.KTextView;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ihsan on 23/04/2017.
 */

public class FloodAdapter extends RecyclerView.Adapter<FloodAdapter.ViewHolder> {

    private final ArrayList<FloodModel> items;

    public FloodAdapter(ArrayList<FloodModel> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_flood, parent, false));
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
        }
        if (item.getType() == TweetActivity.Type.COMPLETE.ordinal()) {
            holder.itemView.setVisibility(View.GONE);
        } else {
            holder.mInReplyLineTop.setVisibility(View.GONE);
            holder.mInReplyLineFull.setVisibility(View.GONE);
            holder.mInReplyLineBottom.setVisibility(View.GONE);
        }
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

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
