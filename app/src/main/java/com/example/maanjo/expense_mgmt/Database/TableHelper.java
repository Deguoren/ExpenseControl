package com.example.maanjo.expense_mgmt.Database;


import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Hilfsklasse zur Erstellung einer Tabelle für die Activity TableViewer
 */
public class TableHelper {

    Context c;

    private String[] tableHeader = {"Zeitpunkt", "Betrag", "Kategorie"};
    private String[][] expenseArr;

    /**
     * Konstruktor
     * Initialisiert den Kontext
     *
     * @param c: Anwendungskontext
     */
    public TableHelper(Context c) {

        this.c = c;
    }

    /**
     * Getter-Methode für die Variable tableHeader (Spaltenüberschriften)
     *
     * @return Spaltenüberschriften
     */
    public String[] getTableHeader() {

        return tableHeader;
    }

    /**
     * Auslesen und Zwischenspeichern aller Einträge der Tabelle Metrics für die übergebene UserID
     *
     * @param userId: NutzerID
     * @return 2-Dimensionales Array mit allen Tupeln der Tabelle Metrics
     */
    public String[][] getExpenseValue(int userId) {

        ArrayList<ExpenseReader> expenseValues = new Data_source(c).getAllExpenses(userId);
        ExpenseReader b;
        expenseArr = new String[expenseValues.size()][3];

        for (int i = 0; i < expenseValues.size(); i++) {

            b = expenseValues.get(i);

            Long date_milSec = b.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
            Date resultdate = new Date(date_milSec);

            expenseArr[i][0] = sdf.format(resultdate);
            expenseArr[i][1] = Integer.toString(b.getExpense());
            expenseArr[i][2] = b.getCategory();

        }
        return expenseArr;
    }

    public String[][] getExpensePreview(int userId) {

        ArrayList<ExpenseReader> expenseValues = new Data_source(c).getAllExpenses(userId);
        ExpenseReader b;
        expenseArr = new String[3][3];

        for (int i = 0; i <= 3; i++) {

            b = expenseValues.get(3-i);

            Long date_milSec = b.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
            Date resultdate = new Date(date_milSec);

            expenseArr[i][0] = sdf.format(resultdate);
            expenseArr[i][1] = Integer.toString(b.getExpense());
            expenseArr[i][2] = b.getCategory();

            if(i == 2){
                break;
            }

        }
        return expenseArr;
    }
}
