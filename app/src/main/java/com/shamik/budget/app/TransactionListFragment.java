package com.shamik.budget.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/4/2016.
 */
public class TransactionListFragment extends BaseFullscreenFragment implements OnItemClickListener {
    private ArrayList<Transaction> mTransactionList;

    private ListView mTransactionListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // fill list from database
        // TODO: paginate
        mTransactionList = BudgetDatabase.getInstance().getAllTransactions();

        // set adapter and listener on list view
        mTransactionListView = (ListView)view.findViewById(R.id.list);
        mTransactionListView.setAdapter(new TransactionAdapter(getActivity(), mTransactionList));
        mTransactionListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ((MainActivity)getActivity()).selectTransaction(mTransactionList.get(position).getID());
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.transaction_list_fragment_title);
    }
}
