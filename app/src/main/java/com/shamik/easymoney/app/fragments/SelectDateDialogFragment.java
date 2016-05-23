package com.shamik.easymoney.app.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.shamik.easymoney.app.R;
import com.shamik.easymoney.app.util.DateSelector;

import java.util.Calendar;

/**
 * Created by Shamik on 5/23/2016.
 */
public class SelectDateDialogFragment extends DialogFragment {
    private Calendar mCalendar;
    private DateSelector mParent;
    private String mID;

    public SelectDateDialogFragment(Calendar calendar, DateSelector parent, String ID) {
        mCalendar = calendar;
        mParent = parent;
        mID = ID;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.dialog_fragment_select_date, null);
        final DatePicker datePicker = (DatePicker)view.findViewById(R.id.datepicker);
        datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH), null);
        Button select = (Button)view.findViewById(R.id.select_date_button);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mParent.setDate(mID, datePicker.getCalendarView().getDate());
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }
}
