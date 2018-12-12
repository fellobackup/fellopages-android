package com.uniprogy.outquiz.helpers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class LoseFocusEditText extends EditText {

    private Context mContext;

    protected final String TAG = getClass().getName();

    public LoseFocusEditText(Context context) {
        super(context);
        mContext = context;
    }

    public LoseFocusEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public LoseFocusEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            //lose focus
            this.clearFocus();

            return true;
        }
        return false;
    }
}
