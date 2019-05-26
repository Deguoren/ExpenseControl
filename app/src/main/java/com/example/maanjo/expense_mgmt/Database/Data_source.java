package com.example.maanjo.expense_mgmt.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.maanjo.expense_mgmt.Activities.StartingPage;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.Format;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Beinhaltet alle Methoden, die direkt auf die Datenbank zugreifen (Werte eintragen & Auslesen)
 */

public class Data_source {

    private static final String LOG_TAG = Data_source.class.getSimpleName();

    private SQLiteDatabase  database;
    private DbHelper dbHelper;
    private Data_source dataSource;

    /**
     * Konstruktor
     * Initialisiert den DiabetesMemoDbHelper (SQLiteOpenHelper)
     *
     * @param context: Anwendungskontext
     */
    public Data_source(Context context){

        Log.d(LOG_TAG, "Data_source erzeug jetzt den dbHelper.");
        dbHelper = new DbHelper(context);
    }

    /**
     * Öffnet die Verbindung zur Datenbank
     */
    public void open(){

        Log.d(LOG_TAG, "Eine Referenz auf die Datenbank wird jetzt angefragt.");
        database = dbHelper.getWritableDatabase();
        Log.d(LOG_TAG, "Datenbank-Referenz erhalten. Pfad zur Datenabnk: " + database.getPath());
    }

    /**
     * Schließt bestehende Verbindung zur Datenbank
     */
    public void close(){

        dbHelper.close();
        Log.d(LOG_TAG, "Datenbank mit Hilfe des DbHelpers geschlossen.");
    }

    /**
     * Hinzufügen eines User-Eintrags in der User-Tabelle
     * @param name: Benutzername
     * @param password: Passwort des Nutzers
     */
    public void createUser(String name, String password){

        ContentValues userEntry = new ContentValues();

        userEntry.put(DbHelper.COLUMN_User_Name, name);
        userEntry.put(DbHelper.COLUMN_User_Password, password);

        database.insert(DbHelper.table_user, null, userEntry);

        Log.d(LOG_TAG, name + " wurde angelegt.");
    }

    /**
     * Hinzufügen einer Ausgabe in der Expenses-Tabelle
     * @param spending: Ausgabe
     * @param category: Kategorie der Ausgabe
     * @param userId: automatisch vergebene UserID
     */
    public void createExpense(float spending, String category, int userId){

        ContentValues expenseEntry = new ContentValues();
        //long date = System.currentTimeMillis();

        //Anpassen des Datums zu Testzwecken, um Daten in der Zukunft anlegen zu können
        Calendar cal = Calendar.getInstance(); //current date and time
        cal.add(Calendar.DAY_OF_MONTH, 6); //add a day
        cal.set(Calendar.HOUR_OF_DAY, 23); //set hour to last hour
        cal.set(Calendar.MINUTE, 59); //set minutes to last minute
        cal.set(Calendar.SECOND, 59); //set seconds to last second
        cal.set(Calendar.MILLISECOND, 999); //set milliseconds to last millisecond
        long date = cal.getTimeInMillis();


        expenseEntry.put(DbHelper.COLUMN_spending, spending*-1);
        expenseEntry.put(DbHelper.COLUMN_category, category);
        expenseEntry.put(DbHelper.COLUMN_expensesDate, date);
        expenseEntry.put(DbHelper.COLUMN_User_ID, userId);

        database.insert(DbHelper.table_expenses,null, expenseEntry);

        Log.d(LOG_TAG, "Ausgabe in Höhe von " + spending + " der Kategorie " + category
                + " am " + date + " wurde angelegt");
    }

    /**
     * Hinzufügen einer Einnahme in der Expenses-Tabelle
     * @param income: Einnahme
     * @param category: Kategorie der Ausgabe
     * @param userId: automatisch vergebene UserID
     */

    public void createIncome(float income, String category, int userId){

        ContentValues expenseEntry = new ContentValues();
        long date = System.currentTimeMillis();

        expenseEntry.put(DbHelper.COLUMN_spending, income);
        expenseEntry.put(DbHelper.COLUMN_category, category);
        expenseEntry.put(DbHelper.COLUMN_expensesDate, date);
        expenseEntry.put(DbHelper.COLUMN_User_ID, userId);

        database.insert(DbHelper.table_expenses,null, expenseEntry);

        Log.d(LOG_TAG, "Einnahme in Höhe von " + income + " der Kategorie " + category
                + " am " + date + " wurde angelegt");

    }

    /**
     * Überprüfen, ob Nutzername in der Datenbank vorhanden ist
     *
     * @param name: Eingegebener Nutzername
     * @return True oder False, abhängig davon, ob der User bereits vorhanden ist
     */
    public boolean checkUserName(String name){

        String[] columns = {DbHelper.COLUMN_User_Name};
        String where = DbHelper.COLUMN_User_Name + " = ?";
        String[] whereArgs = {name};

        Cursor cursor = database.query(DbHelper.table_user,
                columns, where, whereArgs, null,null, null);

        int cursorCount = cursor.getCount();
        cursor.close();

        if(cursorCount > 0){
            return true;
        }

        return false;
    }

