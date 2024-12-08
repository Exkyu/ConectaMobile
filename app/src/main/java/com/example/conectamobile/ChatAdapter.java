package com.example.conectamobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ChatAdapter extends android.widget.BaseAdapter {
    private Context context;
    private List<String> messages;

    public ChatAdapter(Context context, List<String> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        }

        String message = messages.get(position);

        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        return convertView;
    }
}