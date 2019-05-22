package com.example.maanjo.expense_mgmt.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.example.maanjo.expense_mgmt.Database.Data_source;
import com.example.maanjo.expense_mgmt.R;

import lecho.lib.hellocharts.view.PieChartView;

public class GraphViewer extends AppCompatActivity{

    public static final String LOG_TAG = LogIn.class.getSimpleName();
    private Data_source dataSource;
    public String userName;
    public int userId;


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
        pieChartView.setPieChartData(dataSource.detailledPieChart(userId));

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
