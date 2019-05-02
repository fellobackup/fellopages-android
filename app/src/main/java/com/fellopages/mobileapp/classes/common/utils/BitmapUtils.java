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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.fellopages.mobileapp.R;
import com.fellopages.mobileapp.classes.core.MainActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

// Bitmap operations helper
public class BitmapUtils {

    public static boolean isImageRotated = false, isFolderCreated = false, success = false;
    private static Context mContext;
    private static File folder;
    private static ArrayList<String> mSelectPath = new ArrayList<>();
    private static int counter = 1;

    /*
    Function to Rotate Bitmap of Image for Samsung device.
     */

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        isImageRotated = true;
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Method to calculate appropriate sample size of image.
     * @param options option is used for sampling the image.
     * @param reqWidth requested width of image.
     * @param reqHeight requested height of image.
     * @return returns sampling size.
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Method to decode image path into Bitmap
     * @param context context of calling class.
     * @param imagePath path of image which is going to be load on imageview.
     * @param reqWidth requested width of image.
     * @param reqHeight requested height of image.
     * @param isRotation true if Exif is not coming fine.(Image rotated.)
     * @return return bitmap with the appropriate width and height
     */
    public static Bitmap decodeSampledBitmapFromFile(Context context, String imagePath, int reqWidth,
                                                     int reqHeight, boolean isRotation) {
        mContext = context;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap;
        if (CacheUtils.getInstance(context).getLru().get(imagePath) != null) {
            Log.d("ThisWasLoggedHere 1 ", "true");
            bitmap = CacheUtils.getInstance(context).getLru().get(imagePath);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
        } else {
            Log.d("ThisWasLoggedHere 2 ", "true");
            bitmap = BitmapFactory.decodeFile(imagePath, options);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
            try {
                String message = " 0";
                ExifInterface ei = new ExifInterface(imagePath);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = RotateBitmap(bitmap, 90);
                        message = " 90";
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = RotateBitmap(bitmap, 180);
                        message = " 180";
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = RotateBitmap(bitmap, 270);
                        message = " 270";
                        break;
                }

                Bundle bundle = new Bundle();
                bundle.putString("image_orientation", message);
                if (MainActivity.mFirebaseAnalytics != null)
                    MainActivity.mFirebaseAnalytics.logEvent("Image_rotation", bundle);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (isRotation)
            storeImageOnLocalPath(bitmap, imagePath.substring(imagePath.lastIndexOf(".")));

        //Saving bitmap to cache. it will later be retrieved using the bitmap_image key
        if (bitmap != null) {
            CacheUtils.getInstance(context).getLru().put(imagePath, bitmap);
        }
        Log.d("ThisWasLoggedHere 3 ", "true");
        return bitmap;
    }

    /**
     * Method to create rotated image for which EXIF is not normal.
     * @param bitmap rotated bitmap.
     * @param fileExtension extension of file.
     */
    public static void storeImageOnLocalPath(Bitmap bitmap, String fileExtension) {

        try {
            if (!isFolderCreated) {
                folder = new File(Environment.getExternalStorageDirectory() + "/" +
                        mContext.getPackageName() + mContext.getResources().getString(R.string.app_name));

                if (!folder.exists()) {
                    success = folder.mkdir();
                }
            }
            if (success) {

                isFolderCreated = true;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                File file = new File(folder + File.separator + "Image" + counter + fileExtension);
                counter++;
                try {
                    file.createNewFile();

                    //write the bytes in file
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(bytes.toByteArray());
                    mSelectPath.add(file.getAbsolutePath());

                    // Close the FileOutput
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to return updated SelectPath.
     * @return return selectpath which contains rotated images.
     */
    public static ArrayList<String> updateSelectPath() {
        return mSelectPath;
    }

    /**
     * Method to delete image folder which contains all the rotated images.
     */
    public static void deleteImageFolder() {
        try {
            if (folder != null && folder.exists()) {
                String[] children = folder.list();
                for (String aChildren : children) {
                    new File(folder, aChildren).delete();
                }
                folder.delete();
                isFolderCreated = false;
                isImageRotated = false;
                counter = 0;
                mSelectPath.clear();

                // When the image rotated then clearing the cache.
                CacheUtils.clearCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return  BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }

    public static ArrayList<String> storeImageOnLocalPathFromUrl(Context context, Bitmap bitmap, String fileExtension) {

        try {
            folder = new File(Environment.getExternalStorageDirectory() + "/" +
                    context.getPackageName() + context.getResources().getString(R.string.app_name));

            if (!folder.exists()) {
                folder.mkdir();
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            File file = new File(folder + File.separator + "Image" + counter + fileExtension);
            counter++;
            try {
                file.createNewFile();

                //write the bytes in file
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(bytes.toByteArray());
                mSelectPath.add(file.getAbsolutePath());

                // Close the FileOutput
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mSelectPath;
    }
}
