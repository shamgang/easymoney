package com.shamik.budget.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AddTransactionFragment extends BaseFullscreenFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        Button saveButton = (Button)view.findViewById(R.id.transaction_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaction();
                // hide keyboard
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                // return to transaction list
                ((MainActivity)getActivity()).selectNavItem(0);
            }
        });
        return view;
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.add_transaction_fragment_title);
    }

    private void addTransaction() {
        TextView addTransactionAmountWhole = (TextView)getView().findViewById(R.id.add_transaction_amount_whole);
        TextView addTransactionAmountDecimal = (TextView)getView().findViewById(R.id.add_transaction_amount_decimal);
        TextView addTransactionDescription = (TextView)getView().findViewById(R.id.add_transaction_description);
        Switch addTransactionIsIncome = (Switch)getView().findViewById(R.id.add_transaction_is_income);

        /*
        ((MainActivity)getActivity()).mTransactionStubList.add(0, new Transaction(
            addTransactionAmountWhole.getText().toString(),
            addTransactionAmountDecimal.getText().toString(),
            addTransactionDescription.getText().toString(),
            null,
            addTransactionIsIncome.isChecked()
        ));
        */
        ((MainActivity)getActivity()).mBudgetDatabase.createTransaction(new Transaction(
            addTransactionAmountWhole.getText().toString(),
            addTransactionAmountDecimal.getText().toString(),
            addTransactionDescription.getText().toString(),
            null,
            addTransactionIsIncome.isChecked()
        ));
        ((MainActivity)getActivity()).refreshTransactionList();
    }
}
