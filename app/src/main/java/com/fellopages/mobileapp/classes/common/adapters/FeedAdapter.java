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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Currency;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.facebook.ads.NativeAd;
import com.fellopages.mobileapp.classes.common.multimediaselector.bean.Folder;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.MapActivity;
import com.fellopages.mobileapp.classes.common.activities.ReportEntry;
import com.fellopages.mobileapp.classes.common.activities.SearchActivity;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.ads.FacebookAdViewHolder;
import com.fellopages.mobileapp.classes.common.ads.admob.AdMobViewHolder;
import com.fellopages.mobileapp.classes.common.ads.communityAds.CommunityAdsHolder;
import com.fellopages.mobileapp.classes.common.ads.communityAds.RemoveAdHolder;
import com.fellopages.mobileapp.classes.common.ads.sponsoredStories.SponsoredStoriesHolder;
import com.fellopages.mobileapp.classes.common.fragments.VideoLightBoxFragment;
import com.fellopages.mobileapp.classes.common.interfaces.OnFeedDisableCommentListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnFilterSelectedListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnGifPlayListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnPinPostListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnStaticMapLoadListener;
import com.fellopages.mobileapp.classes.common.ui.ActionIconButton;
import com.fellopages.mobileapp.classes.common.ui.ActionIconThemedTextView;
import com.fellopages.mobileapp.classes.common.ui.BezelImageView;
import com.fellopages.mobileapp.classes.common.ui.CustomGridLayoutManager;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.NameView;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.ui.ThemedTextView;
import com.fellopages.mobileapp.classes.common.ui.hashtag.OnTagClickListener;
import com.fellopages.mobileapp.classes.common.ui.hashtag.TagSelectingTextView;
import com.fellopages.mobileapp.classes.common.ui.viewholder.ProgressViewHolder;
import com.fellopages.mobileapp.classes.common.utils.AddPeopleList;
import com.fellopages.mobileapp.classes.common.utils.BitMapCreatorUtil;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.CacheUtils;
import com.fellopages.mobileapp.classes.common.utils.CommunityAdsList;
import com.fellopages.mobileapp.classes.common.utils.CustomTabUtil;
import com.fellopages.mobileapp.classes.common.utils.FeedList;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.Smileys;
import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.SponsoredStoriesList;
import com.fellopages.mobileapp.classes.common.utils.StaticMap;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.MainActivity;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.FeedsFragment;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.MultiPhotoRecyclerAdapter;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.SingleFeedPage;
import com.fellopages.mobileapp.classes.modules.advancedActivityFeeds.Status;
import com.fellopages.mobileapp.classes.modules.advancedVideos.AdvVideoUtil;
import com.fellopages.mobileapp.classes.modules.likeNComment.Comment;
import com.fellopages.mobileapp.classes.modules.likeNComment.Likes;
import com.fellopages.mobileapp.classes.modules.messages.CreateNewMessage;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoLightBoxActivity;
import com.fellopages.mobileapp.classes.modules.photoLightBox.PhotoListDetails;
import com.fellopages.mobileapp.classes.modules.store.adapters.SimpleSheetAdapter;
import com.fellopages.mobileapp.classes.modules.store.utils.SheetItemModel;
import com.fellopages.mobileapp.classes.modules.story.StoryBrowseAdapter;
import com.fellopages.mobileapp.classes.modules.story.StoryUtils;
import com.fellopages.mobileapp.classes.modules.story.StoryView;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;
import com.fellopages.mobileapp.classes.modules.video.VideoUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;

import github.ankushsachdeva.emojicon.EmojiconTextView;


