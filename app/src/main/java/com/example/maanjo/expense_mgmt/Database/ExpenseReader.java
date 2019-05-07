package com.example.maanjo.expense_mgmt.Database;

public class ExpenseReader {

    /**
     * Objektklasse zum Zwischenspeichern der ausgelesenen Datenbanktupel der Tabelle Expenses.
     */

    private int expense;
    private long date;
    private String category;
    private int user_id;

    /**
     * Konstruktor
     * Initialisieren der Instanzvariablen mithilfe der Übergabeparameter
     *
     * @param expense: Ausgabe des Nutzers
     * @param date: Aktuelle Uhrzeit und Datum zum Eingabezeitpunkt
     * @param category: Zur Ausgabe hinzugefügte Kategorie
     * @param user_id: UserId
     */

    public ExpenseReader(int expense, long date, String category, int user_id){

        this.expense = expense;
        this.date = date;
        this.category = category;
        this.user_id = user_id;
    }

    /**
     * Getter- & Setter-Methoden der Instanzvariablen, um auf diese zuzugreifen.
     */

    public int getExpense() {
        return expense;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category= category;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    /**
     * ToString-Methode zum Erstellen eines String, der die Werte eines Tupels der Tabelle Metrics enthält
     *
     * @return String mit Informationen des Tupels
     */
    public String toString() {

        String output = expense + " x " + category+ " x " + date + " x " + user_id;

        return output;
    }
}


