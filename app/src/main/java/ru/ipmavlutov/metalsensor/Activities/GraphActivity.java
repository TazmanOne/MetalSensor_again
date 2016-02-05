package ru.ipmavlutov.metalsensor.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;

import java.util.Calendar;
import java.util.Date;

import ru.ipmavlutov.metalsensor.DBHelper;
import ru.ipmavlutov.metalsensor.Fragments.DatePickerFragment;
import ru.ipmavlutov.metalsensor.Fragments.DatePickerFragment.DatePickerFragmentListener;
import ru.ipmavlutov.metalsensor.R;


public class GraphActivity extends AppCompatActivity implements DatePickerFragmentListener {

    DatePickerFragment fragment;
    DatePickerFragment fragment2;

    LineChart lineChart;
    LineData lineData;
    SQLiteDatabase sqLiteOpenHelper;
    DBHelper dbHelper;
    Cursor cursor;
    DatePicker datePicker;
    Button btn_dateDialog;
    int day;
    int month;
    int year;
    int day2;
    int month2;
    int year2;
    TextView label_date;
    TextView label_date2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        lineChart = (LineChart) findViewById(R.id.line_chart);
        lineData = new LineData();
        dbHelper = new DBHelper(this);
        sqLiteOpenHelper = dbHelper.getReadableDatabase();
        btn_dateDialog = (Button) findViewById(R.id.btn_date_picker);
        label_date = (TextView) findViewById(R.id.textView3);
        label_date2 = (TextView) findViewById(R.id.textView4);
        fragment = DatePickerFragment.newInstance(this);
        fragment2 = DatePickerFragment.newInstance(this);
        btn_dateDialog.setOnClickListener(lister);

    }


    public void setYear2(int year2) {
        this.year2 = year2;
    }

    public void setMonth2(int month2) {
        this.month2 = month2 + 1;
    }

    public void setDay2(int day2) {
        this.day2 = day2;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setMonth(int month) {
        this.month = month + 1;
    }

    public void setYear(int year) {
        this.year = year;
    }

    View.OnClickListener lister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_date_picker:
                    fragment2.show(getSupportFragmentManager(), "DatePicker2");
                    fragment.show(getSupportFragmentManager(), "DatePicker");
            }
        }
    };

    @Override
    public void DataSet(Date date, String tag) {

        if (tag.equals(fragment.getTag())) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            setDay(c.get(Calendar.DAY_OF_MONTH));
            setMonth(c.get(Calendar.MONTH));
            setYear(c.get(Calendar.YEAR));
            label_date.setText(String.format("С: %d/%d/%d", day, month, year));
            Toast.makeText(GraphActivity.this, "Date1: " + day + "/" + month + "/" + year, Toast.LENGTH_SHORT).show();

        }
        if (tag.equals(fragment2.getTag())) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            setDay2(c.get(Calendar.DAY_OF_MONTH));
            setMonth2(c.get(Calendar.MONTH));
            setYear2(c.get(Calendar.YEAR));
            label_date2.setText(String.format("По: %d/%d/%d", day2, month2, year2));
            Toast.makeText(GraphActivity.this, "Date2: " + day2 + "/" + month2 + "/" + year2, Toast.LENGTH_SHORT).show();
        }
    }

}

