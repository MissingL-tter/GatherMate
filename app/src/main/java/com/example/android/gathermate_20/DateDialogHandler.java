package com.example.android.gathermate_20;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.TextView;
import android.widget.DatePicker;

public class DateDialogHandler extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "DATE_DIALOG";

    Integer year;
    Integer month;
    Integer day;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // TODO: initialize to current day (optional)

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        this.year = year;
        this.month = month;
        this.day = day;

        String date = month + "/" + day + "/" + year;

        TextView timeTV = (TextView) getActivity().findViewById((R.id.addEventSetDateButton));
        timeTV.setText(date);
    }
}
