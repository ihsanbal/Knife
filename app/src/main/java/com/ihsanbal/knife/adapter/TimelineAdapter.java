/*
 * Created by ihsan on 5/14/17 6:39 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/2/17 9:19 PM
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
import android.widget.ImageView;
import android.widget.TextView;

import com.ihsanbal.knife.R;
import com.ihsanbal.knife.core.KRecyclerAdapter;
import com.ihsanbal.knife.model.TypeText;
import com.ihsanbal.knife.tools.TweetDateUtils;
import com.ihsanbal.knife.tools.TweetUtils;
import com.ihsanbal.knife.ui.TweetActivity;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ihsan on 03/04/2017.
 */
public class TimelineAdapter extends KRecyclerAdapter<RecyclerView.ViewHolder, Tweet> {

    public TimelineAdapter(final ArrayList<Tweet> items) {
        super(items);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, Type type) {
        switch (type) {
            case AD:
                return new ViewAdHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_native_ad, parent, false));
            default:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tweet, parent, false));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder h, final Tweet item) {
        final ViewHolder holder = (ViewHolder) h;
        Picasso.with(holder.getContext())
                .load(item.user.profileImageUrl.replace("_normal", ""))
                .fit()
                .into(holder.mProfileView);
        TypeText type = new TypeText(item, true);
        holder.mProfileView.setTag(type);
        holder.mKnifeAction.setTag(type);
        holder.mDisplayName.setText(item.user.name);
        holder.mUserText.setText(item.text);
        if (item.place != null) {
            holder.mTextGeo.setVisibility(View.VISIBLE);
            holder.mTextGeo.setText(item.place.fullName);
        } else {
            holder.mTextGeo.setVisibility(View.GONE);
        }
        holder.mUserName.setText("by " + item.user.screenName + " " + TweetDateUtils.getTimestamp(item, holder.getContext()));
        holder.mTweetView.setTweet(item);
        holder.mAvatarView.setVisibility(View.GONE);
        holder.mScreenNameView.setVisibility(View.GONE);
        holder.mFullNameView.setVisibility(View.GONE);
        holder.mTimeStamp.setVisibility(View.GONE);
        holder.mTwitterLogo.setVisibility(View.GONE);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.profile_image)
        AppCompatImageView mProfileView;

        @BindView(R.id.display_name)
        TextView mDisplayName;

        @BindView(R.id.user_name)
        TextView mUserName;

        @BindView(R.id.user_text)
        TextView mUserText;

        @BindView(R.id.user_geo)
        TextView mTextGeo;

        @BindView(R.id.tweet_ui_view)
        TweetView mTweetView;

        @BindView(R.id.tw__tweet_author_full_name)
        TextView mFullNameView;

        @BindView(R.id.tw__tweet_author_screen_name)
        TextView mScreenNameView;

        @BindView(R.id.tw__tweet_author_avatar)
        ImageView mAvatarView;

        @BindView(R.id.tw__twitter_logo)
        ImageView mTwitterLogo;

        @BindView(R.id.tw__tweet_timestamp)
        TextView mTimeStamp;

        @BindView(R.id.knife_action_view)
        CardView mKnifeAction;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mProfileView.setOnClickListener(this);
            mKnifeAction.setOnClickListener(this);
        }

        Context getContext() {
            return itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                TypeText type = (TypeText) v.getTag();
                switch (v.getId()) {
                    case R.id.knife_action_view:
                        TweetActivity.start(getContext(), type);
                        break;
                    default:
                        TweetUtils.showProfile(getContext(), type.getScreenName());
                        break;
                }
            }
        }
    }
}
