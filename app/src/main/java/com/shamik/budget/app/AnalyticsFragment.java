package com.shamik.budget.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AnalyticsFragment extends BaseCategorySelectFragment {
    private View mView;
    private Category mCategory;
    private XYPlot mPlot;

    private static final String AVERAGE_DAILY = "Daily";
    private static final String AVERAGE_MONTHLY = "Monthly";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_analytics, container, false);

        // style plot
        mPlot = (XYPlot)mView.findViewById(R.id.plot);
        mPlot.getLegendWidget().setVisible(false);
        mPlot.getGraphWidget().setDomainGridLinePaint(null);
        mPlot.getGraphWidget().setRangeGridLinePaint(null);
        mPlot.getGraphWidget().setGridBackgroundPaint(null);
        mPlot.getGraphWidget().setDomainOriginLinePaint(null);
        mPlot.getGraphWidget().setRangeOriginLinePaint(null);
        mPlot.getGraphWidget().setDomainTickLabelPaint(null);
        mPlot.getGraphWidget().setRangeTickLabelPaint(null);
        mPlot.getGraphWidget().setDomainOriginTickLabelPaint(null);
        mPlot.getGraphWidget().setRangeOriginTickLabelPaint(null);

        // spinner
        final Spinner averageSpinner = (Spinner)mView.findViewById(R.id.average_spinner);
        List<String> averages = new ArrayList<String>();
        averages.add(AVERAGE_DAILY);
        averages.add(AVERAGE_MONTHLY);
        ArrayAdapter<String> averagesAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, averages);
        averageSpinner.setAdapter(averagesAdapter);
        averageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

                ArrayList<Transaction> transactionList = BudgetDatabase.getInstance()
                        .getTransactionsWhere(BudgetDatabase.COLUMN_CATEGORY + "='" + mCategory.getName() + "'");
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

                // calculate average
                double numDays =
                        (toDate.getCalendarView().getDate() - fromDate.getCalendarView().getDate())
                        / 1000. / 60. / 60. / 24 + 1;
                Log.d(AnalyticsFragment.class.getName(), Long.toString(toDate.getCalendarView().getDate()));
                Log.d(AnalyticsFragment.class.getName(), Long.toString(fromDate.getCalendarView().getDate()));
                Log.d(AnalyticsFragment.class.getName(), Double.toString(numDays));
                double avg;
                if(averageSpinner.getSelectedItem().equals(AVERAGE_DAILY)) {
                    avg = sum / numDays;
                } else {
                    avg = sum / (numDays / 30.);
                }
                TextView transactionAvg = (TextView)getView().findViewById(R.id.analytics_avg);
                transactionAvg.setText("$" + String.format("%.2f", avg));
                TextView transactionSum = (TextView)getView().findViewById(R.id.analytics_sum);
                transactionSum.setText("$" + String.format("%.2f", sum));

                XYSeries series = new SimpleXYSeries(plotX, plotY, "Transactions");
                LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
                seriesFormat.configure(getActivity().getApplicationContext(),
                        R.xml.line_point_formatter);
                LineAndPointFormatter selectedFormat = new LineAndPointFormatter();
                selectedFormat.configure(getActivity().getApplicationContext(),
                        R.xml.selected_point_formatter);
                mPlot.addSeries(series, seriesFormat);

                ArrayList<Number> selectedX = new ArrayList<Number>();
                ArrayList<Number> selectedY = new ArrayList<Number>();
                selectedX.add(20160513);
                selectedY.add(34.55);
                XYSeries selectedPoint = new SimpleXYSeries(selectedX, selectedY, "Selected Point");
                mPlot.addSeries(selectedPoint, selectedFormat);
                mPlot.redraw();

                TextView selectedDate = (TextView)mView.findViewById(R.id.selected_date);
                TextView selectedAmount = (TextView)mView.findViewById(R.id.selected_amount);
                selectedDate.setText("March 04, 2016");
                selectedAmount.setText("$" + String.format("%.2f", sum));

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
