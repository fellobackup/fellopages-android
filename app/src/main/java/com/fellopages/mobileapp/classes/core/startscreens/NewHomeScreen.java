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
 *
 */

package com.fellopages.mobileapp.classes.core.startscreens;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.core.NewLoginActivity;


public class NewHomeScreen extends FragmentActivity implements View.OnClickListener {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private TextView btnSkip, btnNext;
    private Context mContext;
    private boolean isCircleColorSet = false;
    private int[] colorsActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_home_screen_new);
        mContext = this;

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);

        // adding bottom dots
        addBottomCircleIndicator(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void addBottomCircleIndicator(int currentPage) {

        if (!isCircleColorSet) {
            colorsActive = new int[ConstantVariables.TOTAL_INTRO_SLIDESHOW_IMAGES];
            dots = new TextView[ConstantVariables.TOTAL_INTRO_SLIDESHOW_IMAGES];
            dotsLayout.removeAllViews();

            for (int i = 0; i < dots.length; i++) {
                switch (i) {
                    case 0:
                        colorsActive[0] = R.color.bg_slider_screen1;
                        break;

                    case 1:
                        colorsActive[1] = R.color.bg_slider_screen2;
                        break;

                    case 2:
                        colorsActive[2] = R.color.bg_slider_screen3;
                        break;

                    case 3:
                        colorsActive[3] = R.color.bg_slider_screen4;
                        break;

                    case 4:
                        colorsActive[4] = R.color.bg_slider_screen5;
                        break;

                    case 5:
                        colorsActive[5] = R.color.bg_slider_screen6;
                        break;

                    case 6:
                        colorsActive[6] = R.color.bg_slider_screen7;
                        break;
                }

                dots[i] = new TextView(this);
                dots[i].setText(Html.fromHtml("&#8226;"));
                dots[i].setGravity(Gravity.CENTER);
                dots[i].setTextSize(getResources().getDimension(R.dimen.body_default_font_size));
                dotsLayout.addView(dots[i]);
            }

            if (ConstantVariables.TOTAL_INTRO_SLIDESHOW_IMAGES <= 1) {
                dotsLayout.setVisibility(View.GONE);
                setViewForLogin();
            }

            isCircleColorSet = true;
        }

        if (ConstantVariables.TOTAL_INTRO_SLIDESHOW_IMAGES > 0 && colorsActive != null) {
            for (int i = 0; i < ConstantVariables.TOTAL_INTRO_SLIDESHOW_IMAGES; i++ ) {
                int color = 0;
                if (i != currentPage) {
                    color = R.color.transparent_white;
                } else {
                    color = R.color.transparent_black;
                }
                dots[i].setTextColor(ContextCompat.getColor(mContext, color));
            }
        }
    }


    //TODO
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = (int) (Color.red(color) * factor);
        int g = (int) (Color.green(color) * factor);
        int b = (int) (Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r,255),
                Math.min(g,255),
                Math.min(b,255));
    }

    //TODO
    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    private void launchLoginScreen() {
        startActivity(new Intent(NewHomeScreen.this, NewLoginActivity.class));
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomCircleIndicator(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == ConstantVariables.TOTAL_INTRO_SLIDESHOW_IMAGES - 1) {
                btnSkip.setVisibility(View.GONE);
                setViewForLogin();
            } else {
                int padding10 = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);
                // still pages are left
                btnNext.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.ic_navigate_next_white_24dp), null, null, null);
                btnSkip.setVisibility(View.VISIBLE);
                btnNext.setText("");
                btnNext.setPadding(padding10, padding10, padding10, padding10);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void setViewForLogin() {
        int padding15 = (int) mContext.getResources().getDimension(R.dimen.padding_15dp);
        // last page. make button text to GOT IT
        btnNext.setText(getString(R.string.start));
        btnNext.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        btnNext.setPadding(padding15, padding15, padding15, padding15);
    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends android.support.v4.view.PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.home_screen_slide, container, false);
            container.addView(view);

            TextView tvTitle = view.findViewById(R.id.slide_title);
            ImageView ivSlideIcon = view.findViewById(R.id.slide_icon);
            TextView tvDescription = view.findViewById(R.id.slide_description);

            String title = "", description = "";
            Drawable drawable = null;
            int bgColor = 0, textColor = 0;
            switch (position) {
                case 0:
                    title = getResources().getString(R.string.first_slide_title);
                    description = getResources().getString(R.string.first_slide_subtitle);
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.slide_screen_1);
                    bgColor = R.color.bg_slider_screen1;
                    textColor = R.color.slide_1_text_color;
                    break;

                case 1:
                    title = getResources().getString(R.string.second_slide_title);
                    description = getResources().getString(R.string.second_slide_subtitle);
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.slide_screen_2);
                    bgColor = R.color.bg_slider_screen2;
                    textColor = R.color.slide_2_text_color;
                    break;

                case 2:
                    title = getResources().getString(R.string.third_slide_title);
                    description = getResources().getString(R.string.third_slide_subtitle);
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.slide_screen_3);
                    bgColor = R.color.bg_slider_screen3;
                    textColor = R.color.slide_3_text_color;
                    break;

                case 3:
                    title = getResources().getString(R.string.fourth_slide_title);
                    description = getResources().getString(R.string.fourth_slide_subtitle);
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.slide_screen_4);
                    bgColor = R.color.bg_slider_screen4;
                    textColor = R.color.slide_4_text_color;
                    break;

                case 4:
                    title = getResources().getString(R.string.fifth_slide_title);
                    description = getResources().getString(R.string.fifth_slide_subtitle);
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.slide_screen_5);
                    bgColor = R.color.bg_slider_screen5;
                    textColor = R.color.slide_5_text_color;
                    break;

                case 5:
                    title = getResources().getString(R.string.sixth_slide_title);
                    description = getResources().getString(R.string.sixth_slide_subtitle);
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.slide_screen_6);
                    bgColor = R.color.bg_slider_screen6;
                    textColor = R.color.slide_6_text_color;
                    break;

                case 6:
                    title = getResources().getString(R.string.seventh_slide_title);
                    description = getResources().getString(R.string.seventh_slide_subtitle);
                    drawable = ContextCompat.getDrawable(mContext, R.drawable.slide_screen_7);
                    bgColor = R.color.bg_slider_screen7;
                    textColor = R.color.slide_7_text_color;
                    break;
            }

            view.setBackgroundColor(ContextCompat.getColor(mContext, bgColor));
            tvTitle.setText(title);
            tvTitle.setTextColor(ContextCompat.getColor(mContext, textColor));
            tvDescription.setText(description);
            tvDescription.setTextColor(ContextCompat.getColor(mContext, textColor));
            ivSlideIcon.setImageDrawable(drawable);

            return view;
        }

        @Override
        public int getCount() {
            return ConstantVariables.TOTAL_INTRO_SLIDESHOW_IMAGES;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_skip:
                launchLoginScreen();
                break;

            case R.id.btn_next:
                // checking for last page
                // if last page login screen will be launched
                int current = getItem(+1);
                if (current < ConstantVariables.TOTAL_INTRO_SLIDESHOW_IMAGES) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchLoginScreen();
                }
                break;
        }
    }

}
