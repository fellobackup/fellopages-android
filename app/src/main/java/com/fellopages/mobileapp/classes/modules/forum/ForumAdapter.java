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

package com.fellopages.mobileapp.classes.modules.forum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.ads.admob.AdMobViewHolder;
import com.fellopages.mobileapp.classes.common.ads.FacebookAdViewHolder;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.List;


public class ForumAdapter extends ArrayAdapter<Object> implements View.OnClickListener, OnMenuClickResponseListener{

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_AD = 1;
    private static final int TYPE_MAX_COUNT = TYPE_AD + 1;

    private String mTAG;
    private Context mContext;
    private List<Object> mBrowseItemList;
    private int mLayoutResID;
    private View mRootView;
    private String convertedDate, creatorTextFormat, detail;
    private int mPosition;
    private BrowseListItems listItems;
    private BrowseListItems mBrowseList;
    private GutterMenuUtils mGutterMenuUtils;
    private ImageLoader mImageLoader;

    /**
     * Public constructor of ForumAdapter to add data to the list
     *
     * @param context Context of calling class.
     * @param layoutResourceID Layout resource id which is going to inflate on ListView
     * @param listItem ListItems which are added to the inflating layout
    */
    public ForumAdapter(Context context, int layoutResourceID,List<Object> listItem,String TAG) {

        super(context, layoutResourceID, listItem);
        this.mContext = context;
        this.mLayoutResID = layoutResourceID;
        this.mBrowseItemList = listItem;
        mBrowseList = new BrowseListItems();
        this.mTAG = TAG;
        mGutterMenuUtils = new GutterMenuUtils(context);
        mImageLoader = new ImageLoader(mContext);
    }
    @Override
    public int getItemViewType(int position) {
        return mBrowseItemList.get(position) instanceof BrowseListItems ? TYPE_ITEM :  TYPE_AD;
    }
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        mRootView = convertView;
        final ListItemHolder listItemHolder;
        FacebookAdViewHolder facebookAdViewHolder = null;
        AdMobViewHolder adMobViewHolder = null;
        int type = getItemViewType(position);

        if (mRootView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemHolder = new ListItemHolder();
            switch (type) {
                case TYPE_ITEM:
                    mRootView = inflater.inflate(mLayoutResID, parent, false);

                    switch (mTAG) {

                        case "ForumHome":

                            listItemHolder.forumTitle = (TextView) mRootView.findViewById(R.id.forum_title);
                            listItemHolder.forumIcon = (TextView) mRootView.findViewById(R.id.forum_icon);
                            listItemHolder.forumIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                            listItemHolder.forumPostTopicCount = (TextView) mRootView.findViewById(R.id.forum_post_topic);
                            listItemHolder.categoryTitle = (TextView) mRootView.findViewById(R.id.category_title);
                            listItemHolder.forumTitleAndDescription = (LinearLayout) mRootView.findViewById(R.id.forum_title_des);
                            break;

                        case "ForumProfile":

                            listItemHolder.lastPostBy = (TextView) mRootView.findViewById(R.id.last_post_by);
                            listItemHolder.lastPostTime = (TextView) mRootView.findViewById(R.id.last_post_time);
                            listItemHolder.totalReplies = (TextView) mRootView.findViewById(R.id.total_replies);
                            listItemHolder.forumProfileImage = (ImageView) mRootView.findViewById(R.id.forum_profileImage);
                            listItemHolder.topicTitle = (TextView) mRootView.findViewById(R.id.topic_title);
                            listItemHolder.isTopicClose = (TextView) mRootView.findViewById(R.id.topic_close_icon);
                            listItemHolder.lastPostBy.setTag(position);
                            listItemHolder.lastPostBy.setOnClickListener(this);
                            break;

                        case "ForumView":

                            listItemHolder.topicPostOwner = (TextView) mRootView.findViewById(R.id.topic_post_by_owner);
                            listItemHolder.topicPostImage = (ImageView) mRootView.findViewById(R.id.topic_post_by_image);
                            listItemHolder.ownerTotalPostWithDate = (TextView) mRootView.findViewById(R.id.owner_post_and_date);
                            listItemHolder.mOptionIcon = (ImageView) mRootView.findViewById(R.id.topicOptionsIcon);
                            listItemHolder.postBody = (TextView) mRootView.findViewById(R.id.topic_content);
                            listItemHolder.linearLayout = (LinearLayout) mRootView.findViewById(R.id.forum_post_body);
                            listItemHolder.postEditInfo = (TextView) mRootView.findViewById(R.id.post_edit_info);
                            listItemHolder.mOptionIcon.setTag(position);
                            listItemHolder.topicPostOwner.setTag(position);
                            listItemHolder.postEditInfo.setTag(position);
                            listItemHolder.topicPostOwner.setOnClickListener(this);
                            break;

                    }
                    mRootView.setTag(listItemHolder);
                    break;
                case TYPE_AD:

                    if(ConstantVariables.ENABLE_ADMOB == 0) {
                        mRootView = inflater.inflate(R.layout.fb_ad_item_list, parent, false);
                        facebookAdViewHolder = new FacebookAdViewHolder(mRootView);
                        mRootView.setTag(facebookAdViewHolder);
                    }else {
                        mRootView = inflater.inflate(R.layout.admob_install_list, parent, false);
                        adMobViewHolder = new AdMobViewHolder(mRootView);
                        mRootView.setTag(adMobViewHolder);
                    }

                    break;
            }

        } else {
            switch (type) {
                case TYPE_ITEM:
                    listItemHolder = (ListItemHolder) mRootView.getTag();

                    if (listItemHolder.mOptionIcon != null) {
                        listItemHolder.mOptionIcon.setTag(position);
                    }
                    if (listItemHolder.isTopicClose != null) {
                        listItemHolder.isTopicClose.setVisibility(View.GONE);
                    }
                    if (listItemHolder.postEditInfo != null) {
                        listItemHolder.postEditInfo.setTag(position);
                        listItemHolder.postEditInfo.setVisibility(View.GONE);
                    }
                    if (listItemHolder.lastPostBy != null) {
                        listItemHolder.lastPostBy.setTag(position);
                    }
                    if (listItemHolder.topicPostOwner != null) {
                        listItemHolder.topicPostOwner.setTag(position);
                    }
                    if (listItemHolder.linearLayout != null) {
                        listItemHolder.linearLayout.setTag(position);
                    }
                    break;

                case TYPE_AD:
                    listItemHolder = null;
                    if(ConstantVariables.ENABLE_ADMOB == 0) {
                        facebookAdViewHolder = (FacebookAdViewHolder) mRootView.getTag();
                    }else {
                        adMobViewHolder = (AdMobViewHolder) mRootView.getTag();
                    }
                    break;

                default:
                    listItemHolder = null;
            }
        }

