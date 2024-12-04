package com.example.ugposts

data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: String,
    val userName: String,
    val pageName: String,
    val category: String
) : Postable {
    override val type: PostableType = PostableType.POST
}