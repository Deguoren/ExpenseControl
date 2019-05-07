package com.example.maanjo.expense_mgmt.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Beinhaltet alle Methoden, die direkt auf die Datenbank zugreifen (Werte eintragen & Auslesen)
 */

public class Data_source {

    private static final String LOG_TAG = Data_source.class.getSimpleName();

    private SQLiteDatabase  database;
    private DbHelper dbHelper;

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
    public void createExpense(int spending, String category, int userId){

        ContentValues expenseEntry = new ContentValues();
        long date = System.currentTimeMillis();

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

    public void createIncome(int income, String category, int userId){

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
     * Festlegen eines Budgets in der Budget-Tabelle
     * @param budget: Individuelles Budget
     * @param date: Monat und Jahr des Budget
     * @param userId: automatisch vergebene UserID
     */
    public void createBudget(int budget, long date, int userId){

        ContentValues budgetEntry = new ContentValues();

        budgetEntry.put(DbHelper.COLUMN_budget, budget);
        budgetEntry.put(DbHelper.COLUMN_budget_date, date);
        budgetEntry.put(DbHelper.COLUMN_User_ID, userId);

        database.insert(DbHelper.table_budget, null, budgetEntry);

        Log.d(LOG_TAG, "Für den Monat " + date + " wurde das Budget " + budget + " angelegt.");

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

        int spending = cursor.getInt(idSpending);
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
                expenseList.add(value);

            } while (cursor.moveToNext());
        }
        Log.d(LOG_TAG, "Ausgabenliste wurde erstellt");
        cursor.close();
        return expenseList;
    }

    /**
     * Ermittelt die UserID, die zu dem übergebenen UserName gehört
     *
     * @param userName: Übergebener Benutzername
     * @return User ID
     */
    public int getUserId(String userName){

        String[] columns = {DbHelper.COLUMN_User_ID};
        String where = DbHelper.COLUMN_User_Name +" LIKE '%"+userName+"%'";
        int userId;

        Cursor cursor = database.query(DbHelper.table_user,
                columns, where, null, null,null, null);

        cursor.moveToFirst();

        if (cursor != null && cursor.moveToFirst()) {

            if (cursor.getCount() > 0) {

                Log.d(LOG_TAG, "UserId: " + cursor.getInt(0));
                userId = cursor.getInt(0);
                return userId;
            }
        }
        return cursor.getInt(0);
    }

}
