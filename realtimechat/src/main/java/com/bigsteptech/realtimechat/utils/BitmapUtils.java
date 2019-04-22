package com.bigsteptech.realtimechat.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;

import java.io.IOException;

public class BitmapUtils {

    public static Bitmap decodeSampledBitmapFromFile(Context context, String imagePath) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        return  BitmapFactory.decodeFile(imagePath, options);
    }
}
