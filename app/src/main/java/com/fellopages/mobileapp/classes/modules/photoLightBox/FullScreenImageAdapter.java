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

package com.fellopages.mobileapp.classes.modules.photoLightBox;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.wunderlist.slidinglayer.SlidingLayer;


public class FullScreenImageAdapter extends PagerAdapter implements View.OnClickListener {

    private Activity _activity;
    private LayoutInflater inflater;
    private View viewLayout;
    private TouchImageView imgDisplay;
    private ImageView placeholder;
    private ProgressBar pbPlaceHolder;
    private ArrayList<PhotoListDetails> mPhotoDetails;
    private ImageLoader mImageLoader;
    public PhotoLongClickListener mLongPressListener;


    public FullScreenImageAdapter(Activity activity, PhotoLongClickListener photoLongClickListener,
                                  ArrayList<PhotoListDetails> photoDetail) {
        this._activity = activity;
        this.mPhotoDetails = photoDetail;
        mLongPressListener = photoLongClickListener;
        mImageLoader = new ImageLoader(_activity);
    }


    @Override
    public int getCount() {
        return mPhotoDetails.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.image);
        placeholder = (ImageView) viewLayout.findViewById(R.id.placeholder);
        pbPlaceHolder = (ProgressBar) viewLayout.findViewById(R.id.holder_progress_bar);
        PhotoListDetails photoListDetails = mPhotoDetails.get(position);

        if (photoListDetails.getImageUrl() != null && !photoListDetails.getImageUrl().isEmpty()) {
            if (photoListDetails.getImageUrl().contains(".gif")) {
                pbPlaceHolder.setVisibility(View.VISIBLE);
                mImageLoader.setImageAnimationListener(photoListDetails.getImageUrl(), pbPlaceHolder, imgDisplay);
            } else {
                pbPlaceHolder.setVisibility(View.GONE);
                mImageLoader.setImageWithListener(photoListDetails.getImageUrl(), placeholder, imgDisplay);
            }
        }


        imgDisplay.setOnClickListener(this);

        imgDisplay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mLongPressListener.onLongPressed(v);
                return false;
            }
        });
        container.addView(viewLayout);

        return viewLayout;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.image:


                if (_activity.findViewById(R.id.bottomView).getVisibility() == View.VISIBLE) {
                    _activity.findViewById(R.id.bottomView).setVisibility(View.GONE);
                    _activity.findViewById(R.id.topView).setVisibility(View.GONE);
                    ((SlidingLayer) _activity.findViewById(R.id.slidingLayer)).closeLayer(true);
                } else {
                    _activity.findViewById(R.id.bottomView).setVisibility(View.VISIBLE);
                    _activity.findViewById(R.id.topView).setVisibility(View.VISIBLE);
                }

                break;
            default:
                break;
        }
    }
    public interface PhotoLongClickListener{
        void onLongPressed(View view);
    }

}
