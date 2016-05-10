package com.shamik.budget.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Shamik on 5/8/2016.
 */
public class BudgetDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_CATEGORIES = "categories";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AMOUNT_WHOLE = "amount_whole";
    public static final String COLUMN_AMOUNT_DECIMAL = "amount_decimal";
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
            + COLUMN_AMOUNT_WHOLE + " varchar(10), "
            + COLUMN_AMOUNT_DECIMAL + " varchar(10), "
            + COLUMN_DESCRIPTION + " varchar(100), "
            + COLUMN_CATEGORY + " varchar(20), "
            + COLUMN_IS_INCOME + " boolean)";
    private static final String CREATE_TABLE_CATEGORIES =
            "create table " + TABLE_CATEGORIES + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " varchar(20) not null, "
            + COLUMN_PARENT + " varchar(20))";

    public BudgetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_TRANSACTIONS);
        database.execSQL(CREATE_TABLE_CATEGORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(BudgetDatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(database);
    }
}