        switch (type) {
            case TYPE_ITEM:
                //Set items in list

                listItems = (BrowseListItems) this.mBrowseItemList.get(position);
                listItemHolder.mOptionsArray = listItems.getForumMenuArray();
                listItemHolder.mListItemId = listItems.getPostId();

                switch (mTAG) {

                    case "ForumHome":

                        //Set category title by checking categoryId
                        if (listItems.getForumId() == 0) {

                            listItemHolder.categoryTitle.setText(listItems.getCategoryTitle());
                            listItemHolder.categoryTitle.setVisibility(View.VISIBLE);
                            listItemHolder.forumTitleAndDescription.setVisibility(View.GONE);
                            listItemHolder.categoryTitle.setClickable(false);
                            listItemHolder.categoryTitle.setEnabled(false);
                            listItemHolder.categoryTitle.setOnClickListener(null);
                        } else {
                            listItemHolder.categoryTitle.setVisibility(View.GONE);
                            listItemHolder.forumTitleAndDescription.setVisibility(View.VISIBLE);
                        }

                        listItemHolder.forumIcon.setText("\uf086");
                        listItemHolder.forumTitle.setText(listItems.getForumTitle());
                        String postText = mContext.getResources().getQuantityString(R.plurals.total_post,
                                listItems.getPostCount());
                        String topicText = mContext.getResources().getQuantityString(R.plurals.total_topic,
                                listItems.getTopicCount());
                        creatorTextFormat = mContext.getResources().getString(R.string.forum_post_with_topic);
                        detail = String.format(creatorTextFormat, listItems.getPostCount(),
                                postText, listItems.getTopicCount(), topicText);
                        listItemHolder.forumPostTopicCount.setText(Html.fromHtml(detail));
                        break;

                    case "ForumProfile":

                        String replyText = mContext.getResources().getQuantityString(R.plurals.total_replies,
                                listItems.getTotalReplies());

                        // Adding close icon to the topic which is closed.
                        if (listItems.getIsTopicClose() == 1) {
                            listItemHolder.isTopicClose.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                            listItemHolder.isTopicClose.setText("\uf023 ");
                            listItemHolder.isTopicClose.setVisibility(View.VISIBLE);
                        }

                        //Checking total replies are null or not
                        if (listItemHolder.totalReplies != null) {
                            listItemHolder.totalReplies.setText(String.format(mContext.getResources().
                                            getString(R.string.forum_replies_count),
                                    listItems.getTotalReplies(), replyText));
                        }
                        listItemHolder.topicTitle.setText(listItems.getTopicTitle());


                        if (listItems.getDisplayName() != null && !listItems.getDisplayName().isEmpty()) {
                            listItemHolder.lastPostBy.setText(" " + listItems.getDisplayName());
                        } else {
                            listItemHolder.lastPostBy.setText(" " + mContext.getResources().getString(R.string.deleted_member_text));
                        }
                        mImageLoader.setImageForUserProfile(listItems.getTopicOwnerImage(), listItemHolder.forumProfileImage);
                        convertedDate = AppConstant.convertDateFormat(mContext.getResources(),
                                listItems.getTopicModifiedDate());
                        listItemHolder.lastPostTime.setText(Html.fromHtml(convertedDate));
                        break;

                    case "ForumView":

                        if (listItems.getPostByName() != null && !listItems.getPostByName().isEmpty()) {
                            listItemHolder.topicPostOwner.setText(listItems.getPostByName());
                        } else {
                            listItemHolder.topicPostOwner.setText(mContext.getResources().getString(R.string.deleted_member_text));
                        }

                        convertedDate = AppConstant.convertDateFormat(mContext.getResources(),
                                listItems.getPostCreationDate());
                        String textPost = mContext.getResources().getQuantityString(R.plurals.total_post,
                                listItems.getOwnerPostCount());
                        creatorTextFormat = mContext.getResources().getString(R.string.creator_view_with_date_and_post);
                        detail = String.format(creatorTextFormat, listItems.getOwnerPostCount(),
                                textPost, convertedDate);
                        listItemHolder.ownerTotalPostWithDate.setText(Html.fromHtml(detail));
                        mImageLoader.setImageForUserProfile(listItems.getPostByImage(), listItemHolder.topicPostImage);

                        String postBody = listItems.getPostBody();

                        //TODO in future
//              listItemHolder.postBody.loadDataWithBaseURL(null, postBody, "text/html", "utf-8", null);

                        // <Start>Code to get the images and data from the html body using Jsoup library <Start>
                        Document doc;
                        doc = Jsoup.parse(postBody);

                        Element link1 = doc.select("img").first();

                        if (link1 != null) {

                            listItemHolder.linearLayout.setVisibility(View.VISIBLE);

                            if (listItemHolder.linearLayout != null) {
                                listItemHolder.linearLayout.removeAllViews();
                            }
                            LinearLayout.LayoutParams layoutParams = CustomViews.getCustomWidthHeightLayoutParams(
                                    (int)mContext.getResources().getDimension(R.dimen.forum_linear_layout_params_width),
                                    (int)mContext.getResources().getDimension(R.dimen.feed_attachment_image_height));
                            layoutParams.setMargins((int)mContext.getResources().getDimension(R.dimen.forum_layout_params),
                                    (int)mContext.getResources().getDimension(R.dimen.margin_2dp),
                                    (int)mContext.getResources().getDimension(R.dimen.forum_layout_params),
                                    (int)mContext.getResources().getDimension(R.dimen.forum_layout_params));

                            int no = doc.select("img").size();
                            for (int i = 0; i < no; i++) {

                                Element element = doc.select("img").get(i);
                                ImageView imageView = new ImageView(mContext);
                                imageView.setLayoutParams(layoutParams);
                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                String imageSource = element.attr("src");
                                mImageLoader.setAlbumPhoto(imageSource, imageView);
                                listItemHolder.linearLayout.addView(imageView);
                            }

                        } else {
                            listItemHolder.linearLayout.setVisibility(View.GONE);
                        }

                        if (!postBody.isEmpty()) {
                            listItemHolder.postBody.setVisibility(View.VISIBLE);
                            listItemHolder.postBody.setText(Html.fromHtml(postBody.replaceAll("<img.+?>", "")));
                            listItemHolder.postBody.setMovementMethod(LinkMovementMethod.getInstance());
                        } else {
                            listItemHolder.postBody.setVisibility(View.GONE);
                        }

                        // <End>Code to get the images and data from the html body using Jsoup library <End>


                        //Checking post is edited or not then set data accordingly
                        if (listItems.getPostEditId() != 0) {
                            listItemHolder.postEditInfo.setVisibility(View.VISIBLE);
                            convertedDate = AppConstant.convertDateFormat(mContext.getResources(),
                                    listItems.getPostModifiedDate());
                            creatorTextFormat = mContext.getResources().getString(R.string.create_view_post_edit_info_format);
                            if (listItems.getPostEditByDisplayName() != null) {
                                detail = String.format(creatorTextFormat,
                                        mContext.getResources().getString(R.string.create_view_post_edit_info),
                                        listItems.getPostEditByDisplayName(),
                                        mContext.getResources().getString(R.string.event_date_info), convertedDate);
                            } else {
                                detail = String.format(creatorTextFormat, mContext.getResources().
                                                getString(R.string.create_view_post_edit_info),
                                        mContext.getResources().getString(R.string.deleted_member_text),
                                        mContext.getResources().getString(R.string.event_date_info), convertedDate);
                            }

                            int index1 = detail.indexOf(mContext.getResources().getString(R.string.by_text)) + 3;
                            int index2;
                            if (listItems.getPostEditByDisplayName() != null && !listItems.getPostEditByDisplayName().isEmpty()) {
                                index2 = index1 + listItems.getPostEditByDisplayName().length();
                            } else {
                                index2 = index1+ mContext.getResources().getString(R.string.deleted_member_text).length();
                            }

                            listItemHolder.postEditInfo.setMovementMethod(LinkMovementMethod.getInstance());
                            listItemHolder.postEditInfo.setText(Html.fromHtml(detail), TextView.BufferType.SPANNABLE);
                            Spannable mySpannable = (Spannable) listItemHolder.postEditInfo.getText();
                            ClickableSpan myClickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View view) {
                                    mPosition = (int) view.getTag();
                                    BrowseListItems listItem = (BrowseListItems) mBrowseItemList.get(mPosition);
                                    if (listItem.getPostEditByUserId() != 0) {
                                        Intent intent = new Intent(mContext, userProfile.class);
                                        intent.putExtra(ConstantVariables.USER_ID, listItem.getPostEditByUserId());
                                        ((Activity) mContext).startActivityForResult(intent, ConstantVariables.
                                                USER_PROFILE_CODE);
                                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right,
                                                R.anim.slide_out_left);
                                    }
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setUnderlineText(false);
                                    ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                                }
                            };
                            mySpannable.setSpan(myClickableSpan, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        }

