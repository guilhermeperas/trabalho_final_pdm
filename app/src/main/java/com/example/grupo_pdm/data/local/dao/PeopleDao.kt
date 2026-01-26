package com.example.grupo_pdm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.grupo_pdm.data.local.entity.PeopleEntity
import com.example.grupo_pdm.data.local.entity.PersonPicture
import kotlinx.coroutines.flow.Flow

@Dao
interface PeopleDao {
    // Insere/atualiza uma pessoa
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPerson(person: PeopleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPeople(people: List<PeopleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPictures(pictures: List<PersonPicture>)

    // Apaga as pictures de uma pessoa (útil antes de inserir as novas do GET)
    @Query("DELETE FROM person_pictures WHERE personId = :personId")
    suspend fun clearPictures(personId: Int)

    // Observa uma pessoa pelo id
    @Query("SELECT * FROM people WHERE id = :id LIMIT 1")
    fun observePerson(id: Int): Flow<PeopleEntity?>

    // Observa as pictures de uma pessoa (main first)
    @Query("SELECT * FROM person_pictures WHERE personId = :personId ORDER BY mainPicture DESC, id ASC")
    fun observePictures(personId: Int): Flow<List<PersonPicture>>

    // Ler uma pessoa (1x)
    @Query("SELECT * FROM people WHERE id = :id LIMIT 1")
    suspend fun getPersonById(id: Int): PeopleEntity?

    @Transaction
    suspend fun savePersonWithPictures(person: PeopleEntity, pictures: List<PersonPicture>) {
        upsertPerson(person)
        clearPictures(person.id)
        upsertPictures(pictures.map { it.copy(personId = person.id) })
    }
}