package com.socialengineaddons.messenger.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class CacheUtils {

    private static CacheUtils instance;
    private LruCache<String, Bitmap> lru;

    private CacheUtils(Context context) {
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;
        lru = new LruCache<>(cacheSize);
    }

    public static CacheUtils getInstance(Context context) {
        if (instance == null) {
            instance = new CacheUtils(context);
        }
        return instance;
    }

    public LruCache<String, Bitmap> getLru() {
        return lru;
    }

    public static void clearCache() {
        instance = null;
    }
}
