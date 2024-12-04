package com.example.ugposts

data class Enlace(
    val id: Int,
    val nombre: String,
    val url: String,
    val descripcion: String?
) : Postable {
    override val type: PostableType = PostableType.ENLACE
}
