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

package com.fellopages.mobileapp.classes.modules.store.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.store.data.CartData;
import com.fellopages.mobileapp.classes.modules.store.utils.CartPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ShippingDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShippingDetailFragment extends Fragment implements View.OnClickListener {
    private AppConstant mAppConst;
    private CartPreferences mCartPref;
    private View mRootView;
    private Context mContext;
    private TextView mContinueButton,mBillingRegionView,mShippingRegionView;
    private SwitchCompat mAddrSwitch;
    private LinearLayout mShippingBlock;
    private JSONObject mFormValues,mFormResponse,mShippingDetails, mBillingDetails;
    private JSONArray mShippingForm, mBillingForm, mBRegionArray,mSRegionArray;
    private EditText mBFirstName,mBLastName,mBPhoneNo,mBCountry,mBRegion,mBCity,mBLoc,mBZip,mBAddress;
    private EditText mSFirstName,mSLastName,mSPhoneNo,mSCountry,mSRegion,mSCity,mSLoc,mSZip,mSAddress;
    private EditText mBEmailId;
    private HashMap<String,String> postParams;
    private String mBillingCountry="", mShippingCountry="", mBillingState="",mShippingState="";
    private boolean isRefreshingStates = false,isVisibleToUser = false;


    ShippingDetailFragmentListener mCallback;

    public interface ShippingDetailFragmentListener{
        void onMultiSelectionCalled(Fragment shippingDetailFragment, String label,
                                    JSONObject multiOptions, JSONArray multiOptionsArray);
        void onContinueOrderClicked(String store_id, boolean b);
    }

    public ShippingDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShippingDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShippingDetailFragment newInstance(String param1, String param2) {
        ShippingDetailFragment fragment = new ShippingDetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mContext = getContext();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getContext();
        mAppConst =  new AppConstant(getContext());
        mCartPref = new CartPreferences();
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_shipping_detail,container,false);


        mShippingBlock = mRootView.findViewById(R.id.shipping_block);
        mBFirstName = mRootView.findViewById(R.id.bill_name);
        mBLastName = mRootView.findViewById(R.id.bill_send_name);
        mBPhoneNo = mRootView.findViewById(R.id.bill_ph_no);
        mBCountry = mRootView.findViewById(R.id.bill_country);
        mBRegion = mRootView.findViewById(R.id.bill_rgn);
        mBCity = mRootView.findViewById(R.id.bill_city);
        mBLoc = mRootView.findViewById(R.id.bill_locality);
        mBZip = mRootView.findViewById(R.id.bill_zip);
        mBAddress = mRootView.findViewById(R.id.bill_addr);
        mBEmailId = mRootView.findViewById(R.id.email_billing);

        mBillingRegionView = mRootView.findViewById(R.id.billing_region);
        mShippingRegionView = mRootView.findViewById(R.id.shipping_region);
        mBillingRegionView.setText(mContext.getResources().getString(R.string.region_label)+" / "
                +mContext.getResources().getString(R.string.state_label));
        mShippingRegionView.setText(mContext.getResources().getString(R.string.region_label)+" / "
                +mContext.getResources().getString(R.string.state_label));


        mContinueButton = mRootView.findViewById(R.id.order);
        mContinueButton.setOnClickListener(this);
        for (Drawable drawable : mContinueButton.getCompoundDrawables()) {
            if (drawable != null) {
                DrawableCompat.setTint(drawable,ContextCompat.getColor(getActivity(),R.color.white));
            }
        }
        mAddrSwitch = mRootView.findViewById(R.id.addr_switch);
        mAddrSwitch.setChecked(true);
        mAddrSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(!isChecked){
                    initializeShippingDetails();
                }else {
                    mShippingBlock.setVisibility(View.GONE);
                }
            }
        });

        return mRootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            getShippingBillingDetails();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible() && isVisible && isVisibleToUser) {
                getShippingBillingDetails();
        }
    }

    public void getShippingBillingDetails(){
        mAppConst.showProgressDialog();
        mAppConst.getJsonResponseFromUrl(mAppConst.isLoggedOutUser() ? UrlUtil.CHECKOUT_ADDRESS_URL
                        + CartData.getStoreInfo(mContext) +"&productsData=" + mCartPref.getProductArray(mContext)
                        : UrlUtil.CHECKOUT_ADDRESS_URL +
                CartData.getStoreInfo(mContext)
                , new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mAppConst.hideProgressDialog();
                isVisibleToUser = true;
                mFormValues = jsonObject.optJSONObject("formValues");
                mFormResponse = jsonObject.getJSONObject("form");

                mBillingForm = mFormResponse.optJSONArray("billingForm");
                mShippingForm = mFormResponse.optJSONArray("shippingForm");
                if(mFormValues != null){
                    mBillingDetails = mFormValues.optJSONObject("billingAddress");
                    mShippingDetails = mFormValues.optJSONObject("shippingAddress");
                }
                initializeBillingDetails();

                if(mShippingForm != null){
                    mRootView.findViewById(R.id.switch_view).setVisibility(View.VISIBLE);
                }else {
                    if(getActivity() != null && ((AppCompatActivity)getActivity()).getSupportActionBar() !=null) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(
                                mContext.getResources().getString(R.string.billing_details_label));
                    }
                    mShippingBlock.setVisibility(View.GONE);
                    mRootView.findViewById(R.id.switch_view).setVisibility(View.GONE);
                }

                if(mAppConst.isLoggedOutUser()){
                    mBEmailId.requestFocus();
                    mRootView.findViewById(R.id.email_view).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbar(mRootView, message);
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ShippingDetailFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void initializeBillingDetails(){

        if(mBillingDetails != null) {
            mBFirstName.setText(mBillingDetails.optString("f_name_billing"));
            mBLastName.setText(mBillingDetails.optString("l_name_billing"));
            mBPhoneNo.setText(mBillingDetails.optString("phone_billing"));
            for (int i = 0; i < mBillingForm.length(); i++) {
                JSONObject object = mBillingForm.optJSONObject(i);
                if (object.optString("country_billing") != null &&
                        object.optJSONObject("multiOptions") != null) {
                    mBillingCountry = mBillingDetails.optString("country_billing");
                    mBCountry.setText(object.optJSONObject("multiOptions").
                            optString(mBillingDetails.optString("country_billing")));
                }
                if (object.optString("state_billing") != null &&
                        object.optJSONArray("multiOptions") != null) {
                    mBillingState = mBillingDetails.optString("state_billing");
                    mBRegion.setText(object.optJSONArray("multiOptions").
                            optString(mBillingDetails.optInt("state_billing")));
                }

                // Checking for visibility of the fields.
                switch (object.optString("name")) {
                    case "f_name_billing":
                        mRootView.findViewById(R.id.layout_bill_name).setVisibility(View.VISIBLE);
                        break;
                    case "l_name_billing":
                        mRootView.findViewById(R.id.layout_bill_second_name).setVisibility(View.VISIBLE);
                        break;
                    case "phone_billing":
                        mRootView.findViewById(R.id.layout_bill_ph_no).setVisibility(View.VISIBLE);
                        break;
                    case "country_billing":
                        mRootView.findViewById(R.id.layout_bill_country).setVisibility(View.VISIBLE);
                        break;
                    case "state_billing":
                        mRootView.findViewById(R.id.layout_bill_region).setVisibility(View.VISIBLE);
                        break;
                    case "city_billing":
                        mRootView.findViewById(R.id.layout_bill_city).setVisibility(View.VISIBLE);
                        break;
                    case "locality_billing":
                        mRootView.findViewById(R.id.layout_bill_locality).setVisibility(View.VISIBLE);
                        break;
                    case "zip_billing":
                        mRootView.findViewById(R.id.layout_bill_zip).setVisibility(View.VISIBLE);
                        break;
                    case "address_billing":
                        mRootView.findViewById(R.id.layout_bill_addr).setVisibility(View.VISIBLE);
                        break;
                }
            }
            mBCity.setText(mBillingDetails.optString("city_billing"));
            mBLoc.setText(mBillingDetails.optString("locality_billing"));
            mBZip.setText(mBillingDetails.optString("zip_billing"));
            mBAddress.setText(mBillingDetails.optString("address_billing"));
        }


        mBFirstName.setTag("f_name_billing");
        mBLastName.setTag("l_name_billing");
        mBPhoneNo.setTag("phone_billing");
        mBCity.setTag("city_billing");
        mBLoc.setTag("locality_billing");
        mBZip.setTag("zip_billing");
        mBAddress.setTag("address_billing");
        mBCountry.setTag("country_billing");
        mBRegion.setTag("state_billing");
        mBEmailId.setTag("email_billing");
        mBCountry.setOnClickListener(this);
        mBRegion.setOnClickListener(this);

    }

    public void initializeShippingDetails(){

        mShippingBlock.setVisibility(View.VISIBLE);
        mSFirstName = mRootView.findViewById(R.id.ship_name);
        mSLastName = mRootView.findViewById(R.id.ship_second_name);
        mSPhoneNo = mRootView.findViewById(R.id.ship_ph_no);
        mSCountry = mRootView.findViewById(R.id.ship_country);
        mSRegion = mRootView.findViewById(R.id.ship_rgn);
        mSCity = mRootView.findViewById(R.id.ship_city);
        mSZip = mRootView.findViewById(R.id.ship_zip);
        mSAddress = mRootView.findViewById(R.id.ship_addr);
        mSLoc = mRootView.findViewById(R.id.ship_locality);

        if(mShippingDetails != null) {

            mSFirstName.setText(mShippingDetails.optString("f_name_shipping"));
            mSLastName.setText(mShippingDetails.optString("l_name_shipping"));
            mSPhoneNo.setText(mShippingDetails.optString("phone_shipping"));
            for (int i =0; i< mShippingForm.length();i++) {
                JSONObject object = mShippingForm.optJSONObject(i);
                if(object.optString("country_shipping") != null&&
                        object.optJSONObject("multiOptions") != null) {
                    mShippingCountry = mShippingDetails.optString("country_shipping");
                    mSCountry.setText(object.optJSONObject("multiOptions").
                            optString(mShippingDetails.optString("country_shipping")));
                }
                if(object.optString("state_shipping") != null &&
                        object.optJSONArray("multiOptions") != null) {
                    mShippingState = mShippingDetails.optString("state_shipping");
                    mSRegion.setText(object.optJSONArray("multiOptions").
                            optString(mShippingDetails.optInt("state_shipping")));
                }
                // Checking for visibility of the fields.
                switch (object.optString("name")) {
                    case "f_name_shipping":
                        mRootView.findViewById(R.id.layout_ship_name).setVisibility(View.VISIBLE);
                        break;
                    case "l_name_shipping":
                        mRootView.findViewById(R.id.layout_ship_second_name).setVisibility(View.VISIBLE);
                        break;
                    case "phone_shipping":
                        mRootView.findViewById(R.id.layout_ship_ph_no).setVisibility(View.VISIBLE);
                        break;
                    case "country_shipping":
                        mRootView.findViewById(R.id.layout_ship_country).setVisibility(View.VISIBLE);
                        break;
                    case "state_shipping":
                        mRootView.findViewById(R.id.layout_ship_region).setVisibility(View.VISIBLE);
                        break;
                    case "city_shipping":
                        mRootView.findViewById(R.id.layout_ship_city).setVisibility(View.VISIBLE);
                        break;
                    case "locality_shipping":
                        mRootView.findViewById(R.id.layout_ship_locality).setVisibility(View.VISIBLE);
                        break;
                    case "zip_shipping":
                        mRootView.findViewById(R.id.layout_ship_zip).setVisibility(View.VISIBLE);
                        break;
                    case "address_shipping":
                        mRootView.findViewById(R.id.layout_ship_addr).setVisibility(View.VISIBLE);
                        break;
                }
            }
            mSCity.setText(mShippingDetails.optString("city_shipping"));
            mSLoc.setText(mShippingDetails.optString("locality_shipping"));
            mSZip.setText(mShippingDetails.optString("zip_shipping"));
            mSAddress.setText(mShippingDetails.optString("address_shipping"));

            mSPhoneNo.setTag("phone_shipping");
            mSFirstName.setTag("f_name_shipping");
            mSLastName.setTag("l_name_shipping");
            mSCity.setTag("city_shipping");
            mSLoc.setTag("locality_shipping");
            mSZip.setTag("zip_shipping");
            mSAddress.setTag("address_shipping");
            mSCountry.setTag("country_shipping");
            mSRegion.setTag("state_shipping");
            mSCountry.setOnClickListener(this);
            mSRegion.setOnClickListener(this);
        }

    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.bill_rgn:
               if(!isRefreshingStates) {
                   showOptions(view.getTag().toString(), mBillingForm);
               }
               break;
           case R.id.bill_country:
               showOptions(view.getTag().toString(),mBillingForm);
               break;
           case R.id.ship_rgn:
               if(!isRefreshingStates) {
                   showOptions(view.getTag().toString(), mShippingForm);
               }
               break;
           case R.id.ship_country:
               showOptions(view.getTag().toString(),mShippingForm);
               break;
           case R.id.order:
               postBillingDetails();
               break;

       }
    }

    public void showOptions(String name,JSONArray valueForm){

        for(int i = 0; i < valueForm.length();i++){
            JSONObject formObject =  valueForm.optJSONObject(i);
            String objectName = formObject.optString("name");
            if(objectName.equals(name)) {
                switch (name) {
                    case "state_shipping":
                        mCallback.onMultiSelectionCalled(this, objectName, formObject, mSRegionArray);
                        break;
                    case "state_billing":
                        mCallback.onMultiSelectionCalled(this, objectName, formObject, mBRegionArray);
                        break;
                    case "country_shipping":
                    case "country_billing":
                        mCallback.onMultiSelectionCalled(this, objectName,
                                formObject, null);
                        break;
                }
            }
        }

    }

    public void setOptionValue(final String viewTag, String item, final String key){
        EditText editText = mRootView.findViewWithTag(viewTag);

        switch (viewTag){
            case "country_shipping":
            case "country_billing":
                isRefreshingStates = true;
                mAppConst.getJsonResponseFromUrl(mAppConst.isLoggedOutUser() ? UrlUtil.GET_STATE_URL
                                + CartData.getStoreInfo(mContext)+"&country=" + key +"&productsData="
                        + mCartPref.getProductArray(mContext) : UrlUtil.GET_STATE_URL +
                        CartData.getStoreInfo(mContext)+"&country=" + key,
                        new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) {
                        LogUtils.LOGD("FormArray",viewTag);
                        LogUtils.LOGD("ViewTag",jsonObject.toString());
                        if(viewTag.equals("country_billing")) {
                            mBillingCountry = key;
                            mBRegion.setText("");
                            mBRegionArray = jsonObject.optJSONArray("response");
                        }else {
                            mShippingCountry = key;
                            mSRegion.setText("");
                            mSRegionArray = jsonObject.optJSONArray("response");
                        }
                        isRefreshingStates = false;
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        SnackbarUtils.displaySnackbar(mRootView, message);
                    }
                });
                break;
            case "state_billing":
                mBillingState = key;
                break;
            case "state_shipping":
                mShippingState = key;
                break;

        }
        editText.setText(item);

    }

    public void postBillingDetails(){

        boolean isValidationFailed = false;
        postParams = new HashMap<>();
        if(mBillingForm != null) {
            for (int i = 0; i < mBillingForm.length(); i++) {
                JSONObject object = mBillingForm.optJSONObject(i);
                EditText editText = mRootView.findViewWithTag(object.optString("name"));
                if (editText != null) {
                    if (editText.getText().length() != 0) {
                        LogUtils.LOGD("Shipping Details", mBillingForm.optJSONObject(i).optString("name"));
                        postParams.put(mBillingForm.optJSONObject(i).optString("name"), editText.getText().toString());
                    } else if (object.optBoolean("hasValidator")){
                        editText.setError(mContext.getResources().getString(R.string.widget_error_msg));
                        isValidationFailed = true;
                        break;
                    }
                }
            }
            postParams.put("country_billing",mBillingCountry);
            postParams.put("state_billing",mBillingState);
        }

        if(mShippingForm != null && !isValidationFailed){
            for(int i=0; i < mShippingForm.length();i++){
                EditText editText;
                if(mAddrSwitch.isChecked()) {
                    postParams.put("common", "1");
                }else {
                    JSONObject object = mShippingForm.optJSONObject(i);
                    editText = mRootView.findViewWithTag(object.optString("name"));
                    if(editText.getText().length() != 0){
                        LogUtils.LOGD("Shipping Details", mShippingForm.optJSONObject(i).optString("name"));
                        postParams.put(mShippingForm.optJSONObject(i).optString("name"),
                                editText.getText().toString());
                    }else if (object.optBoolean("hasValidator")){
                        editText.setError(mContext.getResources().getString(R.string.widget_error_msg));
                        isValidationFailed = true;
                        break;
                    }
                    postParams.put("country_shipping",mShippingCountry);
                    postParams.put("state_shipping",mShippingState);
                }
            }
        }

        if(!isValidationFailed) {
            mAppConst.showProgressDialog();
            LogUtils.LOGD("Shipping Details", UrlUtil.POST_BILL_SHIP_URL + CartData.getStoreInfo(mContext));
            mAppConst.postJsonResponseForUrl(mAppConst.isLoggedOutUser() ? UrlUtil.POST_BILL_SHIP_URL
                    + CartData.getStoreInfo(mContext) +"&productsData="
                    + mCartPref.getProductArray(mContext) : UrlUtil.POST_BILL_SHIP_URL +
                    CartData.getStoreInfo(mContext), postParams, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();
                    CartData.updateBillingAddressId(mContext,jsonObject.optString("billingAddress"));
                    CartData.updateShippingAddressId(mContext,jsonObject.optString("shippingAddress"));
                    mCallback.onContinueOrderClicked(CartData.getStoreInfo(mContext),mShippingForm != null);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mAppConst.hideProgressDialog();
                    try {

                        if(message.contains("state_billing")){
                            JSONObject jsonObject = new JSONObject(message);
                            SnackbarUtils.displaySnackbar(mRootView, jsonObject.optString("state_billing"));
                        }else {
                            SnackbarUtils.displaySnackbar(mRootView, message);
                        }
                    }catch (NullPointerException|JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
