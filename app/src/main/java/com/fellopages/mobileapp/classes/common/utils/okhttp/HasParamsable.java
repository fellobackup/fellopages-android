package com.fellopages.mobileapp.classes.common.utils.okhttp;

import java.util.Map;

/**
 * Created by zhy on 16/3/1.
 */
public interface HasParamsable
{
    OkHttpRequestBuilder addParams(Map<String, String> params);
    OkHttpRequestBuilder addParam(String key, String val);
}
