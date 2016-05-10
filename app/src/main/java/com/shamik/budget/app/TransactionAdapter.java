package com.shamik.budget.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Shamik on 5/7/2016.
 */
public class TransactionAdapter extends ArrayAdapter<Transaction> {
    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        super(context, 0, transactions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // get data item
        Transaction transaction = getItem(position);
        // inflate view unless one is being reused
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
        }
        // populate the correct views
        TextView transactionAmountWhole = (TextView)convertView.findViewById(R.id.transaction_item_amount_whole);
        TextView transactionAmountDecimal = (TextView)convertView.findViewById(R.id.transaction_item_amount_decimal);
        TextView transactionDescription = (TextView)convertView.findViewById(R.id.transaction_item_description);
        TextView transactionCategory = (TextView)convertView.findViewById(R.id.transaction_item_category);
        transactionAmountWhole.setText(transaction.getAmountDollars().toString());
        // Pad cents with zero if necessary
        transactionAmountDecimal.setText((transaction.getAmountCents().toString() + "0").substring(0, 2));
        transactionDescription.setText(transaction.getDescription());
        transactionCategory.setText(transaction.getCategory().getName());
        return convertView;
    }
}
