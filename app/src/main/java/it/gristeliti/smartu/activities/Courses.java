package it.gristeliti.smartu.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    public static final String COURSE_KEY = "COURSE";

    private Button datamanagementButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        datamanagementButton = (Button)findViewById(R.id.board_button);

        datamanagementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Courses.this, Board.class);
                intent.putExtra(COURSE_KEY, "Data Management");
                startActivity(intent);
            }
        });
    }
}
