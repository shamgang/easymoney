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
                addCategory();
                // hide keyboard
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mView.getWindowToken(), 0);
                // close dialog
                dismiss();
            }
        });
        builder.setView(mView);
        return builder.create();
    }

    private void addCategory() {
        TextView addCategoryName = (TextView)mView.findViewById(R.id.add_category_name);

        ((MainActivity)getActivity()).mCategoryStubList.add(0,
                new Category(null, addCategoryName.getText().toString()));
    }
}