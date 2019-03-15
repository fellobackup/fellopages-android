package com.fellopages.mobileapp.classes.modules.likeNComment;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.user.profile.userProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventLikesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private Context mContext;
    private Toolbar mToolbar;
    private ImageLoader mImageLoader;
    private ProgressBar mProgressBar;
    private View mTabSaperator;
    private ViewPager viewPager;
    private TabLayout mTabLayout;
    private ListView mLikeListView;
    private List<CommentList> mLikeListItems;
    private CommentAdapter mLikeAdapter;
    private AppConstant mAppConst;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        mAppConst = new AppConstant(this);
        setupToolbar();
        init();
        makeRequest();
    }

    private void makeRequest() {
        String url = getIntent().getStringExtra(ConstantVariables.VIEW_PAGE_URL);
        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                JSONObject response = jsonObject.getJSONObject("response");
                JSONArray jsonArray = response.getJSONArray("list_of_likers");
                loadData(jsonArray);

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    private void loadData(JSONArray jsonArray) {
        if (jsonArray != null) {
            try {
                List<CommentList> notFriend = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject likeInfoObject = jsonArray.getJSONObject(i);
                    int user_id = likeInfoObject.optInt("user_id");
                    String displayName = likeInfoObject.getString("displayname");
                    String photoUrl = likeInfoObject.optString("image_profile");
                    String friendshipType = likeInfoObject.optString("friendship_type");
                    int isVerified = likeInfoObject.optInt("isVerified", 0);
                    //SORT PEOPLE WHO LIKE THE EVENT
                    if (friendshipType == null)
                        mLikeListItems.add(0, new CommentList(user_id, displayName, photoUrl,
                                friendshipType, isVerified));
                    else if (friendshipType.equals("add_friend")) {
                        notFriend.add(new CommentList(user_id, displayName, photoUrl,
                                friendshipType, isVerified));
                    } else {
                        mLikeListItems.add(new CommentList(user_id, displayName, photoUrl,
                                friendshipType, isVerified));
                    }
                }
                mLikeListItems.addAll(notFriend);
                mLikeAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void init(){
        mLikeListItems = new ArrayList<>();
        mTabSaperator = findViewById(R.id.tabSaperator);
        viewPager = findViewById(R.id.viewpager);
        mTabLayout= findViewById(R.id.tabs);
        mLikeListView = findViewById(R.id.likeList);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        mLikeAdapter = new CommentAdapter(this, R.layout.list_comment, mLikeListItems, new CommentList(), false);
        mLikeListView.setAdapter(mLikeAdapter);
        mLikeListView.setOnItemClickListener(this);

    }

    private void setupToolbar() {
        mToolbar = findViewById(R.id.toolbar);
        mContext = this;
        mImageLoader = new ImageLoader(getApplicationContext());

        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(getResources().getString(R.string.like_activity_text));
        CustomViews.createMarqueeTitle(this, mToolbar);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        CommentList clickedLikedList = mLikeListItems.get(position);
        int userId = clickedLikedList.getmUserId();

        Intent userProfileIntent = new Intent(this, userProfile.class);
        userProfileIntent.putExtra("user_id", userId);
        startActivityForResult(userProfileIntent, ConstantVariables.USER_PROFILE_CODE);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(EventLikesActivity.this)) {
                SoundUtil.playSoundEffectOnBackPressed(EventLikesActivity
                        .this);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
