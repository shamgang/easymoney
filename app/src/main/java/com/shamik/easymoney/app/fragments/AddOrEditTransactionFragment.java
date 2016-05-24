package com.shamik.easymoney.app.fragments;

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

import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.BaseCategorySelectFragment;
import com.shamik.easymoney.app.data.BudgetDatabase;
import com.shamik.easymoney.app.types.Category;
import com.shamik.easymoney.app.MainActivity;
import com.shamik.easymoney.app.util.TextValidator;
import com.shamik.easymoney.app.types.Transaction;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AddOrEditTransactionFragment extends BaseCategorySelectFragment {
    private View mView;
    private EditText mAddTransactionAmount;
    private EditText mAddTransactionDescription;
    private Switch mAddTransactionIsIncome;
    private boolean isNew;
    private int mTransactionID;
    private Category mCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_or_edit_transaction, container, false);

        mAddTransactionAmount = (EditText)mView.findViewById(R.id.add_transaction_amount);
        mAddTransactionDescription = (EditText)mView.findViewById(R.id.add_transaction_description);
        Button categorizeButton = (Button)mView.findViewById(R.id.transaction_categorize_button);
        mAddTransactionIsIncome = (Switch)mView.findViewById(R.id.add_transaction_is_income);

        // if editing, fill default values
        isNew = getArguments().getBoolean(MainActivity.TRANSACTION_IS_NEW_TAG);
        if(!isNew) {
            // get current transaction from database
            mTransactionID = getArguments().getInt(MainActivity.TRANSACTION_ID_TAG);
            Transaction transaction = BudgetDatabase.getInstance()
                    .getTransactionByID(mTransactionID);
            mCategory = transaction.getCategory();
            // pad cents with 0 if necessary
            String amountCents = transaction.getAmountCents().toString();
            if(amountCents.length() == 1) {
                // single digit, add leading zero
                amountCents = "0" + amountCents;
            }
            mAddTransactionAmount.setText(transaction.getAmountDollars().toString() + "."
                    + amountCents);
            mAddTransactionDescription.setText(transaction.getDescription());
            categorizeButton.setText(mCategory.getName());
        }

        // validate amount input
        mAddTransactionAmount.addTextChangedListener(new TextValidator(mAddTransactionAmount) {
            @Override
            public void validate(TextView textView, String text) {
                int decPos = text.lastIndexOf('.');
                if(decPos != -1 && text.length() - decPos == 3) {
                    // if decimal has been entered and two digits after, prevent entry and advance
                    mAddTransactionDescription.requestFocus();
                } else if(decPos != -1 && text.length() - decPos > 3) {
                    // if more digits are entered, remove them and advance
                    mAddTransactionAmount.setText(text.substring(0, text.length()-1));
                    mAddTransactionDescription.requestFocus();
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
                args.putString(MainActivity.PARENT_FRAGMENT_TAG_TAG, getTag());
                selectCategoryDialogFragment.setArguments(args);
                selectCategoryDialogFragment.show(getActivity().getSupportFragmentManager(),
                        getActivity().getString(R.string.select_category_dialog_fragment_title));
            }
        });

        /*
        // TODO: requires mimimum api level 14->16, still supporting for now
        // set switch colors on click
        mAddTransactionIsIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAddTransactionIsIncome.isChecked()) {
                    // green on income
                    mAddTransactionIsIncome.setThumbDrawable(getResources()
                            .getDrawable(R.drawable.green_background));
                } else {
                    // blue on expense
                    mAddTransactionIsIncome.setThumbDrawable(getResources()
                            .getDrawable(R.drawable.light_blue_background));
                }
            }
        });
        */

        // save button behavior
        Button saveButton = (Button)mView.findViewById(R.id.transaction_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make a new transaction or show toast for parse error
                try {
                    addOrUpdateTransaction();
                } catch(NumberFormatException e) {
                    Toast toast = Toast.makeText(getActivity(),
                            "Please enter a valid amount.", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                // hide keyboard
                InputMethodManager imm = (InputMethodManager)getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                // go back to previous fragment
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // cancel button behavior
        Button cancelButton = (Button)mView.findViewById(R.id.transaction_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back to previous fragment
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return mView;
    }

    public void setCategory(Category category) {
        Button categorizeButton = (Button)mView.findViewById(R.id.transaction_categorize_button);
        mCategory = category;
        // fill category text view
        categorizeButton.setText(mCategory.getName());
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.add_transaction_fragment_title);
    }

    private void addOrUpdateTransaction() throws NumberFormatException {
        // parse amount
        String amountText = mAddTransactionAmount.getText().toString();
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
                mAddTransactionDescription.getText().toString(),
                (mCategory == null) ? -1 : mCategory.getID(),
                mAddTransactionIsIncome.isChecked()
        );
        // Add or update an entry
        if(isNew) {
            BudgetDatabase.getInstance().createTransaction(newTransaction);
        } else {
            BudgetDatabase.getInstance().updateTransactionByID(mTransactionID, newTransaction);
        }
    }
}
