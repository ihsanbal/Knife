/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 5/14/17 6:24 PM
 *
 */

package com.ihsanbal.knife.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.ihsanbal.knife.R;

/**
 * @author ihsan on 08/04/2017.
 */

public class KAppCompatButton extends AppCompatButton {

    public KAppCompatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @SuppressLint("CustomViewStyleable")
    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode())
            return;
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.KTextView);
        int font = attributes.getInteger(R.styleable.KTextView_font, 0);
        switch (font) {
            case 0:
                setTypeface(Typeface.createFromAsset(context.getAssets(),
                        "fonts/" + "CenturyGothic" + ".ttf"));
                break;
            default:
                setTypeface(Typeface.createFromAsset(context.getAssets(),
                        "fonts/" + "CenturyGothicBold" + ".ttf"));
                break;
        }
        attributes.recycle();
    }

}
