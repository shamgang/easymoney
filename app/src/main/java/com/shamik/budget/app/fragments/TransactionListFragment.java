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

        mUncategorizedTransactionList = BudgetDatabase.getInstance().getUncategorizedTransactions();

        // set adapter and listener on list view
        mUncategorizedTransactionListView = (ListView)view
                .findViewById(R.id.uncategorized_transactions_list);
        mUncategorizedTransactionListView.setAdapter(
                new TransactionAdapter(getActivity(), mUncategorizedTransactionList));
        mUncategorizedTransactionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity())
                        .selectTransaction(mUncategorizedTransactionList.get(i).getID());
            }
        });

        // set list height to cover all items
        if(!mUncategorizedTransactionListView.getAdapter().isEmpty()) {
            View itemView = mUncategorizedTransactionListView.getAdapter()
                    .getView(0, null, (ViewGroup) mUncategorizedTransactionListView);
            itemView.measure(0, 0);
            ViewGroup.LayoutParams layoutParams
                    = mUncategorizedTransactionListView.getLayoutParams();
            layoutParams.height
                    = itemView.getMeasuredHeight() * mUncategorizedTransactionList.size();
            mUncategorizedTransactionListView.setLayoutParams(layoutParams);
            mUncategorizedTransactionListView.requestLayout();
        }

        mTransactionList = BudgetDatabase.getInstance().getCategorizedTransactions();

        // set adapter and listener on list view
        mTransactionListView = (ListView)view.findViewById(R.id.transactions_list);
        mTransactionListView.setAdapter(new TransactionAdapter(getActivity(), mTransactionList));
        mTransactionListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity()).selectTransaction(mTransactionList.get(i).getID());
            }
        });

        // set list height to cover all items
        if(!mTransactionListView.getAdapter().isEmpty()) {
            View itemView = mTransactionListView.getAdapter()
                    .getView(0, null, (ViewGroup) mTransactionListView);
            itemView.measure(0, 0);
            ViewGroup.LayoutParams layoutParams = mTransactionListView.getLayoutParams();
            layoutParams.height = itemView.getMeasuredHeight() * mTransactionList.size();
            mTransactionListView.setLayoutParams(layoutParams);
            mTransactionListView.requestLayout();
        }

        return view;
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.transaction_list_fragment_title);
    }
}
