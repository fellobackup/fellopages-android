package com.fellopages.mobileapp.classes.common.utils.okhttp;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by bigstep on 4/7/18.
 */

public abstract class OkHttpCallBack<T> {
    public static OkHttpCallBack CALLBACK_DEFAULT = new OkHttpCallBack() {

        @Override
        public Object parseNetworkResponse(Response response, Object tag) {
            return null;
        }

        @Override
        public void onError(Call call, Exception e, Object tag) {

        }

        @Override
        public void onResponse(Object response, Object tag) {

        }
    };

    /**
     * UI Thread
     *
     * @param request
     */
    public void onBefore(Request request, Object tag) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onAfter(Object tag) {
    }

    /**
     * UI Thread
     *
     * @param progress
     */
    public void inProgress(float progress, long total, Object tag) {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response
     * @return
     */
    public boolean validateReponse(Response response, Object tag) {
        return response.isSuccessful();
    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    public abstract T parseNetworkResponse(Response response, Object tag) throws Exception;

    public abstract void onError(Call call, Exception e, Object tag);

    public abstract void onResponse(T response, Object tag);
}
