package com.example.ugposts

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var spinnerPages: Spinner
    private lateinit var spinnerRole: Spinner
    private lateinit var spinnerArea: Spinner
    private lateinit var tvAreaDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        dbHelper = DBHelper(this)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        spinnerRole = findViewById(R.id.spinnerRole)
        spinnerPages = findViewById(R.id.spinnerArea)
        spinnerArea = findViewById(R.id.spinnerArea)
        tvAreaDescription = findViewById(R.id.tvInfo)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        loadPagesIntoSpinner()

        spinnerArea.visibility = View.GONE
        tvAreaDescription.visibility = View.GONE

        spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedRole = spinnerRole.selectedItem.toString()
                if (selectedRole == "Administrador") {
                    spinnerArea.visibility = View.VISIBLE
                    tvAreaDescription.visibility = View.VISIBLE
                } else {
                    spinnerArea.visibility = View.GONE
                    tvAreaDescription.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val selectedPage = spinnerPages.selectedItem?.toString() ?: ""
            val selectedRole = spinnerRole.selectedItem?.toString() ?: "Usuario"

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRole.isEmpty()) {
                Toast.makeText(this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = registerUser(name, email, password, selectedPage, selectedRole)
            if (result) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar. Intenta de nuevo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadPagesIntoSpinner() {
        val pages = dbHelper.getAllPages().map { it.nombrePagina }

        if (pages.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pages)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPages.adapter = adapter
        } else {
            val emptyList = listOf<String>("")
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emptyList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPages.adapter = adapter
        }
    }

    private fun registerUser(name: String, email: String, password: String, page: String, role: String): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", name)
            put("correo", email)
            put("rol", role)
            put("password", password)
            if (role == "Administrador" && page.isNotEmpty()) {
                val pageId = dbHelper.getPageIdByName(page)
                put("pagina_id", pageId)
            } else {
                putNull("pagina_id")
            }
        }

        return try {
            db.insertOrThrow("Usuarios", null, values) > 0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}