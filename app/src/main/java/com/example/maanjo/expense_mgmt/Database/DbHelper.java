package com.example.maanjo.expense_mgmt.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Individualisierte SQLiteOpenHelper-Klasse.
 * Wird verwendet, um Datenbankstruktur (Datenbank & Tabellen) zu erzeugen.
 */
public class DbHelper extends SQLiteOpenHelper{

    /**
     * Deklarieren und Initialisieren der SQL-Elemente in Form von Strings.
     */
    private static final String LOG_TAG = DbHelper.class.getSimpleName();

    public static final String db_name = "expense_db";
    public static final int db_version = 1;

    public static final String table_user = "userTable";
    public static final String table_expenses = "expenses";

    public static final String COLUMN_User_ID = "id";
    public static final String COLUMN_User_Name = "userName";
    public static final String COLUMN_User_Password = "userPassword";

    public static final String COLUMN_spending = "spending";
    public static final String COLUMN_category = "category";
    public static final String COLUMN_expensesDate = "date";


    /**
     * Deklarieren und Initialisieren der SQL-Statements zum Erzeugen der Tabellen.
     */

    public static final String sql_createUser_table = "CREATE TABLE " + table_user +
            "(" + COLUMN_User_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_User_Name + " TEXT NOT NULL, " +
            COLUMN_User_Password + " TEXT NOT NULL);";

    public static final String sql_createExpenses_table = "CREATE TABLE "
            + table_expenses + "("
            + COLUMN_spending + " FLOAT NOT NULL, "
            + COLUMN_category + " TEXT NOT NULL, "
            + COLUMN_expensesDate + " LONG NOT NULL, "
            + COLUMN_User_ID + " INTEGER, "
            + " FOREIGN KEY ("+COLUMN_User_ID+") REFERENCES "+ table_user +"("+COLUMN_User_ID+"));";


    public DbHelper(Context context){

        super(context, db_name, null, db_version);
        Log.d(LOG_TAG, "DbHelper hat die Datenbank: " + getDatabaseName() + "erzeugt.");
    }

    /**
     * Methode wird ausgeführt, wenn die Applikation zum ersten Mal gestartet wird; Tabellen werden erzeugt
     *
     * @param db: Zu befüllende Datenbank
     */
    public void onCreate(SQLiteDatabase db){

        try{
            Log.d(LOG_TAG, "Die Tabelle  table_user wird mit SQL-Befehl: " + sql_createUser_table + " angelegt.");
            db.execSQL(sql_createUser_table);

            Log.d(LOG_TAG, "Die Tabelle  table_expenses wird mit SQL-Befehl: " + sql_createExpenses_table + " angelegt.");
            db.execSQL(sql_createExpenses_table);
        }
        catch (Exception ex) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: " + ex.getMessage());
        }
    }

    /**
     * Methode wird ausgeführt, wenn die Datenbank geupdated wird.
     * Funktion in unserer Applikation nicht eingeführt.
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
