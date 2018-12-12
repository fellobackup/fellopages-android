package com.bigsteptech.realtimechat.settings.adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigsteptech.realtimechat.R;
import com.bigsteptech.realtimechat.interfaces.OnItemClickListener;
import com.bigsteptech.realtimechat.settings.data_model.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsListAdapter extends RecyclerView.Adapter {


    private List<Settings> mSettingsList = new ArrayList<>();
    private Context mContext;
    private Settings settings;
    private OnItemClickListener mOnItemClickListener;



    public SettingsListAdapter(Context context, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        mOnItemClickListener = onItemClickListener;
    }


    @Override
    public int getItemCount() {
        return mSettingsList.size();
    }


    public void update(List<Settings> settingsList){
        mSettingsList = settingsList;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = this.mSettingsList.size();
        this.mSettingsList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.settings_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            settings = mSettingsList.get(position);
            itemViewHolder.settingTitle.setText(settings.getSettingTitle());

            switch (settings.getSettingName()){

                case "blockList":
                    itemViewHolder.nextIcon.setTypeface(Typeface.createFromAsset(mContext.getAssets(),
                            "fontIcons/fontawesome-webfont.ttf"));
                    itemViewHolder.nextIcon.setVisibility(View.VISIBLE);
                    itemViewHolder.nextIcon.setText("\uF054");
                    itemViewHolder.settingValue.setVisibility(View.GONE);
                    itemViewHolder.notificationSettingSwitch.setVisibility(View.GONE);
                    break;

                case "status":
                    switch (settings.getOnlineStatus()){
                        case 0:
                            itemViewHolder.settingValue.setText(mContext.getResources().getString(R.string.offline));
                            break;
                        case 1:
                            itemViewHolder.settingValue.setText(mContext.getResources().getString(R.string.online));
                            break;
                    }
                    itemViewHolder.nextIcon.setVisibility(View.GONE);
                    itemViewHolder.settingValue.setVisibility(View.VISIBLE);
                    itemViewHolder.notificationSettingSwitch.setVisibility(View.GONE);
                    break;

                case "language":
                    String lng = settings.getLanguage();
                    String[] language = lng.split("_");
                    Locale locale;
                    if (language.length == 2) {
                        locale = new Locale(language[0], language[1]);
                    } else {
                        locale = new Locale(language[0]);
                    }

                    String name = locale.getDisplayLanguage(locale);
                    itemViewHolder.settingValue.setText(name);
                    itemViewHolder.nextIcon.setVisibility(View.GONE);
                    itemViewHolder.settingValue.setVisibility(View.VISIBLE);
                    itemViewHolder.notificationSettingSwitch.setVisibility(View.GONE);
                    break;

                case "notifications":
                    itemViewHolder.notificationSettingSwitch.setChecked(settings.isNotificationsEnabled() == 1);
                    itemViewHolder.notificationSettingSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnItemClickListener.onItemClick(itemViewHolder.getAdapterPosition());
                        }
                    });
                    itemViewHolder.nextIcon.setVisibility(View.GONE);
                    itemViewHolder.settingValue.setVisibility(View.GONE);
                    itemViewHolder.notificationSettingSwitch.setVisibility(View.VISIBLE);
                    break;
            }

            itemViewHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(itemViewHolder.getAdapterPosition());
                }
            });

    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView settingTitle, settingValue, nextIcon;
        public SwitchCompat notificationSettingSwitch;

        public View container;

        public ItemViewHolder(View view) {
            super(view);

            container = view;
            settingTitle = (TextView) view.findViewById(R.id.setting_title);
            settingValue = (TextView) view.findViewById(R.id.setting_value);
            nextIcon = (TextView) view.findViewById(R.id.next_icon);
            notificationSettingSwitch = (SwitchCompat) view.findViewById(R.id.notification_setting_switch);

        }
    }

}
