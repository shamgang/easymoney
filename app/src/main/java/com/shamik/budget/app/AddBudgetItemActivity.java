package com.shamik.budget.app;

import android.os.Bundle;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AddBudgetItemActivity extends BudgetAppActivity  {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget_item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_budget_item);

        addDrawer();

    }
}
