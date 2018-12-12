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

import android.os.AsyncTask;
import android.webkit.URLUtil;

import com.fellopages.mobileapp.classes.common.interfaces.OnVideoSourceLoadCompleteListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Async Class to generate dataSource on background thread.
 */
public class GetVideoDataSourceUtils extends AsyncTask<Void, Void, String> {

    // Member variables.
    private String mVideoViewUrl;
    private OnVideoSourceLoadCompleteListener mOnVideoSourceLoadCompleteListener;

    /**
     *
     * @param videoViewUrl Video url from which video path needs to be fetched.
     * @param onVideoSourceLoadCompleteListener Listener instance.
     */
    public GetVideoDataSourceUtils(String videoViewUrl,
                                   OnVideoSourceLoadCompleteListener onVideoSourceLoadCompleteListener) {
        this.mVideoViewUrl = videoViewUrl;
        this.mOnVideoSourceLoadCompleteListener = onVideoSourceLoadCompleteListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            return getDataSource(mVideoViewUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to get dataSource.
     *
     * @param path video url.
     * @return returns the dataSource path.
     * @throws IOException
     */
    private String getDataSource(String path) throws IOException {
        if (!URLUtil.isNetworkUrl(path)) {
            return path;
        } else {
            URL url = new URL(path);
            try {
                HttpsURLConnection.setDefaultSSLSocketFactory(new TLSSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            URLConnection cn = url.openConnection();
            cn.connect();
            InputStream stream = cn.getInputStream();
            if (stream == null)
                throw new RuntimeException("Stream is null");
            File temp = File.createTempFile("mediaplayertmp", "dat");
            temp.deleteOnExit();
            String tempPath = temp.getAbsolutePath();
            FileOutputStream out = new FileOutputStream(temp);
            byte buf[] = new byte[128];
            do {
                int numread = stream.read(buf);
                if (numread <= 0)
                    break;
                out.write(buf, 0, numread);
                if (isCancelled()) {
                    return null;
                }
            } while (!isCancelled());
            try {
                stream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return tempPath;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mOnVideoSourceLoadCompleteListener != null) {
            mOnVideoSourceLoadCompleteListener.onSuccess(result);
        }
    }
}
