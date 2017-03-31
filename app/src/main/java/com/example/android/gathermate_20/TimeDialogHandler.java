package com.example.android.gathermate_20;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.widget.TimePicker;

/**
 * Created by Todd on 3/31/2017.
 */

public class TimeDialogHandler extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    int hour;
    int min;


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
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hour = i;
        min = i1;
    }

    public int getHour(){
        return hour;
    }
    public int getMin(){
        return min;
    }
}
