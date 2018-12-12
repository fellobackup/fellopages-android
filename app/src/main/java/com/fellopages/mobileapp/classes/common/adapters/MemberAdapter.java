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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemDeleteResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.viewholder.ProgressViewHolder;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;


import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;


import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MemberAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        OnMenuClickResponseListener{

    Context mContext;
    int mLayoutResID;
    List<Object> mBrowseItemlist;
    AppConstant mAppConst;
    BrowseListItems listItems;
    BrowseListItems mBrowseList;
    JSONArray mMenuJsonArray;
    int mCanEdit, mContentId, mRsvp;
    String mRsvpTitle = "";
    String redirectUrl, dialogueMessage, dialogueTitle, dialogueButton, successMessage, actionUrl,
            mCurrentSelectedModule;
    Map<String, String> postParams = new HashMap<>();
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private OnItemClickListener mOnItemClickListener;
    private OnItemDeleteResponseListener mOnItemDeleteResponseListener;
    private GutterMenuUtils mGutterMenuUtils;
    private ImageLoader mImageLoader;


    public MemberAdapter(Context context, int layoutResourceID, List<Object> listItem,
                         BrowseListItems browseList, int contentId, String currentSelectedModule,
                         OnItemClickListener onItemClickListener, OnItemDeleteResponseListener onItemDeleteResponseListener) {

        mContext = context;
        mLayoutResID = layoutResourceID;
        mBrowseItemlist = listItem;
        mBrowseList = browseList;
        mContentId = contentId;
        mOnItemClickListener = onItemClickListener;
        mOnItemDeleteResponseListener = onItemDeleteResponseListener;

        mAppConst = new AppConstant(context);
        mGutterMenuUtils = new GutterMenuUtils(context);

     /* Fetch Current Selected Module*/
        mCurrentSelectedModule = currentSelectedModule;
        if (mCurrentSelectedModule == null || mCurrentSelectedModule.isEmpty()) {
            mCurrentSelectedModule = PreferencesUtils.getCurrentSelectedModule(mContext);
        }
        mImageLoader = new ImageLoader(mContext);

    }

    @Override
    public int getItemViewType(int position) {
        return mBrowseItemlist.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType == VIEW_ITEM){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutResID, viewGroup, false);
           return new ListItemHolder(view);
        }else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.progress_item, viewGroup, false);

            return new ProgressViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {

        if (viewHolder instanceof ListItemHolder) {

            listItems = (BrowseListItems) this.mBrowseItemlist.get(position);
            final ListItemHolder listItemHolder = (ListItemHolder) viewHolder;
            listItemHolder.mUserId = listItems.getmUserId();

            ((ListItemHolder) viewHolder).memberBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
                }
            });

            /*
            Set Data in the List View Items
             */
            mImageLoader.setImageForUserProfile(listItems.getmBrowseImgUrl(), listItemHolder.mListImage);

            listItemHolder.mListImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listItemHolder.mUserId != 0){
                        Intent userProfileIntent = new Intent(mContext, userProfile.class);
                        userProfileIntent.putExtra("user_id", listItemHolder.mUserId);
                        ((Activity)mContext).startActivityForResult(userProfileIntent, ConstantVariables.
                                USER_PROFILE_CODE);
                        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });

            listItemHolder.mOwnerName.setText(listItems.getmBrowseListOwnerTitle());

            listItemHolder.mOwnerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(listItemHolder.mUserId != 0){
                        Intent userProfileIntent = new Intent(mContext, userProfile.class);
                        userProfileIntent.putExtra("user_id", listItemHolder.mUserId);
                        ((Activity)mContext).startActivityForResult(userProfileIntent, ConstantVariables.
                                USER_PROFILE_CODE);
                        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });

            if (mCurrentSelectedModule.equals("core_main_group")){
                mCanEdit = mBrowseList.getCanEdit();
                if (listItems.getmStaff() != null && !listItems.getmStaff().isEmpty()) {
                    int padding5 = (int) mContext.getResources().getDimension(R.dimen.padding_5dp);
                    int padding10 = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);
                    listItemHolder.mOwnerName.setPadding(0, padding5, 0, padding5);
                    listItemHolder.staff.setPadding(padding10, padding5, 0, padding5);

                    listItemHolder.staff.setVisibility(View.VISIBLE);
                    listItemHolder.staff.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    listItemHolder.staff.setText("(" + listItems.getmStaff() + ")");
                    if (mCanEdit != 0) {
                        listItemHolder.staff.setText("(" + listItems.getmStaff() + " \uf040 " + ")");
                        listItemHolder.staff.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                redirectUrl = AppConstant.DEFAULT_URL + "groups/member/edit/" + mContentId;
                                postParams.put("user_id", String.valueOf(listItemHolder.mUserId));
                                dialogueMessage = mContext.getResources().getString(R.string.edit_title_dialogue_message);
                                dialogueTitle = mContext.getResources().getString(R.string.edit_title_dialogue_title);
                                dialogueButton = mContext.getResources().getString(R.string.edit_title_dialogue_button);
                                successMessage = mContext.getResources().getString(R.string.edit_title_dialogue_success_message);
                                editMemberStaff(redirectUrl, dialogueMessage, dialogueTitle, dialogueButton,
                                        successMessage, postParams, position);
                            }
                        });
                    }
                }else{
                    listItemHolder.staff.setVisibility(View.GONE);
                }
            }else if(mCurrentSelectedModule.equals("core_main_event")){
                if(listItems.getmIsOwner() != 0){
                    listItemHolder.staff.setText("(" + "owner" + ")");
                }

                mRsvp = listItems.getmRsvp();

                switch (mRsvp){
                    case 0:
                        mRsvpTitle = "Not Attending";
                        break;
                    case 1:
                        mRsvpTitle = "May be Attending";
                        break;
                    case 2:
                        mRsvpTitle = "Attending";
                        break;
                    case 3:
                        mRsvpTitle = "Awaiting Reply";
                        break;
                }

                if(!mRsvpTitle.isEmpty()){
                    listItemHolder.mRsvpInfo.setText(mRsvpTitle);
                    listItemHolder.mRsvpInfo.setVisibility(View.VISIBLE);
                } else {
                    listItemHolder.mRsvpInfo.setVisibility(View.GONE);
                }

            }

            listItemHolder.menuJsonArray = mMenuJsonArray = listItems.getMenuArray();
            listItemHolder.mOptionIcon.setTag(position);
            mGutterMenuUtils.setOnMenuClickResponseListener(this);
            if(mMenuJsonArray == null){
                listItemHolder.mOptionIconLayout.setVisibility(View.GONE);
            }else {
                listItemHolder.mOptionIconLayout.setVisibility(View.VISIBLE);
                listItemHolder.mOptionIcon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        mGutterMenuUtils.showPopup(view, listItemHolder.menuJsonArray,
                                (int) view.getTag(), mBrowseItemlist, mCurrentSelectedModule);
                    }
                });
            }

            // Showing friendship options if condition matches.
            if (listItems.getmFriendShipType() != null && !listItems.getmFriendShipType().isEmpty() &&
                    listItemHolder.menuJsonArray == null && listItems.getmUserId() != 0) {

                listItemHolder.mMemberOptions.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                listItemHolder.mMemberOptions.setVisibility(View.VISIBLE);
                switch (listItems.getmFriendShipType()) {
                    case "add_friend":
                    case "accept_request":
                    case "member_follow":
                        listItemHolder.mMemberOptions.setText("\uf234");
                        break;
                    case "remove_friend":
                    case "member_unfollow":
                        listItemHolder.mMemberOptions.setText("\uf235");
                        break;
                    case "cancel_request":
                    case "cancel_follow":
                        listItemHolder.mMemberOptions.setText("\uf00d");
                        break;
                }

                listItemHolder.mMemberOptions.setTag(position);
                listItemHolder.mMemberOptions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int position = (int) view.getTag();
                        BrowseListItems browseListItems = (BrowseListItems) mBrowseItemlist.get(position);
                        mGutterMenuUtils.setPopUpForFriendShipType(position, browseListItems, null, mCurrentSelectedModule);
                    }
                });

            } else {
                listItemHolder.mMemberOptions.setVisibility(View.GONE);
            }

        }else{
            ProgressViewHolder.inflateProgressBar(((ProgressViewHolder) viewHolder).progressView);
        }

    }

    @Override
    public int getItemCount() {
        return mBrowseItemlist.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onItemDelete(int position) {

        mBrowseItemlist.remove(position);
        notifyDataSetChanged();
        if (mOnItemDeleteResponseListener != null) {
            mOnItemDeleteResponseListener.onItemDelete(getItemCount(), false);
        }
    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {

        switch (menuName) {
            case "friendship_type":
            case "make_admin":
            case "remove_admin":
            case "make_officer":
            case "demote_officer":
                mBrowseItemlist.set(position, itemList);
                notifyDataSetChanged();
                break;

            case "cancel_invite":
            case "approved_member":
                mBrowseItemlist.remove(position);
                notifyDataSetChanged();
                if (mOnItemDeleteResponseListener != null) {
                    mOnItemDeleteResponseListener.onItemDelete(getItemCount(), false);
                }
                break;

            default:
                if (mOnItemDeleteResponseListener != null) {
                    //TODO Recheck it as we are implementing fragment view now.
                    Intent membersIntent = ((Activity) mContext).getIntent();
                    ((Activity) mContext).finish();
                    mContext.startActivity(membersIntent);
                }
                break;
        }
    }

    public static class ListItemHolder extends RecyclerView.ViewHolder{

        ImageView mListImage;
        TextView mOwnerName,staff, mRsvpInfo, mMemberOptions;
        ImageView mOptionIcon;
        JSONArray menuJsonArray;
        int mUserId;
        View memberBlock;
        LinearLayout mOptionIconLayout;

        public ListItemHolder(View itemView) {
            super(itemView);
            memberBlock = itemView;
            mOwnerName = (TextView) itemView.findViewById(R.id.ownerTitle);
            mListImage = (ImageView) itemView.findViewById(R.id.ownerImage);
            mOptionIcon = (ImageView) itemView.findViewById(R.id.optionIcon);
            mOptionIconLayout = (LinearLayout) itemView.findViewById(R.id.option_icon_layout);
            staff = (TextView) itemView.findViewById(R.id.staff);
            mRsvpInfo = (TextView) itemView.findViewById(R.id.rsvpInfo);
            mMemberOptions = (TextView) itemView.findViewById(R.id.memberOption);
        }
    }

        /*
    Code For Deleting a Entry..
     */

    public void editMemberStaff(String url, String message, String title, String buttonTitle,
                                String showsuccessMessage, Map<String, String> urlParams, final int position){

        actionUrl = url;
        successMessage = showsuccessMessage;
        postParams = urlParams;

        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setMessage(message);
        alertBuilder.setTitle(title);

        final EditText input = new EditText(mContext);
        input.setId(R.id.update_staff);

        alertBuilder.setView(input);

        alertBuilder.setPositiveButton(buttonTitle, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                mAppConst.hideKeyboard();
                postParams.put("title", input.getText().toString());
                actionUrl = mAppConst.buildQueryString(actionUrl, postParams);

                mAppConst.putResponseForUrl(actionUrl, postParams, new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        /* Show Message */
                        Toast.makeText(mContext, successMessage, Toast.LENGTH_LONG).show();
                        BrowseListItems listItems = (BrowseListItems) mBrowseItemlist.get(position);
                        listItems.setmStaff(input.getText().toString());
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {

                    }
                });
            }
        });

        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertBuilder.create().show();
    }

}
