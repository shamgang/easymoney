package com.shamik.easymoney.app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.BaseCategorySelectFragment;
import com.shamik.easymoney.app.data.BudgetDatabase;
import com.shamik.easymoney.app.types.Category;
import com.shamik.easymoney.app.MainActivity;
import com.shamik.easymoney.app.types.Transaction;
import com.shamik.easymoney.app.adapters.TransactionAdapter;
import com.shamik.easymoney.app.util.DateSelector;
import com.shamik.easymoney.app.util.TransactionListHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Shamik on 5/5/2016.
 */
public class AnalyticsFragment extends BaseCategorySelectFragment implements DateSelector {
    private View mView;
    private Category mCategory;
    private Button mFromDateButton;
    private Button mToDateButton;
    private long mFromDate;
    private long mToDate;
    private Spinner mAverageSpinner;
    private List<String> mAverages;
    private double mDailyAverage;
    private double mMonthlyAverage;
    private TextView mAverageView;
    private XYPlot mPlot;
    private ArrayList<Map.Entry<String, DataPoint>> mDataArray;
    private boolean hasPlotted;
    private ArrayList<Number> mPlotX;
    private ArrayList<Number> mPlotY;
    private XYSeries mPlotSeries;
    private int mFromDateInt;
    private int mToDateInt;
    private double mDailyMin;
    private double mDailyMax;
    private XYSeries mSelectedPoint;
    private int mSelectedIndex;
    private TextView mSelectedDateView;
    private TextView mSelectedAmountView;
    private LineAndPointFormatter mSelectedFormat;
    private ArrayList<Transaction> mSelectedTransactions;
    private ListView mSelectedTransactionsView;

    private static SimpleDateFormat sSqlDate = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sIntDate = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sReadableDate = new SimpleDateFormat("MMMM dd, yyyy");

    private static final String FROM_DATE_ID = "from_date";
    private static final String TO_DATE_ID = "to_date";

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // prevents object from being destroyed and recreated on orientation change
        // and presumably whenever else the activity is destroyed. also prevents onCreate, onDestroy
        setRetainInstance(true);
        // boolean to track whether the plot has been initialized through rotations, navigation
        hasPlotted = false;
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

