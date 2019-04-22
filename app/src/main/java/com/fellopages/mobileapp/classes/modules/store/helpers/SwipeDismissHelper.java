package com.fellopages.mobileapp.classes.modules.store.helpers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.fellopages.mobileapp.classes.modules.store.adapters.ProductListingAdapter;

public class SwipeDismissHelper extends ItemTouchHelper.SimpleCallback {
    ProductListingAdapter adapter;

    public SwipeDismissHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    public SwipeDismissHelper(ProductListingAdapter adapter){
        super(ItemTouchHelper.UP,ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.dismissProduct(viewHolder.getAdapterPosition());

    }
}
