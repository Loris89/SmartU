package it.gristeliti.smartu.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import it.gristeliti.smartu.R;
import it.gristeliti.smartu.utils.Validation;

public class SignupActivity extends AppCompatActivity {

    private Toolbar toolbar;

    // shared preferences keys
    private static final String MY_PREFERENCES = "MyPref";
    private static final String NICKNAME_KEY = "NicknameKey";
    private static final String PASSWORD_KEY = "PasswordKey";
    private static final String EMAIL_KEY = "EmailKey";
    private static final String NAME_KEY = "NameKey";
    private static final String SURNAME_KEY = "SurnameKey";
    private static final String MATRICULA_KEY = "MatriculaKey";

    private Button signupButton;
    private EditText nickname;
    private EditText password;
    private EditText email;
    private EditText name;
    private EditText surname;
    private EditText matricula;

    private String nicknametxt;
    private String passwordtxt;
    private String emailtxt;
    private String nametxt;
    private String surnametxt;
    private String matriculatxt; // prima era int

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get views
        signupButton = (Button) this.findViewById(R.id.signup_button);
        nickname = (EditText) this.findViewById(R.id.nickname_edittext);
        email = (EditText) this.findViewById(R.id.email_edittext);
        name = (EditText) this.findViewById(R.id.name_edittext);
        surname = (EditText) this.findViewById(R.id.surname_edittext);
        matricula = (EditText) this.findViewById(R.id.matricula_edittext);
        password = (EditText) this.findViewById(R.id.password_edittext);

        // SHARED PREFERENCES

        // get the shared preferences
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);
        // check if there are data in the SharedPreferences
        if (sharedPreferences.contains(NICKNAME_KEY)) {
            nickname.setText(sharedPreferences.getString(NICKNAME_KEY, ""));
        }
        if (sharedPreferences.contains(PASSWORD_KEY)) {
            password.setText(sharedPreferences.getString(PASSWORD_KEY, ""));
        }
        if (sharedPreferences.contains(EMAIL_KEY)) {
            email.setText(sharedPreferences.getString(EMAIL_KEY, ""));
        }
        if (sharedPreferences.contains(NAME_KEY)) {
            name.setText(sharedPreferences.getString(NAME_KEY, ""));
        }
        if (sharedPreferences.contains(SURNAME_KEY)) {
            surname.setText(sharedPreferences.getString(SURNAME_KEY, ""));
        }
        if (sharedPreferences.contains(MATRICULA_KEY)) {
            matricula.setText(sharedPreferences.getString(MATRICULA_KEY, ""));
        }

        // REGISTRATION
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check if the user has filled the form
                if (!checkValidation()) {
                    Toast.makeText(getBaseContext(), "Please fill the forms", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isProfessor(email.getText().toString()) && !isStudent(email.getText().toString())) {
                    Toast.makeText(getBaseContext(), "Please fill the form with the istitutional email account", Toast.LENGTH_LONG).show();
                    email.setError("Wrong email format");
                    return;
                }

                nicknametxt = nickname.getText().toString();
                passwordtxt = password.getText().toString();
                emailtxt = email.getText().toString();
                nametxt = name.getText().toString();
                surnametxt = surname.getText().toString();
                matriculatxt = matricula.getText().toString();

                // update shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(NICKNAME_KEY, nicknametxt);
                editor.putString(PASSWORD_KEY, passwordtxt);
                editor.putString(EMAIL_KEY, emailtxt);
                editor.putString(NAME_KEY, nametxt);
                editor.putString(SURNAME_KEY, surnametxt);
                editor.putString(MATRICULA_KEY, matriculatxt);
                editor.commit();

                ParseUser user = new ParseUser();

                user.setUsername(emailtxt);
                user.setPassword(passwordtxt);
                user.setEmail(emailtxt);
                user.put("name", nametxt);
                user.put("surname", surnametxt);
                user.put("matricula", Integer.valueOf(matriculatxt)); // parse vuole un intero per la matricola, ma gli sto passando una stringa
                user.put("nickname", nicknametxt);

                if (isProfessor(emailtxt)) {
                    user.put("isProfessor", true);
                }

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Show a simple Toast message upon successful registration
                            Toast.makeText(getApplicationContext(),
                                    "Successfully Signed up, please log in.",
                                    Toast.LENGTH_LONG).show();
                            // torna alla schemata di login
                            Intent intent = new Intent(SignupActivity.this, LoginRegActivity.class);
                            intent.putExtra("EMAIL", emailtxt);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    e.toString(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.hasText(nickname) || !Validation.hasText(password) ||
                !Validation.hasText(email) || !Validation.hasText(name) || !Validation.hasText(surname) ||
                !Validation.hasText(matricula)) {
            ret = false;
        }
        return ret;

    }

    private boolean isProfessor(String email) {
        int indexofat = email.indexOf("@");
        String temp = email.substring(indexofat + 1);
        if (temp.equals("dis.uniroma1.it")) return true;
        else return false;
    }

    private boolean isStudent(String email) {
        int indexofat = email.indexOf("@");
        String temp = email.substring(indexofat + 1);
        if (temp.equals("studenti.uniroma1.it")) return true;
        else return false;
    }
}