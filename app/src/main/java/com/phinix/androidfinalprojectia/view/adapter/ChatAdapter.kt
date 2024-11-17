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
        val markwon = Markwon.create(context) // Utilizamos Markdown para formatear el ouput del modelo de IA

        if (message.role == "user") {
            holder.messageTextView.text = "Tú: ${message.content}"
        } else {
            markwon.setMarkdown(holder.messageTextView, "**Gemini**: ${message.content}")
        }
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
}