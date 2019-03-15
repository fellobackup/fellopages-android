package com.fellopages.mobileapp.classes.modules.store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.adapters.RecyclerViewAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.packages.PackageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageDownloadableProduct extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mBrowseAdapter;
    private List<Object> mBrowseItemList;
    private String mURLString,mAddURL,mTitle,mCurrentSelectedOption;
    private Context mContext;
    private AppConstant mAppConst;
    private JSONObject mBody;
    private JSONArray mDataResponse;
    private Snackbar snackbar;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_downloadable_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mContext = this;
        mAppConst = new AppConstant(mContext);
        mCurrentSelectedOption = getIntent().getStringExtra(ConstantVariables.TITLE);
        if(mCurrentSelectedOption.equals("main_file")){
            mTitle = getResources().getString(R.string.main_files);
        }else{
            mTitle = getResources().getString(R.string.sample_files);
        }
        setTitle(mTitle);
        mRecyclerView = findViewById(R.id.recycler_view_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        mBrowseItemList = new ArrayList<>();
        mBrowseAdapter = new RecyclerViewAdapter(this, mBrowseItemList, true, false, 0,
                "downloadable_product",
                new OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        BrowseListItems listItems = (BrowseListItems) mBrowseItemList.get(position);

                    }
                });
        mRecyclerView.setAdapter(mBrowseAdapter);
        mURLString = getIntent().getStringExtra("URL");
        mProgressBar = findViewById(R.id.downloadable_progressBar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL,AppConstant.DEFAULT_URL+mAddURL);
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE,ConstantVariables.PRODUCT_MENU_TITLE);
                intent.putExtra(ConstantVariables.FORM_TYPE,mCurrentSelectedOption);
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
                                    findViewById(R.id.downloadable_progressBar).setVisibility(View.VISIBLE);
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
                JSONObject downloadableItem = mDataResponse.optJSONObject(i);
                mBrowseItemList.add(new BrowseListItems(downloadableItem.optJSONObject("file")));
            }
            mBrowseAdapter.notifyDataSetChanged();
            if(findViewById(R.id.message_layout).getVisibility() == View.VISIBLE){
                findViewById(R.id.message_layout).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = findViewById(R.id.error_icon);
            SelectableTextView errorMessage = findViewById(R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf019");
            errorMessage.setText(mContext.getResources().getString(R.string.no_downloadable_product_available));
        }

        mAddURL = mBody.optString("uploadurl");
        if(mCurrentSelectedOption.equals("sample_file")) {
            mAddURL+="?type=sample";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case ConstantVariables.CREATE_REQUEST_CODE:
            case ConstantVariables.EDIT_ENTRY_RETURN_CODE:
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
