package com.shamik.budget.app;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AddTransactionFragment extends BaseFullscreenFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        // validate amount input
        final EditText addTransactionAmount = (EditText)view.findViewById(R.id.add_transaction_amount);
        final EditText addTransactionDescription = (EditText)view.findViewById(R.id.add_transaction_description);
        addTransactionAmount.addTextChangedListener(new TextValidator(addTransactionAmount) {
            @Override
            public void validate(TextView textView, String text) {
                int decPos = text.lastIndexOf('.');
                if(decPos != -1 && text.length() - decPos == 3) {
                    // if decimal has been entered and two digits after, prevent entry and advance
                    addTransactionDescription.requestFocus();
                }
                else if(decPos != -1 && text.length() - decPos > 3) {
                    // if more digits are entered, remove them and advance
                    addTransactionAmount.setText(text.substring(0, text.length()-1));
                    addTransactionDescription.requestFocus();
                }
            }
        });
        // save button behavior
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
        Button cancelButton = (Button)view.findViewById(R.id.transaction_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        TextView addTransactionAmount = (TextView)getView().findViewById(R.id.add_transaction_amount);
        TextView addTransactionDescription = (TextView)getView().findViewById(R.id.add_transaction_description);
        Switch addTransactionIsIncome = (Switch)getView().findViewById(R.id.add_transaction_is_income);

        // parse amount
        String amountText = addTransactionAmount.getText().toString();
        int decPos = amountText.lastIndexOf('.');
        int amountDollars, amountCents;
        if(decPos == -1) {
            // no decimal, round dollar amount
            amountDollars = Integer.parseInt(amountText);
            amountCents = 0;
        }
        else {
            // parse substrings around decimal
            amountDollars = Integer.parseInt(amountText.substring(0, decPos));
            amountCents = Integer.parseInt(amountText.substring(decPos + 1, amountText.length()));
        }

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
            amountDollars,
            amountCents,
            addTransactionDescription.getText().toString(),
            null,
            addTransactionIsIncome.isChecked()
        ));
        ((MainActivity)getActivity()).refreshTransactionList();
    }
}
