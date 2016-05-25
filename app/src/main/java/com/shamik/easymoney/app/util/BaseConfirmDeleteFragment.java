package com.shamik.easymoney.app.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.shamik.easymoney.app.MainActivity;
import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.data.BudgetDatabase;
import com.shamik.easymoney.app.fragments.TransactionListFragment;

/**
 * Created by Shamik on 5/24/2016.
 */
public abstract class BaseConfirmDeleteFragment extends BaseFullscreenFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // allows onCreateOptionsMenu to be called
        setHasOptionsMenu(true);
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
                                        onDeleteConfirmed();
                                        // clear backstack and return to transaction list, in case
                                        // any references to this ID remain in created fragments
                                        ((MainActivity)getActivity()).replaceFragmentClearBackstack(
                                                new TransactionListFragment(),
                                                TransactionListFragment.class.getName());
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

    protected abstract void onDeleteConfirmed();
}
