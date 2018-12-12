package com.socialengineaddons.messenger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.socialengineaddons.messenger.adapter.FragmentAdapter;
import com.socialengineaddons.messenger.contacts.ContactsListFragment;
import com.socialengineaddons.messenger.conversation.ConversationActivity;
import com.socialengineaddons.messenger.conversation.NewConversationActivity;
import com.socialengineaddons.messenger.conversation.RecentConversationListFragment;
import com.socialengineaddons.messenger.groups.GroupsListFragment;
import com.socialengineaddons.messenger.search.SearchContactsActivity;
import com.socialengineaddons.messenger.settings.SettingsFragment;
import com.socialengineaddons.messenger.ui.CustomViewPager;
import com.socialengineaddons.messenger.ui.bottomNavigationBar.BottomNavigationBar;
import com.socialengineaddons.messenger.ui.bottomNavigationBar.BottomNavigationItem;
import com.socialengineaddons.messenger.utils.GlobalFunctionsUtil;
import com.socialengineaddons.messenger.utils.MessengerDatabaseUtils;

public class MessengerActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener, View.OnClickListener {

    private FloatingActionButton floatingActionButton;
    private Context mContext;
    private ProgressBar mProgressBar;
    private BottomNavigationBar mBottomNavigationView;
    private LinearLayout searchViewLayout;
    private CustomViewPager viewPager;
    private FragmentAdapter adapter;
    private MessengerDatabaseUtils messengerDatabaseUtils;
    private DatabaseReference forceUpgradeDB;
    private String packageName = "";
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        mContext = this;
        messengerDatabaseUtils = MessengerDatabaseUtils.getInstance();
        forceUpgradeDB = messengerDatabaseUtils.getForceUpgradeDatabase();
        bundle = getIntent().getExtras();

        initViews();
        checkToShowUpgradeDialog();
    }

    private void initViews(){


        floatingActionButton = (FloatingActionButton) findViewById(R.id.create_fab);

        searchViewLayout = (LinearLayout) findViewById(R.id.searchViewLayout);

        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mBottomNavigationView = (BottomNavigationBar) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationView.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        mBottomNavigationView.setFab(floatingActionButton);
    }

    private void setViews() {
        floatingActionButton.setOnClickListener(this);
        searchViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, SearchContactsActivity.class);
                startActivity(intent);
            }
        });

        TextView tvHome = (TextView) findViewById(R.id.home_button);
        tvHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        addTabsToBottomNavigationView();
    }

    /**
     * Function to check if to show force upgrade dialog or not
     */
    private void checkToShowUpgradeDialog() {

        mProgressBar.bringToFront();
        mProgressBar.setVisibility(View.VISIBLE);
        forceUpgradeDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(View.GONE);
                boolean isNewInfrastructureLive = dataSnapshot.hasChild("newInfrastructureLive")
                        && dataSnapshot.child("newInfrastructureLive").getValue(boolean.class);

                if (isNewInfrastructureLive) {
                    packageName = bundle.getString("package_name");
                    showForceUpgradeDialog();

                } else {
                    setViews();
                    // Open Conversation Activity
                    if (bundle != null && bundle.containsKey("chatKey")) {
                        Intent intent = new Intent(mContext, ConversationActivity.class);
                        intent.putExtra("chatRoomId", bundle.getString("chatKey"));
                        intent.putExtra("typeOfChat", bundle.getString("isGroup").equals("1") ? 1 : 0);
                        intent.putExtra(Constants.SENDER, messengerDatabaseUtils.getCurrentUserId());
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Method to show app upgrade dialog if needed.
     */
    private void showForceUpgradeDialog() {

        View view = getLayoutInflater().inflate(R.layout.upgrade_dialog_layout, null);
        TextView tvUpgrade = (TextView) view.findViewById(R.id.upgrade);
        TextView tvNoThanks = (TextView) view.findViewById(R.id.no_thanks);
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setCornerRadius(30f);
        view.setBackground(drawable);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this,R.style.Theme_Dialog);
        alertBuilder.setView(view);
        final AlertDialog dialog = alertBuilder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = GlobalFunctionsUtil.getDisplayMetricsWidth(mContext) - 100;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(layoutParams);

        // Adding click listener on all buttons, and setting up the value accordingly.
        tvUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAppInPlayStore();
            }
        });
        tvNoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });
    }

    /**
     * function to open the app in playstore
     */
    public void openAppInPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
                    + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/" +
                    "store/apps/details?id=" + packageName)));
        }
    }

    private void addTabsToBottomNavigationView() {

        mBottomNavigationView.setTabSelectedListener(this);
        mBottomNavigationView
                .addItem(new BottomNavigationItem(R.drawable.ic_recent, getResources().getString(R.string.recent)).setActiveColorResource(R.color.colorPrimary).setInActiveColorResource(R.color.icon_color))
                .addItem(new BottomNavigationItem(R.drawable.ic_contact, getResources().getString(R.string.contacts)).setActiveColorResource(R.color.colorPrimary).setInActiveColorResource(R.color.icon_color))
                .addItem(new BottomNavigationItem(R.drawable.ic_groups, getResources().getString(R.string.groups)).setActiveColorResource(R.color.colorPrimary).setInActiveColorResource(R.color.icon_color))
                .addItem(new BottomNavigationItem(R.drawable.ic_setting, getResources().getString(R.string.settings)).setActiveColorResource(R.color.colorPrimary).setInActiveColorResource(R.color.icon_color))
                .setFirstSelectedPosition(0)
                .initialise();

        floatingActionButton.setTag("recent");

        viewPager.setPagingEnabled(false);
        viewPager.setOffscreenPageLimit(4);

        adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(RecentConversationListFragment.newInstance(bundle), "");
        adapter.addFragment(ContactsListFragment.newInstance(null),  "");
        adapter.addFragment(GroupsListFragment.newInstance(null),  "");
        adapter.addFragment(SettingsFragment.newInstance(null),  "");
        viewPager.setAdapter(adapter);
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
                        mContext, R.drawable.ic_create_group));
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
