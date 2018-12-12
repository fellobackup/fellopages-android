package com.fellopages.mobileapp.classes.modules.store.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import org.json.JSONObject;

import java.util.Iterator;

public class StoreInfoFragment extends Fragment {
    private View rootView;
    private Context mContext;
    private JSONObject mDataResponse,mProfileFieldInfo;
    private String mOverViewString;
    private GridLayout mInfoFieldsView;
    private Bundle mInfoExtras;
    private WebView mDescriptionText;
    private LinearLayout mProfileInfoView;

    public StoreInfoFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.info_view, null);

        ViewCompat.setNestedScrollingEnabled(rootView,true);

        mDescriptionText = (WebView) rootView.findViewById(R.id.view_description);
        mInfoFieldsView = (GridLayout) rootView.findViewById(R.id.info_fields);
        mProfileInfoView = (LinearLayout) rootView.findViewById(R.id.profile_info_fields);


        mInfoExtras = getArguments();

        setInfoDetails();

        return rootView;

    }

    public void setInfoDetails(){

        try {
            mDataResponse = new JSONObject(mInfoExtras.getString(ConstantVariables.RESPONSE_OBJECT));
            mOverViewString = mInfoExtras.getString(ConstantVariables.OVERVIEW);
            if(mInfoExtras.getString("profileInfo") != null){
                rootView.findViewById(R.id.profile_info_label).setVisibility(View.VISIBLE);
                mProfileInfoView.setVisibility(View.VISIBLE);
                mProfileFieldInfo = new JSONObject(mInfoExtras.getString("profileInfo"));
                LogUtils.LOGD("StoreInfo",mProfileFieldInfo.toString());
                getKeyValuePairs(mProfileFieldInfo.keys(),mProfileFieldInfo);
            }
            showFieldsWithInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void getKeyValuePairs(Iterator<String> iterator, JSONObject jsonObject){
        GridLayout gridLayout = new GridLayout(mContext);
        gridLayout.setColumnCount(2);
        while (iterator.hasNext()){
            String key = iterator.next();
            if(jsonObject.optString(key)!= null && jsonObject.optJSONObject(key) != null){
                AppCompatTextView textView = new AppCompatTextView(mContext);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.body_medium_font_size));
                textView.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
                textView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        0, getResources().getDimensionPixelSize(R.dimen.padding_5dp));
                textView.setTextColor(ContextCompat.getColor(mContext,R.color.black));
                textView.setText(key);
                mProfileInfoView.addView(textView);
                LogUtils.LOGD("StoreInfo - Json",jsonObject.optJSONObject(key).toString());
                getKeyValuePairs(jsonObject.optJSONObject(key).keys(),jsonObject.optJSONObject(key));
            }else {
                LogUtils.LOGD("StoreInfo","Key - "+key +" Value - "+jsonObject.optString(key));
                showFieldInformation(gridLayout,key,jsonObject);
            }
        }
        mProfileInfoView.addView(gridLayout);
    }

    public void showFieldInformation(GridLayout gridLayout,String key,JSONObject jsonObject){

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.setGravity(Gravity.FILL_HORIZONTAL);
        AppCompatTextView labelView = new AppCompatTextView(mContext);
        AppCompatTextView mainText = new AppCompatTextView(mContext);
        labelView.setText(key + "   :   ");
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mContext.getResources().getDimension(R.dimen.body_default_font_size));
        labelView.setGravity(GravityCompat.START| Gravity.CENTER_VERTICAL);
        labelView.setPadding(getResources().getDimensionPixelSize(R.dimen.offset_distance),
                getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                getResources().getDimensionPixelSize(R.dimen.offset_distance),
                getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));
        mainText.setText(jsonObject.optString(key));
        mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mContext.getResources().getDimension(R.dimen.body_default_font_size));

        mainText.setLayoutParams(layoutParams);
        mainText.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                getResources().getDimensionPixelSize(R.dimen.offset_distance),
                getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));
        gridLayout.addView(labelView);
        gridLayout.addView(mainText);

    }
    public void showFieldsWithInfo(){
        mInfoFieldsView.removeAllViews();

        Iterator<String> keys = mDataResponse.keys();
        while( keys.hasNext() ) {
            showFieldInformation(mInfoFieldsView, keys.next(),mDataResponse);
        }

        if(mOverViewString != null && mOverViewString.length() > 0){
            rootView.findViewById(R.id.overview_label).setVisibility(View.VISIBLE);
            mDescriptionText.setVisibility(View.VISIBLE);
              /* Setting Body in TextView */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mDescriptionText.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            } else {
                mDescriptionText.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }
            WebSettings webSettings = mDescriptionText.getSettings();
            webSettings.setDefaultFontSize(14);
            mDescriptionText.loadDataWithBaseURL("file:///android_asset/",
                        GlobalFunctions.getHtmlData(mContext, mOverViewString, true), "text/html", "utf-8", null);

        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
