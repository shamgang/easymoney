package com.shamik.budget.app;

/**
 * Created by Shamik on 5/7/2016.
 */
public class Transaction {
    private String mAmountWhole;
    private String mAmountDecimal;
    private String mDescription;
    private Category mCategory;
    private boolean mIsIncome;

    public Transaction() {
        set(null, null, null, null, false);
    }

    public Transaction(String amountWhole, String amountDecimal, String description,
                       Category category, boolean isIncome) {
        set(amountWhole, amountDecimal, description, category, isIncome);
    }

    public String getAmountWhole() {
        return mAmountWhole;
    }

    public String getAmountDecimal() {
        return mAmountDecimal;
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

    public void set(String amountWhole, String amountDecimal, String description,
                    Category category, boolean isIncome) {
        setAmountWhole(amountWhole);
        setAmountDecimal(amountDecimal);
        setDescription(description);
        setCategory(category);
        setIncome(isIncome);
    }

    public void setAmountWhole(String amountWhole) {
        mAmountWhole = (amountWhole == null) ? "00" : amountWhole;
    }

    public void setAmountDecimal(String amountDecimal) {
        mAmountDecimal = (amountDecimal == null) ? "00" : amountDecimal;
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
