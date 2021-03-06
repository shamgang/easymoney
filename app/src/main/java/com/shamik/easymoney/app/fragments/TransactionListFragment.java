package com.shamik.easymoney.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.BaseFullscreenFragment;
import com.shamik.easymoney.app.data.BudgetDatabase;
import com.shamik.easymoney.app.MainActivity;
import com.shamik.easymoney.app.types.Transaction;
import com.shamik.easymoney.app.util.TransactionListHelper;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/4/2016.
 */
public class TransactionListFragment extends BaseFullscreenFragment {
    private ArrayList<Transaction> mUncategorizedTransactionList;
    private ArrayList<Transaction> mTransactionList;

    private ListView mUncategorizedTransactionListView;
    private ListView mTransactionListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        // fill lists from database
        // TODO: paginate

        // uncategorized list
        mUncategorizedTransactionList = BudgetDatabase.getInstance().getUncategorizedTransactions();
        if(mUncategorizedTransactionList.isEmpty()) {
            // remove if empty
            view.findViewById(R.id.uncategorized_wrapper).setVisibility(View.GONE);
        } else {
            mUncategorizedTransactionListView = (ListView)view
                    .findViewById(R.id.uncategorized_transactions_list);
            TransactionListHelper.fillAndResizeTransactionList((MainActivity) getActivity(),
                    mUncategorizedTransactionList, mUncategorizedTransactionListView);
        }

        // categorized list
        mTransactionList = BudgetDatabase.getInstance().getCategorizedTransactions();
        mTransactionListView = (ListView) view.findViewById(R.id.transactions_list);
        mTransactionListView.setEmptyView(view.findViewById(R.id.empty_transactions_list));
        TransactionListHelper.fillAndResizeTransactionList((MainActivity) getActivity(),
                mTransactionList, mTransactionListView);

        return view;
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.transaction_list_fragment_title);
    }
}
