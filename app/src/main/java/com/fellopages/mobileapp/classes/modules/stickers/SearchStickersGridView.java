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
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;;
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
import java.util.HashMap;
import java.util.List;

public class SearchStickersGridView implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {
    public View rootView;
    StickersPopup mStickersPopup;
    private Context mContext;
    GridView mSearchGridView, mSearchResultGridView;
    private AppConstant mAppConst;
    private List<ImageViewList> mStickersList, mStickersSearchResultList;
    private int columnWidth;
    StickersAdapter mStickersAdapter;
    private JSONArray mSearchList;
    private SearchView mSearchView;
    private String mSearchText;
    private RelativeLayout mainContent;
    private GridViewAdapter mAdapter;
    private LinearLayout mMessageLayout;
    private ImageView searchCloseBtn;
    private Drawable searchCloseButtonDrawable;

    public SearchStickersGridView(Context context, JSONArray searchList, StickersPopup emojiconPopup) {

        mContext = context;
        mAppConst = new AppConstant(mContext);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mStickersPopup = emojiconPopup;
        mSearchList = searchList;
        rootView = inflater.inflate(R.layout.stickers_gridview, null);

        if (!StickersUtil.isStorySticker) {
            rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray_light));
        } else {
            rootView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));
        }

        mainContent = (RelativeLayout) rootView.findViewById(R.id.main_content);

        mSearchGridView = (GridView) rootView.findViewById(R.id.searchStickersGridView);
        mSearchResultGridView = (GridView) rootView.findViewById(R.id.searchResultsGridView);

        rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        mSearchView = (SearchView) rootView.findViewById(R.id.searchView);
        mSearchView.setVisibility(View.VISIBLE);

        mMessageLayout = (LinearLayout ) rootView.findViewById(R.id.message_layout);

        InitializeGridLayout(mSearchGridView, 2);
        InitializeGridLayout(mSearchResultGridView, 4);
        initializeSearchView();


        // clear focus from searchview when it is initialized
        mainContent.setFocusableInTouchMode(true);
        mSearchView.clearFocus();

        mStickersList = new ArrayList<>();
        mStickersSearchResultList = new ArrayList<>();

        mStickersAdapter = new StickersAdapter(mContext, mStickersList);
        mSearchGridView.setAdapter(mStickersAdapter);

        mAdapter = new GridViewAdapter((Activity) mContext, columnWidth, true, mStickersSearchResultList);
        mSearchResultGridView.setAdapter(mAdapter);

        mSearchGridView.setOnItemClickListener(this);
        mSearchResultGridView.setOnItemClickListener(this);

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(mSearchView.getQuery() == null || mSearchView.getQuery().toString().isEmpty()){
                    searchCloseBtn.setImageDrawable(ContextCompat.getDrawable(mContext, R.color.transparent));
                }
                if(hasFocus){
                    mSearchView.requestFocus();
                }
            }
        });

        if(mSearchList != null && mSearchList.length() !=  0){
            for(int i = 0; i < mSearchList.length(); i++){
                JSONObject jsonObject = mSearchList.optJSONObject(i);
                int stickerId = jsonObject.optInt("stickersearch_id");
                String stickerTitle = jsonObject.optString("title");
                String stickersKey = jsonObject.optString("keyword");
                String stickerImage = jsonObject.optString("image_profile");
                String stickerBackGround = jsonObject.optString("background_color");
                mStickersList.add(new ImageViewList(stickerImage, stickerId, stickerTitle, stickersKey, stickerBackGround));
            }
        }
        mStickersAdapter.notifyDataSetChanged();

    }

    /**
     * Method to calculate the grid dimensions Calculates number columns and
     * columns width in grid
     * */
    public void InitializeGridLayout(GridView gridView, int noOfColumns) {

        Resources r = mContext.getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.STICKERS_GRID_PADDING, r.getDisplayMetrics());

        // Column width

        columnWidth = (int) ((mAppConst.getScreenWidth() - ((noOfColumns + 1) * padding)) /
                noOfColumns);


        // Setting number of grid columns
        gridView.setNumColumns(noOfColumns);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setColumnWidth(columnWidth);

        // Setting horizontal and vertical padding
        gridView.setPadding((int)padding, (int)padding, (int)padding, (int)padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
    }

    public void initializeSearchView(){

        SearchManager searchManager = (SearchManager)mContext.getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(((Activity)mContext).getComponentName()));

        if (!StickersUtil.isStorySticker) {
            mSearchView.setIconifiedByDefault(true);
        } else {
            mSearchView.setIconifiedByDefault(false);
        }

        mSearchView.setIconified(false);
        mSearchView.clearFocus();
        int searchImgId = mContext.getResources().getIdentifier("android:id/search_mag_icon", null, null);
        ImageView searchIconView = (ImageView) mSearchView.findViewById(searchImgId);
        if(searchIconView != null){
            Drawable searchIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_action_search);
            DrawableCompat.setTint(searchIcon, ContextCompat.getColor(mContext, R.color.light_gray));
            searchIconView.setImageDrawable(searchIcon);

