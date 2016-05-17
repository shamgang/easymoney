package com.shamik.budget.app.util;

import com.shamik.budget.app.types.Category;
import com.shamik.budget.app.util.BaseFullscreenFragment;

/**
 * Created by Shamik on 5/11/2016.
 */
public abstract class BaseCategorySelectFragment extends BaseFullscreenFragment {
    public abstract void setCategory(Category category);
}
