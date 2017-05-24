/*
 * Created by ihsan on 5/23/17 3:06 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/23/17 3:06 PM
 *
 */

package com.ihsanbal.knife.model;

import com.bignerdranch.expandablerecyclerview.model.Parent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ihsan on 23/05/2017.
 */
public class FloodCollection implements Parent<FloodModel> {

    private String title;
    private ArrayList<FloodModel> floodList;
    private boolean checkable;
    private boolean isChecked;

    public boolean isCheckable() {
        return checkable;
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

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

    @Override
    public List<FloodModel> getChildList() {
        return floodList;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
