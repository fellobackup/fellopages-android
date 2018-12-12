package com.fellopages.mobileapp.classes.modules.store;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.WebViewActivity;
import com.fellopages.mobileapp.classes.common.adapters.FragmentAdapter;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.store.adapters.ProductListingAdapter;
import com.fellopages.mobileapp.classes.modules.store.adapters.SimpleSheetAdapter;
import com.fellopages.mobileapp.classes.modules.store.data.CartData;
import com.fellopages.mobileapp.classes.modules.store.fragments.CartFragment;
import com.fellopages.mobileapp.classes.modules.store.fragments.OrderReviewFragment;
import com.fellopages.mobileapp.classes.modules.store.fragments.ShippingDetailFragment;
import com.fellopages.mobileapp.classes.modules.store.helpers.SwipeDismissHelper;
import com.fellopages.mobileapp.classes.modules.store.ui.CustomViewPager;
import com.fellopages.mobileapp.classes.modules.store.utils.CartPreferences;
import com.fellopages.mobileapp.classes.modules.store.utils.ProductInfoModel;
import com.fellopages.mobileapp.classes.modules.store.utils.SheetItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CartView extends FormActivity implements CartFragment.CartFragmentActionListener,
        ShippingDetailFragment.ShippingDetailFragmentListener, ProductListingAdapter.OnItemClickListener,
        View.OnClickListener, OrderReviewFragment.OrderReviewFragmentLister, ViewPager.OnPageChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private AppConstant mAppConst;
    private FragmentAdapter mPagerAdapter;
    private TabLayout mTabLayout;
    private Toolbar toolbar;
    private BottomSheetBehavior<View> behavior;
    private RecyclerView mProductListView;
    private ProductListingAdapter mProductListingAdapter;
    private List<ProductInfoModel> mProductInfoList;
    private BottomSheetDialog mShippingMethodDialog,mPaymentOptionDialog;
    private Context mContext;
    private ArrayList<String> mShippingMethodList,mPaymentOptionList;
    private TextView mStoreTitle,mUpdateCartButton;
    private HashMap<String,String> postParams;
    private CartFragment cartFragmentInstance;
    private OrderReviewFragment reviewFragmentInstance;
    private CartPreferences mCartPref;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_activity_view);
        mContext = this;
        initBottomSheet();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        mAppConst = new AppConstant(this);
        mCartPref = new CartPreferences();
        mProductInfoList = new ArrayList<>();
        mStoreTitle = (TextView) findViewById(R.id.store_title);
        mUpdateCartButton = (TextView) findViewById(R.id.update_cart);
        mUpdateCartButton.setOnClickListener(this);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(this);
        mTabLayout = (TabLayout) findViewById(R.id.materialTabHost);
        mTabLayout.setVisibility(View.GONE);
        mProductListView = (RecyclerView) findViewById(R.id.product_list_view);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mPagerAdapter = new FragmentAdapter(getSupportFragmentManager());

        if(getIntent().getStringExtra("order_url") != null){
            mPagerAdapter.addFragment(CartFragment.newInstance(getIntent().getStringExtra("order_url")),
                    getResources().getString(R.string.title_activity_cart_view));
        }else {
            mPagerAdapter.addFragment(new CartFragment(),getResources().getString(R.string.title_activity_cart_view));
        }

        mPagerAdapter.addFragment(new ShippingDetailFragment(),getResources().getString(R.string.shipping_detail_tab_name));
        mPagerAdapter.addFragment(new OrderReviewFragment(),getResources().getString(R.string.order_review_label));
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount() + 1);
        mViewPager.setAdapter(mPagerAdapter);
        mProductListingAdapter =  new ProductListingAdapter(this,mProductInfoList,this);
        mProductListView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.close_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() != 0) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

                    alertBuilder.setMessage(mContext.getResources().getString(R.string.cancel_transaction_msg));
                    alertBuilder.setTitle(mContext.getResources().getString(R.string.cancel_transaction_title));

                    alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.yes_label),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                }
                            });
                    alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.no_text),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertBuilder.setCancelable(false);
                    alertBuilder.create().show();

                }else {
                    onBackPressed();
                }
            }
        });


    }

    private void initBottomSheet() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setSkipCollapsed(true);
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    @Override
    public void onViewButtonClicked(Fragment fragment, String storeTitle, JSONArray productArray, String defaultCurrency) {

        ItemTouchHelper.Callback swipecallback =  new SwipeDismissHelper(mProductListingAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(swipecallback);
        helper.attachToRecyclerView(mProductListView);
        mUpdateCartButton.setVisibility(View.VISIBLE);
        cartFragmentInstance = (CartFragment) fragment;
        mStoreTitle.setText(storeTitle);
        mProductInfoList.clear();
        mProductListView.setAdapter(mProductListingAdapter);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        for(int i=0;i < productArray.length();i++){
            JSONObject productObject =  productArray.optJSONObject(i);
            if(mAppConst.isLoggedOutUser()){
                mProductInfoList.add(new ProductInfoModel(productObject.optString("title"),
                        productObject.optString("image_profile"), mAppConst.isLoggedOutUser() ?
                        productObject.optInt("product_id") : productObject.optInt("cartproduct_id"),
                        productObject.optInt("quantity"), productObject.optDouble("price"),
                        productObject.optDouble("unitPrice"), productObject.optString("error"),
                        productObject.optJSONObject("configuration"),
                        productObject.optJSONObject("configFields"),defaultCurrency,true));
            }else {
                mProductInfoList.add(new ProductInfoModel(productObject.optString("title"),
                        productObject.optString("image_profile"), mAppConst.isLoggedOutUser() ?
                        productObject.optInt("product_id") : productObject.optInt("cartproduct_id"),
                        productObject.optInt("quantity"), productObject.optDouble("price"),
                        productObject.optDouble("unitPrice"), productObject.optString("error"),
                        productObject.optJSONObject("configuration"),defaultCurrency, true));
            }

        }
        mProductListingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onApplyCouponButtonClicked(final Fragment fragment, final String couponParam, final String storeId) {
        View applyCouponView = getLayoutInflater().inflate(R.layout.apply_coupon_view,null);
        TextView applyBtn = (TextView) applyCouponView.findViewById(R.id.apply_btn);
        final TextView errorView = (TextView) applyCouponView.findViewById(R.id.coupon_error);
        final EditText couponText = (EditText) applyCouponView.findViewById(R.id.apply_coupon_text);
        final ProgressBar progressBar = (ProgressBar) applyCouponView.findViewById(R.id.loadingProgressBar);
        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(couponText.getText().length() > 0){
                    progressBar.setVisibility(View.VISIBLE);
                    mAppConst.getJsonResponseFromUrl(UrlUtil.CART_VIEW_URL + "?"+
                            couponParam + "="+couponText.getText().toString() +
                            (mAppConst.isLoggedOutUser() ? "&productsData=" + mCartPref.getProductArray(mContext):"") ,
                            new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                            CartData.updateCouponCode(mContext, couponParam, couponText.getText().toString());
                            progressBar.setVisibility(View.GONE);
                            if(storeId!= null &&
                                    !jsonObject.optJSONObject("stores")
                                            .optJSONObject(storeId).isNull("couponerror")){
                                errorView.setVisibility(View.VISIBLE);
                                errorView.setText(jsonObject.optJSONObject("stores")
                                        .optJSONObject(storeId).optString("couponerror"));

                            }else if(!jsonObject.isNull("couponerror")) {

                                errorView.setVisibility(View.VISIBLE);
                                errorView.setText(jsonObject.optString("couponerror"));

                            }else {
                                errorView.setVisibility(View.GONE);
                                mShippingMethodDialog.dismiss();
                                ((CartFragment) fragment).onCouponApplied(jsonObject);
                            }
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            SnackbarUtils.displaySnackbar(view, message);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

            }
        });
        showBottomSheet(true,applyCouponView);


    }

    @Override
    public void onCheckoutClicked() {
        mViewPager.setCurrentItem(1);
    }


    @Override
    public void onMultiSelectionCalled(final Fragment fragment, final String viewTag,
                                       JSONObject multiOptions, JSONArray updatedCountryList) {
        List<SheetItemModel> list = new ArrayList<>();
        if(updatedCountryList == null && multiOptions.optJSONObject("multiOptions") != null) {
            Iterator<?> keys = multiOptions.optJSONObject("multiOptions").keys();
            if(keys != null) {
                keys.next();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    list.add(new SheetItemModel(multiOptions.optJSONObject("multiOptions").optString(key), key));
                }
            }
        }else {
            JSONArray countryArray = multiOptions.optJSONArray("multiOptions");
            if(updatedCountryList != null){
                LogUtils.LOGD("UpdatedCountryList",updatedCountryList.toString());
                countryArray = updatedCountryList;
            }
            for(int i = 1;i < countryArray.length();i++){
                list.add(new SheetItemModel(countryArray.optString(i), String.valueOf(i-1)));
            }
        }

        SimpleSheetAdapter adapter = new SimpleSheetAdapter(list);
        adapter.setOnItemClickListener(new SimpleSheetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SheetItemModel item, int position) {
                mShippingMethodDialog.dismiss();
                ((ShippingDetailFragment)fragment).setOptionValue(viewTag,item.getName(),item.getKey());
            }
        });


        View view = getLayoutInflater().inflate(R.layout.fragmen_cart, null);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        view.findViewById(R.id.cart_bottom).setVisibility(View.GONE);
        recyclerView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        showBottomSheet(true,view);

    }

    @Override
    public void onContinueOrderClicked(String store_id, boolean isShippingRequest) {
        if(isShippingRequest) {
            CartData.clearShippingInfo(mContext);
            final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_view, null);
            final LinearLayout shippingMethodBlock = (LinearLayout) view.findViewById(R.id.custom_fields_block);
            showBottomSheet(true, view);
            mAppConst.getJsonResponseFromUrl(UrlUtil.SHIPPING_METHOD_URL
                    + store_id + (mAppConst.isLoggedOutUser() ? "&productsData=" + mCartPref.getProductArray(mContext):""),
                    new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                            view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                            shippingMethodBlock.setVisibility(View.VISIBLE);
                            shippingMethodBlock.setTag("ship");
                            mShippingMethodList = new ArrayList<>();
                            if(jsonObject != null) {
                                createViewForBottomSheet(jsonObject, shippingMethodBlock, mShippingMethodList);
                            }
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            mShippingMethodDialog.dismiss();
                            SnackbarUtils.displaySnackbar(mViewPager, message);
                        }
                    });
        }else {
            showPaymentOptions();
        }

    }
    public void showPaymentOptions(){

        final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_view, null);
        final LinearLayout paymentMethodBlock = (LinearLayout) view.findViewById(R.id.custom_fields_block);
        showBottomSheet(false,view);

        mAppConst.getJsonResponseFromUrl(UrlUtil.PAYMENT_OPTION_URL
                + CartData.getStoreInfo(this)+
                (mAppConst.isLoggedOutUser() ? "&productsData=" + mCartPref.getProductArray(mContext):""),
                new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                paymentMethodBlock.setVisibility(View.VISIBLE);
                paymentMethodBlock.setTag("pay");
                mPaymentOptionList = new ArrayList<>();
                createViewForBottomSheet(jsonObject,paymentMethodBlock,mPaymentOptionList);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mPaymentOptionDialog.dismiss();
                Toast.makeText(CartView.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBottomSheet(boolean isNormalDialog,View view){
        if(isNormalDialog) {
            mShippingMethodDialog = new BottomSheetDialog(this);
            mShippingMethodDialog.setContentView(view);
            mShippingMethodDialog.show();
        }else {
            mPaymentOptionDialog = new BottomSheetDialog(this);
            mPaymentOptionDialog.setContentView(view);
            mPaymentOptionDialog.show();
        }
    }



    private View formView;
    public void createViewForBottomSheet(JSONObject jsonObject, final LinearLayout view_block,
                                         ArrayList<String> list){

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.margin_10dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.margin_20dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.margin_10dp));

        JSONArray formArray = jsonObject.optJSONArray("form");
        final JSONObject optionsObject = jsonObject.optJSONObject("options");
        for (int i=0;i< formArray.length()-1;i++){
            JSONObject elementObject = formArray.optJSONObject(i);
            String label = elementObject.optString("label");
            final FrameLayout blankView = new FrameLayout(mContext);
            View divider = new View(mContext);
            divider.setLayoutParams(layoutParams);
            divider.setMinimumHeight(1);
            divider.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp));
            divider.setBackgroundColor(Color.LTGRAY);
            RadioGroup radioGroup = new RadioGroup(mContext);
            radioGroup.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp));
            radioGroup.setTag(elementObject.optString("name"));
            list.add(elementObject.optString("name"));
            AppCompatTextView storeLabel =  new AppCompatTextView(mContext);
            storeLabel.setPadding(0,
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp),0,
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp));
            storeLabel.setLayoutParams(layoutParams);
            storeLabel.setTypeface(storeLabel.getTypeface(), Typeface.BOLD);
            storeLabel.setTextColor(Color.BLACK);
            storeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.view_description_margin_top));
            storeLabel.setText(label);

            String shippingInformation = elementObject.optString("shippingInformation");
            AppCompatTextView storeDetails =  new AppCompatTextView(mContext);
            storeLabel.setPadding(0,
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_20dp),0,
                    mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp));
            storeDetails.setLayoutParams(layoutParams);
            storeDetails.setTextColor(Color.GRAY);
            storeDetails.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mContext.getResources().getDimension(R.dimen.body_default_font_size));

            JSONObject multiOptions = elementObject.optJSONObject("multiOptions");
            Iterator<?> keys = multiOptions.keys();
            while( keys.hasNext() ) {
                AppCompatRadioButton radioButton = new AppCompatRadioButton(mContext);
                String key = (String) keys.next();
                radioButton.setText(multiOptions.optString(key));
                radioButton.setTag(key);
                radioButton.setChecked(false);
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.body_default_font_size));
                radioGroup.addView(radioButton);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked ){
                            if(optionsObject != null && optionsObject.optJSONArray(compoundButton.getTag().toString()) != null){
                                JSONObject jsonObjectNew = new JSONObject();
                                try {
                                    jsonObjectNew.put("form", optionsObject.optJSONArray(compoundButton.getTag().toString()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                formView = generateForm(jsonObjectNew, true, "sitestore_cart");
                                blankView.addView(formView);

                            }else if(formView != null) {
                                formView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
                radioButton.setGravity(GravityCompat.START| Gravity.CENTER_VERTICAL);
            }
            view_block.addView(storeLabel);
            view_block.addView(radioGroup);
            if(shippingInformation != null && !shippingInformation.isEmpty()){
                //shippingInformation = shippingInformation.replaceAll("USD",getCurrencySymbol("USD"));
                storeDetails.setText(Html.fromHtml(shippingInformation));
                view_block.addView(storeDetails);
            }
            if(i != formArray.length()- 2) {
                view_block.addView(divider);
            }
            view_block.addView(blankView);

        }

        AppCompatTextView continueButton =  new AppCompatTextView(mContext);
        continueButton.setBackgroundColor(ContextCompat.getColor(mContext,R.color.themeButtonColor));
        continueButton.setLayoutParams(view_block.getLayoutParams());
        continueButton.setHeight(mContext.getResources().getDimensionPixelSize(R.dimen.home_icon_tab_height));
        continueButton.setGravity(Gravity.CENTER);
        continueButton.setTypeface(continueButton.getTypeface(), Typeface.BOLD);
        continueButton.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        continueButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mContext.getResources().getDimension(R.dimen.body_default_font_size));
        continueButton.setText(getResources().getString(R.string.continue_to_order_label));
        continueButton.setTag(view_block);
        continueButton.setOnClickListener(this);

        view_block.addView(continueButton);

    }

    public String getCurrencySymbol(String currencyCode) {
        Currency currency = Currency.getInstance(currencyCode);
        // Note we don't supply a locale to this method - uses default locale to format the currency symbol.
        return currency.getSymbol();
    }

    private Map<String,String> mChequeDetailsMap = null;
    @Override
    public void onClick(View view) {
        boolean isError = false;
        switch (view.getId()) {
            case R.id.update_cart:
                if(!mAppConst.isLoggedOutUser()){
                    if(!isUpdatingCart) {
                        updateCart();
                    }
                }else if(cartFragmentInstance != null) {
                    for(int i = 0 ; i < mProductInfoList.size();i++) {
                        if(mProductInfoList.get(i).getProductConfigurationsKeys() != null) {
                            mCartPref.updateProduct(mContext,
                                    mProductInfoList.get(i).getProductQty(),
                                    mProductInfoList.get(i).getProductConfigurationsKeys());
                        }else {
                            mCartPref.updateProduct(mContext, mProductInfoList.get(i).getProductId(),
                                    mProductInfoList.get(i).getProductQty());
                        }
                    }
                    cartFragmentInstance.onCartUpdate();
                }


                break;
            default:
                View parent = (View) view.getTag();
                if (parent.getTag().equals("ship")) {
                    for (int i = 0; i < mShippingMethodList.size(); i++) {
                        RadioGroup radioGroup = (RadioGroup) parent.findViewWithTag(mShippingMethodList.get(i));
                        int checkedId = radioGroup.getCheckedRadioButtonId();
                        if (checkedId != -1) {
                            String value = radioGroup.findViewById(checkedId).getTag().toString();
                            CartData.updateShippingMethodInfo(this,mShippingMethodList.get(i),value);
                        } else {
                            Toast.makeText(CartView.this,getResources().getString(R.string.option_selection_warning),
                                    Toast.LENGTH_SHORT).show();
                            isError = true;
                            break;
                        }
                    }
                    if (!isError) {
                        mAppConst.showProgressDialog();
                        mAppConst.postJsonResponseForUrl(mAppConst.isLoggedOutUser() ? UrlUtil.SHIPPING_METHOD_URL
                                        + CartData.getStoreInfo(mContext) +
                                CartData.getShippingMethodInfo(mContext)+ "&productsData=" + mCartPref.getProductArray(mContext)
                                        : UrlUtil.SHIPPING_METHOD_URL + CartData.getStoreInfo(mContext)
                                + CartData.getShippingMethodInfo(mContext),null,
                                new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                                        mAppConst.hideProgressDialog();
                                        showPaymentOptions();
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                        mAppConst.hideProgressDialog();
                                        mShippingMethodDialog.dismiss();
                                        SnackbarUtils.displaySnackbar(mViewPager, message);
                                    }
                                });

                    }
                } else {
                    isError = false;
                    for (int i = 0; i < mPaymentOptionList.size(); i++) {
                        RadioGroup radioGroup = (RadioGroup) parent.findViewWithTag(mPaymentOptionList.get(i));
                        int checkedId = radioGroup.getCheckedRadioButtonId();
                        if (checkedId != -1) {
                            if(formView != null && formView.getVisibility() == View.VISIBLE){
                                mChequeDetailsMap = save();
                                if(mChequeDetailsMap == null){
                                    isError = true;
                                }
                            }else {
                                mChequeDetailsMap = null;
                            }
                            String value = radioGroup.findViewById(checkedId).getTag().toString();
                            CartData.updatePaymentGateway(this,value);
                        } else {
                            Toast.makeText(CartView.this,getResources().getString(R.string.option_selection_warning),
                                    Toast.LENGTH_SHORT).show();
                            isError = true;
                            break;
                        }
                    }
                    if (!isError) {
                        mAppConst.showProgressDialog();
                        mAppConst.postJsonResponseForUrl(mAppConst.isLoggedOutUser() ? UrlUtil.PAYMENT_OPTION_URL
                                        + CartData.getStoreInfo(this)+"&productsData=" + mCartPref.getProductArray(mContext)+
                                        "&payment_gateway=" + CartData.getPaymentGatewayInfo(mContext):
                                        UrlUtil.PAYMENT_OPTION_URL + CartData.getStoreInfo(this)
                                                +"&payment_gateway=" + CartData.getPaymentGatewayInfo(mContext),
                                mChequeDetailsMap, new OnResponseListener() {
                                    @Override
                                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                                        mAppConst.hideProgressDialog();
                                        loadOrderReviewPage();
                                    }

                                    @Override
                                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                                        mAppConst.hideProgressDialog();
                                        mPaymentOptionDialog.dismiss();
                                        SnackbarUtils.displaySnackbar(mViewPager, message);
                                    }
                                });

                    }

                }
        }
    }

    private boolean isUpdatingCart = false;
    public void updateCart(){
        isUpdatingCart = true;
        mUpdateCartButton.setText(getResources().getString(R.string.updating_cart));
        String pIds = "";
        String pQtys = "";
        postParams = new HashMap<>();
        for(int i = 0 ; i < mProductInfoList.size();i++){
            if(i < mProductInfoList.size() - 1 ){
                pIds += mProductInfoList.get(i).getProductId() + ",";
                pQtys += mProductInfoList.get(i).getProductQty() + ",";
            }else {
                pIds += mProductInfoList.get(i).getProductId();
                pQtys += mProductInfoList.get(i).getProductQty();
            }
        }
        postParams.put("cartproduct_id",pIds);
        postParams.put("quantity",pQtys);
        mAppConst.postJsonResponseForUrl(UrlUtil.UPDATE_CART_URL, postParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                isUpdatingCart = false;
                mUpdateCartButton.setText(getResources().getString(R.string.update_cart));
                for(int i = 0; i< mProductInfoList.size();i++){
                    mProductInfoList.get(i).setErrorMsg(null);
                }
                mProductListingAdapter.notifyDataSetChanged();
                refreshCart(true);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                isUpdatingCart = false;
                mUpdateCartButton.setText(getResources().getString(R.string.update_cart));
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    jsonObject.keys();
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext() ){
                        String key = iterator.next();
                        for(int i = 0; i< mProductInfoList.size();i++){
                            if(key.equals(String.valueOf(mProductInfoList.get(i).getProductId()))){
                                mProductInfoList.get(i).setErrorMsg(jsonObject.optString(key));
                            }
                        }
                        mProductListingAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Toast.makeText(CartView.this,message,
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });


    }

    public void loadOrderReviewPage(){
        if(mShippingMethodDialog != null) {
            mShippingMethodDialog.dismiss();
        }
        mPaymentOptionDialog.dismiss();
        mViewPager.setCurrentItem(2);

    }
    @Override
    public void onBackPressed() {
        if(behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else if(mViewPager.getCurrentItem() != 0) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }else {
            CartData.clearCartData(mContext);
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onItemViewListener(Fragment reviewFragment, String storeTitle, JSONArray productArray,
                                   String defaultCurrency) {
        reviewFragmentInstance = (OrderReviewFragment) reviewFragment;
        mUpdateCartButton.setVisibility(View.GONE);
        mStoreTitle.setText(storeTitle);
        mProductInfoList.clear();
        mProductListView.setAdapter(mProductListingAdapter);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        for(int i=0;i < productArray.length();i++){
            JSONObject productObject =  productArray.optJSONObject(i);
            mProductInfoList.add(new ProductInfoModel(productObject.optString("title"),
                    productObject.optString("image_profile"),productObject.optInt("cartproduct_id"),
                    productObject.optInt("quantity"),productObject.optDouble("price"),
                    productObject.optDouble("unitPrice"), productObject.optString("error"),
                    productObject.optJSONObject("configuration"),defaultCurrency,false));

        }
        mProductListingAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPayNowClicked() {
        mAppConst.showProgressDialog();
        String orderInfoUrl = UrlUtil.VALIDATE_ORDER_URL
                + "?store_id="+ CartData.getStoreInfo(mContext)
                + "&billingAddress_id="+ CartData.getBillingAddressId(mContext)
                + "&shippingAddress_id="+ CartData.getShippingAddressId(mContext)
                + "&payment_gateway=" + CartData.getPaymentGatewayInfo(mContext)
                + CartData.getShippingMethodInfo(mContext) + CartData.getCouponCodeParams(mContext)
                + "&is_private_order="+ CartData.getOrderDetails(mContext);
        if(mAppConst.isLoggedOutUser()){
            orderInfoUrl+="&productsData=" + mCartPref.getProductArray(mContext);
        }
        mAppConst.postJsonResponseForUrl(orderInfoUrl, mChequeDetailsMap, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mAppConst.hideProgressDialog();
                LogUtils.LOGD("CartView","Place order response - "+jsonObject.toString());
                if(jsonObject.optInt("status_code") == 204){
                    mViewPager.setCurrentItem(0);
                    SnackbarUtils.displaySnackbar(mViewPager, getResources().getString(R.string.order_success_msg));
                    refreshCart(false);
                }else if(jsonObject.has("productids")){
                    JSONArray jsonArray = jsonObject.optJSONArray("productids");
                    for(int i=0;i<jsonArray.length();i++){
                        LogUtils.LOGD("CartView","Product Id - "+ jsonArray.optInt(i));
                        mCartPref.removeProduct(CartView.this,jsonArray.optInt(i));
                    }
                    mViewPager.setCurrentItem(0);
                    refreshCart(false);
                }

                if(jsonObject.optString("payment_url") != null && !jsonObject.isNull("payment_url")){
                    Intent intent = new Intent(CartView.this, WebViewActivity.class);
                    intent.putExtra("url",jsonObject.optString("payment_url"));
                    intent.putExtra("cartorder",true);
                    startActivityForResult(intent, ConstantVariables.WEB_VIEW_ACTIVITY_CODE);
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbar(mViewPager, message);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ConstantVariables.WEB_VIEW_ACTIVITY_CODE) {
            mViewPager.setCurrentItem(0);
            refreshCart(false);
            if(resultCode == RESULT_CANCELED){
                SnackbarUtils.displaySnackbar(mViewPager, getResources().getString(R.string.payment_cancel_msg));
            }else {
                SnackbarUtils.displaySnackbar(mViewPager, getResources().getString(R.string.order_success_msg));
            }
        }
    }

    @Override
    public void onItemClick(View view, ProductInfoModel productInfoModel) {
        if(!mAppConst.isLoggedOutUser()) {
            mAppConst.deleteResponseForUrl(UrlUtil.DELETE_PRODUCT_URL + productInfoModel.getProductId(),
                    null, new OnResponseListener() {
                        @Override
                        public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                            refreshCart(true);
                        }

                        @Override
                        public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                            Toast.makeText(CartView.this, message,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
            if(productInfoModel.getProductConfigurationsKeys() != null) {
                mCartPref.removeProduct(mContext,productInfoModel.getProductConfigurationsKeys());
            }else {
                mCartPref.removeProduct(mContext,productInfoModel.getProductId());
            }
            refreshCart(true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(getSupportActionBar() != null)
        getSupportActionBar().setTitle(mPagerAdapter.getPageTitle(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void refreshCart(boolean displayMsg){
        if(cartFragmentInstance != null){
            cartFragmentInstance.onCartUpdate();
        }else {
            cartFragmentInstance = (CartFragment) mPagerAdapter.getItem(0);
            cartFragmentInstance.onCartUpdate();
        }
        if(displayMsg){
            Toast.makeText(CartView.this, mContext.getResources().getString(R.string.cart_update_msg),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
