package com.example.android.gathermate_20;

import android.annotation.SuppressLint;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, min,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

        this.hour = hour;
        this.minute = minute;

        String time = String.format("%d:%02d", hour, minute) + " " + "AM";
        if(hour >= 12) {
            if(hour > 12){
                hour -= 12;
            }
            time = String.format("%d:%02d", hour, minute) + " " + "PM";
        }else if(hour == 0) {
            hour = 12;
            time = String.format("%d:%02d", hour, minute) + " " + "AM";
        }

        TextView timeTV = (TextView) getActivity().findViewById((R.id.addEventSetTimeButton));
        timeTV.setText((time));
    }

}