        // datepicker buttons
        mFromDateButton = (Button)mView.findViewById(R.id.from_date_button);
        mToDateButton = (Button)mView.findViewById(R.id.to_date_button);
        final AnalyticsFragment thisFragment = this;
        mFromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDateDialogFragment selectDateDialogFragment = new SelectDateDialogFragment(
                        Calendar.getInstance(), thisFragment, FROM_DATE_ID);
                selectDateDialogFragment.show(getActivity().getSupportFragmentManager(),
                        getActivity().getString(R.string.select_date_dialog_fragment_title));
            }
        });
        mToDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectDateDialogFragment selectDateDialogFragment = new SelectDateDialogFragment(
                        Calendar.getInstance(), thisFragment, TO_DATE_ID);
                selectDateDialogFragment.show(getActivity().getSupportFragmentManager(),
                        getActivity().getString(R.string.select_date_dialog_fragment_title));
            }
        });

        // spinner
        mAverageSpinner = (Spinner)mView.findViewById(R.id.average_spinner);
        mAverages = Arrays.asList(getActivity().getResources().getStringArray(R.array.averages));
        ArrayAdapter<String> averagesAdapter =
                new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, mAverages);
        mAverageSpinner.setAdapter(averagesAdapter);
        mAverageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(mAverageView != null) {
                    if (i == 0) {
                        // Daily
                        mAverageView.setText("$" + String.format("%.2f", mDailyAverage));
                    } else {
                        // Monthly
                        mAverageView.setText("$" + String.format("%.2f", mMonthlyAverage));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // analyze button behavior
        Button analyzeButton = (Button)mView.findViewById(R.id.analyze_button);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analyze();
            }
        });

        // style plot - remove all extra ink
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

        if(hasPlotted) {
            // Resuming, plot needs to be recreated
            plotData();
            selectPoint(mSelectedIndex);
        }

        return mView;
    }

    @Override
    public void setCategory(Category category) {
        mCategory = category;
        Button categorizeButton = (Button)mView.findViewById(R.id.analytics_categorize_button);
        // fill category text view
        categorizeButton.setText(mCategory.getName());
        analyze();
    }

    public void setDate(String ID, long date) {
        if(ID.equals(FROM_DATE_ID)) {
            // From
            mFromDate = date;
            mFromDateButton.setText(sReadableDate.format(mFromDate));
        } else {
            // To
            mToDate = date;
            mToDateButton.setText(sReadableDate.format(mToDate));
        }
        analyze();
    }

    @Override
    protected String getTitle() {
        return this.getString(R.string.analytics_fragment_title);
    }

    private void analyze() {
        // validate fields
        if(mCategory == null) {
            Toast toast = Toast.makeText(getActivity(), "Please choose a category", 2000);
            toast.show();
            return;
        }
        if(mFromDate == 0 || mToDate == 0) {
            // Date not yet selected
            return;
        }

        // format epoch dates as SQL and query database for transactions
        ArrayList<Transaction> transactionList = BudgetDatabase.getInstance()
                .getTransactionsByCategoryIDAndDateRange(mCategory.getID(),
                        sSqlDate.format(mFromDate),
                        sSqlDate.format(mToDate));

        // construct a data point object for each date, containing transactions
        TreeMap<String, DataPoint> dateToData = new TreeMap<String, DataPoint>();
        double sum = 0;
        for(Transaction transaction : transactionList) {
            // add transaction amount to running sum
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

        // set sum text
        TextView transactionSum = (TextView)mView.findViewById(R.id.analytics_sum);
        transactionSum.setText("$" + String.format("%.2f", sum));

        // construct numeric plot data by iterating in date order
        mPlotX = new ArrayList<Number>();
        mPlotY = new ArrayList<Number>();
        mDataArray = new ArrayList<Map.Entry<String, DataPoint>>(dateToData.entrySet());
        // format date as a monotonically increasing integer for x axis
        // keep track of max/min data point for graph boundaries
        mDailyMax = 0;
        mDailyMin = 0;
        for(Map.Entry<String, DataPoint> entry : mDataArray) {
            try {
                // convert from SQL date to epoch time to integer date
                mPlotX.add(Integer.valueOf(
                        sIntDate.format(sSqlDate.parse(entry.getKey()).getTime())));
                mPlotY.add(entry.getValue().getValue());
                if(entry.getValue().getValue() > mDailyMax) {
                    mDailyMax = entry.getValue().getValue();
                }
                if(entry.getValue().getValue() < mDailyMin) {
                    mDailyMin = entry.getValue().getValue();
                }
            } catch(ParseException e) {
                Log.e(AnalyticsFragment.class.getName(), e.getMessage());
            }
        }

        // calculate average by converting range from epoch time to integer date
        mFromDateInt
                = Integer.valueOf(sIntDate.format(mFromDate));
        mToDateInt
                = Integer.valueOf(sIntDate.format(mToDate));
        int numDays = mToDateInt - mFromDateInt + 1;
        mAverageView = (TextView)mView.findViewById(R.id.analytics_avg);
        mDailyAverage = sum / (double)numDays;
        mMonthlyAverage = sum / (numDays / 30.);
        if(mAverageSpinner.getSelectedItem().equals(mAverages.get(0))) {
            // Daily
            mAverageView.setText("$" + String.format("%.2f", mDailyAverage));
        } else {
            // Monthly
            mAverageView.setText("$" + String.format("%.2f", mMonthlyAverage));
        }

        // fill graph
        plotData();

        // TODO: design: what if no data or one date?

        // prepare formatter for selected point
        mSelectedFormat = new LineAndPointFormatter();
        mSelectedFormat.configure(getActivity().getApplicationContext(),
                R.xml.selected_point_formatter);

        // if new data is empty, make sure old graph and selection is cleared
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

        // all necessary data to re-plot onCreateView exists in class
        // need to know for orientation change / back navigation
        hasPlotted = true;
    }

    private void plotData() {
        // remove old data
        if(mPlotSeries != null) {
            mPlot.removeSeries(mPlotSeries);
        }
        // add new data
        mPlotSeries = new SimpleXYSeries(mPlotX, mPlotY, "Transactions");
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter);
        mPlot.addSeries(mPlotSeries, seriesFormat);
        // set graph boundaries
        mPlot.setDomainBoundaries(mFromDateInt - 1, mToDateInt + 1, BoundaryMode.FIXED);
        mPlot.setRangeBoundaries(mDailyMin, mDailyMax + 1, BoundaryMode.FIXED);
        mPlot.redraw();
    }

    private double sumTransactions(ArrayList<Transaction> transactions) {
        // sum expenses and subtract income
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

        mSelectedDateView.setText(sReadableDate.format(selectedDate));
        // sum all transaction amounts for this date
        mSelectedAmountView.setText("$" + String.format("%.2f",
                sumTransactions(mDataArray.get(mSelectedIndex).getValue().getTransactions())));

        // fill transaction list for this date
        // TODO: paginate
        mSelectedTransactions = mDataArray.get(mSelectedIndex).getValue().getTransactions();
        mSelectedTransactionsView.setEmptyView(
                mView.findViewById(R.id.empty_selected_transaction_list));
        mSelectedTransactionsView.setAdapter(
                new TransactionAdapter(getActivity(), mSelectedTransactions));

        TransactionListHelper.resizeTransactionList(mSelectedTransactions,
                mSelectedTransactionsView);
    }
}
