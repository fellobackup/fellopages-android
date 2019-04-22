package com.fellopages.mobileapp.classes.modules.store.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.store.utils.ProductInfoModel;


import java.util.Iterator;
import java.util.List;


public class ProductListingAdapter extends RecyclerView.Adapter implements View.OnClickListener {
    private Context mContext;
    private List<ProductInfoModel> mProductList;
    private ProductInfoModel mProductInfo;
    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;

    public interface OnItemClickListener {
        void onItemClick(View view, ProductInfoModel productInfoModel);
    }
    public ProductListingAdapter(Context context, List<ProductInfoModel> productList,
                                 OnItemClickListener onItemClickListener){
        mContext = context;
        mProductList = productList;
        mOnItemClickListener = onItemClickListener;
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductListHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.product_listing_view, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ProductListHolder productListHolder = (ProductListHolder) holder;
        mProductInfo = mProductList.get(position);
        mImageLoader.setCartImageUrl(mProductInfo.getProductImage(), productListHolder.productImage);
        productListHolder.productTitle = new TextView(mContext);
        productListHolder.productTitle.setTypeface(productListHolder.productTitle.getTypeface(), Typeface.BOLD);
        productListHolder.productTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mContext.getResources().getDimension(R.dimen.body_default_font_size));
        productListHolder.productTitle.setTextColor(Color.BLACK);
        productListHolder.productTitle.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp));
        productListHolder.productPrice = new TextView(mContext);
        productListHolder.productPrice.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp)
                ,mContext.getResources().getDimensionPixelSize(R.dimen.padding_10dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp));
        productListHolder.productTitle.setText(mProductInfo.getProductTitle());
        productListHolder.productPrice.setText(GlobalFunctions.getFormattedCurrencyString(
                mProductInfo.getCurrency(),mProductInfo.getProductPrice()));
        productListHolder.productConfigView.addView(productListHolder.productTitle);
        if(mProductInfo.getProductConfigurations() != null){
            Iterator<?> keys = mProductInfo.getProductConfigurations().keys();
            while( keys.hasNext() ) {
                String key = (String) keys.next();
                TextView configField = new TextView(mContext);
                configField.setTextColor(Color.GRAY);
                configField.setPadding(mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_2dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_5dp),
                        mContext.getResources().getDimensionPixelSize(R.dimen.padding_2dp));
                configField.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        mContext.getResources().getDimension(R.dimen.caption_font_size));
                configField.setText(key + " : " +
                        mProductInfo.getProductConfigurations().optString(key));
                productListHolder.productConfigView.addView(configField);
            }
        }


        productListHolder.productQty.setText(String.valueOf(mProductInfo.getProductQty()));
        if(mProductInfo.getProductQty() == 1){
            productListHolder.qtyRmv.setVisibility(View.GONE);
        }else {
            productListHolder.qtyRmv.setVisibility(View.VISIBLE);
        }
        if(mProductInfo.isAllowModification()) {
            productListHolder.productRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismissProduct(productListHolder.getAdapterPosition());
                }
            });
            productListHolder.qtyAdd.setOnClickListener(this);
            productListHolder.qtyRmv.setOnClickListener(this);
            productListHolder.qtyAdd.setTag(position);
            productListHolder.qtyRmv.setTag(position);
        }else {
            productListHolder.productRemove.setVisibility(View.GONE);
            productListHolder.qtyAdd.setVisibility(View.GONE);
            productListHolder.qtyRmv.setVisibility(View.GONE);
        }
        if(mProductInfo.getErrorMsg() != null && !mProductInfo.getErrorMsg().isEmpty() ){
            productListHolder.errorView.setVisibility(View.VISIBLE);
            productListHolder.errorView.setText(mProductInfo.getErrorMsg());
        }else {
            productListHolder.errorView.setVisibility(View.GONE);
        }
        productListHolder.productConfigView.addView(productListHolder.productPrice);

    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder){
        ((ProductListHolder)holder).productConfigView.removeAllViews();

    }
    @Override
    public int getItemCount() {
        return mProductList.size();
    }

    public void dismissProduct(final int position){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(mContext);

        alertBuilder.setMessage(mContext.getResources().getString(R.string.delete_product_warning));
        alertBuilder.setTitle(mContext.getResources().getString(R.string.delete_product_title));

        alertBuilder.setPositiveButton(mContext.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //TODO NullPointerException
                try {
                    PreferencesUtils.updateCartCount(mContext,
                            String.valueOf(Integer.parseInt(PreferencesUtils.
                                    getNotificationsCounts(mContext,PreferencesUtils.CART_COUNT)) -
                                    mProductList.get(position).getTotalProductCount()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mOnItemClickListener.onItemClick(null, mProductList.get(position));
                mProductList.remove(position);
                ProductListingAdapter.this.notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }});
        alertBuilder.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ProductListingAdapter.this.notifyItemChanged(position);
                dialog.cancel();
            }
        });
        alertBuilder.setCancelable(false);
        alertBuilder.create().show();
    }

    @Override
    public void onClick(View view) {
        int position = Integer.parseInt(view.getTag().toString());
        switch (view.getId()){
            case R.id.qty_add:
                mProductList.get(position).setProductQty(mProductList.get(position).getProductQty() + 1);
                mProductList.get(position).setProductPrice(mProductList.get(position).getProductPrice()
                        + mProductList.get(position).getUnitPrice());
                this.notifyDataSetChanged();
                break;
            case R.id.qty_rmv:
                mProductList.get(position).setProductQty(mProductList.get(position).getProductQty() - 1);
                mProductList.get(position).setProductPrice(mProductList.get(position).getProductPrice()
                        - mProductList.get(position).getUnitPrice());
                this.notifyDataSetChanged();
                break;
        }
    }

    public class ProductListHolder extends RecyclerView.ViewHolder{
        TextView productQty,productTitle,productPrice,errorView;
        ImageView productImage,qtyAdd,qtyRmv,productRemove;
        LinearLayout productConfigView;
        public ProductListHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productConfigView = itemView.findViewById(R.id.product_details);
            productQty = itemView.findViewById(R.id.qty_count);
            qtyAdd = itemView.findViewById(R.id.qty_add);
            qtyRmv = itemView.findViewById(R.id.qty_rmv);
            errorView = itemView.findViewById(R.id.error_view);
            productRemove = itemView.findViewById(R.id.remove_button);

        }
    }
}
