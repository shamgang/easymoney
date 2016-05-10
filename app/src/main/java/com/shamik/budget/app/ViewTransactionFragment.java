package com.shamik.budget.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Shamik on 5/6/2016.
 */
public class ViewTransactionFragment extends BaseFullscreenFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int position = getArguments().getInt("position");
        Transaction transaction = ((MainActivity)getActivity()).mTransactionStubList.get(position);
        View view = inflater.inflate(R.layout.fragment_view_transaction, container, false);
        TextView viewTransactionAmount = (TextView)view.findViewById(R.id.view_transaction_amount);
        viewTransactionAmount.setText(transaction.getAmountDollars().toString() + "." + transaction.getAmountCents().toString());
        TextView vewTransactionDescription = (TextView)view.findViewById(R.id.view_transaction_description);
        vewTransactionDescription.setText(transaction.getDescription());
        TextView viewTransactionCategory = (TextView)view.findViewById(R.id.view_transaction_category);
        viewTransactionCategory.setText(transaction.getCategory().getName());
        return view;
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.view_transaction_fragment_title);
    }
}
