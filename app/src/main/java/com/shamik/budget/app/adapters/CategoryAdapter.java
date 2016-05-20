package com.shamik.budget.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shamik.budget.app.types.Category;
import com.shamik.budget.app.R;

import java.util.ArrayList;

/**
 * Created by Shamik on 5/8/2016.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {
    public CategoryAdapter(Context context, ArrayList<Category> transactions) {
        super(context, 0, transactions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // get data item
        Category category = getItem(position);
        // inflate view unless one is being reused
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_category, parent, false);
        }
        // display category name
        TextView categoryName = (TextView)convertView.findViewById(R.id.category_item_name);
        categoryName.setText(category.getName());
        // display carat if parent
        // TODO: currently nesting isn't implemented so there will be no parents/children
        if(category.hasChildren()) {
            categoryName.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.ic_right_arrow_dark, 0);
        }
        return convertView;
    }
}
