package com.emilburzo.portabilitate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.emilburzo.portabilitate.R;
import com.emilburzo.portabilitate.pojo.Contact;

import java.util.List;

public class ContactsAdapter extends BaseAdapter {

    private final List<Contact> contactList;
    private static LayoutInflater inflater = null;

    private Context context;

    public ContactsAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;

        if (vi == null) {
            vi = inflater.inflate(R.layout.contacts_item, parent);
        }

        Contact contact = contactList.get(position);

        TextView name = (TextView) vi.findViewById(R.id.contactName);
        name.setText(contact.name);

        TextView phone = (TextView) vi.findViewById(R.id.contactPhone);
        if (contact.phoneType == null) {
            phone.setText(String.format("%s", contact.phone));
        } else {
            phone.setText(String.format("%s (%s)", contact.phone, contact.phoneType));
        }

        return vi;
    }
}
