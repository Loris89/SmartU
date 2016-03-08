package it.gristeliti.smartu.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import it.gristeliti.smartu.R;

/**
 * Da questa Activity è possibile  selezionare i corsi che l'utente vuole
 * seguire. Visualizza i corsi attualmente seguiti. Cliccando sui corsi
 * seguiti è possibile rimuoverli.
 *
 * Usa list view per visualizzare i corsi seguiti. Ascolta il click degli
 * item per gestire la loro rimozione. Sfrutta il concetto di channel di Parse
 * per segnarsi/togliersi da un corso.
 */
public class Courses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_subscription);
    }
}