    /**
     * Überprüfen, ob das Password in der Datenbank vorhanden ist
     *
     * @param password: Eingegebenes Passwort
     * @return True oder False, abhängig davon, ob der User bereits vorhanden ist
     */
    public boolean checkPassword(String password){

        String[] columns = {DbHelper.COLUMN_User_Password};
        String where = DbHelper.COLUMN_User_Password + " = ?";
        String[] whereArgs = {password};

        Cursor cursor = database.query(DbHelper.table_user,
                columns, where, whereArgs, null,null, null);

        int cursorCount = cursor.getCount();
        cursor.close();

        if(cursorCount > 0){
            return true;
        }

        return false;
    }

    /**
     * Eintrag der Tabelle Expenses an der Stelle des Cursors wird ermittelt
     *
     * @param cursor: Auswahl der SQL-Query
     * @return Ausgabe; enthält die Informationen Ausgabe, Kategorie, Datum und UserID
     */
    private ExpenseReader cursorToExpenseReader(Cursor cursor){

        int idSpending = cursor.getColumnIndex(DbHelper.COLUMN_spending);
        int idCategory = cursor.getColumnIndex(DbHelper.COLUMN_category);
        int idDate = cursor.getColumnIndex(DbHelper.COLUMN_expensesDate);
        int idUserId = cursor.getColumnIndex(DbHelper.COLUMN_User_ID);

        float spending = cursor.getFloat(idSpending);
        String category = cursor.getString(idCategory);
        long date = cursor.getLong(idDate);
        int userId = cursor.getInt(idUserId);


        ExpenseReader expense = new ExpenseReader(spending, date, category, userId);

        return expense;
    }

    /**
     * Ausgabe aller Einträge der Tabelle Expenses mit der übergebenen UserID
     *
     * @param userId: Übergebene UserID
     * @return Alle Tupel der Tabelle Expenses in Form einer ArrayList
     */
    public ArrayList<ExpenseReader> getAllExpenses(int userId) {

        database = dbHelper.getWritableDatabase();
        ArrayList<ExpenseReader> expenseList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        String[] columns = {DbHelper.COLUMN_User_ID, DbHelper.COLUMN_expensesDate,
                DbHelper.COLUMN_spending, DbHelper.COLUMN_category};

        String where = DbHelper.COLUMN_User_ID + " = " + userId;

        Cursor cursor = database.query(DbHelper.table_expenses,
                columns, where, null, null, null, null);

        cursor.moveToFirst();
        ExpenseReader value;

        if (cursor != null && cursor.moveToFirst()) {

            do {

                value = cursorToExpenseReader(cursor);
                cal.setTimeInMillis(value.getDate());

                if(cal.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)){

                    expenseList.add(value);
                }

            } while (cursor.moveToNext());
        }
        Log.d(LOG_TAG, "Ausgabenliste wurde erstellt");
        cursor.close();
        return expenseList;
    }

    public float getAccountBalance(String userName){

        database = dbHelper.getWritableDatabase();
        float sum = 0;
        String[] columns = {DbHelper.COLUMN_spending, DbHelper.COLUMN_expensesDate};
        Calendar cal = Calendar.getInstance();

        String where = DbHelper.COLUMN_User_ID + " = " + getUserId(userName);
        Cursor c = database.query(DbHelper.table_expenses,
                columns, where, null, null, null, null);

        c.moveToFirst();

        if(c.moveToFirst()) {

            do {
                cal.setTimeInMillis(c.getLong(c.getColumnIndex("date")));

                if(cal.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {

                    sum += c.getFloat(c.getColumnIndex("spending"));
                }

            } while (c.moveToNext());
        }
        c.close();

        Log.d(LOG_TAG, "Der Kontostand beträgt: " + sum);
        return sum;
    }


    /**
     * Ermittelt die UserID, die zu dem übergebenen UserName gehört
     *
     * @param userName: Übergebener Benutzername
     * @return User ID
     */
    public int getUserId(String userName){

        database = dbHelper.getWritableDatabase();
        String[] columns = {DbHelper.COLUMN_User_ID, DbHelper.COLUMN_User_Name};
        String where = DbHelper.COLUMN_User_Name +" = ?";
        String[] whereArgs = {userName};
        int userId;

        Cursor cursor = database.query(DbHelper.table_user,
                columns, where, whereArgs, null,null, null);

        cursor.moveToFirst();


        if (cursor != null && cursor.moveToFirst()) {

            if (cursor.getCount() > 0) {

                Log.d(LOG_TAG, "UserId: " + cursor.getInt(cursor.getColumnIndex("id")));
                userId = cursor.getInt(cursor.getColumnIndex("id"));
                cursor.close();
                return userId;
            }
        }
        cursor.close();
        return cursor.getInt(0);
    }

    public PieData startingPieChart(String userName){

        ArrayList<ExpenseReader> rawData = getAllExpenses(getUserId(userName));
        ExpenseReader eR;
        float x = 0f;

        float einnahme = 0f;
        float ausgabe = 0f;

        for(int i = 0; i < rawData.size(); i++){

            eR = rawData.get(i);
            x = eR.getExpense();

            if(x <= 0){

                ausgabe += x;
            }
            else if(x > 0){

                einnahme += x;
            }
        }

        ArrayList<Entry> yValue = new ArrayList<Entry>();
        yValue.add(new Entry(ausgabe*(-1), 0));
        yValue.add(new Entry(einnahme,1));

        ArrayList<String> xValue = new ArrayList<String>();
        xValue.add("Ausgabe");
        xValue.add("Einnahme");

        PieDataSet set = new PieDataSet(yValue, "");
        set.setColors(new int[]{ Color.rgb(254,142,156), Color.rgb(198,255,140)});


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

        return data;
    }

}
