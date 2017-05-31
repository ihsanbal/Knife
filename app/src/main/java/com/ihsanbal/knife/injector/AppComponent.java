/*
 * Created by ihsan on 5/23/17 8:59 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/23/17 8:59 PM
 *
 */

package com.ihsanbal.knife.injector;

import com.ihsanbal.knife.ui.CollectionActivity;
import com.ihsanbal.knife.ui.DashboardActivity;
import com.ihsanbal.knife.ui.TweetActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author ihsan on 23/05/2017.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(DashboardActivity activity);

    void inject(TweetActivity activity);

    void inject(CollectionActivity activity);

}
