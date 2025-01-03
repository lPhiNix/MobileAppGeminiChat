package com.phinix.androidfinalprojectia.view.activities

import android.annotation.SuppressLint
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
import com.phinix.androidfinalprojectia.common.db.ChatMessageEntity
import com.phinix.androidfinalprojectia.common.db.UserDatabase
import com.phinix.androidfinalprojectia.view.activities.fragments.TopBarFragment
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    // Componentes de la UI
    private lateinit var userInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val messages = mutableListOf<Message>()

    // Base de datos y configuración del modelo de IA predeterminado
    private lateinit var db: UserDatabase
    private var modelName: String = "gemini-1.5-flash"

    /**
     * Inicializa la actividad, configura los fragmentos y establece los listeners.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialización de la base de datos y preferencias compartidas
        db = UserDatabase.getDatabase(applicationContext)
        val sharedPref = getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("name", null)
        val apiKey = sharedPref.getString("apiKey_$userName", null)

        // Redirige al login si no hay usuario o apiKey almacenados
        if (userName == null || apiKey == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Configuración del fragmento superior
        val topHeaderFragment = TopBarFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.topHeaderContainer, topHeaderFragment)
            .commit()

        // Configuración de la UI
        userInput = findViewById(R.id.userInput)
        sendButton = findViewById(R.id.sendButton)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)

        chatAdapter = ChatAdapter(messages)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter = chatAdapter

        // Envío de mensajes del usuario
        sendButton.setOnClickListener {
            val createAt = System.currentTimeMillis()
            val userMessage = userInput.text.toString()
            if (userMessage.isNotBlank()) {
                val message = Message("user", userMessage, createAt)
                addMessageToChat(message)

                // Guardar el mensaje en la base de datos
                lifecycleScope.launch {
                    val chatMessageEntity = ChatMessageEntity(userName = userName!!, role = "user", content = userMessage, createdAt = createAt)
                    db.chatMessageDao().insertMessage(chatMessageEntity)
                }

                userInput.text.clear()
                sendMessage(userName, apiKey, messages)
            }
        }

        // Cargar los mensajes guardados de la base de datos
        lifecycleScope.launch {
            val savedMessages = db.chatMessageDao().getMessagesByUser(userName)
            messages.addAll(savedMessages.map { Message(it.role, it.content, it.createdAt) })
            chatAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Envía el mensaje del usuario al modelo generativo y obtiene una respuesta.
     * La respuesta se va mostrando mientras se recibe en fragmentos.
     */
    private fun sendMessage(userName : String, apiKey: String, messageHistory: List<Message>) {
        CoroutineScope(Dispatchers.IO).launch {
            val createAt = System.currentTimeMillis()
            try {
                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey
                )

                val prompt = messageHistory.joinToString("\n") { it.content }
                val responseStream = generativeModel.generateContentStream(prompt)

                var partialResponse = ""
                withContext(Dispatchers.Main) {
                    val tempMessage = Message("assistant", "", createAt)
                    addMessageToChat(tempMessage)
                }

                responseStream.collect { chunk ->
                    partialResponse += chunk.text
                    withContext(Dispatchers.Main) {
                        chatAdapter.updateLastMessage(partialResponse)
                        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                    }
                }

                // Guardar la respuesta generada en la base de datos
                lifecycleScope.launch {
                    val chatMessageEntity = ChatMessageEntity(userName = userName!!, role = "assistant", content = partialResponse, createdAt = createAt)
                    db.chatMessageDao().insertMessage(chatMessageEntity)
                }

            } catch (e: Exception) {
                Log.e("Gemini", "Error: ${e.message}")
                withContext(Dispatchers.Main) {
                    addMessageToChat(Message("assistant", "Error: ${e.message}", createAt))
                }
            }
        }
    }

    private fun addMessageToChat(message: Message) {
        chatAdapter.addMessage(message)
        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearMessages() {
        messages.clear()
        chatAdapter.notifyDataSetChanged()
    }

    fun setModelName(name : String) {
        modelName = name
    }
}