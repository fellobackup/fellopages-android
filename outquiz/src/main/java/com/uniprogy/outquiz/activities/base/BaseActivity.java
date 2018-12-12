package com.uniprogy.outquiz.activities.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.uniprogy.outquiz.R;

import java.util.Arrays;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showErrors(String[] errors)
    {
        if(errors.length > 0)
        {
            String error = errors[0];

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.tr_error)
                    .setMessage(error)
                    .setCancelable(false)
                    .setPositiveButton(R.string.tr_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();

            if(errors.length > 1) {
                String[] copyerrors = new String[errors.length - 1];
                copyerrors = Arrays.copyOfRange(errors, 1, errors.length);
                showErrors(copyerrors);
            }
        }
    }

}