                        if (listItemHolder.mOptionsArray != null) {

                            listItemHolder.mOptionIcon.setVisibility(View.VISIBLE);
                            //Setting on click listener on optionIcon
                            mGutterMenuUtils.setOnMenuClickResponseListener(this);
                            listItemHolder.mOptionIcon.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(final View view) {

                                    mPosition = (int) view.getTag();
                                    mGutterMenuUtils.showPopup(view, listItemHolder.mOptionsArray,
                                            mPosition, mBrowseItemList, ConstantVariables.FORUM_MENU_TITLE);
                                }
                            });
                        } else {
                            listItemHolder.mOptionIcon.setVisibility(View.GONE);
                        }

                        break;
                }
                break;
            case TYPE_AD:
                if(ConstantVariables.ENABLE_ADMOB == 0 ) {
                    if (facebookAdViewHolder.mNativeAd != null) {
                        facebookAdViewHolder.mNativeAd.unregisterView();
                    }
                    facebookAdViewHolder.mNativeAd = (NativeAd) mBrowseItemList.get(position);

                    FacebookAdViewHolder.inflateAd(facebookAdViewHolder.mNativeAd,
                            facebookAdViewHolder.adView, mContext, false);
                }else {
                    AdMobViewHolder.inflateAd(mContext,
                            (NativeAppInstallAd) mBrowseItemList.get(position),adMobViewHolder.mAdView);
                }
                break;
        }

        return mRootView;
    }

    @Override
    public int getCount() {
        return mBrowseItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void onClick(View view) {
        int id = view.getId();
        mPosition = (int) view.getTag();
        BrowseListItems listItem = (BrowseListItems) this.mBrowseItemList.get(mPosition);
        Intent intent;
        switch (id) {
            case R.id.last_post_by:
                if (listItem.getTopicLastPostByUserId() != 0) {
                    intent = new Intent(mContext, userProfile.class);
                    intent.putExtra(ConstantVariables.USER_ID, listItem.getTopicLastPostByUserId());
                    ((Activity) mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case R.id.topic_post_by_owner:
                if (listItem.getPostedByUserId() != 0) {
                    intent = new Intent(mContext, userProfile.class);
                    intent.putExtra(ConstantVariables.USER_ID, listItem.getPostedByUserId());
                    ((Activity) mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
        }
    }

    @Override
    public void onItemDelete(int position) {

        mBrowseItemList.remove(position);
        mBrowseList.setPostTotalItemCount(mBrowseList.getPostTotalItemCount() - 1);
        notifyDataSetChanged();
    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {
        mBrowseItemList.set(position, itemList);
        notifyDataSetChanged();

    }

    private static class ListItemHolder {

        //ForumHome items
        TextView categoryTitle, forumTitle, forumPostTopicCount, forumIcon;
        LinearLayout forumTitleAndDescription;

        //ForumProfile items
        TextView topicTitle, lastPostBy, lastPostTime, totalReplies, postBody;
        TextView isTopicClose;
        ImageView forumProfileImage;

        //ForumView items
        TextView topicPostOwner, ownerTotalPostWithDate, postEditInfo;
        ImageView topicPostImage, mOptionIcon;
        JSONArray mOptionsArray;
        int mListItemId;
        LinearLayout linearLayout;
        // TODO in future
//        WebView postBody;
    }

}
