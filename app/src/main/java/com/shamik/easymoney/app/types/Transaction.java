package com.shamik.easymoney.app.types;

import com.shamik.easymoney.app.data.BudgetDatabase;

/**
 * Created by Shamik on 5/7/2016.
 */
public class Transaction {
    private Integer mID;
    private String mDate;
    private Integer mAmountDollars;
    private Integer mAmountCents;
    private String mDescription;
    private Integer mCategoryID;
    private boolean mIsIncome;

    private static final String BLANK_CATEGORY = "Uncategorized";

    // pre-database
    // categoryID should be -1 if no category
    public Transaction(int amountDollars, int amountCents, String description,
                       int categoryID, boolean isIncome) {
        set(-1, null, amountDollars, amountCents, description, categoryID, isIncome);
    }

    // post-database
    public Transaction(int ID, String date, int amountDollars, int amountCents, String description,
                       int categoryID, boolean isIncome) {
        set(ID, date, amountDollars, amountCents, description, categoryID, isIncome);
    }

    public Integer getID() {
        return mID;
    }

    public String getDate() {
        return mDate;
    }

    public Integer getAmountDollars() {
        return mAmountDollars;
    }

    public Integer getAmountCents() {
        return mAmountCents;
    }

    public double getAmount() {
        return mAmountDollars + mAmountCents / 100.;
    }

    public String getDescription() {
        return mDescription;
    }

    // should return -1 if no category
    public Integer getCategoryID() {
        return mCategoryID;
    }

    public Category getCategory() {
        if(mCategoryID == -1) {
            return new Category(BLANK_CATEGORY, -1);
        } else {
            return BudgetDatabase.getInstance().getCategoryByID(mCategoryID);
        }
    }

    public boolean isIncome() {
        return mIsIncome;
    }

    public void set(int ID, String date, int amountWhole, int amountDecimal, String description,
                    int categoryID, boolean isIncome) {
        setID(ID);
        setDate(date);
        setAmountDollars(amountWhole);
        setAmountCents(amountDecimal);
        setDescription(description);
        setCategoryID(categoryID);
        setIncome(isIncome);
    }

    private void setID(int ID) {
        mID = ID;
    }

    private void setDate(String date) {
        mDate = date;
    }

    private void setAmountDollars(int amountDollars) {
        mAmountDollars = amountDollars;
    }

    private void setAmountCents(int amountCents) {
        mAmountCents = amountCents;
    }

    private void setDescription(String description) {
        mDescription = (description == null) ? "" : description;
    }

    private void setCategoryID(int categoryID) {
        mCategoryID = categoryID;
    }

    private void setIncome(boolean isIncome) {
        mIsIncome = isIncome;
    }
}