//            searchIconView.setImageResource(R.drawable.ic_action_search);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            searchIconView.setLayoutParams(layoutParams);
        }
        int searchPlateId = mSearchView.getContext().getResources().
                getIdentifier("android:id/search_plate", null, null);
        View searchPlate = mSearchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            int searchTextId = searchPlate.getContext().getResources().
                    getIdentifier("android:id/search_src_text", null, null);
            final TextView searchText = (TextView) searchPlate.findViewById(searchTextId);
            searchText.setHintTextColor(ContextCompat.getColor(mContext, R.color.gray_stroke_color));

            if (StickersUtil.isStorySticker) {
                searchText.setTextColor(ContextCompat.getColor(mContext, R.color.light_gray));
            } else {
                searchText.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            }

            mSearchView.setQueryHint("Search stickers");
            int searchCloseBtnId = mSearchView.getContext().getResources().
                    getIdentifier("android:id/search_close_btn", null, null);
            searchCloseBtn = (ImageView) mSearchView.findViewById(searchCloseBtnId);
            searchCloseBtn.setEnabled(false);
            searchCloseButtonDrawable = ContextCompat.getDrawable
                    (mContext, R.drawable.ic_clear_white_24dp);
            DrawableCompat.setTint(searchCloseButtonDrawable, ContextCompat.getColor(mContext, R.color.transparent));
            searchCloseBtn.setImageDrawable(searchCloseButtonDrawable);
            searchCloseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchText.setText("");
                    mSearchView.clearFocus();
                    mSearchGridView.setVisibility(View.VISIBLE);
                    mMessageLayout.setVisibility(View.GONE);
                    mSearchResultGridView.setVisibility(View.GONE);
                }
            });
        }
        mSearchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if(newText != null && !newText.isEmpty()){
            searchCloseBtn.setEnabled(true);
            DrawableCompat.setTint(searchCloseButtonDrawable, ContextCompat.getColor(mContext, R.color.gray_stroke_color));
            searchCloseBtn.setImageDrawable(searchCloseButtonDrawable);
            mSearchText = newText;
            searchStickers();
        } else{
            searchCloseBtn.setVisibility(View.GONE);
            mSearchText = null;
            mSearchGridView.setVisibility(View.VISIBLE);
            mMessageLayout.setVisibility(View.GONE);
            mStickersSearchResultList.clear();
            mSearchResultGridView.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        int viewId = parent.getId();
        if(viewId == R.id.searchStickersGridView){
            ImageViewList stickerClicked = mStickersList.get(position);
            String keyword = stickerClicked.getmStickerKey();
            mSearchView.setQuery(keyword, true);
        } else{
            mAppConst.hideKeyboard();
            ImageViewList stickerInfo = mStickersSearchResultList.get(position);
            if(mStickersPopup.onStickerClickedListener != null){
                mStickersPopup.onStickerClickedListener.onStickerClicked(stickerInfo);
            }
        }
    }

    private void searchStickers(){
        HashMap<String, String> searchParams = new HashMap<>();
        searchParams.put("sticker_search", mSearchText);

        String stickerSearchUrl = mAppConst.buildQueryString(UrlUtil.AAF_VIEW_STICKERS_URL, searchParams);
        mAppConst.getJsonResponseFromUrl(stickerSearchUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                JSONArray mStickerArray = jsonObject.optJSONArray("stickers");
                mStickersSearchResultList.clear();
                if(mSearchText != null && !mSearchText.isEmpty()) {
                    mSearchGridView.setVisibility(View.GONE);
                    mMessageLayout.setVisibility(View.GONE);
                    if(mStickerArray != null && mStickerArray.length() != 0){
                        addStickersToList(mStickerArray);
                        mSearchResultGridView.setVisibility(View.VISIBLE);
                    } else{
                        mMessageLayout.setVisibility(View.VISIBLE);
                        TextView errorIcon = (TextView) rootView.findViewById(R.id.error_icon);
                        SelectableTextView errorMessage = (SelectableTextView) rootView.findViewById
                                (R.id.error_message);
                        errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
                        errorMessage.setText(mContext.getResources().getString(R.string.no_stickers));
                    }
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    private void addStickersToList(JSONArray stickersArray){
        if(stickersArray != null && stickersArray.length() != 0){
            for(int i = 0; i < stickersArray.length(); i++){
                JSONObject jsonObject = stickersArray.optJSONObject(i);
                String stickerGuid = jsonObject.optString("guid");
                String imageUrl = jsonObject.optString("image_profile");
                mStickersSearchResultList.add(new ImageViewList(imageUrl, stickerGuid));
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
