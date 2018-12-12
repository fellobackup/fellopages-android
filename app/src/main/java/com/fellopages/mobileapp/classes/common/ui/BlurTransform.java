/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.common.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransform implements Transformation {
    private static final int UP_LIMIT = 25;
    private static final int LOW_LIMIT = 1;
    protected final Context context;
    private final int blurRadius;


    public BlurTransform(Context context, int radius) {
        this.context = context;

        if (radius < LOW_LIMIT) {
            this.blurRadius = LOW_LIMIT;
        } else if (radius > UP_LIMIT) {
            this.blurRadius = UP_LIMIT;
        } else {
            this.blurRadius = radius;
        }
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Bitmap sourceBitmap = source;

        Bitmap blurredBitmap;
        blurredBitmap = Bitmap.createBitmap(sourceBitmap);

        RenderScript renderScript = RenderScript.create(context);

        Allocation input = Allocation.createFromBitmap(renderScript,
                sourceBitmap,
                Allocation.MipmapControl.MIPMAP_FULL,
                Allocation.USAGE_SCRIPT);

        Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur script = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            script = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));

            script.setInput(input);
            script.setRadius(blurRadius);

            script.forEach(output);
            output.copyTo(blurredBitmap);
            if (source != blurredBitmap)
                source.recycle();
        }

        return blurredBitmap;
    }

    @Override
    public String key() {
        return "blurred";
    }
}
