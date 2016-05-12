package com.shamik.budget.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.sql.BatchUpdateException;
import java.util.ArrayList;

/**
 * Created by Shamik on 5/11/2016.
 */
public class CategoryFragment extends BaseFullscreenFragment {
    private ListView mSubcategoryListView;
    private ListView mTransactionListView;
    private ArrayList<Transaction> mTransactionList;
    private String mTitle;

    private static final String TAG = "CategoryFragment";

    @Override
    public void onStart() {
        mTitle = ((MainActivity)getActivity())
                .mCategoryStubList.get(getArguments().getInt("position")).getName();
        Log.d(TAG, mTitle);
        super.onStart();

        mTransactionList = ((MainActivity)getActivity()).mBudgetDatabase
                .getTransactionsWhere(BudgetDatabaseHelper.COLUMN_CATEGORY + "='" + mTitle + "'");

        mTransactionListView = (ListView)getView().findViewById(R.id.category_transaction_list);
        mTransactionListView.setAdapter(new TransactionAdapter(getActivity(), mTransactionList));
        // set list height to cover all items
        View itemView = mTransactionListView.getAdapter().getView(0, null, (ViewGroup)mTransactionListView);
        itemView.measure(0, 0);
        ViewGroup.LayoutParams layoutParams = mTransactionListView.getLayoutParams();
        layoutParams.height = itemView.getMeasuredHeight() * mTransactionList.size();
        mTransactionListView.setLayoutParams(layoutParams);
        mTransactionListView.requestLayout();

        mTransactionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
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
