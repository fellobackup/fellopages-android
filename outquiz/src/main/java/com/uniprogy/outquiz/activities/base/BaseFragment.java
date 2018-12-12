package com.uniprogy.outquiz.activities.base;

import android.app.Fragment;

public class BaseFragment extends Fragment {

    public void showErrors(String[] errors)
    {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showErrors(errors);
    }

}
