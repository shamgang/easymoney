package com.shamik.budget.app;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/7/2016.
 */
public class Category {
    private int mID;
    private Category mParent;
    private String mName;
    private ArrayList<Category> mChildren;

    public Category(Category parent, String name) {
        this(-1, parent, name);
    }

    public Category(int ID, Category parent, String name) {
        mID = ID;
        mParent = parent;
        mName = name;
        mChildren = new ArrayList<Category>();
    }

    public int getID() {
        return mID;
    }

    public Category getParent() {
        return mParent;
    }

    public String getName() {
        return mName;
    }

    public ArrayList<Category> getChildren() {
        return mChildren;
    }
}
