package com.uniprogy.outquiz.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.uniprogy.outquiz.App;
import com.uniprogy.outquiz.activities.MainActivity;
import com.uniprogy.outquiz.R;
import com.uniprogy.outquiz.models.Player;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Misc {

    private static Player currentPlayer;

    public static Player getCurrentPlayer() {
        if(currentPlayer == null)
        {
            currentPlayer = getPlayer();
        }
        return currentPlayer;
    }

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
        savePlayer(player);
    }

    public static void saveToken(String token)
    {
        String key = App.getContext().getResources().getString(R.string.shared_key);
        SharedPreferences sharedPref = App.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public static String getToken()
    {
        String key = App.getContext().getResources().getString(R.string.shared_key);
        SharedPreferences sharedPref = App.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        return sharedPref.getString("token", null);
    }

    public static void savePlayer(Player player)
    {
        String key = App.getContext().getResources().getString(R.string.shared_key) + ".currentPlayer";
        SharedPreferences sharedPref = App.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if(player != null)
        {
            editor.putString("id", String.valueOf(player.id));
            editor.putString("token", player.token);
            editor.putString("username", player.username);
            editor.putString("avatar", player.avatar);
            editor.putString("referral", player.referral);
            editor.commit();
        }
        else
        {
            editor.clear();
            editor.commit();
        }
    }

    public static Player getPlayer()
    {
        String key = App.getContext().getResources().getString(R.string.shared_key) + ".currentPlayer";
        SharedPreferences sharedPref = App.getContext().getSharedPreferences(key, Context.MODE_PRIVATE);

        Map<String,String> data = new HashMap<String,String>();
        if(sharedPref.getString("id", null) != null) {
            data.put("id", sharedPref.getString("id", null));
            data.put("token", sharedPref.getString("token", null));
            data.put("username", sharedPref.getString("username", null));
            data.put("avatar", sharedPref.getString("avatar", null));
            data.put("referral", sharedPref.getString("referral", null));

            Player player = new Player(data);
            return player;
        }
        return null;
    }

    public static void logout()
    {
        // empty stored user
        setCurrentPlayer(null);
//        toMain();
    }

    public static void toMain()
    {
        // rollback to initial activity
        Intent intent = new Intent(App.getContext(), MainActivity.class);
        intent.putExtra("close",true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        App.getContext().startActivity(intent);
    }

    public static String docUrl(String type) {
        String host = App.getContext().getResources().getString(R.string.app_host);
        switch (type) {
            case "terms":
                return host + "/page/terms";
            case "privacy":
                return host + "/page/privacy";
            case "faq":
                return host + "/page/faq";
            case "rules":
                return host + "/page/rules";
            default:
                return host;
        }
    }

    public static String moneyFormat(int amount)
    {
        String format = amount%100.0 > 0 ? "%1$s%2$.2f" : "%1$s%2$.0f";
        return String.format(format, App.getContext().getResources().getString(R.string.app_currencySymbol), amount/100.0f);
    }

    public static String streamingUrl()
    {
        Resources res = App.getContext().getResources();
        return "rtsp://" + res.getString(R.string.wowza_host)
            + ":" + res.getString(R.string.wowza_port)
            + "/" + res.getString(R.string.wowza_application)
            + "/show";
    }

    public static int colorWithAlpha(double newAlpha, int color)
    {
        int alpha = (int)(newAlpha * 255);
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static String jsonNull(JSONObject data, String key)
    {
        String value = data.isNull(key) ? null : data.optString(key);
        return stringNull(value);
    }

    public static String stringNull(String value)
    {
        return TextUtils.isEmpty(value) ? null : value;
    }

    public static int pxToDp(int px) {
        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static int dpToPx(int dp) {
        DisplayMetrics displayMetrics = App.getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
