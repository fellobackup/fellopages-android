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

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.utils.SlideShowListItems;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;


public class SlideShowAdapter extends PagerAdapter {
    List<SlideShowListItems> mSlideShowList;
    LayoutInflater inflater;
    View mRootView;
    private OnItemClickListener mOnItemClickListener;
    private SlideShowListItems listItems;
    private int mLayoutResID;
    Context mContext;
    private ImageLoader mImageLoader;


    public SlideShowAdapter(Context context, int layoutResourceID, List<SlideShowListItems> slideShowList,
                            OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mSlideShowList = slideShowList;
        mOnItemClickListener = onItemClickListener;
        mImageLoader = new ImageLoader(mContext);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return mSlideShowList.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        // Initialize view
        final ListItemHolder listItemHolder = new ListItemHolder();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView = inflater.inflate(mLayoutResID, container, false);

        listItemHolder.mListImage = mRootView.findViewById(R.id.contentImage);
        listItemHolder.mContentTitle = mRootView.findViewById(R.id.title);
        mRootView.findViewById(R.id.featuredLabel).setVisibility(View.VISIBLE);

        mRootView.setTag(listItemHolder);
        listItems = this.mSlideShowList.get(position);

        //Set content in view
        mImageLoader.setImageUrl(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);
        listItemHolder.mContentTitle.setText(listItems.getmBrowseListTitle());

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(view, position);
            }
        });

        container.addView(mRootView);
        return mRootView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private static class ListItemHolder {

        ImageView mListImage;
        TextView mContentTitle;
    }
}
