package com.uniprogy.outquiz.helpers;

import org.json.JSONObject;

public abstract class APIListener {

    abstract public void success(JSONObject response);

    abstract public void failure(int code, String[] errors);

}
