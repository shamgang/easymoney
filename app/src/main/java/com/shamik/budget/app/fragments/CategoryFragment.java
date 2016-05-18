package com.shamik.budget.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.shamik.budget.app.util.BaseFullscreenFragment;
import com.shamik.budget.app.data.BudgetDatabase;
import com.shamik.budget.app.MainActivity;
import com.shamik.budget.app.R;
import com.shamik.budget.app.types.Transaction;
import com.shamik.budget.app.adapters.TransactionAdapter;
import com.shamik.budget.app.util.TransactionListHelper;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/11/2016.
 */
public class CategoryFragment extends BaseFullscreenFragment {
    private ListView mSubcategoryListView;
    private ListView mTransactionListView;
    private ArrayList<Transaction> mTransactionList;
    private String mTitle;

    @Override
    public void onStart() {
        int id = getArguments().getInt(MainActivity.CATEGORY_ID_TAG);
        mTitle = BudgetDatabase.getInstance().getCategoryByID(id).getName();
        super.onStart();

        // list view
        mTransactionList = BudgetDatabase.getInstance().getTransactionsByCategoryID(id);
        mTransactionListView = (ListView)getView().findViewById(R.id.category_transaction_list);
        TransactionListHelper.fillAndResizeTransactionList((MainActivity)getActivity(),
                mTransactionList, mTransactionListView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    protected String getTitle() {
        return mTitle;
    }
}
