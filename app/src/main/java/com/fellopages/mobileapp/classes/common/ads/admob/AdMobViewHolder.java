package com.fellopages.mobileapp.classes.common.ads.admob;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;

import java.util.List;

/**
 * Created by root on 10/6/16.
 */
public class AdMobViewHolder extends RecyclerView.ViewHolder {
    public View mAdView;

    public AdMobViewHolder(View itemView) {
        super(itemView);
        mAdView = itemView;
    }

    public static void inflateAd(Context context, NativeAppInstallAd appInstallAd, View view){

        NativeAppInstallAdView adView = (NativeAppInstallAdView) view.findViewById(R.id.native_view);
        ImageView mAdIcon = (ImageView)view.findViewById(R.id.appinstall_app_icon);
        ImageView cardImage = (ImageView)view.findViewById(R.id.appinstall_image_card);
        ImageView mAdImage = (ImageView) view.findViewById(R.id.appinstall_image);
        TextView mAdHeadLine = (TextView) view.findViewById(R.id.appinstall_headline);
        TextView mAdBody = (TextView)view.findViewById(R.id.appinstall_body);
        Button mInstallButton = (Button)view.findViewById(R.id.appinstall_call_to_action);
        RatingBar mAdRating = (RatingBar) view.findViewById(R.id.appinstall_stars);

        ImageLoader imageLoader = new ImageLoader(context);
        if(mAdIcon != null && appInstallAd.getIcon().getUri() != null && !appInstallAd.getIcon().getUri().toString().isEmpty()) {
            imageLoader.setImageUrl(appInstallAd.getIcon().getUri().toString(), mAdIcon);
        }

        List<NativeAd.Image> images = appInstallAd.getImages();
        if (images.size() > 0 && mAdImage != null &&
                images.get(0).getUri() != null && !images.get(0).getUri().toString().isEmpty()) {
            imageLoader.setImageUrl(images.get(0).getUri().toString(), mAdImage);
        }
        if(images.size() > 0 && cardImage != null &&
                images.get(0).getUri() != null && !images.get(0).getUri().toString().isEmpty()){
            imageLoader.setImageUrl(images.get(0).getUri().toString(), cardImage);
        }
        if(mAdHeadLine != null) {
            mAdHeadLine.setText(appInstallAd.getHeadline());
        }

        if(mAdBody != null) {
            mAdBody.setText(appInstallAd.getBody());
        }

        if(mAdRating != null) {
            mAdRating.setRating(appInstallAd.getStarRating().floatValue());
        }
        if(mInstallButton != null) {
            mInstallButton.setText(appInstallAd.getCallToAction());
        }

        adView.setCallToActionView(adView);
        adView.setNativeAd(appInstallAd);


    }

    public static void inflateAd(Context context, NativeContentAd appInstallAd, View view){

        NativeContentAdView adView = (NativeContentAdView) view.findViewById(R.id.native_view);
        ImageView mAdIcon = (ImageView)view.findViewById(R.id.contentad_logo);
        ImageView mAdImage = (ImageView) view.findViewById(R.id.contentad_image);
        TextView mAdHeadLine = (TextView) view.findViewById(R.id.contentad_headline);
        TextView mAdBody = (TextView)view.findViewById(R.id.contentad_body);
        TextView mAdStore =(TextView) view.findViewById(R.id.contentad_advertiser);
        Button mInstallButton = (Button)view.findViewById(R.id.contentad_call_to_action);

        ImageLoader imageLoader = new ImageLoader(context);

        if(mAdIcon != null && appInstallAd.getLogo().getUri() != null && !appInstallAd.getLogo().getUri().toString().isEmpty()) {
            imageLoader.setImageUrl(appInstallAd.getLogo().getUri().toString(), mAdIcon);
        }

        List<NativeAd.Image> images = appInstallAd.getImages();
        if (images.size() > 0 && mAdImage != null &&
                images.get(0).getUri() != null && !images.get(0).getUri().toString().isEmpty()) {
            imageLoader.setImageUrl(images.get(0).getUri().toString(), mAdImage);
        }
        if(mAdHeadLine != null) {
            mAdHeadLine.setText(appInstallAd.getHeadline());
        }

        if(mAdBody != null) {
            mAdBody.setText(appInstallAd.getBody());
        }
        if(mAdStore != null) {
            mAdStore.setText(appInstallAd.getAdvertiser());
        }

        if(mInstallButton != null) {
            mInstallButton.setText(appInstallAd.getCallToAction());
            adView.setCallToActionView(mInstallButton);
        }

        adView.setNativeAd(appInstallAd);
    }
}
