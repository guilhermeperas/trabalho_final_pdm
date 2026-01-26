package com.example.grupo_pdm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "people")
data class PeopleEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val dateOfBirth: String? = null
)
@Entity(
    tableName = "person_pictures",
    primaryKeys = ["personId", "id"]
)
data class PersonPicture(
    val personId: Int,
    val id: Int,

    val mainPicture: Boolean,
    val filename: String,
    val contentType: String,
    val description: String? = null
)