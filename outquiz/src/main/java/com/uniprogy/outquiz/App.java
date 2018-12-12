package com.uniprogy.outquiz;

import android.app.Application;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        AndroidNetworking.initialize(mContext);
    }

    public static void  initialize(Context context){
        mContext = context;
        AndroidNetworking.initialize(mContext);
    }
    public static Context getContext(){
        return mContext;
    }

}
