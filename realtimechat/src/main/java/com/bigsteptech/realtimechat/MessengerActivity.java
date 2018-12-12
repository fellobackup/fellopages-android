package com.bigsteptech.realtimechat;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.bigsteptech.realtimechat.adapter.FragmentAdapter;
import com.bigsteptech.realtimechat.contacts.ContactsListFragment;
import com.bigsteptech.realtimechat.conversation.ConversationActivity;
import com.bigsteptech.realtimechat.conversation.NewConversationActivity;
import com.bigsteptech.realtimechat.conversation.RecentConversationListFragment;
import com.bigsteptech.realtimechat.groups.GroupsListFragment;
import com.bigsteptech.realtimechat.search.SearchContactsActivity;
import com.bigsteptech.realtimechat.settings.SettingsFragment;
import com.bigsteptech.realtimechat.ui.CustomViewPager;
import com.bigsteptech.realtimechat.ui.bottomNavigationBar.BottomNavigationBar;
import com.bigsteptech.realtimechat.ui.bottomNavigationBar.BottomNavigationItem;
import com.bigsteptech.realtimechat.utils.MessengerDatabaseUtils;

public class MessengerActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, View.OnClickListener {

    private FloatingActionButton floatingActionButton;
    private Context mContext;
    private BottomNavigationBar mBottomNavigationView;
    private LinearLayout searchViewLayout;
    private CustomViewPager viewPager;
    private FragmentAdapter adapter;
    private MessengerDatabaseUtils messengerDatabaseUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        mContext = this;
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();

        initViews();
        // Open Conversation Activity

        if(getIntent().getExtras() != null ) {
            messengerDatabaseUtils.pushNotificationsClicked();
            if(getIntent().getExtras().containsKey("chatKey")){
                Bundle chatInfo = getIntent().getExtras();
                Intent intent = new Intent(mContext, ConversationActivity.class);
                intent.putExtra("chatRoomId", chatInfo.getString("chatKey"));
                intent.putExtra("typeOfChat", chatInfo.getString("isGroup").equals("1") ? 1 : 0);
                intent.putExtra(Constants.SENDER, messengerDatabaseUtils.getCurrentUserId());
                startActivity(intent);
            }
        }
    }

    private void initViews(){


        floatingActionButton = (FloatingActionButton) findViewById(R.id.create_fab);
        floatingActionButton.setOnClickListener(this);

        searchViewLayout = (LinearLayout) findViewById(R.id.searchViewLayout);
        searchViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, SearchContactsActivity.class);
                startActivity(intent);
            }
        });

        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(4);

        adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(RecentConversationListFragment.newInstance(null), "");
        adapter.addFragment(ContactsListFragment.newInstance(null),  "");
        adapter.addFragment(GroupsListFragment.newInstance(null),  "");
        adapter.addFragment(SettingsFragment.newInstance(null),  "");
        viewPager.setAdapter(adapter);



        floatingActionButton = (FloatingActionButton) findViewById(R.id.create_fab);
        floatingActionButton.setOnClickListener(this);

        mBottomNavigationView = (BottomNavigationBar) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setTabSelectedListener(this);
        mBottomNavigationView.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationView.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationView.setFab(floatingActionButton);

        addTabsToBottomNavigationView();

    }

    private void addTabsToBottomNavigationView() {

        mBottomNavigationView
                .addItem(new BottomNavigationItem(R.drawable.ic_access_time_black_24dp, getResources().getString(R.string.recent)).setActiveColorResource(R.color.colorPrimary).setInActiveColorResource(R.color.icon_color))
                .addItem(new BottomNavigationItem(R.drawable.ic_assignment_ind_black_24dp, getResources().getString(R.string.contacts)).setActiveColorResource(R.color.colorPrimary).setInActiveColorResource(R.color.icon_color))
                .addItem(new BottomNavigationItem(R.drawable.ic_people_white_24dp, getResources().getString(R.string.groups)).setActiveColorResource(R.color.colorPrimary).setInActiveColorResource(R.color.icon_color))
                .addItem(new BottomNavigationItem(R.drawable.ic_settings_24dp, getResources().getString(R.string.settings)).setActiveColorResource(R.color.colorPrimary).setInActiveColorResource(R.color.icon_color))
                .setFirstSelectedPosition(0)
                .initialise();

        floatingActionButton.setTag("recent");
    }

    @Override
    public void onTabSelected(int position) {

        switch (position) {
            case 0:
                viewPager.setCurrentItem(position);
                floatingActionButton.setVisibility(View.VISIBLE);
                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(
                        mContext, R.drawable.ic_chat_black_24dp));
                floatingActionButton.setTag("recent");
                break;

            case 1:
                viewPager.setCurrentItem(position);
                floatingActionButton.setVisibility(View.GONE);
                break;

            case 2:
                viewPager.setCurrentItem(position);
                floatingActionButton.setVisibility(View.VISIBLE);
                floatingActionButton.setImageDrawable(ContextCompat.getDrawable(
                        mContext, R.drawable.ic_group_add_black_24dp));
                floatingActionButton.setTag("groups");
                break;

            case 3:
                viewPager.setCurrentItem(position);
                floatingActionButton.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.create_fab){
            Intent intent = null;
            switch (view.getTag().toString()){

                case "recent":
                    intent = new Intent(mContext, SearchContactsActivity.class);
                    intent.putExtra("showOnlyContacts", true);
                    break;

                case "groups":
                    intent = new Intent(mContext, NewConversationActivity.class);
                    break;
            }

            if(intent != null){
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(Constants.OPEN_MESSENGER_REQUEST, intent);
        super.onBackPressed();
    }
}
