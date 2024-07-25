package com.example.myapp

import java.io.Serializable

data class Movie(
    val name: String,
    val genre: String,
    val director: String,
    val company: String,
    var picture: String = R.drawable.no_picture.toString()
) : Serializable