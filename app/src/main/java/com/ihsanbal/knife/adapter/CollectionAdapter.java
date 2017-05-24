/*
 * Created by ihsan on 5/23/17 9:30 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/23/17 9:30 PM
 *
 */

package com.ihsanbal.knife.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.ihsanbal.knife.R;
import com.ihsanbal.knife.model.FloodCollection;
import com.ihsanbal.knife.model.FloodModel;
import com.ihsanbal.knife.ui.TweetActivity;
import com.ihsanbal.knife.widget.KTextView;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ihsan on 23/05/2017.
 */
public class CollectionAdapter extends ExpandableRecyclerAdapter<FloodCollection, FloodModel, CollectionAdapter.ViewHolder, CollectionAdapter.ViewChildHolder> {

    private ArrayList<FloodCollection> items;

    public CollectionAdapter(ArrayList<FloodCollection> items) {
        super(items);
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateParentViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_collection, viewGroup, false));
    }

    @NonNull
    @Override
    public ViewChildHolder onCreateChildViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewChildHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_flood, viewGroup, false));
    }

    @Override
    public void onBindParentViewHolder(@NonNull ViewHolder holder, int i, @NonNull FloodCollection item) {
        holder.mTitle.setText(item.getTitle());
        if (item.isCheckable()) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindChildViewHolder(@NonNull ViewChildHolder holder, int position, int i1, @NonNull FloodModel item) {
        Picasso.with(holder.getContext())
                .load(item.getUser().profileImageUrl.replace("_normal", ""))
                .fit()
                .into(holder.mProfile);
        User user = item.getUser();
        holder.mUserText.setText(item.getTweet());
        holder.mDisplayName.setText(user.screenName);
        holder.mUserName.setText(user.name);
        if (item.getType() == TweetActivity.Type.REPLY.ordinal() && items.get(position).getFloodList().size() > 1) {
            if (i1 == 0) {
                holder.mInReplyLineTop.setVisibility(View.VISIBLE);
                holder.mInReplyLineFull.setVisibility(View.GONE);
                holder.mInReplyLineBottom.setVisibility(View.GONE);
            } else if (i1 == items.get(position).getFloodList().size() - 1) {
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

    class ViewChildHolder extends ChildViewHolder<FloodModel> {

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

        ViewChildHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }

    class ViewHolder extends ParentViewHolder<FloodCollection, FloodModel> {

        private static final float INITIAL_POSITION = 0.0f;
        private static final float ROTATED_POSITION = 90f;

        @BindView(R.id.title)
        KTextView mTitle;

        @BindView(R.id.icon_arrow)
        AppCompatImageView mArrow;

        @BindView(R.id.checkbox)
        AppCompatCheckBox mCheckBox;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("NewApi")
        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (expanded) {
                mArrow.setRotation(ROTATED_POSITION);
            } else {
                mArrow.setRotation(INITIAL_POSITION);
            }
        }

        public Context getContext() {
            return itemView.getContext();
        }
    }
}
