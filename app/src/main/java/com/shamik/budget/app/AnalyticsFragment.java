package com.shamik.budget.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AnalyticsFragment extends BaseCategorySelectFragment {
    private View mView;
    private Category mCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_analytics, container, false);
        Button categorizeButton = (Button)mView.findViewById(R.id.analytics_categorize_button);
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

        XYPlot plot = (XYPlot)mView.findViewById(R.id.plot);
        Number[] seriesNumbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
        XYSeries series = new SimpleXYSeries(Arrays.asList(seriesNumbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series");
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesFormat.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_labels);
        plot.addSeries(series, seriesFormat);

        Button analyzeButton = (Button)mView.findViewById(R.id.analyze_button);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate fields
                if(mCategory == null) {
                    Toast toast = Toast.makeText(getActivity(), "Please choose a category", 2000);
                    toast.show();
                    return;
                }
                DatePicker fromDate = (DatePicker)mView.findViewById(R.id.from_date);
                DatePicker toDate = (DatePicker)mView.findViewById(R.id.to_date);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                String message = mCategory.getName() + " "
                        + sdf.format(fromDate.getCalendarView().getDate()) + " "
                        + sdf.format(toDate.getCalendarView().getDate());
                Toast toast2 = Toast.makeText(getActivity(), message, 2000);
                toast2.show();
            }
        });

        return mView;
    }

    @Override
    public void setCategory(Category category) {
        mCategory = category;

        Button categorizeButton = (Button)getView().findViewById(R.id.analytics_categorize_button);
        categorizeButton.setText(mCategory.getName());

        ArrayList<Transaction> transactionList = ((MainActivity)getActivity()).mBudgetDatabase
                .getTransactionsWhere(BudgetDatabaseHelper.COLUMN_CATEGORY + "='" + mCategory.getName() + "'");

        double sum = 0;
        for(Transaction transaction : transactionList) {
            sum += transaction.getAmountDollars() + transaction.getAmountCents()/100.;
        }

        TextView transactionAvg = (TextView)getView().findViewById(R.id.analytics_avg);
        TextView transactionSum = (TextView)getView().findViewById(R.id.analytics_sum);
        transactionSum.setText("Total: $" + String.format("%.2f", sum));


    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.analytics_fragment_title);
    }
}
