package com.shamik.budget.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Shamik on 5/9/2016.
 */
public class BudgetDatabase extends SQLiteOpenHelper {
    private static ConcurrentHashMap<Context, BudgetDatabase> mInstances
            = new ConcurrentHashMap<Context, BudgetDatabase>();

    private SQLiteDatabase mDatabase;

    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_CATEGORIES = "categories";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_AMOUNT_DOLLARS = "amount_dollars";
    public static final String COLUMN_AMOUNT_CENTS = "amount_cents";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_IS_INCOME = "is_income";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PARENT = "parent";

    private static final String DATABASE_NAME = "budget.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation SQL statement
    private static final String CREATE_TABLE_TRANSACTIONS =
            "create table " + TABLE_TRANSACTIONS + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_DATE + " date default current_timestamp, "
                    + COLUMN_AMOUNT_DOLLARS + " int, "
                    + COLUMN_AMOUNT_CENTS + " int, "
                    + COLUMN_DESCRIPTION + " varchar(100), "
                    + COLUMN_CATEGORY + " varchar(20), "
                    + COLUMN_IS_INCOME + " boolean)";
    private static final String CREATE_TABLE_CATEGORIES =
            "create table " + TABLE_CATEGORIES + "("
                    + COLUMN_ID + " integer primary key autoincrement, "
                    + COLUMN_NAME + " varchar(20) not null, "
                    + COLUMN_PARENT + " varchar(20))";
    
    private static final String[] TRANSACTION_COLUMNS = {
            COLUMN_ID,
            COLUMN_DATE,
            COLUMN_AMOUNT_DOLLARS,
            COLUMN_AMOUNT_CENTS,
            COLUMN_DESCRIPTION,
            COLUMN_CATEGORY,
            COLUMN_IS_INCOME
    };
    private static final String[] CATEGORY_COLUMNS = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_PARENT
    };

    // returns null if database open fails
    public static BudgetDatabase getInstance(Context context) {
        // factory
        BudgetDatabase instance;
        if((instance = mInstances.get(context)) == null) {
            instance = new BudgetDatabase(context);
            try {
                instance.open();
                mInstances.put(context, instance);
            } catch(SQLException e) {
                Log.e(BudgetDatabase.class.getName(), "Failed to open database.");
                instance = null;
            }
        }
        return instance;
    }

    public BudgetDatabase(Context context) {
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

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_TRANSACTIONS);
        database.execSQL(CREATE_TABLE_CATEGORIES);
        prePopulate(database);
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

    public Transaction createTransaction(Transaction transaction) {
        long insertId = mDatabase.insert(BudgetDatabase.TABLE_TRANSACTIONS, null,
                transactionToValues(transaction));
        Cursor cursor = mDatabase.query(BudgetDatabase.TABLE_TRANSACTIONS, TRANSACTION_COLUMNS,
                BudgetDatabase.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Transaction newTransaction = cursorToTransaction(cursor);
        cursor.close();
        return newTransaction;
    }

    public void updateTransaction(Transaction transaction) {
        mDatabase.update(BudgetDatabase.TABLE_TRANSACTIONS, transactionToValues(transaction),
                "_id=" + transaction.getID(), null);
    }

    private ContentValues transactionToValues(Transaction transaction) {
        ContentValues values = new ContentValues();
        values.put(BudgetDatabase.COLUMN_AMOUNT_DOLLARS, transaction.getAmountDollars());
        values.put(BudgetDatabase.COLUMN_AMOUNT_CENTS, transaction.getAmountCents());
        values.put(BudgetDatabase.COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(BudgetDatabase.COLUMN_CATEGORY, transaction.getCategory().getName());
        values.put(BudgetDatabase.COLUMN_IS_INCOME, transaction.isIncome());
        return values;
    }

    public Category createCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(BudgetDatabase.COLUMN_NAME, category.getName());
        if(category.getParent() != null) {
            values.put(BudgetDatabase.COLUMN_PARENT, category.getParent().getName());
        }
        long insertId = mDatabase.insert(BudgetDatabase.TABLE_CATEGORIES, null, values);
        Cursor cursor = mDatabase.query(BudgetDatabase.TABLE_CATEGORIES, CATEGORY_COLUMNS,
                BudgetDatabase.COLUMN_ID + " = " + insertId, null, null, null, null);
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

        Cursor cursor = mDatabase.query(BudgetDatabase.TABLE_TRANSACTIONS,
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

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<Category>();

        Cursor cursor = mDatabase.query(BudgetDatabase.TABLE_CATEGORIES,
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
            Log.e(BudgetDatabase.class.getName(), "Parsing SQL date failed");
            date = null;
        }
        return new Transaction(cursor.getInt(0), date, cursor.getInt(2), cursor.getInt(3),
                cursor.getString(4), new Category(null, cursor.getString(5)), cursor.getInt(6) > 0);
    }

    private Category cursorToCategory(Cursor cursor) {
        // TODO: makes a new Category - shouldn't do that. Also, switch param order in Category?
        return new Category(cursor.getInt(0), new Category(null, cursor.getString(2)), cursor.getString(1));
    }

    // TODO: remove stub functions
    private void prePopulate(SQLiteDatabase database) {
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "acatagory2"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);
        insertTransaction(new Transaction(34, 55, "New purchase", new Category(null, "category1"), false), database);

        insertCategory(new Category(null, "acatagory"), database);
        insertCategory(new Category(null, "acatagory1"), database);
        insertCategory(new Category(null, "acatagory2"), database);
        insertCategory(new Category(null, "acatagory3"), database);
        insertCategory(new Category(null, "acatagory4"), database);
        insertCategory(new Category(null, "acatagory5"), database);
        insertCategory(new Category(null, "acatagory6"), database);
        insertCategory(new Category(null, "acatagory7"), database);
        insertCategory(new Category(null, "acatagory8"), database);
        insertCategory(new Category(null, "acatagory9"), database);
        insertCategory(new Category(null, "acatagory11"), database);
        insertCategory(new Category(null, "acatagory12"), database);
        insertCategory(new Category(null, "acatagory13"), database);
        insertCategory(new Category(null, "acatagory14"), database);
        insertCategory(new Category(null, "category1"), database);
    }

    private void insertTransaction(Transaction transaction, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT_DOLLARS, transaction.getAmountDollars());
        values.put(COLUMN_AMOUNT_CENTS, transaction.getAmountCents());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(COLUMN_CATEGORY, transaction.getCategory().getName());
        values.put(COLUMN_IS_INCOME, transaction.isIncome());
        database.insert(BudgetDatabase.TABLE_TRANSACTIONS, null, values);
    }

    private void insertCategory(Category category, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, category.getName());
        if(category.getParent() != null) {
            values.put(COLUMN_PARENT, category.getParent().getName());
        }
        database.insert(BudgetDatabase.TABLE_CATEGORIES, null, values);
    }
}
