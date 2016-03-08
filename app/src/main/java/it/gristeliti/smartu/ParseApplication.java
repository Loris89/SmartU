package it.gristeliti.smartu;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "WYN5XzaG0B7TdkyUGVKuW1kKOZICxdLdoK4Xw8G9",
                "7zuvkZey7TxierCjPY47BPJKDoGVrC5rrHAyvpo4");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParseUser.enableRevocableSessionInBackground();

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }
}
