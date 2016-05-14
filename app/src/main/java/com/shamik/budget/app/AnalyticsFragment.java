package com.shamik.budget.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AnalyticsFragment extends BaseCategorySelectFragment {
    private static final String TAG = "AnalyticsFragment";
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

                ArrayList<Transaction> transactionList = ((MainActivity)getActivity()).mBudgetDatabase
                        .getTransactionsWhere(BudgetDatabaseHelper.COLUMN_CATEGORY + "='" + mCategory.getName() + "'");
                //ArrayList<Number> plotX = new ArrayList<Number>();
                Number[] plotXnums = {20160513, 20160514, 20160515};
                List<Number> plotX = Arrays.asList(plotXnums);
                //ArrayList<Number> plotY = new ArrayList<Number>();
                Number[] plotYnums = {34.55, 12, 5};
                List<Number> plotY = Arrays.asList(plotYnums);
                double sum = 0;
                SimpleDateFormat intDate = new SimpleDateFormat("yyyyMMdd");
                for(Transaction transaction : transactionList) {
                    double amount = transaction.getAmountDollars() + transaction.getAmountCents()/100.;
                    // add transaction amount to running sum
                    sum += amount;
                    // format date as a monotonically increasing integer as x coordinate
                    //plotX.add(Integer.valueOf(intDate.format(transaction.getDate())));
                    // add amount as y coordinate
                    //plotY.add(amount);
                }

                Log.d(TAG, plotX.toString());
                Log.d(TAG, plotY.toString());

                TextView transactionAvg = (TextView)getView().findViewById(R.id.analytics_avg);
                TextView transactionSum = (TextView)getView().findViewById(R.id.analytics_sum);
                transactionSum.setText("Total: $" + String.format("%.2f", sum));

                XYPlot plot = (XYPlot)mView.findViewById(R.id.plot);
                plot.getLegendWidget().setVisible(false);
                plot.getGraphWidget().setDomainGridLinePaint(null);
                plot.getGraphWidget().setRangeGridLinePaint(null);
                plot.getGraphWidget().setGridBackgroundPaint(null);
                plot.getGraphWidget().setDomainOriginLinePaint(null);
                plot.getGraphWidget().setRangeOriginLinePaint(null);
                plot.getGraphWidget().setDomainTickLabelPaint(null);
                plot.getGraphWidget().setRangeTickLabelPaint(null);
                plot.getGraphWidget().setDomainOriginTickLabelPaint(null);
                plot.getGraphWidget().setRangeOriginTickLabelPaint(null);
                XYSeries series = new SimpleXYSeries(plotX, plotY, "Transactions");
                LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
                seriesFormat.configure(getActivity().getApplicationContext(),
                        R.xml.line_point_formatter);
                LineAndPointFormatter selectedFormat = new LineAndPointFormatter();
                selectedFormat.configure(getActivity().getApplicationContext(),
                        R.xml.selected_point_formatter);
                plot.addSeries(series, seriesFormat);

                ArrayList<Number> selectedX = new ArrayList<Number>();
                ArrayList<Number> selectedY = new ArrayList<Number>();
                selectedX.add(20160513);
                selectedY.add(34.55);
                XYSeries selectedPoint = new SimpleXYSeries(selectedX, selectedY, "Selected Point");
                plot.addSeries(selectedPoint, selectedFormat);
                plot.redraw();

            }
        });

        return mView;
    }

    @Override
    public void setCategory(Category category) {
        mCategory = category;

        Button categorizeButton = (Button)getView().findViewById(R.id.analytics_categorize_button);
        categorizeButton.setText(mCategory.getName());
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.analytics_fragment_title);
    }
}
