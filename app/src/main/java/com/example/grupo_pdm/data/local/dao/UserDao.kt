package com.example.grupo_pdm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.grupo_pdm.data.local.entity.UserEntity
import com.example.grupo_pdm.data.local.entity.UserPicture
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Insere/atualiza o utilizador (REPLACE substitui se o id já existir)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: UserEntity)

    // Observa um utilizador (útil para UI reagir a alterações)
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeUser(id: Int): Flow<UserEntity?>

    // Busca um utilizador uma vez (sem Flow)
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    // Apaga um utilizador
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Int)

    // Guarda/atualiza a picture do user (1 por userId)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPicture(pic: UserPicture)

    // Observa a picture do user
    @Query("SELECT * FROM user_picture WHERE userId = :userId LIMIT 1")
    fun observePicture(userId: Int): Flow<UserPicture?>

    // Remove a picture local do user (equivalente ao "delete own pic" local)
    @Query("DELETE FROM user_picture WHERE userId = :userId")
    suspend fun deletePicture(userId: Int)

    // Guardar user + picture numa transação (útil após register)
    @Transaction
    suspend fun saveUserWithPicture(user: UserEntity, picture: UserPicture?) {
        upsertUser(user)
        if (picture != null) {
            upsertPicture(picture.copy(userId = user.id))
        }
    }
}