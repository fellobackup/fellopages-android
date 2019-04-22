package com.socialengineaddons.messenger.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.conversation.data_model.AttachmentItems;
import com.socialengineaddons.messenger.interfaces.OnItemClickListener;

import java.util.List;


public class AttachmentRecyclerViewAdapter extends RecyclerView.Adapter<AttachmentRecyclerViewAdapter.AttachmentRecyclerViewHolder> {

    //Member Variables.
    private Context mContext;
    private List<AttachmentItems> mAttachmentList;
    private OnItemClickListener mOnClickListener;


    public AttachmentRecyclerViewAdapter(Context context, List<AttachmentItems> attachmentList,
                                         OnItemClickListener onClickListener) {
        mContext = context;
        mAttachmentList = attachmentList;
        mOnClickListener = onClickListener;
    }

    @Override
    public AttachmentRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new AttachmentRecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.attachment_items, parent, false));
    }

    @Override
    public void onBindViewHolder(final AttachmentRecyclerViewHolder viewHolder, int position) {

        AttachmentItems attachmentItem = mAttachmentList.get(position);

        Drawable drawable = ContextCompat.getDrawable(mContext, attachmentItem.getIcon());
        drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.title_color),
                PorterDuff.Mode.SRC_ATOP));

        viewHolder.ivAttachment.setImageDrawable(drawable);
        viewHolder.tvTitle.setText(attachmentItem.getTitle());

        // Set Onclick listener on each item of recycler view.
        viewHolder.mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onItemClick(viewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAttachmentList.size();
    }


    public static class AttachmentRecyclerViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivAttachment;
        public TextView tvTitle;
        public View mContainer;

        public AttachmentRecyclerViewHolder(View view) {
            super(view);
            mContainer = view;
            ivAttachment = (ImageView) view.findViewById(R.id.attachment_icon);
            tvTitle = (TextView) view.findViewById(R.id.title);
        }
    }
}
