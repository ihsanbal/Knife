/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 4/16/17 9:37 PM
 *
 */

package com.ihsanbal.knife.tools;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author ihsan on 09/04/2017.
 */

public class AnimUtils {
    private static final long DURATION = 1000;

    public static ValueAnimator animateFit(final View animateView, int endScore) {
        ValueAnimator animFit = ValueAnimator.ofInt(animateView.getMeasuredWidth(), endScore);
        animFit.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = animateView.getLayoutParams();
                layoutParams.width = val;
                animateView.setLayoutParams(layoutParams);
            }
        });
        animFit.setDuration(DURATION);
        return animFit;
    }

    public static void animateAlpha(final View animateView) {
        ValueAnimator animFit = ValueAnimator.ofFloat(animateView.getAlpha(), 1f);
        animFit.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                animateView.setAlpha(val);
            }
        });
        animFit.setDuration(DURATION);
        animFit.start();
    }

    public static ValueAnimator animateDefault(final View animateView, int endScore) {
        ValueAnimator animDefault = ValueAnimator.ofInt(animateView.getMeasuredWidth(), endScore);
        animDefault.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = animateView.getLayoutParams();
                layoutParams.width = val;
                animateView.setLayoutParams(layoutParams);
            }
        });
        animDefault.setDuration(DURATION);
        return animDefault;
    }
}
