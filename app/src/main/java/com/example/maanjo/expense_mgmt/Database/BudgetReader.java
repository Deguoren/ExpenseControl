package com.example.maanjo.expense_mgmt.Database;

/**
 * Objektklasse zum Zwischenspeichern der ausgelesenen Datenbanktupel der Tabelle Budget.
 */
public class BudgetReader {

    private int budget;
    private long date;
    private int user_id;

    /**
     * Konstruktor
     * Initialisieren der Instanzvariablen mithilfe der Übergabeparameter
     *
     * @param budget: Monatliches Budget des Nutzers
     * @param date: Zum Budget zugehöriger Monat und Jahr
     * @param user_id: UserId
     */
    public BudgetReader(int budget, long date, int user_id){

        this.budget = budget;
        this.date = date;
        this.user_id = user_id;
    }

    /**
     * Getter- & Setter-Methoden der Instanzvariablen, um auf diese zuzugreifen.
     */
    public int getBudget(){

        return this.budget;
    }

    public void setBudget(int budget){

        this.budget = budget;
    }

    public long getDate(){

        return this.date;
    }

    public void setDate(long date){

        this.date = date;
    }

    public int getUser_id(){

        return this.user_id;
    }

    public void setUser_id(int user_id){

        this.user_id = user_id;
    }

    /**
     * ToString-Methode zum Erstellen eines String, der die Werte eines Tupels der Tabelle Budget enthält
     *
     * @return String mit Informationen des Tupels
     */
    public String toString() {

        String output = budget + " x " + date + " x " + user_id;
        return output;
    }


}