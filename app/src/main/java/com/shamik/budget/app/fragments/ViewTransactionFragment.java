package com.shamik.budget.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shamik.budget.app.util.BaseFullscreenFragment;
import com.shamik.budget.app.data.BudgetDatabase;
import com.shamik.budget.app.MainActivity;
import com.shamik.budget.app.R;
import com.shamik.budget.app.types.Transaction;

/**
 * Created by Shamik on 5/6/2016.
 */
public class ViewTransactionFragment extends BaseFullscreenFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get current transaction
        final int transactionID = getArguments().getInt(MainActivity.TRANSACTION_ID_TAG);
        Transaction transaction = BudgetDatabase.getInstance()
                .getTransactionByID(transactionID);

        View view = inflater.inflate(R.layout.fragment_view_transaction, container, false);
        TextView viewTransactionAmount = (TextView)view
                .findViewById(R.id.view_transaction_amount);
        TextView viewTransactionDescription = (TextView)view
                .findViewById(R.id.view_transaction_description);
        TextView viewTransactionCategory = (TextView)view
                .findViewById(R.id.view_transaction_category);
        TextView viewTransactionIsIncome = (TextView)view
                .findViewById(R.id.view_transaction_is_income);

        // amount
        // pad cents with 0 if necessary
        viewTransactionAmount.setText(transaction.getAmountDollars().toString() + "."
                + (transaction.getAmountCents().toString() + "0").substring(0, 2));
        // description
        viewTransactionDescription.setText(transaction.getDescription());
        // category
        viewTransactionCategory.setText(transaction.getCategory().getName());
        // expense/income
        viewTransactionIsIncome.setText(transaction.isIncome() ? "Income" : "Expense");
        // save button behavior
        Button editButton = (Button)view.findViewById(R.id.transaction_edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).editTransaction(transactionID);
            }
        });
        return view;
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.view_transaction_fragment_title);
    }
}
