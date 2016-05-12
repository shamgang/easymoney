package com.shamik.budget.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AnalyticsFragment extends BaseCategorySelectFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);
        Button categorizeButton = (Button)view.findViewById(R.id.analytics_categorize_button);
        // categorize button behavior
        categorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open category list as selection modal
                SelectCategoryDialogFragment selectCategoryDialogFragment
                        = new SelectCategoryDialogFragment();
                Bundle args = new Bundle();
                args.putString("parent", getTag());
                selectCategoryDialogFragment.setArguments(args);
                selectCategoryDialogFragment.show(getActivity().getSupportFragmentManager(),
                        getActivity().getString(R.string.select_category_dialog_fragment_title));
            }
        });
        return view;
    }

    @Override
    public void setCategory(Category category) {
        Button categorizeButton = (Button)getView().findViewById(R.id.analytics_categorize_button);
        categorizeButton.setText(category.getName());
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.analytics_fragment_title);
    }
}
