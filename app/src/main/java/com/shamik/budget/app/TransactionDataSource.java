package com.shamik.budget.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shamik on 5/9/2016.
 */
public class TransactionDataSource {
    private SQLiteDatabase database;
    private BudgetDatabaseHelper dbHelper;
    private String[] transactionColumns = {
            BudgetDatabaseHelper.COLUMN_ID,
            BudgetDatabaseHelper.COLUMN_AMOUNT_WHOLE,
            BudgetDatabaseHelper.COLUMN_DESCRIPTION,
            BudgetDatabaseHelper.COLUMN_CATEGORY,
            BudgetDatabaseHelper.COLUMN_IS_INCOME
    };

    public TransactionDataSource(Context context) {
        dbHelper = new BudgetDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Transaction createTransaction(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(BudgetDatabaseHelper.COLUMN_AMOUNT_WHOLE, transaction.getAmountWhole());
        values.put(BudgetDatabaseHelper.COLUMN_AMOUNT_DECIMAL, transaction.getAmountDecimal());
        values.put(BudgetDatabaseHelper.COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(BudgetDatabaseHelper.COLUMN_CATEGORY, transaction.getCategory().getName());
        values.put(BudgetDatabaseHelper.COLUMN_IS_INCOME, transaction.isIncome());
        long insertId = database.insert(BudgetDatabaseHelper.TABLE_TRANSACTIONS, null, values);
        Cursor cursor = database.query(BudgetDatabaseHelper.TABLE_TRANSACTIONS, transactionColumns,
                BudgetDatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Transaction newTrasaction = cursorToTransaction(cursor);
        cursor.close();
        return newTrasaction;
    }

    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        Cursor cursor = database.query(BudgetDatabaseHelper.TABLE_TRANSACTIONS,
                transactionColumns, null, null, null, null, null);

        cursor.moveToLast();
        while(!cursor.isBeforeFirst()) {
            Transaction transaction = cursorToTransaction(cursor);
            transactions.add(transaction);
            cursor.moveToPrevious();
        }
        cursor.close();
        return transactions;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        // TODO: makes a new Category, shouldn't have to do that - maybe Category shouldn't be a class
        return new Transaction(cursor.getString(0), cursor.getString(1),
                cursor.getString(2), new Category(null, cursor.getString(3)), cursor.getInt(4) > 0);
    }
}
