package com.bigsteptech.realtimechat.utils;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bigsteptech.realtimechat.R;

public class DialogUtils {

    /** A popup to select the type of message to send, "Text", "Image", "Location".*/
    public static PopupWindow getMenuOptionPopup(Context context, View.OnClickListener listener){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.chat_sdk_popup_options, null);
        PopupWindow optionPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupView.findViewById(R.id.photo_attachment_btn).setOnClickListener(listener);
        popupView.findViewById(R.id.video_attachment_btn).setOnClickListener(listener);
        popupView.findViewById(R.id.audio_attachment_btn).setOnClickListener(listener);
//        popupView.findViewById(R.id.document_attachment_btn).setOnClickListener(listener);

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        // TODO fix popup size to wrap view size.
        optionPopup.setContentView(popupView);
        optionPopup.setBackgroundDrawable(new BitmapDrawable());
        optionPopup.setOutsideTouchable(true);
        optionPopup.setWidth(popupView.getMeasuredWidth());
        optionPopup.setHeight(popupView.getMeasuredHeight());
        return optionPopup;
    }
}
