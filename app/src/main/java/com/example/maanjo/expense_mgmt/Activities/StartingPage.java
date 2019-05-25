package com.example.maanjo.expense_mgmt.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maanjo.expense_mgmt.Database.Data_source;
import com.example.maanjo.expense_mgmt.Database.TableHelper;
import com.example.maanjo.expense_mgmt.R;

import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * StartingPage verwaltet die Funktionalitäten der Oberfläche activity_starting_page
 */
public class StartingPage extends AppCompatActivity{

    public static final String LOG_TAG = LogIn.class.getSimpleName();
    private TextView mTextMessage;
    private TextView balance_text;
    private TextView expenses;
    private Data_source dataSource;
    public String userName;
    public Button button_plus;
    public Button button_minus;
    public int userId;
    private String m_Text = "";
    public de.codecrafters.tableview.TableView<String[]> tv;
    public TableHelper tableHelper;
    PieChartView pieChartView;

    /**
     * OnCreate-Methode der Klasse Registrate
     * Referenziert die Klasse zum Layout und öffnet die Datenbankverbindung
     * Übernimmt die Parameter aus der vorherigen Activity
     * Setzt den Begrüßungstext
     *
     * @param savedInstanceState: Gespeicherter Zustand der Activity
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);

        dataSource = new Data_source(this);
        Log.d(LOG_TAG, "Das Datenquellen-Objekt wird angelegt.");

        userName = getIntent().getStringExtra("userString");
        userId = dataSource.getUserId(userName);

        mTextMessage = findViewById(R.id.greetings);
        balance_text = (TextView) findViewById(R.id.balance_text);
        pieChartView = (PieChartView) findViewById(R.id.chart);
        button_plus = (Button) findViewById(R.id.button_plus);
        button_minus = (Button) findViewById(R.id.button_minus);

        mTextMessage.setText(new StringBuilder().append("Hey, ").append(userName).toString());

        balance_text.setText(new StringBuilder().append("Dein Kontostand: " + dataSource.getAccountBalance(userName)));
        pieChartView.setPieChartData(dataSource.startingPieChart(userName));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tableHelper = new TableHelper(this);
        tv = findViewById(R.id.tableView2);
        tv.setColumnCount(3);
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(this, 3, 130);
        columnModel.setColumnWidth(1, 80);
        columnModel.setColumnWidth(2, 170);
        tv.setColumnModel(columnModel);
        tv.setHeaderAdapter(new SimpleTableHeaderAdapter(this,tableHelper.getTableHeader()));
        tv.setDataAdapter(new SimpleTableDataAdapter(this, tableHelper.getExpensePreview(userId)));

        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateIncomeButton();
            }
        });

        button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateExpenseButton();
            }
        });


    }

    /**
     * Öffnet Verbindung zur Datenbank, wenn Activity erneut geöffnet wird
     */
    protected void onResume() {

        super.onResume();
        dataSource.open();
        mTextMessage = findViewById(R.id.greetings);
        userName = String.valueOf(getIntent().getStringExtra("userString"));
        userId = dataSource.getUserId(userName);
        mTextMessage.setText(new StringBuilder().append("Hey, ").append(userName).toString());
        balance_text.setText(new StringBuilder().append("Dein Kontostand: " + dataSource.getAccountBalance(userName)));
        tv.setDataAdapter(new SimpleTableDataAdapter(this, tableHelper.getExpensePreview(userId)));
        pieChartView.setPieChartData(dataSource.startingPieChart(userName));

        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
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
     * Funktionalitäten, die ausgeführt werden, wenn der Minus-Button gedrückt wird --> Ausgabe wird angelegt
     * Hinzufügen eines OnClick-Listeners
     * Erzeugen eines Eintrages in der Tabelle Expenses durch aufrufen der Methode createBloodValue()
     */
    private void activateExpenseButton(){

       LayoutInflater layoutInflater = LayoutInflater.from(StartingPage.this);
       View promptView = layoutInflater.inflate(R.layout.expense_dialog, null);
       AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartingPage.this);

       final EditText exp_inc_value = (EditText) promptView.findViewById(R.id.exp_inc_value);
       final Spinner category_spinner = (Spinner) promptView.findViewById(R.id.cat_spinner);

       alertDialogBuilder.setView(promptView);
       alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

           @Override
           public void onClick(DialogInterface dialog, int id) {

               userId = dataSource.getUserId(getIntent().getStringExtra("userString"));
               float exp = Float.parseFloat(exp_inc_value.getText().toString());
               String category = category_spinner.getSelectedItem().toString();

               dataSource.createExpense(exp, category, userId);
               recreate();
               overridePendingTransition(0, 0);
           }
       }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

               dialog.cancel();
           }
       });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * Funktionalitäten, die ausgeführt werden, wenn der Plus-Button gedrückt wird --> Einnahme wird angelegt
     * Hinzufügen eines OnClick-Listeners
     * Erzeugen eines Eintrages in der Tabelle Expenses durch aufrufen der Methode createIncome()
     */
    private void activateIncomeButton(){

        LayoutInflater layoutInflater = LayoutInflater.from(StartingPage.this);
        View promptView = layoutInflater.inflate(R.layout.income_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartingPage.this);

        final EditText income_value = (EditText) promptView.findViewById(R.id.income_value);

        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {

                userId = dataSource.getUserId(getIntent().getStringExtra("userString"));
                float inc = Float.parseFloat(income_value.getText().toString());
                String category = "Einnahme";

                dataSource.createIncome(inc, category, userId);
                recreate();
                overridePendingTransition(0, 0);

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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

            userId = dataSource.getUserId(getIntent().getStringExtra("userString"));
            Bundle bundle = new Bundle();
            bundle.putInt("userId", userId);
            bundle.putString("userName", userName);
            Intent table_intent = new Intent();

            switch (item.getItemId()) {
                case R.id.navigation_start:

                    recreate();
                    overridePendingTransition(0, 0);
                    return true;

                case R.id.navigation_table:

                    table_intent.setClassName("com.example.maanjo.expense_mgmt", "com.example.maanjo.expense_mgmt.Activities.TableViewer");
                    table_intent.putExtras(bundle);
                    startActivity(table_intent);
                    return true;

                case R.id.navigation_graph:

                    table_intent.setClassName("com.example.maanjo.expense_mgmt", "com.example.maanjo.expense_mgmt.Activities.GraphViewer");
                    table_intent.putExtras(bundle);
                    startActivity(table_intent);
                    return true;
            }
            return false;
        }
    };

}