package com.emilburzo.portabilitate.util;

import android.content.Intent;
import android.util.Log;
import com.emilburzo.portabilitate.BuildConfig;
import com.emilburzo.portabilitate.constant.Constants;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class Utils {

    public static String getPhoneNumber(Intent intent) {
        return intent.getStringExtra(Constants.Intents.PHONE_NUMBER);
    }

    public static boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    public static String getNationalNumber(String number) throws NumberParseException {
        // parse phone number
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber roNumber = phoneUtil.parse(number, Constants.Phone.COUNTRY_CODE_RO);

        // format to national number format and remove spaces
        String national = phoneUtil.format(roNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        national = national.replaceAll(" ", "");

        return national;
    }

    public static void debug(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }
}
