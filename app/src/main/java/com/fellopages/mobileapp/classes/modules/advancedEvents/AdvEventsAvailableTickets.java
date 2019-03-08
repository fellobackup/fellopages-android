package com.fellopages.mobileapp.classes.modules.advancedEvents;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.CustomViews;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.user.profile.MemberInfoFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdvEventsAvailableTickets extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        OnOptionItemClickResponseListener, OnItemClickListener, View.OnClickListener {

    private Context mContext;
    private AppConstant mAppConst;
    private GutterMenuUtils mGutterMenuUtils;
    private BrowseListItems mBrowseListItems;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBrowseAdapter;
    private RelativeLayout mRootView;
    private List<Object> mBrowseItemList;
    private BrowseListItems mBrowseList;
    private JSONArray mGutterMenus;
    private JSONObject mEventInfoObject, mBody;
    private Snackbar snackbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar mToolbar;
    private String currentModule, available_tickets_url;
    private int mLoadingPageNo = 1, mEventId, ticket_id;
    private boolean isLoading = false;
    private boolean isFromWebViewPayment = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_layout);

        mContext = this;
        mAppConst = new AppConstant(this);
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mBrowseItemList = new ArrayList<>();
        mBrowseList = new BrowseListItems();

        View headerView = getLayoutInflater().inflate(R.layout.toolbar, null, false);
        /* Create Back Button On Action Bar **/
        mToolbar = (Toolbar) headerView.findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mContext.getResources().getString(R.string.available_ticket_title));
        }
        FloatingActionButton fab = new FloatingActionButton(mContext);
        fab.setImageResource(R.drawable.ic_action_new);
        fab.setSize(FloatingActionButton.SIZE_NORMAL);
        fab.show();
        fab.setFocusable(true);
        RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lay.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lay.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lay.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.ticket_fab_left_margin), mContext.getResources().getDimensionPixelSize(R.dimen.ticket_fab_top_margin),
                mContext.getResources().getDimensionPixelSize(R.dimen.ticket_fab_right_margin), mContext.getResources().getDimensionPixelSize(R.dimen.ticket_fab_bottom_margin));
        fab.setLayoutParams(lay);

        // Adding fab button to main view.
        ViewGroup vg = (ViewGroup) findViewById(R.id.main_view_recycler);
        vg.addView(fab);

        if(getIntent() != null){
            isFromWebViewPayment = getIntent().getBooleanExtra("isFromWebViewPayment", false);
            try {
                if (getIntent().getStringExtra("urlParams") != null) {
                    mEventInfoObject = new JSONObject(getIntent().getStringExtra("urlParams"));
                    mEventId = mEventInfoObject.optInt("event_id");
                } else {
                    mEventId = getIntent().getIntExtra("isAdvEventId", 0);
                    Log.d("AdvEventsSam ", String.valueOf(mEventId));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        currentModule = ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE;
        mRootView = (RelativeLayout) findViewById(R.id.main_view_recycler);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        // Adding header view to main view.
        mRootView.addView(headerView);
        CustomViews.addHeaderView(R.id.toolbar, swipeRefreshLayout);
        headerView.findViewById(R.id.toolbar).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mBrowseAdapter = new RecyclerViewAdapter(mContext, mBrowseItemList, false, currentModule, this);
        mRecyclerView.setAdapter(mBrowseAdapter);

        available_tickets_url = AppConstant.DEFAULT_URL + "advancedeventtickets/tickets/manage?limit=" + AppConstant.LIMIT + "&page=" +mLoadingPageNo + "&event_id=" + mEventId;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView
                        .getLayoutManager();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleCount = layoutManager.findLastVisibleItemPosition() + 1;
                int visibleItemCount = lastVisibleCount - firstVisibleItem;

                int limit = firstVisibleItem + visibleItemCount;

                if (limit == totalItemCount && !isLoading) {

                    if (limit >= AppConstant.LIMIT && (AppConstant.LIMIT * mLoadingPageNo)
                            < mBrowseList.getmTotalItemCount()) {

                        mLoadingPageNo = mLoadingPageNo + 1;
//                        String url = available_tickets_url + "&page=" + mLoadingPageNo;
                        isLoading = true;
                        loadMoreData(available_tickets_url);
                    }
                }
            }

        });
        if (mBody != null) {
            addDataToList(mBody);
        } else {
            makeRequest();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createEntry;
                String url = null;
                createEntry = new Intent(AdvEventsAvailableTickets.this, CreateNewEntry.class);
                url = AppConstant.DEFAULT_URL + "advancedeventtickets/tickets/add?" + "event_id=" + mEventId;
                createEntry.putExtra(ConstantVariables.CREATE_URL, url);
                createEntry.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE);
                createEntry.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.TICKET_CREATE);
                startActivityForResult(createEntry, ConstantVariables.CREATE_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    public void makeRequest() {
        mLoadingPageNo = 1;
        mAppConst.getJsonResponseFromUrl(available_tickets_url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                // For hiding No Available ticket error message
                if(jsonObject != null){
                    findViewById(R.id.message_layout).setVisibility(View.GONE);
                }

                mBrowseItemList.clear();
                addDataToList(jsonObject);

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, ConstantVariables.REFRESH_DELAY_TIME);

                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(mContext, mRootView, message,
                            new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(mRootView, message);
                }

            }
        });
    }

    public void addDataToList(JSONObject jsonObject) {
        mBody = jsonObject;
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        mGutterMenus = jsonObject.optJSONArray("menu");
        if (mGutterMenus != null)
            invalidateOptionsMenu();
        JSONArray responseArray = jsonObject.optJSONArray("response");

        int mTotalItemCount;
        mTotalItemCount = jsonObject.optInt("totalItemCount");
//        System.out.println("totalItemCount--"+ mTotalItemCount);
        String body = jsonObject.optString("body");
        if (body != null && !body.isEmpty() && mLoadingPageNo == 1) {
            mBrowseItemList.add(body);
        }
        mBrowseList.setmTotalItemCount(mTotalItemCount);

        if (responseArray != null && responseArray.length() > 0) {
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject jsonDataObject = responseArray.optJSONObject(i);
                String ticket_title = jsonDataObject.optString("title");
                String ticket_price = jsonDataObject.optString("price").replace("A","");
                String ticket_quantity = jsonDataObject.optString("quantity");
                ticket_id = jsonDataObject.optInt("ticket_id");
                Log.d("TicketAvailable ", ticket_title+" "+ticket_price);
                mBrowseItemList.add(new BrowseListItems(ticket_title, ticket_price, ticket_quantity,
                        ticket_id, jsonDataObject.optJSONArray("menu")));
                System.out.println("Ticket_Details-- " + ticket_title + " " + ticket_price + " " + ticket_quantity);
                System.out.println("Gutter_options--"+ jsonDataObject.optJSONArray("menu"));

            }
        }else {
            findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf046");
            errorMessage.setText(mContext.getResources().getString(R.string.no_available_tickets));
        }
        mBrowseAdapter.notifyDataSetChanged();

    }

    private void loadMoreData(String url) {
        //add null , so the adapter will check view_type and show progress bar at bottom
        mBrowseItemList.add(null);
        mBrowseAdapter.notifyItemInserted(mBrowseItemList.size() - 1);

        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                //   remove progress item
                mBrowseItemList.remove(mBrowseItemList.size() - 1);
                mBrowseAdapter.notifyItemRemoved(mBrowseItemList.size());

                addDataToList(jsonObject);
                mBrowseAdapter.notifyItemInserted(mBrowseItemList.size());
                isLoading = false;
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                SnackbarUtils.displaySnackbar(mRootView, message);
            }
        });
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                makeRequest();
            }
        });
    }

    @Override
    public void onItemDelete(String successMessage) {

    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {
        mBrowseListItems = (BrowseListItems) itemList;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFromWebViewPayment){
            Intent viewIntent;
            String url = AppConstant.DEFAULT_URL;
            url += "advancedevents/view/" + mEventId + "?gutter_menu=" + 1;
            viewIntent = new Intent(mContext, AdvEventsProfilePage.class);
            if (getIntent().getBooleanExtra(ConstantVariables.KEY_USER_CREATE_SESSION, false))
                viewIntent.putExtra(ConstantVariables.KEY_USER_CREATE_SESSION, true);
            viewIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADVANCED_EVENT_MENU_TITLE);
            viewIntent.putExtra(ConstantVariables.VIEW_PAGE_URL, url);
            viewIntent.putExtra(ConstantVariables.VIEW_PAGE_ID, mEventId);
            viewIntent.putExtra("isRedirectedFromEventProfile", true);
            startActivityForResult(viewIntent, ConstantVariables.CREATE_REQUEST_CODE);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mGutterMenus != null) {
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenus, currentModule, mBrowseListItems);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(mContext)) {
                SoundUtil.playSoundEffectOnBackPressed(mContext);
            } else {
                if (mGutterMenus != null) {

                    mGutterMenuUtils.onMenuOptionItemSelected(mRootView, findViewById(item.getItemId()),
                            id, mGutterMenus);
                }
            }
        }
            return super.onOptionsItemSelected(item);
        }

    @Override
    public void onItemClick(View view, int position) {
        if(mBrowseItemList.size() > 0 ){
            BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
            int mTicketId = listItems.getTicketId();
//            System.out.println("Ticket_Id--"+ mTicketId);
            if (listItems != null) {
                Intent detailsIntent = new Intent(AdvEventsAvailableTickets.this, FragmentLoadActivity.class);
                String url = AppConstant.DEFAULT_URL + "advancedeventtickets/tickets/details?" + "&event_id=" + mEventId + "&ticket_id=" + mTicketId;
                detailsIntent.putExtra(ConstantVariables.URL_STRING, url);
                detailsIntent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.TICKET_DETAILS_FORM_TYPE);
                detailsIntent.putExtra(ConstantVariables.FRAGMENT_NAME, ConstantVariables.TICKET_DETAILS_FRAGMENT_NAME);
                detailsIntent.putExtra(ConstantVariables.CONTENT_TITLE, mContext.getResources().getString(R.string.ticket_details_title));
                detailsIntent.putExtra(ConstantVariables.TOTAL_ITEM_COUNT, 1);
                detailsIntent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.ADV_EVENT_TICKET_MENU_TITLE);
                startActivityForResult(detailsIntent, ConstantVariables.VIEW_PAGE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ConstantVariables.CREATE_REQUEST_CODE:
            case ConstantVariables.VIEW_PAGE_EDIT_CODE:
                if (resultCode == ConstantVariables.PAGE_EDIT_CODE) {
                    swipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(true);
                            makeRequest();
                        }
                    });
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {

    }
}
