package com.example.microredesocial.model

import java.util.Date

data class Post(
    val id: String = "",
    val autorEmail: String = "",
    val autorUsername: String = "",
    val autorFoto: String = "",
    val texto: String = "",
    val imagemBase64: String = "",
    val cidade: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val dataCriacao: Date = Date()
)