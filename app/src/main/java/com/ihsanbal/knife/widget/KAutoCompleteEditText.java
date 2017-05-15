/*
 * Created by ihsan on 5/14/17 6:40 PM
 * Copyright (c) 2017 All rights reserved.
 *
 * Last modified 4/10/17 11:12 PM
 *
 */

package com.ihsanbal.knife.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.ihsanbal.knife.R;

/**
 * @author ihsan on 08/04/2017.
 */

public class KAutoCompleteEditText extends AppCompatAutoCompleteTextView {

    public KAutoCompleteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

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
