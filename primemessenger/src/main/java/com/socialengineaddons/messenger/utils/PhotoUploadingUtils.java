package com.socialengineaddons.messenger.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.socialengineaddons.messenger.Constants;
import com.socialengineaddons.messenger.multiimageselector.MultiImageSelectorActivity;

public class PhotoUploadingUtils {

    public static void openPhotoUploadingActivity(Context context, int selectedMode, boolean showCamera, int maxNum,
                                             boolean isOpenPhotoBlock){


        Intent intent;

        intent = new Intent(context, MultiImageSelectorActivity.class);
        // Whether photoshoot
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // The maximum number of selectable image
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, maxNum);
        // Select mode
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);

        intent.putExtra(MultiImageSelectorActivity.OPEN_PHOTO_BLOCK, isOpenPhotoBlock);
        ((Activity) context).startActivityForResult(intent, Constants.REQUEST_IMAGE);
    }
}
