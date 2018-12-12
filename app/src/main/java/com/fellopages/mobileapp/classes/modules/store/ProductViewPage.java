package com.fellopages.mobileapp.classes.modules.store;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.CreateNewEntry;
import com.fellopages.mobileapp.classes.common.activities.FragmentLoadActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnOptionItemClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.BadgeView;
import com.fellopages.mobileapp.classes.common.ui.scrollview.ObservableScrollView;
import com.fellopages.mobileapp.classes.common.ui.scrollview.ObservableScrollViewCallbacks;
import com.fellopages.mobileapp.classes.common.ui.scrollview.ScrollState;
import com.fellopages.mobileapp.classes.common.ui.scrollview.ScrollUtils;
import com.fellopages.mobileapp.classes.common.utils.BrowseListItems;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.SocialShareUtil;
import com.fellopages.mobileapp.classes.common.utils.SoundUtil;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.modules.store.adapters.ProductPagerAdapter;
import com.fellopages.mobileapp.classes.modules.store.adapters.ProductViewAdapter;
import com.fellopages.mobileapp.classes.modules.store.adapters.SimpleSheetAdapter;
import com.fellopages.mobileapp.classes.modules.store.ui.CircleIndicator;
import com.fellopages.mobileapp.classes.modules.store.utils.CartPreferences;
import com.fellopages.mobileapp.classes.modules.store.utils.Product;
import com.fellopages.mobileapp.classes.modules.store.utils.ProductInfoModel;
import com.fellopages.mobileapp.classes.modules.store.utils.SheetItemModel;
import com.fellopages.mobileapp.classes.modules.store.utils.StoreUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductViewPage extends AppCompatActivity implements ObservableScrollViewCallbacks,
        OnItemClickListener, View.OnClickListener, TabLayout.OnTabSelectedListener, OnOptionItemClickResponseListener{

    private AppConstant mAppConst;
    private SocialShareUtil mSocialShareUtil;
    private HashMap<String,String> postParams;
    private Toolbar mToolbar;
    private ProgressBar mProgressBar;
    private ViewPager mProductViewPager;
    private BadgeView mCartCountBadge;
    private List<ProductInfoModel> mProductImageList;
    private List<Object> mProductList;
    private JSONObject mBody, mProductInfoObject, mShippingMethodsObject, mConfigObject,mRelatedProductObject;
    private JSONObject mProfileFieldInfo;
    private JSONArray mGutterMenusArray, mProfileTabs,mProductImageArray;
    private JSONArray mDependentFieldArray,mIndependentFieldArray,mRelatedProducts;
    private String mProductViewPageUrl,mDefaultCurrencyCode, mLikeUnlikeUrl;
    private String mMenuFieldName,mMenuFieldValue, mProductUrl, mProductType;
    private TextView mProductTitleView, mProductDefaultPrice, mProductPrice,mDiscountView;
    private TextView mShippingDetails,mDescriptionView,mProductInfoLabel;
    private TextView mProductTypeText;
    private TextView mRatingCount, mLikeCountView, tvMessage, tvWishList;
    private RatingBar mRatingView;
    private WebView mDescriptionText;
    private ImageView mLikeIcon, mWishListIcon;
    private View bottomPurchaseView;
    private LinearLayout.LayoutParams llParamMessage, llParamWishlist, llParamAddToCart;
    private RecyclerView mProductRecyclerView, mOtherProductRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight,mProductId,mStoreId, mIsLike, mLikeCount;
    private int mDependentFieldCount,mIndependentFieldCount,mTotalFieldCount;
    private double mInitialPrice,mFieldPriceUpdate = 0;
    private TabLayout mTabLayout;
    private CircleIndicator mCircleIndicator;
    private ProductPagerAdapter mPagerAdapter;
    private ProductViewAdapter mProductViewAdapter,mOtherProductViewAdapter;
    private LinearLayout mConfigViewParent,mShippingView,mRelatedProductView;
    private LinearLayout mOtherProductView,mProfileInfoView,mProductInfoBlock, mReviewBlock;
    private BottomSheetDialog mConfigurationDialog;
    private Map<String,View> mDependentConfigFields, mIndependentConfigFields,mRadioButtonFields;
    private Map<String,JSONObject> mDependentFieldsMap;
    private Map<String,Boolean> mMultiCheckBoxValueMap,mMultiCheckBoxValueMapTemp, mRadioButtonValueMap;
    private Map<String,String> mFinalDataMap ;
    private Map<String, JSONObject> mPriceMap;
    private CartPreferences mCartPref;
    private Context mContext;
    private GutterMenuUtils mGutterMenuUtils;
    private CoordinatorLayout mMainContent;
    private BrowseListItems mBrowseList;
    private int canEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view_page);

        postParams = new HashMap<>();
        mAppConst = new AppConstant(this);
        mSocialShareUtil = new SocialShareUtil(this);
        mCartPref = new CartPreferences();
        mProductImageList = new ArrayList<>();
        mProductList = new ArrayList<>();
        mProductId = getIntent().getIntExtra("product_id",0);
        mStoreId = getIntent().getIntExtra("store_id",0);
        mContext = this;
     /* Create Back Button On Action Bar **/
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.blank_string));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, ContextCompat.getColor(this,R.color.colorPrimary)));

        getViews();
        mGutterMenuUtils = new GutterMenuUtils(this);
        mGutterMenuUtils.setOnOptionItemClickResponseListener(this);
        mMainContent = (CoordinatorLayout) findViewById(R.id.main_content);

        mPagerAdapter = new ProductPagerAdapter(this,mProductImageList,this);
        mProductViewPager.setAdapter(mPagerAdapter);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.product_imageview_height);
        mProductViewPageUrl = UrlUtil.PRODUCT_VIEW_URL + mProductId + "?cart=1";

        setListeners();

        makeRequest();
    }

    public void getViews(){

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mProgressBar = (ProgressBar)findViewById(R.id.loadingBar);

        mProductTitleView = (TextView) findViewById(R.id.product_title);
        mProductDefaultPrice = (TextView) findViewById(R.id.default_price);
        mProductPrice = (TextView) findViewById(R.id.product_price);
        mDiscountView = (TextView) findViewById(R.id.product_discount);

        mRatingCount = (TextView) findViewById(R.id.total_rating);
        mRatingView = (RatingBar) findViewById(R.id.smallRatingBar);
        LayerDrawable stars = (LayerDrawable) mRatingView.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.dark_yellow),
                PorterDuff.Mode.SRC_ATOP);
        mRatingView.setIsIndicator(true);

        mLikeCountView = (TextView) findViewById(R.id.like_count);
        mLikeIcon = (ImageView) findViewById(R.id.like_button);
        mWishListIcon = (ImageView) findViewById(R.id.wishlist_icon);

        mShippingDetails = (TextView) findViewById(R.id.shipping_text_view);
        mDescriptionView = (TextView) findViewById(R.id.product_description);
        mProductInfoLabel = (TextView) findViewById(R.id.profile_info_label);

        mDescriptionText = (WebView) findViewById(R.id.view_description);
        mProductViewPager = (ViewPager) findViewById(R.id.product_image_pager);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.circle_indicator);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);

        bottomPurchaseView = findViewById(R.id.purchase_view);
        mConfigViewParent = (LinearLayout) findViewById(R.id.configuration_fields);
        mShippingView = (LinearLayout) findViewById(R.id.shipping_details);
        mProfileInfoView = (LinearLayout) findViewById(R.id.profile_info_fields);
        mProductInfoBlock = (LinearLayout) findViewById(R.id.product_info_block);
        mReviewBlock = (LinearLayout) findViewById(R.id.review_block);

        mRelatedProductView = (LinearLayout)findViewById(R.id.related_product_view);
        mProductRecyclerView = (RecyclerView) findViewById(R.id.related_product);
        mOtherProductRecyclerView = (RecyclerView) findViewById(R.id.other_product);
        mOtherProductView = (LinearLayout) findViewById(R.id.other_product_view);
        mProductTypeText = (TextView) findViewById(R.id.text_product_type);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mProductRecyclerView.setLayoutManager(mLayoutManager);
        mOtherProductRecyclerView.setLayoutManager(new LinearLayoutManager(
                this,LinearLayoutManager.HORIZONTAL,false));

        // Bottom views.
        tvMessage = findViewById(R.id.message);
        tvWishList = findViewById(R.id.wishlist);
        TextView tvAddToCart = findViewById(R.id.add_to_cart);
        View addToCartView = findViewById(R.id.add_to_cart_view);

        Drawable messageDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_message);
        messageDrawable.mutate();
        messageDrawable.setColorFilter(ContextCompat.getColor(mContext, R.color.colorPrimary),
                PorterDuff.Mode.SRC_ATOP);
        tvMessage.setCompoundDrawablesWithIntrinsicBounds(null, messageDrawable, null, null);
        Drawable wishListDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_wishlist);
        tvWishList.setCompoundDrawablesWithIntrinsicBounds(null, wishListDrawable, null, null);
        Drawable cartDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_shopping_cart);
        cartDrawable.mutate();
        cartDrawable.setBounds(0, 0, mContext.getResources().getDimensionPixelSize(R.dimen.padding_16dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_16dp));
        tvAddToCart.setCompoundDrawables(cartDrawable, null, null, null);


        llParamMessage = (LinearLayout.LayoutParams) tvMessage.getLayoutParams();
        llParamWishlist = (LinearLayout.LayoutParams) tvWishList.getLayoutParams();
        llParamAddToCart = (LinearLayout.LayoutParams) addToCartView.getLayoutParams();
        tvMessage.setOnClickListener(this);
        tvWishList.setOnClickListener(this);
        addToCartView.setOnClickListener(this);
    }

    public void setListeners(){

        mShippingDetails.setOnClickListener(this);
        mDescriptionView.setOnClickListener(this);
        mProductInfoLabel.setOnClickListener(this);
        mTabLayout.addOnTabSelectedListener(this);
        mReviewBlock.setOnClickListener(this);

        mLikeIcon.setOnClickListener(this);
        mScrollView.setScrollViewCallbacks(this);
        mWishListIcon.setOnClickListener(this);

    }
    public void makeRequest(){

        // Do not send request if coming from create page
        mAppConst.getJsonResponseFromUrl(mProductViewPageUrl, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mProgressBar.setVisibility(View.GONE);
                mScrollView.setVisibility(View.VISIBLE);
                setViewDetails(jsonObject);

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mProgressBar.setVisibility(View.GONE);
                SnackbarUtils.displaySnackbarLongWithListener(mToolbar, message,
                        new SnackbarUtils.OnSnackbarDismissListener() {
                            @Override
                            public void onSnackbarDismissed() {
                                finish();
                            }
                        });
            }
        });
    }

    private String mProductTitle;
    public void setViewDetails(JSONObject jsonObject){

        mBody = jsonObject;
        mGutterMenusArray = mBody.optJSONArray("menu");
        invalidateOptionsMenu();

        mProductUrl =  mBody.optString("content_url");
        mProductTitle = mBody.optString("title");
        mStoreId = mBody.optInt("store_id");
        mProductTitleView.setText(mProductTitle);
        mProductInfoObject =  mBody.optJSONObject("information");
        mConfigObject = mBody.optJSONObject("config");
        canEdit = mBody.optInt("edit");
        mShippingMethodsObject = mProductInfoObject.optJSONObject("shippingMethods");
        mDescriptionView.setActivated(true);
        /* Setting Body in TextView */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mDescriptionText.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            mDescriptionText.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        WebSettings webSettings = mDescriptionText.getSettings();
        webSettings.setDefaultFontSize(14);
        if(!mProductInfoObject.optString("description").isEmpty()) {
            mDescriptionText.loadDataWithBaseURL("file:///android_asset/",
                    GlobalFunctions.getHtmlData(this, mProductInfoObject.optString("description"), true),
                    "text/html", "utf-8", null);
        }else{
            mDescriptionText.loadDataWithBaseURL("file:///android_asset/",
                    GlobalFunctions.getHtmlData(this, mBody.optString("body"), true), "text/html", "utf-8", null);
        }
        mDefaultCurrencyCode =  mBody.optString("currency");

        mRatingView.setRating(mBody.optInt("rating_avg"));
        mRatingCount.setText(mBody.optString("review_count") +" "+
                getResources().getString(R.string.review_text));

        mLikeCount = mBody.optInt("like_count");
        mLikeCountView.setText(mLikeCount +" "+getResources().
                getQuantityString(R.plurals.profile_page_like, mBody.optInt("like_count")));

        mIsLike = mBody.optInt("is_liked");
        if(mIsLike == 1){
            mLikeIcon.setColorFilter(ContextCompat.getColor(this, R.color.themeButtonColor));
        }else {
            mLikeIcon.setColorFilter(ContextCompat.getColor(this, R.color.grey));
        }
        if(mBody.optInt("canAddtoCart") == 1){
            bottomPurchaseView.setVisibility(View.VISIBLE);
            mScrollView.setPadding(0,0,0,getResources().getDimensionPixelSize(R.dimen.home_icon_tab_height));
        }
        if(mBody.optInt("wishlistPresent") != 0){
            mWishListIcon.setActivated(true);
            tvWishList.setActivated(true);
        } else {
            tvWishList.setActivated(false);
        }

        if(mAppConst.isLoggedOutUser()){
            mWishListIcon.setVisibility(View.GONE);
        }
        mProfileTabs =  mBody.optJSONArray("tabs");
        mTabLayout.removeAllTabs();
        for(int i=0;i<mProfileTabs.length();i++){
            JSONObject tabsObject =  mProfileTabs.optJSONObject(i);
            String tabLabel;
            if(tabsObject.optString("name").equals("overview")){
                tabLabel = getResources().getString(R.string.action_bar_title_info);
            }else if(tabsObject.has("count")){
                tabLabel = tabsObject.optString("label") + " ("+tabsObject.optInt("count")+") ";
            }else {
                tabLabel = tabsObject.optString("label");
            }
            mTabLayout.addTab(mTabLayout.newTab().setText(tabLabel));
        }

        mProductImageArray = mBody.optJSONArray("images");
        mProductImageList.removeAll(mProductImageList);
        if(mProductImageArray != null){
            for(int i = 0;i < mProductImageArray.length(); i++){
                JSONObject imageObject =  mProductImageArray.optJSONObject(i);
                mProductImageList.add(new ProductInfoModel(imageObject.optString("image")));
                mPagerAdapter.notifyDataSetChanged();
            }
        }
        mProductType =  mBody.optString("product_type");
        if(mProductType != null && mProductType.length() > 0){
            setUpProductTypes();
        }
        mProfileFieldInfo = mProductInfoObject.optJSONObject("profileFields");
        if(mProfileFieldInfo != null){
            mProductInfoBlock.setVisibility(View.VISIBLE);
            mProductInfoLabel.setActivated(true);
            getKeyValuePairs(mProfileFieldInfo.keys(),mProfileFieldInfo);
        }
        mRelatedProductObject = mProductInfoObject.optJSONObject("relatedProducts");
        mRelatedProducts = mRelatedProductObject.optJSONArray("products");
        mProductViewAdapter = new ProductViewAdapter(this, mProductList, true, new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ProductInfoModel productInfo = (ProductInfoModel) mProductList.get(position);
                Intent intent = new Intent(ProductViewPage.this, ProductViewPage.class);
                intent.putExtra("store_id", productInfo.getStoreId());
                intent.putExtra("product_id", productInfo.getProductId());
                startActivityForResult(intent,ConstantVariables.VIEW_PAGE_CODE);
            }
        }, null);
        mProductRecyclerView.setAdapter(mProductViewAdapter);
        setUpPriceForProduct();
        setUpConfigurableProduct();
        setUpShippingDetails();
        addProductsToList(mRelatedProducts,mProductViewAdapter,mProductList);
        if(mProductImageList.size() > 1) {
            mCircleIndicator.setVisibility(View.VISIBLE);
            mCircleIndicator.setViewPager(mProductViewPager);
        } else {
            mCircleIndicator.setVisibility(View.GONE);
        }
        mBrowseList = new BrowseListItems(mProductId, mProductTitle,0,null, mProductUrl);

        String messageOwnerUrl = null, messageOwnerTitle = null;
        if(mGutterMenusArray != null && mGutterMenusArray.length() > 0) {
            for(int i = 0;i < mGutterMenusArray.length(); i++){
                JSONObject menuObject =  mGutterMenusArray.optJSONObject(i);
                if (menuObject.optString("name").toLowerCase().equals("messageowner")) {
                    messageOwnerUrl = AppConstant.DEFAULT_URL + menuObject.optString("url");
                    messageOwnerTitle = menuObject.optString("label");
                    break;
                }
            }
        }
        // Used for message.
        mBrowseList.setUserId(mBody.optInt("owner_id"));
        mBrowseList.setUserDisplayName(mBody.optString("owner_title"));
        mBrowseList.setUserProfileImageUrl(mBody.optString("owner_image_normal"));
        mBrowseList.setMessageOwnerUrl(messageOwnerUrl);
        mBrowseList.setMessageOwnerTitle(messageOwnerTitle);

        checkMessageOwnerOption();
    }

    /**
     * Method to check message option is available or not.
     */
    private void checkMessageOwnerOption() {
        if (mBrowseList.getMessageOwnerUrl() != null && !mBrowseList.getMessageOwnerUrl().isEmpty()) {
            tvMessage.setVisibility(View.VISIBLE);
            llParamWishlist.weight = 0.8f;
        } else {
            tvMessage.setVisibility(View.GONE);
            llParamWishlist.weight = 1.6f;
        }
    }

    public void getKeyValuePairs(Iterator<String> iterator, JSONObject jsonObject){
        GridLayout gridLayout = new GridLayout(this);
        gridLayout.setColumnCount(2);
        while (iterator.hasNext()){
            String key = iterator.next();
            if(jsonObject.optString(key)!= null && jsonObject.optJSONObject(key) != null){
                AppCompatTextView textView = new AppCompatTextView(this);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.body_medium_font_size));
                textView.setGravity(GravityCompat.START | Gravity.CENTER_VERTICAL);
                textView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        0, getResources().getDimensionPixelSize(R.dimen.padding_5dp));
                textView.setTextColor(ContextCompat.getColor(this,R.color.black));
                textView.setText(key);
                mProfileInfoView.addView(textView);
                getKeyValuePairs(jsonObject.optJSONObject(key).keys(),jsonObject.optJSONObject(key));
            }else {
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
        AppCompatTextView labelView = new AppCompatTextView(this);
        AppCompatTextView mainText = new AppCompatTextView(this);
        labelView.setText(key + "   :   ");
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.body_default_font_size));
        labelView.setGravity(GravityCompat.START| Gravity.CENTER_VERTICAL);
        labelView.setPadding(getResources().getDimensionPixelSize(R.dimen.offset_distance),
                getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                getResources().getDimensionPixelSize(R.dimen.offset_distance),
                getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));
        mainText.setText(jsonObject.optString(key));
        mainText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimension(R.dimen.body_default_font_size));

        mainText.setLayoutParams(layoutParams);
        mainText.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding),
                getResources().getDimensionPixelSize(R.dimen.offset_distance),
                getResources().getDimensionPixelSize(R.dimen.drawer_item_top_padding));
        gridLayout.addView(labelView);
        gridLayout.addView(mainText);

    }

    List<Object> itemList = null;
    public void setUpProductTypes(){
        JSONArray itemsArray = null;
        switch (mProductType){
            case "grouped":
                itemList = new ArrayList<>();
                mProductTypeText.setText(getResources().getString(R.string.grouped_product_label));
                itemsArray = mBody.optJSONArray("groupedProducts");
                break;
            case "bundled":
                itemList = new ArrayList<>();
                mProductTypeText.setText(getResources().getString(R.string.bundled_product_label));
                itemsArray = mBody.optJSONArray("bundledProducts");
                break;
            case "downloadable":
                if(mAppConst.isLoggedOutUser()){
                    bottomPurchaseView.setVisibility(View.GONE);
                    mScrollView.setPadding(0,0,0,0);
                }
                itemsArray = mBody.optJSONArray("sampleFiles");
                mProductTypeText.setText(getResources().getString(R.string.downloadable_product_label));
                break;
        }
        if(itemsArray != null && itemsArray.length() > 0) {
            mOtherProductView.setVisibility(View.VISIBLE);
            if (!mProductType.equals("downloadable")) {
                mOtherProductViewAdapter = new ProductViewAdapter(this, itemList, true, new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ProductInfoModel productInfo = (ProductInfoModel) itemList.get(position);
                        Intent intent = new Intent(ProductViewPage.this, ProductViewPage.class);
                        intent.putExtra("store_id", productInfo.getStoreId());
                        intent.putExtra("product_id", productInfo.getProductId());
                        startActivityForResult(intent,ConstantVariables.VIEW_PAGE_CODE);
                    }
                }, null);
                mOtherProductRecyclerView.setAdapter(mOtherProductViewAdapter);
                addProductsToList(itemsArray, mOtherProductViewAdapter, itemList);
            } else {
                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject fileObject = itemsArray.optJSONObject(i);
                    View configFieldView = getLayoutInflater().inflate(R.layout.product_config_view, null);
                    configFieldView.findViewById(R.id.configuration_fields).setOnClickListener(this);
                    TextView fieldLabel = (TextView) configFieldView.findViewById(R.id.view_label);
                    TextView fieldValue = (TextView) configFieldView.findViewById(R.id.filed_value);
                    fieldValue.setCompoundDrawables(null, null, null, null);
                    fieldValue.setTextColor(ContextCompat.getColor(this, R.color.themeButtonColor));
                    fieldLabel.setText(fileObject.optString("title"));
                    fieldValue.setTag(fileObject.optString("filepath"));
                    fieldValue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(view.getTag().toString()));
                                startActivityForResult(browserIntent,ConstantVariables.VIEW_PAGE_CODE);
                            }catch (ActivityNotFoundException e){
                                LogUtils.LOGD(LogUtils.makeLogTag(ProductViewPage.class),view.getTag().toString());
                                e.printStackTrace();
                            }
                        }
                    });
                    fieldValue.setText(getResources().getString(R.string.download_button_label));
                    mOtherProductView.addView(configFieldView);

                }
            }
        }
    }
    public void setUpPriceForProduct(){

        if(mProductInfoObject.optJSONObject("price").optInt("discount") != 0) {
            mProductDefaultPrice.setVisibility(View.VISIBLE);
            mDiscountView.setVisibility(View.VISIBLE);
            mProductPrice.setText(GlobalFunctions.getFormattedCurrencyString(
                    mDefaultCurrencyCode,mProductInfoObject.optJSONObject("price").optDouble("discounted_amount")));

            mInitialPrice = mProductInfoObject.optJSONObject("price").optDouble("discounted_amount");
            mProductDefaultPrice.setText(GlobalFunctions.getFormattedCurrencyString(
                    mDefaultCurrencyCode,mProductInfoObject.optJSONObject("price").optDouble("price")));
            mDiscountView.setText(mProductInfoObject.optJSONObject("price").optString("discount_percentage") + "%");
            mProductDefaultPrice.setPaintFlags(mProductDefaultPrice.getPaintFlags() |
                    Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            mProductDefaultPrice.setVisibility(View.GONE);
            mDiscountView.setVisibility(View.GONE);
            mInitialPrice = mProductInfoObject.optJSONObject("price").optDouble("price");
            mProductPrice.setText(GlobalFunctions.getFormattedCurrencyString(
                    mDefaultCurrencyCode,mProductInfoObject.optJSONObject("price").optDouble("price")));
        }
    }

    public void setUpConfigurableProduct(){
        if(mConfigObject != null) {
            mDependentFieldCount = mConfigObject.optInt("dependentFieldsCount");
            mIndependentFieldCount = mConfigObject.optInt("independentFieldsCount");
            mTotalFieldCount = mDependentFieldCount + mIndependentFieldCount;
            if(mTotalFieldCount != 0) {
                mPriceMap = new HashMap<>();
                mFinalDataMap = new HashMap<>();
                mMultiCheckBoxValueMapTemp = new HashMap<>();
                mMultiCheckBoxValueMap = new HashMap<>();
                mRadioButtonValueMap = new HashMap<>();
                mDependentConfigFields = new HashMap<>();
                mConfigViewParent.setVisibility(View.VISIBLE);
                mDependentFieldArray = mConfigObject.optJSONArray("dependentFields");
                if (mDependentFieldArray != null) {
                    mDependentFieldsMap = new HashMap<>();
                    for (int i = 0; i < mDependentFieldArray.length(); i++) {
                        JSONObject fieldObject = mDependentFieldArray.optJSONObject(i);
                        View configFieldView = getLayoutInflater().inflate(R.layout.product_config_view, null);
                        configFieldView.findViewById(R.id.configuration_fields).setOnClickListener(this);
                        TextView fieldLabel = (TextView) configFieldView.findViewById(R.id.view_label);
                        TextView fieldValue = (TextView) configFieldView.findViewById(R.id.filed_value);
                        fieldValue.setTag(fieldObject.optInt("order"));
                        mDependentConfigFields.put(fieldObject.optString("order"), fieldValue);
                        fieldLabel.setText(fieldObject.optString("label"));
                        fieldLabel.setTag(fieldObject.optString("name"));
                        fieldValue.setText(getResources().getString(R.string.please_select_label));
                        mDependentFieldsMap.put(fieldObject.optString("order"),fieldObject);
                        configFieldView.findViewById(R.id.configuration_fields).setTag(fieldObject);
                        mConfigViewParent.addView(configFieldView);

                    }
                }

                mIndependentFieldArray = mConfigObject.optJSONArray("independentFields");
                if (mIndependentFieldArray != null) {
                    mIndependentConfigFields = new HashMap<>();
                    for (int i = 0; i < mIndependentFieldArray.length(); i++) {
                        JSONObject fieldObject = mIndependentFieldArray.optJSONObject(i);
                        View configFieldView = getLayoutInflater().inflate(R.layout.product_config_view, null);
                        configFieldView.findViewById(R.id.configuration_fields).setOnClickListener(this);
                        TextView fieldLabel = (TextView) configFieldView.findViewById(R.id.view_label);
                        TextView fieldValue = (TextView) configFieldView.findViewById(R.id.filed_value);
                        fieldValue.setTag(fieldObject.optString("name"));
                        fieldValue.setText(getResources().getString(R.string.please_select_label));
                        mIndependentConfigFields.put(fieldObject.optString("name"), fieldValue);
                        fieldLabel.setText(fieldObject.optString("label"));
                        configFieldView.findViewById(R.id.configuration_fields).setTag(fieldObject);
                        mConfigViewParent.addView(configFieldView);

                    }
                }
            } else {
                mConfigViewParent.setVisibility(View.GONE);
            }
        }
    }

    public void setUpShippingDetails(){
        if(mShippingMethodsObject != null && mShippingMethodsObject.optInt("totalItemCount") > 0) {
            mShippingDetails.setActivated(true);
            JSONArray methodArray = mShippingMethodsObject.optJSONArray("methods");
            for (int i = 0; i < methodArray.length(); i++) {
                JSONObject methodObject = methodArray.optJSONObject(i);
                JSONObject priceRateObject = methodObject.optJSONObject("price_rate");
                View shippingMethodView = getLayoutInflater().inflate(R.layout.product_shipping_methods, null);
                TextView methodTitle = (TextView) shippingMethodView.findViewById(R.id.item_title);
                TextView methodRegion = (TextView) shippingMethodView.findViewById(R.id.shipping_method_region);
                TextView methodWeight = (TextView) shippingMethodView.findViewById(R.id.shipping_method_weight);
                TextView methodDelivery = (TextView) shippingMethodView.findViewById(R.id.shipping_method_delivery);
                TextView methodDependency = (TextView) shippingMethodView.findViewById(R.id.shipping_method_dependency);
                TextView methodLimit = (TextView) shippingMethodView.findViewById(R.id.shipping_method_limit);
                TextView methodCharge = (TextView) shippingMethodView.findViewById(R.id.shipping_method_charge);
                TextView methodRate = (TextView) shippingMethodView.findViewById(R.id.shipping_method_rate);
                TextView methodCountry = (TextView) shippingMethodView.findViewById(R.id.method_country);
                TextView methodPrice = (TextView) shippingMethodView.findViewById(R.id.shipping_price);
                methodTitle.setText(methodObject.optString("title"));
                methodCountry.setText("   :   " + methodObject.optString("country"));
                methodRegion.setText("   :   " + methodObject.optString("region"));
                methodWeight.setText("   :   " + methodObject.optString("weight_limit"));
                methodDelivery.setText("   :   " + methodObject.optString("delivery_time"));
                methodDependency.setText("   :   " + methodObject.optString("dependency"));
                methodLimit.setText("   :   " + methodObject.optString("limit"));
                methodCharge.setText("   :   "+methodObject.optString("charge_on"));
                methodPrice.setText("   :   " + GlobalFunctions.getFormattedCurrencyString(
                        mDefaultCurrencyCode, methodObject.optDouble("shipping_price")));

                if(priceRateObject != null && priceRateObject.optInt("type") == 0){
                    methodRate.setText("   :   " + GlobalFunctions.getFormattedCurrencyString(
                            mDefaultCurrencyCode, priceRateObject.optDouble("value")));
                }else {
                    methodRate.setText("   :   " + priceRateObject.optDouble("value") + "%");
                }
                mShippingView.addView(shippingMethodView);
            }
        }else {
            findViewById(R.id.shipping_methods_details).setVisibility(View.GONE);
        }
    }


    public void addProductsToList(JSONArray productArray,ProductViewAdapter adapter,List<Object> productList){
        if(productArray != null && productArray.length() > 0 ) {
            mRelatedProductView.setVisibility(View.VISIBLE);
            productList.removeAll(productList);
            for (int i = 0; i < productArray.length(); i++) {
                JSONObject jsonDataObject = productArray.optJSONObject(i);
                int product_id = jsonDataObject.optInt("product_id");
                String title = jsonDataObject.optString("title");
                String image = jsonDataObject.optString("image");
                int store_id = jsonDataObject.optInt("store_id");
                int featured = jsonDataObject.optInt("featured");
                int sponsored = jsonDataObject.optInt("sponsored");
                int newLabel = jsonDataObject.optInt("newlabel");
                double ratingAvg = jsonDataObject.optDouble("rating_avg");
                int user_type = jsonDataObject.optInt("user_type");
                JSONObject priceObject = jsonDataObject.optJSONObject("information").optJSONObject("price");
                double discount_value = priceObject.optDouble("discount_amount");
                double discount_amount = priceObject.optDouble("discounted_amount");
                int discount = priceObject.optInt("discount");
                double price = priceObject.optDouble("price");
                JSONArray menu = priceObject.optJSONArray("menu");
                productList.add(new ProductInfoModel(store_id, product_id, title, image, ratingAvg, price,
                        discount_amount, discount, user_type, discount_value,
                        featured, sponsored, newLabel, mDefaultCurrencyCode, false, false, false, menu));
            }
            adapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = ContextCompat.getColor(this,R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        mProductViewPager.setTranslationY(scrollY / 2);
    }

    Iterator<?> keys;
    public void createFieldListView(final JSONObject jsonObject, final View view, final String name, String label){
        switch (jsonObject.optString("type")){
            case "select":

                //TODO make this functionality common (Remove from CartView Page)
                if(jsonObject.optJSONObject("multiOptions") != null) {
                    List<SheetItemModel> list = new ArrayList<>();
                    Iterator<?> keys = jsonObject.optJSONObject("multiOptions").keys();
                    if(keys != null) {
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject valueObject = jsonObject.optJSONObject("multiOptions").optJSONObject(key);
                            if(valueObject != null) {
                                list.add(new SheetItemModel(valueObject, key));
                            }
                        }
                    }
                    SimpleSheetAdapter adapter = new SimpleSheetAdapter(list);
                    adapter.setOnItemClickListener(new SimpleSheetAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(SheetItemModel item, int position) {
                            mConfigurationDialog.dismiss();
                            updateProductDetails(view,item,name);

                        }
                    });
                    View selectView = getLayoutInflater().inflate(R.layout.fragmen_cart, null);
                    RecyclerView recyclerView = (RecyclerView) selectView.findViewById(R.id.recycler_view);
                    selectView.findViewById(R.id.cart_bottom).setVisibility(View.GONE);
                    recyclerView.getLayoutParams().height = RecyclerView.LayoutParams.WRAP_CONTENT;
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    recyclerView.setAdapter(adapter);
                    showBottomSheetDialog(selectView);
                }else {
                    SnackbarUtils.displaySnackbar(bottomPurchaseView,
                            getResources().getString(R.string.please_select_label) +" "+ label);
                }


                break;
           default:
               //TODO make this functionality common (Remove from CartView Page)
               LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                       LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

               layoutParams.setMargins(getResources().getDimensionPixelSize(R.dimen.margin_20dp),
                       getResources().getDimensionPixelSize(R.dimen.margin_10dp),
                       getResources().getDimensionPixelSize(R.dimen.margin_20dp),
                       getResources().getDimensionPixelSize(R.dimen.margin_10dp));
               View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_view, null);
               final LinearLayout customFieldBlock = (LinearLayout) bottomSheetView.findViewById(R.id.custom_fields_block);
               AppCompatTextView continueButton =  new AppCompatTextView(this);
               AppCompatTextView fieldDescription =  new AppCompatTextView(this);
               fieldDescription.setPadding(0,
                       getResources().getDimensionPixelSize(R.dimen.margin_20dp),0,
                       getResources().getDimensionPixelSize(R.dimen.margin_10dp));
               fieldDescription.setLayoutParams(layoutParams);
               fieldDescription.setTextColor(Color.BLACK);
               fieldDescription.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                       getResources().getDimension(R.dimen.body_default_font_size));
               fieldDescription.setText(jsonObject.optString("description"));
               customFieldBlock.addView(fieldDescription);
               final JSONObject multiOptions = jsonObject.optJSONObject("multiOptions");
               if(multiOptions != null) {
                   keys = multiOptions.keys();
               }
               switch (jsonObject.optString("type")){
                   case "checkbox":
                   case "multi_checkbox":
                       mMultiCheckBoxValueMapTemp.clear();
                       if (jsonObject.optString("type").equals("multi_checkbox")) {
                           for (Map.Entry<String, Boolean> entry : mMultiCheckBoxValueMap.entrySet()) {
                               mMultiCheckBoxValueMapTemp.put(entry.getKey(),entry.getValue());
                           }
                       }

                       while( keys.hasNext() ) {
                           AppCompatCheckBox checkBox = new AppCompatCheckBox(this);
                           String key = (String) keys.next();
                           checkBox.setText(multiOptions.optJSONObject(key).optString("label"));
                           checkBox.setTag(key);
                           if(mMultiCheckBoxValueMap.get(key) != null && mMultiCheckBoxValueMap.get(key)) {
                               checkBox.setChecked(true);
                           }else {
                               checkBox.setChecked(false);
                           }
                           checkBox.setLayoutParams(layoutParams);
                           checkBox.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_20dp),
                                   getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                                   getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                                   getResources().getDimensionPixelSize(R.dimen.padding_10dp));
                           checkBox.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                   getResources().getDimension(R.dimen.body_default_font_size));
                           checkBox.setGravity(GravityCompat.START| Gravity.CENTER);
                           checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                               @Override
                               public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                   mMultiCheckBoxValueMapTemp.put(compoundButton.getTag().toString(),b);
                               }
                           });
                           customFieldBlock.addView(checkBox);
                       }
                       break;
                   case "radio":
                       mRadioButtonFields =new HashMap<>();
                       final RadioGroup radioGroup = new RadioGroup(this);
                       radioGroup.setPadding(getResources().getDimensionPixelSize(R.dimen.padding_20dp),
                               getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                               getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                               getResources().getDimensionPixelSize(R.dimen.padding_20dp));
                       continueButton.setTag(radioGroup);
                       while( keys.hasNext() ) {
                           AppCompatRadioButton radioButton = new AppCompatRadioButton(this);
                           String key = (String) keys.next();
                           radioButton.setText(multiOptions.optJSONObject(key).optString("label"));
                           radioButton.setTag(key);
                           if(mRadioButtonValueMap.get(key) != null && mRadioButtonValueMap.get(key)) {
                               mRadioButtonFields.put(key,radioButton);
                               radioButton.setChecked(true);
                           }
                           radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                               @Override
                               public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                   if(!mRadioButtonValueMap.isEmpty() && !mRadioButtonFields.isEmpty() ){
                                       for (Map.Entry<String,View> entry : mRadioButtonFields.entrySet()) {
                                           RadioButton value = (RadioButton) entry.getValue();
                                           value.setChecked(false);
                                           mRadioButtonFields.clear();
                                       }
                                   }

                               }
                           });
                           radioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                                  getResources().getDimension(R.dimen.body_default_font_size));
                           radioGroup.addView(radioButton);
                           radioButton.setGravity(GravityCompat.START| Gravity.CENTER);

                       }
                       customFieldBlock.addView(radioGroup);
                       break;
                   case "textarea":
                   case "text":
                       AppCompatEditText editText = new AppCompatEditText(this);
                       editText.setSingleLine(jsonObject.optString("type").equals("text"));
                       editText.setGravity(Gravity.START);
                       editText.setTag(jsonObject.optString("name"));
                       continueButton.setTag(editText);
                       customFieldBlock.addView(editText);
                       break;


               }


               continueButton.setBackgroundColor(ContextCompat.getColor(this,R.color.themeButtonColor));
               continueButton.setLayoutParams(customFieldBlock.getLayoutParams());
               continueButton.setHeight(getResources().getDimensionPixelSize(R.dimen.home_icon_tab_height));
               continueButton.setGravity(Gravity.CENTER);
               continueButton.setTypeface(continueButton.getTypeface(), Typeface.BOLD);
               continueButton.setTextColor(Color.WHITE);
               continueButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                       getResources().getDimension(R.dimen.body_default_font_size));
               continueButton.setText(getResources().getString(R.string.action_bar_button_title));
               continueButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View doneButton) {
                       if(doneButton.getTag() instanceof RadioGroup){
                           RadioGroup radioG = (RadioGroup) doneButton.getTag();
                           RadioButton radioB = (RadioButton) radioG.findViewById(radioG.getCheckedRadioButtonId());
                           ((TextView)view.findViewById(R.id.filed_value)).
                                   setText(radioB.getText());
                           ((TextView)view.findViewById(R.id.filed_value))
                                   .setTextColor(ContextCompat.getColor(ProductViewPage.this,R.color.grey));
                           mRadioButtonValueMap.clear();
                           mFinalDataMap.put(view.findViewById(R.id.filed_value).getTag().toString(),
                                   radioB.getTag().toString());
                           mRadioButtonValueMap.put(radioB.getTag().toString(),true);
                           JSONObject keyObject = jsonObject.optJSONObject("multiOptions").
                                   optJSONObject(radioB.getTag().toString());
                           mPriceMap.put("radioB",keyObject);
                           updatePrice();
                           mConfigurationDialog.dismiss();
                       }else if(doneButton.getTag() instanceof AppCompatEditText){
                           AppCompatEditText text = (AppCompatEditText) doneButton.getTag();
                           ((TextView)view.findViewById(R.id.filed_value)).
                                   setText(text.getText());
                           ((TextView)view.findViewById(R.id.filed_value))
                                   .setTextColor(ContextCompat
                                           .getColor(ProductViewPage.this,R.color.grey));
                           mFinalDataMap.put(text.getTag().toString(), text.getText().toString());
                           mConfigurationDialog.dismiss();
                       }else {
                           if(mMultiCheckBoxValueMapTemp.size() > 0) {
                               String string = "";
                               String value = "";
                               for (Map.Entry<String, Boolean> entry : mMultiCheckBoxValueMapTemp.entrySet()) {
                                   mMultiCheckBoxValueMap.put(entry.getKey(),entry.getValue());
                                   if(entry.getValue()){
                                       AppCompatCheckBox checkBox= (AppCompatCheckBox)
                                               customFieldBlock.findViewWithTag(entry.getKey());
                                       if(value.isEmpty()) {
                                           value += entry.getKey();
                                       }else {
                                           value += ","+ entry.getKey();
                                       }
                                       mFinalDataMap.put(view.findViewById(R.id.filed_value).getTag().toString(),
                                               value);
                                       if (checkBox != null) {
                                           JSONObject keyObject = jsonObject.optJSONObject("multiOptions").
                                                   optJSONObject(checkBox.getTag().toString());
                                           mPriceMap.put(entry.getKey(),keyObject);
                                           string += checkBox.getText() + " ";
                                       }
                                   }else {
                                       if (mFinalDataMap.containsKey(jsonObject.optString("name"))) {
                                           mFinalDataMap.remove(jsonObject.optString("name"));
                                       }
                                       mPriceMap.put(entry.getKey(),null);
                                       updatePrice();
                                   }
                               }
                               updatePrice();
                               ((TextView)view.findViewById(R.id.filed_value)).
                                       setText(string);
                               ((TextView)view.findViewById(R.id.filed_value))
                                       .setTextColor(ContextCompat
                                               .getColor(ProductViewPage.this,R.color.grey));
                               mConfigurationDialog.dismiss();
                           }
                       }
                   }
               });

               bottomSheetView.findViewById(R.id.progressBar).setVisibility(View.GONE);
               customFieldBlock.addView(continueButton);
               customFieldBlock.setVisibility(View.VISIBLE);
               showBottomSheetDialog(bottomSheetView);
        }

    }

    public void updateProductDetails(View view, SheetItemModel itemModel,String name){

        mAppConst.showProgressDialog();
        mPriceMap.put(view.findViewById(R.id.filed_value).getTag().toString(),itemModel.getKeyObject());
        ((TextView)view.findViewById(R.id.filed_value)).
                setText(itemModel.getKeyObject().optString("label"));
        ((TextView)view.findViewById(R.id.filed_value))
                .setTextColor(ContextCompat.getColor(this,R.color.grey));
        mFinalDataMap.put(view.findViewById(R.id.view_label).getTag().toString(),itemModel.getKey());

        Set<String> set = mDependentConfigFields.keySet();
        for (String key : set) {
            if(Integer.parseInt(key) > (Integer)view.findViewById(R.id.filed_value).getTag()){
                TextView fieldView = (TextView) mDependentConfigFields.get(key);
                mDependentFieldsMap.put(key,null);
                mPriceMap.put(key,null);
                fieldView.setText(getResources().getString(R.string.please_select_label));;
            }
        }

        updatePrice();

        String url = UrlUtil.PRODUCT_VARIATIONS_URL + name+"="+itemModel.getKey()+
                "&product_id="+mProductId;


        mAppConst.getJsonResponseFromUrl(url, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mAppConst.hideProgressDialog();
                if(jsonObject.optJSONObject("field") != null && jsonObject.optJSONObject("field").length() > 0) {
                    mDependentFieldsMap.put(jsonObject.optJSONObject("field").optString("order"),
                            jsonObject.optJSONObject("field"));
                }else {
                    int quantityAva = jsonObject.optInt("quantity_available");
                    int status =  jsonObject.optInt("status");
                    LogUtils.LOGD("ProductView","Qty -" + quantityAva + " "+ "Status - "+status);
                    mFinalDataMap.put("combination_id",jsonObject.optString("combination_id"));
                }
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbar(bottomPurchaseView, message);
            }
        });

    }

    public void updatePrice(){
        mFieldPriceUpdate = 0;
        Set<String> priceSet = mPriceMap.keySet();
        for (String key : priceSet) {
            JSONObject jsonObject = mPriceMap.get(key);
            if(jsonObject != null && jsonObject.length() > 0){
                if(jsonObject.optBoolean("price_increment")){
                    mFieldPriceUpdate += jsonObject.optDouble("price");
                }else {
                    mFieldPriceUpdate -= jsonObject.optDouble("price");
                }
            }
        }
        mProductPrice.setText(GlobalFunctions.getFormattedCurrencyString(
                mDefaultCurrencyCode,mInitialPrice+mFieldPriceUpdate));
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if(tab.getPosition() != 0){
            redirectTabs(tab.getPosition());
        }
    }

    public void showBottomSheetDialog(View view){

        mConfigurationDialog = new BottomSheetDialog(this);
        mConfigurationDialog.setContentView(view);
        mConfigurationDialog.show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shipping_text_view:
                if(mShippingDetails.isActivated()){
                    mShippingDetails.setActivated(false);
                    mShippingView.setVisibility(View.VISIBLE);
                }else {
                    mShippingDetails.setActivated(true);
                    mShippingView.setVisibility(View.GONE);
                }
                break;
            case R.id.product_description:
                if(mDescriptionView.isActivated()){
                    mDescriptionView.setActivated(false);
                    mDescriptionText.setVisibility(View.VISIBLE);
                }else {
                    mDescriptionView.setActivated(true);
                    mDescriptionText.setVisibility(View.GONE);
                }
                break;
            case R.id.profile_info_label:
                if(mProductInfoLabel.isActivated()){
                    mProductInfoLabel.setActivated(false);
                    mProfileInfoView.setVisibility(View.VISIBLE);
                }else {
                    mProductInfoLabel.setActivated(true);
                    mProfileInfoView.setVisibility(View.GONE);
                }
                break;
            case R.id.add_to_cart_view:
                if(mTotalFieldCount != 0){
                    validateConfigFields();
                }else {
                    addProductToCart();
                }
                break;
            case R.id.configuration_fields:
                try {
                    if(view.getTag() != null) {
                        JSONObject jsonObject = new JSONObject(view.getTag().toString());
                        if(jsonObject.isNull("order")) {
                            createFieldListView(jsonObject, view,jsonObject.optString("name"),jsonObject.optString("label"));
                        }else if(mDependentFieldsMap.get(jsonObject.optString("order")) != null) {
                            createFieldListView(mDependentFieldsMap.get(jsonObject.optString("order")),
                                    view,jsonObject.optString("name"),jsonObject.optString("label"));
                        }else {
                            SnackbarUtils.displaySnackbar(bottomPurchaseView,
                                    getResources().getString(R.string.please_select_label)
                                            + " "+jsonObject.optString("label"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.like_button:
                if(!mAppConst.isLoggedOutUser()) {
                    likeButtonAction();
                }
                break;

            case R.id.wishlist_icon:
            case R.id.wishlist:
                Intent intent = new Intent(this, CreateNewEntry.class);
                intent.putExtra(ConstantVariables.CREATE_URL, UrlUtil.ADD_TO_WISHLIST_STORE
                        +mProductId);
                intent.putExtra(ConstantVariables.FORM_TYPE, "add_wishlist");
                intent.putExtra(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.PRODUCT_MENU_TITLE);
                startActivityForResult(intent, ConstantVariables.WISHLIST_CREATE_CODE);
                break;

            case R.id.review_block:
                if(mBody.optInt("review_count") != 0) {
                    Intent newIntent;
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantVariables.CONTENT_TITLE, mProductTitle);
                    bundle.putInt(ConstantVariables.TOTAL_ITEM_COUNT, mBody.optInt("review_count"));
                    newIntent = StoreUtil.getUserReviewPageIntent(this,
                            mProductId, UrlUtil.PRODUCT_BROWSE_REVIEW_URL + "/" + mProductId, bundle);
                    newIntent.putExtras(bundle);
                    startActivityForResult(newIntent, ConstantVariables.VIEW_PAGE_CODE);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;

            case R.id.message:
                GlobalFunctions.messageOwner(mContext, ConstantVariables.PRODUCT_MENU_TITLE,
                        mBrowseList);
                break;

        }
    }

    public void validateConfigFields(){
        boolean isError = false;
        if(mDependentConfigFields != null) {
            Set<String> set = mDependentConfigFields.keySet();
            for (String key : set) {
                TextView fieldView = (TextView) mDependentConfigFields.get(key);
                if (fieldView.getText().length() > 0
                        && !fieldView.getText().equals(getResources().getString(R.string.field_blank_msg))
                        && !fieldView.getText().equals(getResources().getString(R.string.please_select_label))) {
                    LogUtils.LOGD("ProductView", "Config Key :- " + key + "  Config Value :- " + fieldView.getText());
                } else {
                    isError = true;
                    fieldView.requestFocus();
                    fieldView.setText(getResources().getString(R.string.field_blank_msg));
                    fieldView.setTextColor(ContextCompat.getColor(this, R.color.red));
                }
            }
        }
        if(mIndependentConfigFields != null){
            Set<String> set = mIndependentConfigFields.keySet();
            for (String key : set) {
                TextView fieldView = (TextView) mIndependentConfigFields.get(key);
                if (fieldView.getText().length() > 0
                        && !fieldView.getText().equals(getResources().getString(R.string.field_blank_msg))) {
                    LogUtils.LOGD("ProductView", "Config Key :- " + key + "  Config Value :- " + fieldView.getText());
                } else {
                    isError = true;
                    fieldView.requestFocus();
                    fieldView.setText(getResources().getString(R.string.field_blank_msg));
                    fieldView.setTextColor(ContextCompat.getColor(this, R.color.red));
                }
            }
        }
       if(!isError){
           postParams.clear();
           for (Map.Entry<String, String> entry : mFinalDataMap.entrySet()) {
               LogUtils.LOGD("ProductView", "Config Key :- " + entry.getKey() + "  Config Value :- " + entry.getValue());
           }
           JSONObject mapObject = new JSONObject(mFinalDataMap);
           postParams.put("product_config", mapObject.toString());
           addProductToCart();
       }
    }
    JsonObject fieldObject ;
    public void addProductToCart(){
        mAppConst.showProgressDialog();
        mAppConst.postJsonResponseForUrl(UrlUtil.ADD_TO_CART_URL + mStoreId + "/" + mProductId,
                postParams, new OnResponseListener() {
                    @Override
                    public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                        // Get instance of Vibrator from current Context
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 100 milliseconds
                        v.vibrate(200);
                        if(mAppConst.isLoggedOutUser()) {
                            if (mFinalDataMap != null) {
                                JsonParser parser = new JsonParser();
                                fieldObject = parser.parse(mFinalDataMap.toString()).getAsJsonObject();
                                mCartPref.addProduct(ProductViewPage.this, new Product(mProductId, 1,
                                        fieldObject), true);
                            } else {
                                if(mProductType != null && mProductType.equals("grouped")){
                                    for(int i=0;i<itemList.size();i++){
                                        mCartPref.addProduct(ProductViewPage.this, new Product(
                                                ((ProductInfoModel)itemList.get(i)).getProductId(), 1,
                                                null), false);
                                    }
                                }else {
                                    mCartPref.addProduct(ProductViewPage.this, new Product(mProductId, 1,
                                            null), false);
                                }
                            }
                        }
                        mAppConst.hideProgressDialog();
                        if (mCartCountBadge != null) {
                            mCartCountBadge.setVisibility(View.VISIBLE);
                            if(!PreferencesUtils.getNotificationsCounts(
                                    ProductViewPage.this, PreferencesUtils.CART_COUNT).equals("") &&
                                    !PreferencesUtils.getNotificationsCounts(
                                    ProductViewPage.this, PreferencesUtils.CART_COUNT).equals("null")) {
                                int cartCount = Integer.parseInt(PreferencesUtils.getNotificationsCounts(
                                        ProductViewPage.this, PreferencesUtils.CART_COUNT));
                                if(mProductType != null && mProductType.equals("grouped")) {
                                    mCartCountBadge.setText(String.valueOf(cartCount + itemList.size()));
                                    PreferencesUtils.updateCartCount(ProductViewPage.this,
                                            String.valueOf(cartCount + itemList.size()));
                                }else {
                                    mCartCountBadge.setText(String.valueOf(cartCount + 1));
                                    PreferencesUtils.updateCartCount(ProductViewPage.this, String.valueOf(cartCount + 1));
                                }
                            }else {
                                if(mProductType != null && mProductType.equals("grouped")) {
                                    mCartCountBadge.setText(String.valueOf(itemList.size()));
                                    PreferencesUtils.updateCartCount(ProductViewPage.this, String.valueOf(itemList.size()));
                                }else {
                                    mCartCountBadge.setText("1");
                                    PreferencesUtils.updateCartCount(ProductViewPage.this, "1");
                                }
                            }
                        }
                    }

                    @Override
                    public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                        mAppConst.hideProgressDialog();
                        SnackbarUtils.displaySnackbar(bottomPurchaseView, message);
                    }
                });

    }

    public void likeButtonAction(){

        mAppConst.showProgressDialog();
        Map<String, String> likeParams = new HashMap<>();
        likeParams.put(ConstantVariables.SUBJECT_TYPE, "sitestoreproduct_product");
        likeParams.put(ConstantVariables.SUBJECT_ID, String.valueOf(mProductId));

        mLikeUnlikeUrl = AppConstant.DEFAULT_URL + (mIsLike == 0 ? "like":"unlike");

        mAppConst.postJsonResponseForUrl(mLikeUnlikeUrl, likeParams, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) throws JSONException {
                mAppConst.hideProgressDialog();
                if(mIsLike == 0) {
                    mIsLike = 1;
                    mLikeIcon.setColorFilter(ContextCompat.getColor(ProductViewPage.this, R.color.themeButtonColor));
                    mLikeCount+=1;
                    mLikeCountView.setText(mLikeCount +" "+getResources().
                            getQuantityString(R.plurals.profile_page_like, mLikeCount));
                }else {
                    mIsLike = 0;
                    mLikeIcon.setColorFilter(ContextCompat.getColor(ProductViewPage.this, R.color.grey));
                    mLikeCount-=1;
                    mLikeCountView.setText(mLikeCount +" "+getResources().
                            getQuantityString(R.plurals.profile_page_like, mLikeCount));
                }

            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {
                mAppConst.hideProgressDialog();
                SnackbarUtils.displaySnackbar(bottomPurchaseView, message);
            }

        });
    }
    /**
     * Function to define Tabs Redirection
     * @param tabPosition Selected Tab position
     */
    public void redirectTabs(int tabPosition) {

        String redirectUrl = null;
        JSONObject profileTabObject = mProfileTabs.
                optJSONObject(tabPosition);
        String tabName = profileTabObject.optString("name");
        JSONObject urlParams = profileTabObject.optJSONObject("urlParams");
        String url = profileTabObject.optString("url");
        if (url != null) {
            redirectUrl = AppConstant.DEFAULT_URL + url;
            if (urlParams != null && urlParams.length() != 0) {
                JSONArray urlParamsNames = urlParams.names();
                Map<String, String> params = new HashMap<>();
                for (int j = 0; j < urlParams.length(); j++) {
                    String name = urlParamsNames.optString(j);
                    String value = urlParams.optString(name);
                    if(tabName.equals("photos")){
                        redirectUrl += "/" + value;
                    } else{
                        params.put(name, value);
                    }
                }

                if(params.size() != 0){
                    redirectUrl = mAppConst.buildQueryString(redirectUrl, params);
                }
            }
        }

        if (!tabName.equals("update")){
            Intent newIntent = null;
            int totalItemCount = profileTabObject.optInt("count");
            Bundle bundle = new Bundle();
            bundle.putString(ConstantVariables.FRAGMENT_NAME, tabName);
            bundle.putString(ConstantVariables.URL_STRING, redirectUrl);
            bundle.putString(ConstantVariables.CONTENT_TITLE, mProductTitle);
            bundle.putInt(ConstantVariables.TOTAL_ITEM_COUNT, totalItemCount);
            bundle.putString(ConstantVariables.EXTRA_MODULE_TYPE, ConstantVariables.PRODUCT_MENU_TITLE);

            switch (tabName){
                case "video":
                case "videos":
                    String viewPageUrl = AppConstant.DEFAULT_URL + "videogeneral/view?gutter_menu=1"
                            + "&subject_type=sitestoreproduct_video&subject_id=";
                    bundle.putString(ConstantVariables.VIEW_PAGE_URL, viewPageUrl);
                    bundle.putString(ConstantVariables.VIDEO_SUBJECT_TYPE, ConstantVariables.PRODUCT_VIDEO_MENU_TITLE);
                    bundle.putBoolean("isProfilePageRequest", true);
                    bundle.putBoolean("isStoreProductVideos", true);
                case "photos":
                    String photosUploadUrl = AppConstant.DEFAULT_URL + "sitestore/product/photo/upload-photo/"+mProductId;
                    newIntent = new Intent(this, FragmentLoadActivity.class);
                    bundle.putInt(ConstantVariables.CAN_UPLOAD, canEdit);
                    bundle.putBoolean(ConstantVariables.SHOW_OPTIONS,false);
                    bundle.putString(ConstantVariables.PHOTO_REQUEST_URL,photosUploadUrl);
                    break;
                case "review":
                    newIntent = StoreUtil.getUserReviewPageIntent(this, mProductId,redirectUrl,bundle);
                    break;
                case "main_file":
                case "sample_file":
                    newIntent = new Intent(mContext,ManageDownloadableProduct.class);
                    bundle.putString(ConstantVariables.URL_STRING,redirectUrl);
                    bundle.putString(ConstantVariables.TITLE,tabName);
            }

            if(newIntent != null) {
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent,ConstantVariables.VIEW_PAGE_CODE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu_item, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        // Not showing the option menu if the gutter menus null.
        if(mGutterMenusArray != null){
            mGutterMenuUtils.showOptionMenus(menu, mGutterMenusArray, ConstantVariables.PRODUCT_MENU_TITLE, mBrowseList);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mTabLayout != null) {
            TabLayout.Tab tab = mTabLayout.getTabAt(0);
            if (tab != null) {
                tab.select();
            }
        }
        //Updating the cart count
        if(mCartCountBadge != null) {
            if (!PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("0") &&
                    !PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("")
                    && !PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT).equals("null")) {
                mCartCountBadge.setVisibility(View.VISIBLE);
                mCartCountBadge.setText(PreferencesUtils.getNotificationsCounts(this, PreferencesUtils.CART_COUNT));
            } else{
                mCartCountBadge.setVisibility(View.GONE);
            }
        }
        LogUtils.LOGD("ProductView",String.valueOf(requestCode));
        if(requestCode == ConstantVariables.WISHLIST_CREATE_CODE && resultCode == RESULT_OK){
            if(data.getIntExtra("wishlist",0) != 0) {
                mWishListIcon.setActivated(true);
                tvWishList.setActivated(true);
            }else {
                mWishListIcon.setActivated(false);
                tvWishList.setActivated(false);
            }
        }else if(requestCode == ConstantVariables.EDIT_ENTRY_RETURN_CODE){
            makeRequest();
        }
    }
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onItemClick(View view, int position) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            // Playing backSound effect when user tapped on back button from tool bar.
            if (PreferencesUtils.isSoundEffectEnabled(this)) {
                SoundUtil.playSoundEffectOnBackPressed(this);
            }
        } else if(mGutterMenusArray != null) {
            mGutterMenuUtils.onMenuOptionItemSelected(mMainContent, findViewById(item.getItemId()),
                    id, mGutterMenusArray);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionItemActionSuccess(Object itemList, String menuName) {

    }

    @Override
    public void onItemDelete(String successMessage) {
            finish();
            makeRequest();
    }
}
