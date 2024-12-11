package com.example.conectamobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.List;

import android.widget.ArrayAdapter;

public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private List<Message> messages;

    public MessageAdapter(@NonNull Context context, List<Message> messages) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Message message = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        }

        TextView messageTextView = convertView.findViewById(R.id.messageTextView);

        if (message != null) {
            messageTextView.setText(message.getText());

            // Cambiar estilo según el tipo de mensaje
            if ("sent".equals(message.getType())) {
                messageTextView.setBackgroundResource(R.drawable.bg_message_sent);
                messageTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                messageTextView.setGravity(View.TEXT_ALIGNMENT_VIEW_END);
            } else {
                messageTextView.setBackgroundResource(R.drawable.bg_message_received);
                messageTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
                messageTextView.setGravity(View.TEXT_ALIGNMENT_VIEW_START);
            }
        }

        return convertView;
    }
}
