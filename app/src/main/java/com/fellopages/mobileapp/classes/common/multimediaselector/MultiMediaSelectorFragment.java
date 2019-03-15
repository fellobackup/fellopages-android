package com.fellopages.mobileapp.classes.common.multimediaselector;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.common.dialogs.AlertDialogWithAction;
import com.fellopages.mobileapp.classes.common.multimediaselector.adapter.FolderAdapter;
import com.fellopages.mobileapp.classes.common.multimediaselector.adapter.ImageGridAdapter;
import com.fellopages.mobileapp.classes.common.multimediaselector.bean.Folder;
import com.fellopages.mobileapp.classes.common.multimediaselector.bean.Image;
import com.fellopages.mobileapp.classes.common.multimediaselector.utils.FileUtils;
import com.fellopages.mobileapp.classes.common.multimediaselector.utils.TimeUtils;
import com.fellopages.mobileapp.classes.common.utils.GlobalFunctions;
import com.fellopages.mobileapp.classes.common.utils.PreferencesUtils;
import com.fellopages.mobileapp.classes.common.utils.SnackbarUtils;
import com.fellopages.mobileapp.classes.core.AppConstant;
import com.fellopages.mobileapp.classes.core.ConstantVariables;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MultiMediaSelectorFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "MultiImageSelector";

    /** The maximum picture selection times , int type */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** Image Selection mode , int type */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** Whether to display the camera , boolean type */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** The default selection of the data set */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";

    public static final String IS_STORY_POST = "story_post";

    /** Radio */
    public static final int MODE_SINGLE = 0;
    /** Multiple choice */
    public static final int MODE_MULTI = 1;
    // Different definitions loader
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    // Request loading system camera
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_VIDEO = 200;

    public static final String EXTRA_SELECTION_TYPE = "selection_type";
    public static final String SELECTION_PHOTO = "photo";
    public static final String SELECTION_VIDEO = "video";
    public static final String SELECTION_PHOTO_VIDEO = "photo_video";

    // Results
    private ArrayList<String> resultList = new ArrayList<>();
    // Folder Data
    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    // Image Grid
    private GridView mGridView;
    private Callback mCallback;

    private ImageGridAdapter mImageAdapter;
    private FolderAdapter mFolderAdapter;

    private ListPopupWindow mFolderPopupWindow;

    // timeline
    private TextView mTimeLineText;
    // category
    private TextView mCategoryText;
    // Preview button
    private Button mPreviewBtn;
    // Bottom View
    private View mPopupAnchorView;

    private int mDesireImageCount;

    private boolean hasFolderGened = false;
    private boolean mIsShowCamera = false;
    private boolean isCameraUploadRequest = true;
    private String selection;

    private int mGridWidth, mGridHeight;

    private File mTmpFile;

    private AppConstant mAppConst;

    private View rootView;
    private TextView tvAddPhoto, tvAddVideo;
    private AlertDialogWithAction mAlertDialogWithAction;
    private boolean isStoryPost;
    private String categoryAllText;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Callback) context;
        }catch (ClassCastException e){
            throw new ClassCastException("The Activity must implement MultiMediaSelectorFragment.Callback interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAppConst = new AppConstant(getActivity());
        mAlertDialogWithAction = new AlertDialogWithAction(getContext());
        rootView =  inflater.inflate(R.layout.fragment_multi_image, container, false);
        tvAddPhoto = getActivity().findViewById(R.id.camera_add);
        tvAddVideo = getActivity().findViewById(R.id.video_add);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Select the number of pictures
        mDesireImageCount = getArguments().getInt(EXTRA_SELECT_COUNT);

        // Image Select mode
        final int mode = getArguments().getInt(EXTRA_SELECT_MODE);

        // The default selection
        if(mode == MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(EXTRA_DEFAULT_SELECTED_LIST);
            if(tmp != null && tmp.size()>0) {
                resultList = tmp;
            }
        }

        isStoryPost = getArguments().getBoolean(IS_STORY_POST, false);

        String selectionType = getArguments().getString(EXTRA_SELECTION_TYPE, SELECTION_PHOTO);

        tvAddPhoto.setOnClickListener(this);
        tvAddVideo.setOnClickListener(this);

        if (selectionType != null && !selectionType.isEmpty()) {
            switch (selectionType) {
                case SELECTION_PHOTO:
                    tvAddPhoto.setVisibility(View.GONE);
                    tvAddVideo.setVisibility(View.GONE);
                    categoryAllText = getContext().getResources().getString(R.string.multi_img_btn_title);
                    selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                    break;
                case SELECTION_VIDEO:
                    tvAddPhoto.setVisibility(View.GONE);
                    tvAddVideo.setVisibility(View.VISIBLE);
                    categoryAllText = getContext().getResources().getString(R.string.all_videos);
                    RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) tvAddVideo.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    tvAddVideo.setLayoutParams(layoutParams);
                    selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                    break;
                case SELECTION_PHOTO_VIDEO:
                    tvAddPhoto.setVisibility(View.VISIBLE);
                    tvAddVideo.setVisibility(View.VISIBLE);
                    categoryAllText = getContext().getResources().getString(R.string.all_media);
                    selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                            + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                            + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                    break;
            }
        }

        // Whether to display the camera
        mIsShowCamera = getArguments().getBoolean(EXTRA_SHOW_CAMERA, true);
        mImageAdapter = new ImageGridAdapter(getActivity(), mIsShowCamera);
        // Choose whether to display indicator
        mImageAdapter.showSelectIndicator(mode == MODE_MULTI);

        mPopupAnchorView = view.findViewById(R.id.footer);

        mTimeLineText = view.findViewById(R.id.timeline_area);
        // Initialization , first hide the current timeline
        mTimeLineText.setVisibility(View.GONE);

        mCategoryText = view.findViewById(R.id.category_btn);
        // Initialization , load all pictures
        mCategoryText.setText(categoryAllText);
        mCategoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mFolderPopupWindow == null){
                    createPopupFolderList(mGridWidth, mGridHeight);
                }

                if (mFolderPopupWindow.isShowing()) {
                    mFolderPopupWindow.dismiss();
                } else {
                    mFolderPopupWindow.show();
                    int index = mFolderAdapter.getSelectIndex();
                    index = index == 0 ? index : index - 1;
                    mFolderPopupWindow.getListView().setSelection(index);
                }
            }
        });

        mPreviewBtn = view.findViewById(R.id.preview);
        // Initialization , the button state initialization
        if(resultList == null || resultList.size()<=0){
            mPreviewBtn.setText(R.string.preview);
            mPreviewBtn.setEnabled(false);
        }
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO 预览
            }
        });

        mGridView = view.findViewById(R.id.grid);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int state) {

                final RequestManager glide = Glide.with(getActivity());
                if(state == SCROLL_STATE_IDLE || state == SCROLL_STATE_TOUCH_SCROLL){
                    glide.resumeRequests();
                }else{
                    glide.pauseRequests();
                }

                if(state == SCROLL_STATE_IDLE){
                    // Stop slide , the date indicator disappears
                    mTimeLineText.setVisibility(View.GONE);
                }else if(state == SCROLL_STATE_FLING){
                    mTimeLineText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mTimeLineText.getVisibility() == View.VISIBLE) {
                    int index = firstVisibleItem + 1 == view.getAdapter().getCount() ? view.getAdapter().getCount() - 1 : firstVisibleItem + 1;
                    Image image = (Image) view.getAdapter().getItem(index);
                    if (image != null) {
                        mTimeLineText.setText(TimeUtils.formatPhotoDate(image.path));
                    }
                }
            }
        });
        mGridView.setAdapter(mImageAdapter);
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int width = mGridView.getWidth();
                final int height = mGridView.getHeight();

                mGridWidth = width;
                mGridHeight = height;

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = width / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (width - columnSpace*(numCount-1)) / numCount;
                mImageAdapter.setItemSize(columnWidth);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else{
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mImageAdapter.isShowCamera()){
                    // If the camera , the first camera is displayed as a Grid to address the special logic
                    if(i == 0){
                        if (mAppConst.checkManifestPermission(Manifest.permission.CAMERA)
                                && mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showCameraAction();
                        } else if (!mAppConst.checkManifestPermission(Manifest.permission.CAMERA)) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    ConstantVariables.PERMISSION_CAMERA);
                        } else if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    ConstantVariables.WRITE_EXTERNAL_STORAGE);
                        }
                    }else{
                        // Normal operation
                        Image image = (Image) adapterView.getAdapter().getItem(i);
                        selectImageFromGrid(image, mode);
                    }
                }else{
                    // Normal operation
                    Image image = (Image) adapterView.getAdapter().getItem(i);
                    selectImageFromGrid(image, mode);
                }
            }
        });

        mFolderAdapter = new FolderAdapter(getActivity(), categoryAllText);
    }

    /**
     * Create pop ListView
     */
    private void createPopupFolderList(int width, int height) {
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height * 5 / 8);
        mFolderPopupWindow.setAnchorView(mPopupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mFolderAdapter.setSelectIndex(i);

                final int index = i;
                final AdapterView v = adapterView;

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();

                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, mLoaderCallback);
                            mCategoryText.setText(categoryAllText);
                            if (mIsShowCamera) {
                                mImageAdapter.setShowCamera(true);
                            } else {
                                mImageAdapter.setShowCamera(false);
                            }
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);
                            if (null != folder) {
                                mImageAdapter.setData(folder.images);
                                mCategoryText.setText(folder.name);
                                // Set the default selection
                                if (resultList != null && resultList.size() > 0) {
                                    mImageAdapter.setDefaultSelected(resultList);
                                }
                            }
                            mImageAdapter.setShowCamera(false);
                        }

                        // Slide to the initial position
                        mGridView.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // First load all pictures
        //new LoadImageTask().execute();
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // After completion of the camera to take pictures , return image path
        if(requestCode == REQUEST_CAMERA){
            if(resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpFile, true);
                    }
                }
            }else{
                if(mTmpFile != null && mTmpFile.exists()){
                    mTmpFile.delete();
                }
            }
        } else if (requestCode == REQUEST_VIDEO) {
            if(resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (mCallback != null) {
                        mCallback.onCameraShot(mTmpFile, false);
                    }
                }
            }else{
                if(mTmpFile != null && mTmpFile.exists()){
                    mTmpFile.delete();
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "on change");

        if(mFolderPopupWindow != null){
            if(mFolderPopupWindow.isShowing()){
                mFolderPopupWindow.dismiss();
            }
        }

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int height = mGridView.getHeight();

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                Log.d(TAG, "Desire Size = " + desireSize);
                final int numCount = mGridView.getWidth() / desireSize;
                Log.d(TAG, "Grid Size = " + mGridView.getWidth());
                Log.d(TAG, "num count = " + numCount);
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
                int columnWidth = (mGridView.getWidth() - columnSpace * (numCount - 1)) / numCount;
                mImageAdapter.setItemSize(columnWidth);

                if (mFolderPopupWindow != null) {
                    mFolderPopupWindow.setHeight(height * 5 / 8);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        super.onConfigurationChanged(newConfig);

    }

    /**
     * Choose camera
     */
    private void showCameraAction() {
        // Skip to system cameras
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(cameraIntent.resolveActivity(getActivity().getPackageManager()) != null){
            // Camera to take pictures after setting the system output path
            // Create a temporary file
            mTmpFile = FileUtils.createTmpFile(getActivity());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, GlobalFunctions.getFileUri(getContext(), mTmpFile));
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        }else{
            Toast.makeText(getActivity(), R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method to show camera recorder for video uploading.
     */
    private void showVideoCameraAction() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + getResources().getString(R.string.app_name));
        if (!myDir.exists()) {
            myDir.mkdir();
        }
        mTmpFile = new File(myDir, "myvideo-"+ timeStamp +".mp4");
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, GlobalFunctions.getFileUri(getContext(), mTmpFile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, PreferencesUtils.getVideoQuality(getContext()));
        startActivityForResult(intent, REQUEST_VIDEO);
    }

    /**
     * Select Image Operation
     * @param image
     */
    private void selectImageFromGrid(Image image, int mode) {
        if(image != null) {
            // Multiple choice mode
            if(mode == MODE_MULTI) {
                if (resultList.contains(image.path)) {
                    resultList.remove(image.path);
                    if(resultList.size() != 0) {
                        mPreviewBtn.setEnabled(true);
                        mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
                    }else{
                        mPreviewBtn.setEnabled(false);
                        mPreviewBtn.setText(R.string.preview);
                    }
                    if (mCallback != null) {
                        mCallback.onImageUnselected(image.path);
                    }
                    mImageAdapter.select(image);
                } else {
                    // Analyzing choose the number of questions
                    if(mDesireImageCount == resultList.size()){
                        Toast.makeText(getActivity(), R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (resultList.size() > 0) {
                        if (isStoryPost) {
                            // Radio mode
                            if(mCallback != null){
                                if (image.mimeType != null && image.mimeType.startsWith("image")) {
                                    setSelectedImage(image);
                                } else {
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.no_video_selection_with_image_story_post), Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            if (image.path.contains(".gif")) {
                                if (isGifImageSelected(resultList)) {
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.one_gif_selection_warning_message), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.gif_selection_warning_message_with_other_photo), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                if (!isGifImageSelected(resultList)) {
                                    setSelectedImage(image);
                                } else {
                                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.photo_selection_warning_message_if_gif_already_selected), Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    } else {
                        if (isStoryPost) {
                            if(mCallback != null){
                                if (image.mimeType != null && image.mimeType.startsWith("image")) {
                                    setSelectedImage(image);
                                } else {
                                    mCallback.onVideoSelected(image.path);
                                }
                            }
                        } else {
                            setSelectedImage(image);
                        }
                    }
                }
            }else if(mode == MODE_SINGLE){
                // Radio mode
                if(mCallback != null){
                    if (image.mimeType != null && image.mimeType.startsWith("image")) {
                        mCallback.onSingleImageSelected(image.path);
                    } else {
                        mCallback.onVideoSelected(image.path);
                    }
                }
            }
        }
    }

    private void setSelectedImage(Image image) {
        resultList.add(image.path);
        mPreviewBtn.setEnabled(true);
        mPreviewBtn.setText(getResources().getString(R.string.preview) + "(" + resultList.size() + ")");
        if (mCallback != null) {
            mCallback.onImageSelected(image.path);
        }
        mImageAdapter.select(image);
    }

    private boolean isGifImageSelected(ArrayList<String> resultList) {
        boolean isGifSelected = false;
        for (int i = 0; i < resultList.size(); i++) {
            String imagePath = resultList.get(i);
            if (imagePath.contains(".gif")) {
                isGifSelected = true;
            }
        }
        return isGifSelected;
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] FILE_PROJECTION = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

        Uri queryUri = MediaStore.Files.getContentUri("external");

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new CursorLoader(
                    getActivity(),
                    queryUri,
                    FILE_PROJECTION,
                    selection,
                    null, // Selection args (none).
                    MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                List<Image> images = new ArrayList<>();
                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do{
                        String path = data.getString(data.getColumnIndexOrThrow(FILE_PROJECTION[1]));
                        String name = data.getString(data.getColumnIndexOrThrow(FILE_PROJECTION[3]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(FILE_PROJECTION[2]));
                        String mimeType = data.getString(data.getColumnIndexOrThrow(FILE_PROJECTION[5]));
                        Image image = new Image(path, name, mimeType, dateTime);
                        images.add(image);
                        if( !hasFolderGened ) {
                            // Get folder name
                            File imageFile = new File(path);
                            File folderFile = imageFile.getParentFile();
                            Folder folder = new Folder();
                            folder.name = folderFile.getName();
                            folder.path = folderFile.getAbsolutePath();
                            folder.mimeType = mimeType;
                            folder.cover = image;
                            if (!mResultFolder.contains(folder)) {
                                List<Image> imageList = new ArrayList<>();
                                imageList.add(image);
                                folder.images = imageList;
                                mResultFolder.add(folder);
                            } else {
                                // Update
                                Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
                                f.images.add(image);
                            }
                        }

                    }while(data.moveToNext());

                    mImageAdapter.setData(images);

                    // Set the default selection
                    if(resultList != null && resultList.size()>0){
                        mImageAdapter.setDefaultSelected(resultList);
                    }

                    mFolderAdapter.setData(mResultFolder);
                    hasFolderGened = true;

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    @Override
    public void onClick(View view) {
        isCameraUploadRequest = (view.getId() == R.id.camera_add);
        if (view.getId() == R.id.camera_add) {
            if (mAppConst.checkManifestPermission(Manifest.permission.CAMERA) && mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showCameraAction();
            } else if (!mAppConst.checkManifestPermission(Manifest.permission.CAMERA)) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        ConstantVariables.PERMISSION_CAMERA);
            } else if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ConstantVariables.WRITE_EXTERNAL_STORAGE);
            }
        } else if (view.getId() == R.id.video_add) {
            if (mAppConst.checkManifestPermission(Manifest.permission.CAMERA) && mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showVideoCameraAction();
            } else if (!mAppConst.checkManifestPermission(Manifest.permission.CAMERA)) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        ConstantVariables.PERMISSION_CAMERA);
            } else if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        ConstantVariables.WRITE_EXTERNAL_STORAGE);
            }

        }
    }

    /**
     * Callback Interface
     */
    public interface Callback{
        void onSingleImageSelected(String path);
        void onImageSelected(String path);
        void onImageUnselected(String path);
        void onCameraShot(File imageFile, boolean isImageFile);
        void onVideoSelected(String path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantVariables.PERMISSION_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(!mAppConst.checkManifestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);
                    } else if (isCameraUploadRequest) {
                        showCameraAction();
                    } else {
                        showVideoCameraAction();
                    }
                } else {
                    // If user deny the permission popup
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.CAMERA,
                                ConstantVariables.PERMISSION_CAMERA);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(getContext(), rootView,
                                ConstantVariables.PERMISSION_CAMERA);

                    }
                }
                break;
            case ConstantVariables.WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, proceed to the normal flow.
                    if (isCameraUploadRequest) {
                        showCameraAction();
                    } else {
                        showVideoCameraAction();
                    }
                } else {
                    // If user deny the permission popup
                    if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an explanation to the user, After the user
                        // sees the explanation, try again to request the permission.

                        mAlertDialogWithAction.showDialogForAccessPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }else{
                        // If user pressed never ask again on permission popup
                        // show snackbar with open app info button
                        // user can revoke the permission from Permission section of App Info.

                        SnackbarUtils.displaySnackbarOnPermissionResult(getContext(), rootView,
                                ConstantVariables.WRITE_EXTERNAL_STORAGE);

                    }
                }
                break;
        }
    }
}
