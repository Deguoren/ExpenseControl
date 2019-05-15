package com.example.maanjo.expense_mgmt.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.maanjo.expense_mgmt.Database.Data_source;
import com.example.maanjo.expense_mgmt.Database.Data_source;
import com.example.maanjo.expense_mgmt.Database.TableHelper;
import com.example.maanjo.expense_mgmt.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * StartingPage verwaltet die Funktionalitäten der Oberfläche activity_starting_page
 */
public class StartingPage extends AppCompatActivity{

    public static final String LOG_TAG = LogIn.class.getSimpleName();
    private TextView mTextMessage;
    private Data_source dataSource;
    public String userName;
    public Button button_plus;
    public Button button_minus;
    public int userId;
    private String m_Text = "";
    public de.codecrafters.tableview.TableView<String[]> tv;
    public TableHelper tableHelper;

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

        mTextMessage = findViewById(R.id.greetings);
        userName = String.valueOf(getIntent().getStringExtra("userString"));
        mTextMessage.setText(new StringBuilder().append("Hey, ").append(userName).toString());
        userName = getIntent().getStringExtra("userString");
        button_plus = (Button) findViewById(R.id.button_plus);
        button_minus = (Button) findViewById(R.id.button_minus);

        tableHelper = new TableHelper(this);
        tv = findViewById(R.id.tableView2);
        tv.setColumnCount(3);
        TableColumnDpWidthModel columnModel = new TableColumnDpWidthModel(this, 3, 130);
        columnModel.setColumnWidth(1, 80);
        columnModel.setColumnWidth(2, 170);
        tv.setColumnModel(columnModel);
        tv.setHeaderAdapter(new SimpleTableHeaderAdapter(this,tableHelper.getTableHeader()));
        tv.setDataAdapter(new SimpleTableDataAdapter(this, tableHelper.getExpensePreview(1)));

        BottomNavigationView navigation = findViewById(R.id.navigation3);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        monthlyBudget();

        button_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateEnterButton("+");
            }
        });

        button_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateEnterButton("-");
            }
        });

    }

    /**
     * Öffnet Verbindung zur Datenbank, wenn Activity erneut geöffnet wird
     */
    protected void onResume() {

        super.onResume();
        mTextMessage = findViewById(R.id.greetings);
        userName = String.valueOf(getIntent().getStringExtra("userString"));
        mTextMessage.setText(new StringBuilder().append("Hey, ").append(userName).toString());
        Log.d(LOG_TAG, "Die Datenquelle wird geöffnet.");
        dataSource.open();
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
     * Funktionalitäten, die ausgeführt werden, wenn der Enter-Button gedrückt wird
     * Hinzufügen eines OnClick-Listeners
     * Erzeugen eines Eintrages in der Tabelle Metric durch aufrufen der Methode createBloodValue()
     */
    private void activateEnterButton(final String operation){

       LayoutInflater layoutInflater = LayoutInflater.from(StartingPage.this);
       View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
       AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartingPage.this);

       final EditText exp_inc_value = (EditText) promptView.findViewById(R.id.exp_inc_value);
       final Spinner category_spinner = (Spinner) promptView.findViewById(R.id.cat_spinner);

       alertDialogBuilder.setView(promptView);
       alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

           @Override
           public void onClick(DialogInterface dialog, int id) {

               userId = dataSource.getUserId(getIntent().getStringExtra("userString"));
               int exp_inc = Integer.parseInt(exp_inc_value.getText().toString());
               String category = category_spinner.getSelectedItem().toString();

               if(operation == "+"){

                   dataSource.createIncome(exp_inc, category, userId);
               }
               else if(operation == "-"){

                   dataSource.createExpense(exp_inc, category, userId);

               }

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

    public void monthlyBudget(){

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String datum = dateFormat.format(date);

        if(Pattern.matches("^(\\d{4})[/](\\d{2})[/](01)$", datum)){

            LayoutInflater layoutInflater = LayoutInflater.from(StartingPage.this);
            View promptView = layoutInflater.inflate(R.layout.budget_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartingPage.this);

            final EditText budget_value = (EditText) promptView.findViewById(R.id.budget_value);

            alertDialogBuilder.setView(promptView);
            alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int id) {

                    userId = dataSource.getUserId(getIntent().getStringExtra("userString"));
                    int budget = Integer.parseInt(budget_value.getText().toString());
                    dataSource.createBudget(budget, userId);

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
            bundle.putString("userName", String.valueOf(userName));
            Intent table_intent = new Intent();

            switch (item.getItemId()) {
                case R.id.navigation_start:

                    table_intent.setClassName("com.example.maanjo.expense_mgmt", "com.example.maanjo.expense_mgmt.Activities.StartingPage");
                    table_intent.putExtras(bundle);
                    startActivity(table_intent);
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