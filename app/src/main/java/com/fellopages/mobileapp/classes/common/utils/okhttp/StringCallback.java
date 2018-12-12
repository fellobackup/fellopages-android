package com.fellopages.mobileapp.classes.common.utils.okhttp;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by bigstep on 4/7/18.
 */
public abstract class StringCallback extends OkHttpCallBack<String>
{
    @Override
    public String parseNetworkResponse(Response response, Object tag) throws IOException
    {
        return response.body().string();
    }
}
