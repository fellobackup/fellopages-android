package com.fellopages.mobileapp.classes.common.multimediaselector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.multimediaselector.bean.Folder;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class FolderAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private String mCategoryText;

    private List<Folder> mFolders = new ArrayList<>();

    int mImageSize;

    int lastSelected = 0;

    public FolderAdapter(Context context, String categoryText){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageSize = mContext.getResources().getDimensionPixelOffset(R.dimen.folder_cover_size);
        mImageLoader = new ImageLoader(mContext);
        mCategoryText = categoryText;
    }

    /**
     * Set dataset
     * @param folders
     */
    public void setData(List<Folder> folders) {
        if(folders != null && folders.size()>0){
            mFolders = folders;
        }else{
            mFolders.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mFolders.size()+1;
    }

    @Override
    public Folder getItem(int i) {
        if(i == 0) return null;
        return mFolders.get(i-1);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = mInflater.inflate(R.layout.list_item_folder, viewGroup, false);
            holder = new ViewHolder(view);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        if (holder != null) {
            if(i == 0){
                holder.name.setText(mCategoryText);
                holder.size.setText(getTotalImageSize() + " Looks");
                if(mFolders.size()>0){
                    Folder f = mFolders.get(0);
                    if (f.mimeType.startsWith("image")) {
                        mImageLoader.setFileImage(new File(f.cover.path), mImageSize, mImageSize, holder.cover);
                    } else {
                        mImageLoader.loadVideoThumbnail(holder.cover, f.cover.path, mImageSize, mImageSize);
                    }
                }
            }else {
                holder.bindData(getItem(i));
            }
            if(lastSelected == i){
                holder.indicator.setVisibility(View.VISIBLE);
            }else{
                holder.indicator.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    private int getTotalImageSize(){
        int result = 0;
        if(mFolders != null && mFolders.size()>0){
            for (Folder f: mFolders){
                result += f.images.size();
            }
        }
        return result;
    }

    public void setSelectIndex(int i) {
        if(lastSelected == i) return;

        lastSelected = i;
        notifyDataSetChanged();
    }

    public int getSelectIndex(){
        return lastSelected;
    }

    class ViewHolder{
        ImageView cover;
        TextView name;
        TextView size;
        ImageView indicator;
        ViewHolder(View view){
            cover = view.findViewById(R.id.cover);
            name = view.findViewById(R.id.name);
            size = view.findViewById(R.id.size);
            indicator = view.findViewById(R.id.indicator);
            cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cover.getLayoutParams().width = mImageSize;
            cover.getLayoutParams().height = mImageSize;
            view.setTag(this);
        }

        void bindData(Folder data) {
            name.setText(data.name);
            size.setText(data.images.size()+" Looks");
            // Display Pictures
            if (data.mimeType.startsWith("image")) {
                mImageLoader.setFileImage(new File(data.cover.path), mImageSize, mImageSize, cover);
            } else {
                mImageLoader.loadVideoThumbnail(cover, data.cover.path, mImageSize, mImageSize);
            }
        }
    }
}
