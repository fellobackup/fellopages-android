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

package com.fellopages.mobileapp.classes.common.activities;


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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.AddPeopleAdapter;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.AddPeopleList;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SelectedFriendList;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.messages.SelectedFriendListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InviteGuest extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener {

    public static final String SCHEMA_KEY_SEND_INVITES = "user_ids";
    private AppConstant mAppConst;
    private String inviteFormUrl;
    private boolean isGuestListBlank = true;
    private Toolbar mToolbar;
    private LinearLayout mainLayout;
    private ListView mGuestListView;
    private RecyclerView mAddedFriendListView;

    private SelectedFriendListAdapter mSelectedGuestListAdapter;
    private List<SelectedFriendList> mSelectedGuestList;
    private AddPeopleAdapter mAddPeopleAdapter;
    private List<AddPeopleList> mAddPeopleList;
    private Map<String, String> selectedGuest;
    private Map<String, String> postParams;

    private JSONArray guestListResponse;
    private Map<Integer, String> showNonSelectedGuest;

    private boolean isSendInviteRequest = false;
    protected EditText mRecipientView;

    private String mCurrentSelectedModule, mSnackBarMessage, mToolBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list_data_view);

        mAppConst = new AppConstant(this);
        showNonSelectedGuest = new HashMap<>();

        mAddPeopleList = new ArrayList<>();
        mSelectedGuestList = new ArrayList<>();
        selectedGuest = new HashMap<>();
        postParams = new HashMap<>();

        if (getIntent().hasExtra(ConstantVariables.USER_ID) && getIntent().hasExtra(ConstantVariables.CONTENT_TITLE)) {
            isSendInviteRequest = true;
        }
        mCurrentSelectedModule = getIntent().getExtras().getString(ConstantVariables.EXTRA_MODULE_TYPE);

        if (mCurrentSelectedModule != null) {
            switch (mCurrentSelectedModule) {
                case ConstantVariables.ADV_GROUPS_MENU_TITLE:
                    mToolBarTitle = getResources().getString(R.string.add_people);
                    mSnackBarMessage = getResources().getString(R.string.add_people_success_message);
                    break;
                case ConstantVariables.ADV_VIDEO_MENU_TITLE:
                case ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE:
                    mToolBarTitle = getIntent().getExtras().getString(ConstantVariables.CONTENT_TITLE);
                    mSnackBarMessage = getResources().getString(R.string.suggestion_success_message);
                    break;
                default:
                    mToolBarTitle = getResources().getString(R.string.title_activity_invite_event);
                    mSnackBarMessage = getResources().getString(R.string.invitation_successful);
                    break;
            }
        } else {
            mToolBarTitle = getResources().getString(R.string.add_people);
            mSnackBarMessage = getResources().getString(R.string.add_people_success_message);
        }

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(mToolBarTitle);

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CustomViews.createMarqueeTitle(this, mToolbar);

        //Views
        mainLayout = findViewById(R.id.searchListLayout);
        mRecipientView = findViewById(R.id.searchBox);

        mRecipientView.setHint(getResources().getString(R.string.invite_guest_hint_text));

        mGuestListView = findViewById(R.id.listView);
        mAddPeopleAdapter = new AddPeopleAdapter(this, R.layout.list_friends, mAddPeopleList);
        mGuestListView.setAdapter(mAddPeopleAdapter);

        mAddedFriendListView = findViewById(R.id.addedFriendList);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mAddedFriendListView.setLayoutManager(layoutManager);
        mSelectedGuestListAdapter = new SelectedFriendListAdapter(mSelectedGuestList,
                mAddedFriendListView,selectedGuest, showNonSelectedGuest, isSendInviteRequest);
        mAddedFriendListView.setAdapter(mSelectedGuestListAdapter);

        inviteFormUrl = getIntent().getStringExtra(ConstantVariables.URL_STRING);

        //Setting the listeners
        mRecipientView.addTextChangedListener(this);
        mGuestListView.setOnItemClickListener(this);

    }

    public void setViewValue( String fieldLabel, String fieldName){
        if (fieldName.equals(SCHEMA_KEY_SEND_INVITES)) {
            mRecipientView.setHint(fieldLabel);
            mRecipientView.setTag(fieldName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_with_action_icon, menu);
        menu.findItem(R.id.submit).setTitle(getResources().getString(R.string.create));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.submit:
                sendInviteRequest();
                break;
            case android.R.id.home:
                onBackPressed();
                // Playing backSound effect when user tapped on back button from tool bar.
                if (PreferencesUtils.isSoundEffectEnabled(InviteGuest.this)) {
                    SoundUtil.playSoundEffectOnBackPressed(InviteGuest.this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAppConst.hideKeyboard();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence searchText, int start, int before, int count) {
        if(searchText != null && !searchText.toString().isEmpty()) {
            getFriendList(UrlUtil.GET_FRIENDS_LIST + "?search=" + searchText);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void getFriendList(String url) {

        findViewById(R.id.loadingBar).setVisibility(View.VISIBLE);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject body) {
                if (body != null && body.length() != 0) {
                    mAddPeopleList.clear();
                    findViewById(R.id.loadingBar).setVisibility(View.GONE);
                    guestListResponse = body.optJSONArray("response");
                    for (int i = 0; i < guestListResponse.length(); i++) {
                        JSONObject friendObject = guestListResponse.optJSONObject(i);
                        String username = friendObject.optString("label");
                        int userId = friendObject.optInt("id");
                        String userImage = friendObject.optString("image_icon");

                        try {

                            if( showNonSelectedGuest.isEmpty() || !showNonSelectedGuest.containsKey(userId)) {
                                mAddPeopleList.add(new AddPeopleList(userId, username, userImage));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    mAddPeopleAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.loadingBar).setVisibility(View.GONE);
                SnackbarUtils.displaySnackbar(mainLayout, message);
            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        AddPeopleList addPeopleList = mAddPeopleList.get(position);
        String label = addPeopleList.getmUserLabel();
        int userId = addPeopleList.getmUserId();

        if(mAddPeopleAdapter != null){
            mAddPeopleAdapter.clear();
        }
        mAddedFriendListView.setVisibility(View.VISIBLE);
        if(!selectedGuest.containsKey(Integer.toString(userId))) {
            selectedGuest.put(Integer.toString(userId), label);
            showNonSelectedGuest.put(userId, label);
            mSelectedGuestList.add(new SelectedFriendList(userId, label));
            mSelectedGuestListAdapter.notifyDataSetChanged();
        }
        mRecipientView.setText("");
    }

    public void sendInviteRequest(){

        mAppConst.hideKeyboard();
        checkValidation();

        if(!isGuestListBlank){

            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            Set<String> keySet = selectedGuest.keySet();

            String res = "";
            for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
                res += iterator.next() + (iterator.hasNext() ? "," : "");
            }

            if (mCurrentSelectedModule != null
                    && (mCurrentSelectedModule.equals(ConstantVariables.ADV_VIDEO_MENU_TITLE)
                    || mCurrentSelectedModule.equals(ConstantVariables.ADV_VIDEO_CHANNEL_MENU_TITLE))) {
                postParams.put("friends_id", res);
            } else {
                postParams.put("user_ids", res);
            }

            mAppConst.postJsonResponseForUrl(inviteFormUrl, postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
//                    Log.d("LambdaCleanUp ", mSnackBarMessage)
                    SnackbarUtils.displaySnackbarShortWithListener(mainLayout, mSnackBarMessage,
                            InviteGuest.this::finish);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbar(mainLayout, message);
                }
            });
        }
    }

    public void checkValidation(){

        if(selectedGuest.size() <= 0){
            mRecipientView.setError(getResources().getString(R.string.field_blank_msg));
            isGuestListBlank = true;
        }else{
            mRecipientView.setError(null);
            isGuestListBlank = false;
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem submit = menu.findItem(R.id.submit);
        if (submit != null) {
            Drawable drawable = submit.getIcon();
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.textColorPrimary), PorterDuff.Mode.SRC_ATOP);
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
