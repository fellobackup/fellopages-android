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


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.notifications.NotificationViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StickerStoreListFragment extends Fragment {

    private List<Object> mBrowseItemList;
    private AppConstant mAppConst;
    private Context mContext;
    private View rootView;
    private RecyclerView mRecyclerView;
    private String mStoreListUrl;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView.LayoutManager mLayoutManager;
    private NotificationViewAdapter mStickerStoreAdapter;
    private OnStickerStoreClickListener mStoreClickedListener;
    private int mClickedPosition;
    private ImageView mStickersStoreImage;

    public StickerStoreListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null){
            // Inflate the layout for this fragment
            mBrowseItemList = new ArrayList<>();
            mAppConst = new AppConstant(mContext);
            rootView = inflater.inflate(R.layout.recycler_view_layout, null);
            mRecyclerView = rootView.findViewById(R.id.recycler_view);
            mStickersStoreImage = rootView.findViewById(R.id.stickersStoreImage);
            mStickersStoreImage.setImageResource(R.drawable.stickers_store);
            mStickersStoreImage.setVisibility(View.VISIBLE);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(mContext);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));

            mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
            mSwipeRefreshLayout.setEnabled(false);
            mStoreListUrl = UrlUtil.BROWSE_STICKERS_STORES_URL;

            mStickerStoreAdapter = new NotificationViewAdapter(mContext, mBrowseItemList, false, true,
                    new NotificationViewAdapter.OnItemClickListener() {
                        BrowseListItems listItems;
                        int id;
                        @Override
                        public void onItemClick(View view, int position) {

                            mClickedPosition = position;
                            mStoreClickedListener.OnStoreClicked((BrowseListItems) mBrowseItemList.get(position));
                        }

                        @Override
                        public void onProfilePictureClicked(View view, int position) {
                            listItems = (BrowseListItems) mBrowseItemList.get(position);
                        }


                        @Override
                        public void onOptionSelected(View v, BrowseListItems listItems, int position) {

                        }
                    });

            mRecyclerView.setAdapter(mStickerStoreAdapter);

            makeRequest();
        }

        return rootView;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        mContext = context;
        mStoreClickedListener = (OnStickerStoreClickListener) context;
    }

    @Override
    public void onResume() {
        // Change the menu button add/remove color when the sticker store will be added/removed from
        // store details page.
        if(PreferencesUtils.getStickersStoreMenu(mContext) != null){
            try {
                JSONArray stickerStoreMenuArray = new JSONArray(PreferencesUtils.getStickersStoreMenu(mContext));
                BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(mClickedPosition);
                listItems.setMenuArray(stickerStoreMenuArray);
                mStickerStoreAdapter.notifyItemChanged(mClickedPosition);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PreferencesUtils.clearStickerStoreMenuPref(mContext);
        }
        super.onResume();
    }

    public void makeRequest() {

        mAppConst.getJsonResponseFromUrl(mStoreListUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mBrowseItemList.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);

                addStickerStoresToList(jsonObject);
                mStickerStoreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            }

        });
    }

    private void addStickerStoresToList(JSONObject jsonObject){

        if(jsonObject != null && jsonObject.length() != 0){
            JSONArray responseArray = jsonObject.optJSONArray("response");
            if(responseArray != null && responseArray.length() != 0){
                for(int i = 0; i < responseArray.length(); i++){
                    JSONObject storeJsonObject = responseArray.optJSONObject(i);
                    int collectionId = storeJsonObject.optInt("collection_id");
                    String stickerStoreTitle = storeJsonObject.optString("title");
                    String stickerStoreImage = storeJsonObject.optString("image_profile");
                    JSONArray menuArray = storeJsonObject.optJSONArray("menu");
                    int stickersCount = storeJsonObject.optInt("sticker_count");
                    mBrowseItemList.add(new BrowseListItems(collectionId, stickerStoreImage, stickerStoreTitle,
                            menuArray, stickersCount));
                }
            }
        }

    }

    public interface OnStickerStoreClickListener{
        void OnStoreClicked(BrowseListItems listItem);
    }

}
