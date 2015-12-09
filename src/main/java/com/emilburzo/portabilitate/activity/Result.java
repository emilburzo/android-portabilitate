package com.emilburzo.portabilitate.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.emilburzo.portabilitate.R;
import com.emilburzo.portabilitate.constant.Constants;
import com.emilburzo.portabilitate.util.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Result extends ActionBarActivity {

    private static final String KEY_NETWORK = "network";
    private static final String KEY_TYPE = "type";

    private static final String TAG = "Result";

    private TextView lblNetwork;
    private TextView textNetwork;

    private TextView lblType;
    private TextView textType;

    private ProgressBar progressBar;

    enum Error {
        UNKNOWN,
        NETWORK_ISSUE,
        INVALID_PHONE_NUMBER,
        ANCOM_DOWN
    }

    private String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.result);

        textNetwork = (TextView) findViewById(R.id.network);
        textType = (TextView) findViewById(R.id.type);

        lblNetwork = (TextView) findViewById(R.id.lblNetwork);
        lblType = (TextView) findViewById(R.id.lblType);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        lblNetwork.setVisibility(View.INVISIBLE);
        lblType.setVisibility(View.INVISIBLE);

        progressBar.setIndeterminate(true);

        getPhoneNumber();

        if (isPhoneValid()) {
            updateView();
        }

        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void updateProgressBar(boolean visibility) {
        progressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

    private void getPhoneNumber() {
        phoneNumber = Utils.getPhoneNumber(getIntent());

        Utils.debug(TAG, "phoneNumber = " + phoneNumber);
    }

    private boolean isPhoneValid() {
        return Utils.isNotEmpty(phoneNumber);
    }

    private void updateView() {
        TextView resultPhoneNumber = (TextView) findViewById(R.id.resultPhoneNumber);
        resultPhoneNumber.setText(phoneNumber);

        new FetchNumberInfo().execute(phoneNumber);
    }

    private class FetchNumberInfo extends AsyncTask<String, Void, Map<String, String>> {

        private static final String URL_TEMPLATE = "http://portabilitate.ro/ro-no-%s";
        private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36";
        private static final String REFERRER = "http://www.google.com";
        private static final int TIMEOUT = 1000 * 10; // 10 seconds

        private Error error;
        private String remoteErrorMsg;

        @Override
        protected Map<String, String> doInBackground(String... numbers) {
            String number = numbers[0];
            String url = String.format(URL_TEMPLATE, number);
            Utils.debug(TAG, "Using url: " + url);

            Map<String, String> result = new HashMap<String, String>();

            try {
                Document doc = Jsoup.connect(url).timeout(TIMEOUT).userAgent(USER_AGENT).referrer(REFERRER).get();

                if (doc == null) {
                    error = Error.ANCOM_DOWN;
                    return null;
                }

                // check for errors
                Element element = doc.getElementById(Constants.Jsoup.ID_ERROR);

                if (element != null) {
                    remoteErrorMsg = element.text();

                    if (remoteErrorMsg.contains(Constants.Jsoup.REMOTE_INVALID_PHONE_STRING)) {
                        error = Error.INVALID_PHONE_NUMBER;
                    } else {
                        error = Error.UNKNOWN;
                    }

                    return null;
                }

                // network
                element = doc.getElementById(Constants.Jsoup.ID_OPERATOR);
                String network = element.text();
                result.put(KEY_NETWORK, network);

                Utils.debug(TAG, "Found network: " + network);

                // number type
                element = doc.getElementById(Constants.Jsoup.ID_NUMBER_TYPE);
                String numberType = element.text();
                result.put(KEY_TYPE, numberType);

                Utils.debug(TAG, "Found number type: " + numberType);
            } catch (IOException e) {
                error = Error.NETWORK_ISSUE;
                return null;
            }

            return result;
        }

        @Override
        protected void onPostExecute(Map<String, String> stringStringMap) {
            if (error == null) {
                textNetwork = (TextView) findViewById(R.id.network);
                textNetwork.setText(stringStringMap.get(KEY_NETWORK));
                lblNetwork.setVisibility(View.VISIBLE);

                textType = (TextView) findViewById(R.id.type);
                textType.setText(stringStringMap.get(KEY_TYPE));
                lblType.setVisibility(View.VISIBLE);
            } else {
                switch (error) {
                    case INVALID_PHONE_NUMBER:
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.phoneNumberInvalid), Toast.LENGTH_LONG).show();
                        break;
                    case UNKNOWN:
                        Toast.makeText(getApplicationContext(), remoteErrorMsg, Toast.LENGTH_LONG).show();
                        break;
                    case NETWORK_ISSUE:
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.networkIssue), Toast.LENGTH_LONG).show();
                        break;
                    case ANCOM_DOWN:
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.ancomIssue), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }

            updateProgressBar(false);
        }
    }
}
