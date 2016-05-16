package com.shamik.budget.app;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AnalyticsFragment extends BaseCategorySelectFragment {
    private View mView;
    private Category mCategory;
    private XYPlot mPlot;

    private static final String AVERAGE_DAILY = "Daily";
    private static final String AVERAGE_MONTHLY = "Monthly";

    private class DataPoint {
        private double mValue;
        private ArrayList<Transaction> mTransactions;

        public DataPoint() {
            mValue = 0;
            mTransactions = new ArrayList<Transaction>();
        }

        public DataPoint(Transaction transaction) {
            this();
            addTransaction(transaction);
        }

        public void addTransaction(Transaction transaction) {
            mValue += transaction.getAmount();
            mTransactions.add(transaction);
        }

        public double getValue() {
            return mValue;
        }

        public ArrayList<Transaction> getTransactions() {
            return mTransactions;
        }
    }

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

                // format epoch dates as SQL and query database for transactions
                SimpleDateFormat sqlDate = new SimpleDateFormat("yyyy-MM-dd");
                ArrayList<Transaction> transactionList = BudgetDatabase.getInstance()
                        .getTransactionsByCategoryIDAndDateRange(mCategory.getID(),
                                sqlDate.format(fromDate.getCalendarView().getDate()),
                                sqlDate.format(toDate.getCalendarView().getDate()));

                // construct a data point object for each date, containing transactions
                TreeMap<String, DataPoint> dateToData = new TreeMap<String, DataPoint>();
                double sum = 0;
                for(Transaction transaction : transactionList) {
                    // add transaction amount to running sum
                    sum += transaction.getAmount();
                    // add transaction to data set
                    if(dateToData.containsKey(transaction.getDate())) {
                        dateToData.get(transaction.getDate()).addTransaction(transaction);
                    } else {
                        dateToData.put(transaction.getDate(), new DataPoint(transaction));
                    }
                    // format date as a monotonically increasing integer as x coordinate
                    //plotX.add(Integer.valueOf(intDate.format(transaction.getDate())));
                    // add amount as y coordinate
                    //plotY.add(amount);
                }

                // TODO: remove stub data
                TreeMap<String, DataPoint> stubDateToData = new TreeMap<String, DataPoint>();
                stubDateToData.put("2016-05-14", new DataPoint(new Transaction(34, 55, "test", -1, false)));
                stubDateToData.get("2016-05-14").addTransaction(new Transaction(34, 55, "test", -1, false));
                stubDateToData.put("2016-05-15", new DataPoint(new Transaction(34, 55, "test", -1, false)));
                stubDateToData.put("2016-05-16", new DataPoint(new Transaction(34, 55, "test", -1, false)));
                dateToData = stubDateToData;
                sum = 54.21;

                // construct plot data by iterating in date order
                ArrayList<Number> plotX = new ArrayList<Number>();
                ArrayList<Number> plotY = new ArrayList<Number>();
                // format date as a monotonically increasing integer as x coordinate
                SimpleDateFormat intDate = new SimpleDateFormat("yyyyMMdd");
                for(Map.Entry<String, DataPoint> entry : dateToData.entrySet()) {
                    try {
                        // convert from SQL date to epoch time to integer date
                        plotX.add(Integer.valueOf(intDate.format(sqlDate.parse(entry.getKey()).getTime())));
                        plotY.add(entry.getValue().getValue());
                    } catch(ParseException e) {
                        Log.e(AnalyticsFragment.class.getName(), e.getMessage());
                    }
                }

                // calculate average by converting range from epoch time to integer date
                int numDays =
                        Integer.valueOf(intDate.format(toDate.getCalendarView().getDate()))
                        - Integer.valueOf(intDate.format(fromDate.getCalendarView().getDate()))
                        + 1;
                double avg;
                if(averageSpinner.getSelectedItem().equals(AVERAGE_DAILY)) {
                    avg = sum / (double)numDays;
                } else {
                    avg = sum / (numDays / 30.);
                }

                // set text for sum and average
                TextView transactionAvg = (TextView)getView().findViewById(R.id.analytics_avg);
                TextView transactionSum = (TextView)getView().findViewById(R.id.analytics_sum);
                transactionAvg.setText("$" + String.format("%.2f", avg));
                transactionSum.setText("$" + String.format("%.2f", sum));

                // plot data
                XYSeries series = new SimpleXYSeries(plotX, plotY, "Transactions");
                LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
                seriesFormat.configure(getActivity().getApplicationContext(),
                        R.xml.line_point_formatter);
                LineAndPointFormatter selectedFormat = new LineAndPointFormatter();
                selectedFormat.configure(getActivity().getApplicationContext(),
                        R.xml.selected_point_formatter);
                mPlot.addSeries(series, seriesFormat);

                // draw selected data point
                ArrayList<Number> selectedX = new ArrayList<Number>();
                ArrayList<Number> selectedY = new ArrayList<Number>();
                selectedX.add(plotX.get(0));
                selectedY.add(plotY.get(0));
                XYSeries selectedPoint = new SimpleXYSeries(selectedX, selectedY, "Selected Point");
                mPlot.addSeries(selectedPoint, selectedFormat);
                mPlot.redraw();

                // fill selected data point views
                TextView selectedDateView = (TextView)mView.findViewById(R.id.selected_date);
                TextView selectedAmountView = (TextView)mView.findViewById(R.id.selected_amount);
                // make date human-readable
                Date selectedDate;
                try {
                    selectedDate = sqlDate.parse(dateToData.firstEntry().getKey());
                } catch(ParseException e) {
                    Log.e(AnalyticsFragment.class.getName(), e.getMessage());
                    selectedDate = null;
                }
                SimpleDateFormat readableDate = new SimpleDateFormat("MMMM dd, yyyy");
                selectedDateView.setText(readableDate.format(selectedDate));
                // sum all transaction amounts for this date
                selectedAmountView.setText("$" + String.format("%.2f",
                        sumTransactions(dateToData.firstEntry().getValue().getTransactions())));
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

    private double sumTransactions(ArrayList<Transaction> transactions) {
        double sum = 0;
        for(Transaction transaction : transactions) {
            sum += transaction.getAmount();
        }
        return sum;
    }
}
