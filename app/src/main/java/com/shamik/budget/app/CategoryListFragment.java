package com.shamik.budget.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/5/2016.
 */
public class CategoryListFragment extends BaseFullscreenFragment implements AdapterView.OnItemClickListener {

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
        ArrayList<Category> categoryStubList = new ArrayList<Category>();
        for(int i = 0; i < 30; ++i) {
            categoryStubList.add(new Category(null, "Blank"));
        }
        mCategoryList = (ListView)getView().findViewById(R.id.list);
        mCategoryList.setAdapter(new CategoryAdapter(getActivity(), categoryStubList));
        mCategoryList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.selectCategory();
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.category_list_fragment_title);
    }
}
