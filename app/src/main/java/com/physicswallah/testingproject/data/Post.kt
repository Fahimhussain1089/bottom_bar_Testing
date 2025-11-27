package com.physicswallah.testingproject.data

import kotlinx.serialization.Serializable


@Serializable
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)