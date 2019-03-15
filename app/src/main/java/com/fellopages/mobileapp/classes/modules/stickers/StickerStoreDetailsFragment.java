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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.GridViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BaseButton;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StickerStoreDetailsFragment extends Fragment implements View.OnClickListener{

    private AppConstant mAppConst;
    private View rootView;
    private Context mContext;
    private String mStickerStoreViewUrl;
    private ImageView mImageView;
    private TextView mTitleView, mBodyView;
    private BaseButton mStickerOptionButton;
    private GridViewWithHeaderAndFooter mGridView;
    private int mCollectionId, NUM_OF_COLUMNS = 4;
    private JSONArray mDataResponseArray;
    private List<ImageViewList> mStickersList;
    private GridViewAdapter mAdapter;
    private int columnWidth;
    private String mStickerOptionUrl, menuName, mPreviousMenuName;
    private String mImageIcon;
    private boolean isAlreadyAdded = false;
    private JSONArray menuArray;
    private String stickerOptionUrl;
    private ImageLoader mImageLoader;


    public StickerStoreDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        mImageLoader = new ImageLoader(mContext);
        rootView = inflater.inflate(R.layout.sticker_store_view, null);

        if(getArguments() != null){
            mCollectionId = getArguments().getInt("collection_id", 0);
            mStickerStoreViewUrl = UrlUtil.BROWSE_STICKERS_STORES_URL + "?collection_id=" + mCollectionId;
        }
        getViews();
        makeRequest();
        return rootView;
    }


    @Override
    public void onAttach(Context context) { 
        super.onAttach(context);
        mContext = context;
    }

    private void getViews(){
        mImageView = rootView.findViewById(R.id.stickerStoreImage);
        mTitleView = rootView.findViewById(R.id.stickerStoreTitle);
        mBodyView = rootView.findViewById(R.id.stickerStoreDescription);
        mStickerOptionButton = rootView.findViewById(R.id.stickerStoreButton);
        mGridView = rootView.findViewById(R.id.gridView);
        mStickersList = new ArrayList<>();
        InitializeGridLayout();

        mAdapter = new GridViewAdapter((Activity) mContext, columnWidth, true, mStickersList);
        mGridView.setAdapter(mAdapter);
    }

    public void makeRequest() {

        mAppConst.getJsonResponseFromUrl(mStickerStoreViewUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                mDataResponseArray = jsonObject.optJSONArray("response");

                setDataInViews();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            }

        });
    }

    private void setDataInViews(){

        if(mDataResponseArray != null && mDataResponseArray.length() != 0){
            JSONObject jsonObject = mDataResponseArray.optJSONObject(0);
            String title = jsonObject.optString("title");
            String body = jsonObject.optString("body");
            String imageProfile = jsonObject.optString("image_profile");
            mImageIcon = jsonObject.optString("image_icon");
            JSONArray stickerImages = jsonObject.optJSONArray("stickers");
            menuArray = jsonObject.optJSONArray("menu");

            mTitleView.setText(title);
            mBodyView.setText(body);

            mImageLoader.setImageForUserProfile(imageProfile, mImageView);

            if(stickerImages != null){
                for(int i = 0; i < stickerImages.length(); i++){
                    String stickerImageProfile = stickerImages.optJSONObject(i).optString("image_profile");
                    mStickersList.add(new ImageViewList(stickerImageProfile));
                }
                mAdapter.notifyDataSetChanged();
            }

            if(menuArray != null){
                mStickerOptionButton.setVisibility(View.VISIBLE);
                menuName =  mPreviousMenuName = menuArray.optJSONObject(1).optString("name");
                if(menuName.equals("add")){
                    isAlreadyAdded = false;
                    mStickerOptionButton.setText(mContext.getResources().getString(R.string.add_sticker_button_text));
                } else{
                    isAlreadyAdded = true;
                    mStickerOptionButton.setText(mContext.getResources().getString(R.string.remove_sticker_button_text));
                }
                mStickerOptionButton.setOnClickListener(this);
            }
        }
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


    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.stickerStoreButton){
            HashMap<String, String> params = new HashMap<>();
            params.put("collection_id", String.valueOf(mCollectionId));
            if(menuName.equals("add")){
                mStickerOptionUrl = UrlUtil.ADD_STICKERS_STORE_URL;
            } else{
                mStickerOptionUrl = UrlUtil.REMOVE_STICKERS_STORE_URL;
            }

            mAppConst.postJsonResponseForUrl(mStickerOptionUrl, params, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    JSONObject stickerStoreObject = new JSONObject();
                    try {
                        stickerStoreObject.put("collection_id", mCollectionId);
                        stickerStoreObject.put("image_icon", mImageIcon);
                        stickerStoreObject.put("action", menuName);
                        if(menuName.equals("add")){
                            if(!isAlreadyAdded){
                                ConstantVariables.STICKERS_STORE_ARRAY.put(String.valueOf(mCollectionId), stickerStoreObject);
                            } else if(ConstantVariables.STICKERS_STORE_ARRAY != null && ConstantVariables.STICKERS_STORE_ARRAY
                                    .optJSONObject(String.valueOf(mCollectionId)) != null){
                                ConstantVariables.STICKERS_STORE_ARRAY.remove(String.valueOf(mCollectionId));
                            }
                            stickerOptionUrl = "reactions/store/remove";
                            menuName = "remove";
                            mStickerOptionButton.setText(mContext.getResources().getString(R.string.remove_sticker_button_text));
                        } else{
                            stickerOptionUrl = "reactions/store/add";
                            menuName = "add";
                            if(ConstantVariables.STICKERS_STORE_ARRAY != null && ConstantVariables.STICKERS_STORE_ARRAY
                                    .optJSONObject(String.valueOf(mCollectionId)) != null){
                                ConstantVariables.STICKERS_STORE_ARRAY.remove(String.valueOf(mCollectionId));
                            } else if (ConstantVariables.STICKERS_STORE_ARRAY != null){
                                ConstantVariables.STICKERS_STORE_ARRAY.put(String.valueOf(mCollectionId), stickerStoreObject);
                            }
                            mStickerOptionButton.setText(mContext.getResources().getString(R.string.add_sticker_button_text));
                        }

                        if(!menuName.equals(mPreviousMenuName)){
                            JSONObject menuObject = menuArray.optJSONObject(1);
                            menuObject.put("name", menuName);
                            menuObject.put("url", mStickerOptionUrl);
                            menuArray.put(1, menuObject);
                            PreferencesUtils.updateStickersStorePref(mContext, menuArray);
                        } else{
                            PreferencesUtils.clearStickerStoreMenuPref(mContext);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                }
            });
        }
    }
}
