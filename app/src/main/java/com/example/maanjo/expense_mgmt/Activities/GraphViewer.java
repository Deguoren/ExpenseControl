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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
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
        userId = bundle.getInt("userId", 0);
        userName = bundle.getString("userName");
        //userName = getIntent().getStringExtra("userString");

        PieChartView pieChartView = (PieChartView) findViewById(R.id.detailledPieChart);
        pieChartView.setPieChartData(detailledPieChart(userId));

        LineChartView lineChartView = findViewById(R.id.lineChartView);
        lineChartView.setLineChartData(detailledLineChart(userId));
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top =110;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);

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

    public LineChartData detailledLineChart(int userId){

        String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
                "Oct", "Nov", "Dec"};
        int[] yAxisData = {50, 20, 15, 30, 20, 60, 15, 40, 45, 10, 90, 18};

        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        for(int i = 0; i < axisData.length; i++){
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < yAxisData.length; i++){
            yAxisValues.add(new PointValue(i, yAxisData[i]));
        }

        List lines = new ArrayList();
        lines.add(line);


        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axis = new Axis();
        axis.setValues(axisValues).setHasTiltedLabels(true);
        data.setAxisXBottom(axis);
        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));

        Axis yAxis = new Axis();
        data.setAxisYLeft(yAxis);
        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);
        yAxis.setName("Sales in millions");



        return data;

       /* ArrayList<ExpenseReader> expenseValues = new Data_source(this).getAllExpenses(userId);
        ExpenseReader b;

        for (int i = 0; i < expenseValues.size(); i++) {

            b = expenseValues.get(i);

            Long date_milSec = b.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
            Date resultdate = new Date(date_milSec);

            xAxis.add(sdf.format(resultdate));
            yAxis.add(b.getExpense());

        }

        Line line = new Line(yAxis);

        for(int i = 0; i < yAxis.size(); i++){
            xAxis.add(i, new AxisValue(i).setLabel((xAxis[i])));
        }

        for(int i = 0; i < xAxis.size(); i++){

            yAxis.add(i, new AxisValue(i).setLabel(Float.toString(yAxis[i])));
        }*/


    }

    public PieChartData detailledPieChart(int userId){

        List<SliceValue> data = new ArrayList<>();
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
                case("Haushaltskosten"):
                    haushaltskosten += x;
                case("Shopping"):
                    shopping += x;
                case("Unternehmungen"):
                    unternehmungen += x;
                case("Einnahme"):
                    einnahme += x;
                case("Sonstiges"):
                    sonstiges += x;
            }
        }

        data.add(new SliceValue(lebensmittel*(-1), Color.BLUE).setLabel("Lebensmittel" + "\n" + lebensmittel + "€"));
        data.add(new SliceValue(haushaltskosten*(-1), Color.YELLOW).setLabel("Haushaltskosten"+ "\n" + haushaltskosten + "€"));
        data.add(new SliceValue(shopping*(-1), Color.RED).setLabel("Shopping"+ "\n" + shopping + "€"));
        data.add(new SliceValue(unternehmungen*(-1), Color.BLACK).setLabel("Unternehmungen"+ "\n" + unternehmungen + "€"));
        data.add(new SliceValue(sonstiges*(-1), Color.GRAY).setLabel("Sonstiges"+ "\n" + sonstiges + "€"));
        data.add(new SliceValue(einnahme, Color.GREEN).setLabel("Einnahme"+ "\n" + einnahme + "€"));

        PieChartData pie = new PieChartData(data);
        pie.setHasLabels(true);
        pie.setHasCenterCircle(true);

        return pie;
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
