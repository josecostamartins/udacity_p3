package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import au.com.bytecode.opencsv.CSVReader;

public class GraphActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STOCK_LOADER = 10;

    LineChart mLineChart;
    BarChart mBarChart;

    private String mSymbol = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph);

        mLineChart = (LineChart) findViewById(R.id.line_chart);
        mLineChart.setBackgroundColor(Color.LTGRAY);

        mBarChart = (BarChart) findViewById(R.id.bar_chart);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.setPinchZoom(false);
//        mBarChart.setMaxVisibleValueCount(20);
        mBarChart.setDescriptionColor(Color.WHITE);
        mBarChart.setGridBackgroundColor(Color.WHITE);
        mBarChart.setBorderColor(Color.WHITE);
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        YAxis yAxisLeft = mBarChart.getAxisLeft();
        yAxisLeft.setTextColor(Color.WHITE);
        YAxis yAxisRight = mBarChart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);


        mBarChart.animateXY(2000, 2000);


        mSymbol = getIntent().getStringExtra("symbol");
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = Contract.Quote.COLUMN_SYMBOL + "=?";
        String[] selectionArgs = {mSymbol};

        return new CursorLoader(this,
                Contract.Quote.uri,
                Contract.Quote.QUOTE_COLUMNS,
                selection,
                selectionArgs,
                Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            setBarChartData(cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader = null;
    }


    private void setChartData(String data){


        CSVReader csvReader = new CSVReader(new StringReader(data));
        ArrayList<Entry> yVals1 = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();

        String [] nextLine;
        int i = 0;
        try {
            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                times.add(nextLine[0]);
                yVals1.add(new Entry( Float.parseFloat(nextLine[4]), i++));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        LineDataSet set1 = new LineDataSet(yVals1, mSymbol);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets

        LineData lineData = new LineData(times, dataSets);
        mLineChart.setData(lineData);
    }

    private void setBarChartData(String data){


        CSVReader csvReader = new CSVReader(new StringReader(data));
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> times = new ArrayList<>();

        String [] nextLine;
        int i = 0;
        try {
            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                times.add(timestampToString(Long.parseLong(nextLine[0])));
                entries.add(new BarEntry( Float.parseFloat(nextLine[4]), i++));
            }
            Collections.reverse(times);
            Collections.reverse(entries);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BarDataSet barDataSet = new BarDataSet(entries, mSymbol);
        barDataSet.setColor(Color.rgb(233, 30, 99));
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setHighLightColor(Color.WHITE);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet); // add the datasets

        BarData barData = new BarData(times, dataSets);
        mBarChart.setData(barData);
    }

    public static String timestampToString(long timestamp){
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        return df.format(new Date(timestamp));
    }

    public static Date timestampToDate(long timestamp){
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
        String localTime = df.format(new Date(timestamp));
        Date date = null;
        try{
            date = df.parse(localTime);
        }
        catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }

}
