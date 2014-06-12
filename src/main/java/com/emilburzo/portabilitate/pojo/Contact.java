package com.emilburzo.portabilitate.pojo;

import android.provider.ContactsContract;

public class Contact {

    public String name;
    public String phone;
    public String phoneType;

    public Contact(String name, String phone, String phoneType) {
        this.name = name;
        this.phone = phone;
        this.phoneType = phoneType;
    }

    public Contact(String name, String phone, int phoneType) {
        this(name, phone, getPhoneTypeLabel(phoneType));
    }

    public static String getPhoneTypeLabel(int phoneType) {
        // todo language resources

        switch (phoneType) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "mobil";
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "fix";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "lucru";
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", phoneType='" + phoneType + '\'' +
                '}';
    }

}
