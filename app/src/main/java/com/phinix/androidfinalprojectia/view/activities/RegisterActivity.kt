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

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameInput = findViewById<EditText>(R.id.nameInput)
        val apiKeyInput = findViewById<EditText>(R.id.apiKeyInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)

        registerButton.setOnClickListener {
            val name = nameInput.text.toString()
            val apiKey = apiKeyInput.text.toString()
            val password = passwordInput.text.toString()

            if (name.isNotBlank() && apiKey.isNotBlank() && password.isNotBlank()) {
                val user = UserEntity(name, password, apiKey)
                val db = UserDatabase.getDatabase(applicationContext)

                lifecycleScope.launch {
                    db.userDao().insertUser(user)
                    Toast.makeText(this@RegisterActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }
    }
}