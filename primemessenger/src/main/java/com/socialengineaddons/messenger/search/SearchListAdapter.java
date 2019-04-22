package com.socialengineaddons.messenger.search;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.socialengineaddons.messenger.R;
import com.socialengineaddons.messenger.interfaces.OnItemClickListener;
import com.socialengineaddons.messenger.ui.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter {


    private List<SearchList> mSearchList = new ArrayList<>();
    private SearchList searchList;
    private Context mContext;

    private static final int CONTACT_LIST_WITH_HEADER = 1;
    private static final int CONTACT_LIST_WITHOUT_HEADER = 2;
    private static final int GROUPS_LIST_WITH_HEADER = 3;
    private static final int GROUPS_LIST_WITHOUT_HEADER = 4;
    private OnItemClickListener mOnItemClickListener;


    public SearchListAdapter(Context context, OnItemClickListener onItemClickListener) {

        this.mContext = context;
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mSearchList.size();
    }

    public List<SearchList> getmSearchList(){

        return mSearchList;
    }

    public void update(List<SearchList> conversationList){
        mSearchList = conversationList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            if(mSearchList.get(position).getTypeOfChat() == 0)
                return CONTACT_LIST_WITH_HEADER;
            else
                return GROUPS_LIST_WITH_HEADER;
        } else if(mSearchList.get(position).getTypeOfChat() != mSearchList.get(position-1).getTypeOfChat()){
            return GROUPS_LIST_WITH_HEADER;
        } else if(mSearchList.get(position).getTypeOfChat() == 1) {
            return GROUPS_LIST_WITHOUT_HEADER;
        } else{
            return CONTACT_LIST_WITHOUT_HEADER;
        }
    }

    public void filter(String text) {
        Iterator<SearchList> it = mSearchList.iterator();
        while (it.hasNext()) {
            SearchList searchList = it.next();
            if (searchList.getTitle() != null
                    && !searchList.getTitle().toLowerCase().contains(text.toLowerCase())) {
                it.remove();
            } else if (searchList.getTitle() == null) {
                it.remove();
            }
        }
        notifyDataSetChanged();

        if(mSearchList.isEmpty()){
            ((SearchContactsActivity)mContext).showErrorMessage(true);
        } else {
            ((SearchContactsActivity)mContext).hideErrorMessage();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View itemView = null;
        switch (viewType){

            case CONTACT_LIST_WITHOUT_HEADER:
            case GROUPS_LIST_WITHOUT_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recent_conversation_list, parent, false);
                itemView.findViewById(R.id.header_text).setVisibility(View.GONE);
                break;

            case CONTACT_LIST_WITH_HEADER:
            case GROUPS_LIST_WITH_HEADER:
                itemView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.recent_conversation_list, parent, false);
                itemView.findViewById(R.id.header_text).setVisibility(View.VISIBLE);
                break;
        }
        viewHolder = new ItemViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder)holder).cleanup();
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        searchList = mSearchList.get(position);

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        switch (holder.getItemViewType()) {

            case CONTACT_LIST_WITH_HEADER:
                (itemViewHolder.headerTextView).setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text_color));
                (itemViewHolder.headerTextView).setText(mContext.getResources().getString(R.string.people));
                break;

            case GROUPS_LIST_WITH_HEADER:
                (itemViewHolder.headerTextView).setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text_color));
                (itemViewHolder.headerTextView).setText(mContext.getResources().getString(R.string.group_conversation));
                break;

        }

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) itemViewHolder.
                mSearchItemTitle.getLayoutParams();

        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        itemViewHolder.mSearchItemTitle.setLayoutParams(layoutParams);
        itemViewHolder.mSearchItemTitle.setText(searchList.getTitle());

        if(searchList.getImagePath() != null && !searchList.getImagePath().isEmpty()){
            itemViewHolder.searchItemImage.hideText();
            Picasso.with(mContext)
                    .load(searchList.getImagePath())
                    .placeholder(R.drawable.person_image_empty)
                    .into(itemViewHolder.searchItemImage);
        } else {
            itemViewHolder.searchItemImage.showText(searchList.getTitle(), searchList.getProfileColor());
        }

        itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        // Fields for Recent Chats and Contacts List
        public CircularImageView searchItemImage;
        public TextView mSearchItemTitle, headerTextView;

        public View container;

        public ItemViewHolder(View view) {
            super(view);

            container = view;
            searchItemImage = (CircularImageView) view.findViewById(R.id.user_image);
            mSearchItemTitle = (TextView) view.findViewById(R.id.user_name);
            view.findViewById(R.id.chatMessage).setVisibility(View.GONE);
            view.findViewById(R.id.online_icon).setVisibility(View.GONE);
            headerTextView = (TextView) view.findViewById(R.id.header_text);

        }

        /**
         * Method to cleanup the image.
         */
        private void cleanup() {
            if (searchItemImage != null) {
                Picasso.with(searchItemImage.getContext())
                        .cancelRequest(searchItemImage);
                searchItemImage.setImageDrawable(null);
            }
        }
    }

}
