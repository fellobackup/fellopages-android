package com.fellopages.mobileapp.classes.common.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fellopages.mobileapp.R;

public class ProgressBarHolder extends RecyclerView.ViewHolder {
    public ProgressBar progressBar;
    public TextView mFooterText;

    public ProgressBarHolder(View v) {
        super(v);
        progressBar = v.findViewById(R.id.progressBar);
        mFooterText = v.findViewById(R.id.footer_text);
    }
}
