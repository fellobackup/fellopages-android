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
 *
 */

package com.fellopages.mobileapp.classes.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.fellopages.mobileapp.classes.common.utils.CacheUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

/**
 * Class to Load the thumbnail image from local video file.
 */
public class VideoRequestHandler extends RequestHandler {
    public String SCHEME_VIDEO = "video";
    Context mContext;

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_VIDEO.equals(scheme));

    }

    @Override
    public Result load(Request data, int arg1) {
        Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        // Storing the bitmap into cache, so that next time it will show the thumbnail from cache.
        if (bm != null) {
            CacheUtils.getInstance(mContext).getLru().put(data.uri.getPath(), bm);
        }
        return new Result(bm, Picasso.LoadedFrom.DISK);

    }

    public void setContext(Context context) {
        mContext = context;

    }
}