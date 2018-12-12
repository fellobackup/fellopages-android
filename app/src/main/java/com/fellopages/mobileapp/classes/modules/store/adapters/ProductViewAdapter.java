package com.fellopages.mobileapp.classes.modules.store.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.formgenerator.FormActivity;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnMenuClickResponseListener;
import com.fellopages.mobileapp.classes.common.ui.ProgressBarHolder;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.GutterMenuUtils;
import com.fellopages.mobileapp.classes.common.utils.LogUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.store.utils.ProductInfoModel;

import org.json.JSONArray;

import java.util.List;

public class ProductViewAdapter  extends RecyclerView.Adapter implements OnMenuClickResponseListener {
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;
    public static final int TYPE_FB_AD = 2;
    public static final int TYPE_ADMOB = 5;

    private Context mContext;
    private List<Object> mItemList;
    private ProductInfoModel mProductInfo;
    private OnItemClickListener mOnItemClickListener;
    boolean isHorizontalScrollingEnabled;
    private AppConstant mAppConst;
    private ImageLoader mImageLoader;
    private GutterMenuUtils mGutterMenuUtils;
    private Fragment mCallingFragment;


    public ProductViewAdapter(Context context,List<Object> itemList,boolean isScrollingEnabled,
                              OnItemClickListener onItemClickListener, Fragment callingFragment){
        mItemList = itemList;
        mContext =  context;
        mAppConst = new AppConstant(mContext);
        mOnItemClickListener = onItemClickListener;
        isHorizontalScrollingEnabled = isScrollingEnabled;
        mImageLoader = new ImageLoader(mContext);
        mGutterMenuUtils = new GutterMenuUtils(mContext);
        this.mCallingFragment = callingFragment;

    }
    @Override
    public int getItemViewType(int position) {
        return (mItemList.get(position) != null
                && !mItemList.get(position).equals(ConstantVariables.FOOTER_TYPE)) ? VIEW_ITEM :VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder =  null;
        View itemView;
        switch (viewType) {
            case VIEW_ITEM:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.store_product_view, parent, false);
                if(isHorizontalScrollingEnabled) {
                    itemView.getLayoutParams().width = mContext.getResources().
                            getDimensionPixelSize(R.dimen.recycler_grid_cover_height);
                }
                viewHolder = new ProductViewHolder(itemView);
                mGutterMenuUtils.setOnMenuClickResponseListener(this);
                break;
            case TYPE_FB_AD:
                break;
            case TYPE_ADMOB:
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.progress_item, parent, false);
                viewHolder = new ProgressBarHolder(itemView);
                break;

        }
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_ITEM:

                final ProductViewHolder productViewHolder = (ProductViewHolder) holder;
                productViewHolder.mainView.setTag("mainview");
                productViewHolder.mainView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mOnItemClickListener.onItemClick(view,holder.getAdapterPosition());
                    }
                });
                mProductInfo = (ProductInfoModel)mItemList.get(position);
                productViewHolder.productTitleView.setText(mProductInfo.getProductTitle());
                mImageLoader.setImageUrl(mProductInfo.getProductImage(), productViewHolder.productImageView);
                LayerDrawable stars = (LayerDrawable) productViewHolder.productRatingView.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext, R.color.dark_yellow),
                        PorterDuff.Mode.SRC_ATOP);
                productViewHolder.productRatingView.setRating(((float) mProductInfo.getRatingCount()));
                productViewHolder.productRatingView.setIsIndicator(true);

                if(mProductInfo.isDiscountAvailable() == 1) {
                    productViewHolder.productDiscountedPrice.setVisibility(View.VISIBLE);
                    productViewHolder.productDiscountedPrice.setText(GlobalFunctions.getFormattedCurrencyString(
                            mProductInfo.getCurrency(),mProductInfo.getProductPrice()));
                    productViewHolder.priceView.setText(GlobalFunctions.getFormattedCurrencyString(
                            mProductInfo.getCurrency(),mProductInfo.getDiscountedPrice()));
                    productViewHolder.productDiscountedPrice.setPaintFlags(productViewHolder.productDiscountedPrice.getPaintFlags() |
                            Paint.STRIKE_THRU_TEXT_FLAG);
                }else {
                    productViewHolder.priceView.setText(GlobalFunctions.getFormattedCurrencyString(
                            mProductInfo.getCurrency(),mProductInfo.getProductPrice()));
                    productViewHolder.productDiscountedPrice.setPaintFlags(
                            productViewHolder.productDiscountedPrice.getPaintFlags());
                    productViewHolder.productDiscountLabel.setVisibility(View.GONE);
                    productViewHolder.productDiscountedPrice.setVisibility(View.GONE);
                }

                if(mProductInfo.isNewItem() == 1){
                    //TODO add new item view
                }
                if(mProductInfo.isFeatured() == 1){
                    productViewHolder.featuredView.setVisibility(View.VISIBLE);
                }else {
                    productViewHolder.featuredView.setVisibility(View.GONE);
                }

                if(mProductInfo.isSponsored() == 1){
                    productViewHolder.sponsoredView.setVisibility(View.VISIBLE);
                }else {
                    productViewHolder.sponsoredView.setVisibility(View.GONE);
                }

                if(isHorizontalScrollingEnabled){
                    productViewHolder.wishListIcon.setVisibility(View.GONE);
                }

                if(!mAppConst.isLoggedOutUser()) {
                    if (mProductInfo.isAddedInWishList()) {
                        productViewHolder.wishListIcon.setActivated(true);
                    } else {
                        productViewHolder.wishListIcon.setActivated(false);
                    }
                }else {
                    productViewHolder.wishListIcon.setVisibility(View.GONE);
                }
                productViewHolder.wishListIcon.setTag(productViewHolder);
                productViewHolder.wishListIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(v,holder.getAdapterPosition());
                    }
                });
                productViewHolder.selectProduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(productViewHolder.mainView,holder.getAdapterPosition());
                    }
                });
                if(mProductInfo.isSelectProduct()){
                    productViewHolder.wishListIcon.setVisibility(View.GONE);
                    productViewHolder.selectProduct.setVisibility(View.VISIBLE);
                    productViewHolder.selectProduct.setChecked(mProductInfo.isSelectProductChecked());
                    productViewHolder.selectProduct.setTag(position);
                }
                if (mProductInfo.getOptionsMenu() != null) {
                    productViewHolder.optionIconLayout.setVisibility(View.VISIBLE);
                    productViewHolder.optionMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int itemPosition = productViewHolder.getAdapterPosition();
                            ProductInfoModel productInfo = (ProductInfoModel) mItemList.get(itemPosition);
                            mGutterMenuUtils.showPopup(v, productInfo.getOptionsMenu(),
                                    itemPosition, null, ConstantVariables.PRODUCT_MENU_TITLE, mCallingFragment);
                        }
                    });
                } else {
                    productViewHolder.optionIconLayout.setVisibility(View.GONE);
                }
                break;
            case TYPE_FB_AD:
                break;
            case TYPE_ADMOB:
                break;
            default:
                       /*
                Show Footer ProgressBar on Scrolling
                Show End Of Result Text if there are no more results.
                */
                if(mItemList.get(position) == null){
                    ((ProgressBarHolder) holder).progressBar.setVisibility(View.VISIBLE);
                    ((ProgressBarHolder) holder).progressBar.setIndeterminate(true);
                    ((ProgressBarHolder) holder).mFooterText.setVisibility(View.GONE);
                }else{
                    ((ProgressBarHolder) holder).mFooterText.setVisibility(View.VISIBLE);
                    ((ProgressBarHolder) holder).mFooterText.setText(mContext.getResources().
                            getString(R.string.end_of_results));
                    ((ProgressBarHolder) holder).progressBar.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public void onItemDelete(int position) {

    }

    @Override
    public void onItemActionSuccess(int position, Object itemList, String menuName) {
        LogUtils.LOGD("Data ","onItemActionSuccess");
                mCallingFragment.onActivityResult(0,0, null);
    }

    public void update(List<Object> mProductList) {
        this.mItemList.clear();
        mItemList = mProductList;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        View mainView;
        ImageView productImageView,wishListIcon;
        TextView productTitleView,priceView,productDiscountLabel,productDiscountedPrice;
        RatingBar productRatingView;
        TextView featuredView,sponsoredView;
        CheckBox selectProduct;
        ImageView optionMenu;
        View optionIconLayout;
        JSONArray mOptionsArray;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            productImageView = (ImageView) itemView.findViewById(R.id.product_image);
            productTitleView = (TextView) itemView.findViewById(R.id.product_title);
            priceView = (TextView) itemView.findViewById(R.id.product_price);
            productRatingView = (RatingBar) itemView.findViewById(R.id.smallRatingBar);
            wishListIcon = (ImageView) itemView.findViewById(R.id.wishlist_icon);
            productDiscountLabel =(TextView) itemView.findViewById(R.id.product_discount);
            productDiscountedPrice = (TextView) itemView.findViewById(R.id.product_discounted_price);
            featuredView = (TextView) itemView.findViewById(R.id.featuredLabel);
            sponsoredView = (TextView) itemView.findViewById(R.id.sponsoredLabel);
            selectProduct = (CheckBox) itemView.findViewById(R.id.select_product);
            optionMenu = (ImageView) itemView.findViewById(R.id.optionIcon);
            optionIconLayout =  itemView.findViewById(R.id.option_icon_layout);
        }
    }
}
