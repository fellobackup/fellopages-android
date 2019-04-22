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

package com.fellopages.mobileapp.classes.common.ui;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.fellopages.mobileapp.R;

public class SelectableTextView extends AppCompatTextView {

    Context mContext;

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
        setCustomSelectionActionModeCallback(new TextSelectionActionModeCallback());
    }

    public SelectableTextView(Context context) {
        super(context);
        this.setTextIsSelectable(true);
        mContext = context;
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextIsSelectable(true);
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTextIsSelectable(true);
    }

    /**
     * Method to make Description resizable.
     * @param textViewDescription TextView which contains Description.
     * @param maxLine max no. of line in textView.
     * @param expandText Expand text
     * @param viewMore true if want to show view more.
     */
    public void makeTextViewResizable(final TextView textViewDescription, final int maxLine,
                                      final String expandText, final boolean viewMore) {

        if (textViewDescription.getTag() == null) {
            textViewDescription.setTag(textViewDescription.getText());
        }
        ViewTreeObserver viewTreeObserver = textViewDescription.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver treeObserver = textViewDescription.getViewTreeObserver();
                treeObserver.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {

                    int lineEndIndex = textViewDescription.getLayout().getLineEnd(0);
                    String text = textViewDescription.getText().subSequence(0, lineEndIndex - expandText.length() + 1) +
                            " " + expandText;
                    textViewDescription.setText(text);
                    textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());
                    textViewDescription.setText(addClickablePart(Html.fromHtml(textViewDescription.getText().toString()),
                                    textViewDescription, expandText, viewMore),
                            TextView.BufferType.SPANNABLE);

                } else if (maxLine > 0 && textViewDescription.getLineCount() >= maxLine) {

                    int lineEndIndex = textViewDescription.getLayout().getLineEnd(maxLine - 1);
                    String text = textViewDescription.getText().subSequence(0, lineEndIndex - expandText.length() + 1)
                            + " " + expandText;
                    textViewDescription.setText(text);
                    textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());
                    textViewDescription.setText(addClickablePart(Html.fromHtml(textViewDescription.getText().toString()),
                                    textViewDescription, expandText, viewMore),
                            TextView.BufferType.SPANNABLE);

                } else {
                    int lineEndIndex = textViewDescription.getLayout().getLineEnd(
                            textViewDescription.getLayout().getLineCount() - 1);
                    String text = textViewDescription.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    textViewDescription.setText(text);
                    textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());
                    textViewDescription.setText(addClickablePart(Html.fromHtml(textViewDescription.getText().toString()),
                                    textViewDescription, expandText, viewMore),
                            TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    /**
     * Method to make "More" and "Less" text clickable.
     * @param strSpanned Spanned string.
     * @param textViewDescription TextView which contains Description.
     * @param spanableText text which is spannable.
     * @param viewMore true if want to show view more.
     * @return
     */
    public SpannableStringBuilder addClickablePart(final Spanned strSpanned, final TextView textViewDescription,
                                                   final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {
            stringBuilder.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {

                    if (viewMore) {
                        textViewDescription.setLayoutParams(textViewDescription.getLayoutParams());
                        textViewDescription.setText(textViewDescription.getTag().toString(), TextView.BufferType.SPANNABLE);
                        textViewDescription.invalidate();
                        makeTextViewResizable(textViewDescription, -1, "..." + getResources().getString(R.string.readLess), false);
                    } else {
                        textViewDescription.setLayoutParams(textViewDescription.getLayoutParams());
                        textViewDescription.setText(textViewDescription.getTag().toString(), TextView.BufferType.SPANNABLE);
                        textViewDescription.invalidate();
                        makeTextViewResizable(textViewDescription, 3, "..." + getResources().getString(R.string.readMore), true);
                    }

                }

                @Override
                public void updateDrawState (TextPaint ds){
                    ds.setUnderlineText(false);
                    ds.setColor(ContextCompat.getColor(mContext, R.color.gray_text_color));

                }
            }

                    ,str.indexOf(spanableText),str.indexOf(spanableText)+spanableText.length(),0);
            textViewDescription.setMovementMethod(LinkMovementMethod.getInstance());

        }
        return stringBuilder;

    }

    /**
     * Class to remove "Text Selection" from Contextual Action Bar.
     */
    private class TextSelectionActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("");
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }

}

