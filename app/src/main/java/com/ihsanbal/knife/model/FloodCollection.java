/*
 * Created by ihsan on 5/23/17 3:06 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/23/17 3:06 PM
 *
 */

package com.ihsanbal.knife.model;

import java.util.ArrayList;

/**
 * @author ihsan on 23/05/2017.
 */
public class FloodCollection {

    private String title;
    private ArrayList<FloodModel> floodList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public FloodCollection(ArrayList<FloodModel> list) {
        this.floodList = list;
    }

    public ArrayList<FloodModel> getFloodList() {
        return floodList;
    }

    public void setFloodList(ArrayList<FloodModel> floodList) {
        this.floodList = floodList;
    }
}
