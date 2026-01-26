package com.example.grupo_pdm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val username: String,

    val dateOfBirth: String? = null,

    val createdAt: String? = null,
    val updatedAt: String? = null
)
@Entity(tableName = "user_picture")
data class UserPicture(
    @PrimaryKey val userId: Int,
    val filename: String? = null,

    val dataBase64: String? = null
)
