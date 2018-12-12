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

package com.fellopages.mobileapp.classes.common.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnCancelClickListener;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

public class CustomViews {

    static int sPosition;
    static OnCancelClickListener mOnCancelClickListener;

    public static void setmOnCancelClickListener(OnCancelClickListener mOnCancelClickListener) {
        CustomViews.mOnCancelClickListener = mOnCancelClickListener;
    }

    /**
     * Method to get Linear layout params with WRAP_CONTENT Width and Height.
     * @return Returns the Linear Layout params.
     */
    public static LinearLayout.LayoutParams getWrapLayoutParams () {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Method to get Linear layout params with MATCH_PARENT Width and WRAP_CONTENT Height.
     * @return Returns the Linear Layout params.
     */
    public static LinearLayout.LayoutParams getFullWidthLayoutParams () {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Method to get Linear layout params with MATCH_PARENT Width and Height.
     * @return Returns the Linear Layout params.
     */
    public static LinearLayout.LayoutParams getFullWidthHeightLayoutParams () {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Method to get Linear layout params with Custom Width and Height.
     * @return Returns the Linear Layout params.
     */
    public static LinearLayout.LayoutParams getCustomWidthHeightLayoutParams (int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    /**
     * Method to get RelativeLayout params with WRAP_CONTENT Width and Height.
     * @return Returns the Linear Layout params.
     */
    public static RelativeLayout.LayoutParams getWrapRelativeLayoutParams () {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Method to get RelativeLayout params with WRAP_CONTENT Width and MATCH_PARENT Height.
     * @return Returns the Linear Layout params.
     */
    public static RelativeLayout.LayoutParams getFullWidthRelativeLayoutParams () {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Method to get RelativeLayout params with MATCH_PARENT Width and Height.
     * @return Returns the Linear Layout params.
     */
    public static RelativeLayout.LayoutParams getFullWidthHeightRelativeLayoutParams () {
        return new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Method to get RelativeLayout params with Custom Width and Height.
     * @return Returns the Linear Layout params.
     */
    public static RelativeLayout.LayoutParams getCustomWidthHeightRelativeLayoutParams (int width, int height) {
        return new RelativeLayout.LayoutParams(width, height);
    }

    /**
     * Method to add header view on top of the list/recycler view.
     * @param headerViewId Id of the layout to be added on top of the view.
     * @param swipeRefreshLayout layout which need to be added at below of the header view.
     */
    public static void addHeaderView(int headerViewId, SwipeRefreshLayout swipeRefreshLayout) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) swipeRefreshLayout.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, headerViewId);
        swipeRefreshLayout.setLayoutParams(layoutParams);
    }

    /**
     * Method to return footer view.
     * @param inflater LayoutInflater to inflate the footer view layout.
     * @return Returns the inflated layout.
     */
    public static View getFooterView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.progress_item, null, false);
    }

    /**
     * Method to add Progress bar in footer on scrolling for more content
     * @param footerView View to be added on footer view.
     */
    public static void addFooterView(View footerView) {
        if (footerView != null) {
            if (footerView.getVisibility() == View.VISIBLE) {
                footerView.setVisibility(View.GONE);
            } else {
                footerView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Method to remove Progress bar from footer
     * @param footerView View to be removed from footer view.
     */
    public static void removeFooterView(View footerView) {
        if (footerView != null && footerView.getVisibility() == View.VISIBLE) {
            footerView.setVisibility(View.GONE);
        }
    }

    /**
     * Method to add Progress bar in footer of listView on scrolling to load more data
     * @param listView ListView in which footer view is added.
     * @param footerView Footer view.
     * @return Return true if added.
     */
    public static boolean addFooterView(ListView listView, View footerView) {
        if (listView.getFooterViewsCount() > 0) {
            return false;
        }else {
            listView.addFooterView(footerView);
            return true;
        }
    }

    /**
     * Method to Remove Progress bar when loading data is done
     * @param listView ListView from which footer view is removed.
     * @param footerView Footer view.
     */
    public static void removeFooterView(ListView listView, View footerView) {
        try {
            if (listView.getFooterViewsCount() > 0){
                listView.removeFooterView(footerView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to generate footer view for end of result message.
     * @param context Context of calling class.
     * @param footerView FooterView Layout which contains the text view..
     */
    public static void showEndOfResults (Context context, View footerView) {
        TextView tvFooter = (TextView) footerView.findViewById(R.id.footer_text);
        footerView.setVisibility(View.VISIBLE);
        footerView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        tvFooter.setVisibility(View.VISIBLE);
        tvFooter.setText(context.getResources().getString(R.string.end_of_results));
    }

    /**
     * Method to hide end of result message.
     * @param footerView FooterView Layout which contains the text view..
     */
    public static void hideEndOfResults (View footerView) {
        footerView.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        footerView.findViewById(R.id.footer_text).setVisibility(View.GONE);
    }

    /**
     * Method to set html data into text view.
     * @param textView View in which text is to be shown.
     * @param value Value which need to be set.
     */
    public static void setText(TextView textView, String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            textView.setText(Html.fromHtml(value));
        }
    }

    /**
     * Method to set html data into text view.
     * @param editText View in which text is to be shown.
     * @param value Value which need to be set.
     */
    public static void setEditText(EditText editText, String value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            editText.setText(Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY));
        } else {
            //noinspection deprecation
            editText.setText(Html.fromHtml(value));
        }
    }

    /**
     * Method for hiding collapsing tool bar title in expanded state.
     * @param collapsingToolbar Collapsing tool bar.
     */
    public static void setCollapsingToolBarTitle(CollapsingToolbarLayout collapsingToolbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsingToolbarLayoutExpandedTextStyleForUpperVersion);
        } else {
            collapsingToolbar.setExpandedTitleTextAppearance(R.style.CollapsingToolbarLayoutExpandedTextStyle);
        }
    }

    /**
     * Method to create Marquee title on toolBar
     * @param context Context of calling class.
     * @param toolbar ToolBar on which marquee title is to be created.
     */
    public static void createMarqueeTitle(Context context, Toolbar toolbar){

        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            TextView titleTextView = (TextView) f.get(toolbar);
            if(titleTextView != null){
                titleTextView.setSelected(true);
                titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                titleTextView.setMarqueeRepeatLimit(-1);
                titleTextView.setSingleLine(true);
                TextViewCompat.setTextAppearance(titleTextView, R.style.TextAppearance);
                titleTextView.setTextColor(ContextCompat.getColor(context, R.color.textColorPrimary));
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to show marquee title on collapsing tool bar.
     * @param verticalOffset VerticalOffset of the collapsing tool bar.
     * @param collapsingToolbar Collapsing tool bar layout.
     * @param toolbar Tool bar.
     * @param tvToolBarTitle TextView which will work as marquee.
     * @param title Title of the tool bar.
     */
    public static void showMarqueeTitle(int verticalOffset, CollapsingToolbarLayout collapsingToolbar,
                                        Toolbar toolbar, TextView tvToolBarTitle, String title){

        if(verticalOffset == -collapsingToolbar.getHeight() + toolbar.getHeight()){
            tvToolBarTitle.setVisibility(View.VISIBLE);
            collapsingToolbar.setTitle("");
        }else{
            tvToolBarTitle.setVisibility(View.GONE);
            collapsingToolbar.setTitle(title);
        }
    }

    /**
     * Method to add reaction images in the ImageView..
     * @param context Context of calling class.
     * @param reactionId ReactionId
     * @param reactionIcon Reaction icon which is added to the ImageView.
     * @return Returns the ImageView with attached reaction icon.
     */
    public static ImageView generateReactionImageView (Context context, int reactionId, String reactionIcon) {

        ImageView imageView = new ImageView(context);
        imageView.setId(reactionId);

        int heightWidth = (int) context.getResources().getDimension(R.dimen.reaction_icon_width_height);
        int margin_5dp = (int) context.getResources().getDimension(R.dimen.margin_5dp);
        int margin_3dp = (int) context.getResources().getDimension(R.dimen.margin_3dp);
        int margin_1dp = (int) context.getResources().getDimension(R.dimen.margin_1dp);
        LinearLayout.LayoutParams layoutParams = getCustomWidthHeightLayoutParams(heightWidth, heightWidth);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.setMargins(0, margin_3dp, margin_1dp, margin_5dp);
        imageView.setLayoutParams(layoutParams);
        ImageLoader imageLoader = new ImageLoader(context);
        imageLoader.setReactionImageUrl(reactionIcon, imageView);
        return imageView;
    }

    /**
     * Method to generate view for the profile fields.
     * @param context Context of calling class.
     * @param profileFieldMap Map contains all the key and value for which view is to be generated.
     * @param profileFieldLayout GridLayout on which Profile fields are going to be inflated.
     */
    public static void generateProfileFieldsView (Context context, Map<String, String> profileFieldMap,
                                                  GridLayout profileFieldLayout) {
        profileFieldLayout.removeAllViews();
        int rightPadding;

        profileFieldLayout.setVisibility(View.VISIBLE);

        SelectableTextView[] labelView = new SelectableTextView[profileFieldMap.size()];
        SelectableTextView[] dataView = new SelectableTextView[profileFieldMap.size()];
        LinearLayout[] linearLayout = new LinearLayout[profileFieldMap.size()];

        int width = AppConstant.getDisplayMetricsWidth(context);

        // Layout params for the linear layout which contain AppCompactTextView
        //Setting linear layout width, Which is half of the screen size for both linear layout.

        LinearLayout.LayoutParams layoutParams, labelParams;

        /* Show Full width Profile Fields if there is only one profile field. */
        if (profileFieldMap.size() == 1) {
            layoutParams = getCustomWidthHeightLayoutParams((width - 5), LinearLayout.LayoutParams.WRAP_CONTENT);
            labelParams = getWrapLayoutParams();

        } else {
            layoutParams = getCustomWidthHeightLayoutParams((width/2) - 5, LinearLayout.LayoutParams.WRAP_CONTENT);
            labelParams = getCustomWidthHeightLayoutParams(context.getResources().getDimensionPixelSize(R.dimen.label_max_width),
                    LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        // Initializing all the AppCompatTextView and LinearLayout
        for (int i = 0; i < labelView.length; i++) {
            labelView[i] = new SelectableTextView(context);
            labelView[i].setLayoutParams(labelParams);
            labelView[i].setPadding(context.getResources().getDimensionPixelSize(R.dimen.offset_distance),
                    context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                    context.getResources().getDimensionPixelSize(R.dimen.offset_distance),
                    context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));

            dataView[i] = new SelectableTextView(context);
            dataView[i].setLayoutParams(getWrapLayoutParams());
            if (i != 0 || i % 2 != 0) {
                rightPadding = context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding);
            } else {
                rightPadding = context.getResources().getDimensionPixelSize(R.dimen.offset_distance);
            }

            dataView[i].setAutoLinkMask(Linkify.WEB_URLS);
            dataView[i].setPadding(context.getResources().getDimensionPixelSize(R.dimen.offset_distance),
                    context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                    rightPadding, context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));
            TextViewCompat.setTextAppearance(dataView[i], R.style.TextAppearance_AppCompat_Body1);

            linearLayout[i] = new LinearLayout(context);
            linearLayout[i].setLayoutParams(layoutParams);
            linearLayout[i].setOrientation(LinearLayout.HORIZONTAL);

        }
        sPosition = 0;

        // Setting values inside View
        for (Map.Entry<String, String> entry : profileFieldMap.entrySet()) {

            String value = entry.getValue();
            String key = entry.getKey();
            labelView[sPosition].setText((String.format("%s: ", key)));
            dataView[sPosition].setText(value);

            linearLayout[sPosition].addView(labelView[sPosition]);
            linearLayout[sPosition].addView(dataView[sPosition]);
            profileFieldLayout.addView(linearLayout[sPosition]);
            sPosition++;
        }
    }

    /**
     * Method to generate view for the InfoTab fields.
     * @param context Context of calling class.
     * @param mDataResponse JsonObject contains all the value for which view is to be generated.
     * @param infoFieldLayout GridLayout on which Profile fields are going to be inflated.
     */
    public static void generateInfoFieldsView(Context context, JSONObject mDataResponse,
                                              GridLayout infoFieldLayout){

        infoFieldLayout.removeAllViews();
        infoFieldLayout.setVisibility(View.VISIBLE);

        Iterator keys = mDataResponse.keys();
        while( keys.hasNext() ) {

            String key = (String) keys.next();
            String value = mDataResponse.optString(key);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = 0;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.setGravity(Gravity.FILL_HORIZONTAL);
            AppCompatTextView labelView = new AppCompatTextView(context);
            AppCompatTextView mainText = new AppCompatTextView(context);
            AppCompatTextView colonView = new AppCompatTextView(context);
            labelView.setText(key);
            colonView.setText(" : ");
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimension(R.dimen.body_default_font_size));
            labelView.setGravity(GravityCompat.START| Gravity.CENTER_VERTICAL);
            labelView.setPadding(context.getResources().getDimensionPixelSize(R.dimen.offset_distance),
                    context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                    context.getResources().getDimensionPixelSize(R.dimen.offset_distance),
                    context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));
            mainText.setText(value);
            mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimension(R.dimen.body_default_font_size));
            mainText.setGravity(GravityCompat.START| Gravity.CENTER_VERTICAL);

            mainText.setLayoutParams(layoutParams);
            mainText.setPadding(context.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                    context.getResources().getDimensionPixelSize(R.dimen.offset_distance),
                    context.getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));

            infoFieldLayout.addView(labelView);
            infoFieldLayout.addView(colonView);
            infoFieldLayout.addView(mainText);
        }
    }

    /**
     * Method for create table structure of rating parameters
     * @param context Context of calling class.
     * @param layoutType Layout type.
     * @param ratingParams JsonArray of rating params.
     * @param ratingLayout Linear layout of rating.
     * @return Returns the generated rating view.
     */
    public static LinearLayout generateRatingView (Context context, String layoutType,
                                                   JSONArray ratingParams, LinearLayout ratingLayout) {

        int size = ratingParams.length();
        int row = size / 2 + 1;

        LinearLayout rowsLayout = new LinearLayout(context);
        rowsLayout.setOrientation(LinearLayout.VERTICAL);
        rowsLayout.setLayoutParams(getFullWidthLayoutParams());

        // outer loop for rows
        for (int i = 0; i < row; i++) {

            View rating_view = null;
            LinearLayout columnLayout = null;

            columnLayout = new LinearLayout(context);
            columnLayout.setOrientation(LinearLayout.HORIZONTAL);
            columnLayout.setLayoutParams(getFullWidthLayoutParams());
            columnLayout.setWeightSum(1);

            // inner loop for columns
            for (int j = i; j < i + 2; j++) {

                if (i + j < ratingParams.length()) {

                    /* Inflating rating parameter's view
                        * @param textView Rating parameter text
                        * @param ratingBar Parameter's rating star
                        */
                    TextView parameterText = null;
                    RatingBar parameterRatingBar = null;

                    if (layoutType.equals("top_view")) {
                        rating_view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                                inflate(R.layout.rating_grid_layout, ratingLayout, false);
                        parameterRatingBar = (RatingBar) rating_view.findViewById(R.id.mainRatingBar);

                    } else {
                        rating_view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                                inflate(R.layout.rating_grid_layout_small, ratingLayout, false);
                        parameterRatingBar = (RatingBar) rating_view.findViewById(R.id.smallRatingBar);
                    }
                    parameterText = (TextView) rating_view.findViewById(R.id.rating_parameter_text);
                    parameterRatingBar.setIsIndicator(true);

                    /* Set yellow color for selected star and gray for un selected stars */
                    LayerDrawable avgRatingStar = (LayerDrawable) parameterRatingBar.getProgressDrawable();
                    avgRatingStar.getDrawable(2).setColorFilter(ContextCompat.getColor(context, R.color.dark_yellow),
                            PorterDuff.Mode.SRC_ATOP);
                    avgRatingStar.getDrawable(0).setColorFilter(ContextCompat.getColor(context, R.color.light_gray),
                            PorterDuff.Mode.SRC_ATOP);

                    /* Get value from parameter's rating array and set into the view */
                    JSONObject jsonObject = ratingParams.optJSONObject(i + j);
                    String rating_parameter = jsonObject.optString("reviewcat_name");

                    int rating;
                    if (jsonObject.has("avg_rating")) {
                        rating = jsonObject.optInt("avg_rating");
                    } else {
                        rating = jsonObject.optInt("rating");
                    }

                    parameterText.setText(rating_parameter);
                    parameterRatingBar.setRating(Float.parseFloat(String.valueOf(rating)));
                    columnLayout.addView(rating_view);
                }
            }
            rowsLayout.addView(columnLayout);
        }

        ratingLayout.removeAllViews();
        ratingLayout.addView(rowsLayout);

        return ratingLayout;

    }

    /**
     * Method to calculate the grid dimensions Calculates number columns and
     * columns width in grid
     * @param context
     * @param noOfColumns
     * @param gridView
     * @return
     */
    public static int initializeGridLayout(Context context, int noOfColumns, GridViewWithHeaderAndFooter gridView){

        Resources r = context.getResources();
        AppConstant mAppConst = new AppConstant(context);
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        int columnWidth;

        // Column width
        if(noOfColumns == 1){
            columnWidth = mAppConst.getScreenWidth();
        }else{
            columnWidth = (mAppConst.getScreenWidth() / noOfColumns);
        }

        // Setting number of grid columns
        gridView.setNumColumns(noOfColumns);
        gridView.setStretchMode(GridViewWithHeaderAndFooter.NO_STRETCH);
        gridView.setColumnWidth(columnWidth);

        // Setting horizontal and vertical padding

        /*
        Set Horizontal Padding in case of RTL for API level 17 or more
         */
        if(mAppConst.isRtlSupported()){
            gridView.setHorizontalSpacing((int) -padding);
        }else{
            gridView.setHorizontalSpacing((int) padding);
        }

        gridView.setVerticalSpacing((int) padding);

        return columnWidth;

    }

    /**
     * Method to create Tagged Friends Layout
     * @param userId id of the fried
     * @param label displayname of the user
     */
    public static Map<String, String> createSelectedUserLayout(final Context context, int userId, String label,
                                                               final PredicateLayout taggedFriendsLayout,
                                                               final Map<String, String> selectedFriends,
                                                               int isShowCancelButton) {

        LinearLayout.LayoutParams defaultLayoutParams;
        View friendView = taggedFriendsLayout.findViewById(userId);

        if(friendView == null ) {

            // Create LinearLayout to show Tagged friend Info

            final LinearLayout selectedFriend = new LinearLayout(context);
            selectedFriend.setOrientation(LinearLayout.HORIZONTAL);
            defaultLayoutParams = CustomViews.getWrapLayoutParams();
            int marginTop = (int) (context.getResources().getDimension(R.dimen.margin_10dp) /
                    context.getResources().getDisplayMetrics().density);
            defaultLayoutParams.setMargins(marginTop, 0, 0, 0);
            int padding = (int) (context.getResources().getDimension(R.dimen.padding_10dp) /
                    context.getResources().getDisplayMetrics().density);
            selectedFriend.setLayoutParams(defaultLayoutParams);
            selectedFriend.setBackgroundResource(R.drawable.dark_blue_background);
            selectedFriend.setPadding(padding, padding, padding, padding);

            // Create Textview to show Friend's Display Name

            TextView selectedFriendLabel = new TextView(context);
            selectedFriendLabel.setText(label);
            selectedFriendLabel.setTextColor(ContextCompat.getColor(context, R.color.white));
            selectedFriendLabel.setTypeface(null, Typeface.BOLD);
            selectedFriend.addView(selectedFriendLabel);

            // Create TextView to show Cancel Button

            defaultLayoutParams = CustomViews.getFullWidthLayoutParams();
            defaultLayoutParams.setMargins(marginTop, 0, 0, 0);
            selectedFriend.setId(userId);

            if (isShowCancelButton == 1) {
                TextView cancelButton = new TextView(context);
                cancelButton.setTypeface(GlobalFunctions.getFontIconTypeFace(context));
                cancelButton.setText("\uf00d");
                cancelButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                cancelButton.setGravity(Gravity.END);
                cancelButton.setPadding(marginTop, 0, marginTop, 0);
                cancelButton.setTag(userId);
                selectedFriend.addView(cancelButton, defaultLayoutParams);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(mOnCancelClickListener != null){
                            mOnCancelClickListener.onCancelButtonClicked((int ) view.getTag());
                        }

                    }
                });
            }

            taggedFriendsLayout.setVisibility(View.VISIBLE);
            taggedFriendsLayout.addView(selectedFriend, new PredicateLayout.LayoutParams(2, 2));


        }else if(friendView.getVisibility() == View.GONE){
            friendView.setVisibility(View.VISIBLE);
        }

        return selectedFriends;
    }

}
