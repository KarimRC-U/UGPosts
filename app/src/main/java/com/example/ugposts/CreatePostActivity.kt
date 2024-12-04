package com.example.ugposts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    private lateinit var radioGroupPostType: RadioGroup
    private lateinit var layoutNews: LinearLayout
    private lateinit var layoutEvent: LinearLayout

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var spinnerCategory: Spinner

    private lateinit var editTextEventName: EditText
    private lateinit var editTextEventDescription: EditText
    private lateinit var textViewStartDate: TextView
    private lateinit var textViewEndDate: TextView
    private lateinit var editTextLocation: EditText

    private lateinit var btnPost: Button
    private lateinit var btnCancel: Button

    private var userId: Int = -1
    private var userName: String = "Desconocido"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postcreate)

        val sharedPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        userId = sharedPref.getInt("USER_ID", -1)
        userName = sharedPref.getString("USER_NAME", "Desconocido").orEmpty()

        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        radioGroupPostType = findViewById(R.id.radioGroupPostType)
        layoutNews = findViewById(R.id.layoutNews)
        layoutEvent = findViewById(R.id.layoutEvent)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        spinnerCategory = findViewById(R.id.spinnerCategory)

        editTextEventName = findViewById(R.id.editTextEventName)
        editTextEventDescription = findViewById(R.id.editTextEventDescription)
        textViewStartDate = findViewById(R.id.textViewStartDate)
        textViewEndDate = findViewById(R.id.textViewEndDate)
        editTextLocation = findViewById(R.id.editTextLocation)

        btnPost = findViewById(R.id.btnPost)
        btnCancel = findViewById(R.id.btnCancel)

        layoutNews.visibility = View.GONE
        layoutEvent.visibility = View.GONE

        radioGroupPostType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioNews -> {
                    layoutNews.visibility = View.VISIBLE
                    layoutEvent.visibility = View.GONE
                }
                R.id.radioEvent -> {
                    layoutEvent.visibility = View.VISIBLE
                    layoutNews.visibility = View.GONE
                }
            }
        }

        btnPost.setOnClickListener {
            val dbHelper = DBHelper(this)

            if (radioGroupPostType.checkedRadioButtonId == R.id.radioNews) {
                val title = editTextTitle.text.toString()
                val content = editTextContent.text.toString()
                val category = spinnerCategory.selectedItem.toString()

                if (title.isNotEmpty() && content.isNotEmpty()) {

                    val timestamp = formatDate(System.currentTimeMillis())

                    val pageId = 1

                    val success = dbHelper.insertPost(title, content, timestamp, category, userId, pageId)
                    if (success) {
                        Toast.makeText(this, "Noticia publicada con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al publicar la noticia", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            } else if (radioGroupPostType.checkedRadioButtonId == R.id.radioEvent) {
                val eventName = editTextEventName.text.toString()
                val eventDescription = editTextEventDescription.text.toString()
                val startDate = textViewStartDate.text.toString()
                val endDate = textViewEndDate.text.toString()
                val location = editTextLocation.text.toString()

                if (eventName.isNotEmpty() && eventDescription.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty() && location.isNotEmpty()) {
                    val success = dbHelper.insertEvent(eventName, eventDescription, startDate, endDate, location, userId)
                    if (success) {
                        Toast.makeText(this, "Evento publicado con éxito", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al publicar el evento", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun formatDate(millis: Long): String {
        val date = Date(millis)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return format.format(date)
    }

    fun showStartDateTimePicker(view: View) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        val formattedDateTime = "$day/${month + 1}/$year $hour:$minute"
                        textViewStartDate.text = formattedDateTime
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun showEndDateTimePicker(view: View) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        val formattedDateTime = "$day/${month + 1}/$year $hour:$minute"
                        textViewEndDate.text = formattedDateTime
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
