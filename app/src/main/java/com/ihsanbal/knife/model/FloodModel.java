/*
 * Created by ihsan on 5/14/17 6:39 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 4/23/17 1:07 PM
 *
 */

package com.ihsanbal.knife.model;

import android.text.TextUtils;

import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ihsan on 23/04/2017.
 */

public class FloodModel {

    private int type;
    private String tweet;
    private User user;
    private List<Long> medias = new ArrayList<>();
    private List<String> mediaPaths = new ArrayList<>();

    public FloodModel(User user) {
        this.user = user;
    }

    public int getType() {
        return type;
    }

    public String getTweet() {
        return tweet;
    }

    public User getUser() {
        return user;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public void setMediaPaths(String path) {
        mediaPaths.add(path);
    }

    public List<String> getMediaPaths() {
        return mediaPaths;
    }

    public List<Long> getMedias() {
        return medias;
    }

    public void setMedias(long id) {
        medias.add(id);
    }

    public String getMediasQuery() {
        String mediasQuery = "";
        for (Long id : medias) {
            mediasQuery += id + ",";
        }
        return TextUtils.isEmpty(mediasQuery) ? null : mediasQuery.substring(0, mediasQuery.length() - 1);
    }
}
