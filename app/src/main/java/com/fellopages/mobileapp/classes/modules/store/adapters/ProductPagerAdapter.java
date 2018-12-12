package com.fellopages.mobileapp.classes.modules.store.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.modules.store.utils.ProductInfoModel;

import java.util.List;

public class ProductPagerAdapter extends PagerAdapter {
    private List<ProductInfoModel> mProductImageList;
    private Activity mActivity;
    LayoutInflater inflater;
    View viewLayout;
    ImageView imageView;
    private OnItemClickListener mItemClickListener;
    private ImageLoader mImageLoader;

    public ProductPagerAdapter(Activity activity,List<ProductInfoModel> imageList,
                               OnItemClickListener itemListener) {
        mActivity = activity;
        mProductImageList = imageList;
        mItemClickListener = itemListener;
        mImageLoader = new ImageLoader(mActivity);
    }


    @Override public int getCount() {
        return mProductImageList.size();
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override public Object instantiateItem(ViewGroup view, final int position) {
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, view, false);
        imageView = (ImageView) viewLayout.findViewById(R.id.image);
        ProductInfoModel productInfoModel = mProductImageList.get(position);
        mImageLoader.setProductCoverImage(productInfoModel.getProductImage(), imageView);
        viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(view, position);
            }
        });
        view.addView(viewLayout);
        return viewLayout;
    }
    public void addItem() {
        notifyDataSetChanged();
    }

    public void removeItem() {
        notifyDataSetChanged();
    }
}