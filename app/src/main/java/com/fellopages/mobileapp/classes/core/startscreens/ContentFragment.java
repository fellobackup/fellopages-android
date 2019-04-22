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

package com.fellopages.mobileapp.classes.core.startscreens;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fellopages.mobileapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "desc";
    private static final String ARG_DRAWABLE = "drawable";

    public ContentFragment() {
        // Required empty public constructor
    }

    public static ContentFragment newInstance(String title, String description, int imageDrawable) {
        ContentFragment sampleSlide = new ContentFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        args.putInt(ARG_DRAWABLE, imageDrawable);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public static ContentFragment newInstance(String title, int imageDrawable) {
        ContentFragment sampleSlide = new ContentFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_DRAWABLE, imageDrawable);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    private int drawable;
    private String title, description;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().size() != 0) {
            drawable = getArguments().getInt(ARG_DRAWABLE);
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESC);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_content, container, false);
        TextView titleView = v.findViewById(R.id.slideTitle);
        TextView subtitle = v.findViewById(R.id.slideSubtitle);
        titleView.setText(title);
        subtitle.setText(description);

        ImageView backgroundImage = v.findViewById(R.id.backgroundImage);
        if (getActivity() != null)
            backgroundImage.setImageDrawable(ResourceUtils.getDrawable(getActivity(), drawable));

        return v;
    }


}