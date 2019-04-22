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

package com.fellopages.mobileapp.classes.modules.advancedActivityFeeds;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.LinearDividerItemDecorationUtil;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.fellopages.mobileapp.classes.core.ConstantVariables.STATUS_POST_OPTIONS;


public class FeelingActivity extends AppCompatActivity implements TextWatcher {

    //Member variables.
    private Context mContext;
    private RecyclerView mRecyclerView;
    private EditText etSearch;
    private ProgressBar mProgressBar;
    private List<BrowseListItems> mBrowseListItem, mSearchList;
    private String parentId, parentIcon, parentTitle;
    private boolean isChildFeeling;
    private AppConstant mAppConst;
    private FeelingActivityAdapter mFeelingActivityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeling);

        // Intialize member variables.
        mContext = FeelingActivity.this;
        mAppConst = new AppConstant(mContext);
        mBrowseListItem = new ArrayList<>();
        mSearchList = new ArrayList<>();

        // Getting Intent values.
        parentId = getIntent().getStringExtra(ConstantVariables.SUBJECT_ID);
        parentIcon = getIntent().getStringExtra(ConstantVariables.IMAGE);
        parentTitle = getIntent().getStringExtra(ConstantVariables.TITLE);
        isChildFeeling = getIntent().getBooleanExtra("is_child_feeling", false);

        // Getting views.
        getViews();

        if (STATUS_POST_OPTIONS.get(ConstantVariables.FEELING_ACTIVITY) != null) {
            setDataFromList();
        } else {
            makeRequest();
        }
    }

    /**
     * Method to get views of this page.
     */
    private void getViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = findViewById(R.id.progressBar);
        etSearch = findViewById(R.id.search_view);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_action_search);
        if (drawable != null)
            drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.grey), PorterDuff.Mode.SRC_ATOP);
        etSearch.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        etSearch.addTextChangedListener(this);

        // Getting recycler view
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new LinearDividerItemDecorationUtil(mContext));
    }

    /**
     * Method to make server request to get the feeling/activity response.
     */
    private void makeRequest() {
        mAppConst.getJsonResponseFromUrl(AppConstant.DEFAULT_URL + "advancedactivity/feelings",
                new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        STATUS_POST_OPTIONS.put(ConstantVariables.FEELING_ACTIVITY, jsonObject);
                        setDataFromList();
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        mProgressBar.setVisibility(View.GONE);
                        SnackbarUtils.displaySnackbarShortWithListener(mRecyclerView, message,
                                () -> finish());
                    }
                });
    }

    /**
     * Method to set data into list.
     */
    private void setDataFromList() {
        mProgressBar.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        if (STATUS_POST_OPTIONS.get(ConstantVariables.FEELING_ACTIVITY) != null) {
            JSONObject jsonObject = (JSONObject) STATUS_POST_OPTIONS.get(ConstantVariables.FEELING_ACTIVITY);
            if (jsonObject != null && jsonObject.length() > 0 && jsonObject.has("parent")) {

                JSONArray responseArray;
                String titleKey;
                if (isChildFeeling) {
                    JSONObject childObject = jsonObject.optJSONObject("child");
                    responseArray = childObject.optJSONArray(parentId);
                    titleKey = "title";
                } else {
                    responseArray = jsonObject.optJSONArray("parent");
                    titleKey = "tagline";
                }
                if (responseArray != null && responseArray.length() > 0) {

                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject parentObject = responseArray.optJSONObject(i);
                        mBrowseListItem.add(new BrowseListItems(parentObject.optString("photo"),
                                parentObject.optString(titleKey), parentObject));
                    }

                    mFeelingActivityAdapter = new FeelingActivityAdapter(mContext, mBrowseListItem, (view, position) -> {
                        BrowseListItems browseListItem = mBrowseListItem.get(position);
                        if (!mSearchList.isEmpty()) {
                            browseListItem = mSearchList.get(position);
                        }

                        if (isChildFeeling) {
                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            bundle.putString("parent_id", parentId);
                            bundle.putString("parenttitle", parentTitle);
                            bundle.putString("child_id", browseListItem.getParentObject().optString("child_id", "custom_feeling"));
                            bundle.putString("photo", browseListItem.getmBrowseImgUrl());
                            bundle.putString("childtitle", browseListItem.getmBrowseListTitle());
                            bundle.putString("type", browseListItem.getParentObject().optString("type"));
                            intent.putExtras(bundle);
                            setResult(ConstantVariables.CHILD_FEELING_REQUEST_CODE, intent);
                            finish();

                        } else {
                            Intent intent = new Intent(mContext, FeelingActivity.class);
                            intent.putExtra("is_child_feeling", true);
                            intent.putExtra(ConstantVariables.SUBJECT_ID, browseListItem.getParentObject().optString("parent_id"));
                            intent.putExtra(ConstantVariables.TITLE, browseListItem.getParentObject().optString("title"));
                            intent.putExtra(ConstantVariables.IMAGE, browseListItem.getmBrowseImgUrl());
                            startActivityForResult(intent, ConstantVariables.CHILD_FEELING_REQUEST_CODE);
                        }
                    });
                    mRecyclerView.setAdapter(mFeelingActivityAdapter);
                    mFeelingActivityAdapter.notifyDataSetChanged();
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ConstantVariables.CHILD_FEELING_REQUEST_CODE
                && resultCode == ConstantVariables.CHILD_FEELING_REQUEST_CODE && data != null) {
            Intent intent = new Intent();
            Bundle bundle = data.getExtras();
            if (bundle != null)
                intent.putExtras(bundle);
            setResult(ConstantVariables.CHILD_FEELING_REQUEST_CODE, intent);
            finish();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing()) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable charSequence) {
        if (charSequence != null && charSequence.length() > 0) {
            mSearchList.clear();
            for (int i = 0; i < mBrowseListItem.size(); i++) {
                BrowseListItems listItems = mBrowseListItem.get(i);
                String title = listItems.getmBrowseListTitle();
                if (title.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                    mSearchList.add(listItems);
                }
            }

            if (isChildFeeling && mSearchList.isEmpty()) {
                mSearchList.add(new BrowseListItems(parentIcon, charSequence.toString(), new JSONObject()));
            }
            mFeelingActivityAdapter.setUpdatedList(mSearchList.size() > 0 ? mSearchList : mBrowseListItem);
            mFeelingActivityAdapter.notifyDataSetChanged();

        } else {
            mSearchList.clear();
            mFeelingActivityAdapter.setUpdatedList(mBrowseListItem);
            mFeelingActivityAdapter.notifyDataSetChanged();
        }
    }
}
