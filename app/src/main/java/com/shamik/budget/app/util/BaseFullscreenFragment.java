package com.shamik.budget.app.util;

import android.support.v4.app.Fragment;

import com.shamik.budget.app.MainActivity;

/**
 * Created by Shamik on 5/6/2016.
 */
public abstract class BaseFullscreenFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.updateActionBar(getTitle());
    }

    protected abstract String getTitle();

}
