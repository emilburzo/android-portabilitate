package com.emilburzo.portabilitate.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.emilburzo.portabilitate.R;
import com.emilburzo.portabilitate.adapter.ContactsAdapter;
import com.emilburzo.portabilitate.constant.Constants;
import com.emilburzo.portabilitate.pojo.Contact;
import com.emilburzo.portabilitate.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static android.provider.ContactsContract.CommonDataKinds.Phone;

public class Contacts extends ActionBarActivity {

    private static final String TAG = "Contacts";

    private List<Contact> contacts = new ArrayList<Contact>();
    private ContactsAdapter contactsAdapter;

    private ListView listView;
    private EditText contactsSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.contactsList);

        contactsSearch = (EditText) findViewById(R.id.contactsSearch);
        contactsSearch.addTextChangedListener(new ContactsSearch());

        // load contacts from the device phonebook/people/contacts
        loadContacts();

        // Assign adapter to ListView
        contactsAdapter = new ContactsAdapter(this, contacts);
        listView.setAdapter(contactsAdapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = (Contact) listView.getItemAtPosition(position);

                Intent intent = new Intent(Contacts.this, Input.class);
                intent.putExtra(Constants.Intents.PHONE_NUMBER, contact.phone);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void loadContacts() {
        contacts.clear();

        // required fields
        String[] projection = new String[]{Phone.DISPLAY_NAME, Phone.NUMBER, Phone.TYPE, Phone.LOOKUP_KEY};

        // filtering query
        String selection = "";
        selection += Phone.DISPLAY_NAME + " LIKE ?";
        selection += " OR ";
        selection += Phone.NUMBER + " LIKE ?";
        selection += " OR ";
        selection += "replace(replace(replace(replace(" + Phone.NUMBER + ", '+', ''), '-', ''), '(', ''), ')', '')" + " LIKE ?";

        // filtering args
        String selectionArg = "%" + contactsSearch.getText() + "%";
        String[] selectionArgs = new String[]{selectionArg, selectionArg, selectionArg};

        // order by
        String orderBy = "lower(" + Phone.DISPLAY_NAME + ") ASC, " + Phone.TYPE + " ASC";

        // query
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectionArgs, orderBy);

        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            String lookupKey = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY)); // todo decide if we need this

            Utils.debug(TAG, String.format("Found name: '%s', phone number: '%s', type: '%s', key: '%s'", name, phoneNumber, phoneType, lookupKey));

            Contact contact = new Contact(name, phoneNumber, phoneType);
            contacts.add(contact);
        }

        phones.close();

        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        loadContacts();
    }

    public void doClearSearch(View view) {
        contactsSearch.setText("");

        loadContacts();
    }

    private class ContactsSearch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            loadContacts();
        }
    }
}
