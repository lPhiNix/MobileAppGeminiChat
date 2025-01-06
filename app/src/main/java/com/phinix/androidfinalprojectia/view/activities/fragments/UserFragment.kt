package com.phinix.androidfinalprojectia.view.activities.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.phinix.androidfinalprojectia.R
import com.phinix.androidfinalprojectia.common.db.UserDatabase
import com.phinix.androidfinalprojectia.common.models.Message
import com.phinix.androidfinalprojectia.view.activities.LoginActivity
import com.phinix.androidfinalprojectia.view.activities.MainActivity
import com.phinix.androidfinalprojectia.view.adapter.ChatAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserFragment : Fragment() {

    private lateinit var initialButton: Button
    private lateinit var dateSearchButton: ImageButton
    private lateinit var clearChatButton: ImageButton
    private lateinit var logoutButton: ImageButton
    private lateinit var deleteAccountButton: ImageButton
    private lateinit var dropdownMenu: LinearLayout

    private lateinit var userDatabase: UserDatabase

    private var originalMessages: MutableList<Message>? = null  // Guardamos la lista original de mensajes

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_fragment, container, false)

        initialButton = view.findViewById(R.id.initialButton)
        dateSearchButton = view.findViewById(R.id.searchDateButton)
        clearChatButton = view.findViewById(R.id.clearChatButton)
        logoutButton = view.findViewById(R.id.logoutButton)
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton)
        dropdownMenu = view.findViewById(R.id.dropdownMenu)
        dropdownMenu.visibility = View.GONE

        userDatabase = UserDatabase.getDatabase(requireContext())

        val sharedPref = activity?.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userName = sharedPref?.getString("name", "") ?: ""
        Log.d("UserFragment", "Username fetched from shared preferences: $userName")

        if (userName.isNotEmpty()) {
            initialButton.text = userName.first().uppercaseChar().toString()
        }

        initialButton.setOnClickListener {
            Log.d("UserFragment", "Initial button clicked")
            dropdownMenu.visibility = if (dropdownMenu.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        logoutButton.setOnClickListener {
            Log.d("UserFragment", "Logout button clicked")
            with(sharedPref?.edit()) {
                this?.clear()
                this?.apply()
            }
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        deleteAccountButton.setOnClickListener {
            Log.d("UserFragment", "Delete account button clicked")
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    userDatabase.userDao().deleteUserByName(userName)
                }
                with(sharedPref?.edit()) {
                    this?.clear()
                    this?.apply()
                }
                startActivity(Intent(activity, LoginActivity::class.java))
                activity?.finish()
            }
        }

        clearChatButton.setOnClickListener {
            Log.d("UserFragment", "Clear chat button clicked")
            val mainActivity = activity as? MainActivity
            mainActivity?.clearMessages()
            originalMessages?.clear()

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    userDatabase.chatMessageDao().deleteAllMessagesByUser(userName!!)
                }
            }
        }

        dateSearchButton.setOnClickListener {
            Log.d("UserFragment", "Date search button clicked")
            val mainActivity = activity as? MainActivity
            val dateSearchFragment = mainActivity?.getDateSearchFragment()

            val fragmentView = dateSearchFragment?.view
            if (fragmentView?.visibility == View.GONE) {
                fragmentView.visibility = View.VISIBLE
            } else {
                fragmentView?.visibility = View.GONE
                dateSearchFragment?.getDateInput()?.setText("")
            }
        }

        return view
    }

    // Esta función se usa para configurar y filtrar los mensajes por fecha
    fun filterMessagesByDate(selectedDate: String?) {
        val mainActivity = activity as? MainActivity
        val messages = mainActivity?.getMessages()

        Log.d("UserFragment", "Filtering messages by date: $selectedDate")

        // Si es la primera vez que se carga, guardamos la lista de mensajes
        if (originalMessages == null) {
            originalMessages = messages?.toMutableList()
        }

        val chatAdapter = mainActivity?.getAdapter()

        if (selectedDate.isNullOrEmpty()) {
            // Si no hay filtro de fecha, restauramos todos los mensajes
            chatAdapter?.apply {
                updateMessages(originalMessages ?: mutableListOf())  // Usamos la lista original
                notifyDataSetChanged()
            }
        } else {
            val normalizedSelectedDate = normalizeDate(selectedDate)
            val filteredMessages = originalMessages?.filter { message ->
                val formattedMessageDate = getFormattedDate(message.createAt)
                Log.d("UserFragment", "Comparing message date: $formattedMessageDate with selected date: $normalizedSelectedDate")
                formattedMessageDate == normalizedSelectedDate
            } ?: emptyList()

            // Actualizamos los mensajes filtrados
            chatAdapter?.apply {
                updateMessages(filteredMessages.toMutableList())  // Solo los mensajes filtrados
                notifyDataSetChanged()
            }
        }
    }

    private fun getFormattedDate(time: Long): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(time))
        Log.d("ChatAdapter", "Formatted message date: $formattedDate")
        return formattedDate
    }

    private fun normalizeDate(date: String): String {
        return date.split("/").joinToString("/") { it.padStart(2, '0') }
    }
}
