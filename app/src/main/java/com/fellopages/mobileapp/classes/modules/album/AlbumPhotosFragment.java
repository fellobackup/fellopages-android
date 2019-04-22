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

package com.fellopages.mobileapp.classes.modules.album;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;


import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.GridViewAdapter;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.GridViewWithHeaderAndFooter;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumPhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumPhotosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        AbsListView.OnScrollListener, AdapterView.OnItemClickListener {


    private Context mContext;
    private AppConstant mAppConst;
    private JSONArray mDataResponseArray;
    private JSONObject mBody;
    private String mBrowseAlbumPhotosUrl;
    private String title, ownerTitle, normalImgUrl;
    private boolean isLoading = false,isVisibleToUser = false;
    private int pageNumber = 1, mLoadingPageNo = 1, mTotalItemCount = 0, mActualPhotoCount = 0, columnWidth, canEdit = 0;
    private ArrayList<PhotoListDetails> mPhotoDetails;
    private View rootView, footerView;
    private GridViewWithHeaderAndFooter mPhotoGridView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private GridViewAdapter mGridViewAdapter;
    Snackbar snackbar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     * @return A new instance of fragment AlbumPhotosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumPhotosFragment newInstance(Bundle bundle) {
        AlbumPhotosFragment fragment = new AlbumPhotosFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public AlbumPhotosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext =getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAppConst = new AppConstant(mContext);
        mPhotoDetails = new ArrayList<>();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.grid_view_layout, null);
        footerView = CustomViews.getFooterView(inflater);
        mPhotoGridView = rootView.findViewById(R.id.gridView);
        mPhotoGridView.addFooterView(footerView);
        footerView.setVisibility(View.GONE);

        columnWidth = CustomViews.initializeGridLayout(mContext, AppConstant.NUM_OF_COLUMNS_FOR_PHOTO_GRID,
                mPhotoGridView);
        ViewCompat.setNestedScrollingEnabled(mPhotoGridView,true);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        //Editing the url for browse album
        mBrowseAlbumPhotosUrl = AppConstant.DEFAULT_URL + "albums/photo/list?order=order%20DESC&limit="
                + AppConstant.LIMIT;

        mGridViewAdapter = new GridViewAdapter(getActivity(), columnWidth, mPhotoDetails, true);
        mPhotoGridView.setAdapter(mGridViewAdapter);

        mPhotoGridView.setOnItemClickListener(this);
        mPhotoGridView.setOnScrollListener(this);

        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            makeRequest();
        } else {
            if(snackbar != null && snackbar.isShown())
                snackbar.dismiss();
        }
    }

    public void makeRequest(){

        String url = mBrowseAlbumPhotosUrl + "&page=" + pageNumber;
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mPhotoDetails.clear();
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }

                addPhotosToList(jsonObject);
                isVisibleToUser = true;
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(getActivity(), rootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    rootView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }
            }
        });
    }

    public void loadMoreData(String url){


            mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    CustomViews.removeFooterView(footerView);
                    addPhotosToList(jsonObject);
                    isLoading = false;

                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    SnackbarUtils.displaySnackbar(rootView, message);
                }
            });

    }
    public void addPhotosToList(JSONObject jsonObject){
        mBody = jsonObject;
        mDataResponseArray = mBody.optJSONArray("photos");
        mTotalItemCount = mBody.optInt("totalPhotoCount");
        mActualPhotoCount = mBody.optInt("actual_count");

        if (mDataResponseArray != null && mDataResponseArray.length() > 0) {
            rootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            for (int i = 0; i < mDataResponseArray.length(); i++) {
                JSONObject imageUrlsObj = mDataResponseArray.optJSONObject(i);
                String menuArray = imageUrlsObj.optString("menu");
                title = imageUrlsObj.optString("album_title");
                ownerTitle = imageUrlsObj.optString("owner_title");
                normalImgUrl = imageUrlsObj.optString("image");
                String image_title = imageUrlsObj.optString("title");
                String image_desc = imageUrlsObj.optString("description");
                int photo_id = imageUrlsObj.optInt("photo_id");
                int likeCount = imageUrlsObj.optInt("like_count");
                int commentCount = imageUrlsObj.optInt("comment_count");
                boolean isLiked = imageUrlsObj.optBoolean("is_like");
                String reactions = imageUrlsObj.optString("reactions");
                String mUserTagArray = imageUrlsObj.optString("tags");
                String contentUrl = imageUrlsObj.optString("content_url");

                mPhotoDetails.add(new PhotoListDetails(title, ownerTitle,
                        image_title, image_desc, photo_id, normalImgUrl, likeCount, commentCount,
                        isLiked, menuArray, reactions, mUserTagArray, contentUrl));


            }
        }else {
            rootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = rootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = rootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf03e");
            errorMessage.setText(mContext.getResources().getString(R.string.no_photos));
        }

        mGridViewAdapter.notifyDataSetChanged();

    }

    public void onRefresh() {
    /**
     * Showing Swipe Refresh animation on activity create
     * As animation won't start on onCreate, post runnable is used
     */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                makeRequest();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if(mPhotoGridView != null){
            mPhotoGridView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ConstantVariables.ALBUM_VIEW_PAGE:
                makeRequest();
                break;

            case ConstantVariables.VIEW_LIGHT_BOX:
                if (resultCode == ConstantVariables.LIGHT_BOX_EDIT) {
                    makeRequest();
                }
                break;
        }

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int limit=firstVisibleItem+visibleItemCount;
        if(limit==totalItemCount && !isLoading) {
            if(limit >= mActualPhotoCount && (AppConstant.LIMIT * mLoadingPageNo)  < mTotalItemCount){
                CustomViews.addFooterView(footerView);
                mLoadingPageNo = mLoadingPageNo + 1;
                String url = mBrowseAlbumPhotosUrl + "&page="+ mLoadingPageNo;
                isLoading = true;
                loadMoreData(url);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // on selecting grid view image
        // launch full screen activity
        Bundle bundle = new Bundle();
        bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mPhotoDetails);
        Intent i = new Intent(getActivity(), PhotoLightBoxActivity.class);
        i.putExtra(ConstantVariables.ITEM_POSITION, position);
        i.putExtra(ConstantVariables.CAN_EDIT, canEdit);
        i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, mTotalItemCount);
        i.putExtra(ConstantVariables.PHOTO_REQUEST_URL, mBrowseAlbumPhotosUrl);
        i.putExtras(bundle);
        startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);
    }
}
