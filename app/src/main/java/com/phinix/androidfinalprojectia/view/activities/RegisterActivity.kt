package com.phinix.androidfinalprojectia.view.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.phinix.androidfinalprojectia.R
import com.phinix.androidfinalprojectia.common.db.UserDatabase
import com.phinix.androidfinalprojectia.common.db.UserEntity
import kotlinx.coroutines.launch

/**
 * Actividad para registrar nuevos usuarios en la aplicación.
 * Permite ingresar el nombre de usuario, clave API y contraseña.
 */
class RegisterActivity : AppCompatActivity() {

    /**
     * Inicializa la actividad y configura el comportamiento del registro de usuario.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val apiKeyInput = findViewById<EditText>(R.id.apiKeyInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Configuración del botón de registro
        registerButton.setOnClickListener {
            val name = nameInput.text.toString()
            val apiKey = apiKeyInput.text.toString()
            val password = passwordInput.text.toString()

            // Verifica que todos los campos sean obligatorios
            if (name.isNotBlank() && apiKey.isNotBlank() && password.isNotBlank()) {
                val user = UserEntity(name, password, apiKey)
                val db = UserDatabase.getDatabase(applicationContext)

                // Inserta el usuario en la base de datos
                lifecycleScope.launch {
                    db.userDao().insertUser(user)
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    finish() // Cierra la actividad y vuelve al login
                }
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }
}