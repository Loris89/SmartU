package it.gristeliti.smartu.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import it.gristeliti.smartu.R;
import it.gristeliti.smartu.utils.Validation;

public class LoginRegActivity extends AppCompatActivity {

    // shared preferences keys
    private static final String MY_PREFERENCES = "MyPref";
    private static final String EMAIL_KEY = "EmailKey";
    private static final String PASSWORD_KEY = "PasswordKey";

    // views elements
    private Button signupButton;
    private Button loginButton;
    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;

    private String emailTxt;
    private String passwordTxt;

    // shared preferences
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_reg_activity);

        emailEditText = (EditText)findViewById(R.id.emailLoginReg);
        passwordEditText = (EditText)findViewById(R.id.passwordLoginReg);
        loginButton = (Button)findViewById(R.id.login);
        signupButton = (Button)findViewById(R.id.signup);

        progressBar = (ProgressBar)findViewById(R.id.loginreg_progressbar);

        // SHARED PREFERENCES

        // get the shared preferences
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);

        // check if there are data in the SharedPreferences
        if(sharedPreferences.contains(EMAIL_KEY)) {
            emailEditText.setText(sharedPreferences.getString(EMAIL_KEY, ""));
        }
        if(sharedPreferences.contains(PASSWORD_KEY)) {
            passwordEditText.setText(sharedPreferences.getString(PASSWORD_KEY, ""));
        }

        // LOGIN BUTTON
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                // check if the user has filled the form
                if(!checkValidation()) {
                    Toast.makeText(getBaseContext(), "Please fill the forms", Toast.LENGTH_LONG).show();
                    return;
                }

                // retrieve the text entered from the EditText
                emailTxt = emailEditText.getText().toString();
                passwordTxt = passwordEditText.getText().toString();

                // update shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(EMAIL_KEY, emailTxt);
                editor.putString(PASSWORD_KEY, passwordTxt);
                editor.commit();

                // send data to Parse.com for verification
                ParseUser.logInInBackground(emailTxt, passwordTxt, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(LoginRegActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "No such user exist, please signup",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        // REGISTRATION BUTTON
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(LoginRegActivity.this, SignupActivity.class);
                startActivity(signupIntent);
            }
        });
    }

    private boolean checkValidation() {
        boolean ret = true;
        if(!Validation.hasText(emailEditText) || !Validation.hasText(passwordEditText)) {
            ret = false;
        }
        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();

        Intent intentEmail = getIntent();
        emailTxt = intentEmail.getStringExtra("EMAIL");
        if(emailTxt != null) {
            passwordEditText.getText().clear();
            emailEditText.setText(emailTxt);
        }

        // check if the user is already logged in Parse
        /*ParseUser user = ParseUser.getCurrentUser();
        if (user.getSessionToken() != null) {
            Intent intent = new Intent(LoginRegActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
