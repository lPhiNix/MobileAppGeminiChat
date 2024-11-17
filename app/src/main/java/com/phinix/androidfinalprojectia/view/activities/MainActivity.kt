package com.phinix.androidfinalprojectia.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.phinix.androidfinalprojectia.R
import com.phinix.androidfinalprojectia.view.adapter.ChatAdapter
import com.phinix.androidfinalprojectia.common.models.Message
import com.phinix.androidfinalprojectia.db.ChatMessageEntity
import com.phinix.androidfinalprojectia.db.UserDatabase
import com.phinix.androidfinalprojectia.view.activities.fragments.TopBarFragment
import com.phinix.androidfinalprojectia.view.activities.fragments.UserFragment
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var userInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()

    private lateinit var db: UserDatabase // Declare the database without initialization here
    private var modelName: String = "gemini-1.5-flash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database after onCreate is called
        db = UserDatabase.getDatabase(applicationContext)

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("name", null)
        val apiKey = sharedPref.getString("apiKey_$userName", null)

        if (userName == null || apiKey == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Cargar mensajes guardados
        lifecycleScope.launch {
            val savedMessages = db.chatMessageDao().getMessagesByUser(userName)
            messages.addAll(savedMessages.map { Message(it.role, it.content) })
            chatAdapter.notifyDataSetChanged()
        }

        val topHeaderFragment = TopBarFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.topHeaderContainer, topHeaderFragment)
            .commit()

        userInput = findViewById(R.id.userInput)
        sendButton = findViewById(R.id.sendButton)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            val userMessage = userInput.text.toString()
            if (userMessage.isNotBlank()) {
                val message = Message("user", userMessage)
                addMessageToChat(message)

                lifecycleScope.launch {
                    val chatMessageEntity = ChatMessageEntity(userName = userName!!, role = "user", content = userMessage)
                    db.chatMessageDao().insertMessage(chatMessageEntity)
                }

                userInput.text.clear()
                sendMessage(userName, apiKey, messages)
            }
        }
    }

    private fun sendMessage(userName : String, apiKey: String, messageHistory: List<Message>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey
                )

                val prompt = messageHistory.joinToString("\n") { it.content }
                val responseStream = generativeModel.generateContentStream(prompt)

                var partialResponse = ""
                withContext(Dispatchers.Main) {
                    val tempMessage = Message("assistant", "")
                    addMessageToChat(tempMessage) // Temporarily add an empty message for the assistant
                }

                responseStream.collect { chunk ->
                    partialResponse += chunk.text
                    withContext(Dispatchers.Main) {
                        chatAdapter.updateLastMessage(partialResponse) // Update the last message in the adapter
                        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }

                lifecycleScope.launch {
                    val chatMessageEntity = ChatMessageEntity(userName = userName!!, role = "assistant", content = partialResponse)
                    db.chatMessageDao().insertMessage(chatMessageEntity)
                }

            } catch (e: Exception) {
                Log.e("Gemini", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    addMessageToChat(Message("assistant", "Error: ${e.message}"))
                }
            }
        }
    }

    private fun addMessageToChat(message: Message) {
        chatAdapter.addMessage(message)
        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    fun clearMessages() {
        messages.clear()
        chatAdapter.notifyDataSetChanged()
    }

    fun getMessages() = messages
    fun getChatAdapter() = chatAdapter
}