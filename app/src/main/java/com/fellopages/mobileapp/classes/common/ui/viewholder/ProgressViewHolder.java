/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 */

package com.fellopages.mobileapp.classes.common.ui.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.ConstantVariables;


public class ProgressViewHolder extends RecyclerView.ViewHolder {

    public View progressView;

    public ProgressViewHolder(View view) {
        super(view);
        progressView = view;
    }

    /***
     * Method to Show Footer ProgressBar on Scrolling and Show End Of Result Text if there are no more results
     * @param context Context of calling class.
     * @param progressView Root View of progress layout.
     * @param listItem ListItem.
     */
    public static void inflateProgressView(Context context, View progressView, Object listItem) {

        ProgressBar progressBar = (ProgressBar) progressView.findViewById(R.id.progressBar);
        TextView footerText = (TextView) progressView.findViewById(R.id.footer_text);
        if (listItem == null) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            footerText.setVisibility(View.GONE);
        } else {
            footerText.setVisibility(View.VISIBLE);
            footerText.setText(context.getResources().getString(R.string.end_of_results));
            progressBar.setVisibility(View.GONE);
        }

    }

    /***
     * Method to Show Footer ProgressBar on Scrolling and Show "View All" Text if there are more results
     * @param context Context of calling class.
     * @param progressView Root View of progress layout.
     * @param listItem ListItem.
     * @param action Action to be performed on view all option.
     */
    public static void inflateFooterView(final Context context, View progressView, Object listItem, final String action) {

        ProgressBar progressBar = (ProgressBar) progressView.findViewById(R.id.progressBar);
        TextView footerText = (TextView) progressView.findViewById(R.id.footer_text);
        RelativeLayout footerView = (RelativeLayout) progressView.findViewById(R.id.footer_layout);

        if(listItem.equals(ConstantVariables.FOOTER_TYPE)){
            footerText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            footerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent();
                    in.setAction(action);
                    context.sendBroadcast(in);
                }
            });

        }else {
            footerText.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
        }
    }

    /***
     * Method to Show Footer ProgressBar on Scrolling.
     * @param progressView Root View of progress layout.
     */
    public static void inflateProgressBar(View progressView) {
        ProgressBar progressBar = (ProgressBar) progressView.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
    }

}
