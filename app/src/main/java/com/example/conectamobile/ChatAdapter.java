package com.example.conectamobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

// Clase personalizada para manejar la lista de mensajes en el chat
public class ChatAdapter extends android.widget.BaseAdapter {

    private Context context; // Contexto de la actividad o fragmento
    private List<String> messages; // Lista de mensajes que se mostrarán en el ListView

    // Constructor para inicializar el contexto y la lista de mensajes
    public ChatAdapter(Context context, List<String> messages) {
        this.context = context;
        this.messages = messages;
    }

    // Devuelve el número total de elementos en la lista de mensajes
    @Override
    public int getCount() {
        return messages.size();
    }

    // Devuelve el mensaje en la posición especificada
    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    // Devuelve el ID del elemento en la posición especificada
    // Aquí simplemente usamos la posición como ID, ya que los elementos no tienen un ID único
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Devuelve la vista correspondiente a un elemento en la posición especificada
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Reutilización de vistas para optimizar el rendimiento del ListView
        if (convertView == null) {
            // Si no hay una vista reutilizable, se infla una nueva vista desde el layout XML
            convertView = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        }

        // Obtiene el mensaje correspondiente a la posición actual
        String message = messages.get(position);

        // Enlaza la vista del TextView en el layout con el contenido del mensaje
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        messageTextView.setText(message); // Establece el texto del mensaje en el TextView

        // Devuelve la vista configurada para esta posición
        return convertView;
    }
}
