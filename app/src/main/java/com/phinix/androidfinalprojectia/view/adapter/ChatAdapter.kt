package com.phinix.androidfinalprojectia.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phinix.androidfinalprojectia.R
import com.phinix.androidfinalprojectia.common.models.Message

class ChatAdapter(private val messages: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.messageTextView.text = if (message.role == "user") {
            "Tú: ${message.content}"
        } else {
            "Gemini: ${message.content}"
        }
    }

    override fun getItemCount(): Int = messages.size

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }


    fun updateLastMessage(newContent: String) {
        if (messages.isNotEmpty() && messages.last().role == "assistant") {
            messages[messages.size - 1] = messages[messages.size - 1].copy(content = newContent)
            notifyItemChanged(messages.size - 1)
        }
    }
}