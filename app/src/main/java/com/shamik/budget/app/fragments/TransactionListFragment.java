package com.shamik.budget.app.fragments;

import android.os.Bundle;
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
        mUncategorizedTransactionListView = (ListView)view
                .findViewById(R.id.uncategorized_transactions_list);
        TransactionListHelper.fillAndResizeTransactionList((MainActivity)getActivity(),
                mUncategorizedTransactionList, mUncategorizedTransactionListView);

        // categorized list
        mTransactionList = BudgetDatabase.getInstance().getCategorizedTransactions();
        mTransactionListView = (ListView)view.findViewById(R.id.transactions_list);
        TransactionListHelper.fillAndResizeTransactionList((MainActivity)getActivity(),
                mTransactionList, mTransactionListView);

        return view;
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.transaction_list_fragment_title);
    }
}
