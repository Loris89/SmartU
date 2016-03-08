package it.gristeliti.smartu.utils;

import android.widget.EditText;

public class Validation {

    // error Messages
    private static final String REQUIRED_MSG = "Required";

    /** check the input field has any text or not
     *  return true if it contains text otherwise false
     */
    public static boolean hasText(EditText editText) {

        String text = editText.getText().toString().trim();
        editText.setError(null);

        // length 0 means there is no text
        if (text.length() == 0) {
            editText.setError(REQUIRED_MSG);
            return false;
        }

        return true;
    }
}