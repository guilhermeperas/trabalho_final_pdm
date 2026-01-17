package com.example.grupo_pdm.data

/**
 * Data class for Picture.
 * Used in Movies and People.
 */
data class Picture(
    val id: Int? = null,
    val filename: String,
    val mainPicture: Boolean = false,
    val description: String? = null,
    val data: String? = null // Base64 encoded image data
)
