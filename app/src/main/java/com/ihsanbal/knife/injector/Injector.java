/*
 * Created by ihsan on 5/23/17 9:02 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/23/17 9:02 PM
 *
 */

package com.ihsanbal.knife.injector;

import android.content.Context;

import com.ihsanbal.knife.KnifeApplication;

/**
 * @author ihsan on 23/05/2017.
 */
public class Injector {
    public static AppComponent getInstance(Context context) {
        return ((KnifeApplication) context.getApplicationContext()).getDaggerComponent();
    }
}
