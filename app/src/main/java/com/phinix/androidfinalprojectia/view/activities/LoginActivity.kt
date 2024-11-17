package com.phinix.androidfinalprojectia.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.phinix.androidfinalprojectia.R
import androidx.lifecycle.lifecycleScope
import com.phinix.androidfinalprojectia.common.db.UserDatabase
import kotlinx.coroutines.launch

/**
 * Fragmento para manejar el inicio de sesión de los usuarios.
 */
class LoginActivity : AppCompatActivity() {

    /**
     * Se configura la vista y la lógica de inicio de sesión.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {
            val name = nameInput.text.toString()
            val password = passwordInput.text.toString()

            val db = UserDatabase.getDatabase(applicationContext)

            lifecycleScope.launch {
                val user = db.userDao().getUser(name, password)
                if (user != null) {
                    // Guardar el nombre en SharedPreferences
                    val sharedPref = getSharedPreferences("UserPref", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("name", user.name)
                        putString("apiKey_$name", user.apiKey)
                        apply()
                    }
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    // Si el usuario no está registrado, se muestra un mensaje
                    Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}