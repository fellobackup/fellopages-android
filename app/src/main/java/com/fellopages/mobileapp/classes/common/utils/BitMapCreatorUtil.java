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

package com.fellopages.mobileapp.classes.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;


public class BitMapCreatorUtil extends AsyncTask<Void, Void, Bitmap> {

    private Context mContext;
    private String url;
    private OnBitmapLoadListener mOnBitmapLoadListener;

    public BitMapCreatorUtil(Context context, String url, OnBitmapLoadListener bitmapLoadListener) {
        this.mContext = context;
        this.url = url;
        this.mOnBitmapLoadListener = bitmapLoadListener;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        return BitmapUtils.getBitmapFromURL(url);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null) {
            CacheUtils.getInstance(mContext).getLru().put(url, bitmap);
            if (mOnBitmapLoadListener != null) {
                mOnBitmapLoadListener.onBitMapLoad(bitmap);
            }
        }

    }

    public interface OnBitmapLoadListener {
        void onBitMapLoad(Bitmap bitmap);
    }
}
