package com.phinix.androidfinalprojectia.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phinix.androidfinalprojectia.R
import com.phinix.androidfinalprojectia.common.models.Message
import io.noties.markwon.Markwon

/**
 * Adaptador para mostrar los mensajes en el RecyclerView en la interfaz de usuario.
 * Administra la presentación de los mensajes de usuario y asistente.
 */
class ChatAdapter(private val messages: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    /**
     * ViewHolder que contiene la referencia a la vista de mensaje.
     */
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView) // Nueva vista para la fecha
    }

    /**
     * Crea una nueva vista para cada elemento de la lista de mensajes.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ChatViewHolder(view)
    }

    /**
     * Asocia los datos de cada mensaje a la vista correspondiente.
     */
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        val context = holder.messageTextView.context
        val markwon = Markwon.create(context)

        if (message.role == "user") {
            holder.messageTextView.text = "Tú: ${message.content}"
        } else {
            markwon.setMarkdown(holder.messageTextView, "**Gemini**: ${message.content}")
        }

        // Mostrar el tiempo transcurrido
        holder.timeTextView.text = getTimeAgo(message.createAt)

        // Mostrar la fecha en formato DD/MM/YYYY
        holder.dateTextView.text = getFormattedDate(message.createAt)
    }

    /**
     * Obtiene el número total de elementos (mensajes) en el adaptador.
     */
    override fun getItemCount(): Int = messages.size

    /**
     * Agrega un nuevo mensaje al chat y notifica al adaptador para actualizar la UI.
     */
    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    /**
     * Actualiza el último mensaje del asistente en el chat con el nuevo contenido.
     */
    fun updateLastMessage(newContent: String) {
        if (messages.isNotEmpty() && messages.last().role == "assistant") {
            messages[messages.size - 1] = messages[messages.size - 1].copy(content = newContent)
            notifyItemChanged(messages.size - 1)
        }
    }

    /**
     * Función que formatea la el tiempo transcurrido de creación del mensaje
     */
    private fun getTimeAgo(time: Long): String {
        val diff = System.currentTimeMillis() - time

        return when {
            diff < 60000 -> "Hace ${diff / 1000} segundos"
            diff < 3600000 -> "Hace ${diff / 60000} minutos"
            diff < 86400000 -> "Hace ${diff / 3600000} horas"
            diff < 2592000000L -> "Hace ${diff / 86400000} días"
            diff < 31536000000L -> "Hace ${diff / 2592000000L} meses"
            else -> "Hace ${diff / 31536000000L} años"
        }
    }

    /**
     * Función que formatea la fecha en el formato DD/MM/YYYY.
     */
    private fun getFormattedDate(time: Long): String {
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return dateFormat.format(java.util.Date(time))
    }
}