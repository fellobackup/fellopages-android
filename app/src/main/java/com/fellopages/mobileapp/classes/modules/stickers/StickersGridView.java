/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *    You may not use this file except in compliance with the
 *    SocialEngineAddOns License Agreement.
 *    You may obtain a copy of the License at:
 *    https://www.socialengineaddons.com/android-app-license
 *    The full copyright and license information is also mentioned
 *    in the LICENSE file that was distributed with this
 *    source code.
 */

package com.fellopages.mobileapp.classes.modules.stickers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.GridViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class StickersGridView{
    public View rootView;
    StickersPopup mStickersPopup;
    private Context mContext;
    public GridView mGridView;
    private AppConstant mAppConst;
    private ProgressBar mProgressBar;
    private List<ImageViewList> mStickersList;
    private int columnWidth;
    private JSONArray mStickerArray;
    GridViewAdapter mAdapter;
    private int NUM_OF_COLUMNS =  4;
    private LinearLayout mMessageLayout;

    public StickersGridView(Context context, int collectionId, StickersPopup stickersPopup) {

        mContext = context;
        mAppConst = new AppConstant(mContext);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mStickersPopup = stickersPopup;
        rootView = inflater.inflate(R.layout.grid_view_layout, null);

        if (!StickersUtil.isStorySticker) {
            rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_light));
        } else {
            rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));
        }

        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mMessageLayout = (LinearLayout ) rootView.findViewById(R.id.message_layout);

        rootView.findViewById(R.id.swipe_refresh_layout).setEnabled(false);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        InitializeGridLayout();

        mStickersList = new ArrayList<>();
        mAdapter = new GridViewAdapter((Activity) mContext, columnWidth, true, mStickersList);
        mGridView.setAdapter(mAdapter);

        if(collectionId != 0){
            String stickersUrl = UrlUtil.AAF_VIEW_STICKERS_URL + "?collectionId=" + collectionId;

            // Send Request for Loading Stickers
            mAppConst.getJsonResponseFromUrl(stickersUrl, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mStickersList.clear();
                    mProgressBar.setVisibility(View.GONE);
                    mStickerArray = jsonObject.optJSONArray("stickers");
                    addDataToList();
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        } else if(mStickerArray != null){
            mProgressBar.setVisibility(View.GONE);
            addDataToList();
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAppConst.hideKeyboard();
                ImageViewList stickerInfo = mStickersList.get(position);
                if(mStickersPopup.onStickerClickedListener != null){
                    mStickersPopup.onStickerClickedListener.onStickerClicked(stickerInfo);
                }
            }
        });

    }

    private void addDataToList(){
        if(mStickerArray != null && mStickerArray.length() != 0){
            mMessageLayout.setVisibility(View.GONE);
            for(int i = 0; i < mStickerArray.length(); i++){
                JSONObject jsonObject = mStickerArray.optJSONObject(i);
                String stickerGuid = jsonObject.optString("guid");
                String imageUrl = jsonObject.optString("image_profile");
                mStickersList.add(new ImageViewList(imageUrl, stickerGuid));
            }

        } else {
            mMessageLayout.setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById
                    (R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorMessage.setText(mContext.getResources().getString(R.string.no_stickers));
        }
        mAdapter.notifyDataSetChanged();

    }

    /**
     * Method to calculate the grid dimensions Calculates number columns and
     * columns width in grid
     * */
    public void InitializeGridLayout() {

        Resources r = mContext.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.STICKERS_GRID_PADDING, r.getDisplayMetrics());

        // Column width

        columnWidth = (int) ((mAppConst.getScreenWidth() - ((NUM_OF_COLUMNS + 1) * padding)) /
                NUM_OF_COLUMNS);


        // Setting number of grid columns
        mGridView.setNumColumns(NUM_OF_COLUMNS);
        mGridView.setStretchMode(GridView.NO_STRETCH);
        mGridView.setColumnWidth(columnWidth);

        // Setting horizontal and vertical padding
        mGridView.setPadding((int)padding, (int)padding, (int)padding, (int)padding);
        mGridView.setHorizontalSpacing((int) padding);
        mGridView.setVerticalSpacing((int) padding);
    }

}
