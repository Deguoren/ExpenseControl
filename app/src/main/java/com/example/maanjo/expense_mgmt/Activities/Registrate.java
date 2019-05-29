package com.example.maanjo.expense_mgmt.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.maanjo.expense_mgmt.Database.DataSource;
import com.example.maanjo.expense_mgmt.R;

/**
 * Registrate verwaltet die Funktionalitäten der Oberfläche activity_registrate
 */
public class Registrate extends AppCompatActivity{

    private DataSource dataSource;
    public EditText editTextName;
    public EditText editTextPassword;
    public EditText editTextPassword2;

    /**
     * OnCreate-Methode der Klasse Registrate
     * Referenziert die Klasse zum Layout und öffnet die Datenbankverbindung
     *
     * @param savedInstanceState: Gespeicherter Zustand der Activity
     */
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrate);

        dataSource = new DataSource(this);
        dataSource.open();
        activateRegButton();

    }

    /**
     * Funktionalitäten, die ausgeführt werden, wenn der Registrieren-Button gedrückt wird
     * Hinzufügen eines OnClick-Listeners
     * Überprüfung, ob die Eingaben vollständig sind
     * Überprüfung, ob Passwort und Passwort Confirmation gleich sind
     * Bei richtiger Eingabe: Anlage eines neuen Nutzers und Wechsel zu LogIn-Activity
     */
    public void activateRegButton(){

        Button buttonReg = findViewById(R.id.button_letsgo);
        editTextName = findViewById(R.id.editText_nameReg);
        editTextPassword = findViewById(R.id.editText_passwordReg);
        editTextPassword2 = findViewById(R.id.editText_passwordReg2);

        buttonReg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v){

                String userString = editTextName.getText().toString();
                String passwordString = editTextPassword.getText().toString();
                String passwordConfirmString = editTextPassword2.getText().toString();

                if(TextUtils.isEmpty(userString)){

                    editTextName.setError(getString(R.string.errorMessage));
                    return;
                }

                if(TextUtils.isEmpty(passwordString)){

                    editTextName.setError(getString(R.string.errorMessage));
                    return;
                }

                if(TextUtils.isEmpty(passwordConfirmString)){

                    editTextName.setError(getString(R.string.errorMessage));
                    return;
                }

                if(passwordString.equals(passwordConfirmString)) {

                    dataSource.createUser(userString, passwordString);
                    startActivity(new Intent(Registrate.this, LogIn.class));
                    return;

                }else{

                    editTextPassword2.setError(getString(R.string.errorMessageSecPw));
                    return;
                }
            }
        });
    }
}
