package com.shamik.budget.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Shamik on 5/9/2016.
 */
public class BudgetDatabase {
    private static final String TAG = "BudgetDatabase";

    private SQLiteDatabase mDatabase;
    private BudgetDatabaseHelper mDBHelper;

    private static final String[] TRANSACTION_COLUMNS = {
            BudgetDatabaseHelper.COLUMN_ID,
            BudgetDatabaseHelper.COLUMN_DATE,
            BudgetDatabaseHelper.COLUMN_AMOUNT_DOLLARS,
            BudgetDatabaseHelper.COLUMN_AMOUNT_CENTS,
            BudgetDatabaseHelper.COLUMN_DESCRIPTION,
            BudgetDatabaseHelper.COLUMN_CATEGORY,
            BudgetDatabaseHelper.COLUMN_IS_INCOME
    };
    private static final String[] CATEGORY_COLUMNS = {
            BudgetDatabaseHelper.COLUMN_ID,
            BudgetDatabaseHelper.COLUMN_NAME,
            BudgetDatabaseHelper.COLUMN_PARENT
    };

    public BudgetDatabase(Context context) {
        mDBHelper = new BudgetDatabaseHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDBHelper.getWritableDatabase();
    }

    public void close() {
        mDBHelper.close();
    }

    public Transaction createTransaction(Transaction transaction) {
        long insertId = mDatabase.insert(BudgetDatabaseHelper.TABLE_TRANSACTIONS, null,
                transactionToValues(transaction));
        Cursor cursor = mDatabase.query(BudgetDatabaseHelper.TABLE_TRANSACTIONS, TRANSACTION_COLUMNS,
                BudgetDatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Transaction newTransaction = cursorToTransaction(cursor);
        cursor.close();
        return newTransaction;
    }

    public void updateTransaction(Transaction transaction) {
        mDatabase.update(BudgetDatabaseHelper.TABLE_TRANSACTIONS, transactionToValues(transaction),
                "_id=" + transaction.getID(), null);
    }

    private ContentValues transactionToValues(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(BudgetDatabaseHelper.COLUMN_AMOUNT_DOLLARS, transaction.getAmountDollars());
        values.put(BudgetDatabaseHelper.COLUMN_AMOUNT_CENTS, transaction.getAmountCents());
        values.put(BudgetDatabaseHelper.COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(BudgetDatabaseHelper.COLUMN_CATEGORY, transaction.getCategory().getName());
        values.put(BudgetDatabaseHelper.COLUMN_IS_INCOME, transaction.isIncome());
        return values;
    }

    public Category createCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(BudgetDatabaseHelper.COLUMN_NAME, category.getName());
        if(category.getParent() != null) {
            values.put(BudgetDatabaseHelper.COLUMN_PARENT, category.getParent().getName());
        }
        long insertId = mDatabase.insert(BudgetDatabaseHelper.TABLE_CATEGORIES, null, values);
        Cursor cursor = mDatabase.query(BudgetDatabaseHelper.TABLE_CATEGORIES, CATEGORY_COLUMNS,
                BudgetDatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Category newCategory = cursorToCategory(cursor);
        cursor.close();
        return newCategory;
    }

    public ArrayList<Transaction> getAllTransactions() {
        return getTransactionsWhere(null);
    }

    public ArrayList<Transaction> getTransactionsWhere(String where) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = mDatabase.query(BudgetDatabaseHelper.TABLE_TRANSACTIONS,
                TRANSACTION_COLUMNS, where, null, null, null, null);

        cursor.moveToLast();
        while(!cursor.isBeforeFirst()) {
            Transaction transaction = cursorToTransaction(cursor);
            transactions.add(transaction);
            cursor.moveToPrevious();
        }
        cursor.close();
        return transactions;
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<Category>();

        Cursor cursor = mDatabase.query(BudgetDatabaseHelper.TABLE_CATEGORIES,
                CATEGORY_COLUMNS, null, null, null, null, null);

        cursor.moveToLast();
        while(!cursor.isBeforeFirst()) {
            Category category = cursorToCategory(cursor);
            categories.add(category);
            cursor.moveToPrevious();
        }
        cursor.close();
        return categories;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        // TODO: makes a new Category, shouldn't have to do that - maybe Category shouldn't be a class
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(cursor.getString(1));
        } catch(ParseException e) {
            Log.e(TAG, "Parsing SQL date failed");
            date = null;
        }
        return new Transaction(cursor.getInt(0), date, cursor.getInt(2), cursor.getInt(3),
                cursor.getString(4), new Category(null, cursor.getString(5)), cursor.getInt(6) > 0);
    }

    private Category cursorToCategory(Cursor cursor) {
        // TODO: makes a new Category - shouldn't do that. Also, switch param order in Category?
        return new Category(new Category(null, cursor.getString(2)), cursor.getString(1));
    }
}
