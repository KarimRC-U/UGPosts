package com.example.ugposts

interface Postable {
    val type: PostableType
}

enum class PostableType {
    POST,
    EVENT,
    ENLACE,
    PAGINA
}