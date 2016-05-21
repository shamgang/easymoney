package com.shamik.easymoney.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shamik.easymoney.app.MainActivity;
import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.BaseCategorySelectFragment;
import com.shamik.easymoney.app.data.BudgetDatabase;
import com.shamik.easymoney.app.types.Category;
import com.shamik.easymoney.app.adapters.CategoryAdapter;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/11/2016.
 */
public class SelectCategoryDialogFragment extends DialogFragment
        implements AdapterView.OnItemClickListener {
    private ArrayList<Category> mCategoryList;
    private ListView mCategoryListView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // list all categories
        // TODO: paginate
        mCategoryList = BudgetDatabase.getInstance().getAllCategories();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.fragment_list, null);
        mCategoryListView = (ListView)view.findViewById(R.id.list);
        mCategoryListView.setAdapter(new CategoryAdapter(getActivity(), mCategoryList));
        mCategoryListView.setOnItemClickListener(this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO: right now there are no children, because nesting isn't implemented
        if(!mCategoryList.get(position).hasChildren()) {
            // no children, we've selected a category
            // call back to addOrEditTransactionFragment, which must exist
            BaseCategorySelectFragment parentFragment =
                    (BaseCategorySelectFragment)getActivity().getSupportFragmentManager()
                    .findFragmentByTag(
                            getArguments().getString(MainActivity.PARENT_FRAGMENT_TAG_TAG));
            parentFragment.setCategory(mCategoryList.get(position));
            dismiss();
        } else {
            // reset list to children of category
            mCategoryList = mCategoryList.get(position).getChildren();
            mCategoryListView.setAdapter(new CategoryAdapter(getActivity(), mCategoryList));
        }
    }
}
