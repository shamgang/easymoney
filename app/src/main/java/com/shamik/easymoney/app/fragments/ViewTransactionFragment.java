package com.shamik.easymoney.app.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.BaseFullscreenFragment;
import com.shamik.easymoney.app.data.BudgetDatabase;
import com.shamik.easymoney.app.MainActivity;
import com.shamik.easymoney.app.types.Transaction;

/**
 * Created by Shamik on 5/6/2016.
 */
public class ViewTransactionFragment extends BaseFullscreenFragment {
    Transaction mTransaction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // allows onCreateOptionsMenu to be called
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get current transaction
        final int transactionID = getArguments().getInt(MainActivity.TRANSACTION_ID_TAG);
        mTransaction = BudgetDatabase.getInstance()
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
        String amountCents = mTransaction.getAmountCents().toString();
        if(amountCents.length() == 1) {
            // single digit, add leading zero
            amountCents = "0" + amountCents;
        }
        viewTransactionAmount.setText(mTransaction.getAmountDollars().toString() + "."
                + amountCents);
        // description
        viewTransactionDescription.setText(mTransaction.getDescription());
        // category
        viewTransactionCategory.setText(mTransaction.getCategory().getName());
        // expense/income
        viewTransactionIsIncome.setText(mTransaction.isIncome() ? "Income" : "Expense");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // add delete icon to action bar
        MenuItem trash = menu.add(R.string.delete_menu_item);
        trash.setIcon(R.drawable.ic_delete);
        trash.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        // open confirmation alert onclick
        trash.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                View view = getActivity().getLayoutInflater()
                        .inflate(R.layout.alert_dialog_delete, null);
                new AlertDialog.Builder(getActivity())
                        .setView(view)
                        .setPositiveButton(R.string.delete_menu_item,
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // delete
                                BudgetDatabase.getInstance()
                                        .deleteTransaction(mTransaction.getID());
                                // return to previous fragment
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        })
                        .setNegativeButton(R.string.cancel_button,
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // cancel, do nothing
                            }
                        })
                        .show();
                // handling done, don't propagate
                return true;
            }
        });
    }
}
