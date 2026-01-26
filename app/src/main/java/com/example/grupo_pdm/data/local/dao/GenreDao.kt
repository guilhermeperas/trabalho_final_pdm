package com.example.grupo_pdm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.grupo_pdm.data.local.entity.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    // Insere vários géneros de uma vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll (genres: List<GenreEntity>)

    // Insere um único género.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (genre: GenreEntity)

    // Devolve todos os géneros ordenados por nome.
    @Query("SELECT * FROM genres ORDER BY name")
    fun getAll (): Flow<List<GenreEntity>>

    // Procura um género pelo id.
    @Query("SELECT * FROM genres WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): GenreEntity?

    // Apaga todos os registos da tabela "genres".
    @Query("DELETE FROM genres")
    suspend fun deleteAll ()

    // Apaga um registo da tabela "genres" pelo id
    @Query("DELETE FROM genres WHERE id = :id")
    suspend fun deleteById(id: Int)

}