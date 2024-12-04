package com.example.ugposts

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MixedAdapter(private var items: List<Postable>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_POST = 0
        private const val TYPE_EVENT = 1
        private const val TYPE_PAGE = 2
        private const val TYPE_ENLACE = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].type) {
            PostableType.POST -> TYPE_POST
            PostableType.EVENT -> TYPE_EVENT
            PostableType.PAGINA -> TYPE_PAGE
            PostableType.ENLACE -> TYPE_ENLACE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_POST -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
                PostViewHolder(view)
            }
            TYPE_EVENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_evento, parent, false)
                EventViewHolder(view)
            }
            TYPE_PAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_link, parent, false)
                PageViewHolder(view)
            }
            TYPE_ENLACE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_link, parent, false)
                EnlaceViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostViewHolder -> holder.bind(items[position] as Post)
            is EventViewHolder -> holder.bind(items[position] as Evento)
            is PageViewHolder -> holder.bind(items[position] as Pagina)
            is EnlaceViewHolder -> holder.bind(items[position] as Enlace)
        }
    }

    override fun getItemCount(): Int = items.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.postTitle)
        private val content: TextView = itemView.findViewById(R.id.postContent)
        private val timestamp: TextView = itemView.findViewById(R.id.postTimestamp)
        private val userName: TextView = itemView.findViewById(R.id.postUserName)
        private val pageName: TextView = itemView.findViewById(R.id.postPageName)
        private val category: TextView = itemView.findViewById(R.id.postCategory)

        fun bind(post: Post) {
            title.text = post.title
            content.text = post.content
            timestamp.text = "Publicado el: ${post.timestamp}"
            userName.text = "Publicado por: ${post.userName}"
            pageName.text = "${post.pageName}"
            category.text = "${post.category}"
        }
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombre: TextView = itemView.findViewById(R.id.eventName)
        private val descripcion: TextView = itemView.findViewById(R.id.eventDescription)
        private val fechas: TextView = itemView.findViewById(R.id.eventDates)
        private val ubicacion: TextView = itemView.findViewById(R.id.eventLocation)
        private val userName: TextView = itemView.findViewById(R.id.eventUserName)

        fun bind(evento: Evento) {
            nombre.text = evento.nombre
            descripcion.text = evento.descripcion
            val fechaInicio = evento.fechaInicio
            val fechaFin = evento.fechaFin
            fechas.text = "Inicia: $fechaInicio - Termina: $fechaFin"
            ubicacion.text = "Ubicación: ${evento.ubicacion}"
            userName.text = "Publicado por: ${evento.userName}"
        }
    }

    class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pageName: TextView = itemView.findViewById(R.id.linkName)
        private val pageUrl: TextView = itemView.findViewById(R.id.linkUrl)
        private val pageCategory: TextView = itemView.findViewById(R.id.linkDescriptionOrCategory)
        private val linkButton: Button = itemView.findViewById(R.id.linkUrl)

        fun bind(page: Pagina) {
            pageName.text = page.nombrePagina
            pageUrl.text = "URL: ${page.urlPagina}"
            pageCategory.text = "Categoría: ${page.categoria}"

            linkButton.text = "Facebook"
            linkButton.tag = page.urlPagina
            linkButton.backgroundTintList = itemView.context.getColorStateList(R.color.FB)
        }
    }

    class EnlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val enlaceName: TextView = itemView.findViewById(R.id.linkName)
        private val enlaceUrl: TextView = itemView.findViewById(R.id.linkUrl)
        private val enlaceDescription: TextView = itemView.findViewById(R.id.linkDescriptionOrCategory)
        private val linkButton: Button = itemView.findViewById(R.id.linkUrl)

        fun bind(enlace: Enlace) {
            enlaceName.text = enlace.nombre
            enlaceUrl.text = "URL: ${enlace.url}"
            enlaceDescription.text = "Descripción: ${enlace.descripcion}"

            linkButton.text = "Abrir Enlace"
            linkButton.tag = enlace.url
            linkButton.backgroundTintList = itemView.context.getColorStateList(R.color.URL)
        }
    }

}
