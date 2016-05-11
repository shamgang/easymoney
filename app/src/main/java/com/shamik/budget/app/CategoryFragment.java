package com.shamik.budget.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Created by Shamik on 5/11/2016.
 */
public class CategoryFragment extends BaseFullscreenFragment {
    private ListView mSubcategoryList;
    private ListView mTransactionList;
    private String mTitle;

    @Override
    public void onStart() {
        mTitle = ((MainActivity)getActivity())
                .mCategoryStubList.get(getArguments().getInt("position")).getName();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    protected String getTitle() {
        return mTitle;
    }
}
