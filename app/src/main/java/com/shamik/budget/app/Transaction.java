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
    private Category mCategory;
    private boolean mIsIncome;

    // pre-database
    public Transaction(int amountDollars, int amountCents, String description,
                       Category category, boolean isIncome) {
        set(-1, null, amountDollars, amountCents, description, category, isIncome);
    }

    // post-database
    public Transaction(int ID, Date date, int amountDollars, int amountCents, String description,
                       Category category, boolean isIncome) {
        set(ID, date, amountDollars, amountCents, description, category, isIncome);
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

    public Category getCategory() {
        return mCategory;
    }

    public boolean isIncome() {
        return mIsIncome;
    }

    public void set(int ID, Date date, int amountWhole, int amountDecimal, String description,
                    Category category, boolean isIncome) {
        setID(ID);
        setDate(date);
        setAmountDollars(amountWhole);
        setAmountCents(amountDecimal);
        setDescription(description);
        setCategory(category);
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

    public void setCategory(Category category) {
        mCategory = (category == null) ? new Category(null, "blank") : category;
    }

    public void setIncome(boolean isIncome) {
        mIsIncome = isIncome;
    }
}
