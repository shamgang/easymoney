package com.shamik.budget.app.fragments;

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
import com.shamik.budget.app.util.BaseCategorySelectFragment;
import com.shamik.budget.app.data.BudgetDatabase;
import com.shamik.budget.app.types.Category;
import com.shamik.budget.app.MainActivity;
import com.shamik.budget.app.R;
import com.shamik.budget.app.types.Transaction;
import com.shamik.budget.app.adapters.TransactionAdapter;
import com.shamik.budget.app.util.TransactionListHelper;

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
    private DatePicker mFromDate;
    private DatePicker mToDate;
    private Spinner mAverageSpinner;
    private List<String> mAverages;
    private XYPlot mPlot;
    private XYSeries mPlotSeries;
    private XYSeries mSelectedPoint;
    private int mSelectedIndex;
    private TextView mSelectedDateView;
    private TextView mSelectedAmountView;
    private LineAndPointFormatter mSelectedFormat;
    private ArrayList<Transaction> mSelectedTransactions;
    private ListView mSelectedTransactionsView;

    private static SimpleDateFormat sSqlDate = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sIntDate = new SimpleDateFormat("yyyyMMdd");

    // UI state
    private static Bundle sUIState;
    private static Category sCategory;
    // data state
    private static Bundle sDataState;
    private static ArrayList<Map.Entry<String, DataPoint>> sDataArray;
    private static ArrayList<Number> sPlotX;
    private static ArrayList<Number> sPlotY;
    // UI state tags
    private static final String CATEGORY_ID_TAG = "category_id";
    private static final String FROM_DATE_TAG = "from_date";
    private static final String TO_DATE_TAG = "to_date";
    private static final String AVERAGE_TYPE_TAG = "average_type";
    private static final String SELECTED_POINT_TAG = "selected_point";
    // data state tags
    private static final String AVERAGE_TAG = "average";
    private static final String SUM_TAG = "sum";
    private static final String FROM_DATE_INT_TAG = "from_date_int";
    private static final String TO_DATE_INT_TAG = "to_date_int";
    private static final String MIN_TAG = "min";
    private static final String MAX_TAG = "max";


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

        Button categorizeButton = (Button)mView.findViewById(R.id.analytics_categorize_button);
        // categorize button behavior
        categorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open category list as selection modal
                SelectCategoryDialogFragment selectCategoryDialogFragment
                        = new SelectCategoryDialogFragment();
                Bundle args = new Bundle();
                args.putString(MainActivity.PARENT_FRAGMENT_TAG_TAG, getTag());
                selectCategoryDialogFragment.setArguments(args);
                selectCategoryDialogFragment.show(getActivity().getSupportFragmentManager(),
                        getActivity().getString(R.string.select_category_dialog_fragment_title));
            }
        });

        // datepickers
        mFromDate = (DatePicker)mView.findViewById(R.id.from_date);
        mToDate = (DatePicker)mView.findViewById(R.id.to_date);

        // spinner
        mAverageSpinner = (Spinner)mView.findViewById(R.id.average_spinner);
        mAverages = Arrays.asList(getActivity().getResources().getStringArray(R.array.averages));
        ArrayAdapter<String> averagesAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, mAverages);
        mAverageSpinner.setAdapter(averagesAdapter);
        mAverageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button analyzeButton = (Button)mView.findViewById(R.id.analyze_button);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analyze();
            }
        });

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

        // if resuming, restore state
        if(sUIState != null) {
            // set category
            setCategory(sCategory);
            // set datepickers
            mFromDate.getCalendarView().setDate(sUIState.getLong(FROM_DATE_TAG));
            mToDate.getCalendarView().setDate(sUIState.getLong(TO_DATE_TAG));
            // set spinner selection
            mAverageSpinner.setSelection(sUIState.getInt(AVERAGE_TYPE_TAG));

            if(sDataState != null) {
                // analysis has been performed, complete display
                displayData(sDataState.getDouble(AVERAGE_TAG),
                        sDataState.getDouble(SUM_TAG),
                        sDataState.getInt(FROM_DATE_INT_TAG),
                        sDataState.getInt(TO_DATE_INT_TAG),
                        sDataState.getDouble(MIN_TAG),
                        sDataState.getDouble(MAX_TAG));
                selectPoint(sUIState.getInt(SELECTED_POINT_TAG));
            }
        }

        return mView;
    }

    @Override
    public void onDestroyView() {
        // save UI state
        sUIState = new Bundle();
        // category
        sUIState.putInt(CATEGORY_ID_TAG, sCategory.getID());
        // datepickers
        sUIState.putLong(FROM_DATE_TAG, mFromDate.getCalendarView().getDate());
        sUIState.putLong(TO_DATE_TAG, mToDate.getCalendarView().getDate());
        // average spinner
        sUIState.putInt(AVERAGE_TYPE_TAG, mAverageSpinner.getSelectedItemPosition());
        // selected point
        sUIState.putInt(SELECTED_POINT_TAG, mSelectedIndex);
        super.onDestroyView();
    }

    @Override
    public void setCategory(Category category) {
        sCategory = category;

        Button categorizeButton = (Button)mView.findViewById(R.id.analytics_categorize_button);
        categorizeButton.setText(sCategory.getName());
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.analytics_fragment_title);
    }

    private void analyze() {
        // validate fields
        if(sCategory == null) {
            Toast toast = Toast.makeText(getActivity(), "Please choose a category", 2000);
            toast.show();
            return;
        }

        // format epoch dates as SQL and query database for transactions
        ArrayList<Transaction> transactionList = BudgetDatabase.getInstance()
                .getTransactionsByCategoryIDAndDateRange(sCategory.getID(),
                        sSqlDate.format(mFromDate.getCalendarView().getDate()),
                        sSqlDate.format(mToDate.getCalendarView().getDate()));

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
        sPlotX = new ArrayList<Number>();
        sPlotY = new ArrayList<Number>();
        sDataArray = new ArrayList<Map.Entry<String, DataPoint>>(dateToData.entrySet());
        // format date as a monotonically increasing integer as x coordinate
        // keep track of maximum data point for graph boundary
        double max = 0;
        double min = 0;
        for(Map.Entry<String, DataPoint> entry : sDataArray) {
            try {
                // convert from SQL date to epoch time to integer date
                sPlotX.add(Integer.valueOf(
                        sIntDate.format(sSqlDate.parse(entry.getKey()).getTime())));
                sPlotY.add(entry.getValue().getValue());
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
                = Integer.valueOf(sIntDate.format(mFromDate.getCalendarView().getDate()));
        int toDateInt
                = Integer.valueOf(sIntDate.format(mToDate.getCalendarView().getDate()));
        int numDays = toDateInt - fromDateInt + 1;
        double avg;
        if(mAverageSpinner.getSelectedItem().equals(mAverages.get(0))) {
            // Daily
            avg = sum / (double)numDays;
        } else {
            // Monthly
            avg = sum / (numDays / 30.);
        }

        storeData(avg, sum, fromDateInt, toDateInt, min, max);
        displayData(avg, sum, fromDateInt, toDateInt, min, max);
    }

    private void storeData(double avg, double sum, int fromDateInt, int toDateInt,
                           double min, double max) {
        sDataState = new Bundle();
        sDataState.putDouble(AVERAGE_TAG, avg);
        sDataState.putDouble(SUM_TAG, sum);
        sDataState.putInt(FROM_DATE_INT_TAG, fromDateInt);
        sDataState.putInt(TO_DATE_INT_TAG, toDateInt);
        sDataState.putDouble(MIN_TAG, min);
        sDataState.putDouble(MAX_TAG, max);
    }

    private void displayData(double avg, double sum, int fromDateInt, int toDateInt,
                             double min, double max) {
        // set text for sum and average
        TextView transactionAvg = (TextView)mView.findViewById(R.id.analytics_avg);
        TextView transactionSum = (TextView)mView.findViewById(R.id.analytics_sum);
        transactionAvg.setText("$" + String.format("%.2f", avg));
        transactionSum.setText("$" + String.format("%.2f", sum));

        // plot data
        if(mPlotSeries != null) {
            mPlot.removeSeries(mPlotSeries);
        }
        mPlotSeries = new SimpleXYSeries(sPlotX, sPlotY, "Transactions");
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter);
        mPlot.addSeries(mPlotSeries, seriesFormat);
        // set graph boundaries
        mPlot.setDomainBoundaries(fromDateInt - 1, toDateInt + 1, BoundaryMode.FIXED);
        mPlot.setRangeBoundaries(min, max + 1, BoundaryMode.FIXED);

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
        if(index < 0 || index >= sDataArray.size()) {
            return;
        }

        mSelectedIndex = index;

        // draw selected data point
        ArrayList<Number> selectedX = new ArrayList<Number>();
        ArrayList<Number> selectedY = new ArrayList<Number>();
        selectedX.add(sPlotX.get(mSelectedIndex));
        selectedY.add(sPlotY.get(mSelectedIndex));
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
            selectedDate = sSqlDate.parse(sDataArray.get(mSelectedIndex).getKey());
        } catch(ParseException e) {
            Log.e(AnalyticsFragment.class.getName(), e.getMessage());
            selectedDate = null;
        }
        SimpleDateFormat readableDate = new SimpleDateFormat("MMMM dd, yyyy");
        mSelectedDateView.setText(readableDate.format(selectedDate));
        // sum all transaction amounts for this date
        mSelectedAmountView.setText("$" + String.format("%.2f",
                sumTransactions(sDataArray.get(mSelectedIndex).getValue().getTransactions())));

        mSelectedTransactions = sDataArray.get(mSelectedIndex).getValue().getTransactions();
        mSelectedTransactionsView.setAdapter(
                new TransactionAdapter(getActivity(), mSelectedTransactions));

        TransactionListHelper.resizeTransactionList(mSelectedTransactions,
                mSelectedTransactionsView);
    }
}