public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        View.OnClickListener, OnTagClickListener, OnMenuClickResponseListener,
        AdapterView.OnItemClickListener {

    public final String TAG = "FeedAdapter";
    private final int FEED_TYPE = 0, HIDE_ALL_TYPE = 1, HIDDEN_TYPE = 2, FOOTER_TYPE = 3, HEADER_TYPE = 4,
            AD_FB = 5, AD_ADMOB = 7, PEOPLE_SUGGESTION = 8, COMMUNITY_ADS = 9, SPONSORED_STORIES = 10,
            REMOVE_AD_TYPE = 11, PREVIEW_TYPE = 12;
    public Map<String, String> mPostParams;
    protected ArrayAdapter<String> _adapter;
    protected Map<String, String> mFilterKeysMap;
    boolean mIsSingleFeed, isPostMenusSet = false, isPhotoFeed = false, isAdapterSet = false;
    int columnWidth, itemPosition, mPhotoPosition;
    TagSelectingTextView mTagSelectingTextView;
    private Context mContext;
    private List<Object> mFeedItemList;
    private FeedList mSelectedFeedList;
    private FeedList mFeedItem;
    private int mLayoutResID;
    private AppConstant mAppConst;
    private SocialShareUtil socialShareUtil;
    private LayoutInflater inflater;
    private Typeface fontIcon;
    private String mProfileIconImage, mUserName, mGreetingId, mUserId;
    private int mLoggedInUserId;
    private ArrayList<Integer> mHiddenFeeds = new ArrayList<>();
    private ArrayList<Integer> mAllHiddenFeeds = new ArrayList<>();
    private ArrayList<Integer> mRemoveAds;
    private Map<String, Integer> mSubjectPositionList = new HashMap<>();
    private List<ImageViewList> mPhotoUrls, reactionsImages, mSellImageList;
    private ArrayList<PhotoListDetails> mPhotoList;
    private MultiPhotoRecyclerAdapter mMultiPhotoRecyclerAdapter;
    private ArrayList<PhotoListDetails> mPhotoDetails;
    private CustomImageAdapter sellPhotoAdapter;
    private ImageAdapter adapter, reactionsAdapter;
    private OnFilterSelectedListener mFilterSelectedListener;
    private OnFeedDisableCommentListener mFeedDisableCommentListener;
    private OnPinPostListener mOnPinPostListener;
    private JSONObject mFeedPostMenus;
    private JSONArray mFeedFiltersArray;
    private List mIncludedModulesList, mDeletedModulesList;
    private EditText mCommentEditText;
    private int mFeedSubjectId, mClickedFeedPosition;
    private String mSubjectType, mModuleName;
    private FeedsFragment mFeedsFragment;
    private int mReactionsEnabled;
    private JSONObject mReactions, bannerObject;
    private GutterMenuUtils mGutterMenuUtils;
    private ArrayList<JSONObject> mReactionsArray;
    private List<AddPeopleList> mAddPeopleList;
    private AddPeopleAdapter mAddPeopleAdapter;
    private Dialog mDialog;
    private ImageLoader mImageLoader;
    private SimpleSheetAdapter mSheetAdapter;
    private BottomSheetDialog mBottomSheetDialog;
    private OnGifPlayListener mOnGifPlayListener;
    private FeedList mFeedPreviewItem;
    private boolean isSaveFeeds = false;


    public FeedAdapter(Context context, int layoutResourceID, List<Object> listItem, boolean isSinglePageFeed,
                       EditText commentEditText, String subjectType, int subjectId, String moduleName,
                       int feedPosition, boolean hidePhotos, boolean isSaveFeeds, FeedsFragment feedsFragment) {


        mContext = context;
        mLayoutResID = layoutResourceID;
        mFeedItemList = listItem;

        fontIcon = GlobalFunctions.getFontIconTypeFace(mContext);
        mAppConst = new AppConstant(context);
        socialShareUtil = new SocialShareUtil(context);
        mGutterMenuUtils = new GutterMenuUtils(context, true);

        mPostParams = new HashMap<>();
        mAddPeopleList = new ArrayList<>();

        this.mIsSingleFeed = isSinglePageFeed;

        inflater = LayoutInflater.from(context);

        mSubjectType = subjectType;
        mFeedSubjectId = subjectId;
        mModuleName = moduleName;
        mClickedFeedPosition = feedPosition;

        _adapter = new ArrayAdapter<>(mContext, R.layout.simple_text_view);

        isPhotoFeed = hidePhotos;

        mFilterKeysMap = new HashMap<>();

        mIncludedModulesList = Arrays.asList(ConstantVariables.INCLUDED_MODULES_ARRAY);
        mDeletedModulesList = Arrays.asList(ConstantVariables.DELETED_MODULES);

        mCommentEditText = commentEditText;
        this.isSaveFeeds = isSaveFeeds;
        mFeedsFragment = feedsFragment;
        mTagSelectingTextView = new TagSelectingTextView();

        mRemoveAds = new ArrayList<>();
        mImageLoader = new ImageLoader(mContext);

    }

    public void setOnFeedDisableCommentListener(OnFeedDisableCommentListener onFeedDisableCommentListener) {
        this.mFeedDisableCommentListener = onFeedDisableCommentListener;
    }

    public void setOnGifPlayListener(OnGifPlayListener onGifPlayListener) {
        this.mOnGifPlayListener = onGifPlayListener;
    }

    /**
     * Return the View Type according to the position of the feed
     */

    @Override
    public int getItemViewType(int position) {
        if (mFeedItemList.get(position) instanceof FeedList) {
            // Header on 0th Position
            if (position == 0) {
                return HEADER_TYPE;
            } else if (((FeedList) mFeedItemList.get(position)).itemViewType == PREVIEW_TYPE) {
                return PREVIEW_TYPE;
            } else {
                if ((mHiddenFeeds.size() == 0 || !mHiddenFeeds.contains(position)) &&
                        (mAllHiddenFeeds.size() == 0 || !mAllHiddenFeeds.contains(position))) {
                    return FEED_TYPE;
                } else if (mAllHiddenFeeds.size() == 0 || !mAllHiddenFeeds.contains(position)) {
                    return HIDDEN_TYPE;
                } else {
                    return HIDE_ALL_TYPE;
                }
            }
        } else if (mFeedItemList.get(position) != null && mFeedItemList.get(position) instanceof ArrayList) {
            return PEOPLE_SUGGESTION;
        } else if (mFeedItemList.get(position) != null && !mFeedItemList.get(position).equals(ConstantVariables.FOOTER_TYPE)) {

            if (mRemoveAds.size() != 0 && mRemoveAds.contains(position)) {
                return REMOVE_AD_TYPE;
            } else {
                // To show Ads
                switch (ConstantVariables.FEED_ADS_TYPE) {
                    case ConstantVariables.TYPE_FACEBOOK_ADS:
                        return AD_FB;
                    case ConstantVariables.TYPE_GOOGLE_ADS:
                        return AD_ADMOB;
                    case ConstantVariables.TYPE_COMMUNITY_ADS:
                        return COMMUNITY_ADS;
                    case ConstantVariables.TYPE_SPONSORED_STORIES:
                        return SPONSORED_STORIES;
                }
            }
        } else {
            // Footer to show Loading bar on scroll
            return FOOTER_TYPE;
        }

        return 0;
    }

    /**
     * Return View Holder according to View Type
     *
     * @param viewType The type of view of a position in RecyclerView
     *                 <p>
     *                 FEED_TYPE:  If the view is for showing the feed content.
     *                 HIDE_ALL_TYPE or HIDDEN_TYPE:  View when a user clicks on option Hide this feed or
     *                 Hide All for this user.
     *                 HEADER_TYPE: to show the header of the feeds which contain Status Menu and Filters
     *                 FOOTER_TYPE: to show the loading bar when we scroll for loading more results in recyclerview.
     */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
//        Log.d("ListViewType ", viewType);
        switch (viewType) {
            case PREVIEW_TYPE:
            case FEED_TYPE:
                viewHolder = new ListItemHolder(inflater.inflate(mLayoutResID, viewGroup, false));
                break;

            case HIDE_ALL_TYPE:
            case HIDDEN_TYPE:
                viewHolder = new HiddenItemHolder(inflater.inflate(R.layout.hidden_feeds, viewGroup, false));
                break;

            case HEADER_TYPE:
                viewHolder = new HeaderViewHolder(mContext, inflater.inflate(R.layout.feeds_header, viewGroup, false));
                break;

            case AD_FB:
                viewHolder = new FacebookAdViewHolder(inflater.inflate(R.layout.feeds_ad_item_card, viewGroup, false));

                break;
            case AD_ADMOB:
                viewHolder = new AdMobViewHolder(inflater.inflate(R.layout.admob_feeds, viewGroup, false));
                break;

            case COMMUNITY_ADS:
                viewHolder = new CommunityAdsHolder(this,
                                                    inflater.inflate(R.layout.list_community_ads, viewGroup, false),
                                                    ConstantVariables.FEED_ADS_POSITION,
                                                    ConstantVariables.FEED_ADS_TYPE, mRemoveAds);
                break;

            case SPONSORED_STORIES:
                viewHolder = new SponsoredStoriesHolder(this,
                                                        inflater.inflate(R.layout.list_sponsored_stories_feed, viewGroup, false),
                                                        ConstantVariables.FEED_ADS_POSITION,
                                                        ConstantVariables.FEED_ADS_TYPE,
                                                        mRemoveAds);
                break;

            case PEOPLE_SUGGESTION:
                viewHolder = new PeopleSuggestionViewHolder(inflater.inflate(R.layout.people_suggestion_recycler_view, viewGroup, false));
                break;

            case REMOVE_AD_TYPE:
                viewHolder = new RemoveAdHolder(this, inflater.inflate(R.layout.remove_ads_layout,
                        viewGroup, false), mRemoveAds, mFeedItemList);
                break;

            default:
                viewHolder = new ProgressViewHolder(inflater.inflate(R.layout.progress_item, viewGroup, false));
                break;
        }

        return viewHolder;
    }

    /**
     * Set Data in views according to view type and holders.
     */

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        int type = getItemViewType(position);

        /* End */

        /* Start : To set data in feeds */
        switch (type) {
            case FEED_TYPE:
                mFeedItem = (FeedList) this.mFeedItemList.get(position);

                /* Start: Put Subject_id and position of the feed in
                 * HashMap for hide all by a user functionality
                 */

                if (mFeedItem != null) {
                    mSubjectPositionList.put(mFeedItem.getmSubjectId() + "-" + position, position);
                }

                final ListItemHolder listItemHolder = (ListItemHolder) viewHolder;

                listItemHolder.mPhotoAttachmentCount = mFeedItem.getmPhotoAttachmentCount();

                /* Start: Set Horizontal Scrolling in Images RecyclerView */
                CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(listItemHolder.mImagesGallery,
                        2, LinearLayoutManager.VERTICAL, false);
                listItemHolder.mImagesGallery.setLayoutManager(gridLayoutManager);

                gridLayoutManager.setSpanSizeLookup(new CustomGridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (listItemHolder.mPhotoAttachmentCount == 2) {
                            return 1;
                        } else {
                            if (position == 0) {
                                return 2;
                            } else {
                                return 1;
                            }
                        }
                    }
                });


                listItemHolder.mImagesGallery.setTag(listItemHolder);
                /* End */

                listItemHolder.mCounterView.setTag(position);
                listItemHolder.mWebUrl = mFeedItem.getmWebUrl();

                /* Share feed header work */
                if (mFeedItem.getmIsShareFeed()) {

                    /* Share feed icon */
                    if (mFeedItem.getmShareFeedIcon() != null && !mFeedItem.getmShareFeedIcon().isEmpty()) {
                        mImageLoader.setImageForUserProfile(mFeedItem.getmShareFeedIcon(), listItemHolder.mShareFeedIcon);
                    }

                    listItemHolder.mShareFeedIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());
                            int subjectId;
                            String subjectType;

                            if (feedList.getmShareFeedType() == 0) {
                                subjectType = "user";
                                subjectId = feedList.getmShareSubjectId();
                            } else {
                                subjectType = feedList.getmShareObjectType();
                                subjectId = feedList.getmShareObjectId();
                            }
                            attachmentClicked(null, subjectType, subjectId, listItemHolder, null);
                        }
                    });

                    /* Share feed title */
                    listItemHolder.mShareFeedTitle.setMovementMethod(LinkMovementMethod.getInstance());

                    HashMap<String, String> shareClickableParts = mFeedItem.getmShareClickableStringsList();

                    // Show Clickable Parts and Apply Click Listener to redirect
                    if (shareClickableParts != null && shareClickableParts.size() != 0) {

                        CharSequence title = Html.fromHtml(mFeedItem.getmShareFeedActionTitle());
                        SpannableString text = new SpannableString(title);
                        SortedSet<String> keys = new TreeSet<>(shareClickableParts.keySet());

                        int lastIndex = 0;
                        for (String key : keys) {

                            final String slug;
                            String[] keyParts = key.split("-");
                            final String attachment_type = keyParts[1];
                            final int attachment_id = Integer.parseInt(keyParts[2]);

                            if (keyParts.length >= 4) {
                                slug = keyParts[3];
                            } else {
                                slug = null;
                            }

                            final String value = shareClickableParts.get(key);

                            if (value != null && !value.isEmpty()) {
                                int i1 = title.toString().indexOf(value, lastIndex);
                                if (i1 != -1) {
                                    int i2 = i1 + value.length();
                                    if (lastIndex != -1) {
                                        lastIndex += value.length();
                                    }
                                    ClickableSpan myClickableSpan = new ClickableSpan() {
                                        @Override
                                        public void onClick(View widget) {
                                            redirectToActivity(value, slug, attachment_type, attachment_id, listItemHolder, null);
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            super.updateDrawState(ds);
                                            ds.setUnderlineText(false);
                                            ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                                        }
                                    };

                                    text.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                            }
                        }
                        listItemHolder.mShareFeedTitle.setText(text);
                    } else {
                        listItemHolder.mShareFeedTitle.setText(mFeedItem.getmFeedTitle());
                    }

                    /* Show Feed Share Time */
                    listItemHolder.mShareFeedTime.setText(AppConstant.convertDateFormat(mContext.getResources(), mFeedItem.getmShareDate()));

                    /* Share feed body */
                    if (mFeedItem.getmShareBody() != null && !mFeedItem.getmShareBody().isEmpty()) {
                        listItemHolder.mShareFeedBody.setText(mFeedItem.getmShareBody());
                        listItemHolder.mShareFeedBody.setMovementMethod(LinkMovementMethod.getInstance());
                        listItemHolder.mShareFeedBody.setVisibility(View.VISIBLE);
                    } else {
                        listItemHolder.mShareFeedBody.setVisibility(View.GONE);
                    }

                    listItemHolder.mShareFeedContainer.setVisibility(View.VISIBLE);
                    listItemHolder.mShareFeedDivider.setVisibility(View.VISIBLE);
                    int padding15 = (int) mContext.getResources().getDimension(R.dimen.padding_15dp);
                    listItemHolder.mFeedBlock.setPadding(padding15, 0, padding15, 0);
                } else {
                    listItemHolder.mShareFeedContainer.setVisibility(View.GONE);
                    listItemHolder.mShareFeedDivider.setVisibility(View.GONE);
                    listItemHolder.mShareFeedBody.setVisibility(View.GONE);
                    listItemHolder.mFeedBlock.setPadding(0, 0, 0, 0);
                }

            /* End share feed work */

            /* Start: Show Feed Icon Image */
                mImageLoader.setImageForUserProfile(mFeedItem.getmFeedIcon(), listItemHolder.mFeedProfileImage);

                listItemHolder.mFeedProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());
                        int subjectId;
                        String subjectType;

                        if (feedList.getmOwnerFeedType() == 0) {
                            subjectType = "user";
                            subjectId = feedList.getmSubjectId();
                        } else {
                            subjectType = feedList.getmObjectType();
                            subjectId = feedList.getmObjectId();
                        }
                        attachmentClicked(null, subjectType, subjectId, listItemHolder, null);

                    }
                });
            /* End Feed Icon work */

                // Showing pin icon
                if (mSubjectType != null && !mSubjectType.isEmpty()) {
                    RelativeLayout.LayoutParams titleViewParam = (RelativeLayout.LayoutParams) listItemHolder.llTitleView.getLayoutParams();
                    if (mFeedItem.isPostPinned()) {
                        listItemHolder.tvPinnedView.setVisibility(View.VISIBLE);
                        listItemHolder.tvPinnedView.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        listItemHolder.tvPinnedView.setText("\uf08d");
                        titleViewParam.addRule(RelativeLayout.LEFT_OF, listItemHolder.tvPinnedView.getId());
                        titleViewParam.addRule(RelativeLayout.START_OF, listItemHolder.tvPinnedView.getId());
                    } else {
                        listItemHolder.tvPinnedView.setVisibility(View.GONE);
                        titleViewParam.addRule(RelativeLayout.LEFT_OF, listItemHolder.mFeedMenusIcon.getId());
                        titleViewParam.addRule(RelativeLayout.START_OF, listItemHolder.mFeedMenusIcon.getId());
                    }
                }

                // Showing post schedule time.
                if (mFeedItem.getSchedulePostTime() != null && !mFeedItem.getSchedulePostTime().isEmpty() && !mFeedItem.getSchedulePostTime().equals("null")) {
                    listItemHolder.tvPostSchedule.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                    listItemHolder.tvPostSchedule.setVisibility(View.VISIBLE);
                    listItemHolder.tvPostSchedule.setText("\uf017 " + mContext.getResources().getString(R.string.feed_will_post) + " " + mFeedItem.getSchedulePostTime());
                } else {
                    listItemHolder.tvPostSchedule.setVisibility(View.GONE);
                }


            /* Start: Show Feed Title with Clickable Links */
                listItemHolder.mFeedTitle.setMovementMethod(LinkMovementMethod.getInstance());

                HashMap<String, String> clickableParts = mFeedItem.getmClickableStringsList();
                final HashMap<Integer, String> videoInfo = mFeedItem.getmVideoInfo();
                listItemHolder.mFeedObject = mFeedItem.getmFeedObject();
                listItemHolder.mPopularReactionsArray = mFeedItem.getmFeedReactions();
                JSONObject feelingObject = mFeedItem.getFeelingObject();

                // Show Clickable Parts and Apply Click Listener to redirect
                if (clickableParts != null && clickableParts.size() != 0) {

                    CharSequence title = Html.fromHtml(mFeedItem.getmFeedTitle());
                    SpannableString text = new SpannableString(title);
                    SortedSet<String> keys = new TreeSet<>(clickableParts.keySet());

                    int lastIndex = 0;
                    for (String key : keys) {

                        final String slug;
                        String[] keyParts = key.split("-");
                        final String attachment_type = keyParts[1];
                        final int attachment_id = Integer.parseInt(keyParts[2]);

                        if (keyParts.length >= 4) {
                            slug = keyParts[3];
                        } else {
                            slug = null;
                        }

                        final String value = clickableParts.get(key);

                        if (value != null && !value.isEmpty()) {
                            int i1 = title.toString().indexOf(value, lastIndex);
                            if (i1 != -1) {
                                int i2 = i1 + value.length();
                                if (lastIndex != -1) {
                                    lastIndex += value.length();
                                }
                                ClickableSpan myClickableSpan = new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        redirectToActivity(value, slug, attachment_type, attachment_id, listItemHolder, videoInfo);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setUnderlineText(false);
                                        ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                                    }
                                };

                                text.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                    }

                    // Feeling icon.
                    if (feelingObject != null && feelingObject.length() > 0
                            && feelingObject.optString("iconUrl") != null) {
                        if (CacheUtils.getInstance(mContext).getLru().get(feelingObject.optString("iconUrl")) != null) {
                            Drawable feelingDrawable = new BitmapDrawable(mContext.getResources(), CacheUtils.getInstance(mContext).getLru().get(feelingObject.optString("iconUrl")));
                            feelingDrawable.setBounds(0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp),
                                    mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp));
                            text.setSpan(new ImageSpan(feelingDrawable),
                                    mFeedItem.getStartIndex() + mFeedItem.getIsTranslation().length() + 1,
                                    mFeedItem.getStartIndex() + mFeedItem.getIsTranslation().length() + 2,
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                            text.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View widget) {

                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setUnderlineText(false);
                                    ds.setColor(ContextCompat.getColor(mContext, R.color.body_text_1));
                                }
                            }, mFeedItem.getStartIndex(), mFeedItem.getEndIndex(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        } else {
                            new BitMapCreatorUtil(mContext, feelingObject.optString("iconUrl"), new BitMapCreatorUtil.OnBitmapLoadListener() {
                                @Override
                                public void onBitMapLoad(Bitmap bitmap) {
                                    notifyItemChanged(listItemHolder.getAdapterPosition());
                                }
                            }).execute();
                        }

                    }

                    listItemHolder.mFeedTitle.setText(text);
                } else {
                    listItemHolder.mFeedTitle.setText(mFeedItem.getmFeedTitle());
                }

            /* End Feed Title work */

                renderFeedBody(listItemHolder, mFeedItem);

                if (mFeedItem.getHashTagString() == null || mFeedItem.getHashTagString().isEmpty()) {
                    listItemHolder.hashTagView.setVisibility(View.GONE);
                } else {
                    listItemHolder.hashTagView.setVisibility(View.VISIBLE);
                    listItemHolder.hashTagView.setText(mTagSelectingTextView.
                            addClickablePart(mFeedItem.getHashTagString(), this, 0,
                                    ConstantVariables.DEFAULT_HASHTAG_COLOR), BufferType.SPANNABLE);
                }


                /* Show Feed Post Time with privacy icon*/
                String privacyIcon = "";
                if (mFeedItem.getPrivacyIcon() != null && !mFeedItem.getPrivacyIcon().isEmpty()) {
                    switch (mFeedItem.getPrivacyIcon()) {
                        case "everyone":
                            privacyIcon = "\uf0ac";
                            break;
                        case "networks":
                            privacyIcon = "\uf0c0";
                            break;
                        case "friends":
                            privacyIcon = "\uf007";
                            break;
                        case "onlyme":
                            privacyIcon = "\uf023";
                            break;
                        case "network_list_custom":
                            privacyIcon = "\uf0c0";
                            break;
                        case "friend_list_custom":
                            privacyIcon = "\uf03a";
                            break;
                        default:
                            if (mFeedItem.getPrivacyIcon().contains("network")) {
                                privacyIcon = "\uf0c0";
                            } else if (mFeedItem.getPrivacyIcon().matches("-?\\d+(\\.\\d+)?")) {
                                privacyIcon = "\uf03a";
                            } else {
                                privacyIcon = "\uf0ac";
                            }
                            break;
                    }
                    privacyIcon = " \u2022 " + privacyIcon;
                }
                listItemHolder.mFeedPostedTime.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                listItemHolder.mFeedPostedTime.setText(AppConstant.convertDateFormat(mContext.getResources(), mFeedItem.getmFeedPostTime()) + privacyIcon);
            /* End Feed Post Time work */

            /* Show Feed Menus Icon with options Delete Feed, Hide feed etc. */

                if (mFeedItem.getmFeedMenusArray() != null && mFeedItem.getmFeedMenusArray().length() != 0) {
                    mGutterMenuUtils.setOnMenuClickResponseListener(this);
                    if (mFeedItem.getmIsShareFeed()) {
                        listItemHolder.mShareFeedMenuIcon.setVisibility(View.VISIBLE);
                        listItemHolder.mFeedMenusIcon.setVisibility(View.GONE);
                        listItemHolder.mShareFeedMenuIcon.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                FeedList selectedFeedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());

                                mGutterMenuUtils.showPopup(view, selectedFeedList.getmFeedMenusArray(),
                                        listItemHolder.getAdapterPosition(), mFeedItemList, "home");
                            }
                        });
                    } else {
                        listItemHolder.mFeedMenusIcon.setVisibility(View.VISIBLE);
                        listItemHolder.mFeedMenusIcon.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                FeedList selectedFeedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());

                                mGutterMenuUtils.showPopup(view, selectedFeedList.getmFeedMenusArray(),
                                        listItemHolder.getAdapterPosition(), mFeedItemList, "home");
                            }
                        });
                    }

                } else {
                    listItemHolder.mFeedMenusIcon.setVisibility(View.GONE);
                    listItemHolder.mShareFeedMenuIcon.setVisibility(View.GONE);
                }
            /* End Show Feed Menus Icon */


            /* Start: Show Attachment Info */
                if (isPhotoFeed) {
                    listItemHolder.mAttachmentView.setVisibility(View.GONE);
                    listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
                    listItemHolder.ivSticker.setVisibility(View.GONE);
                    listItemHolder.mImagesGallery.setVisibility(View.GONE);
                    listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                    listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
                } else {

                    listItemHolder.mFeedType = mFeedItem.getmFeedType();

                    if (mFeedItem.getmAttachmentCount() != 0) {

                        listItemHolder.mFeedAttachmentArray = mFeedItem.getmFeedAttachmentArray();
                        listItemHolder.mFeedAttachmentType = mFeedItem.getmFeedAttachmentType();

                        if (listItemHolder.mFeedAttachmentArray != null
                                && listItemHolder.mFeedAttachmentArray.length() != 0) {
                            try {

                                listItemHolder.mAttachmentView.setVisibility(View.VISIBLE);
                                listItemHolder.mPhotoAttachmentCount = mFeedItem.getmPhotoAttachmentCount();

                                renderAttachment(position, listItemHolder, mFeedItem);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            listItemHolder.mAttachmentView.setVisibility(View.GONE);
                            listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
                            listItemHolder.ivSticker.setVisibility(View.GONE);
                            listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                            listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
                            listItemHolder.mImagesGallery.setVisibility(View.GONE);
                        }

                    } else if ((listItemHolder.mFeedType.equals("sitetagcheckin_status") ||
                            listItemHolder.mFeedType.equals("sitetagcheckin_checkin"))
                            && mFeedItem.getmAttachmentCount() == 0
                            && (bannerObject == null || bannerObject.length() <= 0)) {
                        renderCheckinAttachment(position, listItemHolder, mFeedItem);

                    } else {
                        listItemHolder.mAttachmentView.setVisibility(View.GONE);
                        listItemHolder.mAttachmentImage.setVisibility(View.GONE);
                        listItemHolder.mAttachmentTitle.setVisibility(View.GONE);
                        listItemHolder.tvPrice.setVisibility(View.GONE);
                        listItemHolder.tvLocation.setVisibility(View.GONE);
                        listItemHolder.mAttachmentPreviewBlock.setVisibility(View.GONE);
                        listItemHolder.mAttachmentImage.setScaleType(ImageView.ScaleType.FIT_XY);
                        listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
                        listItemHolder.ivSticker.setVisibility(View.GONE);
                        listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                        listItemHolder.mImagesGallery.setVisibility(View.GONE);
                        listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
                    }
                }
            /* End: Show Attachment Info */


            /* Start: Set Like and Comment Count Info */

                if (mFeedItem.ismCanComment() != 0) {

                /* Show Like Count */
                    if (mFeedItem.getmLikeCount() > 0) {
                        listItemHolder.mCounterView.setVisibility(View.VISIBLE);
                        listItemHolder.mLikeCount.setVisibility(View.VISIBLE);

                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) listItemHolder.mCommentCount.getLayoutParams();
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                        listItemHolder.mCommentCount.setLayoutParams(layoutParams);

                        /**
                         * Show 3 popular reaction icons with the reaction count
                         */
                        if (mReactionsEnabled == 1 && listItemHolder.mPopularReactionsArray != null &&
                                listItemHolder.mPopularReactionsArray.length() != 0) {

                            listItemHolder.mCountSaperator.setVisibility(View.VISIBLE);
                            listItemHolder.mPopularReactionsView.setVisibility(View.VISIBLE);
                            listItemHolder.mPopularReactionsView.removeAllViews();

                            JSONArray reactionIds = listItemHolder.mPopularReactionsArray.names();

                            for (int i = 0; i < listItemHolder.mPopularReactionsArray.length() && i < 3; i++) {
                                String imageUrl = listItemHolder.mPopularReactionsArray.optJSONObject(reactionIds.optString(i)).
                                        optString("reaction_image_icon");
                                int reactionId = listItemHolder.mPopularReactionsArray.optJSONObject(reactionIds.optString(i)).
                                        optInt("reactionicon_id");
                                listItemHolder.mPopularReactionsView.addView(CustomViews
                                        .generateReactionImageView(mContext, reactionId, imageUrl));
                            }
                        } else {
                            listItemHolder.mCountSaperator.setVisibility(View.GONE);
                            listItemHolder.mPopularReactionsView.setVisibility(View.GONE);
                        }

                        setLikeCount(mFeedItem.getmIsLike(), mFeedItem.getmLikeCount(), listItemHolder);

                    } else {
                        listItemHolder.mLikeCount.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) listItemHolder.mCommentCount.getLayoutParams();
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                        listItemHolder.mCommentCount.setLayoutParams(layoutParams);
                        listItemHolder.mCountSaperator.setVisibility(View.GONE);
                        listItemHolder.mPopularReactionsView.setVisibility(View.GONE);
                    }

                    /* Show Comment Count */
                    if (mFeedItem.getmCommentCount() != 0 && ((!mIsSingleFeed || isPhotoFeed) || (mIsSingleFeed && listItemHolder.mPhotoAttachmentCount > 1))) {
                        listItemHolder.mCounterView.setVisibility(View.VISIBLE);
                        listItemHolder.mCommentCount.setVisibility(View.VISIBLE);
                        String commentText = mContext.getResources().getQuantityString(R.plurals.profile_page_comment,
                                mFeedItem.getmCommentCount());
                        listItemHolder.mCommentCount.setText(Html.fromHtml(String.format(
                                mContext.getResources().getString(R.string.comment_count_text),
                                mFeedItem.getmCommentCount(), commentText
                        )));
                    } else {
                        listItemHolder.mCommentCount.setVisibility(View.GONE);
                    }
                } else {
                    listItemHolder.mLikeCount.setVisibility(View.GONE);
                    listItemHolder.mCommentCount.setVisibility(View.GONE);
                    listItemHolder.mPopularReactionsView.setVisibility(View.GONE);
                    listItemHolder.mCountSaperator.setVisibility(View.GONE);
                }
            /* End: Set Like and Comment Count Info */


            /* Start: Show FooterMenus Like/Comment/Share */
                if (mFeedItem.getmFeedFooterMenus() != null && mFeedItem.getmFeedFooterMenus().length() != 0) {
                    listItemHolder.mFeedFooterMenusBlock.setVisibility(View.VISIBLE);

                    // Change the state of save feed button
                    int saveFeedOption = mFeedItem.getmIsSaveFeedOption();

                    if(saveFeedOption == 1){
                        listItemHolder.mSaveFeedBlock.setActivated(false);
                        listItemHolder.mSaveFeedButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                    } else {
                        listItemHolder.mSaveFeedBlock.setActivated(true);
                        listItemHolder.mSaveFeedButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
                    }

                    try {
                        if (mFeedItem.ismCanComment() != 0) {
                            // Show Like Option
                            // Create New TextView for Like Option and add this in mFeedFooterMenusBlock.
                            if (mFeedItem.getmIsLike() == 0) {
                                listItemHolder.mLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                                        ContextCompat.getDrawable(mContext, R.drawable.ic_thumbs_up),
                                        null, null, null);

                                int drawablePadding = mContext.getResources().getDimensionPixelSize(R.dimen.element_spacing_normal);
                                listItemHolder.mLikeButton.setCompoundDrawablePadding(drawablePadding);
                                listItemHolder.mLikeButton.setActivated(false);
                                listItemHolder.mLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
                                listItemHolder.mLikeButton.setText(mContext.getResources().getString(R.string.like_text));

                                listItemHolder.mReactionImage.setVisibility(View.GONE);
                            } else {
                                listItemHolder.mLikeButton.setActivated(true);
                                listItemHolder.mLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));

                                if (mReactionsEnabled == 1 && mFeedItem.getmMyFeedReactions() != null) {
                                    String reactionImage = mFeedItem.getmMyFeedReactions().optString("reaction_image_icon");

                                    listItemHolder.mReactionImage.setVisibility(View.VISIBLE);
                                    mImageLoader.setImageUrl(reactionImage, listItemHolder.mReactionImage);
                                    listItemHolder.mLikeButton.setCompoundDrawables(null, null, null, null);

                                    // listItemHolder.mLikeButton.setText(mFeedItem.getmMyFeedReactions().optString("caption"));
                                    // TODO: The text is replaced manually since the Like string is from the API and should be changed from there.
                                    // TODO: Restore the above commented code if the text from the API is changed.
                                    String likeText = mFeedItem.getmMyFeedReactions().optString("caption");
                                    if(likeText.toLowerCase().equals("like")){
                                        // change to Fello Like
                                        likeText = mContext.getResources().getString(R.string.like_text);
                                    }
                                    listItemHolder.mLikeButton.setText(likeText);
                                } else {
                                    listItemHolder.mReactionImage.setVisibility(View.GONE);
                                    listItemHolder.mLikeButton.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_thumbs_up), null, null, null);
                                    listItemHolder.mLikeButton.setText(mContext.getResources().getString(R.string.like_text));
                                }
                            }

                            listItemHolder.mLikeButton.setVisibility(View.VISIBLE);
                            listItemHolder.mLikeBlock.setTag(position);
                            listItemHolder.mCommentButton.setVisibility(View.VISIBLE);
                            listItemHolder.mCommentBlock.setTag(position);
                            listItemHolder.mLikeBlock.setVisibility(View.VISIBLE);
                            listItemHolder.mCommentBlock.setVisibility(View.VISIBLE);

                        } else {
                            // Hide Like and Comment if comments are not allowed.
                            listItemHolder.mLikeButton.setVisibility(View.GONE);
                            listItemHolder.mCommentButton.setVisibility(View.GONE);
                            listItemHolder.mLikeBlock.setVisibility(View.GONE);
                            listItemHolder.mCommentBlock.setVisibility(View.GONE);
                            listItemHolder.mReactionImage.setVisibility(View.GONE);
                        }

                        // Show Share Option
                        if (mFeedItem.getmShareAble() != 0 && mFeedItem.getmFeedFooterMenus().optJSONObject("share") != null) {
                            listItemHolder.mShareButton.setVisibility(View.VISIBLE);
                            listItemHolder.mShareBlock.setVisibility(View.VISIBLE);
                            listItemHolder.mShareButton.setText(mFeedItem.getmFeedFooterMenus().optJSONObject("share").getString("label").trim());
                            listItemHolder.mShareBlock.setTag(position);
                        } else {
                            listItemHolder.mShareButton.setVisibility(View.GONE);
                            listItemHolder.mShareBlock.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    listItemHolder.mFeedFooterMenusBlock.setVisibility(View.GONE);
                }
            /* End: Show FooterMenus Like/Comment/Share */



            /* Start: Click Listener on Comment Option */

                listItemHolder.mCommentBlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemPosition = (int) view.getTag();
                        FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());

                        if (feedList.getmPhotoAttachmentCount() > 1) {
                            showComments(true);
                        } else if (!mIsSingleFeed ) {
                            launchSingleFeedPage(listItemHolder);
                        } else {
                            mAppConst.showKeyboard();

                            if (mCommentEditText != null)
                                mCommentEditText.requestFocus();
                        }

                    }
                });

            /* End: Click Listener on Comment Option */

            /* Start: Click Listener on Like Comment Info */

                if (listItemHolder.mCounterView != null) {
                    listItemHolder.mCounterView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemPosition = (int) view.getTag();
                            FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());

                            if (feedList.getmPhotoAttachmentCount() > 1 || mIsSingleFeed) {
                                showComments(true);
                            } else if (!mIsSingleFeed ) {
                                launchSingleFeedPage(listItemHolder);
                            }
                        }
                    });

                    listItemHolder.mCounterView.setClickable((mFeedItem.ismCanComment() != 0));
                }

            /* End: Click Listener on Like Comment Info */

            /* Start: Click Listener on Like Menu */

                if (mReactionsEnabled == 1) {
                    listItemHolder.mLikeBlock.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {

                            itemPosition = (int) v.getTag();
                            final FeedList feedInfoList = (FeedList) mFeedItemList.get(itemPosition);

                            int[] location = new int[2];
                            listItemHolder.mCounterView.getLocationOnScreen(location);
                            RecyclerView reactionsRecyclerView = new RecyclerView(mContext);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                            reactionsRecyclerView.setHasFixedSize(true);
                            reactionsRecyclerView.setLayoutManager(linearLayoutManager);
                            reactionsRecyclerView.setItemAnimator(new DefaultItemAnimator());

                            final PopupWindow popUp = new PopupWindow(reactionsRecyclerView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            popUp.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape));
                            popUp.setTouchable(true);
                            popUp.setFocusable(true);
                            popUp.setOutsideTouchable(true);
                            popUp.setAnimationStyle(R.style.customDialogAnimation);

                            // Playing popup effect when user long presses on like button of a feed.
                            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                                SoundUtil.playSoundEffectOnReactionsPopup(mContext);
                            }

                            popUp.showAtLocation(reactionsRecyclerView, Gravity.TOP, location[0], location[1]);

                            if (mReactions != null && mReactionsArray != null) {

                                reactionsImages = new ArrayList<>();

                                for (int i = 0; i < mReactionsArray.size(); i++) {
                                    JSONObject reactionObject = mReactionsArray.get(i);
                                    String reaction_image_url = reactionObject.optJSONObject("icon").optString("reaction_image_icon");
                                    String caption = reactionObject.optString("caption");
                                    String reaction = reactionObject.optString("reaction");
                                    int reactionId = reactionObject.optInt("reactionicon_id");
                                    String reactionIconUrl = reactionObject.optJSONObject("icon").optString("reaction_image_icon");
                                    reactionsImages.add(new ImageViewList(reaction_image_url, caption, reaction, reactionId, reactionIconUrl));
                                }

                                reactionsAdapter = new ImageAdapter((Activity) mContext, reactionsImages, true,
                                        new OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {


                                                ImageViewList imageViewList = reactionsImages.get(position);
                                                String reaction = imageViewList.getmReaction();
                                                String caption = imageViewList.getmCaption();
                                                String reactionIcon = imageViewList.getmReactionIcon();
                                                int reactionId = imageViewList.getmReactionId();
                                                popUp.dismiss();

                                                /**
                                                 * If the user Presses the same reaction again then don't do anything
                                                 */
                                                if (feedInfoList.getmMyFeedReactions() != null) {
                                                    if (feedInfoList.getmMyFeedReactions().optInt("reactionicon_id") != reactionId) {
                                                        boolean isLike = feedInfoList.getmIsLike() == 1;
                                                        instantLike(listItemHolder, caption, reactionId, reactionIcon, isLike);
                                                        doLikeUnlike(listItemHolder.getAdapterPosition(), reaction, isLike);
                                                    }
                                                } else {
                                                    instantLike(listItemHolder, caption, reactionId, reactionIcon, false);
                                                    doLikeUnlike(listItemHolder.getAdapterPosition(), reaction, false);
                                                }
                                            }
                                        });

                                reactionsRecyclerView.setAdapter(reactionsAdapter);
                            }
                            return true;
                        }
                    });
                }

                listItemHolder.mLikeBlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        /**
                         * Apply animation on like button
                         */
                        listItemHolder.mLikeButton.startAnimation(AppConstant.getZoomInAnimation(mContext));
                        itemPosition = (int) view.getTag();

                        int reactionId = 0;
                        String reactionIcon = null;

                        if (mReactions != null) {
                            reactionId = mReactions.optJSONObject("like").optInt("reactionicon_id");
                            reactionIcon = mReactions.optJSONObject("like").optJSONObject("icon").optString("reaction_image_icon");
                        }

                        instantLike(listItemHolder, mContext.getResources().getString(R.string.like_text), reactionId, reactionIcon, false);
                        doLikeUnlike(itemPosition, null, false);
                    }

                });
            /* End: Click Listener on Like Menu */

            /* Start: Save Feed Block click listener */
                listItemHolder.mSaveFeedBlock.setTag(position);
                listItemHolder.mSaveFeedBlock.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        // Toast.makeText(view.getContext(), "Save Feed!", Toast.LENGTH_SHORT).show();

                        /**
                         * Apply animation on like button
                         */
                        listItemHolder.mSaveFeedBlock.startAnimation(AppConstant.getZoomInAnimation(mContext));
                        itemPosition = (int) view.getTag();

                        initiateSaveFeed(view, listItemHolder, itemPosition);
                        // doSaveUnsaveFeedIndicate(listItemHolder, itemPosition);
                    }
                });
            /* End: Save Feed Block click listener */

            /* Start: Click Listener on Share Menu */

                listItemHolder.mShareBlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String attachmentType = null, contentUrl = null;

                        itemPosition = (int) view.getTag();
                        final FeedList feedInfoList = (FeedList) mFeedItemList.get(itemPosition);
                        final JSONArray feedAttachmentArray = feedInfoList.getmFeedAttachmentArray();
                        final JSONObject shareJsonObject = feedInfoList.getmFeedFooterMenus().optJSONObject("share");
                        JSONObject feedObject = feedInfoList.getmFeedObject();
                        String title = null, image = null, url = null;

                        try {
                            url = AppConstant.DEFAULT_URL + shareJsonObject.getString("url");
                            JSONObject urlParams = shareJsonObject.optJSONObject("urlParams");
                            HashMap<String, String> shareParams = new HashMap<>();

                            if (urlParams != null && urlParams.length() != 0) {
                                JSONArray urlParamsKeys = urlParams.names();

                                for (int i = 0; i < urlParams.length(); i++) {
                                    String key = urlParamsKeys.getString(i);
                                    String value = urlParams.getString(key);
                                    shareParams.put(key, value);
                                }
                            }
                            if (shareParams.size() != 0) {
                                url = mAppConst.buildQueryString(url, shareParams);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (feedAttachmentArray != null && feedAttachmentArray.length() != 0) {
                            JSONObject feedAttachmentObject = feedAttachmentArray.optJSONObject(0);

                            if (feedAttachmentObject != null && feedAttachmentObject.length() != 0) {
                                title = feedAttachmentObject.optString("title");
                                JSONObject imgObject = feedAttachmentObject.optJSONObject("image_main");
                                if (imgObject != null)
                                    image = imgObject.optString("src");
                                attachmentType = feedAttachmentObject.optString("attachment_type");
                                contentUrl = feedAttachmentObject.optString("content_url");
                                if (contentUrl == null || contentUrl.isEmpty()) {
                                    contentUrl = feedAttachmentObject.optString("uri");
                                }
                            }
                        }
                        socialShareUtil.sharePost(view, title, image, url,
                                attachmentType,
                                (contentUrl != null && !contentUrl.isEmpty() ? contentUrl : feedObject.optString("url")));
                    }
                });
           /* End: Click Listener on Share Menu */

                break;

            case HIDE_ALL_TYPE:
            case HIDDEN_TYPE:
                mFeedItem = (FeedList) this.mFeedItemList.get(position);

                /* Start: Put Subject_id and position of the feed in
                 * HashMap for hide all by a user functionality
                 */

                if (mFeedItem != null) {
                    mSubjectPositionList.put(mFeedItem.getmSubjectId() + "-" + position, position);
                }
                // Show Hidden Type Feed
                final HiddenItemHolder hiddenItemHolder = (HiddenItemHolder) viewHolder;

                if (type == HIDDEN_TYPE && mFeedItem != null) {
                    String hideBodyText = mFeedItem.getmHiddenBodyText();
                    final String undoURl = mFeedItem.getmUndoHiddenFeedURl();
                    String undoText = mContext.getResources().getString(R.string.undo_text);
                    hiddenItemHolder.mHiddenFeedBody.setVisibility(View.VISIBLE);
                    hiddenItemHolder.mHiddenFeedBody.setMovementMethod(LinkMovementMethod.getInstance());
                    hiddenItemHolder.mHiddenFeedBody.setText(hideBodyText + " " + undoText, BufferType.SPANNABLE);

                    // Make Undo Clickable and Show the Feed again
                    Spannable mySpannable = (Spannable) hiddenItemHolder.mHiddenFeedBody.getText();
                    int start = hideBodyText.length() + 1;
                    int end = start + undoText.length();

                    ClickableSpan myClickableSpan = new ClickableSpan() {

                        @Override
                        public void onClick(View widget) {

                            mAppConst.showProgressDialog();
                            mAppConst.postJsonResponseForUrl(undoURl, mPostParams,
                                    new OnResponseListener() {
                                        @Override
                                        public void onTaskCompleted(JSONObject jsonObject) {
                                            mAppConst.hideProgressDialog();
                                            if (mHiddenFeeds.contains(viewHolder.getAdapterPosition())) {
                                                mHiddenFeeds.remove(mHiddenFeeds.indexOf(viewHolder.getAdapterPosition()));
                                            }
                                            mAllHiddenFeeds.clear();
                                            notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                            mAppConst.hideProgressDialog();

                                        }
                                    });
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                        }
                    };

                    mySpannable.setSpan(myClickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    // Set File a Report Option and Redirect to Report Activity
                    String hideAllName = mFeedItem.getmHideAllName();
                    if (hideAllName != null && hideAllName.equals("report")) {
                        hiddenItemHolder.mHiddenFeedOptions.setPadding(0, 0, 0, 0);
                        hiddenItemHolder.mHiddenFeedOptions.setText(mFeedItem.getmHideAllText());
                        hiddenItemHolder.mHiddenFeedOptions.setClickable(true);
                        final String hideAllUrl = mFeedItem.getmHideAllUrl();
                        hiddenItemHolder.mHiddenFeedOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent reportIntent = new Intent(mContext, ReportEntry.class);
                                reportIntent.putExtra(ConstantVariables.URL_STRING, hideAllUrl);
                                mContext.startActivity(reportIntent);
                                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });
                    }
                } else {
                    hiddenItemHolder.mHiddenFeedBody.setVisibility(View.GONE);
                    hiddenItemHolder.mHiddenFeedOptions.setVisibility(View.GONE);
                }

                break;
            case HEADER_TYPE:
                mFeedItem = (FeedList) this.mFeedItemList.get(position);

                /* Start: Put Subject_id and position of the feed in
                 * HashMap for hide all by a user functionality
                 */

                if (mFeedItem != null) {
                    mSubjectPositionList.put(mFeedItem.getmSubjectId() + "-" + position, position);
                }

                // Show Header Block with Status Menus and Filters
                final HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;

                assert mFeedItem != null;
                mFeedPostMenus = mFeedItem.getmFeedPostMenus();
                mFeedFiltersArray = mFeedItem.getmFeedFilterArray();
                boolean isNoFeed = mFeedItem.isNoFeed();
                mReactionsEnabled = mFeedItem.getmReactionsEnabled();
                mReactions = mFeedItem.getmReactions();

                if (mReactions != null) {
                    mReactionsArray = GlobalFunctions.sortReactionsObjectWithOrder(mReactions);
                }

                if (!mIsSingleFeed) {
                    if (PreferencesUtils.getUserDetail(mContext) != null) {
                        try {
                            JSONObject userDetail = new JSONObject(PreferencesUtils.getUserDetail(mContext));
                            mProfileIconImage = userDetail.getString("image_profile");
                            mLoggedInUserId = userDetail.getInt("user_id");
                            mUserName = userDetail.optString("displayname");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (mFeedPostMenus != null && mFeedPostMenus.length() != 0 && !mIsSingleFeed) {
                        if (mProfileIconImage != null && !mProfileIconImage.isEmpty()) {
                            mImageLoader.setImageForUserProfile(mProfileIconImage, headerViewHolder.mUserProfileImage);

                            headerViewHolder.mUserProfileImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent userProfileIntent = new Intent(mContext, userProfile.class);
                                    userProfileIntent.putExtra(ConstantVariables.USER_ID, mLoggedInUserId);
                                    ((Activity) mContext).startActivityForResult(userProfileIntent, ConstantVariables.
                                            USER_PROFILE_CODE);
                                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                }
                            });
                        }

                        if (!isPostMenusSet) {
                            try {
                                int photo = 0, checkIn = 0, video = 0;

                                if (mFeedPostMenus.has("video")) {
                                    video = mFeedPostMenus.getInt("video");
                                }

                                if (mFeedPostMenus.has("photo")) {
                                    photo = mFeedPostMenus.getInt("photo");
                                }

                                if (mFeedPostMenus.has("checkin")) {
                                    checkIn = mFeedPostMenus.getInt("checkin");
                                }

                                // Show Video
                                if (video != 0) {
                                    headerViewHolder.mVideoMenuIcon.setTypeface(fontIcon, Typeface.BOLD);
                                    headerViewHolder.mVideoMenuIcon.setText("\uf03d");
                                    headerViewHolder.mVideoMenuText.setText(mContext.getResources().getString(R.string.video));
                                } else {
                                    headerViewHolder.mVideoMenu.setVisibility(View.GONE);
                                }

                                // Show Photo
                                if (photo != 0) {
                                    headerViewHolder.mPhotoMenuIcon.setTypeface(fontIcon, Typeface.BOLD);
                                    headerViewHolder.mPhotoMenuIcon.setText("\uf030 ");
                                    headerViewHolder.mPhotoMenuText.setText(
                                            mContext.getResources().getString(R.string.photos));
                                } else {
                                    headerViewHolder.mPhotoMenu.setVisibility(View.GONE);
                                }

                                // Show CheckIn Option

                                if (checkIn != 0 && !mContext.getResources().getString(R.string.places_api_key).isEmpty()) {
                                    headerViewHolder.mCheckInMenuIcon.setTypeface(fontIcon, Typeface.BOLD);
                                    headerViewHolder.mCheckInMenuIcon.setText("\uF041");
                                    headerViewHolder.mCheckInMenuText.setText(mContext.getResources().getString(R.string.checkIn));
                                } else {
                                    headerViewHolder.mCheckInMenu.setVisibility(View.GONE);
                                }

                                headerViewHolder.mVideoMenu.setOnClickListener(this);
                                headerViewHolder.mPhotoMenu.setOnClickListener(this);
                                headerViewHolder.mCheckInMenu.setOnClickListener(this);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            headerViewHolder.mStatusUpdateText.setText(mContext.getResources().getString
                                    (R.string.status_box_default_text) + "...");
                            headerViewHolder.mStatusTextLayout.setOnClickListener(this);

                            headerViewHolder.mPostFeedOptions.setVisibility(View.VISIBLE);
                            isPostMenusSet = true;
                        }
                    }

                    // Set filters.
                    if (PreferencesUtils.isAAFFilterEnabled(mContext) &&
                        mFeedFiltersArray != null &&
                        mFeedFiltersArray.length() != 0 &&
                        (mSubjectType == null || mSubjectType.isEmpty())) {

                        List<SheetItemModel> mOptionsItemList = new ArrayList<>();
                        headerViewHolder.llFilterBlock.setVisibility(View.VISIBLE);
                        headerViewHolder.tvFirstFilterIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        headerViewHolder.tvSecondFilterIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        headerViewHolder.tvThirdFilterIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        headerViewHolder.tvForthFilterIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        headerViewHolder.tvMoreIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));

                        for (int i = 0; i < mFeedFiltersArray.length(); i++) {
                            JSONObject filterObject = mFeedFiltersArray.optJSONObject(i);
                            JSONObject urlParams = filterObject.optJSONObject("urlParams");
                            String filterType = urlParams.optString("filter_type");
                            String filterTitle = filterObject.optString("tab_title");

                            // Showing first 4 filters directly with icons and rest filters will be showing in bottom sheet.
                            if (i < 4) {
                                if (i == 0) {
                                    setFilterDataInViews(headerViewHolder.firstFilterBlock,
                                            headerViewHolder.tvFirstFilterIcon,
                                            headerViewHolder.tvFirstFilter,
                                            headerViewHolder.ivBgShapeFirstFilter,
                                            filterType, filterTitle, R.color.first_filter);
                                    headerViewHolder.firstFilterBlock.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setSelectedView(headerViewHolder.firstFilterBlock,
                                                    headerViewHolder.secondFilterBlock,
                                                    headerViewHolder.thirdFilterBlock,
                                                    headerViewHolder.forthFilterBlock,
                                                    headerViewHolder.moreFilterBlock);
                                            setFilter(v);
                                        }
                                    });
                                }
                                if (i == 1) {
                                    setFilterDataInViews(headerViewHolder.secondFilterBlock,
                                            headerViewHolder.tvSecondFilterIcon,
                                            headerViewHolder.tvSecondFilter,
                                            headerViewHolder.ivBgShapeSecondFilter,
                                            filterType, filterTitle, R.color.second_filter);
                                    headerViewHolder.secondFilterBlock.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setSelectedView(headerViewHolder.secondFilterBlock,
                                                    headerViewHolder.firstFilterBlock,
                                                    headerViewHolder.thirdFilterBlock,
                                                    headerViewHolder.forthFilterBlock,
                                                    headerViewHolder.moreFilterBlock);
                                            setFilter(v);
                                        }
                                    });
                                }
                                if (i == 2) {
                                    setFilterDataInViews(headerViewHolder.thirdFilterBlock,
                                            headerViewHolder.tvThirdFilterIcon,
                                            headerViewHolder.tvThirdFilter,
                                            headerViewHolder.ivBgShapeThirdFilter,
                                            filterType, filterTitle, R.color.third_filter);
                                    headerViewHolder.thirdFilterBlock.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setSelectedView(headerViewHolder.thirdFilterBlock,
                                                    headerViewHolder.firstFilterBlock,
                                                    headerViewHolder.secondFilterBlock,
                                                    headerViewHolder.forthFilterBlock,
                                                    headerViewHolder.moreFilterBlock);
                                            setFilter(v);
                                        }
                                    });
                                }
                                if (i == 3) {
                                    setFilterDataInViews(headerViewHolder.forthFilterBlock,
                                            headerViewHolder.tvForthFilterIcon,
                                            headerViewHolder.tvForthFilter,
                                            headerViewHolder.ivBgShapeForthFilter,
                                            filterType, filterTitle, R.color.forth_filter);
                                    headerViewHolder.forthFilterBlock.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            setSelectedView(headerViewHolder.forthFilterBlock,
                                                    headerViewHolder.firstFilterBlock,
                                                    headerViewHolder.secondFilterBlock,
                                                    headerViewHolder.thirdFilterBlock,
                                                    headerViewHolder.moreFilterBlock);
                                            setFilter(v);
                                        }
                                    });
                                }

                            } else {
                                setFilterDataInViews(headerViewHolder.moreFilterBlock,
                                        headerViewHolder.tvMoreIcon, headerViewHolder.tvMore,
                                        headerViewHolder.ivBgShapeMoreFilter, "more_filter", mContext.getResources().getString(R.string.more), R.color.second_filter);
                                mOptionsItemList.add(new SheetItemModel(filterTitle, filterType, getFilterIcon(filterType)));
                                mSheetAdapter = new SimpleSheetAdapter(mContext, mOptionsItemList, true, true);
                                Log.d("SampleKeyHere ", String.valueOf(isSaveFeeds));
//                                if (isSaveFeeds){
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            mFilterSelectedListener.setFilterType("user_saved");
//                                        }
//                                    }, 2500);
//                                }
                                mSheetAdapter.setOnItemClickListener(new SimpleSheetAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(SheetItemModel item, int position) {
                                        mBottomSheetDialog.dismiss();
                                        if (item.getKey() != null && mFilterSelectedListener != null) {
                                            Log.d("SelectedOption ", item.getKey());
                                            mFilterSelectedListener.setFilterType(item.getKey());
                                        }
                                    }
                                });
                                headerViewHolder.moreFilterBlock.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setSelectedView(headerViewHolder.moreFilterBlock,
                                                headerViewHolder.firstFilterBlock,
                                                headerViewHolder.secondFilterBlock,
                                                headerViewHolder.thirdFilterBlock,
                                                headerViewHolder.forthFilterBlock);
                                        View inflateView = inflater.inflate(R.layout.fragmen_cart, null);
                                        RecyclerView recyclerView = (RecyclerView) inflateView.findViewById(R.id.recycler_view);
                                        inflateView.findViewById(R.id.cart_bottom).setVisibility(View.GONE);
                                        recyclerView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                                        recyclerView.setAdapter(mSheetAdapter);
                                        mBottomSheetDialog = new BottomSheetDialog(mContext);
                                        mBottomSheetDialog.setContentView(inflateView);
                                        mBottomSheetDialog.show();
                                    }
                                });
                            }
                        }

                    } else {
                        headerViewHolder.llFilterBlock.setVisibility(View.GONE);
                    }

                    // Showing greetings.
                    JSONArray greetingsArray = mFeedItem.getGreetingsArray();
                    JSONArray birthdayArray = mFeedItem.getBirthdayArray();
                    boolean isBirthdayExist = false;

                    if (birthdayArray != null && birthdayArray.length() > 0) {
                        headerViewHolder.greetingView.setVisibility(View.GONE);
                        String description = null, userName = null, userProfile = null;

                        // Getting removed greetings from preferences.
                        ArrayList<String> removedBirthdayList = PreferencesUtils.getRemovedBirthdays(mContext);
                        for (int i = 0; i < birthdayArray.length(); i++) {
                            JSONObject userBirthday = birthdayArray.optJSONObject(i);
                            mUserId = userBirthday.optString("user_id");
                            userName = userBirthday.optString("displayname");
                            userProfile = userBirthday.optString("image_normal");

                            // If the removed birthday list contains the user then do not showing it.
                            if (removedBirthdayList == null
                                    || !removedBirthdayList.contains(mUserId)) {
                                isBirthdayExist = true;
                                description = userBirthday.optString("birthday_title");
                                break;
                            }
                        }

                        if (description != null) {
                            headerViewHolder.birthdayView.setVisibility(View.VISIBLE);
                            headerViewHolder.dividerView.setVisibility(View.VISIBLE);
                            mImageLoader.setImageForUserProfile(userProfile, headerViewHolder.ivUserProfile);
                            headerViewHolder.tvUserName.setText(userName);
                            headerViewHolder.tvBirthdayDescription.setText(description);

                            // Getting drawable for write post and send message.
                            if (!mUserId.equals(String.valueOf(mLoggedInUserId))) {
                                headerViewHolder.llFooterBlock.setVisibility(View.VISIBLE);

                                Drawable writePostDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_mode_edit_24dp);
                                writePostDrawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor),
                                        PorterDuff.Mode.SRC_ATOP));
                                Drawable sendMessage = ContextCompat.getDrawable(mContext, R.drawable.ic_action_message);
                                sendMessage.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor),
                                        PorterDuff.Mode.SRC_ATOP));
                                headerViewHolder.tvWritePost.setCompoundDrawablesWithIntrinsicBounds(writePostDrawable, null, null, null);
                                headerViewHolder.tvSendMessage.setCompoundDrawablesWithIntrinsicBounds(sendMessage, null, null, null);

                                headerViewHolder.llPostBlock.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent statusIntent = new Intent(mContext, Status.class);
                                        statusIntent.putExtra(ConstantVariables.SUBJECT_TYPE, ConstantVariables.USER_TITLE);
                                        statusIntent.putExtra(ConstantVariables.SUBJECT_ID, Integer.parseInt(mUserId));
                                        statusIntent.putExtra("showPhotoBlock", false);
                                        statusIntent.putExtra("feedPostMenus", mFeedPostMenus.toString());
                                        if (mFeedsFragment != null) {
                                            mFeedsFragment.startActivityForResult(statusIntent, ConstantVariables.UPDATE_REQUEST_CODE);
                                        } else {
                                            mContext.startActivity(statusIntent);
                                        }
                                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        PreferencesUtils.setRemovedBirthday(mContext, mUserId);
                                    }
                                });

                                final String finalUserName = userName;
                                headerViewHolder.llMessageBlock.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(mContext, CreateNewMessage.class);
                                        intent.putExtra(ConstantVariables.USER_ID, mUserId);
                                        intent.putExtra(ConstantVariables.CONTENT_TITLE, finalUserName);
                                        intent.putExtra("isSendMessageRequest", true);
                                        if (mFeedsFragment != null) {
                                            mFeedsFragment.startActivityForResult(intent, ConstantVariables.UPDATE_REQUEST_CODE);
                                        } else {
                                            mContext.startActivity(intent);
                                        }
                                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        PreferencesUtils.setRemovedBirthday(mContext, mUserId);
                                    }
                                });
                            } else {
                                headerViewHolder.llFooterBlock.setVisibility(View.GONE);
                            }

                            headerViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(mContext, userProfile.class);
                                    intent.putExtra(ConstantVariables.USER_ID, Integer.parseInt(mUserId));
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                }
                            });

                            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_clear_grey);
                            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.dark_grey_background),
                                    PorterDuff.Mode.SRC_ATOP);
                            headerViewHolder.ivBVCancel.setImageDrawable(drawable);
                            headerViewHolder.ivBVCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PreferencesUtils.setRemovedBirthday(mContext, mUserId);
                                    notifyItemChanged(headerViewHolder.getAdapterPosition());
                                }
                            });

                        } else {
                            headerViewHolder.birthdayView.setVisibility(View.GONE);
                            headerViewHolder.dividerView.setVisibility(View.GONE);
                        }
                    }

                    if (!isBirthdayExist && greetingsArray != null && greetingsArray.length() > 0) {
                        headerViewHolder.birthdayView.setVisibility(View.GONE);
                        String description = null;
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                        // Setting current date, and if the current date is changed then clearing the removed greetings.
                        if (PreferencesUtils.getGreetingCurrentDate(mContext) == null) {
                            PreferencesUtils.setCurrentDate(mContext, currentDate);
                        } else if (!PreferencesUtils.getGreetingCurrentDate(mContext).equals(currentDate)) {
                            PreferencesUtils.clearGreetingPref(mContext);
                            PreferencesUtils.setCurrentDate(mContext, currentDate);
                        }

                        // Getting removed greetings from preferences.
                        ArrayList<String> removedGreetingList = PreferencesUtils.getRemovedGreetings(mContext);
                        for (int i = 0; i < greetingsArray.length(); i++) {
                            JSONObject greeting = greetingsArray.optJSONObject(i);
                            mGreetingId = greeting.optString("greeting_id");

                            // If the removed greeting list contains the greeting then do not showing it.
                            if (removedGreetingList == null
                                    || !removedGreetingList.contains(mGreetingId)) {
                                description = greeting.optString("body").replace("[USER_NAME]", mUserName);
                                break;
                            }
                        }

                        /* Setting Body in TextView */
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            headerViewHolder.mGreetingWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                        } else {
                            headerViewHolder.mGreetingWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                        }

                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) headerViewHolder.mGreetingWebView.getLayoutParams();
                        layoutParams.width = AppConstant.getDisplayMetricsWidth(mContext) - 30;

                        if (!mFeedItem.isGreetingSet()) {
                            headerViewHolder.mGreetingWebView.loadDataWithBaseURL("file:///android_asset/",
                                    GlobalFunctions.getHtmlData(mContext, description, false), "text/html", "utf-8", null);
                            mFeedItem.setGreetingSet(true);
                        }

                        headerViewHolder.ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PreferencesUtils.setRemovedGreeting(mContext, mGreetingId);
                                notifyItemChanged(headerViewHolder.getAdapterPosition());
                            }
                        });

                        // If there is any non-removed greeting then showing it in webview.
                        if (description != null) {
                            headerViewHolder.greetingView.setVisibility(View.VISIBLE);
                            headerViewHolder.dividerView.setVisibility(View.VISIBLE);
                        } else {
                            headerViewHolder.greetingView.setVisibility(View.GONE);
                            headerViewHolder.dividerView.setVisibility(View.GONE);
                        }

                    } else {
                        headerViewHolder.greetingView.setVisibility(View.GONE);
                        headerViewHolder.dividerView.setVisibility(isBirthdayExist ? View.VISIBLE : View.GONE);
                    }
                }

                // Set a No feeds Available Message if there are no feeds
                if (isNoFeed) {
                    headerViewHolder.mNoFeedMessage.setVisibility(View.VISIBLE);
                    headerViewHolder.mNoFeedMessage.setText(mContext.getResources().getString
                            (R.string.no_activity_feed_message));
                } else {
                    headerViewHolder.mNoFeedMessage.setVisibility(View.GONE);
                }

                //Showing stories.
                if (mFeedItem.getBrowseListItem() != null && mFeedItem.getBrowseListItem().size() > 0) {
                    headerViewHolder.storyRecyclerView.setVisibility(View.VISIBLE);
                    if (mFeedItem.getIsStoryDataChange()) {
                        StoryBrowseAdapter storyBrowseAdapter = new StoryBrowseAdapter(mContext, mFeedsFragment, mFeedItem.getBrowseListItem());
                        headerViewHolder.storyRecyclerView.setAdapter(storyBrowseAdapter);
                        mFeedItem.setStoryDataChange(false);
                    }

                } else {
                    headerViewHolder.storyRecyclerView.setVisibility(View.GONE);
                }
                break;

            case AD_FB:
                FacebookAdViewHolder adMob = (FacebookAdViewHolder) viewHolder;
                if (adMob.mNativeAd != null) {
                    adMob.mNativeAd.unregisterView();
                }
                adMob.mNativeAd = (NativeAd) mFeedItemList.get(position);
                FacebookAdViewHolder.inflateAd(adMob.mNativeAd, adMob.adView, mContext, false);
                break;
            case AD_ADMOB:
                AdMobViewHolder adMobViewHolder = (AdMobViewHolder) viewHolder;

                AdMobViewHolder.inflateAd(mContext,
                        (NativeAppInstallAd) mFeedItemList.get(position), adMobViewHolder.mAdView);
                break;

            case COMMUNITY_ADS:
                CommunityAdsHolder communityAdsHolder = (CommunityAdsHolder) viewHolder;
                communityAdsHolder.mCommunityAd = (CommunityAdsList) mFeedItemList.get(position);
                CommunityAdsHolder.inflateAd(communityAdsHolder.mCommunityAd,
                        communityAdsHolder.adView, mContext, position);

                break;

            case SPONSORED_STORIES:
                SponsoredStoriesHolder sponsoredStoriesHolder = (SponsoredStoriesHolder) viewHolder;
                sponsoredStoriesHolder.mSponsoredStory = (SponsoredStoriesList) mFeedItemList.get(position);
                SponsoredStoriesHolder.inflateAd(sponsoredStoriesHolder.mSponsoredStory,
                        sponsoredStoriesHolder.adView, mContext, position);

                break;

            case PEOPLE_SUGGESTION:

                // Showing people suggestion recycler view at the specified position.
                final PeopleSuggestionViewHolder peopleSuggestionViewHolder = (PeopleSuggestionViewHolder) viewHolder;
                final List<Object> browseList = (List<Object>) mFeedItemList.get(position);
                final PeopleSuggestionAdapter peopleSuggestionAdapter = new PeopleSuggestionAdapter(mContext,
                        browseList, "feed_suggestion", new PeopleSuggestionAdapter.OnItemDeleteListener() {
                    @Override
                    public void onItemDelete() {
                        // When all the suggestion removed the sent the request to reload the data.
                        browseList.clear();
                        peopleSuggestionViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                        mFeedsFragment.addPeopleSuggestionList(false, true);
                    }
                }, new PeopleSuggestionAdapter.OnAddRequestSuccessListener() {
                    @Override
                    public void onScrollPosition(int position) {
                        peopleSuggestionViewHolder.mSuggestionRecyclerView.smoothScrollToPosition(position);
                    }
                });

                // Setting adapter only once until no new data is added.
                if (!isAdapterSet) {
                    peopleSuggestionViewHolder.mSuggestionRecyclerView.setHasFixedSize(true);
                    ((SimpleItemAnimator) peopleSuggestionViewHolder.mSuggestionRecyclerView.getItemAnimator()).
                            setSupportsChangeAnimations(false);
                    peopleSuggestionViewHolder.mSuggestionRecyclerView.setLayoutManager(
                            new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                    peopleSuggestionViewHolder.mSuggestionRecyclerView.setAdapter(peopleSuggestionAdapter);
                    peopleSuggestionViewHolder.mProgressBar.setVisibility(View.GONE);
                    isAdapterSet = true;
                }
                peopleSuggestionViewHolder.tvSeeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        peopleSuggestionAdapter.showAllSuggestionActivity();
                    }
                });

                break;

            case REMOVE_AD_TYPE:

                // Show Hidden Type Feed
                final RemoveAdHolder removeAdHolder = (RemoveAdHolder) viewHolder;
                if (ConstantVariables.FEED_ADS_TYPE == ConstantVariables.TYPE_COMMUNITY_ADS) {
                    removeAdHolder.mCommunityAd = (CommunityAdsList) mFeedItemList.get(position);
                    removeAdHolder.removeAd(removeAdHolder.mCommunityAd, removeAdHolder.adView, mContext, position);
                } else {
                    removeAdHolder.mSponsoredStory = (SponsoredStoriesList) mFeedItemList.get(position);
                    removeAdHolder.removeAd(removeAdHolder.mSponsoredStory, removeAdHolder.adView, mContext, position);
                }
                break;
            case PREVIEW_TYPE:
                _renderPreview((ListItemHolder) viewHolder, position);
                break;
            default:
                ProgressViewHolder.inflateProgressView(mContext, ((ProgressViewHolder) viewHolder).progressView,
                        mFeedItemList.get(position));
                break;
        }
    }

    private void renderCheckinAttachment(int position, final ListItemHolder listItemHolder, FeedList mFeedItem) {

        listItemHolder.mLatitude = mFeedItem.getmLatitude();
        listItemHolder.mLongitude = mFeedItem.getmLongitude();
        listItemHolder.mLocationLabel = mFeedItem.getmLocationLabel();

        listItemHolder.mAttachmentView.setVisibility(View.VISIBLE);
        listItemHolder.mAttachmentImage.setVisibility(View.VISIBLE);
        listItemHolder.mAttachmentPreviewBlock.setVisibility(View.VISIBLE);
        listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
        listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
        listItemHolder.ivSticker.setVisibility(View.GONE);
        listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
        listItemHolder.mAttachmentImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
        listItemHolder.tvPrice.setVisibility(View.GONE);
        listItemHolder.tvLocation.setVisibility(View.GONE);

        String getMapUrl = "http://maps.google.com/maps/api/staticmap?maptype=roadmap" +
                "&center=" + listItemHolder.mLatitude + "," + listItemHolder.mLongitude +
                "&markers=" + listItemHolder.mLatitude + "," + listItemHolder.mLongitude +
                "&zoom=16&size=720x720&sensor=false" +
                "&key=" + mContext.getResources().getString(R.string.places_api_key);
        if (CacheUtils.getInstance(mContext).getLru().get(getMapUrl) != null) {
            listItemHolder.mAttachmentImage.setImageBitmap(CacheUtils.
                    getInstance(mContext).getLru().get(getMapUrl));
        } else {
            listItemHolder.mAttachmentImage.setImageResource(R.color.gray_light);
            new StaticMap(mContext, position, getMapUrl, new OnStaticMapLoadListener() {
                @Override
                public void onMapLoaded(int position) {
                    notifyItemChanged(position);
                }
            }).execute();
        }

        if (mFeedItem.getmLocationLabel() != null && !mFeedItem.getmLocationLabel().isEmpty()) {
            listItemHolder.mAttachmentTitle.setVisibility(View.VISIBLE);
        } else {
            listItemHolder.mAttachmentTitle.setVisibility(View.GONE);
        }
        listItemHolder.mAttachmentTitle.setText(mFeedItem.getmLocationLabel());
        listItemHolder.mImagesGallery.setVisibility(View.GONE);
        listItemHolder.mPlayIcon.setVisibility(View.GONE);
        listItemHolder.mAttachmentBody.setVisibility(View.GONE);
        listItemHolder.mAttachmentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());
                if (feedList.itemViewType == PREVIEW_TYPE ) return;
                if (feedList.getmAttachmentCount() == 0) {
                    openMapActivity(listItemHolder.mLocationLabel,
                            listItemHolder.mLatitude,
                            listItemHolder.mLongitude, listItemHolder.mPlaceId);
                } else if (listItemHolder.mFeedAttachmentArray != null) {
                    JSONObject singleAttachmentObject = listItemHolder.mFeedAttachmentArray.optJSONObject(0);
                    String attachmentType = singleAttachmentObject.optString("attachment_type");
                    int attachmentId = singleAttachmentObject.optInt("attachment_id");
                    redirectToAttachmentClickedActivity(attachmentType, attachmentId,
                            singleAttachmentObject, listItemHolder);
                }
            }
        });
    }

    private void _renderPreview(ListItemHolder viewHolder, int position) {
        mFeedPreviewItem = (FeedList) this.mFeedItemList.get(position);
        viewHolder.itemViewType = mFeedPreviewItem.itemViewType;
        final ListItemHolder previewItemHolder = viewHolder;
        previewItemHolder.mPhotoAttachmentCount = mFeedPreviewItem.getmPhotoAttachmentCount();
        previewItemHolder.mFeedAttachmentType = mFeedPreviewItem.getmFeedAttachmentType();
        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(previewItemHolder.mImagesGallery,
                2, LinearLayoutManager.VERTICAL, false);
        previewItemHolder.mImagesGallery.setLayoutManager(layoutManager);

        layoutManager.setSpanSizeLookup(new CustomGridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (previewItemHolder.mPhotoAttachmentCount == 2) {
                    return 1;
                } else {
                    if (position == 0) {
                        return 2;
                    } else {
                        return 1;
                    }
                }
            }
        });
        previewItemHolder.mImagesGallery.setTag(previewItemHolder);

        previewItemHolder.mFeedTitleBody.setVisibility(View.GONE);
        previewItemHolder.mFeedBlock.setAlpha(0.3f);
        previewItemHolder.mAttachmentView.setClickable(false);
        previewItemHolder.mFeedTitle.setText(mFeedPreviewItem.posterTitle);
        mImageLoader.setImageForUserProfile(mFeedPreviewItem.posterThumb,
                previewItemHolder.mFeedProfileImage);
        previewItemHolder.mFeedTitleBody.setMovementMethod(LinkMovementMethod.getInstance());
        previewItemHolder.mFeedTitleBodyText = mFeedPreviewItem.postedItemBody;
        mFeedPreviewItem.setmActionTypeBody(mFeedPreviewItem.postedItemBody);
        previewItemHolder.mProgressPercentage.setText(mFeedPreviewItem.mProgress + "%");
        previewItemHolder.mCounterView.setVisibility(View.GONE);
        previewItemHolder.mFeedUploader.setVisibility(View.VISIBLE);
        previewItemHolder.mImagesGallery.setVisibility(View.GONE);
        previewItemHolder.mFeedFooterMenusBlock.setVisibility(View.GONE);
        previewItemHolder.mShareFeedDivider.setVisibility(View.GONE);
        previewItemHolder.mMultiPhotoRecyclerView.setVisibility(View.GONE);
        previewItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
        previewItemHolder.ivSticker.setVisibility(View.GONE);
        previewItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
        previewItemHolder.mAttachmentView.setVisibility(View.GONE);
        previewItemHolder.mAttachmentBody.setVisibility(View.GONE);
        previewItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
        previewItemHolder.mCancelUpload.setTag(mFeedPreviewItem.getNotifyPosition());
        previewItemHolder.mCancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedsFragment != null && previewItemHolder.getAdapterPosition() > 0) {
                    FeedList feedList = (FeedList) mFeedItemList.get(previewItemHolder.getAdapterPosition());
                    mFeedsFragment.cancelRequest(Integer.parseInt(feedList.getNotifyPosition()));
                }
            }
        });
        if (mFeedPreviewItem.isRequestProcessing) {
            previewItemHolder.processingRequest.setVisibility(View.VISIBLE);
            previewItemHolder.mCancelUpload.setVisibility(View.GONE);
        } else {
            previewItemHolder.processingRequest.setVisibility(View.GONE);
            previewItemHolder.mCancelUpload.setVisibility(View.VISIBLE);
        }
        previewItemHolder.mFeedAttachmentArray = mFeedPreviewItem.getmFeedAttachmentArray();

        try {
            renderFeedBody(previewItemHolder, mFeedPreviewItem);
            if ((mFeedPreviewItem.getmFeedType() != null && mFeedPreviewItem.getmFeedType().equals("sitetagcheckin_status"))
                    && mFeedPreviewItem.getmAttachmentCount() == 0
                    && (bannerObject == null || bannerObject.length() <= 0)) {
                renderCheckinAttachment(position, previewItemHolder, mFeedPreviewItem);

            }
            renderAttachment(position, previewItemHolder, mFeedPreviewItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        previewItemHolder.feedUploadProgress.setProgress(mFeedPreviewItem.mProgress);
    }




    private void renderFeedBody(final ListItemHolder listItemHolder, FeedList mFeedItem) {
        bannerObject = mFeedItem.getBannerObject();
        String bannerTextColor = null;
        if (bannerObject != null && bannerObject.length() > 0) {
            bannerTextColor = bannerObject.optString("color");
        }
    /* Start: Show body of the feed */
        listItemHolder.mFeedTitleBodyText = mFeedItem.getmActionTypeBody();
        listItemHolder.mFeedTitleBody.setMovementMethod(LinkMovementMethod.getInstance());

        if (listItemHolder.mFeedTitleBodyText != null && !listItemHolder.mFeedTitleBodyText.isEmpty()) {
            listItemHolder.mFeedTitleBodyText = Smileys.getEmojiFromString(listItemHolder.mFeedTitleBodyText);
            listItemHolder.mFeedTitleBody.setVisibility(View.VISIBLE);

            if (mFeedItem.getDecoration() != null && mFeedItem.getDecoration().length() > 0) {
                String fontColor = mFeedItem.getDecoration().optString("font_color");
                try {
                    listItemHolder.mFeedTitleBody.setTextColor(Color.parseColor(bannerTextColor != null && !bannerTextColor.isEmpty()
                            ? bannerTextColor : fontColor));
                } catch (IllegalArgumentException e) {
                    listItemHolder.mFeedTitleBody.setTextColor(ContextCompat.getColor(mContext, R.color.body_text_1));
                    e.printStackTrace();
                }

                listItemHolder.mFeedTitleBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        GlobalFunctions.getFontSizeFromPixel(mContext, mFeedItem.getDecoration().optInt("font_size")));
                if (mFeedItem.getDecoration().optString("font_style") != null
                        && mFeedItem.getDecoration().optString("font_style").equals("italic")) {
                    listItemHolder.mFeedTitleBody.setTypeface(null, Typeface.ITALIC);
                }

                // If banner is coming with feed then showing it as background.
                if (bannerObject != null && bannerObject.length() > 0
                        && mFeedItem.getmAttachmentCount() == 0) {

                    listItemHolder.mFeedTitleBody.setTypeface(null, Typeface.NORMAL);
                    listItemHolder.mFeedTitleBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            GlobalFunctions.getFontSizeFromPixel(mContext, Status.BANNER_TEXT_SIZE));

                    if (bannerObject.optString("feed_banner_url") != null
                            && !bannerObject.optString("feed_banner_url").isEmpty()
                            && CacheUtils.getInstance(mContext).getLru().get(bannerObject.optString("feed_banner_url")) != null) {
                        Drawable bannerDrawable = new BitmapDrawable(mContext.getResources(),
                                CacheUtils.getInstance(mContext).getLru().get(bannerObject.optString("feed_banner_url")));
                        listItemHolder.mFeedTitleBody.setBackground(bannerDrawable);

                    } else if (bannerObject.optString("feed_banner_url") != null
                            && !bannerObject.optString("feed_banner_url").isEmpty()) {
                        listItemHolder.mFeedTitleBody.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_light));

                        new BitMapCreatorUtil(mContext, bannerObject.optString("feed_banner_url"),
                                new BitMapCreatorUtil.OnBitmapLoadListener() {
                                    @Override
                                    public void onBitMapLoad(Bitmap bitmap) {
                                        notifyItemChanged(listItemHolder.getAdapterPosition());
                                    }
                                }).execute();
                    } else if (bannerObject.optString("background-color") != null
                            && !bannerObject.optString("background-color").isEmpty()) {
                        try {
                            listItemHolder.mFeedTitleBody.setBackgroundColor(Color.parseColor(bannerObject.optString("background-color")));
                        } catch (IllegalArgumentException e) {
                            listItemHolder.mFeedTitleBody.setTextColor(ContextCompat.getColor(mContext, R.color.body_text_1));
                            e.printStackTrace();
                        }
                    }
                    listItemHolder.mFeedTitleBody.setMinimumHeight(mContext.getResources().getDimensionPixelSize(R.dimen.feed_attachment_image_height));
                    listItemHolder.mFeedTitleBody.setGravity(Gravity.CENTER);
                } else {
                    listItemHolder.mFeedTitleBody.setBackground(null);
                    listItemHolder.mFeedTitleBody.setMinimumHeight(0);
                    listItemHolder.mFeedTitleBody.setGravity(Gravity.START);
                }

            } else if (bannerObject != null && bannerObject.length() > 0
                    && mFeedItem.getmAttachmentCount() == 0) {
                listItemHolder.mFeedTitleBody.setTypeface(null, Typeface.NORMAL);
                listItemHolder.mFeedTitleBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        GlobalFunctions.getFontSizeFromPixel(mContext, Status.BANNER_TEXT_SIZE));
                try {
                    listItemHolder.mFeedTitleBody.setTextColor(Color.parseColor(bannerTextColor != null && bannerTextColor.length() > 1
                            ? bannerTextColor : "#de000000"));
                } catch (IllegalArgumentException e) {
                    listItemHolder.mFeedTitleBody.setTextColor(ContextCompat.getColor(mContext, R.color.body_text_1));
                    e.printStackTrace();
                }

                if (bannerObject.optString("feed_banner_url") != null
                        && !bannerObject.optString("feed_banner_url").isEmpty()
                        && CacheUtils.getInstance(mContext).getLru().get(bannerObject.optString("feed_banner_url")) != null) {
                    Drawable bannerDrawable = new BitmapDrawable(mContext.getResources(),
                            CacheUtils.getInstance(mContext).getLru().get(bannerObject.optString("feed_banner_url")));
                    listItemHolder.mFeedTitleBody.setBackground(bannerDrawable);

                } else if (bannerObject.optString("feed_banner_url") != null
                        && !bannerObject.optString("feed_banner_url").isEmpty()) {
                    listItemHolder.mFeedTitleBody.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_light));

                    new BitMapCreatorUtil(mContext, bannerObject.optString("feed_banner_url"),
                            new BitMapCreatorUtil.OnBitmapLoadListener() {
                                @Override
                                public void onBitMapLoad(Bitmap bitmap) {
                                    notifyItemChanged(listItemHolder.getAdapterPosition());
                                }
                            }).execute();
                } else if (bannerObject.optString("background-color") != null
                        && !bannerObject.optString("background-color").isEmpty()) {
                    try {
                        listItemHolder.mFeedTitleBody.setBackgroundColor(Color.parseColor(bannerObject.optString("background-color")));
                    } catch (IllegalArgumentException e) {
                        listItemHolder.mFeedTitleBody.setTextColor(ContextCompat.getColor(mContext, R.color.body_text_1));
                        e.printStackTrace();
                    }
                }

                listItemHolder.mFeedTitleBody.setMinimumHeight(mContext.getResources().getDimensionPixelSize(R.dimen.feed_attachment_image_height));
                listItemHolder.mFeedTitleBody.setGravity(Gravity.CENTER);
            } else {
                listItemHolder.mFeedTitleBody.setTextColor(ContextCompat.getColor(mContext, R.color.body_text_1));
                listItemHolder.mFeedTitleBody.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.body_default_font_size));
                listItemHolder.mFeedTitleBody.setTypeface(null, Typeface.NORMAL);
                listItemHolder.mFeedTitleBody.setBackground(null);
                listItemHolder.mFeedTitleBody.setMinimumHeight(0);
                listItemHolder.mFeedTitleBody.setGravity(Gravity.START);
            }
            listItemHolder.mFeedTitleBody.setEmojiconSize((int) listItemHolder.mFeedTitleBody.getTextSize());

            listItemHolder.mFeedTitleBody.setMinWidth(AppConstant.getDisplayMetricsWidth(mContext));
            //Checking feed is single feed or activity feed
            // if feed is single then showing post with large text else showing a "See More" option(if text too large)
            //Checking if post length is grater than 300 then showing "More" option else showing full post
            if (!mIsSingleFeed && listItemHolder.mFeedTitleBodyText.trim().length() >
                    ConstantVariables.FEED_TITLE_BODY_LENGTH) {
                seeMoreOption(listItemHolder.mFeedTitleBody, listItemHolder.mFeedTitleBodyText,
                        listItemHolder, null, true);
            } else {
                showFullContentInFeedTitle(listItemHolder);
            }


        } else {
            listItemHolder.mFeedTitleBody.setVisibility(View.GONE);
        }

            /* End Feed Body Work */
    }

    private void showFullAttachmentBody(ListItemHolder listItemHolder, String attachmentBody) {
        listItemHolder.mAttachmentBody.setText(
                attachmentBody.replaceAll("<img.+?>|<IMG.+?>", ""));
    }

    private void showFullContentInFeedTitle(final ListItemHolder listItemHolder) {
        HashMap<String, String> clickablePartsNew = mFeedItem.getmClickableStringsListNew();
        HashMap<String, String> wordStylingClickableParts = mFeedItem.getWordStylingClickableParts();

        // Show Clickable Parts and Apply Click Listener to redirect
        if (clickablePartsNew != null && clickablePartsNew.size() != 0) {

            CharSequence title = mTagSelectingTextView.addClickablePart(listItemHolder.mFeedTitleBodyText.
                    replaceAll("<img.+?>|<IMG.+?>", ""), this, 0, ConstantVariables.DEFAULT_HASHTAG_COLOR);

            SpannableString text = new SpannableString(title);
            SortedSet<String> keys = new TreeSet<>(clickablePartsNew.keySet());

            int lastIndex = 0;
            for (String key : keys) {

                String[] keyParts = key.split("-");
                final String attachment_type = keyParts[1];
                final int attachment_id = Integer.parseInt(keyParts[2]);

                final String value = clickablePartsNew.get(key);

                if (value != null && !value.isEmpty()) {
                    int i1 = title.toString().indexOf(value, lastIndex);
                    if (i1 != -1) {
                        int i2 = i1 + value.length();
                        if (lastIndex != -1) {
                            lastIndex += value.length();
                        }
                        ClickableSpan myClickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                attachmentClicked(value, attachment_type, attachment_id, listItemHolder, null);
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false);
                                ds.setColor(ContextCompat.getColor(mContext, R.color.black));
                                ds.setFakeBoldText(true);
                            }
                        };
                        text.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            if (wordStylingClickableParts != null && wordStylingClickableParts.size() > 0) {
                text = checkForWordStyling(wordStylingClickableParts, title, text);
            }
            listItemHolder.mFeedTitleBody.setText(text);

        } else if (wordStylingClickableParts != null && wordStylingClickableParts.size() > 0) {
            CharSequence title = mTagSelectingTextView.addClickablePart(listItemHolder.mFeedTitleBodyText.
                    replaceAll("<img.+?>|<IMG.+?>", ""), this, 0, ConstantVariables.DEFAULT_HASHTAG_COLOR);

            SpannableString text = new SpannableString(title);
            text = checkForWordStyling(wordStylingClickableParts, title, text);
            listItemHolder.mFeedTitleBody.setText(text);
        } else {
            listItemHolder.mFeedTitleBody.setText(mTagSelectingTextView.addClickablePart(listItemHolder.mFeedTitleBodyText.
                            replaceAll("<img.+?>|<IMG.+?>", ""), this, 0, ConstantVariables.DEFAULT_HASHTAG_COLOR),
                    BufferType.SPANNABLE);
        }
    }

    private void renderAttachment(final int position, final ListItemHolder listItemHolder, FeedList mFeedItem) throws JSONException {
        if (listItemHolder.mFeedAttachmentArray == null) {
            return;
        }
                    /* Start: Show Attachment Info */
        listItemHolder.mFeedType = mFeedItem.getmFeedType();
        mPhotoUrls = new ArrayList<>();
        mPhotoDetails = new ArrayList<>();
        mPhotoList = new ArrayList<>();
        mSellImageList = new ArrayList<>();
        mPhotoPosition = 0;
        for (int i = 0; i < listItemHolder.mFeedAttachmentArray.length(); i++) {
            columnWidth = 0;

            final JSONObject singleAttachmentObject = listItemHolder.mFeedAttachmentArray.getJSONObject(i);
            final String attachmentType = singleAttachmentObject.optString("attachment_type");
            final int attachmentId = singleAttachmentObject.optInt("attachment_id");
            String mainImage = singleAttachmentObject.optString("image_main");
            JSONObject imageMainObj = singleAttachmentObject.optJSONObject("image_main");
            if (imageMainObj != null && imageMainObj.length() > 0) {
                mainImage = imageMainObj.optString("src");
            }
            final int playlistId = singleAttachmentObject.optInt("playlist_id");
            final String attachmentTitle = singleAttachmentObject.optString("title");
            int mode = singleAttachmentObject.optInt("mode");
            String attachmentUri = singleAttachmentObject.optString("uri");
            if (attachmentUri != null && !attachmentUri.isEmpty()) {
                listItemHolder.mWebUrl = attachmentUri;
            }

            listItemHolder.mMusicAttachmentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachmentClicked(attachmentTitle,
                            listItemHolder.mFeedAttachmentType, playlistId,
                            listItemHolder, null);
                }
            });

            listItemHolder.mAttachmentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listItemHolder.itemViewType == 12 ) return;
                    if (listItemHolder.mFeedType != null
                            && (listItemHolder.mFeedType.equals("sitetagcheckin_status")
                            || listItemHolder.mFeedType.equals("sitetagcheckin_checkin"))) {
                        openMapActivity(listItemHolder.mLocationLabel,
                                listItemHolder.mLatitude,
                                listItemHolder.mLongitude, listItemHolder.mPlaceId);
                    } else if (listItemHolder.mFeedAttachmentType != null &&
                            !listItemHolder.mFeedAttachmentType.equals("advancedactivity_sell")) {
                        redirectToAttachmentClickedActivity(attachmentType, attachmentId,
                                singleAttachmentObject, listItemHolder);
                    }
                }
            });

            if (mode == 1 || mode == 2) {
                if (listItemHolder.mPhotoAttachmentCount > 0) {
                    if (attachmentType.contains("_photo") || (listItemHolder.mFeedAttachmentType != null
                            && listItemHolder.mFeedAttachmentType.contains("_photo")
                            && listItemHolder.mFeedType.equals("share"))) {

                        // Add PhotoDetails in list to show in PhotoLightBox
                        final String mediumImage = singleAttachmentObject.optString("image_medium");
                        int photo_id = singleAttachmentObject.optInt("photo_id");
                        int album_id = singleAttachmentObject.optInt("album_id");
                        int likes_count = singleAttachmentObject.optInt("likes_count");
                        int comment_count = singleAttachmentObject.optInt("comment_count");
                        int is_like = singleAttachmentObject.optInt("is_like");
                        boolean likeStatus = is_like != 0;
                        String reactions = singleAttachmentObject.optString("reactions");
                        String mUserTagArray = singleAttachmentObject.optString("tags");
                        final String menuArray = singleAttachmentObject.optString("menu");
                        String uri = singleAttachmentObject.optString("uri");
                        mPhotoDetails.add(new PhotoListDetails(photo_id, mainImage,
                                likes_count, comment_count, mUserTagArray, likeStatus, reactions));
                        mFeedItem.setmPhotoDetails(mPhotoDetails);

                        listItemHolder.mAttachmentView.setVisibility(View.GONE);
                        listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
                        listItemHolder.ivSticker.setVisibility(View.GONE);

                        int columnHeight = (int) (mAppConst.getScreenWidth() / ConstantVariables.IMAGE_SCALE_FACTOR);

                        if (listItemHolder.mPhotoAttachmentCount == 1) {

                            LinearLayout.LayoutParams singleImageParam;

                            // Show Single image in ImageView.
                            if (imageMainObj != null && imageMainObj.length() > 0) {
                                singleImageParam = getSingleImageParamFromWidthHeight(imageMainObj, listItemHolder.mSingleAttachmentImage);
                                singleImageParam.setMargins(0, mContext.getResources().getDimensionPixelSize(R.dimen.element_spacing_small), 0, 0);
                                listItemHolder.rlSingleImageLayout.setLayoutParams(singleImageParam);
                                mImageLoader.setFeedImage(mainImage, listItemHolder.mSingleAttachmentImage, singleImageParam.width, singleImageParam.height);

                                final String imageSrc = mainImage;

                                if (mainImage.contains((".gif")) && !mFeedItem.getIsGifLoad()) {
                                    listItemHolder.ivGifIcon.setVisibility(View.VISIBLE);
                                } else {
                                    listItemHolder.ivGifIcon.setVisibility(View.GONE);
                                }

                                listItemHolder.ivGifIcon.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (listItemHolder.itemViewType == 12 ) return;
                                        listItemHolder.ivGifIcon.setVisibility(View.GONE);
                                        mImageLoader.setFeedImageWithAnimation(imageSrc, listItemHolder.mSingleAttachmentImage);
                                        if (mOnGifPlayListener != null) {
                                            mOnGifPlayListener.onGifPlay(position);
                                        }
                                    }
                                });

                                listItemHolder.rlSingleImageLayout.setVisibility(View.VISIBLE);
                                listItemHolder.mImagesGallery.setVisibility(View.GONE);
                                listItemHolder.mMultiPhotoRecyclerView.setVisibility(View.GONE);
                                listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
                                listItemHolder.mSingleAttachmentImage.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (listItemHolder.itemViewType == 12 ) return;
                                                if (listItemHolder.getAdapterPosition() < mFeedItemList.size()) {
                                                    mPhotoDetails.clear();
                                                    FeedList mFeedItem = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());
                                                    JSONObject singleAttachmentObject = mFeedItem.getmFeedAttachmentArray().optJSONObject(0);
                                                    int photo_id = singleAttachmentObject.optInt("photo_id");
                                                    int album_id = singleAttachmentObject.optInt("album_id");
                                                    int likes_count = singleAttachmentObject.optInt("likes_count");
                                                    int comment_count = singleAttachmentObject.optInt("comment_count");
                                                    int is_like = singleAttachmentObject.optInt("is_like");
                                                    String reactions = singleAttachmentObject.optString("reactions");
                                                    String mUserTagArray = singleAttachmentObject.optString("tags");
                                                    final String menuArray = singleAttachmentObject.optString("menu");
                                                    String uri = singleAttachmentObject.optString("uri");
                                                    boolean likeStatus = is_like != 0;
                                                    String albumViewUrl;

                                                    if (listItemHolder.mFeedAttachmentType != null &&
                                                            listItemHolder.mFeedAttachmentType.equals("album_photo")) {
                                                        albumViewUrl = UrlUtil.ALBUM_VIEW_PAGE + album_id + "?gutter_menu=1";

                                                    } else {
                                                        albumViewUrl = UrlUtil.ALBUM_VIEW_URL + album_id;
                                                    }

                                                    String postBody = null;
                                                    if (mFeedItem.getmActionTypeBody() != null && !mFeedItem.getmActionTypeBody().isEmpty()) {
                                                        postBody = Smileys.getEmojiFromString(mFeedItem.getmActionTypeBody());
                                                        postBody = postBody.replaceAll("\n", "<br/>");
                                                    }

                                                    // Set gif load true so we can stop playing gif when comes back from photolightbox
                                                    if (imageSrc.contains((".gif"))) {
                                                        mFeedItem.setIsGifLoad(true);
                                                    }

                                                    mPhotoDetails.add(new PhotoListDetails(postBody, photo_id, imageSrc,
                                                            likes_count, comment_count, likeStatus, reactions, mUserTagArray, menuArray, uri));
                                                    mFeedItem.setmPhotoDetails(mPhotoDetails);

                                                    openPhotoLightBox(listItemHolder.getAdapterPosition(),
                                                            albumViewUrl, listItemHolder.mPhotoAttachmentCount,
                                                            listItemHolder.mFeedAttachmentType, album_id);
                                                }
                                            }
                                        });

                            }
                        } else if (!mIsSingleFeed) {
                            listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                            listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
                            listItemHolder.mImagesGallery.setVisibility(View.VISIBLE);
                            switch (mFeedItem.getmAttachmentCount()) {
                                case 2:
                                    columnWidth = mAppConst.getScreenWidth() / 2;
                                    if (mainImage != null && !mainImage.isEmpty()) {
                                        mPhotoUrls.add(new ImageViewList(mainImage, columnWidth,
                                                columnHeight));
                                    }
                                    break;
                                case 3:
                                    if (mPhotoPosition == 0) {
                                        columnWidth = mAppConst.getScreenWidth();
                                    } else {
                                        columnWidth = mAppConst.getScreenWidth() / 2;
                                    }
                                    if (mainImage != null && !mainImage.isEmpty()) {
                                        mPhotoUrls.add(new ImageViewList(mainImage, columnWidth,
                                                columnHeight));
                                    }
                                    ++mPhotoPosition;
                                    break;
                                default:
                                    if (mPhotoPosition < 2) {
                                        if (mPhotoPosition == 0) {
                                            columnWidth = mAppConst.getScreenWidth();
                                        } else {
                                            columnWidth = mAppConst.getScreenWidth() / 2;
                                        }
                                        if (mainImage != null && !mainImage.isEmpty()) {
                                            mPhotoUrls.add(new ImageViewList(mainImage, columnWidth,
                                                    columnHeight));
                                        }
                                    } else if (mPhotoPosition == 2) {
                                        columnWidth = mAppConst.getScreenWidth() / 2;
                                        if (mainImage != null && !mainImage.isEmpty()) {
                                            mPhotoUrls.add(new ImageViewList(mainImage, columnWidth,
                                                    columnHeight, mFeedItem.getmAttachmentCount() - mPhotoPosition - 1));
                                        }
                                    }
                                    ++mPhotoPosition;
                                    break;
                            }
                        } else {
                            listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                            listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
                            listItemHolder.mMultiPhotoRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            listItemHolder.mImagesGallery.setVisibility(View.GONE);
                            listItemHolder.mMultiPhotoRecyclerView.setVisibility(View.VISIBLE);
                            mPhotoList.add(new PhotoListDetails(photo_id, album_id, mainImage, likes_count,
                                    comment_count, likeStatus, attachmentType, reactions, menuArray, uri));
                        }
                    } else if (attachmentType.equals("advancedactivity_sell")
                            || (listItemHolder.mFeedAttachmentType != null
                            && listItemHolder.mFeedAttachmentType.equals("advancedactivity_sell"))) {
                        listItemHolder.sellPhotosRecyclerView.setVisibility(View.VISIBLE);
                        listItemHolder.sellPhotosRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        listItemHolder.mMultiPhotoRecyclerView.setVisibility(View.GONE);
                        listItemHolder.mImagesGallery.setVisibility(View.GONE);
                        listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                        listItemHolder.mAttachmentPreviewBlock.setVisibility(View.GONE);

                        JSONArray sellImagesArray = singleAttachmentObject.optJSONArray("sell_image");
                        if (sellImagesArray != null && sellImagesArray.length() > 0) {
                            for (int j = 0; j < sellImagesArray.length(); j++) {
                                JSONObject sellObject = sellImagesArray.optJSONObject(j);
                                mSellImageList.add(new ImageViewList(sellObject.optString("image_main")));
                            }
                        }

                    } else {
                        // Show Photo as attachment in card view
                        listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
                        listItemHolder.mImagesGallery.setVisibility(View.GONE);
                        listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                        listItemHolder.mMultiPhotoRecyclerView.setVisibility(View.GONE);
                        if (!listItemHolder.mFeedAttachmentType.equals("music_playlist_song")) {
                            listItemHolder.mAttachmentView.setVisibility(View.VISIBLE);
                            if (mainImage != null && !mainImage.isEmpty()) {
                                listItemHolder.mAttachmentPreviewBlock.setVisibility(View.VISIBLE);

                                if (!attachmentType.contains("video") && imageMainObj != null && imageMainObj.length() > 0) {

                                    LinearLayout.LayoutParams singleImageParam = getSingleImageParamFromWidthHeight(imageMainObj, listItemHolder.mAttachmentImage);
                                    singleImageParam.setMargins(0,
                                            mContext.getResources().getDimensionPixelSize(R.dimen.element_spacing_small), 0, 0);
                                    listItemHolder.mAttachmentPreviewBlock.setLayoutParams(singleImageParam);

                                    mImageLoader.setFeedImage(mainImage,
                                            listItemHolder.mAttachmentImage, mAppConst.getScreenWidth(),
                                            (singleImageParam.height > 0 ) ? singleImageParam.height : (int) mContext.getResources().getDimension(R.dimen.feed_attachment_image_height));
                                } else if (imageMainObj != null && imageMainObj.length() > 0) {

                                    LinearLayout.LayoutParams singleImageParam = CustomViews.getCustomWidthHeightLayoutParams(mAppConst.getScreenWidth(),
                                            (int) mContext.getResources().getDimension(R.dimen.feed_attachment_image_height));
                                    listItemHolder.mAttachmentPreviewBlock.setLayoutParams(singleImageParam);
                                    mImageLoader.setFeedImage(mainImage,
                                            listItemHolder.mAttachmentImage, mAppConst.getScreenWidth(),
                                            (int) mContext.getResources().getDimension(R.dimen.feed_attachment_image_height));
                                } else {
                                    mImageLoader.setFeedImage(mainImage,
                                            listItemHolder.mAttachmentImage, mAppConst.getScreenWidth(),
                                            (int) mContext.getResources().getDimension(R.dimen.feed_attachment_image_height));
                                }
                                listItemHolder.mAttachmentImage.setVisibility(View.VISIBLE);
                            }
                        } else {
                            listItemHolder.mAttachmentPreviewBlock.setVisibility(View.GONE);
                            listItemHolder.mAttachmentImage.setVisibility(View.GONE);
                            listItemHolder.mPlayIcon.setVisibility(View.GONE);
                        }

                        if ((attachmentType.contains("video") && !attachmentType.equals("sitevideo_channel")
                                && !attachmentType.equals("sitevideo_playlist"))
                                || ((listItemHolder.mFeedAttachmentType != null
                                && (listItemHolder.mFeedAttachmentType.contains("video")
                                && !listItemHolder.mFeedAttachmentType.equals("sitevideo_channel")
                                && !listItemHolder.mFeedAttachmentType.equals("sitevideo_playlist"))
                                && listItemHolder.mFeedType.equals("share")))) {
                            listItemHolder.mPlayIcon.setVisibility(View.VISIBLE);
                            listItemHolder.mPlayIcon.setTag(position);
                            listItemHolder.mPlayIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int itemPosition = listItemHolder.getAdapterPosition();
                                    FeedList mFeedItem = (FeedList) mFeedItemList.get(itemPosition);
                                    if (mFeedItem.itemViewType == 12 ) return;
                                    JSONArray attachmentArray = mFeedItem.getmFeedAttachmentArray();
                                    JSONObject videoInfoObject = attachmentArray.optJSONObject(0);
                                    Intent lightBox = new Intent(mContext, VideoLightBoxFragment.class);
                                    Bundle args = new Bundle();
                                    args.putString(ConstantVariables.VIDEO_URL, videoInfoObject.optString("attachment_video_url"));
                                    args.putInt(ConstantVariables.VIDEO_TYPE, videoInfoObject.optInt("attachment_video_type"));
                                    lightBox.putExtras(args);
                                    ((Activity) mContext).startActivity(lightBox);
                                    ((Activity) mContext).overridePendingTransition(R.anim.slide_up_in, R.anim.push_down_out);
                                }
                            });
                        } else {
                            listItemHolder.mPlayIcon.setVisibility(View.GONE);
                        }
                    }
                } else {
                    // Hide Photo Fields if there are no photos
                    listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                    listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
                    listItemHolder.mImagesGallery.setVisibility(View.GONE);
                    listItemHolder.mAttachmentImage.setVisibility(View.GONE);
                    listItemHolder.mAttachmentPreviewBlock.setVisibility(View.GONE);
                    listItemHolder.mPlayIcon.setVisibility(View.GONE);
                }

                // Set Title and description for mode 1.
                if (mode == 1) {
                    String attachmentBody = singleAttachmentObject.optString("body");

                 /*
                    Show Attachment Title in card View
                    Do not show title and description in case of photo attachment feeds
                 */
                    switch (listItemHolder.mFeedAttachmentType) {
                        case "music_playlist_song":
                            listItemHolder.mMusicAttachmentView.setVisibility(View.VISIBLE);
                            listItemHolder.ivSticker.setVisibility(View.GONE);
                            listItemHolder.mAttachmentView.setVisibility(View.GONE);
                            listItemHolder.mMusicAndLinkAttachmentImage.setVisibility(View.VISIBLE);
                            listItemHolder.mMusicAndLinkAttachmentImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_music2));

                            if (attachmentTitle != null && !attachmentTitle.isEmpty()
                                    && !attachmentTitle.equals("null")) {

                                listItemHolder.mMusicAttachmentTitle.setVisibility(View.VISIBLE);
                                listItemHolder.mMusicAttachmentTitle.setText(attachmentTitle);
                                listItemHolder.mMusicAttachmentTitle.setMovementMethod
                                        (LinkMovementMethod.getInstance());

                            } else {
                                listItemHolder.mMusicAttachmentTitle.setVisibility(View.GONE);
                            }

                            if (attachmentBody != null && !attachmentBody.isEmpty()) {
                                listItemHolder.mMusicAttachmentBody.setVisibility(View.VISIBLE);
                                listItemHolder.mMusicAttachmentBody.setText(
                                        attachmentBody.replaceAll("<img.+?>|<IMG.+?>", ""));
                            } else {
                                listItemHolder.mMusicAttachmentBody.setVisibility(View.GONE);
                            }
                            break;

                        case "sitereaction_sticker":
                            listItemHolder.mFeedTitleBody.setGravity(Gravity.CENTER);
                            listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
                            listItemHolder.mAttachmentView.setVisibility(View.GONE);
                            listItemHolder.ivSticker.setVisibility(View.VISIBLE);
                            mImageLoader.setStickerImageWithPlaceholder(mainImage, listItemHolder.ivSticker);
                            break;

                        case "advancedactivity_sell":
                            listItemHolder.tvPrice.setVisibility(View.VISIBLE);
                            listItemHolder.tvLocation.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                            listItemHolder.tvPrice.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                            listItemHolder.mAttachmentTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                    mContext.getResources().getDimension(R.dimen.body_medium_font_size));
                            if (singleAttachmentObject.optString("place") != null && !singleAttachmentObject.optString("place").isEmpty()) {
                                listItemHolder.tvLocation.setVisibility(View.VISIBLE);
                                listItemHolder.tvLocation.setText("\uf041  " + singleAttachmentObject.optString("place"));
                            } else {
                                listItemHolder.tvLocation.setVisibility(View.GONE);
                            }
                            if (singleAttachmentObject.optString("price") != null
                                    && singleAttachmentObject.optString("currency") != null
                                    && !singleAttachmentObject.optString("currency").isEmpty()) {
                                listItemHolder.tvPrice.setVisibility(View.VISIBLE);
                                Map<Currency, Locale> map = getCurrencyLocaleMap();
                                Locale locale = new Locale("en", singleAttachmentObject.optString("currency"));
                                Currency currency = Currency.getInstance(singleAttachmentObject.optString("currency"));
                                String symbol = currency.getSymbol(map.get(currency)).replace("US", "");
                                Log.d("TestPrice ", symbol+" \uf155  " + singleAttachmentObject.optString("currency")
                                        + " " + singleAttachmentObject.optDouble("price"));
                                listItemHolder.tvPrice.setText(symbol+" " + singleAttachmentObject.optString("currency")
                                        + " " + singleAttachmentObject.optDouble("price"));
                            } else {
                                listItemHolder.tvPrice.setVisibility(View.GONE);
                            }

                        default:
                            listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
                            listItemHolder.ivSticker.setVisibility(View.GONE);
                            if (!(attachmentType.contains("_photo") || (listItemHolder.mFeedAttachmentType != null
                                    && listItemHolder.mFeedAttachmentType.contains("_photo")
                                    && listItemHolder.mFeedType.equals("share")))) {

                                if (!listItemHolder.mFeedAttachmentType.equals("advancedactivity_sell")) {
                                    listItemHolder.tvPrice.setVisibility(View.GONE);
                                    listItemHolder.tvLocation.setVisibility(View.GONE);
                                    listItemHolder.mAttachmentTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                            mContext.getResources().getDimension(R.dimen.body_default_font_size));
                                }

                                if (attachmentTitle != null && !attachmentTitle.isEmpty()
                                        && !attachmentTitle.equals("null")) {

                                    listItemHolder.mAttachmentTitle.setVisibility(View.VISIBLE);
                                    listItemHolder.mAttachmentTitle.setText(attachmentTitle);
                                    listItemHolder.mAttachmentTitle.setMovementMethod
                                            (LinkMovementMethod.getInstance());

                                } else {
                                    listItemHolder.mAttachmentTitle.setVisibility(View.GONE);
                                }

                                if (attachmentBody != null && !attachmentBody.isEmpty()) {
                                    listItemHolder.mAttachmentView.setVisibility(View.VISIBLE);
                                    listItemHolder.mAttachmentBody.setVisibility(View.VISIBLE);

                                    //Checking feed is singlefeed or activity feed
                                    // if feed is single then showing post with large text else showing a "See More" option(if text too large)
                                    //Checking if post length is grater than 300 then showing "See More" option else showing full post
                                    if (!mIsSingleFeed && attachmentBody.trim().length() >
                                            ConstantVariables.FEED_TITLE_BODY_LENGTH) {

                                        String contentAttachment = attachmentType;
                                        if (listItemHolder.mFeedAttachmentType != null
                                                && listItemHolder.mFeedType.equals("share")) {
                                            contentAttachment = listItemHolder.mFeedAttachmentType;
                                        }
                                        seeMoreOption(listItemHolder.mAttachmentBody,
                                                attachmentBody,
                                                listItemHolder, contentAttachment, false);
                                    } else {
                                        showFullAttachmentBody(listItemHolder, attachmentBody);
                                    }
                                } else {
                                    listItemHolder.mAttachmentBody.setVisibility(View.GONE);
                                }
                            } else {
                                listItemHolder.mAttachmentUrlView.setVisibility(View.GONE);
                            }
                            break;
                    }

                } else {
                    listItemHolder.ivSticker.setVisibility(View.GONE);
                    listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
                    listItemHolder.mAttachmentTitle.setVisibility(View.GONE);
                    listItemHolder.mAttachmentBody.setVisibility(View.GONE);
                    listItemHolder.tvPrice.setVisibility(View.GONE);
                    listItemHolder.tvLocation.setVisibility(View.GONE);
                }

            } else if (mode == 3) {
                listItemHolder.mMusicAttachmentView.setVisibility(View.GONE);
                listItemHolder.ivSticker.setVisibility(View.GONE);
                // Show Attachment Body in mode 3
                String attachmentBody = singleAttachmentObject.optString("body");
                if (attachmentBody != null && !attachmentBody.isEmpty()) {
                    listItemHolder.mAttachmentView.setVisibility(View.VISIBLE);
                    listItemHolder.mAttachmentBody.setVisibility(View.VISIBLE);
                    listItemHolder.mAttachmentBody.setText(attachmentBody.trim());

                    // Showing attachment url in card view.
                    if (listItemHolder.mFeedType.equals("share") &&
                            listItemHolder.mWebUrl != null &&
                            !listItemHolder.mWebUrl.isEmpty()) {
                        listItemHolder.mAttachmentUrlView.setText(Html.fromHtml(listItemHolder.mWebUrl).toString().trim());
                        listItemHolder.mAttachmentUrlView.setVisibility(View.VISIBLE);
                    } else {
                        listItemHolder.mAttachmentUrlView.setVisibility(View.GONE);
                    }
                } else {
                    listItemHolder.mAttachmentBody.setVisibility(View.GONE);
                }
                listItemHolder.mImagesGallery.setVisibility(View.GONE);
                listItemHolder.rlSingleImageLayout.setVisibility(View.GONE);
                listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
            }
        }

        if (mPhotoUrls != null && mPhotoUrls.size() != 0) {
            if (mIsSingleFeed) {
                listItemHolder.mImagesGallery.setNestedScrollingEnabled(false);
            }
            adapter = new ImageAdapter((Activity) mContext, mPhotoUrls,
                    new OnItemClickListener() {

                        @Override
                        public void onItemClick(View view, int position) {
                            if (listItemHolder.itemViewType == 12 ) return;
                            mSelectedFeedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());
                            Intent intent = new Intent(mContext, SingleFeedPage.class);
                            intent.putExtra(ConstantVariables.ITEM_POSITION, listItemHolder.getAdapterPosition());
                            intent.putExtra(ConstantVariables.ACTION_ID, mSelectedFeedList.getmActionId());
                            intent.putExtra(ConstantVariables.IS_MULTI_PHOTO_FEED, true);

                            intent.putExtra("reactionEnabled", mReactionsEnabled);
                            if (mReactions != null) {
                                intent.putExtra("reactions", mReactions.toString());
                            }
                            if (mFeedsFragment != null) {
                                mFeedsFragment.startActivityForResult(intent, ConstantVariables.VIEW_SINGLE_FEED_PAGE);
                            } else {
                                (mContext).startActivity(intent);
                            }
                        }
                    });
            listItemHolder.mImagesGallery.setAdapter(adapter);

        } else {
            listItemHolder.mImagesGallery.setVisibility(View.GONE);
        }

        if (mPhotoList != null && mPhotoList.size() > 0) {
            listItemHolder.mMultiPhotoRecyclerView.setVisibility(View.VISIBLE);
            mMultiPhotoRecyclerAdapter = new MultiPhotoRecyclerAdapter(mContext, mReactionsEnabled, mPhotoList,
                    mReactions, mReactionsArray);
            listItemHolder.mMultiPhotoRecyclerView.setAdapter(mMultiPhotoRecyclerAdapter);
            mMultiPhotoRecyclerAdapter.notifyDataSetChanged();
        } else {
            listItemHolder.mMultiPhotoRecyclerView.setVisibility(View.GONE);
        }

        if (mSellImageList != null && mSellImageList.size() != 0) {
            float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    AppConstant.GRID_PADDING, mContext.getResources().getDisplayMetrics());
            if (mSellImageList.size() == 1) {
                columnWidth = AppConstant.getDisplayMetricsWidth(mContext)
                        - mContext.getResources().getDimensionPixelSize(R.dimen.margin_30dp);
            } else {
                columnWidth = (int) ((mAppConst.getScreenWidth() - (11 * padding)) / (1.3));
            }
            listItemHolder.sellPhotosRecyclerView.setVisibility(View.VISIBLE);
            sellPhotoAdapter = new CustomImageAdapter((Activity) mContext, mSellImageList,
                    columnWidth, new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });
            listItemHolder.sellPhotosRecyclerView.setAdapter(sellPhotoAdapter);
            sellPhotoAdapter.notifyDataSetChanged();

        } else {
            listItemHolder.sellPhotosRecyclerView.setVisibility(View.GONE);
        }

            /* End: Show Attachment Info */

    }

    /**
     * Method to make spannable string with the word styling.
     *
     * @param wordStylingClickableParts Clickable parts which needs to shown according to styling.
     * @param title                     Title which contains the whole body.
     * @param text                      Existing spannable string on which new styling string need to be added.
     * @return Returns the updated SpannableString.
     */
    private SpannableString checkForWordStyling(HashMap<String, String> wordStylingClickableParts,
                                                CharSequence title, SpannableString text) {
        SortedSet<String> styleKeys = new TreeSet<>(wordStylingClickableParts.keySet());

        int endIndex = 0;
        for (String key : styleKeys) {

            JSONObject wordObject = null;
            try {
                wordObject = new JSONObject(wordStylingClickableParts.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String value = wordObject.optString("title");

            if (value != null && !value.isEmpty()) {
                int i1 = title.toString().toLowerCase().indexOf(value.toLowerCase(), endIndex);
                if (i1 != -1) {
                    int i2 = i1 + value.length();
                    if (endIndex != -1) {
                        endIndex += value.length();
                    }
                    int textColor = 0, bgColor = 0;
                    try {
                        textColor = Color.parseColor(wordObject.optString("color"));
                        bgColor = Color.parseColor(wordObject.optString("background_color"));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                    final String style = wordObject.optString("style");
                    JSONObject param = wordObject.optJSONObject("params");
                    final int inBGEnabled = param.optInt("bg_enabled", 0);

                    final int finalBgColor = bgColor;
                    final int finalTextColor = textColor;
                    ClickableSpan myClickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            //TODO, Do Animation work in future.
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            if (inBGEnabled == 1 && finalBgColor != 0) {
                                ds.bgColor = finalBgColor;
                            }
                            if (finalTextColor != 0) {
                                ds.setColor(finalTextColor);
                            }
                            ds.setFakeBoldText(true);
                            if (style.equals("italic")) {
                                ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                            } else {
                                ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                            }
                        }
                    };
                    text.setSpan(myClickableSpan, i1, i2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return text;
    }

    /**
     * Method Update comment count on photos when backed from comment page.
     *
     * @param bundle Bundle which contains all data.
     */
    public void updateMultiPhotoComment(Bundle bundle) {
        int photoPosition = bundle.getInt(ConstantVariables.PHOTO_POSITION);
        int photoCommentCount = bundle.getInt(ConstantVariables.PHOTO_COMMENT_COUNT);
        if (mPhotoList != null && mPhotoList.size() > 0 && mPhotoList.get(photoPosition) != null) {
            PhotoListDetails photoListDetails = mPhotoList.get(photoPosition);
            photoListDetails.setmImageCommentCount(photoCommentCount);
            mMultiPhotoRecyclerAdapter.notifyItemChanged(photoPosition);
        }

    }

    /**
     * Method to update Photo List when backed from PhotoLightBox
     *
     * @param photoList Updated list.
     */
    public void updatePhotosDetail(ArrayList<PhotoListDetails> photoList) {
        if (mPhotoList != null) {
            mPhotoList.clear();
            mPhotoList.addAll(photoList);
            mMultiPhotoRecyclerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Method to set first four filter block's data.
     *
     * @param filterBlock     Filter block.
     * @param tvFilterIcon    Filter's icon.
     * @param tvFilter        Title view of filter.
     * @param ivBgShapeFilter ImageView on which color is filled.
     * @param filterType      Filter type.
     * @param filterTitle     Title of filter.
     * @param filterIconColor Filter icon color.
     */
    private void setFilterDataInViews(View filterBlock, TextView tvFilterIcon, TextView tvFilter,
                                      ImageView ivBgShapeFilter, String filterType, String filterTitle, int filterIconColor) {
        filterBlock.setTag(filterType);
        try {
            tvFilterIcon.setText(new String(
                    Character.toChars(Integer.parseInt(getFilterIcon(filterType), 16))));
        } catch (NumberFormatException e) {
            tvFilterIcon.setText("\uF08B");
        }
        ivBgShapeFilter.setColorFilter(ContextCompat.getColor(mContext, filterIconColor),
                PorterDuff.Mode.SRC_IN);
        tvFilter.setText(filterTitle);
    }

    /**
     * Method to set filter according to view.
     *
     * @param view Clicked view.
     */
    private void setFilter(View view) {
        String filterType = (String) view.getTag();
        if (this.mFilterSelectedListener != null && filterType != null) {
            mFilterSelectedListener.setFilterType(filterType);
        }
    }

    /**
     * Method to set Alpha of selected and non-selected views.
     *
     * @param selectedView    Selected text view.
     * @param unselectedView1 Non-Selected view.
     * @param unselectedView2 Non-Selected view.
     * @param unselectedView3 Non-Selected view.
     * @param unselectedView4 Non-Selected view.
     */
    private void setSelectedView(View selectedView, View unselectedView1, View unselectedView2,
                                 View unselectedView3, View unselectedView4) {
        selectedView.setAlpha(1);
        unselectedView1.setAlpha(0.5f);
        unselectedView2.setAlpha(0.5f);
        unselectedView3.setAlpha(0.5f);
        unselectedView4.setAlpha(0.5f);
    }

    /**
     * Method to get filter icon according to filter type.
     *
     * @param filterType Filter type for which icon need to be set.
     * @return Returns the filter icon.
     */
    private String getFilterIcon(String filterType) {
        String icon;
        switch (filterType) {

            case "more_filter":
                icon = "f141";
                break;

            case "all":
                icon = "f0ac";
                break;

            case "membership":
                icon = "f007";
                break;

            case "photo":
                icon = "f03e";
                break;

            case "video":
                icon = "f03d";
                break;

            case "sitepage":
                icon = "f15c";
                break;

            case "posts":
                icon = "f044";
                break;

            case "sitenews":
                icon = "f09e";
                break;

            case "siteevent":
                icon = "f073";
                break;

            case "group":
            case "sitegroup":
                icon = "f0c0";
                break;

            case "hidden_post":
                icon = "f070";
                break;

            case "schedule_post":
                icon = "f017";
                break;

            case "memories":
                icon = "f274";
                break;

            case "like":
                icon = "f164";
                break;

            case "advertise":
                icon = "f02c";
                break;

            case "classified":
                icon = "f03a";
                break;

            case "poll":
                icon = "f080";
                break;

            case "sitetagcheckin":
                icon = "f041";
                break;

            case "sitestore":
                icon = "f290";
                break;

            case "sitestoreproduct":
                icon = "f291";
                break;

            case "user_saved":
                icon = "f0c7";
                break;

            case "sitereview_listtype_11":
                icon = "f040";
                break;

            case "sitereview_listtype_14":
                icon = "f03a";
                break;

            case "music":
                icon = "f001";
                break;

            case "sitereview_listtype_19":
                icon = "f072";
                break;

            case "sitereview_listtype_18":
                icon = "f15c";
                break;

            case "sitereview_listtype_20":
                icon = "f0f2";
                break;

            case "network_list":
                icon = "f233";
                break;

            default:
                icon = "f08b";
                if (filterType.contains("sitereview_listtype")) {
                    icon = "f03a";
                }
                break;
        }

        return icon;
    }

    /**
     * Method to launch single feed page on click of comment.
     *
     * @param listItemHolder ListItemHolder instance of clicked item.
     */
    private void launchSingleFeedPage(ListItemHolder listItemHolder) {
        FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());
        Intent intent = new Intent(mContext, SingleFeedPage.class);
        intent.putExtra(ConstantVariables.ITEM_POSITION, listItemHolder.getAdapterPosition());

        if (feedList.getmIsShareFeed()) {
            intent.putExtra(ConstantVariables.ACTION_ID, feedList.getmShareActionId());
        } else {
            intent.putExtra(ConstantVariables.ACTION_ID, feedList.getmActionId());
        }

        if (feedList.getmAttachmentCount() > 1) {
            intent.putExtra(ConstantVariables.IS_MULTI_PHOTO_FEED, true);
        }

        // Put Reactions params if Reactions are enabled
        intent.putExtra("reactionEnabled", mReactionsEnabled);
        if (mReactions != null) {
            intent.putExtra("reactions", mReactions.toString());
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (mFeedsFragment != null) {
            mFeedsFragment.startActivityForResult(intent, ConstantVariables.VIEW_SINGLE_FEED_PAGE);
        } else {
            (mContext).startActivity(intent);
        }
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private LinearLayout.LayoutParams getSingleImageParamFromWidthHeight(JSONObject imageMainObj, ImageView attachmentView) {
        LinearLayout.LayoutParams singleImageParam;
        JSONObject size = imageMainObj.optJSONObject("size");

        if (size.optInt("width") != 0 && size.optInt("height") != 0) {
            String imageUrl = imageMainObj.optString("src");
            if (imageUrl.contains("gif") && size.optInt("width") < size.optInt("height")) {
                singleImageParam = CustomViews.getCustomWidthHeightLayoutParams(
                        mAppConst.getScreenWidth(), size.optInt("height"));
                attachmentView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            } else if (size.optInt("width") == size.optInt("height")) {
                singleImageParam = CustomViews.getCustomWidthHeightLayoutParams(
                        mAppConst.getScreenWidth(), mAppConst.getScreenWidth());
                attachmentView.setScaleType(ImageView.ScaleType.FIT_XY);

            } else {
                float ratio = ((float) size.optInt("height") / (float) size.optInt("width")) *
                        (mAppConst.getScreenWidth() - size.optInt("width"));
                singleImageParam = CustomViews.getCustomWidthHeightLayoutParams(
                        mAppConst.getScreenWidth(), size.optInt("height") + Math.round(ratio));
                attachmentView.setScaleType(ImageView.ScaleType.FIT_XY);
            }

        } else {
            singleImageParam = CustomViews.getCustomWidthHeightLayoutParams(
                    mAppConst.getScreenWidth(),
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        return singleImageParam;
    }

    @Override
    public int getItemCount() {
        return mFeedItemList.size();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        Bundle feedPostMenus = new Bundle();
        switch (id) {
            case R.id.status_text_layout:
                feedPostMenus.putBoolean("showPhotoBlock", false);
                feedPostMenus.putBoolean("openCheckIn", false);
                feedPostMenus.putBoolean("openVideo", false);
                break;
            case R.id.videoMenu:
                feedPostMenus.putBoolean("showPhotoBlock", false);
                feedPostMenus.putBoolean("openCheckIn", false);
                feedPostMenus.putBoolean("openVideo", true);
                break;
            case R.id.photoMenu:
                feedPostMenus.putBoolean("showPhotoBlock", true);
                feedPostMenus.putBoolean("openCheckIn", false);
                feedPostMenus.putBoolean("openVideo", false);
                break;
            case R.id.checkInMenu:
                feedPostMenus.putBoolean("showPhotoBlock", false);
                feedPostMenus.putBoolean("openCheckIn", true);
                feedPostMenus.putBoolean("openVideo", false);
                break;
        }
        if (mFeedPostMenus != null && mFeedPostMenus.length() != 0) {
            feedPostMenus.putString("feedPostMenus", mFeedPostMenus.toString());
        }
        Intent statusIntent = new Intent(mContext, Status.class);
        statusIntent.putExtra(ConstantVariables.SUBJECT_TYPE, mSubjectType);
        statusIntent.putExtra(ConstantVariables.SUBJECT_ID, mFeedSubjectId);
        statusIntent.putExtras(feedPostMenus);

        if (mFeedsFragment != null && (id == R.id.photoMenu || id == R.id.videoMenu || id == R.id.checkInMenu || id == R.id.status_text_layout)) {
            mFeedsFragment.startActivityForResult(statusIntent, ConstantVariables.FEED_REQUEST_CODE);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (id == R.id.videoMenu || id == R.id.status_text_layout) {
            ((Activity) mContext).startActivityForResult(statusIntent, ConstantVariables.FEED_REQUEST_CODE);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (mFeedsFragment != null) {
                mFeedsFragment.checkManifestPermissions(statusIntent, Manifest.permission.ACCESS_FINE_LOCATION,
                        ConstantVariables.ACCESS_FINE_LOCATION, true, false);
        }
    }

    /**
     * Method to reset people suggestion adapter.
     *
     * @param isAdapterSet true if need to reset.
     */
    public void isPeopleSuggestionAdapterSet(boolean isAdapterSet) {
        this.isAdapterSet = isAdapterSet;
    }

    /**
     * Initiate the saving of feed
     *
     * @param view
     * @param listItemHolder
     * @param itemPosition
     */
    private void initiateSaveFeed(View view, ListItemHolder listItemHolder, int itemPosition){
        final FeedList feedInfoList = (FeedList) mFeedItemList.get(itemPosition);
        JSONArray menuArray = feedInfoList.getmFeedMenusArray();
        // final int actionID = feedInfoList.getmActionId();
        // final JSONObject feedJsonObject = feedInfoList.getmFeedFooterMenus();

        mGutterMenuUtils.mFeedList = feedInfoList;
        mGutterMenuUtils.onMenuItemSelected(null, 2, menuArray, true, false, null);

        // TODO: Enable the showing of dialog.
        // mAppConst.showProgressDialog();

        // The index of 2 is from the header menu of the feed which is Save Feed
        // TODO: This would mimic the execution of save feed from Gutter menu. Feel free to change if you want a new implementation.
//        try {
//            JSONArray menuArray = feedInfoList.getmFeedMenusArray();
//
//            JSONObject menuJsonObject = menuArray.optJSONObject(2);
//            JSONObject urlParams = menuJsonObject.optJSONObject("urlParams");
//            String redirectUrl = AppConstant.DEFAULT_URL + menuJsonObject.optString("url");
//
//            if (urlParams != null && urlParams.length() != 0) {
//                JSONArray urlParamsNames = urlParams.names();
//
//                for (int j = 0; j < urlParams.length(); j++) {
//                    String name = urlParamsNames.getString(j);
//                    String value = urlParams.getString(name);
//
//                    mPostParams.put(name, value);
//                }
//
//                redirectUrl = mAppConst.buildQueryString(redirectUrl, mPostParams);
//
//
//
//                // Execute POST API for Saving of feed
//                mAppConst.postJsonResponseForUrl(redirectUrl, mPostParams, new OnResponseListener() {
//                    @Override
//                    public void onTaskCompleted(JSONObject jsonObject) {
//                        feedInfoList.setmIsSaveFeedOption(feedInfoList.getmIsSaveFeedOption() == 1 ? 0 : 1);
//
//                        if (mOnMenuClickResponseListener != null) {
//                            switch (mMenuName) {
//                                case "hide":
//                                case "report_feed":
//                                    mOnMenuClickResponseListener.onItemActionSuccess(mPosition,
//                                            updateFeedInfo(jsonObject, mFeedList), mMenuName);
//                                    break;
//
//                                case "disable_comment":
//                                case "lock_this_feed":
//                                case "update_save_feed":
//                                case "unpin_post":
//                                    mOnMenuClickResponseListener.onItemActionSuccess(mPosition, mFeedList, mMenuName);
//                                    break;
//
//                                default:
//                                    mOnMenuClickResponseListener.onItemActionSuccess(mPosition, mBrowseListItems,
//                                            mMenuName);
//                                    break;
//                            }
//                        }
//
//                        if (mOnOptionItemClickResponseListener != null) {
//                            if (mMenuName.equals("favourite")) {
//                                mOnOptionItemClickResponseListener.onOptionItemActionSuccess(mBrowseListItems, mMenuName);
//                            } else if (mMenuName.equals("make_profile_photo") && mMainView != null) {
//                                mAppConst.refreshUserData();
//                                SnackbarUtils.displaySnackbar(mMainView,
//                                        mContext.getResources().getString(R.string.profile_photo_updated));
//                            }
//                        }
//                        mAppConst.hideProgressDialog();
//                    }
//
//                    @Override
//                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
//                        mAppConst.hideProgressDialog();
//                        SnackbarUtils.displaySnackbar(mMainView, message);
//                    }
//                });
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


    }

    /**
     * Function Called to change Like/Unlike Option in footer Menus
     */
    public void doLikeUnlike(final int position, String reaction, boolean isReactionChanged) {
        final Map<String, String> likeParams = new HashMap<>();
        final FeedList feedInfoList = (FeedList) mFeedItemList.get(position);
        final int action_id = feedInfoList.getmActionId();
        likeParams.put("action_id", String.valueOf(action_id));

        if (reaction != null) {
            likeParams.put("reaction", reaction);
        }

        final JSONObject likeJsonObject = feedInfoList.getmFeedFooterMenus().optJSONObject("like");

        String likeUnlikeUrl = AppConstant.DEFAULT_URL;
        final String sendLikeNotificationUrl = AppConstant.DEFAULT_URL + "advancedactivity/send-like-notitfication";
        if (feedInfoList.getmIsLike() == 0) {
            feedInfoList.setmIsLike(1);
            notifyItemChanged(position);
            likeUnlikeUrl += "/advancedactivity/like?sendNotification=0";

            mAppConst.postJsonResponseForUrl(likeUnlikeUrl, likeParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {

                    try {
                        likeJsonObject.put("label", "Unlike");
                        likeJsonObject.put("name", "unlike");
                        feedInfoList.setmFeedFooterMenus(feedInfoList.getmFeedFooterMenus().put("like", likeJsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (mIsSingleFeed && mClickedFeedPosition != -1) {
                        if (mReactionsEnabled == 1) {
                            PreferencesUtils.updateFeedReactionsPref(mContext, PreferencesUtils.FEED_REACTIONS, feedInfoList.getmFeedReactions());
                            PreferencesUtils.updateFeedReactionsPref(mContext, PreferencesUtils.MY_FEED_REACTIONS, feedInfoList.getmMyFeedReactions());
                        }
                    }

                    updatePhotoLikeCommentCount(position);

                    /* Calling to send notifications after like action */
                    mAppConst.postJsonRequest(sendLikeNotificationUrl, likeParams);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    try {
                        likeJsonObject.put("label", "Like");
                        likeJsonObject.put("name", "like");
                        feedInfoList.setmFeedFooterMenus(feedInfoList.getmFeedFooterMenus().put("like", likeJsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            if (isReactionChanged) {
                likeUnlikeUrl += "/advancedactivity/like?sendNotification=1";
                feedInfoList.setmIsLike(1);
            } else {
                likeUnlikeUrl += "/advancedactivity/unlike";
                feedInfoList.setmIsLike(0);
            }

            notifyItemChanged(position);

            mAppConst.postJsonResponseForUrl(likeUnlikeUrl, likeParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {

                    try {
                        likeJsonObject.put("label", "Like");
                        likeJsonObject.put("name", "like");
                        feedInfoList.setmFeedFooterMenus(feedInfoList.getmFeedFooterMenus().put("like", likeJsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (mIsSingleFeed && mClickedFeedPosition != -1) {
                        if (mReactionsEnabled == 1) {
                            PreferencesUtils.updateFeedReactionsPref(mContext, PreferencesUtils.FEED_REACTIONS, feedInfoList.getmFeedReactions());
                            PreferencesUtils.updateFeedReactionsPref(mContext, PreferencesUtils.MY_FEED_REACTIONS, feedInfoList.getmMyFeedReactions());
                        }
                    }

                    updatePhotoLikeCommentCount(position);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    try {
                        likeJsonObject.put("label", "Unlike");
                        likeJsonObject.put("name", "unlike");
                        feedInfoList.setmFeedFooterMenus(feedInfoList.getmFeedFooterMenus().put("like", likeJsonObject));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Method to update photo like and comment count.
     *
     * @param position Position of item on which count is to be update.
     */
    public void updatePhotoLikeCommentCount(int position) {
        try {
            final FeedList feedInfoList = (FeedList) mFeedItemList.get(position);
            if (feedInfoList.getmPhotoAttachmentCount() == 1) {
                JSONArray feedAttachmentArray = feedInfoList.getmFeedAttachmentArray();
                JSONObject singleAttachmentObject = feedAttachmentArray.optJSONObject(0);
                try {
                    singleAttachmentObject.put("likes_count", feedInfoList.getmLikeCount());
                    singleAttachmentObject.put("comment_count", feedInfoList.getmCommentCount());
                    singleAttachmentObject.put("is_like", feedInfoList.getmIsLike());
                    if (mReactionsEnabled == 1 && PreferencesUtils.isNestedCommentEnabled(mContext)) {
                        JSONObject reactionsObject = new JSONObject();
                        reactionsObject.put("feed_reactions", feedInfoList.getmFeedReactions());
                        reactionsObject.put("my_feed_reaction", feedInfoList.getmMyFeedReactions());
                        singleAttachmentObject.put("reactions", reactionsObject);
                    }
                    feedAttachmentArray.put(0, singleAttachmentObject);
                    feedInfoList.setmFeedAttachmentArray(feedAttachmentArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                notifyItemChanged(position);
            }
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    /**
     * Function Called to Comment on Feeds.
     *
     * @param isCommentButton
     */

    public void showComments(boolean isCommentButton) {

        FeedList feedInfoList = (FeedList) mFeedItemList.get(itemPosition);
        Intent commentIntent;

        String mLikeCommentsUrl = AppConstant.DEFAULT_URL + "advancedactivity/feeds/likes-comments";

        /* In case of Single feed page we will redirect it to direct Like Page*/
        if (mIsSingleFeed && feedInfoList.getmAttachmentCount() > 1 && isCommentButton) {
            commentIntent = getCommentIntent(mLikeCommentsUrl, feedInfoList);
        } else if (!(!mIsSingleFeed || isPhotoFeed)) {
            commentIntent = getLikeIntent(mLikeCommentsUrl, feedInfoList);
        } else {
            commentIntent = getCommentIntent(mLikeCommentsUrl, feedInfoList);
        }
        if (mFeedsFragment != null) {
            mFeedsFragment.startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
        } else {
            ((Activity) mContext).startActivityForResult(commentIntent, ConstantVariables.VIEW_COMMENT_PAGE_CODE);
        }
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private Intent getCommentIntent(String mLikeCommentsUrl, FeedList feedInfoList) {
        mLikeCommentsUrl += "?subject_type=activity_action" + "&subject_id=" + feedInfoList.getmActionId() + "&viewAllComments=1&page=1&limit=20";
        Intent commentIntent = new Intent(mContext, Comment.class);
        commentIntent.putExtra(ConstantVariables.ITEM_POSITION, itemPosition);
        commentIntent.putExtra(ConstantVariables.LIKE_COMMENT_URL, mLikeCommentsUrl);
        commentIntent.putExtra("reactionsEnabled", mReactionsEnabled);
        commentIntent.putExtra(ConstantVariables.SUBJECT_TYPE, ConstantVariables.AAF_MENU_TITLE);
        commentIntent.putExtra(ConstantVariables.SUBJECT_ID, feedInfoList.getmActionId());
        commentIntent.putExtra(ConstantVariables.ACTION_ID, feedInfoList.getmActionId());

        if(feedInfoList.getmFeedReactions() != null){
            commentIntent.putExtra("popularReactions", feedInfoList.getmFeedReactions().toString());
        }
        return commentIntent;
    }

    private Intent getLikeIntent(String mLikeCommentsUrl, FeedList feedInfoList) {
        Intent commentIntent;

        mLikeCommentsUrl += "?viewAllLikes=1&action_id=" + feedInfoList.getmActionId();
        commentIntent = new Intent(mContext, Likes.class);
        commentIntent.putExtra("reactionsEnabled", mReactionsEnabled);
        if (mReactionsEnabled == 1) {
            mLikeCommentsUrl = UrlUtil.AAF_VIEW_REACTIONS_URL;
            if (feedInfoList.getmActionId() != 0) {
                mLikeCommentsUrl += "?action_id=" + feedInfoList.getmActionId();
            } else {
                mLikeCommentsUrl += "?subject_type=" + mSubjectType +
                        "&subject_id=" + mFeedSubjectId;
                ;
            }
        } else {
            //Changing url for the activity_action.
            if (mSubjectType.equals("activity_action")) {
                mLikeCommentsUrl += "?viewAllLikes=1&action_id=" + feedInfoList.getmActionId();
            } else {
                mLikeCommentsUrl = UrlUtil.VIEW_LIKES_URL + "&subject_type=" + mSubjectType + "&subject_id=" + mFeedSubjectId;
            }
        }
        commentIntent.putExtra("ViewAllLikesUrl", mLikeCommentsUrl);
        return commentIntent;
    }

    /**
     * Method to check for parameter before transfer to respective activity.
     *
     * @param title          Title of the feed.
     * @param slug           Slug in case of forum.
     * @param attachmentType Type of attachment.
     * @param attachmentId   Id of attachment.
     * @param listItemHolder ListItemHolder for current selected item.
     * @param videoInfo      video info.
     */
    public void redirectToActivity(String title, String slug, String attachmentType, int attachmentId,
                                   ListItemHolder listItemHolder, HashMap<Integer, String> videoInfo) {

        FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());

        if ((attachmentType.contains("video") && !attachmentType.equals("sitevideo_channel")
                && !attachmentType.equals("sitevideo_playlist"))
                || ((listItemHolder.mFeedAttachmentType != null
                && (listItemHolder.mFeedAttachmentType.contains("video")
                && !listItemHolder.mFeedAttachmentType.equals("sitevideo_channel")
                && !listItemHolder.mFeedAttachmentType.equals("sitevideo_playlist"))
                && listItemHolder.mFeedType.equals("share")))) {
            if (videoInfo != null && videoInfo.size() != 0) {
                String videoParams = videoInfo.get(attachmentId);
                String[] videoParts = videoParams.split("-");
                String videoType = videoParts[0];
                String videoUrl = videoParts[1];

                videoAttachmentClicked(listItemHolder.mFeedType, attachmentType,
                        listItemHolder.mFeedAttachmentType, videoType, videoUrl,
                        attachmentId, listItemHolder.mFeedObject,
                        listItemHolder.mFeedAttachmentArray.optJSONObject(0));
            }
        } else {
            int id;
            switch (attachmentType) {
                case "checkIn":
                    openMapActivity(title, feedList.getmLatitude(),
                            feedList.getmLongitude(), feedList.getmPlaceId());
                    break;

                case "sitereview_review":
                    id = listItemHolder.mFeedObject.optInt("listing_id",
                            listItemHolder.mFeedObject.optInt("resource_id"));
                    if (id == 0 && feedList.getmFeedAttachmentArray() != null) {
                        id = feedList.getmFeedAttachmentArray().optJSONObject(0).optInt("listing_id");
                    }
                    attachmentClicked(title, attachmentType, id, listItemHolder, slug);
                    break;

                case "sitepage_review":
                case "sitepagereview_review":
                    id = listItemHolder.mFeedObject.optInt("page_id",
                            listItemHolder.mFeedObject.optInt("resource_id"));
                    if (id == 0 && feedList.getmFeedAttachmentArray() != null) {
                        id = feedList.getmFeedAttachmentArray().optJSONObject(0).optInt("page_id");
                    }
                    attachmentClicked(title, attachmentType, id, listItemHolder, slug);
                    break;

                case "sitegroup_review":
                case "sitegroupreview_review":
                    id = listItemHolder.mFeedObject.optInt("group_id",
                            listItemHolder.mFeedObject.optInt("resource_id"));
                    if (id == 0 && feedList.getmFeedAttachmentArray() != null) {
                        id = feedList.getmFeedAttachmentArray().optJSONObject(0).optInt("group_id");
                    }
                    attachmentClicked(title, attachmentType, id, listItemHolder, slug);
                    break;

                default:
                    attachmentClicked(title, attachmentType, attachmentId, listItemHolder, slug);
                    break;
            }
        }
    }

    /**
     * Method to check for parameters before transfer to respective activity.
     *
     * @param attachmentType         Type of attachment.
     * @param attachmentId           Id of attachment.
     * @param singleAttachmentObject Single attachment object.
     * @param listItemHolder         ListItemHolder for current selected item.
     */
    public void redirectToAttachmentClickedActivity(String attachmentType, int attachmentId,
                                                    JSONObject singleAttachmentObject,
                                                    ListItemHolder listItemHolder) {
        int id;
        String attachmentTitle = singleAttachmentObject.optString("title");

        if ((attachmentType.contains("video") && !attachmentType.equals("sitevideo_channel")
                && !attachmentType.equals("sitevideo_playlist"))
                || ((listItemHolder.mFeedAttachmentType != null
                && (listItemHolder.mFeedAttachmentType.contains("video")
                && !listItemHolder.mFeedAttachmentType.equals("sitevideo_channel")
                && !listItemHolder.mFeedAttachmentType.equals("sitevideo_playlist"))
                && listItemHolder.mFeedType.equals("share")))) {

            videoAttachmentClicked(listItemHolder.mFeedType, listItemHolder.mFeedAttachmentType,
                    attachmentType, singleAttachmentObject.optString("attachment_video_type"),
                    singleAttachmentObject.optString("attachment_video_url"), attachmentId,
                    listItemHolder.mFeedObject, singleAttachmentObject);

        } else {
            switch (attachmentType) {

                case "music_playlist_song":
                    id = singleAttachmentObject.optInt("playlist_id");
                    attachmentClicked(attachmentTitle, attachmentType, id, listItemHolder, null);
                    break;

                case "sitereview_review":
                    id = listItemHolder.mFeedObject.optInt("listing_id",
                            listItemHolder.mFeedObject.optInt("resource_id"));
                    attachmentClicked(attachmentTitle, attachmentType, id, listItemHolder, null);
                    break;

                case "siteevent_review":
                    id = singleAttachmentObject.optInt("event_id");
                    attachmentClicked(attachmentTitle, attachmentType, id, listItemHolder, null);
                    break;

                case "sitepage_review":
                case "sitepagereview_review":
                    id = listItemHolder.mFeedObject.optInt("page_id",
                            listItemHolder.mFeedObject.optInt("resource_id"));
                    if (id == 0 && mFeedItem.getmFeedAttachmentArray() != null) {
                        id = mFeedItem.getmFeedAttachmentArray().optJSONObject(0).optInt("page_id");
                    }
                    attachmentClicked(attachmentTitle, attachmentType, id, listItemHolder, null);
                    break;

                case "sitegroup_review":
                case "sitegroupreview_review":
                    id = listItemHolder.mFeedObject.optInt("group_id",
                            listItemHolder.mFeedObject.optInt("resource_id"));
                    if (id == 0 && mFeedItem.getmFeedAttachmentArray() != null) {
                        id = mFeedItem.getmFeedAttachmentArray().optJSONObject(0).optInt("group_id");
                    }
                    attachmentClicked(attachmentTitle, attachmentType, id, listItemHolder, null);
                    break;

                default:
                    if (listItemHolder.mFeedAttachmentType != null
                            && listItemHolder.mFeedType.equals("share")) {

                        id = attachmentId;
                        switch (listItemHolder.mFeedAttachmentType) {
                            case "sitereview_review":
                                id = singleAttachmentObject.optInt("listing_id");
                                break;

                            case "siteevent_review":
                                id = singleAttachmentObject.optInt("event_id");
                                break;

                            case "sitegroup_review":
                            case "sitegroupreview_review":
                                id = singleAttachmentObject.optInt("group_id");
                                break;

                            case "sitepage_review":
                            case "sitepagereview_review":
                                id = singleAttachmentObject.optInt("page_id");
                                break;
                        }
                        attachmentClicked(attachmentTitle, listItemHolder.mFeedAttachmentType, id,
                                listItemHolder, null);

                    } else {
                        attachmentClicked(attachmentTitle, attachmentType, attachmentId, listItemHolder, null);
                    }
                    break;
            }
        }
    }

    /**
     * Method to redirect attachments on click.
     *
     * @param title          Attachment Title.
     * @param attachmentType Which decide where to redirect the attachment.
     * @param attachmentId   The id of attachment.
     * @param listItemHolder ListItemHolder for current selected item.
     * @param slug           Slug in case of forum.
     */
    public void attachmentClicked(String title, String attachmentType, int attachmentId,
                                  ListItemHolder listItemHolder, String slug) {

        Intent viewIntent;
        int listingTypeId = 0;
        FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());
        if (feedList.itemViewType == 12 ) return;
        if (attachmentType.equals("user")) {
            if (mModuleName != null && !mModuleName.isEmpty() && mModuleName.equals("userProfile")) {
                if (mFeedSubjectId != attachmentId) {
                    viewIntent = new Intent(mContext, userProfile.class);
                    viewIntent.putExtra(ConstantVariables.USER_ID, attachmentId);
                    viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ((Activity) mContext).startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            } else {
                viewIntent = new Intent(mContext, userProfile.class);
                viewIntent.putExtra(ConstantVariables.USER_ID, attachmentId);
                if (feedList.getSubjectProfileInfo() != null) {
                    viewIntent.putExtra(ConstantVariables.PROFILE_QUICK_INFO, feedList.getSubjectProfileInfo().toString());
                }
                viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ((Activity) mContext).startActivityForResult(viewIntent, ConstantVariables.USER_PROFILE_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        } else if (attachmentType.equals("activity_action") || attachmentType.equals("activity_action_app_post")) {
            Intent intent = new Intent(mContext, SingleFeedPage.class);
            intent.putExtra(ConstantVariables.ITEM_POSITION, listItemHolder.getAdapterPosition());
            intent.putExtra(ConstantVariables.ACTION_ID, attachmentId);
            if (feedList.getmAttachmentCount() > 1) {
                intent.putExtra(ConstantVariables.IS_MULTI_PHOTO_FEED, true);
            }

            // We are not update feeds data when user going to someone's liked post's feeds page
            if (mFeedsFragment != null && !attachmentType.equals("activity_action_app_post")) {
                mFeedsFragment.startActivityForResult(intent, ConstantVariables.VIEW_SINGLE_FEED_PAGE);
            } else {
                (mContext).startActivity(intent);
            }
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (mIncludedModulesList.contains(attachmentType)
                && !mDeletedModulesList.contains(attachmentType)) {

            /*
              Do not make Groups/Events name clickable if the feeds are loading on group profile page.
             */
            if (!((attachmentType.equals("group") || attachmentType.equals("event")) &&
                    mModuleName != null && !mModuleName.isEmpty() && mModuleName.equals("groupEventProfile")
                    && (mFeedSubjectId == attachmentId))) {

                if (attachmentType.equals("sitestoreproduct_review")) {
                    viewIntent = GlobalFunctions.getIntentForModule(mContext,
                            listItemHolder.mFeedObject.optInt("product_id"), attachmentType, slug);

                } else if (attachmentType.equals(ConstantVariables.ALBUM_PHOTO_MENU_TITLE)) {
                    viewIntent = GlobalFunctions.getIntentForSubModule(mContext, attachmentId,
                            listItemHolder.mFeedAttachmentArray.optJSONObject(0).optInt("album_id"), attachmentType);

                } else {
                    viewIntent = GlobalFunctions.getIntentForModule(mContext, attachmentId, attachmentType, slug);
                }

                switch (attachmentType) {
                    case ConstantVariables.FORUM_TITLE:
                    case "form_topic":
                        // Add Slug in Forum Urls
                        viewIntent.putExtra(ConstantVariables.CONTENT_TITLE, title);
                        break;

                    case ConstantVariables.MLT_MENU_TITLE:
                        if (listItemHolder.mFeedObject.optInt("listingtype_id") != 0) {
                            listingTypeId = listItemHolder.mFeedObject.optInt("listingtype_id");
                        } else if (listItemHolder.mFeedAttachmentArray != null) {
                            listingTypeId = listItemHolder.mFeedAttachmentArray.optJSONObject(0).optInt("listingtype_id");
                        }
                        viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, listingTypeId);
                        break;

                    case ConstantVariables.MLT_REVIEW_MENU_TITLE:
                        if (listItemHolder.mFeedObject.optInt("listingtype_id") != 0) {
                            listingTypeId = listItemHolder.mFeedObject.optInt("listingtype_id");
                        } else if (listItemHolder.mFeedAttachmentArray != null) {
                            listingTypeId = listItemHolder.mFeedAttachmentArray.optJSONObject(0).optInt("listingtype_id");
                        }
                        viewIntent.putExtra(ConstantVariables.LISTING_TYPE_ID, listingTypeId);
                        break;

                    case ConstantVariables.MLT_WISHLIST_MENU_TITLE:
                        if (listItemHolder.mFeedObject.has("title")) {
                            viewIntent.putExtra(ConstantVariables.CONTENT_TITLE,
                                    listItemHolder.mFeedObject.optString("title"));
                        } else if (listItemHolder.mFeedAttachmentArray != null) {
                            viewIntent.putExtra(ConstantVariables.CONTENT_TITLE,
                                    listItemHolder.mFeedAttachmentArray.optJSONObject(0).optString("title"));
                        }
                        break;
                }

                if (viewIntent != null) {
                    ((Activity) mContext).startActivityForResult(viewIntent, ConstantVariables.VIEW_PAGE_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }
        } else if (attachmentType.equals("tagged_users_list")) {

            mAddPeopleList.clear();

            JSONArray jsonArray = feedList.getmUserTagArray();

            // Create listview for tagged user's
            ListView mUserListView = new ListView(mContext);
            mUserListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            mAddPeopleAdapter = new AddPeopleAdapter(mContext, R.layout.list_friends, mAddPeopleList);
            mUserListView.setAdapter(mAddPeopleAdapter);
            mUserListView.setOnItemClickListener(this);

            // Create dialog for show listview inside it
            mDialog = new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(mUserListView);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();

            // Get user's info and add into the list
            for (int i = 1; i < jsonArray.length(); i++) {
                JSONObject userObject = jsonArray.optJSONObject(i);
                JSONObject tagObject = userObject.optJSONObject("tag_obj");
                String username = tagObject.optString("displayname");
                int userId = tagObject.optInt("user_id");
                String image = tagObject.optString("image_icon");

                mAddPeopleList.add(new AddPeopleList(userId, username, image));

            }
            mAddPeopleAdapter.notifyDataSetChanged();

        } else if ((slug != null && slug.equals("otherMembers") && feedList.mOtherMembers != null)) {
            mAddPeopleList.clear();

            JSONArray jsonArray = feedList.mOtherMembers;

            ListView mUserListView = new ListView(mContext);
            mUserListView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            mAddPeopleAdapter = new AddPeopleAdapter(mContext, R.layout.list_friends, mAddPeopleList);
            mUserListView.setAdapter(mAddPeopleAdapter);
            mUserListView.setOnItemClickListener(this);

            // Create dialog for show listView inside it
            mDialog = new Dialog(mContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setContentView(mUserListView);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();

            // Get user's info and add into the list
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userObject = jsonArray.optJSONObject(i);
                String username = userObject.optString("title");
                int userId = userObject.optInt("id");
                String image = userObject.optString("image_normal");
                mAddPeopleList.add(new AddPeopleList(userId, username, image));

            }
            mAddPeopleAdapter.notifyDataSetChanged();
        } else if (ConstantVariables.WEBVIEW_ENABLE == 0) {
            CustomTabUtil.launchCustomTab((Activity) mContext, GlobalFunctions.getWebViewUrl(listItemHolder.mWebUrl, mContext));
        } else {
            Intent webViewActivity = new Intent(mContext, WebViewActivity.class);
            webViewActivity.putExtra("url", listItemHolder.mWebUrl);
            ((Activity) mContext).startActivityForResult(webViewActivity, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    /**
     * When Video Attachment in feeds will be clicked
     */
    public void videoAttachmentClicked(String feedType, String feedAttachmentType, String attachment_type, String video_type, String video_url, int attachment_id,
                                       JSONObject mFeedObject, JSONObject singleAttachmentObject) {

        Intent mainIntent = null;
        List<String> mEnabledModuleList = null;
        if (PreferencesUtils.getEnabledModuleList(mContext) != null) {
            mEnabledModuleList = new ArrayList<String>(Arrays.asList(PreferencesUtils.getEnabledModuleList(mContext).split("\",\"")));
        }

        ///TODO, recheck and confirm with api team that if can replace the check with only "feedAttachmentType".
        // Checking if the video is present in enabled module list
        // and also checked for the activity_action videos (Videos which are shared)
        if (mEnabledModuleList != null
                && ((mEnabledModuleList.contains("video") || mEnabledModuleList.contains("sitevideo"))
                || ((feedAttachmentType.contains("video") || attachment_type.contains("video"))
                && attachment_type.equals("activity_action")))) {

            if (mEnabledModuleList.contains("sitevideo") && !mDeletedModulesList.contains("core_main_sitevideo")
                    && ((feedAttachmentType.equals("video")) || attachment_type.equals("video"))) {
                mainIntent = AdvVideoUtil.getViewPageIntent(mContext, attachment_id, video_url, new Bundle());
                mainIntent.putExtra(ConstantVariables.VIDEO_TYPE, Integer.parseInt(video_type));
                ((Activity) mContext).startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            } else if (mEnabledModuleList.contains("video") && !mDeletedModulesList.contains("video")
                    && (video_type.equals("1") || video_type.equals("2") || video_type.equals("3"))) {
                mainIntent = VideoUtil.getViewPageIntent(mContext, attachment_id, video_url, new Bundle());
                if (mainIntent != null) {
                    if (!feedAttachmentType.equals("video")) {
                        if (!mFeedObject.optString("name").equals("user") && !feedType.equals("share")) {
                            mainIntent = GlobalFunctions.setIntentParamForVideo(feedAttachmentType, mFeedObject,
                                    attachment_id, mainIntent);
                        } else if (singleAttachmentObject != null) {
                            mainIntent = GlobalFunctions.setIntentParamForVideo(feedAttachmentType,
                                    singleAttachmentObject, attachment_id, mainIntent);
                        }
                    }
                    mainIntent.putExtra(ConstantVariables.VIDEO_TYPE, Integer.parseInt(video_type));
                    ((Activity) mContext).startActivityForResult(mainIntent, ConstantVariables.VIEW_PAGE_CODE);
                    ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            } else {
                mainIntent = new Intent(mContext, WebViewActivity.class);
                if (feedType != null && feedType.equals("share") && singleAttachmentObject != null) {
                    String url = singleAttachmentObject.optString("uri");
                    if (url == null || url.isEmpty()) {
                        url = singleAttachmentObject.optString("content_url");
                    }
                    mainIntent.putExtra("url", url);
                } else {
                    mainIntent.putExtra("url", mFeedObject.optString("url"));
                }
                if (singleAttachmentObject != null) {
                    mainIntent.putExtra("headerText", singleAttachmentObject.optString("title"));
                }
                ((Activity) mContext).startActivityForResult(mainIntent, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } else {
            mainIntent = new Intent(mContext, WebViewActivity.class);
            if (feedType != null && feedType.equals("share") && singleAttachmentObject != null) {
                String url = singleAttachmentObject.optString("uri");
                if (url == null || url.isEmpty()) {
                    url = singleAttachmentObject.optString("content_url");
                }
                mainIntent.putExtra("url", url);
            } else {
                mainIntent.putExtra("url", mFeedObject.optString("url"));
            }
            if (singleAttachmentObject != null) {
                mainIntent.putExtra("headerText", singleAttachmentObject.optString("title"));
            }
            ((Activity) mContext).startActivityForResult(mainIntent, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    /**
     * When Location text get clicked in checkIn feeds
     */

    public void openMapActivity(String locationLabel, double latitude, double longitude, String place_id) {

        Intent viewIntent = new Intent(mContext, MapActivity.class);
        viewIntent.putExtra("location", locationLabel);
        viewIntent.putExtra("latitude", latitude);
        viewIntent.putExtra("longitude", longitude);
        viewIntent.putExtra("place_id", place_id);

        if (mFeedsFragment != null) {
            mFeedsFragment.checkManifestPermissions(viewIntent, Manifest.permission.ACCESS_FINE_LOCATION,
                    ConstantVariables.ACCESS_FINE_LOCATION, false, false);
        } else {
            mContext.startActivity(viewIntent);
            ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void clickedTag(CharSequence tag) {
        Intent searchActivity = new Intent(mContext, SearchActivity.class);
        searchActivity.putExtra(ConstantVariables.HASTAG_SEARCH, true);
        searchActivity.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, "home");
        searchActivity.putExtra(SearchManager.QUERY, tag.toString());
        mContext.startActivity(searchActivity);
    }

    @Override
    public void onItemDelete(int position) {

        Toast.makeText(mContext, mContext.getResources().getString(R.string.feed_delete_success),
                Toast.LENGTH_SHORT).show();
        if (mIsSingleFeed) {
            ((Activity) mContext).finish();
        } else {
            mFeedItemList.remove(position);
            mFeedsFragment.defaultFeedCountTemp--;
            mFeedsFragment.mAccurateActivityCount--;
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {

        switch (menuName) {

            case "hide":
            case "report_feed":
                mHiddenFeeds.add(position);
                break;

            case "disable_comment":
                // When single feed, so hide comment bar when disabled comment is performed.
                if (mIsSingleFeed && mFeedDisableCommentListener != null) {
                    FeedList feedList = (FeedList) itemList;
                    mFeedDisableCommentListener.onFeedDisableComment(feedList.ismCanComment() == 1);
                } else {
                    mFeedItemList.set(position, itemList);
                }
                break;

            case "edit_feed":
                FeedList feedList = (FeedList) itemList;
                Intent intent = new Intent(mContext, Status.class);
                intent.putExtra("showPhotoBlock", false);
                intent.putExtra(ConstantVariables.ITEM_POSITION, position);
                intent.putExtra(ConstantVariables.FEED_LIST, feedList);
                try {
                    if (mFeedPostMenus != null && mFeedPostMenus.length() > 0) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("emotions", mFeedPostMenus.optInt("emotions"));
                        jsonObject.put("userprivacy", mFeedPostMenus.optJSONObject("userprivacy"));
                        jsonObject.put("userlist", mFeedPostMenus.optJSONArray("userlist"));
                        jsonObject.put("multiple_networklist", mFeedPostMenus.optJSONArray("multiple_networklist"));
                        if (feedList.getSchedulePostTime() != null && !feedList.getSchedulePostTime().isEmpty()
                                && !feedList.getSchedulePostTime().equals("null")) {
                            jsonObject.put("allowSchedulePost", mFeedPostMenus.optInt("allowSchedulePost"));
                        }
                        intent.putExtra("feedPostMenus", jsonObject.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((Activity) mContext).startActivityForResult(intent, ConstantVariables.FEED_REQUEST_CODE);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case "pin_post":
            case "unpin_post":
                if (mOnPinPostListener != null) {
                    mOnPinPostListener.onPostPinUnpin();
                }
                break;
        }
        notifyDataSetChanged();

    }

    // Set The Selected Filters
    public void setmFilterSelectedListener(OnFilterSelectedListener mFilterSelectedListener) {
        this.mFilterSelectedListener = mFilterSelectedListener;
    }

    public void setPinPostListener(OnPinPostListener onPinPostListener) {
        this.mOnPinPostListener = onPinPostListener;
    }

    /**
     * Method to show showMore option when post is large.
     *
     * @param postTextView   text view containing long post.
     * @param postBodyText   long which which needs to be truncated.
     * @param attachmentType if see more option is clicked for attachment.
     */
    public void seeMoreOption(TextView postTextView,
                              String postBodyText,
                              final ListItemHolder listItemHolder,
                              final String attachmentType,
                              boolean isFeedTitle) {

        try {
            int index = ConstantVariables.FEED_TITLE_BODY_LENGTH;

            /* Show see more option after url if any see more link is coinciding with link( in between of url) */
            Matcher m = Patterns.WEB_URL.matcher(postBodyText);
            while (m.find()) {
                String url = m.group();

                int firstIndex = postBodyText.indexOf(url);
                int lastIndex = firstIndex + url.length();

                if (index >= firstIndex && index <= lastIndex ) {
                    index = lastIndex;

                    if (index < postBodyText.length()) {
                        index++;
                    }
                }

                /* Don't show see more link if url is ending point of body text */
                if (index == postBodyText.length()) {
                    if (isFeedTitle) {
                        showFullContentInFeedTitle(listItemHolder);
                    } else {
                        showFullAttachmentBody(listItemHolder, postBodyText);
                    }
                    return;
                }
            }

            String mTitleBody = postBodyText.trim().substring(0, index);

            mTitleBody = mTitleBody.concat("..." + mContext.getResources().getString(R.string.readMore)).replaceAll("<img.+?>|<IMG.+?>", "");
            int index1 = mTitleBody.trim().length() - mContext.getResources().getString(R.string.readMore).length();
            int index2 = mTitleBody.trim().length();

            postTextView.setMovementMethod(LinkMovementMethod.getInstance());
            postTextView.setText(mTitleBody, TextView.BufferType.SPANNABLE);
            Spannable mySpannable = (Spannable) postTextView.getText();

            ClickableSpan myClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {

                    FeedList feedList = (FeedList) mFeedItemList.get(listItemHolder.getAdapterPosition());

                    if (attachmentType != null && !attachmentType.equals("advancedactivity_sell")) {
                        JSONArray attachmentArray = feedList.getmFeedAttachmentArray();
                        JSONObject singleAttachmentObject = attachmentArray.optJSONObject(0);
                        final int attachmentId = singleAttachmentObject.optInt("attachment_id");
                        final String attachmentTitle = singleAttachmentObject.optString("title");
                        final String attachmentVideoType = singleAttachmentObject.optString("attachment_video_type");
                        final String attachment_video_url = singleAttachmentObject.optString("attachment_video_url");

                        /*
                        Redirect to view pages when see more get clicked on body of attachments
                        */
                        switch (attachmentType) {
                            case ConstantVariables.VIDEO_TITLE:
                            case ConstantVariables.MLT_VIDEO_MENU_TITLE:
                            case ConstantVariables.ADV_GROUPS_VIDEO_MENU_TITLE:
                            case ConstantVariables.SITE_STORE_VIDEO_MENU_TITLE:
                            case ConstantVariables.PRODUCT_VIDEO_MENU_TITLE:
                            case ConstantVariables.ADV_EVENT_VIDEO_MENU_TITLE:
                                videoAttachmentClicked(feedList.getmFeedType(), "", attachmentType,
                                        attachmentVideoType, attachment_video_url, attachmentId,
                                        feedList.getmFeedObject(), attachmentArray.optJSONObject(0));
                                break;

                            case ConstantVariables.AAF_MENU_TITLE:
                                attachmentClicked(attachmentTitle, attachmentType, feedList.getmActionId(),
                                        listItemHolder, null);
                                break;

                            default:
                                attachmentClicked(attachmentTitle, attachmentType, attachmentId,
                                        listItemHolder, null);
                                break;
                        }

                    } else if (feedList.getmActionId() != 0) {
                        Intent intent = new Intent(mContext, SingleFeedPage.class);
                        intent.putExtra(ConstantVariables.ITEM_POSITION, listItemHolder.getAdapterPosition());
                        intent.putExtra(ConstantVariables.ACTION_ID, feedList.getmActionId());
                        if (feedList.getmAttachmentCount() > 1) {
                            intent.putExtra(ConstantVariables.IS_MULTI_PHOTO_FEED, true);
                        }

                        // Put Reactions params if Reactions are enabled
                        intent.putExtra("reactionEnabled", mReactionsEnabled);
                        if (mReactions != null) {
                            intent.putExtra("reactions", mReactions.toString());
                        }
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (mFeedsFragment != null) {
                            mFeedsFragment.startActivityForResult(intent, ConstantVariables.VIEW_SINGLE_FEED_PAGE);
                        } else {
                            (mContext).startActivity(intent);
                        }
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(ContextCompat.getColor(mContext, R.color.body_text_3));
                }
            };

            mySpannable.setSpan(myClickableSpan, index1, index2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (mDialog != null)
            mDialog.dismiss();

        AddPeopleList addPeopleList = mAddPeopleList.get(position);
        int userId = addPeopleList.getmUserId();

        Intent intent = new Intent(mContext, userProfile.class);
        intent.putExtra(ConstantVariables.USER_ID, userId);
        ((Activity) mContext).startActivityForResult(intent, ConstantVariables.USER_PROFILE_CODE);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        if (mAddPeopleAdapter != null) {
            mAddPeopleAdapter.clear();
        }
    }

    public void clearLists() {
        mHiddenFeeds.clear();
        mAllHiddenFeeds.clear();
        if (mRemoveAds != null) {
            mRemoveAds.clear();
        }
    }

    private void openPhotoLightBox(int feedPosition, String album_url, int photoCount, String mFeedAttachmentType, int albumId) {
        mSelectedFeedList = (FeedList) mFeedItemList.get(feedPosition);

        Bundle bundle = new Bundle();
        ArrayList<PhotoListDetails> mFeedPhotoDetails = mSelectedFeedList.getmPhotoDetails();
        bundle.putSerializable(PhotoLightBoxActivity.EXTRA_IMAGE_URL_LIST, mFeedPhotoDetails);
        Intent i = new Intent(mContext, PhotoLightBoxActivity.class);
        i.putExtra(ConstantVariables.ITEM_POSITION, feedPosition);
        i.putExtra(ConstantVariables.SUBJECT_TYPE, mFeedAttachmentType);
        i.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, photoCount);
        i.putExtra(ConstantVariables.SHOW_ALBUM_BUTTON, true);
        i.putExtra(ConstantVariables.PHOTO_REQUEST_URL, album_url);
        i.putExtra(ConstantVariables.ALBUM_ID, albumId);
        i.putExtras(bundle);

        if (mFeedsFragment != null) {
            mFeedsFragment.startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);
        } else {
            ((Activity) mContext).startActivityForResult(i, ConstantVariables.VIEW_LIGHT_BOX);
        }
    }

    private void setLikeCount(int isLike, int likeCount, ListItemHolder listItemHolder) {

        if (mReactionsEnabled == 1) {
            // You and count others
            if (isLike == 1) {
                if (likeCount == 1) {
                    listItemHolder.mLikeCount.setText(mContext.getResources().getString(R.string.reaction_string));
                } else {
                    String likeText = mContext.getResources().getQuantityString(R.plurals.others, likeCount - 1, likeCount - 1);
                    listItemHolder.mLikeCount.setText(
                            String.format(mContext.getResources().getString(R.string.reaction_text_format),
                                    mContext.getResources().getString(R.string.you_and_text),
                                    likeText));
                }
            } else {
                // Only count
                listItemHolder.mLikeCount.setText(Integer.toString(mFeedItem.getmLikeCount()));
            }
        } else {
            String likeText = mContext.getResources().getQuantityString(R.plurals.profile_page_like, likeCount);
            listItemHolder.mLikeCount.setText(String.format(mContext.getResources().getString(R.string.like_count_text), likeCount, likeText));
            listItemHolder.mCountSaperator.setVisibility(View.GONE);
            listItemHolder.mPopularReactionsView.setVisibility(View.GONE);
        }
    }

    private void doSaveUnsaveFeedIndicate(ListItemHolder listItemHolder, int itemPosition){
        FeedList feedInfoList = (FeedList) mFeedItemList.get(itemPosition);

        if(feedInfoList.getmIsSaveFeedOption() == 1){
            // Playing likeSound effect when user liked a post.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnLike(mContext);
            }

            // color the save block / button blue to signify that the feed is saved
            listItemHolder.mSaveFeedBlock.setActivated(true);
            listItemHolder.mSaveFeedButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));

            // TODO: This is temporary remove this if save feed api call is done. This should be set there depending on the response of the server.
            feedInfoList.setmIsSaveFeedOption(0);
        } else {

            // color the save block / button to grey to signify unsave feed
            listItemHolder.mSaveFeedBlock.setActivated(false);
            listItemHolder.mSaveFeedButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));

            // TODO: This is temporary remove this if save feed api call is done. This should be set there depending on the response of the server.
            feedInfoList.setmIsSaveFeedOption(1);
        }
    }

    private void instantLike(ListItemHolder listItemHolder, String likeButtonText, int reactionId, String reactionIcon, boolean isReactionChanged) {
        FeedList feedInfoList = (FeedList) mFeedItemList.get(itemPosition);

        if (feedInfoList.getmIsLike() == 0) {

            /**
             * Increase Like count and set Visibility visible to show Like count
             */

            // Playing likeSound effect when user liked a post.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnLike(mContext);
            }

            feedInfoList.setmLikeCount(feedInfoList.getmLikeCount() + 1);

            setLikeCount(1, feedInfoList.getmLikeCount(), listItemHolder);

            listItemHolder.mCounterView.setVisibility(View.VISIBLE);
            listItemHolder.mLikeCount.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) listItemHolder.mCommentCount.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            listItemHolder.mCommentCount.setLayoutParams(layoutParams);

            listItemHolder.mLikeButton.setActivated(true);
            listItemHolder.mLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.themeButtonColor));
            listItemHolder.mLikeButton.setText(likeButtonText);

            if (mReactionsEnabled == 1) {
                JSONObject feedReactions = feedInfoList.getmFeedReactions();
                setFeedReactions(likeButtonText, reactionIcon, reactionId, feedReactions, feedInfoList);
            } else {
                listItemHolder.mCountSaperator.setVisibility(View.VISIBLE);
                listItemHolder.mPopularReactionsView.setVisibility(View.VISIBLE);
            }


        } else {
            /**
             * Decrease like count, hide Like count option visibility if there was only
             * one like, also check if comment count is zero, then hide mFeedLikeCommentInfo
             * view.
             * Else decrease the like count and set in textview.
             */
            if (!isReactionChanged) {
                if (feedInfoList.getmLikeCount() == 1) {
                    feedInfoList.setmLikeCount(feedInfoList.getmLikeCount() - 1);
                    listItemHolder.mLikeCount.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) listItemHolder.mCommentCount.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    listItemHolder.mCommentCount.setLayoutParams(layoutParams);

                    if (feedInfoList.getmCommentCount() == 0)
                        listItemHolder.mCounterView.setVisibility(View.INVISIBLE);
                } else {
                    feedInfoList.setmLikeCount(feedInfoList.getmLikeCount() - 1);
                    setLikeCount(0, feedInfoList.getmLikeCount(), listItemHolder);
                    listItemHolder.mLikeCount.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) listItemHolder.mCommentCount.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
                    listItemHolder.mCommentCount.setLayoutParams(layoutParams);
                }

                listItemHolder.mLikeButton.setActivated(false);
                listItemHolder.mLikeButton.setTextColor(ContextCompat.getColor(mContext, R.color.grey_dark));
            }

            /**
             * Check the reaction count of previous reaction
             * If the reaction count is 1 then remove that object from feedReactions
             * Else Decrease the reaction count and update feedReactions.
             * If the new reaction does not exit in FeedReactions object then put it in the object
             * with Reaction count 1 and update myFeedReactions
             */
            if (mReactionsEnabled == 1) {
                try {
                    JSONObject feedReactions = feedInfoList.getmFeedReactions();
                    JSONObject myFeedReactions = feedInfoList.getmMyFeedReactions();

                    if (myFeedReactions != null && feedReactions != null) {
                        int myReactionId = myFeedReactions.optInt("reactionicon_id");

                        if (feedReactions.optJSONObject(String.valueOf(myReactionId)) != null) {
                            int myReactionCount = feedReactions.optJSONObject(String.valueOf(myReactionId)).optInt("reaction_count");

                            if ((myReactionCount - 1) <= 0) {
                                feedReactions.remove(String.valueOf(myReactionId));
                            } else {
                                feedReactions.optJSONObject(String.valueOf(myReactionId)).put("reaction_count", myReactionCount - 1);
                            }

                            feedInfoList.setmFeedReactions(feedReactions);
                        }
                    }

                    if (isReactionChanged) {
                        setFeedReactions(likeButtonText, reactionIcon, reactionId, feedReactions, feedInfoList);
                    } else {
                        feedInfoList.setmMyFeedReactions(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            listItemHolder.mLikeButton.setText(likeButtonText);
        }
    }

    private void setFeedReactions(String likeButtonText, String reactionIcon, int reactionId, JSONObject feedReactions,
                                  FeedList feedInfoList) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("caption", likeButtonText);
            jsonObject.put("reaction_image_icon", reactionIcon);
            jsonObject.put("reactionicon_id", reactionId);

            feedInfoList.setmMyFeedReactions(jsonObject);

            if (feedReactions != null) {
                if (feedReactions.optJSONObject(String.valueOf(reactionId)) != null) {
                    int reactionCount = feedReactions.optJSONObject(String.valueOf(reactionId)).optInt("reaction_count");
                    feedReactions.optJSONObject(String.valueOf(reactionId)).putOpt("reaction_count", reactionCount + 1);
                } else {
                    jsonObject.put("reaction_count", 1);
                    feedReactions.put(String.valueOf(reactionId), jsonObject);
                }
            } else {
                feedReactions = new JSONObject();
                jsonObject.put("reaction_count", 1);
                feedReactions.put(String.valueOf(reactionId), jsonObject);
            }
            feedInfoList.setmFeedReactions(feedReactions);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Holder for Feed View
     */
    public static class ListItemHolder extends RecyclerView.ViewHolder {

        ActionIconThemedTextView mLikeButton, mCommentButton, mShareButton, mSaveFeedButton;
        ThemedTextView mLikeCount, mCommentCount;
        BezelImageView mFeedProfileImage;
        RelativeLayout mAttachmentPreviewBlock;
        ImageView mAttachmentImage, ivSticker, mSingleAttachmentImage, mMusicAndLinkAttachmentImage, mPlayIcon, ivGifIcon;
        EmojiconTextView mFeedTitleBody;
        TextView mFeedTitle, mAttachmentTitle, hashTagView, mMusicAttachmentTitle,
                mMusicAttachmentBody, tvPinnedView, tvPostSchedule, tvPrice, tvLocation;
        SelectableTextView mAttachmentBody, mFeedPostedTime, mAttachmentUrlView;
        ActionIconButton mFeedMenusIcon, mShareFeedMenuIcon;
        JSONArray mFeedAttachmentArray;
        JSONObject mFeedObject, mPopularReactionsArray;
        int mPhotoAttachmentCount;
        String mWebUrl, mFeedTitleBodyText, mFeedType, mFeedAttachmentType;
        LinearLayout mFeedFooterMenusBlock, mAttachmentView, mMusicAttachmentView, mPopularReactionsView, llTitleView;
        RelativeLayout mCounterView;
        RelativeLayout rlSingleImageLayout;
        RecyclerView mImagesGallery, mMultiPhotoRecyclerView, sellPhotosRecyclerView;
        View mCountSaperator, mLikeBlock, mCommentBlock, mShareBlock, mSaveFeedBlock;
        ImageView mReactionImage, mCancelUpload;
        Double mLatitude, mLongitude;
        String mLocationLabel, mPlaceId;
        ProgressBar pbPlaceHolder, feedUploadProgress, processingRequest;
        TextView mShareFeedTitle, mShareFeedTime, mProgressPercentage;
        BezelImageView mShareFeedIcon;
        RelativeLayout mShareFeedContainer;
        EmojiconTextView mShareFeedBody;
        View mShareFeedDivider;
        LinearLayout mFeedBlock, mFeedUploader;
        private int itemViewType = 0;

        public ListItemHolder(View itemView) {
            super(itemView);


            /* Feed Body Info Fields */
            mFeedBlock = (LinearLayout) itemView.findViewById(R.id.activityFeedBlock);
            mFeedUploader = (LinearLayout) itemView.findViewById(R.id.feed_uploader);
            mFeedProfileImage = (BezelImageView) itemView.findViewById(R.id.profile_image);
            mFeedTitle = (TextView) itemView.findViewById(R.id.feed_title);
            mFeedTitleBody = (EmojiconTextView) itemView.findViewById(R.id.feed_body);
            mFeedMenusIcon = (ActionIconButton) itemView.findViewById(R.id.feed_menu);
            mFeedPostedTime = (SelectableTextView) itemView.findViewById(R.id.feed_time);
            mCounterView = (RelativeLayout) itemView.findViewById(R.id.counts_container);
            mCountSaperator = itemView.findViewById(R.id.counts_saperator);
            mPopularReactionsView = (LinearLayout) itemView.findViewById(R.id.popularReactionIcons);
            llTitleView = (LinearLayout) itemView.findViewById(R.id.title_view);
            mReactionImage = (ImageView) itemView.findViewById(R.id.reactionIcon);
            tvPinnedView = (TextView) itemView.findViewById(R.id.pinned_tag);
            tvPostSchedule = (TextView) itemView.findViewById(R.id.tv_post_schedule);

            /*
            Feed Attachment Fields
             */
            // Music and link attachment fields.
            mMusicAndLinkAttachmentImage = (ImageView) itemView.findViewById(R.id.attachment_preview_link_music);
            mMusicAttachmentView = (LinearLayout) itemView.findViewById(R.id.attachment_view_link_music);
            mMusicAttachmentTitle = (NameView) itemView.findViewById(R.id.attachment_title_link_music);
            mMusicAttachmentBody = (TextView) itemView.findViewById(R.id.attachment_body_link_music);

            mAttachmentPreviewBlock = (RelativeLayout) itemView.findViewById(R.id.attachment_preview_layout);
            mAttachmentImage = (ImageView) itemView.findViewById(R.id.attachment_preview);
            mPlayIcon = (ImageView) itemView.findViewById(R.id.play_button);
            mAttachmentView = (LinearLayout) itemView.findViewById(R.id.attachment_view);
            mAttachmentTitle = (NameView) itemView.findViewById(R.id.attachment_title);
            mAttachmentBody = (SelectableTextView) itemView.findViewById(R.id.attachment_body);
            mAttachmentUrlView = (SelectableTextView) itemView.findViewById(R.id.attachment_url_view);
            mSingleAttachmentImage = (ImageView) itemView.findViewById(R.id.singleAlbumPhoto);
            mImagesGallery = (RecyclerView) itemView.findViewById(R.id.image_preview);
            hashTagView = (TextView) itemView.findViewById(R.id.hashTag_view);
            rlSingleImageLayout = (RelativeLayout) itemView.findViewById(R.id.single_attachment_layout);
            ivGifIcon = (ImageView) itemView.findViewById(R.id.gif_icon);
            pbPlaceHolder = (ProgressBar) itemView.findViewById(R.id.holder_progress_bar);
            feedUploadProgress = (ProgressBar) itemView.findViewById(R.id.feed_upload_progressbar);
            processingRequest = (ProgressBar) itemView.findViewById(R.id.processing_request);
            mProgressPercentage = (TextView) itemView.findViewById(R.id.upload_progress);
            mCancelUpload = (ImageView) itemView.findViewById(R.id.cancel_upload);
            tvPrice = (TextView) itemView.findViewById(R.id.price_text);
            tvLocation = (TextView) itemView.findViewById(R.id.location);
            ivSticker = (ImageView) itemView.findViewById(R.id.iv_sticker);

            mImagesGallery.setHasFixedSize(true);
            mImagesGallery.setItemAnimator(new DefaultItemAnimator());
            mImagesGallery.addItemDecoration(new SpaceItemDecoration(5));

            mMultiPhotoRecyclerView = (RecyclerView) itemView.findViewById(R.id.multi_photo_recycler);
            mMultiPhotoRecyclerView.setHasFixedSize(true);
            mMultiPhotoRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mMultiPhotoRecyclerView.setNestedScrollingEnabled(false);
            ((SimpleItemAnimator) mMultiPhotoRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

            sellPhotosRecyclerView = (RecyclerView) itemView.findViewById(R.id.sell_image_preview);
            sellPhotosRecyclerView.setHasFixedSize(true);
            sellPhotosRecyclerView.setItemAnimator(new DefaultItemAnimator());
            ((SimpleItemAnimator) sellPhotosRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

            mAttachmentBody.setMovementMethod(LinkMovementMethod.getInstance());
            hashTagView.setMovementMethod(LinkMovementMethod.getInstance());
            /*
            Like and Comment Fields
             */
            mLikeCount = (ThemedTextView) itemView.findViewById(R.id.like_count);
            mCommentCount = (ThemedTextView) itemView.findViewById(R.id.comment_count);

            mShareButton = (ActionIconThemedTextView) itemView.findViewById(R.id.share_button);
            mLikeButton = (ActionIconThemedTextView) itemView.findViewById(R.id.like_button);
            mCommentButton = (ActionIconThemedTextView) itemView.findViewById(R.id.comment_button);
            mSaveFeedButton = (ActionIconThemedTextView) itemView.findViewById(R.id.save_feed_button);
            mLikeBlock = itemView.findViewById(R.id.like_view);
            mCommentBlock = itemView.findViewById(R.id.comment_view);
            mShareBlock = itemView.findViewById(R.id.share_view);
            mSaveFeedBlock = itemView.findViewById(R.id.save_feed_view);
            // for save feed block


            /*
            Feed Footer Menu Fields
             */
            mFeedFooterMenusBlock = (LinearLayout) itemView.findViewById(R.id.feedFooterMenusBlock);

            /* Shared feed layout fields */
            mShareFeedContainer = (RelativeLayout) itemView.findViewById(R.id.share_feed_container);
            mShareFeedIcon = (BezelImageView) itemView.findViewById(R.id.share_feed_icon);
            mShareFeedTitle = (TextView) itemView.findViewById(R.id.share_feed_title);
            mShareFeedTime = (TextView) itemView.findViewById(R.id.share_feed_time);
            mShareFeedMenuIcon = (ActionIconButton) itemView.findViewById(R.id.share_feed_menu);
            mShareFeedBody = (EmojiconTextView) itemView.findViewById(R.id.share_feed_body);
            mShareFeedDivider = (View) itemView.findViewById(R.id.share_feed_divider);

        }
    }

    /**
     * Holder for Hidden Feeds
     */

    public static class HiddenItemHolder extends RecyclerView.ViewHolder {
        TextView mHiddenFeedOptions;
        SelectableTextView mHiddenFeedBody;

        public HiddenItemHolder(View itemView) {
            super(itemView);
            mHiddenFeedBody = (SelectableTextView) itemView.findViewById(R.id.hiddenFeedBody);
            mHiddenFeedOptions = (TextView) itemView.findViewById(R.id.hiddenFeedOptions);
            itemView.setBackgroundResource(R.color.white);
        }
    }

    /**
     * Holder for Header with Status Menu and Filters
     */

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public BezelImageView mUserProfileImage;
        public TextView mCheckInMenuIcon, mCheckInMenuText, mStatusUpdateText, mPhotoMenuIcon,
                mPhotoMenuText, mVideoMenuIcon, mVideoMenuText;
        public SelectableTextView mNoFeedMessage;
        public LinearLayout mPhotoMenu, mVideoMenu, mCheckInMenu;
        LinearLayout mPostFeedOptions, mStatusTextLayout, llPostBlock, llMessageBlock, llFooterBlock;
        View greetingView, birthdayView, dividerView, llFilterBlock, firstFilterBlock, secondFilterBlock,
                thirdFilterBlock, forthFilterBlock, moreFilterBlock;
        TextView tvFirstFilterIcon, tvSecondFilterIcon, tvThirdFilterIcon, tvForthFilterIcon, tvMoreIcon;
        TextView tvFirstFilter, tvSecondFilter, tvThirdFilter, tvForthFilter, tvMore;
        RecyclerView storyRecyclerView;
        ImageView ivCancel, ivBgShapeFirstFilter, ivBgShapeSecondFilter, ivBgShapeThirdFilter,
                ivBgShapeForthFilter, ivBgShapeMoreFilter;
        WebView mGreetingWebView;
        TextView tvUserName, tvBirthdayDescription, tvWritePost, tvSendMessage;
        ImageView ivUserProfile, ivBVCancel;
        RelativeLayout rlStoryView;

        public HeaderViewHolder(Context context, View v) {
            super(v);
            mUserProfileImage = (BezelImageView) v.findViewById(R.id.userProfileImage);
            mVideoMenu = (LinearLayout) v.findViewById(R.id.videoMenu);
            mVideoMenuIcon = (TextView) v.findViewById(R.id.videoMenuIcon);
            mVideoMenuText = (TextView) v.findViewById(R.id.videoMenuText);
            mPhotoMenuIcon = (TextView) v.findViewById(R.id.photoMenuIcon);
            mPhotoMenuText = (TextView) v.findViewById(R.id.photoMenuText);
            mPhotoMenu = (LinearLayout) v.findViewById(R.id.photoMenu);
            mCheckInMenu = (LinearLayout) v.findViewById(R.id.checkInMenu);
            mCheckInMenuIcon = (TextView) v.findViewById(R.id.checkInMenuIcon);
            mCheckInMenuText = (TextView) v.findViewById(R.id.checkInMenuText);
            mPostFeedOptions = (LinearLayout) v.findViewById(R.id.postFeedLayout);
            mStatusTextLayout = (LinearLayout) v.findViewById(R.id.status_text_layout);
            mStatusUpdateText = (TextView) v.findViewById(R.id.status_update_text);
            mNoFeedMessage = (SelectableTextView) v.findViewById(R.id.noFeedMessage);

            storyRecyclerView = (RecyclerView) v.findViewById(R.id.story_recyclerview);
            ((SimpleItemAnimator) storyRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            storyRecyclerView.setHasFixedSize(true);
            storyRecyclerView.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            greetingView = v.findViewById(R.id.greetings_view);
            dividerView = v.findViewById(R.id.divider);
            birthdayView = v.findViewById(R.id.birthday_view);
            ivUserProfile = (ImageView) v.findViewById(R.id.user_profile_image);
            ivBVCancel = (ImageView) v.findViewById(R.id.birthday_cancel);
            tvUserName = (TextView) v.findViewById(R.id.user_name);
            tvBirthdayDescription = (TextView) v.findViewById(R.id.birthday_description);
            tvWritePost = (TextView) v.findViewById(R.id.write_post);
            tvSendMessage = (TextView) v.findViewById(R.id.send_message);
            llFooterBlock = (LinearLayout) v.findViewById(R.id.birthday_footer_block);
            llPostBlock = (LinearLayout) v.findViewById(R.id.write_post_layout);
            llMessageBlock = (LinearLayout) v.findViewById(R.id.message_layout);
            mGreetingWebView = (WebView) v.findViewById(R.id.greeting_web_view);
            ivCancel = (ImageView) v.findViewById(R.id.cancel);
            GlobalFunctions.setWebSettings(mGreetingWebView, false);

            /**
             * Filter views
             */
            llFilterBlock = v.findViewById(R.id.filter_block);
            firstFilterBlock = v.findViewById(R.id.first_filter);
            // Show all filter selected by default
            firstFilterBlock.setAlpha(1);
            secondFilterBlock = v.findViewById(R.id.second_filter);
            thirdFilterBlock = v.findViewById(R.id.third_filter);
            forthFilterBlock = v.findViewById(R.id.forth_filter);
            moreFilterBlock = v.findViewById(R.id.more_filter);
            tvFirstFilterIcon = (TextView) firstFilterBlock.findViewById(R.id.filter_icon);
            tvFirstFilter = (TextView) firstFilterBlock.findViewById(R.id.filter_title);
            ivBgShapeFirstFilter = (ImageView) firstFilterBlock.findViewById(R.id.iv_bg);
            tvSecondFilterIcon = (TextView) secondFilterBlock.findViewById(R.id.filter_icon);
            tvSecondFilter = (TextView) secondFilterBlock.findViewById(R.id.filter_title);
            ivBgShapeSecondFilter = (ImageView) secondFilterBlock.findViewById(R.id.iv_bg);
            tvThirdFilterIcon = (TextView) thirdFilterBlock.findViewById(R.id.filter_icon);
            tvThirdFilter = (TextView) thirdFilterBlock.findViewById(R.id.filter_title);
            ivBgShapeThirdFilter = (ImageView) thirdFilterBlock.findViewById(R.id.iv_bg);
            tvForthFilterIcon = (TextView) forthFilterBlock.findViewById(R.id.filter_icon);
            tvForthFilter = (TextView) forthFilterBlock.findViewById(R.id.filter_title);
            ivBgShapeForthFilter = (ImageView) forthFilterBlock.findViewById(R.id.iv_bg);
            tvMoreIcon = (TextView) moreFilterBlock.findViewById(R.id.filter_icon);
            tvMore = (TextView) moreFilterBlock.findViewById(R.id.filter_title);
            ivBgShapeMoreFilter = (ImageView) moreFilterBlock.findViewById(R.id.iv_bg);

        }
    }

    public static class PeopleSuggestionViewHolder extends RecyclerView.ViewHolder {

        public RecyclerView mSuggestionRecyclerView;
        public TextView tvSeeAll;
        public ProgressBar mProgressBar;

        public PeopleSuggestionViewHolder(View view) {
            super(view);
            mSuggestionRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_list);
            tvSeeAll = (TextView) view.findViewById(R.id.tv_see_all);
            mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }

    public static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int mVerticalSpaceHeight;

        public SpaceItemDecoration(int mVerticalSpaceHeight) {
            this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = mVerticalSpaceHeight;
            outRect.right = mVerticalSpaceHeight;
        }
    }

    public static Map<Currency, Locale> getCurrencyLocaleMap() {
        Map<Currency, Locale> map = new HashMap<>();
        for (Locale locale : Locale.getAvailableLocales()) {
            try {
                Currency currency = Currency.getInstance(locale);
                map.put(currency, locale);
            }
            catch (Exception e){
                // skip strange locale
            }
        }
        return map;
    }

}
