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
import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.classes.common.ui.SelectableTextView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
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
 * Activities that contain this fragment must implement the
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements OnItemClickListener, PopupMenu.OnMenuItemClickListener,
        View.OnClickListener{
    private View mRootView;
    private AppConstant mAppConst;
    private Context mContext;
    private JSONObject mBody;
    private JSONObject mDataResponse;
    private List<CartInfoModel> mCartItemList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mCartViewAdapter;
    private PopupMenu mStoreMenuPopUp;
    private ProgressBar mProgressBar;
    private TextView mCheckoutButton, mStoreNameView;
    private FrameLayout mStoreSelectBtn,mApplyCpnBtn;
    private LinearLayout mCartBottomView, mCartOptionView;
    private LinearLayoutManager mLinearLayoutManager;
    private int isCouponEnabled, selectedStorePosition;
    private boolean isDirectPayment, isStoreSelected = false;
    private String mReorderUrl;
    private CartPreferences mCartPref;

    CartFragmentActionListener mCallback;

    // Container Activity must implement this interface
    public interface CartFragmentActionListener {
        void onViewButtonClicked(Fragment cartFragment, String storeTitle, JSONArray productArray, String defaultCurrency);
        void onApplyCouponButtonClicked(Fragment fragment, String url, String storeId);
        void onCheckoutClicked();
    }

    public CartFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param reorderUrl Re-order Url.
     * @return A new instance of fragment BrowseStoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String reorderUrl) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString("reorder_url",reorderUrl);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragmen_cart,container,false);


        mContext = getActivity();
        mAppConst = new AppConstant(mContext);
        mCartItemList =  new ArrayList<>();
        mCartPref = new CartPreferences();


        mRecyclerView = mRootView.findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mCartViewAdapter = new CartViewAdapter(mContext,mCartItemList,this);
        mRecyclerView.setAdapter(mCartViewAdapter);
        mRecyclerView.setPadding(0,0,0,mContext.getResources().getDimensionPixelSize(R.dimen.playback_view_height));

        mCartBottomView = mRootView.findViewById(R.id.cart_bottom);
        mCartOptionView = mRootView.findViewById(R.id.checkout_option_view);
        mStoreSelectBtn = mRootView.findViewById(R.id.store_selection);
        mApplyCpnBtn = mRootView.findViewById(R.id.apply_coupon_btn);
        mCheckoutButton = mRootView.findViewById(R.id.checkout_button);
        mStoreNameView = mRootView.findViewById(R.id.store_name_view);
        mProgressBar = mRootView.findViewById(R.id.progressBar);

        Drawable[] drawables = mCheckoutButton.getCompoundDrawables();
        for (Drawable drawable:drawables) {
            if(drawable != null)
                DrawableCompat.setTint(drawable, ContextCompat.getColor(mContext,R.color.white));
        }

        mProgressBar.setVisibility(View.VISIBLE);
        if(getArguments() != null && getArguments().getString("reorder_url") != null){
            mReorderUrl = getArguments().getString("reorder_url");
            getOrderInfo(mReorderUrl);
        }else {
            getCartInfo();
        }

        mApplyCpnBtn.setOnClickListener(this);
        mCheckoutButton.setOnClickListener(this);
        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (CartFragmentActionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }
    public void getCartInfo() {
        try {
            mAppConst.showProgressDialog();
            String url = mAppConst.isLoggedOutUser() ? UrlUtil.CART_VIEW_URL + "?productsData="
                    + mCartPref.getProductArray(mContext) :
                    UrlUtil.CART_VIEW_URL + "?limit=20";
            url += CartData.getCouponCodeParams(mContext);
            mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
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
                    SnackbarUtils.displaySnackbar(mRootView, message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getOrderInfo(String url) {
        try {

            mAppConst.postJsonResponseForUrl(url,null, new OnResponseListener() {
                @Override
                public void onTaskCompleted(JSONObject jsonObject) {
                    mProgressBar.setVisibility(View.GONE);
                    addDataToList(jsonObject);
                }

                @Override
                public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                    mProgressBar.setVisibility(View.GONE);
                    SnackbarUtils.displaySnackbar(mRootView, message);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDataToList(JSONObject jsonObject){
        mStoreMenuPopUp = new PopupMenu(mContext, mCartBottomView);
        mBody = jsonObject;
        mDataResponse = mBody.optJSONObject("stores");
        if(mDataResponse != null && mDataResponse.length() > 0) {
            mCartBottomView.setVisibility(View.VISIBLE);
            String currency = mBody.optString("currency");
            double grand_total = mBody.optDouble("grandTotal");
            isDirectPayment = mBody.optBoolean("directPayment");
            isCouponEnabled = mBody.optInt("canApplyCoupon");
            mCheckoutButton.setVisibility(View.VISIBLE);
            JSONObject mainCoupon = mBody.optJSONObject("coupon");
            int totalItemCount = mBody.optInt("totalProductsCount");
            PreferencesUtils.updateCartCount(mContext, String.valueOf(mBody.optInt("totalProductsQuantity")));
            for (int i = 0; i < mDataResponse.length(); i++) {
                JSONObject storeObject = mDataResponse.optJSONObject(mDataResponse.names().opt(i).toString());
                String store_id = mDataResponse.names().opt(i).toString();
                String store_title = storeObject.optString("name");
                int products_count = storeObject.optInt("totalProductsCount");
                int totalProductQty = storeObject.optInt("totalProductsQuantity");
                double totalAmount = storeObject.optDouble("total");
                double tax = 0;
                if(storeObject.has("totalVat")) {
                    tax = storeObject.optDouble("totalVat");
                }
                double subTotal = storeObject.optDouble("subTotal");
                int canApplyCoupon = storeObject.optInt("canApplyCoupon");
                JSONObject couponObject = storeObject.optJSONObject("coupon");
                mStoreMenuPopUp.getMenu().add(Menu.NONE, i, Menu.NONE, store_title);
                JSONArray productArray = storeObject.optJSONArray("products");
                mCartItemList.add(new CartInfoModel(store_id, store_title, products_count, totalProductQty,
                        totalAmount,subTotal,tax,productArray, currency, grand_total, canApplyCoupon,
                        totalItemCount, couponObject));
            }
            if (isDirectPayment) {
                mCartOptionView.setVisibility(View.VISIBLE);
                mStoreSelectBtn.setVisibility(View.VISIBLE);
                if(mCartItemList.size() == 1){
                    isStoreSelected = true;
                    selectedStorePosition = 0;
                    mStoreNameView.setText(mCartItemList.get(0).getStoreTitle());
                    if(mCartItemList.get(0).getCanApplyCoupon() == 1){
                        mApplyCpnBtn.setVisibility(View.VISIBLE);
                    }else {
                        mApplyCpnBtn.setVisibility(View.GONE);
                    }
                }else {
                    mStoreSelectBtn.setOnClickListener(this);
                    mStoreMenuPopUp.setOnMenuItemClickListener(this);
                }

            } else {
                mCartItemList.add(new CartInfoModel(null, null, 0, 0,
                        0,0,0, null, currency, grand_total, isCouponEnabled,
                        totalItemCount, mainCoupon));
                if (isCouponEnabled == 1) {
                    mCartOptionView.setVisibility(View.VISIBLE);
                    mApplyCpnBtn.setVisibility(View.VISIBLE);
                } else {
                    mApplyCpnBtn.setVisibility(View.GONE);
                }
            }

        }else {
            mCartBottomView.setVisibility(View.GONE);
            mRootView.findViewById(R.id.message_layout).setVisibility(View.VISIBLE);
            TextView errorIcon = mRootView.findViewById(R.id.error_icon);
            SelectableTextView errorMessage = mRootView.findViewById
                    (R.id.error_message);
            errorIcon.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            errorIcon.setText("\uf07a");
            errorMessage.setText(mContext.getResources().getString(R.string.error_empty_cart));
            PreferencesUtils.updateCartCount(mContext, "0");
        }
        mCartViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.store_selection:
                mStoreMenuPopUp.show();
                break;
            case R.id.apply_coupon_btn:
                if(isDirectPayment) {
                    mCallback.onApplyCouponButtonClicked(this,"coupon_code_"+
                            mCartItemList.get(selectedStorePosition).getStoreId(),
                            mCartItemList.get(selectedStorePosition).getStoreId());
                }else {
                    mCallback.onApplyCouponButtonClicked(this,"coupon_code",null);

                }
                break;
            case R.id.checkout_button:
                if(isDirectPayment){
                    if(!isStoreSelected) {
                        SnackbarUtils.displaySnackbar(mApplyCpnBtn,
                                mContext.getResources().getString(R.string.store_select_msg));
                    }else {
                        CartData.updateStoreInfo(mContext,mCartItemList.get(selectedStorePosition).getStoreId());
                        mCallback.onCheckoutClicked();
                    }
                }else {
                    CartData.updateStoreInfo(mContext,String.valueOf(0));
                    mCallback.onCheckoutClicked();
                }
                break;
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        isStoreSelected = true;
        selectedStorePosition = item.getItemId();
        mStoreNameView.setText(item.getTitle());
        mRecyclerView.scrollToPosition(item.getItemId());
        if(mCartItemList.get(item.getItemId()).getCanApplyCoupon() == 1){
            mApplyCpnBtn.setVisibility(View.VISIBLE);
        }else {
            mApplyCpnBtn.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public void onItemClick(View view, int position) {
        mCallback.onViewButtonClicked(this,mCartItemList.get(position).getStoreTitle(),
                mCartItemList.get(position).getProductsArray(),mCartItemList.get(position).getDefaultCurrency());
    }

    public void onCouponApplied(JSONObject jsonObject){

        mCartItemList.clear();
        addDataToList(jsonObject);
        SnackbarUtils.displaySnackbar(mApplyCpnBtn,
                mContext.getResources().getString(R.string.coupon_applied_msg));
    }

    public void onCartUpdate(){
        mCartItemList.clear();
        if(mReorderUrl != null && !mReorderUrl.isEmpty()){
            getOrderInfo(mReorderUrl);
        }else {
            getCartInfo();
        }

    }
}
