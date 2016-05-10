package com.shamik.budget.app;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mNavDrawerList;
    private ArrayAdapter<String> mNavDrawerAdapter;
    private String mTitle;
    private String mDrawerTitle;

    private static final String TAG = "MainActivity";

    public ArrayList<Transaction> mTransactionStubList;
    public ArrayList<Category> mCategoryStubList;
    public BudgetDatabase mBudgetDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // TODO: remove stub data
        /*
        mTransactionStubList = new ArrayList<Transaction>();
        for(int i = 0; i < 30; ++i) {
            mTransactionStubList.add(new Transaction());
        }
        */
        mBudgetDatabase = new BudgetDatabase(this);
        try {
            mBudgetDatabase.open();
            loadTransactionList();
            loadCategoryList();
        } catch(SQLException e) {
            mTransactionStubList = new ArrayList<Transaction>();
            mCategoryStubList = new ArrayList<Category>();
            Log.e(TAG, "Could not open database");
        }
        /*
        mCategoryStubList = new ArrayList<Category>();
        for(int i = 0; i < 30; ++i) {
            mCategoryStubList.add(new Category(null, "Blank"));
        }
        */

        // the title that will be set onDrawerOpened
        mDrawerTitle= this.getString(R.string.drawer_title);

        // populate the nav drawer and set an item click listener
        mNavDrawerList = (ListView)findViewById(R.id.nav_drawer);
        String[] osArray = { "Transactions", "Categories", "Analytics" };
        mNavDrawerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mNavDrawerList.setAdapter(mNavDrawerAdapter);
        mNavDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // add a toggle button for the drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // clicking the home button will toggle drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        findViewById(R.id.fragment_container);

        // If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }

        // Create a new default Fragment to be placed in the activity layout
        TransactionListFragment transactionListFragment = new TransactionListFragment();

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, transactionListFragment).commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Hide the plus button on the analytics page, add, and view pages
        if(mTitle.equals(this.getString(R.string.analytics_fragment_title))
                || mTitle.equals(this.getString(R.string.add_transaction_fragment_title))
                || mTitle.equals(this.getString(R.string.view_transaction_fragment_title))) {
            menu.findItem(R.id.action_add).setVisible(false);
        }
        // Hide the search button on the analytics, add, view, and category list pages
        if(mTitle.equals(this.getString(R.string.analytics_fragment_title))
                || mTitle.equals(this.getString(R.string.add_transaction_fragment_title))
                || mTitle.equals(this.getString(R.string.category_list_fragment_title))
                || mTitle.equals(this.getString(R.string.view_transaction_fragment_title))) {
            menu.findItem(R.id.action_search).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_add) {
            if(mTitle.equals(this.getString(R.string.transaction_list_fragment_title))) {
                // We are on the transaction list, so the plus button will add an item
                // Switch to AddTransactionFragment, set title, and refresh options menu

                Fragment fragment = new AddTransactionFragment();

                // Insert the fragment by replacing any existing fragment
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            else if(mTitle.equals(this.getString(R.string.category_list_fragment_title))) {
                // We are on the category list, so the plus button will open a text entry modal

                new AddCategoryDialogFragment().show(getSupportFragmentManager(),
                        this.getString(R.string.add_category_dialog_fragment_title));
            }
            return true;
        }
        if(id == R.id.action_search) {
            // toggle search drawer
            if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }
            else {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateActionBar(String fragmentTitle) {
        mTitle = fragmentTitle;
        getActionBar().setTitle(mTitle);
        invalidateOptionsMenu();
    }

    public void selectTransaction(int position) {
        // switch to ViewTransactionFragment, change title and refresh options menu
        Fragment fragment = new ViewTransactionFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void selectCategory() {
        // switch to TransactionListFragment, change title and refresh options menu
        Fragment fragment = new TransactionListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /** Swaps fragments in the main content view */
    public void selectNavItem(int position) {
        // Switch fragment and title

        Fragment fragment;
        if(position == 0) {
            fragment = new TransactionListFragment();
        }
        else if(position == 1) {
            fragment = new CategoryListFragment();
        }
        else {
            fragment = new AnalyticsFragment();
        }

        // Insert the fragment by replacing any existing fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        mNavDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mNavDrawerList);
    }

    public void loadTransactionList() {
        mTransactionStubList = mBudgetDatabase.getAllTransactions();
    }

    public void loadCategoryList() {
        mCategoryStubList = mBudgetDatabase.getAllCategories();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectNavItem(position);
        }
    }
}
