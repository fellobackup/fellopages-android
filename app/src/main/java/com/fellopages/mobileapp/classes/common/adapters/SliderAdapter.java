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

package com.fellopages.mobileapp.classes.common.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;

public class SliderAdapter extends PagerAdapter {
    private Activity mActivity;
    List<ImageViewList> mPhotoList;
    LayoutInflater inflater;
    View viewLayout;
    ImageView imageView;
    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;


    public SliderAdapter(Activity activity,List<ImageViewList> photoList,OnItemClickListener onItemClickListener){

        this.mActivity = activity;
        this.mPhotoList = photoList;
        mOnItemClickListener = onItemClickListener;
        mImageLoader = new ImageLoader(mActivity);
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewLayout = inflater.inflate(R.layout.pager_photo_view, container,
                false);
        imageView = viewLayout.findViewById(R.id.thumbnail);
        ImageViewList img = mPhotoList.get(position);
        mImageLoader.setImageUrl(img.getmGridViewImageUrl(), imageView);
        viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(view, position);
            }
        });
        container.addView(viewLayout);
        return viewLayout;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
