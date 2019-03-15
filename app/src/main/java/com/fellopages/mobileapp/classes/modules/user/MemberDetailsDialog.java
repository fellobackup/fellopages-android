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

package com.fellopages.mobileapp.classes.modules.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MemberDetailsDialog extends DialogFragment {

    // Member variables.
    private Context mContext;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private TextView tvTitle;
    private Dialog mDialog;
    private String mViewDetailsUrl, mTitle, mUserTitle;
    private int mLoadingPageNo = 1;
    private boolean mIsNeedToDismissDialog = false, isLoading = false, isNeedToShowAdminApprovalMessage = false;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private MemberDetailsAdapter mMemberDetailsAdapter;
    private AppConstant mAppConst;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Getting arguments.
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mTitle = bundle.getString(ConstantVariables.TITLE);
            mUserTitle = bundle.getString(ConstantVariables.USER_TITLE);
            mViewDetailsUrl = bundle.getString(ConstantVariables.URL_STRING) + "&limit=" + AppConstant.LIMIT;
            isNeedToShowAdminApprovalMessage = bundle.getBoolean("is_admin_approval_required")
                    && !bundle.getBoolean("admin_approve");
        }

        //Initializing members.
        mContext = getActivity();
        mBrowseItemList = new ArrayList<>();
        mAppConst = new AppConstant(mContext);
        mBrowseList = new BrowseListItems();

        // Showing alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Dialog_NoTitle);

        // Get the layout inflater. Inflate and set the layout for the dialog
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        mRootView = inflater.inflate(R.layout.fragment_members_view, null);

        builder.setView(mRootView)
                // Add cancel button
                .setNegativeButton(R.string.close_listing_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        mDialog = builder.create();
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(mDialog.getWindow().getAttributes());
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mDialog.setCancelable(true);
        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                AlertDialog alertDialog = (AlertDialog) dialog;
                alertDialog.getButton(Dialog.BUTTON_NEGATIVE).setTypeface(null, Typeface.BOLD);
            }
        });
        mDialog.show();
        mDialog.getWindow().setAttributes(layoutParams);

        // Initializing views.
        initializeViews();
        makeRequest();
        return mDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mIsNeedToDismissDialog) {
            dismiss();
        }
    }

    /**
     * Method to initialize views.
     */
    public void initializeViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        tvTitle = mRootView.findViewById(R.id.title);
        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.setMinimumHeight(AppConstant.getDisplayMetricsHeight(mContext));
        mMemberDetailsAdapter = new MemberDetailsAdapter(mContext, mBrowseItemList, MemberDetailsDialog.this);
        mRecyclerView.setAdapter(mMemberDetailsAdapter);
        setScrollListener();
    }

    /**
     * Method to apply scroll listener on recycler view.
     */
    public void setScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleCount = linearLayoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT* mLoadingPageNo)
                            < mBrowseList.getmTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
                        String url = mViewDetailsUrl + "&page=" + mLoadingPageNo;

                        isLoading = true;
                        loadMoreData(url);
                    }

                }
            }
        });
    }

    /**
     * Method to send request to server for the list of member who reacted on the post.
     */
    public void makeRequest() {
        mLoadingPageNo = 1;

        mAppConst.getJsonResponseFromUrl(mViewDetailsUrl + "&page=1", new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(mTitle);
                addDataToList(jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(mTitle);
                mRootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        });
    }

    /**
     * Method to load more data on scrolling.
     * @param url Url of load more request.
     */
    public void loadMoreData(String url) {
        //add progress , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add("progress");
        mMemberDetailsAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mMemberDetailsAdapter.notifyItemRemoved(mBrowseItemList.size());
                addDataToList(jsonObject);
                mMemberDetailsAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                try {
                    Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method to set member list into adapter.
     *
     * @param jsonObject JsonObject which contains the members.
     */
    public void addDataToList(JSONObject jsonObject) {
        JSONArray responseArray = jsonObject.optJSONArray("response");
        mBrowseList.setmTotalItemCount(jsonObject.optInt("totalItemCount"));
        if (responseArray != null && responseArray.length() > 0) {
            mRootView.findViewById(R.id.message_layout).setVisibility(View.GONE);
            addAdminApprovalMessage();
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject userObject = responseArray.optJSONObject(i);
                int userId = userObject.optInt("user_id");
                String userName = userObject.optString("displayname");
                String userImage = userObject.optString("image_profile");
                String commentDescription = userObject.optString("comments");
                mBrowseItemList.add(new BrowseListItems(userName, userImage, commentDescription, userId));
            }

            if (mBrowseItemList.size() > 1) {
                mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));
            }

            mMemberDetailsAdapter.notifyDataSetChanged();

        } else if (isNeedToShowAdminApprovalMessage) {
            addAdminApprovalMessage();

        } else {
            mRecyclerView.setVisibility(View.GONE);
            mRootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mRootView.findViewById(R.id.message_layout).getLayoutParams();
            layoutParams.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp),
                    0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp));
            mRootView.findViewById(R.id.message_layout).setLayoutParams(layoutParams);
            TextView errorIcon = mRootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = mRootView.findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf007");
            errorMessage.setText(mUserTitle + " " + mContext.getResources().getString(R.string.no_user_verified));
        }
    }

    /**
     * Method to show admin approval message when its enabled from admin panel setting.
     */
    private void addAdminApprovalMessage() {
        if (isNeedToShowAdminApprovalMessage && PreferencesUtils.getUserDetail(mContext) != null) {
            try {
                JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                mBrowseItemList.add(0, new BrowseListItems(userDetail.optString("displayname"),
                        userDetail.getString("image_profile"),
                        mContext.getResources().getString(R.string.admin_approval_msg) + " " + mUserTitle
                                + " " + mContext.getResources().getString(R.string.approved_by),
                        userDetail.getInt("user_id")));
                mMemberDetailsAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
