package com.example.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SMSAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> smsList;

    public SMSAdapter(Context context, List<String> smsList) {
        super(context, 0, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        String sms = smsList.get(position);

        TextView smsTextView = view.findViewById(android.R.id.text1);
        smsTextView.setText(sms);

        return view;
    }
}
