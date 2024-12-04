package com.example.ugposts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CuentaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuenta)

        val sharedPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userName = sharedPref.getString("USER_NAME", "Desconocido")
        val userEmail = sharedPref.getString("USER_EMAIL", "Correo no disponible")
        val userRole = sharedPref.getString("USER_ROLE", "Rol no disponible")
        val userPage = sharedPref.getString("USER_PAGE", "Los usuarios no cuentan con pagina.")

        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val tvUserRole = findViewById<TextView>(R.id.tvUserRole)
        val tvUserPage = findViewById<TextView>(R.id.tvUserPage)

        tvUserName.text = "Nombre: $userName"
        tvUserEmail.text = "Correo: $userEmail"
        tvUserRole.text = "$userRole"
        tvUserPage.text = "Página: $userPage"
    }

    fun onClickBackToMenu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun onClickLogout(view: View) {
        val sharedPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}

