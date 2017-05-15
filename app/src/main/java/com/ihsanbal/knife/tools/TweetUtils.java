/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/1/17 2:45 PM
 *
 */

package com.ihsanbal.knife.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.ihsanbal.knife.model.FloodModel;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;

/**
 * @author ihsan on 22/04/2017.
 */

public class TweetUtils {
    public static ArrayList<FloodModel> formatTweetList(String tweet, User user, int type, String screenName) {
        String[] split = tweet.split(" |\\\\.|\\\\,|\\\\;");
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<FloodModel> items = new ArrayList<>();
        int index = 0;
        int screenLength = getScreenLength(screenName);
        FloodModel model;
        for (String item : split) {
            if (split.length > index) {
                if ((stringBuilder.toString() + item).length() + 1 > 140) {
                    model = new FloodModel(user);
                    model.setTweet(stringBuilder.toString());
                    model.setType(type);
                    items.add(model);
                    stringBuilder = new StringBuilder(item);
                } else {
                    stringBuilder.append(index == 0 ? (screenLength > 0 ? "@" + screenName + " " + item : item) : " " + item);
                }
                if (index == split.length - 1) {
                    model = new FloodModel(user);
                    model.setTweet(stringBuilder.toString());
                    model.setType(type);
                    items.add(model);
                }
            }
            index++;
        }
        return items;
    }

    public static ArrayList<FloodModel> formatTweetNumber(String tweet, User user, int type) {
        String[] split = tweet.split(" |\\\\.|\\\\,|\\\\;");
        StringBuilder stringBuilder = new StringBuilder();
        ArrayList<FloodModel> items = new ArrayList<>();
        int index = 0;
        FloodModel model;
        for (String item : split) {
            if (split.length > index) {
                if ((stringBuilder.toString() + item).length() + 1 > 135) {
                    model = new FloodModel(user);
                    model.setTweet(items.size() + 1 + " - " + stringBuilder.toString());
                    model.setType(type);
                    items.add(model);
                    stringBuilder = new StringBuilder(item);
                } else {
                    stringBuilder.append(index == 0 ? item : " " + item);
                }
                if (index == split.length - 1) {
                    model = new FloodModel(user);
                    model.setTweet(items.size() + 1 + " - " + stringBuilder.toString());
                    model.setType(type);
                    items.add(model);
                }
            }
            index++;
        }
        return items;
    }

    private static int getScreenLength(String screenName) {
        if (!TextUtils.isEmpty(screenName)) {
            screenName += "@" + screenName;
            return screenName.length() > 1 ? screenName.length() : 0;
        }
        return 0;
    }

    public static void showProfile(Context context, String screenName) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + screenName)));
    }
}
