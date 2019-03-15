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

package com.fellopages.mobileapp.classes.common.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.PhotoUploadingActivity;
import com.fellopages.mobileapp.classes.common.adapters.CustomImageAdapter;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.interfaces.OnFragmentDataChangeListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnUploadResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UploadFileToServerUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;

import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        OnUploadResponseListener {


    //Member variables.
    private Context mContext;
    private AppCompatActivity mActivity;
    private View mRootView, mInfoView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridLayoutManager mLayoutManager;
    private CustomImageAdapter mRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private SelectableTextView mNoPhotoMessage;
    private AppConstant mAppConst;
    private String mPhotoListUrl, mCurrentSelectedModule, mPhotoUploadUrl, contentIdString, mSubjectType,
            mSuccessMessage;
    private int mPageNumber = 1, mColumnWidth, mTotalItemCount, mCanUpload, mSubjectId;
    private boolean isLoading = false, isVisibleToUser = false, isProfilePageRequest = false,
            mIsCoverRequest = false, mIsCoverChange = false;
    private JSONObject mBody;
    private JSONArray mPhotosJsonArray;
    private ImageViewList mGridViewImages;
    private List<ImageViewList> mPhotoUrls;
    private ArrayList<String> mSelectPath;
    private ArrayList<PhotoListDetails> mPhotoDetails;
    private OnFragmentDataChangeListener mOnFragmentDataChangeListener;
    private AlertDialogWithAction mAlertDialogWithAction;


    public PhotoFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(boolean visible) {
        super.setMenuVisibility(visible);
        // Make sure that currently visible
        if (visible && !isVisibleToUser && mContext != null) {
            sendRequest(mPhotoListUrl);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.recycler_view_layout, container, false);
        mInfoView = inflater.inflate(R.layout.fragment_no_data, container, false);


        mContext = getContext();
        mActivity = (AppCompatActivity) getActivity();

        mAppConst = new AppConstant(mContext);
        mAlertDialogWithAction = new AlertDialogWithAction(mContext);
        mPhotoUrls = new ArrayList<>();
        mPhotoDetails = new ArrayList<>();
        mGridViewImages = new ImageViewList();

        mPhotoUploadUrl = mPhotoListUrl = getArguments().getString(ConstantVariables.URL_STRING);
        String contentTitle = getArguments().getString(ConstantVariables.CONTENT_TITLE);
        mCanUpload = getArguments().getInt(ConstantVariables.CAN_UPLOAD);
        isProfilePageRequest = getArguments().getBoolean(ConstantVariables.IS_PROFILE_PAGE_REQUEST);
        mIsCoverChange = getArguments().getBoolean("isCoverChange");
        mIsCoverRequest = getArguments().getBoolean("isCoverRequest");
        mSubjectType = getArguments().getString(ConstantVariables.SUBJECT_TYPE);
        mSubjectId = getArguments().getInt(ConstantVariables.SUBJECT_ID);
        boolean isFirstTab = getArguments().getBoolean(ConstantVariables.IS_FIRST_TAB_REQUEST);

        mCurrentSelectedModule = getArguments().getString(ConstantVariables.EXTRA_MODULE_TYPE);
        if (mCurrentSelectedModule == null || mCurrentSelectedModule.isEmpty()) {
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }

        mPhotoListUrl += "?getReaction=1&limit=" + AppConstant.LIMIT;
        if (mCurrentSelectedModule != null && !mCurrentSelectedModule.equals(ConstantVariables.PRODUCT_MENU_TITLE)) {

            mPhotoListUrl = UrlUtil.VIEW_PHOTOS_URL;

            switch (mCurrentSelectedModule) {

                case ConstantVariables.GROUP_MENU_TITLE:
                    mSubjectType = "group";
                    break;
                case ConstantVariables.EVENT_MENU_TITLE:
                    mSubjectType = "event";
                    break;
                case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                    mSubjectType = "siteevent_event";
                    break;
                case ConstantVariables.MLT_MENU_TITLE:
                    mPhotoUploadUrl += "?listingtype_id=" + getArguments().getInt(ConstantVariables.LISTING_TYPE_ID);
                    mSubjectType = "sitereview_listing";
                    break;
            }
            mPhotoListUrl += "?getReaction=1&subject_id=" + mSubjectId + "&subject_type=" + mSubjectType
                    + "&limit=" + AppConstant.LIMIT;
        }

        getViews();

        InitializeGridLayout();
        setRecyclerViewAdapter();
        setOnScrollListener();
        if (isProfilePageRequest) {
            mOnFragmentDataChangeListener = FragmentUtils.getOnFragmentDataChangeListener();
        } else {
            sendRequest(mPhotoListUrl);
        }

        if (isFirstTab) {
            sendRequest(mPhotoListUrl);
        }

        return mRootView;
    }

    /***
     * Method to get Views
     */
    public void getViews() {

        mProgressBar = mRootView.findViewById(R.id.progressBar);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = mRootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mSwipeRefreshLayout.getLayoutParams();
        layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_2dp), 0, 0);
        mSwipeRefreshLayout.setLayoutParams(layoutParams);

        // Adding no data view at top of the recycler view.
        mNoPhotoMessage = mInfoView.findViewById(R.id.no_data_msg);
        RelativeLayout mainView = mRootView.findViewById(R.id.main_view_recycler);
        mainView.addView(mInfoView);
        CustomViews.addHeaderView(R.id.fragment_main_view, mSwipeRefreshLayout);
    }

    /**
     * Method to calculate the grid dimensions Calculates number columns and
     * columns width in grid
     */
    private void InitializeGridLayout() {
        // Column width
        mColumnWidth = (mAppConst.getScreenWidth() / AppConstant.NUM_OF_COLUMNS_FOR_VIEW_PAGE);
        mLayoutManager = new GridLayoutManager(mContext, AppConstant.NUM_OF_COLUMNS_FOR_VIEW_PAGE);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /**
     * Method to set recycler view adapter on recycler view.
     */
    public void setRecyclerViewAdapter() {

        mRecyclerViewAdapter = new CustomImageAdapter(mActivity, mGridViewImages, mPhotoUrls, mColumnWidth,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        position++;
                        PhotoListDetails photoListDetails = mPhotoDetails.get(position);
                        String actionUrl;

                        if (mIsCoverChange) {
                            actionUrl = UrlUtil.UPLOAD_COVER_PHOTO_URL + "subject_id=" + mSubjectId + "&subject_type="
                                    + mSubjectType + "&photo_id=" + photoListDetails.getPhotoId();

                            if (mIsCoverRequest) {
                                mSuccessMessage = mContext.getResources().getString(R.string.cover_photo_updated);
                            } else {
                                actionUrl += "&special=profile";
                                mSuccessMessage = mContext.getResources().getString(R.string.profile_photo_updated);
                            }
                            mAppConst.showProgressDialog();
                            mAppConst.postJsonRequestWithoutParams(actionUrl, new OnResponseListener() {
                                @Override
                                public void onTaskCompleted(JSONObject jsonObject) {
                                    launchUserProfileOnSuccessOrError(mSuccessMessage);
                                }

                                @Override
                                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                    launchUserProfileOnSuccessOrError(message);
                                }
                            });

                        } else {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mPhotoDetails);
                            Intent i = new Intent(mActivity, PhotoLightBoxActivity.class);
                            i.putExtra(ConstantVariables.ITEM_POSITION, position);
                            i.putExtra(ConstantVariables.CAN_EDIT, mCanUpload);
                            i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, mTotalItemCount);
                            i.putExtra(ConstantVariables.PHOTO_REQUEST_URL, mPhotoListUrl);
                            i.putExtra(ConstantVariables.IS_ALBUM_PHOTO_REQUEST, true);
                            i.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, mCurrentSelectedModule);
                            i.putExtras(bundle);
                            startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);
                        }

                    }
                });
        mRecyclerView.setAdapter(mRecyclerViewAdapter);

    }

    /**
     * Method to launch user profile activity when photo uploaded successfully or there is an error.
     *
     * @param successMessage success or error message
     */
    public void launchUserProfileOnSuccessOrError(String successMessage) {
        mAppConst.hideProgressDialog();
        SnackbarUtils.displaySnackbarLongWithListener(mRootView, successMessage,
                new SnackbarUtils.OnSnackbarDismissListener() {
                    @Override
                    public void onSnackbarDismissed() {

                        // TODO recheck this if we can implement this by finishing current activity
                        // TODO and refresh the launching activity.
//                        Intent intent = new Intent();
//                        ((Activity)mContext).setResult(ConstantVariables.VIEW_PAGE_EDIT_CODE, intent);
//                        ((Activity)mContext).finish();

                        Bundle bundle = new Bundle();
                        Intent intent = GlobalFunctions.getIntentForModule(mContext, mSubjectId, mSubjectType, null);
                        if (mSubjectType.equals(ConstantVariables.MLT_MENU_TITLE)) {
                            intent.putExtra(ConstantVariables.LISTING_TYPE_ID, getArguments().getInt(ConstantVariables.LISTING_TYPE_ID));
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtras(bundle);
                        intent.putExtra(ConstantVariables.IS_PHOTO_UPLOADED, true);
                        startActivityForResult(intent, ConstantVariables.CONTENT_COVER_EDIT_CODE);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
    }

    /**
     * Method to set scroll listener on recycler view.
     */
    public void setOnScrollListener() {

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();
                int lastVisibleCount = mLayoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;
                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mPageNumber)
                            < mGridViewImages.getTotalPhotoCount()) {
                        mPhotoUrls.add(null);
                        mRecyclerViewAdapter.notifyItemInserted(mPhotoUrls.size() - 1);

                        mPageNumber = mPageNumber + 1;
                        String url = UrlUtil.VIEW_PHOTOS_URL + "?subject_id=" + mSubjectId + "&subject_type="
                                + mSubjectType + "&page=" + mPageNumber + "&limit=" + AppConstant.LIMIT ;
                        loadMorePhotos(url);
                        isLoading = true;
                    }
                }


            }
        });
    }

    /**
     * Method to send request on the server to load the photos response.
     *
     * @param url Url of calling request.
     */
    public void sendRequest(String url) {

        mPageNumber = 1;

        mAppConst.getJsonResponseFromUrl(url + "&page=" + mPageNumber, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mPhotoDetails.clear();
                mPhotoUrls.clear();
                mBody = jsonObject;
                mProgressBar.setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                isVisibleToUser = true;

                mTotalItemCount = mBody.optInt("totalPhotoCount");
                if (mTotalItemCount == 0) {
                    mTotalItemCount = mBody.optInt("totalItemCount");
                }
                if (mOnFragmentDataChangeListener != null) {
                    mOnFragmentDataChangeListener.onFragmentTitleUpdated(PhotoFragment.this, mTotalItemCount);
                }

                mCanUpload = mBody.optInt("canUpload");
                mPhotosJsonArray = mBody.optJSONArray("albumPhotos");
                if (mPhotosJsonArray == null) {
                    mPhotosJsonArray = mBody.optJSONArray("images");
                }

                JSONObject reactionsData = mBody.optJSONObject("reactions");
                if(reactionsData != null ){
                    PreferencesUtils.updateReactionsEnabledPref(mContext, reactionsData.optInt("reactionsEnabled"));
                    PreferencesUtils.storeReactions(mContext, reactionsData.optJSONObject("reactions"));
                }
                mGridViewImages.setTotalPhotoCount(mTotalItemCount);

                    /*
                    Show No Photo Message if there is not any photos in the Group/Event
                     */
                if (mTotalItemCount == 0) {
                    mInfoView.setVisibility(View.VISIBLE);
                    mNoPhotoMessage.setVisibility(View.VISIBLE);
                    switch (mCurrentSelectedModule) {
                        case ConstantVariables.GROUP_MENU_TITLE:
                            mNoPhotoMessage.setText(getString(R.string.no_photo_message_group));
                            break;
                        case ConstantVariables.EVENT_MENU_TITLE:
                        case ConstantVariables.ADVANCED_EVENT_MENU_TITLE:
                            mNoPhotoMessage.setText(getString(R.string.no_photo_message_event));
                            break;
                        case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                            mNoPhotoMessage.setText(getString(R.string.no_photo_message_channel));
                            break;
                        default:
                            mNoPhotoMessage.setText(getString(R.string.no_photo_message));
                            break;
                    }
                } else {
                    mInfoView.setVisibility(View.GONE);
                }

                    /*
                    Show Images in Grid View
                     */
                if (mPhotosJsonArray != null) {
                    addPhotosToList();
                } else {
//                        mRecyclerView.setVisibility(View.GONE);
                }

                mRecyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                SnackbarUtils.displaySnackbar(mRootView, message);
            }
        });

    }

    /**
     * Method to load more photos on scrolling.
     *
     * @param url Url of calling request.
     */
    public void loadMorePhotos(String url) {

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mPhotoUrls.remove(mPhotoUrls.size() - 1);
                mRecyclerViewAdapter.notifyItemRemoved(mPhotoUrls.size());
                mBody = jsonObject;
                mPhotosJsonArray = mBody.optJSONArray("albumPhotos");
                addPhotosToList();
                mRecyclerViewAdapter.notifyItemInserted(mPhotoUrls.size());
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    /**
     * Method to add photos into the list.
     */
    public void addPhotosToList() {

        if (mPhotosJsonArray != null) {
            for (int i = 0; i < mPhotosJsonArray.length(); i++) {

                JSONObject imageUrlsObj = mPhotosJsonArray.optJSONObject(i);
                String imgIcon = imageUrlsObj.optString("image");
                JSONArray menuArray = imageUrlsObj.optJSONArray("menu");
                String albumTitle = imageUrlsObj.optString("album_title");
                String owner_title = imageUrlsObj.optString("user_title");
                String normalImgUrl = imageUrlsObj.optString("image");
                String image_title = imageUrlsObj.optString("title");
                String image_desc = imageUrlsObj.optString("description");
                int photo_id = imageUrlsObj.optInt("photo_id");
                int album_id = imageUrlsObj.optInt("album_id");
                int likeCount = imageUrlsObj.optInt("like_count");
                int commentCount = imageUrlsObj.optInt("comment_count");
                int isLiked = imageUrlsObj.optInt("is_like");
                String reactions = imageUrlsObj.optString("reactions");
                String contentUrl = imageUrlsObj.optString("content_url");
                boolean likeStatus = false;
                if (isLiked == 1) {
                    likeStatus = true;
                }
                mPhotoDetails.add(new PhotoListDetails(albumTitle, owner_title,
                        image_title, image_desc, photo_id, normalImgUrl, likeCount,
                        commentCount, likeStatus, (menuArray != null ? menuArray.toString() : null), reactions, null, contentUrl));

//                mRequestMoreDataUrl = AppConstant.DEFAULT_URL + "albums/photo/list?" +
//                        "album_id="+album_id+"&limit=" + AppConstant.LIMIT;

                mPhotoUrls.add(new ImageViewList(imgIcon));
                isLoading = false;
            }
        }
    }

    /**
     * Method to start photo uploading activity.
     */
    public void startPhotoUploading() {
        Intent intent = new Intent(mContext, PhotoUploadingActivity.class);
        startActivityForResult(intent, ConstantVariables.REQUEST_IMAGE);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Method to check permission for the photo access.
     */
    public void checkPermission() {
        if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ConstantVariables.WRITE_EXTERNAL_STORAGE);
        } else {
            startPhotoUploading();
        }

    }

    @Override
    public void onUploadResponse(JSONObject jsonObject, boolean isRequestSuccessful) {
        String message;
        if (isRequestSuccessful) {
            message = getResources().
                    getQuantityString(R.plurals.photo_upload_msg,
                            mSelectPath.size(),
                            mSelectPath.size());
            onRefresh();
        } else {
            message = jsonObject.optString("message");
        }
        SnackbarUtils.displaySnackbarLongTime(mRootView, message);
    }

    @Override
    public void onRefresh() {
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                sendRequest(mPhotoListUrl);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (AppCompatActivity) context;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // if the result is capturing Image
        switch (requestCode) {
            case ConstantVariables.VIEW_LIGHT_BOX:

                switch (resultCode) {
                    case ConstantVariables.LIGHT_BOX_DELETE:
                        onRefresh();
                        break;

                    case ConstantVariables.LIGHT_BOX_EDIT:
                        if (data.getBooleanExtra(ConstantVariables.IS_CONTENT_EDITED, false)) {
                            onRefresh();
                        } else {
                            mPhotoDetails = (ArrayList<PhotoListDetails>) data.getSerializableExtra(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST);
                        }
                        break;
                }
                break;

            case ConstantVariables.REQUEST_IMAGE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mSelectPath = data.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
                        // uploading the photos to server
                        new UploadFileToServerUtils(mContext, mPhotoUploadUrl, mSelectPath, this).execute();
                        break;

                    case Activity.RESULT_CANCELED:
                        break;

                    default:
                        // failed to capture image
                        Toast.makeText(mContext, getResources().getString(R.string.image_capturing_failed),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    startPhotoUploading();
                } else {
                    // If user deny the permission popup
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    } else {
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(mContext, mRootView,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;
        }
    }

}
