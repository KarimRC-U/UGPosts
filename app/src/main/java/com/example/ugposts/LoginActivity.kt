package com.example.ugposts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        dbHelper = DBHelper(this)
    }

    fun onLoginClick(view: View) {
        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)

        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val user = checkUser(email, password)
        if (user != null) {
            val sharedPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean("IS_LOGGED_IN", true)
                putInt("USER_ID", user.id)
                putString("USER_NAME", user.nombre)
                putString("USER_EMAIL", user.correo)
                putString("USER_ROLE", user.rol)
                putString("USER_PAGE", user.pagina)
                apply()
            }

            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkUser(email: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Usuarios WHERE correo = ? AND password = ?",
            arrayOf(email, password)
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID_usuario"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val correo = cursor.getString(cursor.getColumnIndexOrThrow("correo"))
            val rol = cursor.getString(cursor.getColumnIndexOrThrow("rol"))
            val pagina = cursor.getString(cursor.getColumnIndexOrThrow("pagina_id"))
            user = User(id, nombre, correo, rol, pagina)
        }
        cursor.close()
        return user
    }

    fun onRegisterClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}

data class User(val id: Int, val nombre: String, val correo: String, val rol: String, val pagina: String?)

