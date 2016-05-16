package com.shamik.budget.app;

import java.util.Date;

/**
 * Created by Shamik on 5/7/2016.
 */
public class Transaction {
    private Integer mID;
    private Date mDate;
    private Integer mAmountDollars;
    private Integer mAmountCents;
    private String mDescription;
    private Integer mCategoryID;
    private boolean mIsIncome;

    // pre-database
    // categoryID should be -1 if no category
    public Transaction(int amountDollars, int amountCents, String description,
                       int categoryID, boolean isIncome) {
        set(-1, null, amountDollars, amountCents, description, categoryID, isIncome);
    }

    // post-database
    public Transaction(int ID, Date date, int amountDollars, int amountCents, String description,
                       int categoryID, boolean isIncome) {
        set(ID, date, amountDollars, amountCents, description, categoryID, isIncome);
    }

    public Integer getID() {
        return mID;
    }

    public Date getDate() {
        return mDate;
    }

    public Integer getAmountDollars() {
        return mAmountDollars;
    }

    public Integer getAmountCents() {
        return mAmountCents;
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
            return new Category("blank", -1);
        }
        else {
            return BudgetDatabase.getInstance().getCategoryByID(mCategoryID);
        }
    }

    public boolean isIncome() {
        return mIsIncome;
    }

    public void set(int ID, Date date, int amountWhole, int amountDecimal, String description,
                    int categoryID, boolean isIncome) {
        setID(ID);
        setDate(date);
        setAmountDollars(amountWhole);
        setAmountCents(amountDecimal);
        setDescription(description);
        setCategoryID(categoryID);
        setIncome(isIncome);
    }

    public void setID(int ID) {
        mID = ID;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public void setAmountDollars(int amountDollars) {
        mAmountDollars = amountDollars;
    }

    public void setAmountCents(int amountCents) {
        mAmountCents = amountCents;
    }

    public void setDescription(String description) {
        mDescription = (description == null) ? "Test" : description;
    }

    public void setCategoryID(int categoryID) {
        mCategoryID = categoryID;
    }

    public void setIncome(boolean isIncome) {
        mIsIncome = isIncome;
    }
}
