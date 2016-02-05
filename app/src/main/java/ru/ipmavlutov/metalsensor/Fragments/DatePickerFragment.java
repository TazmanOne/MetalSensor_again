package ru.ipmavlutov.metalsensor.Fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;


public class DatePickerFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatePickerFragmentListener datePickerListener;
       public interface DatePickerFragmentListener{
    public void DataSet(Date date, String tag);
}
    public void callBackDate(Date date){
    this.datePickerListener.DataSet(date,DatePickerFragment.this.getTag());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(),this , year, month, day);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        Date date = c.getTime();
        callBackDate(date);
        if(view.isShown()){
            view.updateDate(year,month,day);
        }

    }
    public static DatePickerFragment newInstance(DatePickerFragmentListener listener) {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setDatePickerListener(listener);
        return fragment;
    }
    public DatePickerFragmentListener getDatePickerListener() {
        return this.datePickerListener;
    }
    public void setDatePickerListener(DatePickerFragmentListener listener) {
        this.datePickerListener = listener;
    }
}