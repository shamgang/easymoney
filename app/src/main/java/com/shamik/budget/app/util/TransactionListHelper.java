package com.shamik.budget.app.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.shamik.budget.app.MainActivity;
import com.shamik.budget.app.adapters.TransactionAdapter;
import com.shamik.budget.app.types.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shamik on 5/17/2016.
 */
public class TransactionListHelper {
    public static void fillAndResizeTransactionList(MainActivity activity,
                                                    ArrayList<Transaction> list,
                                                    ListView listView) {
        fillTransactionList(activity, list, listView);
        resizeTransactionList(list, listView);
    }

    public static void fillTransactionList(final MainActivity activity,
                                           final ArrayList<Transaction> list, ListView listView) {
        // set adapter and click listener
        listView.setAdapter(new TransactionAdapter(activity, list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                activity.selectTransaction(list.get(i).getID());
            }
        });
    }

    public static void resizeTransactionList(ArrayList<Transaction> list, ListView listView) {
        // set list height to cover all items
        if(!listView.getAdapter().isEmpty()) {
            View itemView = listView.getAdapter().getView(0, null, listView);
            itemView.measure(0, 0);
            ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
            layoutParams.height = itemView.getMeasuredHeight() * list.size();
            listView.setLayoutParams(layoutParams);
            listView.requestLayout();
        }
    }
}
