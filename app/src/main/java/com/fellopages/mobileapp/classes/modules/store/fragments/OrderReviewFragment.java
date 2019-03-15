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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.modules.store.adapters.CartViewAdapter;
import com.fellopages.mobileapp.classes.modules.store.data.CartData;
import com.fellopages.mobileapp.classes.modules.store.utils.CartInfoModel;
import com.fellopages.mobileapp.classes.modules.store.utils.CartPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderReviewFragment extends Fragment implements OnItemClickListener,
        View.OnClickListener {
    private View mRootView;
    private AppConstant mAppConst;
    private CartPreferences mCartPref;
    private Context mContext;
    private JSONObject mBody;
    private JSONObject mDataResponse;
    private List<CartInfoModel> mCartItemList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mCartViewAdapter;
    private ProgressBar mProgressBar;
    private TextView mCheckoutButton;
    private FrameLayout mStoreSelectBtn,mApplyCpnBtn;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isVisibleToUser = false;
    OrderReviewFragmentLister mCallback;

    public interface OrderReviewFragmentLister{
        void onItemViewListener(Fragment cartFragment, String storeTitle, JSONArray productArray, String defaultCurrency);
        void onPayNowClicked();
    }
    public OrderReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragmen_cart,container,false);


        mContext = getActivity();
        mAppConst = new AppConstant(mContext);
        mCartPref = new CartPreferences();
        mCartItemList =  new ArrayList<>();

        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mCartViewAdapter = new CartViewAdapter(mContext,mCartItemList,this);
        mRecyclerView.setAdapter(mCartViewAdapter);

        mStoreSelectBtn = mRootView.findViewById(R.id.store_selection);
        mApplyCpnBtn = mRootView.findViewById(R.id.apply_coupon_btn);
        mCheckoutButton = mRootView.findViewById(R.id.checkout_button);
        mProgressBar = mRootView.findViewById(R.id.progressBar);

        Drawable[] drawables = mCheckoutButton.getCompoundDrawables();
        for (Drawable drawable:drawables) {
            if(drawable != null)
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext,R.color.white));
        }

        mProgressBar.setVisibility(View.VISIBLE);


        mApplyCpnBtn.setOnClickListener(this);
        mStoreSelectBtn.setOnClickListener(this);
        mCheckoutButton.setOnClickListener(this);

        return mRootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && !isVisibleToUser) {
            mAppConst.showProgressDialog();
            getOrderInfo();
            Log.d("OrderFragment", "Visible.  start audio.");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisibleToUser);

        // Make sure that we are currently visible
        if (this.isVisible() && isVisible && isVisibleToUser) {
            getOrderInfo();
            Log.d("OrderFragment", "Visible.  Stopping audio.");
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OrderReviewFragmentLister) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    public void getOrderInfo() {
        mCartItemList.clear();

        String orderInfoUrl = UrlUtil.VALIDATE_ORDER_URL
                + "?store_id="+ CartData.getStoreInfo(mContext)
                + "&billingAddress_id="+ CartData.getBillingAddressId(mContext)
                + "&shippingAddress_id="+ CartData.getShippingAddressId(mContext)
                + "&payment_gateway=" + CartData.getPaymentGatewayInfo(mContext)
                + CartData.getShippingMethodInfo(mContext) + CartData.getCouponCodeParams(mContext);
        if(mAppConst.isLoggedOutUser()){
            orderInfoUrl+="&productsData=" + mCartPref.getProductArray(mContext);
        }

        try {

            mAppConst.getJsonResponseFromUrl(orderInfoUrl  , new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mAppConst.hideProgressDialog();
                    mProgressBar.setVisibility(View.GONE);
                    addDataToList(jsonObject);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mAppConst.hideProgressDialog();
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addDataToList(JSONObject jsonObject){
        mRecyclerView.setPadding(0,0,0,mContext.getResources().getDimensionPixelSize(R.dimen.home_icon_tab_height));
        mCheckoutButton.setVisibility(View.VISIBLE);
        mCheckoutButton.setText(mContext.getResources().getString(R.string.place_order_label));
        mBody = jsonObject;
        mDataResponse = mBody.optJSONObject("stores");
        String currency = mBody.optString("currency");
        double grand_total =  mBody.optDouble("grandTotal");
        JSONObject mainCoupon = mBody.optJSONObject("coupon");
        int totalItemCount =  mBody.optInt("totalItemCount");
        JSONArray privateOrderForm = mBody.optJSONArray("form");
        for(int i=0;i< mDataResponse.length();i++){
            JSONObject storeObject =  mDataResponse.optJSONObject(mDataResponse.names().opt(i).toString());
            String store_id = mDataResponse.names().opt(i).toString();
            String store_title = storeObject.optString("name");
            int products_count =  storeObject.optInt("totalProductsCount");
            int totalProductQty = storeObject.optInt("totalProductsQuantity");
            double totalAmount = storeObject.optDouble("total");
            double subTotal = storeObject.optDouble("subTotal");
            double tax = 0;
            if(storeObject.has("tax")){
                tax = storeObject.optDouble("tax");
            }else if(storeObject.has("totalVat")){
                tax = storeObject.optDouble("totalVat");
            }
            int canApplyCoupon = storeObject.optInt("canApplyCoupon");
            JSONObject couponObject = storeObject.optJSONObject("coupon");
            JSONArray productArray = storeObject.optJSONArray("products");
            String shippingMethodName = storeObject.optString("shipping_method");
            double shippingMethodPrice = storeObject.optDouble("shipping_method_price");
            JSONArray noteForm = storeObject.optJSONArray("form");
            mCartItemList.add(new CartInfoModel(store_id,store_title,products_count,totalProductQty,
                    totalAmount,subTotal,productArray,currency,grand_total,tax,canApplyCoupon,totalItemCount,
                    couponObject,shippingMethodName,shippingMethodPrice,noteForm));
        }

        mCartItemList.add(new CartInfoModel(null,null,0,0,0,
                0,null,currency,grand_total,0,0,totalItemCount,mainCoupon,null,0,privateOrderForm));

        mCartViewAdapter.notifyDataSetChanged();
    }
    @Override
    public void onItemClick(View view, int position) {
        mCallback.onItemViewListener(this,mCartItemList.get(position).getStoreTitle(),
                mCartItemList.get(position).getProductsArray(),mCartItemList.get(position).getDefaultCurrency());
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.checkout_button){
            mCallback.onPayNowClicked();
        }
    }
}
