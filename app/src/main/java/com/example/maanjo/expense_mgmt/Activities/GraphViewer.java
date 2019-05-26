package com.example.maanjo.expense_mgmt.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.maanjo.expense_mgmt.Database.Data_source;
import com.example.maanjo.expense_mgmt.Database.ExpenseReader;
import com.example.maanjo.expense_mgmt.Database.TableHelper;
import com.example.maanjo.expense_mgmt.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.formatter.AxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class GraphViewer extends AppCompatActivity{

    public static final String LOG_TAG = LogIn.class.getSimpleName();
    private Data_source dataSource;
    public String userName;
    public int userId;
    public TableHelper tableHelper;



    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        dataSource = new Data_source(this);
        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");

        Bundle bundle = getIntent().getExtras();
        userId = bundle.getInt("userId");
        userName = bundle.getString("userName");
        //userName = getIntent().getStringExtra("userString");

        PieChart pie = (PieChart) findViewById(R.id.detailledPieChart);
        pie.setData(detailledPieChart(userId));
        pie.setDrawHoleEnabled(false);
        pie.setDescription("");
        pie.setDrawSliceText(false);

        LineChart line = findViewById(R.id.lineChartView);
        line.setData(detailledLineChart(userId));
        line.setDescription("");

        BarChart bar = findViewById(R.id.barchart);
        bar.setData(detailledBarChart(userId));
        bar.setDescription("");
        YAxis yAxis = bar.getAxisLeft();
        yAxis.setAxisMinValue(0);

        BottomNavigationView navigation = findViewById(R.id.navigation3);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    protected void onResume() {

        super.onResume();
        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getInt("userId", 0);
        userName = bundle.getString("userName");

    }

    protected void onPause() {

        super.onPause();
        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }

    public LineData detailledLineChart(int userId){

        ArrayList<ExpenseReader> rawData = dataSource.getAllExpenses(userId);
        ExpenseReader eR;
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<Entry> yValues = new ArrayList<Entry>();

        ArrayList<String> aggrDate = new ArrayList<String>();
        ArrayList<Float> aggrExp = new ArrayList<Float>();

        for (int i = 0; i < rawData.size(); i++) {

            eR = rawData.get(i);

            if(!eR.getCategory().equals("Einnahme")) {

                if(aggrDate.contains(sdf.format(new Date(eR.getDate())))){

                    int pos = aggrDate.indexOf(sdf.format(new Date(eR.getDate())));
                    float x = aggrExp.get(pos);
                    aggrExp.set(pos, x += eR.getExpense()*(-1));
                }
                else{
                    aggrDate.add(sdf.format(new Date(eR.getDate())));
                    aggrExp.add(eR.getExpense()*(-1));
                }
            }

        }

        Float exp = 0f;

        for(int i = 0; i < aggrDate.size() && i < aggrExp.size(); i++){

            String date = aggrDate.get(i);

            if(i == 0) {
                exp = aggrExp.get(i);
            }
            else{

                exp += aggrExp.get(i);
            }

            yValues.add(new Entry(exp, i));
            xValues.add(date);
        }

        LineDataSet set = new LineDataSet(yValues, "Ausgaben insgesamt in €");
        set.setColor(Color.BLACK);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(1f);
        set.setCircleRadius(3f);
        set.setDrawCircleHole(false);
        set.setValueTextSize(9f);
        set.setDrawFilled(true);
        set.setFillAlpha(110);
        set.setFillColor(Color.RED);

        LineData data = new LineData(xValues, set);
        return data;
    }

    public BarData detailledBarChart(int userId) {

        ArrayList amount = new ArrayList();
        ArrayList<String> labels = new ArrayList<String>();

        labels.add("Einnahmen");
        labels.add("Ausgaben");

        ArrayList<ExpenseReader> rawData = dataSource.getAllExpenses(userId);
        ExpenseReader eR;
        float x = 0;
        String y;

        float ausgabe = 0;
        float einnahme = 0;

        for (int i = 0; i < rawData.size(); i++) {

            eR = rawData.get(i);
            x = eR.getExpense();
            y = eR.getCategory();

            if(x < 0){

                x = x*-1;
            }

            switch (y) {

                case("Lebensmittel"):
                    ausgabe += x;
                    break;
                case("Haushaltskosten"):
                    ausgabe += x;
                    break;
                case("Shopping"):
                    ausgabe += x;
                    break;
                case("Unternehmungen"):
                    ausgabe += x;
                    break;
                case("Einnahme"):
                    einnahme += x;
                    break;
                case("Sonstiges"):
                    ausgabe += x;
                    break;
            }
        }
        amount.add(new BarEntry(einnahme, 0));
        amount.add(new BarEntry(ausgabe, 1));

        BarDataSet set = new BarDataSet(amount, "Betrag in Euro");
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(labels, set);

        return data;
    }

    public PieData detailledPieChart(int userId){

        ArrayList<ExpenseReader> rawData = dataSource.getAllExpenses(userId);
        ExpenseReader eR;
        float x = 0;
        String y = "";

        float lebensmittel = 0;
        float haushaltskosten = 0;
        float shopping = 0;
        float unternehmungen = 0;
        float einnahme = 0;
        float sonstiges = 0;

        for(int i = 0; i < rawData.size(); i++){

            eR = rawData.get(i);
            x = eR.getExpense();
            y = eR.getCategory();

            switch(y){

                case("Lebensmittel"):
                    lebensmittel += x;
                    break;
                case("Haushaltskosten"):
                    haushaltskosten += x;
                    break;
                case("Shopping"):
                    shopping += x;
                    break;
                case("Unternehmungen"):
                    unternehmungen += x;
                    break;
                case("Einnahme"):
                    einnahme += x;
                    break;
                case("Sonstiges"):
                    sonstiges += x;
                    break;
            }
        }

        ArrayList<Entry> yValue = new ArrayList<Entry>();
        ArrayList<String> xValue = new ArrayList<String>();

        if(lebensmittel*(-1) > 0){
            yValue.add(new Entry(lebensmittel*(-1), 0));
            xValue.add("Lebensmittel");
        }
        if(haushaltskosten*(-1) > 0){
            yValue.add(new Entry(haushaltskosten*(-1), 1));
            xValue.add("Haushaltskosten");
        }
        if(shopping*(-1) > 0){
            yValue.add(new Entry(shopping*(-1), 2));
            xValue.add("Shopping");
        }
        if(unternehmungen*(-1) > 0){
            yValue.add(new Entry(unternehmungen*(-1), 3));
            xValue.add("Unternehmungen");
        }
        if(sonstiges*(-1) > 0){
            yValue.add(new Entry(sonstiges*(-1), 4));
            xValue.add("Sonstiges");
        }

        PieDataSet set = new PieDataSet(yValue, "");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);

        PieData data = new PieData(xValue, set);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(13f);

        return data;
    }

    //Hinzufügen eines Listeners zu dem BottomNavigationView (Menü)
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        /**
         * Anonyme onNavigationItemSelected-Listener Klasse
         * Fügt den einzelnen Menüfeldern eine Funktionalität hinzu
         * Hier: Navigieren zwischen den Activities Graph, Tabelle und Startseite
         *
         * @param item: Ausgewähltes Element des Menüs
         * @return True oder False, abhängig davon, ob ein Element ausgewählt wurde
         */
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);
            bundle.putString("userName", String.valueOf(userName));
            Intent table_intent = new Intent();

            switch (item.getItemId()) {
                case R.id.navigation_start3:

                    //startActivity(new Intent(GraphViewer.this, StartingPage.class).putExtra("userString", String.valueOf(userName)));
                    finish();
                    return true;

                case R.id.navigation_table3:

                    table_intent.setClassName("com.example.maanjo.expense_mgmt", "com.example.maanjo.expense_mgmt.Activities.TableViewer");
                    table_intent.putExtras(bundle);
                    startActivity(table_intent);
                    finish();
                    return true;

                case R.id.navigation_graph3:

                    recreate();
                    overridePendingTransition(0, 0);
                    return true;
            }
            return false;
        }
    };

}
