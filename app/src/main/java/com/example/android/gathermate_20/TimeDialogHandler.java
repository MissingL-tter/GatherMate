package com.example.android.gathermate_20;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeDialogHandler extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    Integer hour;
    Integer minute;
    String amPm = "AM";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            TimePickerDialog dialog;



            return new TimePickerDialog(getActivity(), this, hour, min,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {

        this.hour = hour;
        this.minute = min;

        if(hour >= 12) {
            amPm = "PM";
            if(hour > 12){
                hour -= 12;
            }
        }else if(hour == 0){
            hour = 12;
        }

        TextView hourTV = (TextView) getActivity().findViewById(R.id.addEventHourTextView);
        hourTV.setText(String.valueOf(hour));

        TextView minTV = (TextView) getActivity().findViewById(R.id.addEventMinTextView);
        minTV.setText(String.valueOf(min));

        TextView amPmTV = (TextView) getActivity().findViewById(R.id.addEventAmPmTV);
        amPmTV.setText(amPm);
    }

}
