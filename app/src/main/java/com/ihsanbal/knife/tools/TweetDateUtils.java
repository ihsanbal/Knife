/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 4/28/17 10:29 AM
 *
 */

package com.ihsanbal.knife.tools;

import android.content.Context;

import com.twitter.sdk.android.core.models.Tweet;

/**
 * @author ihsan on 09/04/2017.
 */

public class TweetDateUtils {

    private static final String EMPTY_STRING = "";

    private TweetDateUtils() {
    }

    public static String getTimestamp(Tweet displayTweet, Context context) {
        final String formattedTimestamp;
        if (displayTweet != null && displayTweet.createdAt != null &&
                com.twitter.sdk.android.tweetui.TweetDateUtils.isValidTimestamp(displayTweet.createdAt)) {
            final Long createdAtTimestamp
                    = com.twitter.sdk.android.tweetui.TweetDateUtils.apiTimeToLong(displayTweet.createdAt);
            final String timestamp = com.twitter.sdk.android.tweetui.TweetDateUtils.getRelativeTimeString(context.getResources(),
                    System.currentTimeMillis(),
                    createdAtTimestamp);
            formattedTimestamp = com.twitter.sdk.android.tweetui.TweetDateUtils.dotPrefix(timestamp);
        } else {
            formattedTimestamp = EMPTY_STRING;
        }

        return formattedTimestamp;
    }

}