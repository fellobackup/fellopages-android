/*
 *
 * Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 * You may not use this file except in compliance with the
 * SocialEngineAddOns License Agreement.
 * You may obtain a copy of the License at:
 * https://www.socialengineaddons.com/android-app-license
 * The full copyright and license information is also mentioned
 * in the LICENSE file that was distributed with this
 * source code.
 *
 */

/*
 * Class handling the hash tags in a string.
 */

package com.fellopages.mobileapp.classes.common.ui.hashtag;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagSelectingTextView {

	String mHastTagColor;
	OnTagClickListener mOnTagClickListener;
	int mHypeLinkEnabled;

	public SpannableStringBuilder addClickablePart(String nTagString,
                                                   OnTagClickListener onTagClickListener, int hypeLinkEnabled, String hastTagColor) {

		this.mHastTagColor = hastTagColor;

		this.mHypeLinkEnabled = hypeLinkEnabled;

		this.mOnTagClickListener = onTagClickListener;

        // Pattern for getting the hash tags from a string


        Pattern hashTagsPattern = Pattern.compile("(#[a-zA-Z0-9_-]+)");

		SpannableStringBuilder string = new SpannableStringBuilder(nTagString);

        CharSequence spanText;
        int start;
        int end;

        // Matching the pattern with the existing string

        Matcher m = hashTagsPattern.matcher(nTagString);

        while (m.find()) {

            start = m.start();
             end = m.end();

            spanText = nTagString.subSequence(start, end);


            final CharSequence mLastTextSpan = spanText;
            
            string.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    // Click on each tag will get here
                    if (mOnTagClickListener != null)
                        mOnTagClickListener.clickedTag(mLastTextSpan);
                }

                @Override
                public void updateDrawState(TextPaint ds) {

                    // color for the hash tag
                    ds.setColor(Color.parseColor(mHastTagColor));

                    if (mHypeLinkEnabled == 0) {
                        ds.setUnderlineText(false);// Disable the
                        // underline for
                        // hash Tags.
                    } else {
                        ds.setUnderlineText(true);// Enables the
                        // underline for
                        // hash Tags.

                    }
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        }



		return string;
	}

}
