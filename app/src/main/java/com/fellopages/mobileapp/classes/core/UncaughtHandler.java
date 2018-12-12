package com.fellopages.mobileapp.classes.core;

/**
 * Created by bigstep on 12/3/18.
 */

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import com.fellopages.mobileapp.R;

public class UncaughtHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Activity mContext;

    public UncaughtHandler(Activity context) {
        mContext = context;
    }

    public void uncaughtException(Thread thread, Throwable exception) {


        Intent intent = new Intent(mContext, SafeHandler.class);
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append(stackTrace.toString());
        intent.putExtra("message",errorReport.toString());
        intent.putExtra("error_code",exception.getMessage());
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

}
