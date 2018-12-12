package com.fellopages.mobileapp.classes.modules.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.EditEntry;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.DataStorage;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.packages.PackageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShippingMethods extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBrowseAdapter;
    private List<Object> mBrowseItemList;
    private String mURLString;
    private Context mContext;
    private AppConstant mAppConst;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private Snackbar snackbar;
    private ProgressBar mProgressBar;
    private List<BrowseListItems> mShippingDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_methods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mContext = this;
        mAppConst = new AppConstant(mContext);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        mBrowseItemList = new ArrayList<>();
        mBrowseAdapter = new RecyclerViewAdapter(this, mBrowseItemList, true, false, 0,
                ConstantVariables.STORE_MENU_TITLE,
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);
                        Intent packageViewIntent = new Intent(mContext, PackageView.class);
                        packageViewIntent.putExtra("packageObject", listItems.getmShippingItem().toString());
                        packageViewIntent.putExtra(ConstantVariables.SHIPPING_METHOD, ConstantVariables.SHIPPING_METHOD);
                        try {
                            packageViewIntent.putExtra("packageTitle", ((JSONObject)listItems.getmShippingItem()).optJSONObject("title").optString("value"));
                        }catch (Exception e){
                            packageViewIntent.putExtra("packageTitle", "Shipping Info");
                        }
                        startActivity(packageViewIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
        mRecyclerView.setAdapter(mBrowseAdapter);
        mURLString = getIntent().getStringExtra("URL");

        mProgressBar = (ProgressBar) findViewById(R.id.shipping_progressBar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_shipping_method);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addShippingURL = AppConstant.DEFAULT_URL+"sitestore/add-shipping-method/"+getIntent().getIntExtra(ConstantVariables.CONTENT_ID,0);
                Intent intent = new Intent(mContext, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, addShippingURL);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.STORE_MENU_TITLE);
                intent.putExtra(ConstantVariables.FORM_TYPE, ConstantVariables.SHIPPING_METHOD);
                startActivityForResult(intent,ConstantVariables.CREATE_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        makeRequest();
    }
    public void makeRequest(){
        mAppConst.getJsonResponseFromUrl(mURLString, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mProgressBar.setVisibility(View.GONE);
                mBrowseItemList.clear();
                if (snackbar != null && snackbar.isShown()) {
                    snackbar.dismiss();
                }
                addDataToList(jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(mContext,message,Toast.LENGTH_LONG).show();
                if (isRetryOption) {
                    snackbar = SnackbarUtils.displaySnackbarWithAction(mContext,
                            findViewById(R.id.fragment_item_view), message, new SnackbarUtils.OnSnackbarActionClickListener() {
                                @Override
                                public void onSnackbarActionClick() {
                                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                                    makeRequest();
                                }
                            });
                } else {
                    SnackbarUtils.displaySnackbar(findViewById(R.id.fragment_item_view), message);
                }
            }
        });
    }
    public void addDataToList(JSONObject jsonObject){
        mBody = jsonObject;
        mDataResponse = mBody.optJSONArray("response");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject shippingItem = mDataResponse.optJSONObject(i);
                mBrowseItemList.add(new BrowseListItems(shippingItem.optJSONObject("method")));
            }
            mBrowseAdapter.notifyDataSetChanged();
            if(findViewById(R.id.message_layout).getVisibility() == View.VISIBLE){
                findViewById(R.id.message_layout).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = (TextView) findViewById(R.id.error_icon);
            SelectableTextView errorMessage = (SelectableTextView) findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf0d1");
            errorMessage.setText(mContext.getResources().getString(R.string.no_shipping_available));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ConstantVariables.EDIT_ENTRY_RETURN_CODE:
            case ConstantVariables.CREATE_REQUEST_CODE:
                makeRequest();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
