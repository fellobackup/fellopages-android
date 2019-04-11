package com.fellopages.mobileapp.classes.common.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.activities.ViewGroupEvent;
import com.fellopages.mobileapp.classes.common.ui.CustomWebView;

public class WebViewFragment extends Fragment {

    private ViewGroupEvent parentActivity;
    public static WebViewFragment newInstance(Bundle bundle) {
        WebViewFragment fragment = new WebViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.web_view_fragment, container, false);
        parentActivity = (ViewGroupEvent) getActivity();
        CustomWebView mWebView = rootView.findViewById(R.id.webViewFragment);
        mWebView.setFragment(this);
        Bundle bundle = getArguments();
        if (bundle != null){

            WebSettings settings=mWebView.getSettings();
            mWebView.setWebChromeClient(new WebChromeClient());
            mWebView.setWebViewClient(new WebViewClient());
            settings.setJavaScriptEnabled(true);
            mWebView.setScrollContainer(false);
            mWebView.setVerticalScrollBarEnabled(false);
            mWebView.setHorizontalScrollBarEnabled(false);
            settings.setBuiltInZoomControls(true);
            settings.setSupportZoom(true);
            settings.setDisplayZoomControls(false);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            mWebView.loadUrl(bundle.getString("discussion_url"));


        }

        return rootView;
    }

    public void setViewPager(boolean b) {
        parentActivity.setViewPagerStatus(b);
    }
}
