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

// Adaptador personalizado para mostrar los mensajes en una lista
public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context; // Contexto de la aplicación
    private List<Message> messages; // Lista de mensajes a mostrar

    // Constructor del adaptador
    public MessageAdapter(@NonNull Context context, List<Message> messages) {
        super(context, 0, messages); // Llamada al constructor de ArrayAdapter
        this.context = context;
        this.messages = messages;
    }

    // Método para obtener la vista de cada elemento de la lista
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Obtener el mensaje en la posición especificada
        Message message = getItem(position);

        // Si la vista no está reciclada, inflar una nueva vista
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        }

        // Referencia al TextView donde se mostrará el texto del mensaje
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);

        // Comprobar si el mensaje no es nulo
        if (message != null) {
            messageTextView.setText(message.getText()); // Establecer el texto del mensaje

            // Cambiar el estilo del mensaje según su tipo
            if ("sent".equals(message.getType())) {
                // Si el mensaje es enviado, cambiar el estilo de fondo, texto y alineación
                messageTextView.setBackgroundResource(R.drawable.bg_message_sent);
                messageTextView.setTextColor(ContextCompat.getColor(context, android.R.color.white)); // Texto blanco
                messageTextView.setGravity(View.TEXT_ALIGNMENT_VIEW_END); // Alinear a la derecha
            } else {
                // Si el mensaje es recibido, cambiar el estilo de fondo, texto y alineación
                messageTextView.setBackgroundResource(R.drawable.bg_message_received);
                messageTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black)); // Texto negro
                messageTextView.setGravity(View.TEXT_ALIGNMENT_VIEW_START); // Alinear a la izquierda
            }
        }

        // Devolver la vista configurada
        return convertView;
    }
}
