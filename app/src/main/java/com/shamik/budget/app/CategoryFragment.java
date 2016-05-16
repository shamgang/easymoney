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

    @Override
    public void onStart() {
        mTitle = BudgetDatabase.getInstance()
                .getCategoryByID(getArguments().getInt(getActivity().getString(R.string.category_id_tag)))
                .getName();
        Log.d(CategoryFragment.class.getName(), mTitle);
        super.onStart();

        mTransactionList = BudgetDatabase.getInstance()
                .getTransactionsWhere(BudgetDatabase.COLUMN_CATEGORY + "='" + mTitle + "'");

        // set adapter and click listener
        mTransactionListView = (ListView)getView().findViewById(R.id.category_transaction_list);
        mTransactionListView.setAdapter(new TransactionAdapter(getActivity(), mTransactionList));
        mTransactionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        // set list height to cover all items
        View itemView = mTransactionListView.getAdapter().getView(0, null, (ViewGroup)mTransactionListView);
        itemView.measure(0, 0);
        ViewGroup.LayoutParams layoutParams = mTransactionListView.getLayoutParams();
        layoutParams.height = itemView.getMeasuredHeight() * mTransactionList.size();
        mTransactionListView.setLayoutParams(layoutParams);
        mTransactionListView.requestLayout();
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
