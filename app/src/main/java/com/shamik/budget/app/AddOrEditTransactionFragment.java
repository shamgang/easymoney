package com.shamik.budget.app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AddOrEditTransactionFragment extends BaseCategorySelectFragment {
    private View mView;
    private boolean isNew;
    private int mTransactionID;
    private Category mCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_or_edit_transaction, container, false);

        final EditText addTransactionAmount =
                (EditText)mView.findViewById(R.id.add_transaction_amount);
        final EditText addTransactionDescription =
                (EditText)mView.findViewById(R.id.add_transaction_description);
        Button categorizeButton = (Button)mView.findViewById(R.id.transaction_categorize_button);

        // if editing, fill default values
        isNew = getArguments().getBoolean(getActivity().getString(R.string.transaction_is_new_tag));
        if(!isNew) {
            // get current transaction from database
            mTransactionID = getArguments()
                    .getInt(getActivity().getString(R.string.transaction_id_tag));
            Transaction transaction = BudgetDatabase.getInstance()
                    .getTransactionByID(mTransactionID);
            mCategory = transaction.getCategory();
            // pad cents with 0 if necessary
            addTransactionAmount.setText(transaction.getAmountDollars().toString() + "."
                    + (transaction.getAmountCents().toString() + "0").substring(0, 2));
            addTransactionDescription.setText(transaction.getDescription());
            categorizeButton.setText(mCategory.getName());
        }

        // validate amount input
        addTransactionAmount.addTextChangedListener(new TextValidator(addTransactionAmount) {
            @Override
            public void validate(TextView textView, String text) {
                int decPos = text.lastIndexOf('.');
                if(decPos != -1 && text.length() - decPos == 3) {
                    // if decimal has been entered and two digits after, prevent entry and advance
                    addTransactionDescription.requestFocus();
                } else if(decPos != -1 && text.length() - decPos > 3) {
                    // if more digits are entered, remove them and advance
                    addTransactionAmount.setText(text.substring(0, text.length()-1));
                    addTransactionDescription.requestFocus();
                }
            }
        });
        // categorize button behavior
        categorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open category list as selection modal
                SelectCategoryDialogFragment selectCategoryDialogFragment
                        = new SelectCategoryDialogFragment();
                Bundle args = new Bundle();
                args.putString(getActivity().getString(R.string.parent_fragment_tag_tag), getTag());
                selectCategoryDialogFragment.setArguments(args);
                selectCategoryDialogFragment.show(getActivity().getSupportFragmentManager(),
                        getActivity().getString(R.string.select_category_dialog_fragment_title));
            }
        });
        // save button behavior
        Button saveButton = (Button)mView.findViewById(R.id.transaction_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addOrUpdateTransaction();
                } catch(NumberFormatException e) {
                    Toast toast =
                            Toast.makeText(getActivity(), "Please enter a valid amount.", 2000);
                    toast.show();
                    return;
                }
                // hide keyboard
                InputMethodManager imm = (InputMethodManager)getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                // return to transaction list
                ((MainActivity)getActivity())
                        .replaceFragmentWithBackstack(new TransactionListFragment());
            }
        });
        // cancel button behavior
        Button cancelButton = (Button)mView.findViewById(R.id.transaction_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return to transaction list
                ((MainActivity)getActivity())
                        .replaceFragmentWithBackstack(new TransactionListFragment());
            }
        });
        return mView;
    }

    public void setCategory(Category category) {
        Button categorizeButton = (Button)mView.findViewById(R.id.transaction_categorize_button);
        mCategory = category;
        categorizeButton.setText(mCategory.getName());
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.add_transaction_fragment_title);
    }

    private void addOrUpdateTransaction() throws NumberFormatException {
        TextView addTransactionAmount =
                (TextView)getView().findViewById(R.id.add_transaction_amount);
        TextView addTransactionDescription =
                (TextView)getView().findViewById(R.id.add_transaction_description);
        Switch addTransactionIsIncome =
                (Switch)getView().findViewById(R.id.add_transaction_is_income);

        // parse amount
        String amountText = addTransactionAmount.getText().toString();
        int decPos = amountText.lastIndexOf('.');
        int amountDollars, amountCents;
        if(decPos == -1) {
            // no decimal, save round dollar amount
            amountDollars = Integer.parseInt(amountText);
            amountCents = 0;
        } else if(decPos == amountText.length() - 1) {
            // terminating decimal, remove and save round dollar amount
            amountDollars = Integer.parseInt(amountText.substring(0, amountText.length() - 1));
            amountCents = 0;
        } else if(decPos == 0) {
            // leading decimal, remove and save cent amount
            amountDollars = 0;
            amountCents = Integer.parseInt(amountText.substring(1));
        } else {
            // parse substrings around decimal
            amountDollars = Integer.parseInt(amountText.substring(0, decPos));
            amountCents = Integer.parseInt(amountText.substring(decPos + 1, amountText.length()));
        }

        // Make a new transaction to send to database
        Transaction newTransaction = new Transaction(
                amountDollars,
                amountCents,
                addTransactionDescription.getText().toString(),
                (mCategory == null) ? -1 : mCategory.getID(),
                addTransactionIsIncome.isChecked()
        );
        // Add or update an entry
        if(isNew) {
            BudgetDatabase.getInstance().createTransaction(newTransaction);
        } else {
            BudgetDatabase.getInstance().updateTransactionByID(mTransactionID, newTransaction);
        }
    }
}
