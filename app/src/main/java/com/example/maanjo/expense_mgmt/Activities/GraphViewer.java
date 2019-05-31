package com.example.maanjo.expense_mgmt.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.maanjo.expense_mgmt.Database.DataSource;
import com.example.maanjo.expense_mgmt.Database.ExpenseReader;
import com.example.maanjo.expense_mgmt.Database.TableHelper;
import com.example.maanjo.expense_mgmt.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * GraphViewer verwaltet die Funktionalitaeten der Oberflaeche activity_graph
 */
public class GraphViewer extends AppCompatActivity{

    public static final String LOG_TAG = LogIn.class.getSimpleName();
    private DataSource dataSource;
    public String userName;
    public int userId;
    public TableHelper tableHelper;

    /**
     * OnCreate-Methode der Klasse GraphViewer
     * Referenziert die Klasse zum Layout und oeffnet die Datenbankverbindung
     * Uebernimmt die Parameter aus der vorherigen Activity
     *
     * Die Graphen werden mit den vorverarbeiteten Datensaetzen zusammengefuehrt und konfiguriert
     *
     * @param savedInstanceState: Gespeicherter Zustand der Activity
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        dataSource = new DataSource(this);
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

    /**
     * Oeffnet Verbindung zur Datenbank, wenn Activity erneut geoeffnet wird und nimmt die uebergebenen Parameter entgegen
     */
    protected void onResume() {

        super.onResume();
        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();
        Bundle bundle = getIntent().getExtras();
        userId = bundle.getInt("userId", 0);
        userName = bundle.getString("userName");

    }

    /**
     * Schließt bestehende Verbindung zur Datenbank, wenn Activity pausiert wird
     */
    protected void onPause() {

        super.onPause();
        Log.d(LOG_TAG, "Die Datenquelle wird geschlossen.");
        dataSource.close();
    }

    /**
     * Die Daten des Users werden aus der Datenbank ausgelesen und so formatiert, dass
     * die jeweiligen Ausgaben eines Tages zu einem Datenpunkt zusammengefasst/summiert werden
     *
     *
     * @param userId
     * @return LineData - Vorverarbeitetes Datenset für den LineGraph
     */
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

    /**
     * Die Daten des Users werden aus der Datenbank ausgelesen und so formatiert, dass
     * die jeweiligen Ausgaben und Einnahmen aufsummiert werden, um so eine Gegenueberstellung zu ermoeglichen
     *
     * @param userId
     * @return BarData - Vorverarbeitetes Datenset für den BarGraph
     */
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

    /**
     * Die Daten des Users werden aus der Datenbank ausgelesen und so formatiert, dass die Ausgaben pro Kategorie gruppiert
     * aufsummiert werden, um eine Aufteilung der Ausgaben zu veranschaulichen
     *
     * @param userId
     * @return PieData - Vorverarbeitetes Datenset für den PieChart
     */
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

        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

                DecimalFormat decFormat;
                decFormat = new DecimalFormat("###,###,##0.00");
                return decFormat.format(value) + " €";
            }
        });
        data.setValueTextSize(13f);
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
