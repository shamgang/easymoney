package com.shamik.budget.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AddCategoryDialogFragment extends DialogFragment {
    private View mView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        mView = inflater.inflate(R.layout.dialog_fragment_add_category, null);
        Button addButton = (Button)mView.findViewById(R.id.add_category_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addCategory();
                } catch(DuplicateCategoryException e) {
                    Toast toast = Toast.makeText(getActivity(), e.getMessage(), 2000);
                    toast.show();
                    return;
                }
                // hide keyboard
                InputMethodManager imm = (InputMethodManager)getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                // close dialog
                dismiss();
                // refresh category fragment
                ((MainActivity)getActivity()).replaceFragment(new CategoryListFragment());
            }
        });
        builder.setView(mView);
        return builder.create();
    }

    public class DuplicateCategoryException extends Exception {
        public DuplicateCategoryException(String message) {
            super(message);
        }
    }

    private void addCategory() throws DuplicateCategoryException {
        TextView addCategoryNameView = (TextView)mView.findViewById(R.id.add_category_name);
        String addCategoryName = addCategoryNameView.getText().toString();

        if(BudgetDatabase.getInstance().hasCategoryName(addCategoryName)) {
            throw new DuplicateCategoryException(
                    "Category '" + addCategoryName + "' already exists");
        }

        BudgetDatabase.getInstance().createCategory(
                new Category(addCategoryNameView.getText().toString(), -1));
    }
}
