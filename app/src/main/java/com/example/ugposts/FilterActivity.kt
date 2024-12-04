package com.example.ugposts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class FilterActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var spinnerPages: Spinner
    private lateinit var spinnerFilterType: Spinner
    private lateinit var spinnerFilterClass: Spinner
    private lateinit var etFilterUser: EditText
    private lateinit var tvFilterStartDate: TextView
    private lateinit var tvFilterEndDate: TextView
    private lateinit var btnApplyFilters: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        dbHelper = DBHelper(this)

        spinnerPages = findViewById(R.id.spinnerArea)
        spinnerFilterType = findViewById(R.id.spinnerFilterType)
        spinnerFilterClass = findViewById(R.id.spinnerFilterClass)
        etFilterUser = findViewById(R.id.etFilterUser)
        tvFilterStartDate = findViewById(R.id.tvFilterStartDate)
        tvFilterEndDate = findViewById(R.id.tvFilterEndDate)
        btnApplyFilters = findViewById(R.id.btnApplyFilters)

        loadPagesIntoSpinner()

        tvFilterStartDate.setOnClickListener { showDateTimePicker(tvFilterStartDate) }
        tvFilterEndDate.setOnClickListener { showDateTimePicker(tvFilterEndDate) }

        btnApplyFilters.setOnClickListener {
            val filterType = spinnerFilterType.selectedItem.toString()
            val filterUser = etFilterUser.text.toString()
            val filterClass = spinnerFilterClass.selectedItem.toString()
            val filterStartDate = tvFilterStartDate.text.toString()
            val filterEndDate = tvFilterEndDate.text.toString()
            val filterPage = spinnerPages.selectedItem.toString()

            val intent = Intent(this, PostActivity::class.java).apply {
                putExtra("filterType", filterType)
                putExtra("filterUser", filterUser)
                putExtra("filterClass", filterClass)
                putExtra("filterStartDate", filterStartDate)
                putExtra("filterEndDate", filterEndDate)
                putExtra("filterPage", filterPage)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun loadPagesIntoSpinner() {
        val pages = mutableListOf("Paginas")
        pages.addAll(dbHelper.getAllPages().map { it.nombrePagina })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPages.adapter = adapter
    }

    private fun showDateTimePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        val formattedDateTime = "$day/${month + 1}/$year $hour:$minute"
                        textView.text = formattedDateTime
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