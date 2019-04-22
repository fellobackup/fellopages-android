package com.uniprogy.outquiz.models;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.androidnetworking.widget.ANImageView;
import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.helpers.Misc;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Player
{
    public int id;
    public String token, username, avatar, referral;

    public Player(JSONObject data)
    {
        this.id = data.optInt("id", 0);

        this.token = Misc.jsonNull(data, "token");
        this.username = Misc.jsonNull(data, "username");
        this.avatar = Misc.jsonNull(data, "avatar");
        this.referral = Misc.jsonNull(data, "referral");
    }

    public Player(Map<String,String> data)
    {
        this.id = Integer.parseInt(data.get("id"));

        this.token = Misc.stringNull(data.get("token"));
        this.username = Misc.stringNull(data.get("username"));
        this.avatar = Misc.stringNull(data.get("avatar"));
        this.referral = Misc.stringNull(data.get("referral"));
    }

    public Player(int id, String username, String avatar)
    {
        this.id = id;
        this.username = Misc.stringNull(username);
        this.avatar = Misc.stringNull(avatar);
    }

    public Player(int id, String token, String username, String avatar, String referral)
    {
        this.id = id;
        this.token = Misc.stringNull(token);
        this.username = Misc.stringNull(username);
        this.avatar = Misc.stringNull(avatar);
        this.referral = Misc.stringNull(referral);
    }

    public void setAvatar(ImageView imageView)
    {
        setAvatar(imageView, false, true);
    }

    public void setAvatar(ImageView imageView, Boolean refresh)
    {
        setAvatar(imageView, refresh, true);
    }

    public void setAvatar(final ImageView imageView, Boolean refresh, Boolean cache)
    {
        if (!TextUtils.isEmpty(avatar)) {
            ANRequest.GetRequestBuilder request = AndroidNetworking.get(avatar);

            if (refresh) {
                request.getResponseOnlyFromNetwork();
            }

            if (!cache) {
                request.doNotCacheResponse();
            }

            request.build().getAsBitmap(new BitmapRequestListener() {
                @Override
                public void onResponse(Bitmap bitmap) {
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onError(ANError error) {
                    // do nothing
                }
            });
        } else {
            imageView.setImageDrawable(App.getContext().getDrawable(R.drawable.avatar));
        }
    }
}
