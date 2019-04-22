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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.interfaces.OnViewTouchListener;
import com.fellopages.mobileapp.classes.common.utils.BitMapCreatorUtil;
import com.fellopages.mobileapp.classes.common.utils.BitmapUtils;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.ImageLoader;
import com.fellopages.mobileapp.classes.common.utils.ImageViewList;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;
import com.fellopages.mobileapp.classes.modules.stickers.StickersClickListener;
import com.fellopages.mobileapp.classes.modules.stickers.StickersPopup;
import com.fellopages.mobileapp.classes.modules.stickers.StickersUtil;
import com.fellopages.mobileapp.classes.modules.story.photofilter.colorFilter.ThumbnailCallback;
import com.fellopages.mobileapp.classes.modules.story.photofilter.colorFilter.ThumbnailItem;
import com.fellopages.mobileapp.classes.modules.story.photofilter.colorFilter.ThumbnailsAdapter;
import com.fellopages.mobileapp.classes.modules.story.photofilter.colorFilter.ThumbnailsManager;
import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;


public class PhotoFilterFragment extends Fragment implements OnPhotoEditorListener,
        View.OnClickListener, PropertiesBSFragment.Properties, EmojiBSFragment.EmojiListener,
        ThumbnailCallback, PhotoEditActivity.StickerResponseListener {

    /* Load native image processor library for photo editor */
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    private View mRootView;
    public PhotoEditor mPhotoEditor;
    private PhotoEditorView mPhotoEditorView;
    private PropertiesBSFragment mPropertiesBSFragment;
    private EmojiBSFragment mEmojiBSFragment;
    public TextView btnSkip;
    public RelativeLayout imgColorFilter;
    public RelativeLayout mStickersParentView;
    private RecyclerView thumbListView;
    private Bitmap selectedBitmap;
    private Context mContext;
    private StickersPopup mStickersPopup;
    private AppConstant mAppConst;
    public LinearLayout llSticker, mEditToolbar;
    private boolean isFilterApplied = false, isPencilSelected = false, isEraserSelected = false;
    private ImageView imgPencil;
    private ImageView imgEraser;
    private ImageView imgText;
    private ImageView imgFilter;
    private ImageView imgSticker;
    private ImageView imgEmo;
    private TextView imgUndo, imgRedo;
    List<Object> mPhotoEditorList;
    private static JSONObject mStickerResponse;
    public static boolean isFilterDisplayed = false, isStickerViewDisplayed = false, isEditToolbarDisplay = false;

    public static Fragment newInstance(int i, String s) {
        return null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView =  inflater.inflate(R.layout.photo_filter_view, container, false);

        mContext = getContext();
        mAppConst = new AppConstant(mContext);
        mPhotoEditorList = new ArrayList<>();

        String mImagePath = getArguments().getString(ConstantVariables.IMAGE);
        String storyType = getArguments().getString("type", "image");

        mPhotoEditorView = mRootView.findViewById(R.id.photoEditorView);
        mEditToolbar = mRootView.findViewById(R.id.editing_tools);
        ImageView ivPlayIcon = mRootView.findViewById(R.id.play_icon);
        final ImageView ivVideoThumbnail = mRootView.findViewById(R.id.video_view);

        if (storyType.equals("image")) {

            initPhotoEditorViews();

            PhotoEditActivity activity = (PhotoEditActivity)getActivity();
            activity.setStickerResponseListener(this);

            mPropertiesBSFragment = new PropertiesBSFragment();
            mEmojiBSFragment = new EmojiBSFragment();

            mEmojiBSFragment.setEmojiListener(this);
            mPropertiesBSFragment.setPropertiesChangeListener(this);
            mPhotoEditorView.setClickable(true);

            ivVideoThumbnail.setVisibility(View.GONE);
            ivPlayIcon.setVisibility(View.GONE);
            mPhotoEditorView.setVisibility(View.VISIBLE);

            selectedBitmap = BitmapUtils.decodeSampledBitmapFromFile(mContext,
                    mImagePath, AppConstant.getDisplayMetricsWidth(mContext),
                    (int) getResources().getDimension(R.dimen.feed_attachment_image_height), false);
            mPhotoEditorView.getSource().setImageBitmap(selectedBitmap);

            mPhotoEditor = new PhotoEditor.Builder(mContext, mPhotoEditorView)
                    .setPinchTextScalable(true) // set flag to make text scalable when pinch
                    .build(); // build photo editor sdk

            mPhotoEditor.setOnPhotoEditorListener(this);

        } else {
            mEditToolbar.setVisibility(View.GONE);
            mPhotoEditorView.setVisibility(View.GONE);
            ivVideoThumbnail.setVisibility(View.VISIBLE);
            ivPlayIcon.setVisibility(View.VISIBLE);

            final ImageLoader mImageLoader = new ImageLoader(mContext);

            mImageLoader.getBitMapFromUrl(mImagePath, new BitMapCreatorUtil.OnBitmapLoadListener() {
                @Override
                public void onBitMapLoad(Bitmap bitmap) {
                    try {
                        File outputDir = mContext.getCacheDir();
                        Random generator = new Random();
                        int n = 10000;
                        n = generator.nextInt(n);
                        String fileName = "Image-" + n ;
                        File outputFile = File.createTempFile(fileName, ".png", outputDir);
                        FileOutputStream out = new FileOutputStream(outputFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        PhotoEditActivity.mVideoThumb = outputFile.getAbsolutePath();

                        mImageLoader.setVideoImage(PhotoEditActivity.mVideoThumb, ivVideoThumbnail);

                        mAppConst.hideProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        return mRootView;
    }

    private void createStickerPopup() {
        if (mStickerResponse != null) {
            if (mStickersPopup == null) {
                mStickersPopup = StickersUtil.createStickersPopup(mContext,
                        mRootView.findViewById(R.id.main_content),
                        mStickersParentView, llSticker, mStickerResponse);
            }
            isStickerViewDisplayed = true;
            StickersUtil.showStickerViewForFilter(false);

            mStickersPopup.setOnStickerClickedListener(new StickersClickListener() {
                @Override
                public void onStickerClicked(ImageViewList stickerItem) {

                    if (stickerItem.getmGridViewImageUrl() != null
                            && !stickerItem.getmGridViewImageUrl().isEmpty()) {

                        isStickerViewDisplayed = false;
                        StickersUtil.showStickerViewForFilter(true);
                        mAppConst.showProgressDialog();
                        new BitMapCreatorUtil(mContext, stickerItem.getmGridViewImageUrl(), new BitMapCreatorUtil.OnBitmapLoadListener() {
                            @Override
                            public void onBitMapLoad(Bitmap bitmap) {
                                mAppConst.hideProgressDialog();
                                mPhotoEditor.addImage(bitmap);
                                mEditToolbar.setVisibility(View.VISIBLE);
                            }
                        }).execute();
                    }
                }
            });
        }
    }

    private void initPhotoEditorViews() {

        RelativeLayout mMainView = mRootView.findViewById(R.id.main_content);
        imgColorFilter = mRootView.findViewById(R.id.image_color_filter);
        llSticker = mRootView.findViewById(R.id.sticker_popup);
        mStickersParentView = mRootView.findViewById(R.id.stickersMainLayout);
        mStickersParentView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black_overlay));

        btnSkip = mRootView.findViewById(R.id.btnSkip);
        btnSkip.setOnClickListener(this);

        imgFilter = mRootView.findViewById(R.id.imgFilter);
        imgFilter.setOnClickListener(this);

        imgEmo = mRootView.findViewById(R.id.imgEmoji);
        imgEmo.setOnClickListener(this);

        imgSticker = mRootView.findViewById(R.id.imgSticker);
        imgSticker.setOnClickListener(this);

        imgPencil = mRootView.findViewById(R.id.imgPencil);
        imgPencil.setOnClickListener(this);

        imgText = mRootView.findViewById(R.id.imgText);
        imgText.setOnClickListener(this);

        imgEraser = mRootView.findViewById(R.id.btnEraser);
        imgEraser.setOnClickListener(this);

        imgUndo = mRootView.findViewById(R.id.imgUndo);
        imgUndo.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        imgUndo.setText("\uf0e2");
        imgUndo.setOnClickListener(this);

        imgRedo = mRootView.findViewById(R.id.imgRedo);
        imgRedo.setTypeface(GlobalFunctions.getFontIconTypeFace(mContext));
        imgRedo.setText("\uf01e");
        imgRedo.setOnClickListener(this);

        mPhotoEditorView.setOnTouchListener(new OnViewTouchListener(mContext) {
            @Override
            public void onTopToBottomSwipe() {
                setViewVisibilityOnEditorBackActions();
            }

            @Override
            public void onTouchRelease() {
                setViewVisibilityOnEditorBackActions();
            }

            @Override
            public void onBottomToTopSwipe() {
                setViewVisibilityOnBottomToTopSwipe();
            }
        });


        mMainView.setOnTouchListener(new OnViewTouchListener(mContext) {
            @Override
            public void onTopToBottomSwipe() {
                setViewVisibilityOnEditorBackActions();
            }

            @Override
            public void onTouchRelease() {
                setViewVisibilityOnEditorBackActions();
            }

            @Override
            public void onBottomToTopSwipe() {
                setViewVisibilityOnBottomToTopSwipe();
            }
        });

    }


    private void setViewVisibilityOnEditorBackActions() {
        if (isFilterDisplayed) {
            imgColorFilter.setVisibility(View.GONE);
            isFilterDisplayed = false;
            isEditToolbarDisplay = false;
            PhotoEditActivity.mTapFilter.setVisibility(View.VISIBLE);
            PhotoEditActivity.mCaptionView.setVisibility(View.VISIBLE);
            PhotoEditActivity.mRecyclerViewList.setVisibility(View.VISIBLE);

        } else if (isStickerViewDisplayed) {
            StickersUtil.showStickerViewForFilter(true);
            isStickerViewDisplayed = false;
            llSticker.setVisibility(View.GONE);
            mEditToolbar.setVisibility(View.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
            mEditToolbar.startAnimation(slideUp);

        } else if (isEditToolbarDisplay) {
            isEditToolbarDisplay = false;
            mEditToolbar.setVisibility(View.GONE);
            PhotoEditActivity.mTapFilter.setVisibility(View.VISIBLE);
            PhotoEditActivity.mRecyclerViewList.setVisibility(View.VISIBLE);
            PhotoEditActivity.mCaptionView.setVisibility(View.VISIBLE);
        }
    }

    private void setViewVisibilityOnBottomToTopSwipe() {
        mAppConst.hideKeyboard();
        isEditToolbarDisplay = true;
        mEditToolbar.setVisibility(View.VISIBLE);

        // Set slide up animation on view visible
        Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
        mEditToolbar.startAnimation(slideUp);

        PhotoEditActivity.mTapFilter.setVisibility(View.GONE);
        PhotoEditActivity.mRecyclerViewList.setVisibility(View.GONE);
        PhotoEditActivity.mCaptionView.setVisibility(View.GONE);
    }

    @Override
    public void onEditTextChangeListener(final View rootView, String text, int colorCode) {
        TextEditorDialogFragment textEditorDialogFragment =
                TextEditorDialogFragment.show((AppCompatActivity) mContext, text, colorCode);
        textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
            @Override
            public void onDone(String inputText, int colorCode) {
                mPhotoEditor.editText(rootView, inputText, colorCode);
                mEditToolbar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        imgUndo.setVisibility(View.VISIBLE);
        imgRedo.setVisibility(View.VISIBLE);

        if (btnSkip.getVisibility() == View.GONE) {
            btnSkip.setVisibility(View.VISIBLE);
            setRedoButtonLeftOfSkip();
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        if (numberOfAddedViews == 0) {
            btnSkip.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            imgRedo.setLayoutParams(params);
        }
    }


    public void setRedoButtonLeftOfSkip() {
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.LEFT_OF, btnSkip.getId());
        layoutParams.addRule(RelativeLayout.START_OF, btnSkip.getId());
        layoutParams.setMargins(0, 0, (int) getResources().getDimension(R.dimen.margin_10dp), 0);
        imgRedo.setLayoutParams(layoutParams);
    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgPencil:
                mEditToolbar.setVisibility(View.VISIBLE);
                if (isEraserSelected) {
                    disableEraserMode();
                }
                if (!isPencilSelected) {
                    isPencilSelected = true;
                    mPhotoEditor.setBrushDrawingMode(true);
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_brush);
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor), PorterDuff.Mode.SRC_ATOP);
                    imgPencil.setImageDrawable(drawable);
                    mPropertiesBSFragment.show(getActivity().getSupportFragmentManager(), mPropertiesBSFragment.getTag());
                } else {
                    disablePencilMode();
                }
                break;

            case R.id.btnEraser:
                mEditToolbar.setVisibility(View.VISIBLE);
                if (isPencilSelected) {
                    disablePencilMode();
                }
                if (!isEraserSelected) {
                    isEraserSelected = true;
                    Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_eraser);
                    drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.themeButtonColor), PorterDuff.Mode.SRC_ATOP);
                    imgEraser.setImageDrawable(drawable);
                    mPhotoEditor.brushEraser();

                } else {
                    disableEraserMode();}
                break;

            case R.id.imgText:
                if (isPencilSelected) {
                    disablePencilMode();
                } else if (isEraserSelected) {
                    disableEraserMode();
                }
                mEditToolbar.setVisibility(View.GONE);
                btnSkip.setTextColor(ContextCompat.getColor(mContext, R.color.transparent));
                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show((AppCompatActivity)mContext);
                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                    @Override
                    public void onDone(String inputText, int colorCode) {
                        if (!inputText.isEmpty()) {
                            mPhotoEditor.addText(inputText, colorCode);
                        }
                        btnSkip.setTextColor(ContextCompat.getColor(mContext, R.color.white));

                        mEditToolbar.setVisibility(View.VISIBLE);
                    }
                });
                break;

            case R.id.imgUndo:
                if (isPencilSelected) {
                    disablePencilMode();
                } else if (isEraserSelected) {
                    disableEraserMode();
                }
                mPhotoEditor.undo();
                break;

            case R.id.imgRedo:
                if (isPencilSelected) {
                    disablePencilMode();
                } else if (isEraserSelected) {
                    disableEraserMode();
                }
                mPhotoEditor.redo();
                if (btnSkip.getVisibility() == View.GONE) {
                    btnSkip.setVisibility(View.VISIBLE);
                    setRedoButtonLeftOfSkip();
                }
                break;

            case R.id.btnSkip:
                mPhotoEditor.clearAllViews();
                imgRedo.setVisibility(View.GONE);
                imgUndo.setVisibility(View.GONE);
                btnSkip.setVisibility(View.GONE);
                if (isFilterApplied) {
                    mPhotoEditorView.getSource().setImageBitmap(selectedBitmap);
                }

                if (isPencilSelected) {
                    disablePencilMode();
                } else if (isEraserSelected) {
                    disableEraserMode();
                }

                break;

            case R.id.imgFilter:
                if (isPencilSelected) {
                    disablePencilMode();
                } else if (isEraserSelected) {
                    disableEraserMode();
                }
                showColorFilter();
                break;

            case R.id.imgSticker:
                if (isPencilSelected) {
                    disablePencilMode();
                } else if (isEraserSelected) {
                    disableEraserMode();
                }
                mEditToolbar.setVisibility(View.GONE);
                createStickerPopup();
                break;

            case R.id.imgEmoji:
                if (isPencilSelected) {
                    disablePencilMode();
                } else if (isEraserSelected) {
                    disableEraserMode();
                }
                mEditToolbar.setVisibility(View.GONE);
                mEmojiBSFragment.show(getActivity().getSupportFragmentManager(), mEmojiBSFragment.getTag());
                break;

            case R.id.photoEditorView:
                setViewVisibilityOnEditorBackActions();
                break;
        }
    }

    private void disableEraserMode() {
        isEraserSelected = false;
        mPhotoEditor.setBrushDrawingMode(false);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_eraser);
        drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP);
        imgEraser.setImageDrawable(drawable);
    }

    private void disablePencilMode() {
        isPencilSelected = false;
        mPhotoEditor.setBrushDrawingMode(false);
        Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_brush);
        drawable.setColorFilter(ContextCompat.getColor(mContext, R.color.white), PorterDuff.Mode.SRC_ATOP);
        imgPencil.setImageDrawable(drawable);
    }

    private void showColorFilter() {
        isFilterDisplayed = true;

        Animation slideDown = AnimationUtils.loadAnimation(mContext, R.anim.push_down_out);
        mEditToolbar.startAnimation(slideDown);
        mEditToolbar.setVisibility(View.GONE);

        imgColorFilter.setVisibility(View.VISIBLE);
        Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
        imgColorFilter.startAnimation(slideUp);

        thumbListView = mRootView.findViewById(R.id.thumbnails);
        initHorizontalList();
    }

    private void initHorizontalList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);
        bindDataToAdapter();
    }

    ThumbnailsAdapter adapter;
    private void bindDataToAdapter() {
        final Context context = mContext;

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                ThumbnailItem t1 = new ThumbnailItem();
                ThumbnailItem t2 = new ThumbnailItem();
                ThumbnailItem t3 = new ThumbnailItem();
                ThumbnailItem t4 = new ThumbnailItem();
                ThumbnailItem t5 = new ThumbnailItem();
                ThumbnailItem t6 = new ThumbnailItem();

                t1.image = selectedBitmap;
                t2.image = selectedBitmap;
                t3.image = selectedBitmap;
                t4.image = selectedBitmap;
                t5.image = selectedBitmap;
                t6.image = selectedBitmap;
                ThumbnailsManager.clearThumbs();
                ThumbnailsManager.addThumb(t1); // Original Image

                t2.filter = SampleFilters.getStarLitFilter();
                ThumbnailsManager.addThumb(t2);

                t3.filter = SampleFilters.getBlueMessFilter();
                ThumbnailsManager.addThumb(t3);

                t4.filter = SampleFilters.getAweStruckVibeFilter();
                ThumbnailsManager.addThumb(t4);

                t5.filter = SampleFilters.getLimeStutterFilter();
                ThumbnailsManager.addThumb(t5);

                t6.filter = SampleFilters.getNightWhisperFilter();
                ThumbnailsManager.addThumb(t6);

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                adapter = new ThumbnailsAdapter(thumbs, mContext);
                thumbListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }


    public void onThumbnailClick(final Filter filter) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isFilterApplied = true;
                if (btnSkip.getVisibility() == View.GONE) {
                    btnSkip.setVisibility(View.VISIBLE);
                    setRedoButtonLeftOfSkip();
                }
                mPhotoEditorView.getSource()
                        .setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectedBitmap, selectedBitmap.getWidth()-1, selectedBitmap.getHeight()-1, false)));
            }
        });
    }

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
    }

    @Override
    public void onEmojiClick(String emojiUnicode, boolean isSelected) {
        if (isSelected && emojiUnicode != null && !emojiUnicode.isEmpty()) {
            mPhotoEditor.addEmoji(emojiUnicode);
        }
        mEditToolbar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onStickerResponse(JSONObject jsonObject) {
        mStickerResponse = jsonObject;
    }
}

