package com.example.ugposts

data class Pagina(
    val id: Int,
    val nombrePagina: String,
    val urlPagina: String,
    val categoria: String
) : Postable {
    override val type: PostableType = PostableType.PAGINA
}
