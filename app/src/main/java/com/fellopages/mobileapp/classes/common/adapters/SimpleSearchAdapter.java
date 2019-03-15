package com.fellopages.mobileapp.classes.common.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fellopages.mobileapp.R;


public class SimpleSearchAdapter extends SimpleCursorAdapter {

    public SimpleSearchAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        TextView textView= view.findViewById(R.id.locationLabel);
        textView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        textView.setTextColor(Color.parseColor("#8b8b8b"));
        textView.setPadding(15,20,15,20);
        textView.setText(cursor.getString(1));
    }
}
