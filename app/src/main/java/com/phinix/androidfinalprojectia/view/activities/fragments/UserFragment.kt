package com.phinix.androidfinalprojectia.view.activities.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.phinix.androidfinalprojectia.view.activities.LoginActivity
import com.phinix.androidfinalprojectia.view.activities.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragmento que maneja las interacciones del usuario, como logout, eliminar cuenta y borrar el chat.
 */
class UserFragment : Fragment() {

    // Componentes de la vista que se enlazarán con los botones y menús desplegables.
    private lateinit var initialButton: Button
    private lateinit var clearChatButton: ImageButton
    private lateinit var logoutButton: ImageButton
    private lateinit var deleteAccountButton: ImageButton
    private lateinit var dropdownMenu: LinearLayout

    private lateinit var userDatabase: UserDatabase

    /**
     * Inflamos la vista y configuramos las acciones para los botones.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_fragment, container, false)

        initialButton = view.findViewById(R.id.initialButton)
        clearChatButton = view.findViewById(R.id.clearChatButton)
        logoutButton = view.findViewById(R.id.logoutButton)
        deleteAccountButton = view.findViewById(R.id.deleteAccountButton)
        dropdownMenu = view.findViewById(R.id.dropdownMenu)
        dropdownMenu.visibility = View.GONE

        userDatabase = UserDatabase.getDatabase(requireContext())

        // Recuperación del nombre del usuario desde preferencias compartidas
        val sharedPref = activity?.getSharedPreferences("UserPref", Context.MODE_PRIVATE)
        val userName = sharedPref?.getString("name", "") ?: ""

        // Establecer la inicial del nombre del usuario si está presente
        if (userName.isNotEmpty()) {
            initialButton.text = userName.first().uppercaseChar().toString()
        }

        // Configuración del botón que despliega las opciones del usuario
        initialButton.setOnClickListener {
            dropdownMenu.visibility = if (dropdownMenu.visibility == View.GONE) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        // Configuración del botón de logout
        logoutButton.setOnClickListener {
            with(sharedPref?.edit()) {
                this?.clear()
                this?.apply()
            }
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }

        // Configuración del botón para eliminar la cuenta
        deleteAccountButton.setOnClickListener {
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

        // Configuración del botón para borrar el historial de chat
        clearChatButton.setOnClickListener {
            val mainActivity = activity as? MainActivity
            mainActivity?.clearMessages()

            CoroutineScope(Dispatchers.IO).launch {
                userDatabase.chatMessageDao().deleteAllMessagesByUser(userName!!)
            }
        }

        return view
    }
}
