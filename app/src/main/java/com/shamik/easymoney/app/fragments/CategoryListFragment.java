package com.shamik.easymoney.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.BaseFullscreenFragment;
import com.shamik.easymoney.app.data.BudgetDatabase;
import com.shamik.easymoney.app.types.Category;
import com.shamik.easymoney.app.adapters.CategoryAdapter;
import com.shamik.easymoney.app.MainActivity;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/5/2016.
 */
public class CategoryListFragment extends BaseFullscreenFragment implements OnItemClickListener {
    private ArrayList<Category> mCategoryList;
    private ListView mCategoryListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        // populate list from database
        // TODO: paginate
        mCategoryList = BudgetDatabase.getInstance().getAllCategories();

        // set list view adapter and click listener
        mCategoryListView = (ListView)view.findViewById(R.id.category_list);
        mCategoryListView.setEmptyView(view.findViewById(R.id.empty_category_list));
        mCategoryListView.setAdapter(new CategoryAdapter(getActivity(), mCategoryList));
        mCategoryListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((MainActivity)getActivity()).selectCategory(mCategoryList.get(position).getID());
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.category_list_fragment_title);
    }
}
