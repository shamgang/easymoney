package com.shamik.easymoney.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.shamik.easymoney.app.types.Category;
import com.shamik.easymoney.app.types.Transaction;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Shamik on 5/9/2016.
 */
// singleton
public class BudgetDatabase extends SQLiteOpenHelper {
    private static Context mContext;
    private static BudgetDatabase mInstance;

    private SQLiteDatabase mDatabase;

    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_CATEGORIES = "categories";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_AMOUNT_DOLLARS = "amount_dollars";
    public static final String COLUMN_AMOUNT_CENTS = "amount_cents";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY_ID = "category";
    public static final String COLUMN_IS_INCOME = "is_income";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENT_ID = "parent";

    private static final String DATABASE_NAME = "easymoney.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation SQL statements
    private static final String CREATE_TABLE_TRANSACTIONS =
            "create table " + TABLE_TRANSACTIONS + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_TIME + " datetime default (datetime(current_timestamp, 'localtime')), "
                    + COLUMN_AMOUNT_DOLLARS + " int, "
                    + COLUMN_AMOUNT_CENTS + " int, "
                    + COLUMN_DESCRIPTION + " varchar(100), "
                    + COLUMN_CATEGORY_ID + " int, "
                    + COLUMN_IS_INCOME + " boolean)";
    private static final String CREATE_TABLE_CATEGORIES =
            "create table " + TABLE_CATEGORIES + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_NAME + " varchar(20) not null, "
                    + COLUMN_PARENT_ID + " int)";
    
    private static final String[] TRANSACTION_COLUMNS = {
            COLUMN_ID,
            COLUMN_TIME,
            COLUMN_AMOUNT_DOLLARS,
            COLUMN_AMOUNT_CENTS,
            COLUMN_DESCRIPTION,
            COLUMN_CATEGORY_ID,
            COLUMN_IS_INCOME
    };
    private static final String[] CATEGORY_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_PARENT_ID
    };

    // must be called before getInstance
    public static void init(Context context) {
        mContext = context;
    }

    // returns null if database open fails
    public static BudgetDatabase getInstance() {
        if(mContext == null) {
            Log.e(BudgetDatabase.class.getName(), "Didn't call init before getInstance");
            return null;
        }
        if(mInstance == null) {
            mInstance = new BudgetDatabase(mContext);
            try {
                mInstance.open();
            } catch(SQLException e) {
                Log.e(BudgetDatabase.class.getName(), "Failed to open database.");
                mInstance = null;
            }
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_TRANSACTIONS);
        database.execSQL(CREATE_TABLE_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(BudgetDatabase.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(database);
    }

    public Transaction getTransactionByID(int id) {
        // TODO: should this not use a function call to avoid extra array overhead?
        return getTransactionsWhere(COLUMN_ID + "=" + Integer.toString(id)).get(0);
    }

    public void createTransaction(Transaction transaction) {
        mDatabase.insert(BudgetDatabase.TABLE_TRANSACTIONS, null, transactionToValues(transaction));
    }

    public void updateTransactionByID(int ID, Transaction transaction) {
        mDatabase.update(BudgetDatabase.TABLE_TRANSACTIONS, transactionToValues(transaction),
                COLUMN_ID + "=" + ID, null);
    }

    public ArrayList<Transaction> getUncategorizedTransactions() {
        // if no category, categoryID should be -1
        return getTransactionsByCategoryID(-1);
    }

    public ArrayList<Transaction> getCategorizedTransactions() {
        // if no category, categoryID should be -1
        return getTransactionsWhere(COLUMN_CATEGORY_ID + "<>-1");
    }

    public ArrayList<Transaction> getTransactionsByCategoryID(int ID) {
        return getTransactionsWhere(COLUMN_CATEGORY_ID + "=" + ID);
    }

    // expects date in SQL format (YYYY-MM-DD)
    public ArrayList<Transaction> getTransactionsByCategoryIDAndDateRange(int ID, String fromDate,
                                                                          String toDate) {
        return getTransactionsWhere("(" + COLUMN_TIME + " between '" + fromDate + "' and '" + toDate
                + "') and (" + COLUMN_CATEGORY_ID + "=" + ID + ")");
    }

    private ArrayList<Transaction> getTransactionsWhere(String where) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        // reverse chronological order
        Cursor cursor = mDatabase.query(BudgetDatabase.TABLE_TRANSACTIONS,
                TRANSACTION_COLUMNS, where, null, null, null, COLUMN_TIME + " desc", null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Transaction transaction = cursorToTransaction(cursor);
            transactions.add(transaction);
            cursor.moveToNext();
        }
        cursor.close();
        return transactions;
    }

    public Category getCategoryByID(int ID) {
        Cursor cursor = mDatabase.query(BudgetDatabase.TABLE_CATEGORIES,
                CATEGORY_COLUMNS, COLUMN_ID + "=" + Integer.toString(ID), null, null, null, null);
        cursor.moveToFirst();
        return cursorToCategory(cursor);
    }

    public boolean hasCategoryName(String name) {
        Cursor cursor = mDatabase.query(BudgetDatabase.TABLE_CATEGORIES,
                CATEGORY_COLUMNS, COLUMN_NAME + "='" + name + "'", null, null, null, null);
        return cursor.getCount() != 0;
    }

    public void createCategory(Category category) {
        mDatabase.insert(BudgetDatabase.TABLE_CATEGORIES, null, categoryToValues(category));
    }

    public ArrayList<Category> getAllCategories() {
        return getCategoriesWhere(null);
    }

    public ArrayList<Category> getCategoriesByParentID(int ID) {
        // TODO: test this once nesting is implemented
        return getCategoriesWhere(COLUMN_PARENT_ID + "=" + ID);
    }

    private ArrayList<Category> getCategoriesWhere(String where) {
        ArrayList<Category> categories = new ArrayList<Category>();

        Cursor cursor = mDatabase.query(BudgetDatabase.TABLE_CATEGORIES,
                CATEGORY_COLUMNS, where, null, null, null, null);

        // populate array in reverse order
        // TODO: order by created datetime descending
        cursor.moveToLast();
        while(!cursor.isBeforeFirst()) {
            Category category = cursorToCategory(cursor);
            categories.add(category);
            cursor.moveToPrevious();
        }
        cursor.close();
        return categories;
    }

    protected BudgetDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // hope to close database connection
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    private void open() throws SQLException {
        mDatabase = getWritableDatabase();
    }

    private ContentValues transactionToValues(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT_DOLLARS, transaction.getAmountDollars());
        values.put(COLUMN_AMOUNT_CENTS, transaction.getAmountCents());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(COLUMN_CATEGORY_ID, transaction.getCategoryID());
        values.put(COLUMN_IS_INCOME, transaction.isIncome());
        return values;
    }

    private ContentValues categoryToValues(Category category) {
        ContentValues values = new ContentValues();
        values.put(BudgetDatabase.COLUMN_NAME, category.getName());
        values.put(BudgetDatabase.COLUMN_PARENT_ID, category.getParentID());
        return values;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        // get date as first ten characters of datetime
        return new Transaction(cursor.getInt(0), cursor.getString(1).substring(0, 10), cursor.getInt(2),
                cursor.getInt(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6) > 0);
    }

    private Category cursorToCategory(Cursor cursor) {
        return new Category(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), false);
    }
}
