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
import android.widget.ListView;

import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.BaseFullscreenFragment;
import com.shamik.easymoney.app.data.BudgetDatabase;
import com.shamik.easymoney.app.MainActivity;
import com.shamik.easymoney.app.types.Transaction;
import com.shamik.easymoney.app.util.TransactionListHelper;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/11/2016.
 */
public class CategoryFragment extends BaseFullscreenFragment {
    private ListView mSubcategoryListView;
    private ListView mTransactionListView;
    private ArrayList<Transaction> mTransactionList;
    private String mTitle;
    private int mID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // allows onCreateOptionsMenu to be called
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        // get current category
        // must set title before calling to super because super.onStart() calls getTitle()
        mID = getArguments().getInt(MainActivity.CATEGORY_ID_TAG);
        mTitle = BudgetDatabase.getInstance().getCategoryByID(mID).getName();
        super.onStart();

        // TODO: subcategories

        // transaction list view
        // TODO: paginate
        mTransactionList = BudgetDatabase.getInstance().getTransactionsByCategoryID(mID);
        mTransactionListView = (ListView)getView().findViewById(R.id.category_transaction_list);
        mTransactionListView.setEmptyView(
                getView().findViewById(R.id.empty_category_transaction_list));
        TransactionListHelper.fillAndResizeTransactionList((MainActivity)getActivity(),
                mTransactionList, mTransactionListView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    protected String getTitle() {
        return mTitle;
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
                                        BudgetDatabase.getInstance().deleteCategory(mID);
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
