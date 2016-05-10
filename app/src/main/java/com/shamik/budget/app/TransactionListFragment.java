package com.shamik.budget.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Created by Shamik on 5/4/2016.
 */
public class TransactionListFragment extends BaseFullscreenFragment implements OnItemClickListener {

    private ListView mTransactionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // populate list and set item click listener
        /*
        String[] budgetListItems = new String[] { "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover", "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover", "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover", "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover", "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover"};
        */
        mTransactionList = (ListView)getView().findViewById(R.id.list);
        mTransactionList.setAdapter(new TransactionAdapter(getActivity(),
                ((MainActivity)getActivity()).mTransactionStubList));
        mTransactionList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.selectTransaction(position);
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.transaction_list_fragment_title);
    }
}
