package com.shamik.budget.app;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

/**
 * Created by Shamik on 5/4/2016.
 */
public class TransactionListFragment extends ListFragment implements OnItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // populate list and set item click listener
        String[] budgetListItems = new String[] { "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover", "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover", "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover", "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover", "Food - meijer", "Transportation - gas",
                "Entertainment - bar cover"};
        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, budgetListItems));
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        MainActivity mainActivity = (MainActivity)getActivity();
        mainActivity.selectTransaction();
    }
}
