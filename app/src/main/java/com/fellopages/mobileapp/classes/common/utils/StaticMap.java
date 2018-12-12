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
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.fellopages.mobileapp.classes.common.interfaces.OnStaticMapLoadListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;

public class StaticMap extends AsyncTask<Void, Void, Bitmap> {

    private Context mContext;
    private int mPosition;
    private String mGetMapUrl;
    private OnStaticMapLoadListener mOnStaticMapLoadListener;

    public StaticMap(Context context, int position, String getMapUrl,
                     OnStaticMapLoadListener onStaticMapLoadListener) {

        this.mContext = context;
        this.mPosition = position;
        this.mGetMapUrl = getMapUrl;
        this.mOnStaticMapLoadListener = onStaticMapLoadListener;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bmp = null;
        if (CacheUtils.getInstance(mContext).getLru().get(mGetMapUrl) != null) {
            return CacheUtils.getInstance(mContext).getLru().get(mGetMapUrl);
        } else {
            HttpClient httpclient = MySSLSocketFactoryUtil.getNewHttpClient();
            HttpGet request = new HttpGet(mGetMapUrl);

            InputStream in = null;
            try {
                HttpResponse response = httpclient.execute(request);
                in = response.getEntity().getContent();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
        if (bmp != null) {
            CacheUtils.getInstance(mContext).getLru().put(mGetMapUrl, bmp);
            if (mOnStaticMapLoadListener != null) {
                mOnStaticMapLoadListener.onMapLoaded(mPosition);
            }
        }
    }

}
