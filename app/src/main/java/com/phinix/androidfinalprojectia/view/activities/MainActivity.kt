package com.phinix.androidfinalprojectia.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.phinix.androidfinalprojectia.R
import com.phinix.androidfinalprojectia.view.adapter.ChatAdapter
import com.phinix.androidfinalprojectia.view.activities.fragments.UserFragment
import com.phinix.androidfinalprojectia.common.models.Message
import com.phinix.androidfinalprojectia.view.activities.fragments.GeminiModelFragment
import com.phinix.androidfinalprojectia.view.activities.fragments.TopBarFragment
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var userInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()

    private var modelName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("name", null)
        val apiKey = sharedPref.getString("apiKey_$userName", null)

        if (userName == null || apiKey == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Añadir el nuevo fragmento de cabecera
        val topHeaderFragment = TopBarFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.topHeaderContainer, topHeaderFragment)
            .commit()

        // Configuración restante de la actividad...
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
                userInput.text.clear()
                sendMessage(apiKey, userMessage)
            }
        }
    }

    private fun sendMessage(apiKey: String, prompt: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey
                )

                val responseStream = generativeModel.generateContentStream(prompt)

                var partialResponse = ""
                withContext(Dispatchers.Main) {
                    val tempMessage = Message("assistant", "")
                    addMessageToChat(tempMessage)
                }

                responseStream.collect { chunk ->
                    partialResponse += chunk.text
                    withContext(Dispatchers.Main) {
                        chatAdapter.updateLastMessage(partialResponse)
                        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    }
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
}