package com.example.ugposts
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PostActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var mixedAdapter: MixedAdapter
    private lateinit var btnPost: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        dbHelper = DBHelper(this)
        recyclerView = findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnPost = findViewById(R.id.btnPublish)

        checkUserRole()
        applyFilters()
    }

    override fun onResume() {
        super.onResume()
        checkUserRole()
        applyFilters()
    }

    private fun checkUserRole() {

        val sharedPref = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userRole = sharedPref.getString("USER_ROLE", "Usuario") ?: "Usuario"

        if (userRole != "Administrador") {
            btnPost.visibility = View.GONE
        } else {
            btnPost.visibility = View.VISIBLE
        }
    }

    private fun applyFilters() {
        val filterType = intent.getStringExtra("filterType") ?: "Eventos y Posts"
        val filterUser = intent.getStringExtra("filterUser") ?: ""
        val filterClass = intent.getStringExtra("filterClass") ?: "Todos"
        val filterStartDate = intent.getStringExtra("filterStartDate") ?: ""
        val filterEndDate = intent.getStringExtra("filterEndDate") ?: ""
        val filterPage = intent.getStringExtra("filterPage") ?: ""

        val posts = dbHelper.getAllPosts()
        val eventos = dbHelper.getAllEvents()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        val filteredItems = mutableListOf<Postable>()

        filteredItems.addAll(posts.filter {
            (filterType == "Eventos y Posts" || filterType == "Publicaciones") &&
                    (filterUser.isEmpty() || it.userName.contains(filterUser, ignoreCase = true)) &&
                    (filterClass == "Todos" || filterClass.isEmpty()  || it.category.equals(filterClass, ignoreCase = true)) &&
                    (filterStartDate.isEmpty() || parseDate(it.timestamp, dateFormat) >= parseDate(filterStartDate, dateFormat)) &&
                    (filterEndDate.isEmpty() || parseDate(it.timestamp, dateFormat) <= parseDate(filterEndDate, dateFormat)) &&
                    (filterPage == "Paginas"|| it.pageName.contains(filterPage, ignoreCase = true))
        })

        filteredItems.addAll(eventos.filter {
            (filterType == "Eventos y Posts" || filterType == "Eventos") &&
                    (filterUser.isEmpty() || it.userName.contains(filterUser, ignoreCase = true)) &&
                    (filterStartDate.isEmpty() || parseDate(it.fechaInicio, dateFormat) >= parseDate(filterStartDate, dateFormat)) &&
                    (filterEndDate.isEmpty() || parseDate(it.fechaFin ?: "", dateFormat) <= parseDate(filterEndDate, dateFormat))
        })

        mixedAdapter = MixedAdapter(filteredItems)
        recyclerView.adapter = mixedAdapter
    }

    private fun applyEnlaces() {
        val enlaces = dbHelper.getAllEnlaces()
        mixedAdapter = MixedAdapter(enlaces)
        recyclerView.adapter = mixedAdapter
    }

    private fun applyPaginas() {
        val paginas = dbHelper.getAllPages()
        mixedAdapter = MixedAdapter(paginas)
        recyclerView.adapter = mixedAdapter
    }

    fun parseDate(dateString: String, format: SimpleDateFormat): Date {
        return try {
            format.parse(dateString) ?: Date(0)
        } catch (e: Exception) {
            Date(0)
        }
    }

    fun openLink(view: View) {
        val button = view as Button
        val url = button.tag.toString()

        if (url.contains("facebook.com")) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.setPackage("com.facebook.katana")
            try {
                startActivity(intent)
            } catch (e: Exception) {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(fallbackIntent)
            }
        } else {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    fun onClickPost(view: View) {
        val intent = Intent(this, CreatePostActivity::class.java)
        startActivity(intent)
    }

    fun onClickCuenta(view: View) {
        val intent = Intent(this, CuentaActivity::class.java)
        startActivity(intent)
    }

    fun onClickFiltro(view: View) {
        val intent = Intent(this, FilterActivity::class.java)
        startActivity(intent)
    }

    fun onClickTodo(view: View) {
        applyFilters()
    }

    fun onClickEnlaces(view: View) {
        applyEnlaces()
    }

    fun onClickPaginas(view: View) {
        applyPaginas()
    }
}
