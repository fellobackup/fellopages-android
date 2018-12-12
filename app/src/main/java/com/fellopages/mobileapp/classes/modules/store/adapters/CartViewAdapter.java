package com.fellopages.mobileapp.classes.modules.store.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.ui.CircularImageView;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.store.data.CartData;
import com.fellopages.mobileapp.classes.modules.store.utils.CartInfoModel;

import java.util.List;

public class CartViewAdapter extends RecyclerView.Adapter {

    public Context mContext;
    public List<CartInfoModel> mCartInfo;
    public CartInfoModel mStoreInfo;
    private OnItemClickListener mOnItemClickListener;
    private ImageLoader mImageLoader;

    public CartViewAdapter(Context context, List<CartInfoModel> cartList, OnItemClickListener onItemClickListener){
        mContext = context;
        mCartInfo = cartList;
        mOnItemClickListener = onItemClickListener;
        mImageLoader = new ImageLoader(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cart_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        CartViewHolder cartViewHolder = (CartViewHolder) holder;
        mStoreInfo = mCartInfo.get(position);
        if(mStoreInfo.getProductsArray() != null) {
            cartViewHolder.privateOrder.setVisibility(View.GONE);
            if(mStoreInfo.getNoteFormArray() != null) {
                cartViewHolder.writeNote.setVisibility(View.VISIBLE);
            }else {
                cartViewHolder.writeNote.setVisibility(View.GONE);
            }
            cartViewHolder.storeTitle.setText(mStoreInfo.getStoreTitle());
            cartViewHolder.subTotalAmount.setText(
                    GlobalFunctions.getFormattedCurrencyString(
                            mStoreInfo.getDefaultCurrency(), mStoreInfo.getSubTotal()));

            if(mStoreInfo.getStoreTax() != 0) {
                cartViewHolder.taxVat.setText(GlobalFunctions.getFormattedCurrencyString(
                        mStoreInfo.getDefaultCurrency(),
                        mStoreInfo.getStoreTax()));
            }else {
                cartViewHolder.tavVatView.setVisibility(View.GONE);
            }
            if(mStoreInfo.getCouponObject() != null) {
                cartViewHolder.couponDiscount.setText("-"+
                        GlobalFunctions.getFormattedCurrencyString(
                                mStoreInfo.getDefaultCurrency(),
                                mStoreInfo.getCouponObject().optDouble("value")));
                cartViewHolder.couponName.setText(mStoreInfo.getCouponObject().optString("coupon_code"));
                cartViewHolder.couponDetailView.setVisibility(View.VISIBLE);

            }else {
                cartViewHolder.couponDetailView.setVisibility(View.GONE);
            }

            if(mStoreInfo.getStoreTax() == 0 && mStoreInfo.getShippingMethodName() == null){
                cartViewHolder.totalAmountView.setVisibility(View.GONE);
            }else {
                cartViewHolder.totalAmountView.setVisibility(View.VISIBLE);
                cartViewHolder.totalAmount.setText(GlobalFunctions.getFormattedCurrencyString(
                        mStoreInfo.getDefaultCurrency(),
                        mStoreInfo.getStoreTotalAmount()));
            }
            if(mStoreInfo.getShippingMethodName() != null && !mStoreInfo.getShippingMethodName().isEmpty()){
                cartViewHolder.shippingCostView.setVisibility(View.VISIBLE);
                cartViewHolder.shippingMethodName.setText(mStoreInfo.getShippingMethodName());
                cartViewHolder.shippingAmount.setText(GlobalFunctions.getFormattedCurrencyString(
                        mStoreInfo.getDefaultCurrency(),
                        mStoreInfo.getShippingMethodAmount()));
            }else {
                cartViewHolder.shippingCostView.setVisibility(View.GONE);
            }
            cartViewHolder.viewItemsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(view, holder.getAdapterPosition());
                }
            });
            cartViewHolder.mProductGroup = new LinearLayout(mContext);
            cartViewHolder.mProductGroup.setOrientation(LinearLayout.HORIZONTAL);
            for (int i = 0; i < mStoreInfo.getProductsArray().length(); i++) {
                String imageUrl = mStoreInfo.getProductsArray().optJSONObject(i).optString("image_profile");
                CircularImageView circularImageView = new CircularImageView(mContext);

                circularImageView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                circularImageView.getLayoutParams().height = 100;
                circularImageView.getLayoutParams().width = 100;
                mImageLoader.setCartImageUrl(imageUrl, circularImageView);
                cartViewHolder.mProductGroup.addView(circularImageView);

            }
            cartViewHolder.productScrollView.addView(cartViewHolder.mProductGroup);
        }else {
            cartViewHolder.subTotalLabel.setText(mContext.getResources().getString(R.string.grand_total));
            cartViewHolder.subTotalAmount.setText(
                    GlobalFunctions.getFormattedCurrencyString(
                            mStoreInfo.getDefaultCurrency(), mStoreInfo.getGrandTotal()));
            if(mStoreInfo.getNoteFormArray() != null) {
                cartViewHolder.privateOrder.setVisibility(View.VISIBLE);
                CartData.updateOrderDetails(mContext,true);
                cartViewHolder.privateOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isPrivateOrder) {
                        CartData.updateOrderDetails(mContext,isPrivateOrder);
                    }
                });
            }else {
                cartViewHolder.privateOrder.setVisibility(View.GONE);
            }
            cartViewHolder.couponDetailView.setVisibility(View.GONE);
            cartViewHolder.totalAmountView.setVisibility(View.GONE);
            cartViewHolder.tavVatView.setVisibility(View.GONE);
            cartViewHolder.mProductMainView.setVisibility(View.GONE);
            cartViewHolder.storeTitle.setVisibility(View.GONE);
            cartViewHolder.dividerFirst.setVisibility(View.GONE);
            cartViewHolder.dividerSecond.setVisibility(View.GONE);
            cartViewHolder.writeNote.setVisibility(View.GONE);
        }
    }
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder){
        ((CartViewHolder)holder).productScrollView.removeAllViews();
    }
    @Override
    public int getItemCount() {
        return mCartInfo.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView storeTitle,subTotalAmount,couponDiscount,taxVat,couponName;
        TableRow shippingCostView,couponDetailView,totalAmountView,tavVatView;
        TextView viewItemsBtn,totalAmount,shippingMethodName,shippingAmount,subTotalLabel;
        EditText writeNote;
        CheckBox privateOrder;
        HorizontalScrollView productScrollView;
        LinearLayout mProductGroup,mProductMainView;
        View dividerFirst,dividerSecond;

        public CartViewHolder(View itemView) {
            super(itemView);
            storeTitle = (TextView) itemView.findViewById(R.id.store_title);
            viewItemsBtn = (TextView) itemView.findViewById(R.id.view_products_btn);
            productScrollView = (HorizontalScrollView) itemView.findViewById(R.id.item_layout);
            mProductMainView = (LinearLayout) itemView.findViewById(R.id.product_view);

            subTotalLabel = (TextView) itemView.findViewById(R.id.subtotal_amount_label);
            subTotalAmount = (TextView) itemView.findViewById(R.id.sub_total_amount);

            shippingCostView = (TableRow) itemView.findViewById(R.id.shipping_cost_view);
            shippingMethodName = (TextView) itemView.findViewById(R.id.shipping_method_name);
            shippingAmount = (TextView) itemView.findViewById(R.id.shipping_amount);

            tavVatView = (TableRow) itemView.findViewById(R.id.taxVatView);
            taxVat = (TextView) itemView.findViewById(R.id.taxVatAmount);

            couponDetailView = (TableRow) itemView.findViewById(R.id.couponView);
            couponDiscount =(TextView) itemView.findViewById(R.id.discountAmount);
            couponName = (TextView) itemView.findViewById(R.id.coupon_name);

            totalAmountView = (TableRow) itemView.findViewById(R.id.total_amount_view);
            totalAmount = (TextView) itemView.findViewById(R.id.remainingAmount);

            dividerFirst = itemView.findViewById(R.id.cart_divider_first);
            dividerSecond = itemView.findViewById(R.id.cart_divider_second);

            writeNote = (EditText) itemView.findViewById(R.id.write_note);
            privateOrder = (CheckBox) itemView.findViewById(R.id.make_private);
        }
    }
}
