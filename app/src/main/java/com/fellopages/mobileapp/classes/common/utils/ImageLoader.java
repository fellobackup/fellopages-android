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

package com.fellopages.mobileapp.classes.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnImageLoadingListener;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageLoader {

    // Member Variables.
    private Context mContext;

    /***
     * Public constructor to initialize the context.
     *
     * @param context Context of calling class.
     */
    public ImageLoader(Context context) {
        mContext = context;
    }

    /***
     * Method to set default_user_profile image from url into ImageView.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setImageForUserProfile(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.default_user_profile))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_user_profile)
                    .into(imageView);
        }
    }

    /***
     * Method to set person_image_empty image from url into ImageView.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setPersonImageUrl(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.person_image_empty))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.person_image_empty)
                    .into(imageView);
        }
    }

    /***
     * Method to set Cover image from url into ImageView.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setCoverImageUrl(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {

            /* Set image fit center if default image is coming from api */
            if (url.contains(ConstantVariables.DEFAULT_IMAGE_PATH)) {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.gradient_bg))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.gradient_bg)
                    .into(imageView);
        }
    }

    /***
     * Method to set feed image from the given url.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setFeedImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {

            /* Set image fit center if default image is coming from api */
            if (url.contains(ConstantVariables.DEFAULT_IMAGE_PATH)) {
                setDefaultImageProperty(imageView);
            } else {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.color.grey_light))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.color.grey_light)
                    .into(imageView);
        }
    }

    private void setDefaultImageProperty(ImageView imageView) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        int padding = (int) mContext.getResources().getDimension(R.dimen.padding_1dp);
        int paddingTopBottom = (int) mContext.getResources().getDimension(R.dimen.padding_10dp);
        layoutParams.setMargins(padding, paddingTopBottom, padding, paddingTopBottom);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
    }


    /***
     * Method to set feed image from the given url.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     * @param width     Required width of image view.
     * @param height    Required height of image view.
     */
    public void setFeedImage(String url, ImageView imageView, int width, int height) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.override(width, height);
        requestOptions.placeholder(R.color.grey_light);
        requestOptions.error(R.color.grey_light);

        /* Set image fit center if default image is coming from api */
        if (url.contains(ConstantVariables.DEFAULT_IMAGE_PATH)) {
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.color.grey_light)
                    .into(imageView);
        }
    }

    /***
     * Method to set feed image with animation (If Gif) from the given url.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setFeedImageWithAnimation(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            try {
                Glide.with(mContext)
                        .setDefaultRequestOptions(getAnimateRequestOptions(R.color.grey_light))
                        .load(url)
                        .into(imageView);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } else {
            Glide.with(mContext)
                    .load(R.color.grey_light)
                    .into(imageView);
        }
    }

    /***
     * Method to set feed image from the given url.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setSingleFeedImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.color.white))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.color.white)
                    .into(imageView);
        }
    }

    /***
     * Method to set animation/gif image from the given url.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setAnimationImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getAnimateRequestOptions(R.drawable.background_image))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.background_image)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the grey color as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setGreyPlaceHolder(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.color.grey))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.color.grey)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the category icon as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setCategoryImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.category_icon))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.category_icon)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the listing icon as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setListingImageUrl(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.nophoto_listing_thumb_profile))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.nophoto_listing_thumb_profile)
                    .into(imageView);
        }
    }

    public void getBitMapFromUrl(String imageUrl, final BitMapCreatorUtil.OnBitmapLoadListener onBitmapLoadListener) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.dontTransform();

        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        if (onBitmapLoadListener != null && resource != null) {
                            onBitmapLoadListener.onBitMapLoad(resource);
                        }
                    }
                });
    }

    /***
     * Method to set image from the given url with the default error icon as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setAlbumPhoto(String url, ImageView imageView) {

        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.default_error))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_error)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the default default video thumbnail icon as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setVideoImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.default_video_thumbnail))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_video_thumbnail)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the default artwork icon as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setMusicImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.default_artwork_dark))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_artwork_dark)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the product icon as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setCartImageUrl(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.nophoto_product))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.nophoto_product)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the background_image as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setStickerImage(String url, ImageView imageView, JSONObject imageSize) {
        if (url != null && !url.isEmpty()) {
            int height = (int) mContext.getResources().getDimension(R.dimen.height_150dp);
            int width = height;
            if (imageSize != null) {
                float ratio = ((float)imageSize.optInt("width") / (float)imageSize.optInt("height"));
                width = (int) (height * ratio);
            }
            RequestOptions requestOptions = getRequestOptions(R.drawable.background_grey_border_rectangle);
            requestOptions.override(width, height);
            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.background_grey_border_rectangle)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the stickers_image_background as a placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setImageWithStickerBackground(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(R.drawable.stickers_image_background))
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.stickers_image_background)
                    .into(imageView);
        }
    }


    /***
     * Method to set image from the given url with blur effect.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setImageWithBlur(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            if (CacheUtils.getInstance(mContext).getLru().get(url) != null) {
                imageView.setImageBitmap(CacheUtils.getInstance(mContext).getLru().get(url));
            } else {
                new BlurredAsyncTask(imageView, url).execute();
            }
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_user_profile)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url without any placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setImageUrl(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {

            /* Set image fit center if default image is coming from api */
            if (url.contains(ConstantVariables.DEFAULT_IMAGE_PATH)) {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            Glide.with(mContext)
                    .setDefaultRequestOptions(getNoAnimationOptions())
                    .load(url)
                    .into(imageView);
        }
    }


    /***
     * Method to set image from the given url without any placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setProductCoverImage(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getNoAnimationOptions())
                    .load(url)
                    .into(imageView);
        }
    }

    /***
     * Method to set reaction image from the given url without any placeholder.
     *
     * @param url       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setReactionImageUrl(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.fitCenter();
            requestOptions.dontAnimate();
            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(url)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with the custom height and width.
     *
     * @param url       Url which contains user image.
     * @param width     Custom width of the image.
     * @param height    Custom height of the iamge.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void setResizeImageUrl(String url, int width, int height, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getRequestOptions(width, height))
                    .load(url)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with image laod listener to remove placeholder on success.
     *
     * @param url         Url which contains user image.
     * @param placeHolder Placeholder which needs to be shown and hide when image is loaded successfully.
     * @param imageView   Image view in which user image needs to be shown.
     */
    public void setImageWithListener(String url, final View placeHolder, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getNoAnimationOptions())
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            placeHolder.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_error)
                    .into(imageView);
        }
    }

    /***
     * Method to set image from the given url with image laod listener to remove placeholder on success.
     *
     * @param url         Url which contains user image.
     * @param placeHolder Placeholder which needs to be shown and hide when image is loaded successfully.
     * @param imageView   Image view in which user image needs to be shown.
     */
    public void setImageAnimationListener(String url, final View placeHolder, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getAnimateRequestOptions(R.drawable.ic_placeholder))
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            placeHolder.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_error)
                    .into(imageView);
        }
    }

    /**
     * Method to set image from the given file
     *
     * @param file        File object which contains the image.
     * @param imageWidth  Custom width of the image.
     * @param imageHeight Custom height of the image.
     * @param imageView   Image view in which user image needs to be shown.
     */
    public void setFileImage(File file, int imageWidth, int imageHeight, ImageView imageView) {
        if (file != null) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getFileRequestOptions(imageWidth, imageHeight))
                    .load(file)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_error)
                    .into(imageView);
        }
    }

    /**
     * Method to get Request Options with not animation.
     *
     * @return Returns the Request Options.
     */
    private RequestOptions getNoAnimationOptions() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        return requestOptions;
    }

    /**
     * Method to Create Animated Request Options with placeholder and error res.
     *
     * @param resId Resource id which is set as placeholder.
     * @return Returns the Request Options.
     */
    private RequestOptions getAnimateRequestOptions(int resId) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(resId);
        requestOptions.error(resId);
        return requestOptions;
    }

    /**
     * Method to Create Request Options with placeholder and error res with no animation.
     *
     * @param resId Resource id which is set as placeholder.
     * @return Returns the Request Options.
     */
    private RequestOptions getRequestOptions(int resId) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.placeholder(resId);
        requestOptions.error(resId);
        return requestOptions;
    }

    /**
     * Method to Create Request Options with custom width and height.
     *
     * @param width  Width of the image.
     * @param height Height if the image.
     * @return Returns the Request Options.
     */
    private RequestOptions getRequestOptions(int width, int height) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(width, height);
        requestOptions.dontAnimate();
        return requestOptions;
    }

    private RequestOptions getRequestOptions(int placeholder, int width, int height) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(placeholder);
        requestOptions.error(placeholder);
        requestOptions.override(width, height);
        requestOptions.dontAnimate();
        return requestOptions;
    }

    /**
     * Method to Create Request Options with placeholder and error res with no animation.
     *
     * @param resId Resource id which is set as placeholder.
     * @return Returns the Request Options.
     */
    private RequestOptions getRequestOptions(int resId,JSONObject imageSize) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.placeholder(resId);
        requestOptions.error(resId);
        requestOptions.override(imageSize.optInt("width"),imageSize.optInt("height"));
        return requestOptions;
    }


    /**
     * Method to create RequestOptions, when image is loaded from file.
     *
     * @param imageWidth  Width of the image.
     * @param imageHeight Height if the image.
     * @return Returns the Request Options.
     */
    private RequestOptions getFileRequestOptions(int imageWidth, int imageHeight) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.dontAnimate();
        requestOptions.placeholder(R.drawable.default_error);
        requestOptions.error(R.drawable.default_error);
        requestOptions.centerCrop();
        requestOptions.override(imageWidth, imageHeight);
        return requestOptions;
    }

    public void setStickerImageWithPlaceholder(String url, ImageView imageView) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .load(url)
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.ic_sticker_placeholder)
                    .into(imageView);
        }
    }

    public void setImageWithCallbackListener(String url, ImageView imageView, final OnImageLoadingListener onImageLoadingListener) {
        if (url != null && !url.isEmpty()) {
            Glide.with(mContext)
                    .setDefaultRequestOptions(getAnimateRequestOptions(R.drawable.ic_placeholder))
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            onImageLoadingListener.onLoadFailed();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            onImageLoadingListener.onResourceReady();
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.default_error)
                    .into(imageView);
        }
    }

    /***
     * Method to load Multiphotos reyclerview images via picasso from the given url with placeholder.
     *
     * @param imageUrl       Url which contains user image.
     * @param imageView Image view in which user image needs to be shown.
     */
    public void loadAlbumImageWithPicasso(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(mContext)
                    .load(imageUrl)
                    .placeholder(R.drawable.default_error)
                    .into(imageView);
        } else {
            Picasso.with(mContext)
                    .load(R.drawable.default_error)
                    .into(imageView);
        }
    }

    /*
     * Method to generate local video thumbnails
     * */

    public void loadVideoThumbnail(ImageView imageView, final String imageUrl, int width, int height) {
        Glide.with(mContext)
                .setDefaultRequestOptions(getRequestOptions(R.drawable.default_video_thumbnail, width, height))
                .asBitmap()
                .load(new File(imageUrl))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        //TODO
//                        if (resource != null) {
//                            CacheUtils.getInstance(mContext).getLru().put(imageUrl, resource);
//                        }
                        return false;
                    }
                })
                .into(imageView);
    }

    public class BlurredAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private ImageView ivMainImage;
        private String mUrl;

        public BlurredAsyncTask(ImageView iv, String url) {
            this.ivMainImage = iv;
            this.mUrl = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(mUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                if (bitmap == null) {
                    return null;
                }
                return BlurBuilder.blur(mContext, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                CacheUtils.getInstance(mContext).getLru().put(mUrl, result);
                ivMainImage.setImageBitmap(result);
            }
        }
    }

}
