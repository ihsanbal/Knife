/*
 * Created by ihsan on 5/14/17 6:39 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 4/23/17 1:07 PM
 *
 */

package com.ihsanbal.knife.model;

import com.twitter.sdk.android.core.models.User;

/**
 * @author ihsan on 23/04/2017.
 */

public class FloodModel {

    private int type;
    private String tweet;
    private User user;

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
}
