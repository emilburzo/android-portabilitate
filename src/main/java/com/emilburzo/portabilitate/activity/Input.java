package com.emilburzo.portabilitate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
import com.emilburzo.portabilitate.R;
import com.emilburzo.portabilitate.constant.Constants;
import com.emilburzo.portabilitate.util.Utils;
import com.google.i18n.phonenumbers.NumberParseException;

public class Input extends AppCompatActivity {

    private static final String TAG = "Input";

    private static final int REQUEST_PICK_CONTACT = 1;

    private EditText inputPhoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initUiFields();
    }

    private void initUiFields() {
        inputPhoneNumber = (EditText) findViewById(R.id.inputPhoneNumber);

        inputPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputErrors();
            }
        });
    }

    public void verify(View view) {
        // get the phone number that the user inputed
        Editable phoneInput = inputPhoneNumber.getText();

        if (phoneInput == null) {
            inputPhoneNumber.setError(getResources().getText(R.string.phoneNumberEmpty));
            return;
        }

        String phoneNumber = phoneInput.toString();

        if (phoneNumber.trim().length() == 0) {
            inputPhoneNumber.setError(getResources().getText(R.string.phoneNumberEmpty));
            return;
        }

        try {
            phoneNumber = Utils.getNationalNumber(phoneNumber);
        } catch (NumberParseException e) {
            inputPhoneNumber.setError(getResources().getText(R.string.phoneNumberInvalid));
            return;
        }

        Utils.debug(TAG, "Found phone number: " + phoneNumber);

        // go to the result activity
        Intent intent = new Intent(this, Result.class);
        intent.putExtra(Constants.Intents.PHONE_NUMBER, phoneNumber);
        startActivity(intent);
    }

    private void loadValues(Intent intent) {
        String phoneNumber = Utils.getPhoneNumber(intent);

        if (Utils.isNotEmpty(phoneNumber)) {
            try {
                String national = Utils.getNationalNumber(phoneNumber);
                inputPhoneNumber.setText(national);
            } catch (NumberParseException e) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.contactPhoneNumberInvalid), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void clearInput(View view) {
        inputPhoneNumber.setText("");

        clearInputErrors();
    }

    private void clearInputErrors() {
        // clear any possible error messages
        inputPhoneNumber.setError(null);
    }

    public void lookup(View view) {
        clearInputErrors();

        Intent intent = new Intent(this, Contacts.class);
        startActivityForResult(intent, REQUEST_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                loadValues(intent);
            }
        }
    }
}
