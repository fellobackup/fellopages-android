package com.uniprogy.outquiz.helpers;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Method;
import com.androidnetworking.common.RequestBuilder;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

class APIRequest {

    String path;
    int method;
    Map<String,String> parameters;
    Boolean autheticate;
    APIListener listener;

    public APIRequest(String path, int method, Map<String,String> parameters, Boolean autheticate, APIListener listener)
    {
        this.path = path;
        this.parameters = parameters;
        this.method = method;
        this.autheticate = autheticate;
        this.listener = listener;
    }
}

public class API {

    //region Helpers

    private static Boolean isRefreshing = false;
    private static ArrayList<APIRequest> queue = new ArrayList<APIRequest>();

    private static String host()
    {
        return App.getContext().getResources().getString(R.string.app_host) + "/api";
    }

    private static void addHeaders(Boolean withAuth, RequestBuilder builder)
    {
        builder.addHeaders("Accept", "application/json");
        if(withAuth)
        {
            builder.addHeaders("Authorization", "Bearer " + Misc.getCurrentPlayer().token);
        }
    }

    private static String[] getErrors(ANError error)
    {
        ArrayList<String> errors = new ArrayList<String>();
        try {
            JSONObject obj = new JSONObject(error.getErrorBody());
            if(obj.has("errors"))
            {
                JSONArray errArray = obj.optJSONArray("errors");
                if(errArray == null)
                {
                    JSONObject errObj = obj.getJSONObject("errors");
                    for(Iterator<String> iter = errObj.keys(); iter.hasNext();) {
                        String key = iter.next();
                        JSONArray itemErrors = errObj.getJSONArray(key);
                        for (int i = 0; i < itemErrors.length(); i++) {
                            errors.add(itemErrors.getString(i));
                        }
                    }
                } else {
                    for (int i = 0; i < errArray.length(); i++) {
                        errors.add(errArray.getString(i));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] returnArray = new String[errors.size()];
        returnArray = errors.toArray(returnArray);

        return returnArray;
    }

    private static void runQueue()
    {
        for(APIRequest r : queue)
        {
            run(r);
        }
        queue.clear();
    }

    private static void run(final APIRequest request)
    {
        if(isRefreshing)
        {
            queue.add(request);
            return;
        }

        ANRequest.DynamicRequestBuilder networkRequest = AndroidNetworking.request(host() + request.path, request.method);
        Log.d("Data ", host() + request.path);
        if(request.parameters != null) {
            for (Map.Entry<String, String> entry : request.parameters.entrySet()) {
                String value = entry.getValue();
                if (value == null) {
                    value = "";
                }

                if (request.method == Method.GET) {
                    networkRequest.addQueryParameter(entry.getKey(), value);
                } else {
                    networkRequest.addBodyParameter(entry.getKey(), value);
                }
            }
        }

        addHeaders(request.autheticate, networkRequest);

        networkRequest.build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                request.listener.success(response);
            }

            @Override
            public void onError(ANError error) {
                int status = error.getErrorCode();
                if (status == 401) {
                    queue.add(request);
                    if(!isRefreshing)
                    {
                        refresh(null);
                    }
                } else {
                    String[] errors = getErrors(error);
                    request.listener.failure(status, errors);
                }
            }
        });
    }

    //endregion

    //region Methods

