package com.uniprogy.outquiz.models;

import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.BitmapRequestListener;
import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.helpers.Misc;

import org.json.JSONObject;

import java.util.Map;

import static android.graphics.Typeface.BOLD;

public class ChatMessage
{
    public int userId;
    public String username, avatar, message;

    public ChatMessage(String message, JSONObject user)
    {
        this.userId = user.optInt("id", 0);
        this.username = Misc.jsonNull(user, "username");
        this.avatar = Misc.jsonNull(user, "avatar");
        this.message = message;
    }

    public void setAvatar(final ImageView imageView)
    {
        if (!TextUtils.isEmpty(avatar)) {
            ANRequest.GetRequestBuilder request = AndroidNetworking.get(avatar);
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

    public SpannableString getSpannableString()
    {
        SpannableString msg = new SpannableString(username+" "+message);
        msg.setSpan(new ForegroundColorSpan(App.getContext().getResources().getColor(R.color.chat_username)), 0, username.length(), 0);
        msg.setSpan(new StyleSpan(BOLD), 0, username.length(), 0);
        msg.setSpan(new ForegroundColorSpan(App.getContext().getResources().getColor(R.color.chat_text)), username.length()+1, msg.length(), 0);

        return msg;
    }
}
