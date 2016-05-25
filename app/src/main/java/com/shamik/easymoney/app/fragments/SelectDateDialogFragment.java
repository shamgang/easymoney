package com.shamik.easymoney.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.DateSelector;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Shamik on 5/23/2016.
 */
public class SelectDateDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        long defaultDate = getArguments().getLong(AnalyticsFragment.DEFAULT_DATE_TAG);
        // expects 0 if no min / max
        long minDate = getArguments().getLong(AnalyticsFragment.MIN_DATE_TAG);
        long maxDate = getArguments().getLong(AnalyticsFragment.MAX_DATE_TAG);
        String parentTag = getArguments().getString(AnalyticsFragment.PARENT_TAG_TAG);
        final DateSelector parentFragment
                = (DateSelector)getFragmentManager().findFragmentByTag(parentTag);
        final String ID = getArguments().getString(AnalyticsFragment.DATE_ID_TAG);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.dialog_fragment_select_date, null);
        final DatePicker datePicker = (DatePicker)view.findViewById(R.id.datepicker);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(defaultDate));
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);
        if(minDate != 0) {
            datePicker.setMinDate(minDate);
        }
        if(maxDate != 0) {
            datePicker.setMaxDate(maxDate);
        }
        Button select = (Button)view.findViewById(R.id.select_date_button);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentFragment.setDate(ID, datePicker.getCalendarView().getDate());
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }
}
