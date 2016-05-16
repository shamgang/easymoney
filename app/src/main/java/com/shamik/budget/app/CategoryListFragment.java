package com.shamik.budget.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Created by Shamik on 5/5/2016.
 */
public class CategoryListFragment extends BaseFullscreenFragment implements OnItemClickListener {
    private ListView mCategoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // populate list and set item click listener
        /*String[] budgetListItems = new String[] { "Transportation", "Food", "Entertainment",
            "Rent", "Gear", "Transportation", "Food", "Entertainment",
            "Rent", "Gear", "Transportation", "Food", "Entertainment",
            "Rent", "Gear", "Transportation", "Food", "Entertainment",
            "Rent", "Gear", "Transportation", "Food", "Entertainment",
            "Rent", "Gear"};
        */
        mCategoryList = (ListView)getView().findViewById(R.id.list);
        mCategoryList.setAdapter(new CategoryAdapter(getActivity(),
                ((MainActivity)getActivity()).mCategoryStubList));
        mCategoryList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((MainActivity)getActivity()).selectCategory(position);
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.category_list_fragment_title);
    }
}
