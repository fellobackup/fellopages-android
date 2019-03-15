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

package com.fellopages.mobileapp.classes.modules.story.photofilter;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.adapters.CustomImageAdapter;
import com.fellopages.mobileapp.classes.common.interfaces.OnCancelClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnItemClickListener;
import com.fellopages.mobileapp.classes.common.interfaces.OnResponseListener;
import com.fellopages.mobileapp.classes.common.multimediaselector.MultiMediaSelectorActivity;
import com.fellopages.mobileapp.classes.common.ui.NonSwipeableViewPager;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.common.utils.UrlUtil;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.stickers.StickersUtil;
import com.fellopages.mobileapp.classes.modules.story.StoryCreate;
import com.fellopages.mobileapp.classes.modules.story.photofilter.colorFilter.ThumbnailCallback;
import com.zomato.photofilters.imageprocessors.Filter;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ja.burhanrashid52.photoeditor.PhotoEditor;

public class PhotoEditActivity extends AppCompatActivity implements
        View.OnClickListener, ThumbnailCallback {

    private AppConstant mAppConst;
    private int NUM_OF_COLUMN = 6;
    private Context mContext;
    private Activity activity;
    private ArrayList<String> mSelectPath, mSelectImageDescription;
    private String videoPath, storyType;
    private int columnWidth, width, recentSelected = 0;
    private CustomImageAdapter mCustomImageAdapter;
    public static RecyclerView mRecyclerViewList;
    private NonSwipeableViewPager nsViewPager;
    public static JSONObject mStickerResponse;
    private EditText mCaptionEdit;
    public static RelativeLayout mCaptionView;
    public static TextView mTapFilter;
    public ImageView sendButton;
    public ArrayList<Fragment> fragmentArrayList;
    private Intent storyIntent;
    private StickerResponseListener mStickerResponseListener;
    private MyPagerAdapter adapterViewPager;
    public static String mVideoThumb = "";
    public int duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        mContext = activity = this;
        mAppConst = new AppConstant(mContext);
        mSelectImageDescription = new ArrayList<>();

        selectMediaFromGallery();
    }

    private void selectMediaFromGallery() {
        Intent intent = new Intent(mContext, MultiMediaSelectorActivity.class);
        // Selection type photo/video to display items in grid
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECTION_TYPE, MultiMediaSelectorActivity.SELECTION_PHOTO_VIDEO);
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiMediaSelectorActivity.IS_STORY_POST, true);
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_COUNT, ConstantVariables.STORY_POST_COUNT_LIMIT);

        // Select mode
        intent.putExtra(MultiMediaSelectorActivity.EXTRA_SELECT_MODE, MultiMediaSelectorActivity.MODE_MULTI);
        startActivityForResult(intent, ConstantVariables.REQUEST_STORY_IMAGE_VIDEO);
    }

    /**
     * Class to load the images in background thread
     * so that it will load the images in background and don't make the main thread slow.
     */
    public class LoadImageAsync extends AsyncTask<Void,String,Void> {

        private ProgressDialog mProgressDialog;
        private RecyclerView mRecyclerView;
        private List<ImageViewList> mPhotoUrls = new ArrayList<>();

        public LoadImageAsync(RecyclerView recyclerView, boolean isPhotoPreview) {
            this.mRecyclerView = recyclerView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage(getApplicationContext().getResources().
                    getString(R.string.loading_text));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                            LinearLayoutManager.HORIZONTAL, false));

                    for(int i = 0; i < mSelectPath.size(); i++) {
                        int selectItem = 0;
                        if (i == 0) {
                            selectItem = 1;
                        }
                        // Getting Bitmap from its real path.
                        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromFile(mContext, mSelectPath.get(i), width,
                                (int) getResources().getDimension(R.dimen.feed_attachment_image_height), false);
                        if (bitmap != null) {
                            mPhotoUrls.add(new ImageViewList(selectItem, bitmap));
                        }

                        mSelectImageDescription.add(i, "");
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            mProgressDialog.dismiss();
            mCustomImageAdapter = new CustomImageAdapter(activity, mPhotoUrls, columnWidth, new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    addCaptionIntoList(recentSelected);
                    setCaptionInView(position);
                    recentSelected = position;
                    nsViewPager.setCurrentItem(position);
                }
            }, new OnCancelClickListener() {
                @Override
                public void onCancelButtonClicked(int removedImage) {
                    if (mSelectPath != null && !mSelectPath.isEmpty()) {
                        mSelectPath.remove(removedImage);
                        mPhotoUrls.remove(removedImage);
                        mCustomImageAdapter.notifyDataSetChanged();
                        synchronized (nsViewPager) {
                            nsViewPager.notify();
                        }
                        adapterViewPager.notifyDataSetChanged();
                            if (mSelectPath.isEmpty()) {
                                finish();
                            }  else {
                                if (recentSelected >= removedImage) {
                                    recentSelected--;
                                }
                                nsViewPager = findViewById(R.id.view_pager);
                                adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
                                nsViewPager.setAdapter(adapterViewPager);
                                nsViewPager.setOffscreenPageLimit(adapterViewPager.getCount() + 1);
                                nsViewPager.setCurrentItem(recentSelected);
                            }
                    }
                }
            });
            mRecyclerView.setAdapter(mCustomImageAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);

            getStickers();
        }
    }

    private void getStickers() {
        mAppConst.getJsonResponseFromUrl(UrlUtil.AAF_VIEW_STICKERS_URL, new OnResponseListener() {
            @Override
            public void onTaskCompleted(JSONObject jsonObject) {
                mStickerResponse = jsonObject;
                mStickerResponseListener.onStickerResponse(jsonObject);
            }

            @Override
            public void onErrorInExecutingTask(String message, boolean isRetryOption) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (fragmentArrayList != null) {
            PhotoFilterFragment fragment = (PhotoFilterFragment) fragmentArrayList.get(recentSelected);

            if (PhotoFilterFragment.isFilterDisplayed) {
                PhotoFilterFragment.isFilterDisplayed = false;
                PhotoFilterFragment.isEditToolbarDisplay = false;
                fragment.imgColorFilter.setVisibility(View.GONE);
                PhotoEditActivity.mTapFilter.setVisibility(View.VISIBLE);
                PhotoEditActivity.mCaptionView.setVisibility(View.VISIBLE);
                PhotoEditActivity.mRecyclerViewList.setVisibility(View.VISIBLE);

            } else if (PhotoFilterFragment.isStickerViewDisplayed) {
                PhotoFilterFragment.isStickerViewDisplayed = false;
                StickersUtil.showStickerViewForFilter(true);
                fragment.mEditToolbar.setVisibility(View.VISIBLE);
                Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
                fragment.mEditToolbar.startAnimation(slideUp);

            } else if (PhotoFilterFragment.isEditToolbarDisplay) {
                PhotoFilterFragment.isEditToolbarDisplay = false;
                fragment.mEditToolbar.setVisibility(View.GONE);
                PhotoEditActivity.mTapFilter.setVisibility(View.VISIBLE);
                PhotoEditActivity.mRecyclerViewList.setVisibility(View.VISIBLE);
                PhotoEditActivity.mCaptionView.setVisibility(View.VISIBLE);

            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    public interface StickerResponseListener{
        void onStickerResponse(JSONObject jsonObject);
    }

    public void setStickerResponseListener(StickerResponseListener stickerResponseListener) {
        mStickerResponseListener = stickerResponseListener;
    }


    // Get photo caption from list and show it in caption view
    private void setCaptionInView(int position) {
        mCaptionEdit.setText(mSelectImageDescription.get(position));
        mCaptionEdit.setSelection(mSelectImageDescription.get(position).length());
    }

    // Add photo caption in there respective position
    private void addCaptionIntoList(int pos) {
        if (mCaptionEdit.getText() != null && !mCaptionEdit.getText().toString().isEmpty()) {
            mSelectImageDescription.set(pos, mCaptionEdit.getText().toString());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sendButton) {
            if (storyType.equals("image")) {
                if (!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    mAppConst.requestForManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            ConstantVariables.WRITE_EXTERNAL_STORAGE);
                } else {
                    saveImage();
                }
            } else {
                redirectToStoryCreateClass();
            }
        }
    }

    int counter;
    private void saveImage() {
        counter = fragmentArrayList.size();
        for (int i = 0; i < fragmentArrayList.size(); i++) {
            PhotoFilterFragment fragment = (PhotoFilterFragment) fragmentArrayList.get(i);

            // If user do any editing on image then create a bitmap and get path
            if (fragment.btnSkip.getVisibility() == View.VISIBLE) {
                saveImage(fragment.mPhotoEditor, i, counter);
            } else {
                counter--;
                if (counter == 0) {
                    mAppConst.hideProgressDialog();
                    redirectToStoryCreateClass();
                }
            }
        }
    }

    public void saveImage(PhotoEditor photoEditor, final int  position, int count) {
        mAppConst.showProgressDialog();
        if (mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + ""
                    + System.currentTimeMillis() + ".png");
            try {
                file.createNewFile();
                photoEditor.saveImage(file.getAbsolutePath(), new PhotoEditor.OnSaveListener() {
                    @Override
                    public void onSuccess(@NonNull String imagePath) {
                        mSelectPath.set(position, imagePath);
                        counter--;
                        if (counter == 0) {
                            mAppConst.hideProgressDialog();
                            redirectToStoryCreateClass();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        counter--;
                        mAppConst.hideProgressDialog();
                        SnackbarUtils.displaySnackbar(nsViewPager, "Failed to save image");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void redirectToStoryCreateClass() {
        if (storyType.equals("video")) {
            mSelectImageDescription.add(0, mCaptionEdit.getText().toString());
        } else {
            mSelectImageDescription.set(recentSelected, mCaptionEdit.getText().toString());
        }
        Intent intent = new Intent(this, StoryCreate.class);
        intent.putExtra(MultiMediaSelectorActivity.VIDEO_RESULT, videoPath);
        intent.putExtra(ConstantVariables.STORY_DURATION, duration);
        intent.putExtra(ConstantVariables.STORY_VIDEO_THUMB, mVideoThumb);
        intent.putStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT, mSelectPath);
        intent.putStringArrayListExtra(ConstantVariables.STORY_DESCRIPTION, mSelectImageDescription);
        startActivityForResult(intent, ConstantVariables.REQUEST_STORY_POST);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public void finish() {
        if (storyIntent != null) {
            setResult(ConstantVariables.REQUEST_STORY_POST, storyIntent);
        }  else {
            Intent data = new Intent();
            setResult(ConstantVariables.STORY_VIEW_PAGE_CODE, data);
        }
        super.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ConstantVariables.REQUEST_STORY_POST:
                    if (data != null) {
                        storyIntent = data;
                        setResult(ConstantVariables.REQUEST_STORY_POST, storyIntent);
                        finish();
                    }
                    break;

                case ConstantVariables.REQUEST_STORY_IMAGE_VIDEO:
                    loadImageVideosDataInView(data);
                    break;
            }
        } else if (requestCode == ConstantVariables.REQUEST_STORY_IMAGE_VIDEO){
            setResult(ConstantVariables.REQUEST_STORY_POST, data);
            finish();
        }
    }

    private void loadImageVideosDataInView(Intent intent) {
        mSelectPath = intent.getStringArrayListExtra(MultiMediaSelectorActivity.EXTRA_RESULT);
        videoPath = intent.getStringExtra(MultiMediaSelectorActivity.VIDEO_RESULT);

        long length = 0;
        if (videoPath != null && !videoPath.isEmpty()) {

            duration = MediaPlayer.create(mContext, Uri.fromFile(new File(videoPath))).getDuration()/1000;
            storyType = "video";
            File file = new File(videoPath);
            long size = file.length();
            length = (int)size/(1024 * 1024);
        } else if (mSelectPath != null && mSelectPath.size() > 0) {
            storyType = "image";
            for (int i = 0; i < mSelectPath.size(); ++i) {
                File file = new File(mSelectPath.get(i));
                long size = file.length();
                length += (int)size/(1024 * 1024);
            }
        }
        String message = ConstantVariables.VALID_FILE_SIZE;
        if ((message = GlobalFunctions.validateFileSize(length, mContext)).equals(ConstantVariables.VALID_FILE_SIZE)) {

            mCaptionView = findViewById(R.id.captionView);
            mCaptionEdit = findViewById(R.id.captionEdit);
            mTapFilter = findViewById(R.id.tap_filter);
            sendButton = findViewById(R.id.sendButton);
            sendButton.setOnClickListener(this);

            mTapFilter.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
            mTapFilter.setText("\uf106" + "\n" + mContext.getResources().getString(R.string.tap_filter_text));

            nsViewPager = findViewById(R.id.view_pager);
            adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
            nsViewPager.setAdapter(adapterViewPager);
            nsViewPager.setOffscreenPageLimit(adapterViewPager.getCount() + 1);

            mRecyclerViewList = findViewById(R.id.recycler_view_list);
            if (storyType.equals("image")) {

                width = AppConstant.getDisplayMetricsWidth(mContext);
                InitializeColumnWidth(NUM_OF_COLUMN);

                new LoadImageAsync(mRecyclerViewList, true).execute();

            } else {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, 0, 0,
                        (int) mContext.getResources().getDimension(R.dimen.margin_10dp));
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mCaptionView.setLayoutParams(layoutParams);
                mTapFilter.setVisibility(View.GONE);
                mRecyclerViewList.setVisibility(View.GONE);
                mSelectImageDescription.add(0, "");
            }
        } else {
            SnackbarUtils.displayMultiLineSnackbarWithAction(mContext, findViewById(android.R.id.content), message, mContext.getResources().
                    getString(R.string.try_again), new SnackbarUtils.OnSnackbarActionClickListener() {

                @Override
                public void onSnackbarActionClick() {
                    selectMediaFromGallery();
                }
            });
        }
    }

    @Override
    public void onThumbnailClick(Filter filter) {
        PhotoFilterFragment fragment = (PhotoFilterFragment) fragmentArrayList.get(recentSelected);
        fragment.onThumbnailClick(filter);
    }


    class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            fragmentArrayList = new ArrayList<>();
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            if (storyType.equals("image")) {
                return mSelectPath.size();
            } else {
                return 1;
            }
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            String image;
            if (storyType.equals("image")) {
                image = mSelectPath.get(position);
            } else {
                image = videoPath;
            }

            Bundle bundle = new Bundle();
            bundle.putString(ConstantVariables.IMAGE, image);
            bundle.putString("type", storyType);

            if (mStickerResponse != null)
                bundle.putString(ConstantVariables.RESPONSE_OBJECT, mStickerResponse.toString());

            Fragment returnFragment = new PhotoFilterFragment();
            returnFragment.setArguments(bundle);
            fragmentArrayList.add(position, returnFragment);

            return returnFragment;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void InitializeColumnWidth(int numColumn) {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                AppConstant.GRID_PADDING, r.getDisplayMetrics());

        // Column width
        columnWidth = (int) ((mAppConst.getScreenWidth() - ((10 + 1) * padding)) /
                numColumn);
    }

}
