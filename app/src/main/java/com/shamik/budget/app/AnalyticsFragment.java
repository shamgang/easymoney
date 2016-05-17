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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AnalyticsFragment extends BaseCategorySelectFragment {
    private View mView;
    private Category mCategory;
    private XYPlot mPlot;
    private ArrayList<Map.Entry<String, DataPoint>> mDataArray;
    private ArrayList<Number> mPlotX;
    private ArrayList<Number> mPlotY;
    private XYSeries mSelectedPoint;
    private int mSelectedIndex;
    private TextView mSelectedDateView;
    private TextView mSelectedAmountView;
    private LineAndPointFormatter mSelectedFormat;
    private ArrayList<Transaction> mSelectedTransactions;
    private ListView mSelectedTransactionsView;

    private static SimpleDateFormat sSqlDate = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sIntDate = new SimpleDateFormat("yyyyMMdd");

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
            if(transaction.isIncome()) {
                mValue -= transaction.getAmount();
            } else {
                mValue += transaction.getAmount();
            }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        final List<String> averages = Arrays.asList(getActivity().getResources()
                .getStringArray(R.array.averages));
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
                args.putString(getActivity().getString(R.string.parent_fragment_tag_tag), getTag());
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
                ArrayList<Transaction> transactionList = BudgetDatabase.getInstance()
                        .getTransactionsByCategoryIDAndDateRange(mCategory.getID(),
                                sSqlDate.format(fromDate.getCalendarView().getDate()),
                                sSqlDate.format(toDate.getCalendarView().getDate()));

                // construct a data point object for each date, containing transactions
                TreeMap<String, DataPoint> dateToData = new TreeMap<String, DataPoint>();
                double sum = 0;
                for(Transaction transaction : transactionList) {
                    // add transaction amount to running sum and check running max
                    if(transaction.isIncome()) {
                        sum -= transaction.getAmount();
                    } else {
                        sum += transaction.getAmount();
                    }
                    // add transaction to data set
                    if(dateToData.containsKey(transaction.getDate())) {
                        dateToData.get(transaction.getDate()).addTransaction(transaction);
                    } else {
                        dateToData.put(transaction.getDate(), new DataPoint(transaction));
                    }
                }

                // construct plot data by iterating in date order
                mPlotX = new ArrayList<Number>();
                mPlotY = new ArrayList<Number>();
                mDataArray = new ArrayList<Map.Entry<String, DataPoint>>(dateToData.entrySet());
                // format date as a monotonically increasing integer as x coordinate
                // keep track of maximum data point for graph boundary
                double max = 0;
                double min = 0;
                for(Map.Entry<String, DataPoint> entry : mDataArray) {
                    try {
                        // convert from SQL date to epoch time to integer date
                        mPlotX.add(Integer.valueOf(
                                sIntDate.format(sSqlDate.parse(entry.getKey()).getTime())));
                        mPlotY.add(entry.getValue().getValue());
                        if(entry.getValue().getValue() > max) {
                            max = entry.getValue().getValue();
                        }
                        if(entry.getValue().getValue() < min) {
                            min = entry.getValue().getValue();
                        }
                    } catch(ParseException e) {
                        Log.e(AnalyticsFragment.class.getName(), e.getMessage());
                    }
                }

                // calculate average by converting range from epoch time to integer date
                int fromDateInt
                        = Integer.valueOf(sIntDate.format(fromDate.getCalendarView().getDate()));
                int toDateInt
                        = Integer.valueOf(sIntDate.format(toDate.getCalendarView().getDate()));
                int numDays = toDateInt - fromDateInt + 1;
                double avg;
                if(averageSpinner.getSelectedItem().equals(averages.get(0))) {
                    // Daily
                    avg = sum / (double)numDays;
                } else {
                    // Monthly
                    avg = sum / (numDays / 30.);
                }

                // set text for sum and average
                TextView transactionAvg = (TextView)getView().findViewById(R.id.analytics_avg);
                TextView transactionSum = (TextView)getView().findViewById(R.id.analytics_sum);
                transactionAvg.setText("$" + String.format("%.2f", avg));
                transactionSum.setText("$" + String.format("%.2f", sum));

                // plot data
                XYSeries series = new SimpleXYSeries(mPlotX, mPlotY, "Transactions");
                LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
                seriesFormat.configure(getActivity().getApplicationContext(),
                        R.xml.line_point_formatter);
                mPlot.addSeries(series, seriesFormat);
                // set graph boundaries
                mPlot.setDomainBoundaries(fromDateInt - 1, toDateInt + 1, BoundaryMode.FIXED);
                mPlot.setRangeBoundaries(0, max + 1, BoundaryMode.FIXED);

                // TODO: what if no data or one date?

                // prepare formatter for selected point
                mSelectedFormat = new LineAndPointFormatter();
                mSelectedFormat.configure(getActivity().getApplicationContext(),
                        R.xml.selected_point_formatter);

                // empty selected data
                mSelectedDateView.setText("");
                mSelectedAmountView.setText("");
                // empty transaction list and shrink
                mSelectedTransactions = new ArrayList<Transaction>();
                mSelectedTransactionsView
                        .setAdapter(new TransactionAdapter(getActivity(), mSelectedTransactions));
                ViewGroup.LayoutParams layoutParams = mSelectedTransactionsView.getLayoutParams();
                layoutParams.height = 0;
                mSelectedTransactionsView.setLayoutParams(layoutParams);
                mSelectedTransactionsView.requestLayout();

                // select first point
                selectPoint(0);
            }
        });

        // graph paging button behavior
        ImageButton leftButton = (ImageButton)mView.findViewById(R.id.analytics_left_button);
        ImageButton rightButton = (ImageButton)mView.findViewById(R.id.analytics_right_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPreviousPoint();
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNextPoint();
            }
        });

        // selected data fields
        mSelectedDateView = (TextView)mView.findViewById(R.id.selected_date);
        mSelectedAmountView = (TextView)mView.findViewById(R.id.selected_amount);
        // selected transactions list behavior
        mSelectedTransactionsView = (ListView)mView.findViewById(R.id.selected_transaction_list);
        mSelectedTransactionsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity)getActivity())
                        .selectTransaction(mSelectedTransactions.get(i).getID());
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
            if(transaction.isIncome()){
                sum -= transaction.getAmount();
            } else {
                sum += transaction.getAmount();
            }
        }
        return sum;
    }

    private void selectPreviousPoint() {
        selectPoint(mSelectedIndex - 1);
    }

    private void selectNextPoint() {
        selectPoint(mSelectedIndex + 1);
    }

    private void selectPoint(int index) {
        // bounds check with silent fail
        if(index < 0 || index >= mDataArray.size()) {
            return;
        }

        mSelectedIndex = index;

        // draw selected data point
        ArrayList<Number> selectedX = new ArrayList<Number>();
        ArrayList<Number> selectedY = new ArrayList<Number>();
        selectedX.add(mPlotX.get(mSelectedIndex));
        selectedY.add(mPlotY.get(mSelectedIndex));
        if(mSelectedPoint != null) {
            mPlot.removeSeries(mSelectedPoint);
        }
        mSelectedPoint = new SimpleXYSeries(selectedX, selectedY, "Selected Point");
        mPlot.addSeries(mSelectedPoint, mSelectedFormat);
        mPlot.redraw();

        // fill selected data point views
        // make date human-readable
        Date selectedDate;
        try {
            selectedDate = sSqlDate.parse(mDataArray.get(mSelectedIndex).getKey());
        } catch(ParseException e) {
            Log.e(AnalyticsFragment.class.getName(), e.getMessage());
            selectedDate = null;
        }
        SimpleDateFormat readableDate = new SimpleDateFormat("MMMM dd, yyyy");
        mSelectedDateView.setText(readableDate.format(selectedDate));
        // sum all transaction amounts for this date
        mSelectedAmountView.setText("$" + String.format("%.2f",
                sumTransactions(mDataArray.get(mSelectedIndex).getValue().getTransactions())));

        mSelectedTransactions = mDataArray.get(mSelectedIndex).getValue().getTransactions();
        mSelectedTransactionsView.setAdapter(
                new TransactionAdapter(getActivity(), mSelectedTransactions));

        // TODO: duplicated from CategoryFragment
        // set list height to cover all items
        if(!mSelectedTransactionsView.getAdapter().isEmpty()) {
            Log.d(AnalyticsFragment.class.getName(), "isn't empty");
            View itemView = mSelectedTransactionsView.getAdapter()
                    .getView(0, null, (ViewGroup) mSelectedTransactionsView);
            itemView.measure(0, 0);
            ViewGroup.LayoutParams layoutParams = mSelectedTransactionsView.getLayoutParams();
            layoutParams.height = itemView.getMeasuredHeight() * mSelectedTransactions.size();
            mSelectedTransactionsView.setLayoutParams(layoutParams);
            mSelectedTransactionsView.requestLayout();
        }
    }
}