    private static void refresh(final APIListener listener)
    {
        isRefreshing = true;

        String path = "/refresh";
        ANRequest.DynamicRequestBuilder networkRequest = AndroidNetworking.request(host() + path, Method.GET);
        addHeaders(true, networkRequest);

        networkRequest.build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                isRefreshing = false;
                try {
                    String token = response.getString("token");
                    Misc.getCurrentPlayer().token = token;
                    Misc.setCurrentPlayer(Misc.getCurrentPlayer());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(listener != null)
                {
                    listener.success(response);
                } else {
                    runQueue();
                }
            }

            @Override
            public void onError(ANError error) {
                isRefreshing = false;
                // couldn't refresh token
                if(error.getErrorCode() == 401)
                {
                    // empty queue
                    queue.clear();
                    // complete logout
                    Misc.logout();
                }
            }
        });
    }

    public static void verify(String country, String number, APIListener listener)
    {
        String path = "/verify";
        Map<String,String> params = new HashMap<String,String>();
        params.put("country", country);
        params.put("number", number);

        APIRequest request = new APIRequest(path, Method.GET, params, false, listener);
        run(request);
    }

    public static void login(String type, String id, String verification, APIListener listener)
    {
        String path = "/login";
        Map<String,String> params = new HashMap<String,String>();
        params.put("channel_type", type);
        params.put("channel_type", type);
        params.put("channel_id", id);
        params.put("channel_verification", verification);
        params.put("push_token", Misc.getToken());
        params.put("push_type", "android");
        params.put("country", Locale.getDefault().getCountry());
        params.put("language", Locale.getDefault().toString());

        APIRequest request = new APIRequest(path, Method.POST, params, false, listener);
        run(request);
    }

    public static void avatar(final File imgFile, final APIListener listener)
    {
        String path = "/avatar";

        // uploading avatar
        if (imgFile != null) {
            ANRequest.MultiPartBuilder networkRequest = AndroidNetworking.upload(host() + path).addMultipartFile("avatar", imgFile);
            addHeaders(true, networkRequest);
            networkRequest.addHeaders("content-type", "multipart/form-data").build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            listener.success(response);
                        }

                        @Override
                        public void onError(ANError error) {
                            int status = error.getErrorCode();
                            if (status == 401) {
                                refresh(new APIListener() {
                                    @Override
                                    public void success(JSONObject response) {
                                        avatar(imgFile, listener);
                                    }

                                    @Override
                                    public void failure(int code, String[] errors) {
                                        // do nothing
                                    }
                                });
                            } else {
                                String[] errors = getErrors(error);
                                listener.failure(status, errors);
                            }
                        }
                    });
        }
        // deleting avatar
        else {
            APIRequest request = new APIRequest(path, Method.POST, null, true, listener);
            run(request);
        }
    }

    public static void profile(String username, APIListener listener)
    {
        String path = "/profile";
        Map<String,String> params = new HashMap<String,String>();
        params.put("username", username);
        APIRequest request = new APIRequest(path, Method.POST, params, true, listener);
        run(request);
    }

    public static void updateNumber(String type, String id, String newId, String verification, APIListener listener)
    {
        String path = "/login";
        Map<String,String> params = new HashMap<String,String>();
        params.put("channel_type", type);
        params.put("newchannel_id", newId);
        params.put("channel_id", id);
        params.put("channel_verification", verification);
        params.put("push_token", Misc.getToken());
        params.put("push_type", "android");
        params.put("country", Locale.getDefault().getCountry());
        params.put("language", Locale.getDefault().toString());

        APIRequest request = new APIRequest(path, Method.POST, params, false, listener);
        run(request);
    }

    public static void home(APIListener listener)
    {
        String path = "/home";
        APIRequest request = new APIRequest(path, Method.GET, null, true, listener);
        run(request);
    }

    public static void leaderboard(APIListener listener)
    {
        String path = "/leaderboard";
        APIRequest request = new APIRequest(path, Method.GET, null, true, listener);
        run(request);
    }

    public static void player(APIListener listener)
    {
        String path = "/player";
        APIRequest request = new APIRequest(path, Method.GET, null, true, listener);
        run(request);
    }

    public static void referral(String code, APIListener listener)
    {
        String path = "/referral";
        Map<String,String> params = new HashMap<String,String>();
        params.put("code", code);
        APIRequest request = new APIRequest(path, Method.GET, params, true, listener);
        run(request);
    }

    public static void cashout(String paypal, APIListener listener)
    {
        String path = "/cashout";
        Map<String,String> params = new HashMap<String,String>();
        params.put("paypal", paypal);
        APIRequest request = new APIRequest(path, Method.POST, params, true, listener);
        run(request);
    }

    //endregion
}
