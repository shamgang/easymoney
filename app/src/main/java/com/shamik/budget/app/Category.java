package com.shamik.budget.app;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/7/2016.
 */
public class Category {
    private String mName;
    private Category mParent;
    private ArrayList<Category> mChildren;

    public Category(Category parent, String name) {
        mParent = parent;
        mName = name;
        mChildren = new ArrayList<Category>();
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
