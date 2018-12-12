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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.fellopages.mobileapp.classes.common.interfaces.OnCancelClickListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.adapters.AddPeopleAdapter;
import com.fellopages.mobileapp.classes.common.ui.PredicateLayout;
import com.fellopages.mobileapp.classes.common.utils.AddPeopleList;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddPeople extends AppCompatActivity implements TextWatcher,
        AdapterView.OnItemClickListener, OnCancelClickListener{

    private EditText mAddPeopleText;
    private ListView mFriendsListView;
    private AppConstant mAppConst;
    private AddPeopleAdapter mAddPeopleAdapter;
    private List<AddPeopleList> mAddPeopleList;
    private PredicateLayout mSelectedFriendsLayout;
    private Map<String, String> selectedFriends;
    private Toolbar mToolbar;
    private boolean isPhotoTag = false;
    private int mSubjectId;
    private String mSubjectType;
    private HashMap<String, String> postParams, removeTagParams;
    private JSONArray mTagJsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_people);

        mAppConst = new AppConstant(this);
        selectedFriends = new HashMap<>();
        postParams = new HashMap<>();



       /* Set Back Button on Action Bar */

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);
        CustomViews.setmOnCancelClickListener(this);

        mAddPeopleList = new ArrayList<>();

        mAddPeopleText = (EditText) findViewById(R.id.addPeopleBox);
        mAddPeopleText.setHint(getResources().getString(R.string.add_people_default_text) + "…");
        mAddPeopleText.addTextChangedListener(this);

        mFriendsListView = (ListView) findViewById(R.id.friendsList);
        mAddPeopleAdapter = new AddPeopleAdapter(this, R.layout.list_friends, mAddPeopleList);
        mFriendsListView.setAdapter(mAddPeopleAdapter);
        mFriendsListView.setOnItemClickListener(this);

        mSelectedFriendsLayout = (PredicateLayout) findViewById(R.id.selectedFriends);

        /*
        Populate the Friends Layout if already selected
         */

        Bundle friendsBundle = getIntent().getExtras();
        if(friendsBundle != null){
            mSubjectId = getIntent().getIntExtra(ConstantVariables.SUBJECT_ID, 0);
            mSubjectType = getIntent().getStringExtra(ConstantVariables.SUBJECT_TYPE);

            if (friendsBundle.containsKey("isPhotoTag")){
                isPhotoTag = true;

            }

            if (!isPhotoTag) {
                Set<String> searchArgumentSet = friendsBundle.keySet();
                for (String key : searchArgumentSet) {
                    if(key.equals(ConstantVariables.SUBJECT_ID) || key.equals(ConstantVariables.SUBJECT_TYPE))
                        continue;
                    String value = friendsBundle.getString(key);
                    selectedFriends.put(key, value);
                    selectedFriends = CustomViews.createSelectedUserLayout(this, Integer.parseInt(key), value,
                            mSelectedFriendsLayout, selectedFriends, 1);

                }
            }

            if (friendsBundle.containsKey("userTagArray")){
                try {
                    mTagJsonArray = new JSONArray(getIntent().getStringExtra("userTagArray"));

                    for (int i = 0; i < mTagJsonArray.length(); i++) {
                        JSONObject jsonObject = mTagJsonArray.optJSONObject(i);
                        int tagmap_id = jsonObject.optInt("tagmap_id");
                        String label = jsonObject.optString("text");
                        int isRemove = jsonObject.optInt("isRemove");

                        selectedFriends.put(String.valueOf(tagmap_id), label);
                        selectedFriends = CustomViews.createSelectedUserLayout(this, tagmap_id, label,
                                mSelectedFriendsLayout, selectedFriends, isRemove);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_title, menu);
        menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.add_people_button_text));

        if (isPhotoTag) {
            menu.findItem(R.id.submit).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){

            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(AddPeople.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(AddPeople.this);
                }
                return true;

            case R.id.submit:
                addPeople();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if(charSequence != null && charSequence.length() != 0) {
            HashMap<String, String> searchTextParams = new HashMap<>();
            searchTextParams.put("search", charSequence.toString());
            searchTextParams.put(ConstantVariables.SUBJECT_TYPE,mSubjectType);
            searchTextParams.put(ConstantVariables.SUBJECT_ID,String.valueOf(mSubjectId));
            String getFriendsUrl = UrlUtil.GET_FRIENDS_LIST;
            getFriendsUrl =  mAppConst.buildQueryString(getFriendsUrl, searchTextParams);
            getFriendList(getFriendsUrl);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /**
     * Send Request to get Friends Suggestions
     * @param url
     */
    public void getFriendList(String url){

        if(mAddPeopleAdapter != null){
            mAddPeopleAdapter.clear();
        }

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {

                mAddPeopleAdapter.clear();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if(jsonObject != null && jsonObject.length() != 0){

                    try {
                        JSONArray responseJsonArray = jsonObject.getJSONArray("response");
                        for(int i = 0; i < responseJsonArray.length(); i++ ){

                            JSONObject friendJsonObject = responseJsonArray.getJSONObject(i);
                            int userId = friendJsonObject.getInt("id");
                            String label = friendJsonObject.getString("label");
                            String photo = friendJsonObject.getString("image_icon");

                            mAddPeopleList.add(new AddPeopleList(userId, label, photo));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mAddPeopleAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(findViewById(R.id.addPeopleContent),message);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        AddPeopleList addPeopleList = mAddPeopleList.get(i);
        final String label = addPeopleList.getmUserLabel();
        int userId = addPeopleList.getmUserId();

        if (isPhotoTag) {
            String photoTagUrl = UrlUtil.ADD_TAG_URL;

            int x = (int) (Math.random() * 100);
            int y = (int) (Math.random() * 100);

            postParams.put("subject_id", String.valueOf(mSubjectId));
            postParams.put("subject_type", mSubjectType);
            postParams.put("id", String.valueOf(userId));
            postParams.put("guid", "user_" + userId);
            postParams.put("label", label);
            postParams.put("extra", "{\"x\":" + x + ",\"y\":" + y + ",\"w\":48,\"h\":48}");
            photoTagUrl = mAppConst.buildQueryString(photoTagUrl, postParams);

            mAppConst.postJsonResponseForUrl(photoTagUrl, null, new OnResponseListener() {
                @Override
                public void onTaskCompleted(final JSONObject jsonObject) throws JSONException {

                    SnackbarUtils.displaySnackbarLongWithListener(mFriendsListView, label + " " +
                            getResources().getString(R.string.user_tagged_in_photo_success_message),
                            new SnackbarUtils.OnSnackbarDismissListener() {
                        @Override
                        public void onSnackbarDismissed() {
                            setIntentDataForTag(jsonObject);
                        }
                    });
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    SnackbarUtils.displaySnackbarLongWithListener(mFriendsListView, message,
                            new SnackbarUtils.OnSnackbarDismissListener() {
                        @Override
                        public void onSnackbarDismissed() {
                            finish();
                        }
                    });
                }
            });

        }

        selectedFriends.put(Integer.toString(userId), label);

        selectedFriends = CustomViews.createSelectedUserLayout(this, userId, label, mSelectedFriendsLayout,
                selectedFriends, 1);

        if(mAddPeopleAdapter != null){
            mAddPeopleAdapter.clear();
        }

        mAddPeopleText.setText("");

    }

    private void setIntentDataForTag(JSONObject jsonObject) {
        Bundle bundle = new Bundle();
        JSONArray response = jsonObject.optJSONArray("response");
        if (response != null) {
            bundle.putString("tagArray", response.toString());
        }

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(ConstantVariables.ADD_PEOPLE_CODE, intent);
        finish();
    }

    public void addPeople(){

        Set<String> keySet = selectedFriends.keySet();
        Bundle bundle = new Bundle();

        for (String key : keySet) {
            String value = selectedFriends.get(key);
            bundle.putString(key, value);
        }

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(ConstantVariables.ADD_PEOPLE_CODE, intent);
        finish();
    }

    @Override
    public void onCancelButtonClicked(final int userId) {

        View canceledView = mSelectedFriendsLayout.findViewById(userId);
        if(canceledView != null){
            mSelectedFriendsLayout.removeView(canceledView);
            selectedFriends.remove(Integer.toString(userId));
        }

        if(selectedFriends == null || selectedFriends.size() == 0){
            mSelectedFriendsLayout.setVisibility(View.GONE);
        }

        if (isPhotoTag) {
            removeTagParams = new HashMap<>();

            removeTagParams.put("subject_id", String.valueOf(mSubjectId));
            removeTagParams.put("subject_type", mSubjectType);
            removeTagParams.put("tagmap_id", String.valueOf(userId));

            String removeUrl = UrlUtil.REMOVE_TAG_URL;
            removeUrl = mAppConst.buildQueryString(removeUrl, removeTagParams);

            mAppConst.deleteResponseForUrl(removeUrl, null, new OnResponseListener() {
                @Override
                public void onTaskCompleted(final JSONObject jsonObject) throws JSONException {

                    SnackbarUtils.displaySnackbarLongWithListener(mFriendsListView,
                            getResources().getString(R.string.remove_user_from_photo_tag_success_message),
                            new SnackbarUtils.OnSnackbarDismissListener() {
                                @Override
                                public void onSnackbarDismissed() {
                                    setIntentDataForTag(jsonObject);
                                }
                            }
                    );
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    SnackbarUtils.displaySnackbar(mFriendsListView, message);
                }
            });

        }

    }
}
