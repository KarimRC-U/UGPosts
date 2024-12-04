package com.example.ugposts

data class Evento(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val fechaInicio: String,
    val fechaFin: String?,
    val ubicacion: String,
    val userName: String
) : Postable {
    override val type: PostableType = PostableType.EVENT
}
