package com.example.grupo_pdm.data

/**
 * Data class for Person (Actor/Director).
 */
data class Person(
    val id: Int? = null,
    val name: String,
    val dateOfBirth: String? = null, // Format: "YYYY-MM-DD"
    val pictures: List<Picture>? = null
)
