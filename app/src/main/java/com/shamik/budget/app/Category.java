package com.shamik.budget.app;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/7/2016.
 */
public class Category {
    private Integer mID;
    private String mName;
    private Integer mParentID;
    private boolean mHasChildren;

    // pre-database
    // parent ID should = -1 if no parent
    public Category(String name, int parentID) {
        this(-1, name, parentID, false);
    }

    // post-database
    public Category(int ID, String name, int parentID, boolean hasChildren) {
        mID = ID;
        mName = name;
        mParentID = parentID;
        mHasChildren = hasChildren;
    }

    public Integer getID() {
        return mID;
    }

    // should return -1 if no parent
    public Integer getParentID() {
        return mParentID;
    }

    public String getName() {
        return mName;
    }

    public boolean hasChildren() {
        return mHasChildren;
    }

    public ArrayList<Category> getChildren() {
        return BudgetDatabase.getInstance().getCategoriesByParentID(mID);
    }
}
