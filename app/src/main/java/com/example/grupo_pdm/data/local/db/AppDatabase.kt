package com.example.grupo_pdm.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.grupo_pdm.data.local.dao.GenreDao
import com.example.grupo_pdm.data.local.dao.MovieDao
import com.example.grupo_pdm.data.local.dao.PeopleDao
import com.example.grupo_pdm.data.local.dao.UserDao
import com.example.grupo_pdm.data.local.entity.GenreEntity
import com.example.grupo_pdm.data.local.entity.MovieCast
import com.example.grupo_pdm.data.local.entity.MovieEntity
import com.example.grupo_pdm.data.local.entity.MovieGenre
import com.example.grupo_pdm.data.local.entity.MoviePicture
import com.example.grupo_pdm.data.local.entity.PeopleEntity
import com.example.grupo_pdm.data.local.entity.PersonPicture
import com.example.grupo_pdm.data.local.entity.UserEntity
import com.example.grupo_pdm.data.local.entity.UserPicture

@Database(
    entities = [
        // Genres
        GenreEntity::class,

        // Movies
        MovieEntity::class,
        MovieGenre::class,
        MovieCast::class,
        MoviePicture::class,

        // People
        PeopleEntity::class,
        PersonPicture::class,

        // Users
        UserEntity::class,
        UserPicture::class
    ],
    //Sempre que o esquema da tabela do banco de dados mudar,
    //vai ser necessário aumentar o número da versão.
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // DAOs (cada um dá acesso às queries da respetiva tabela)
    abstract fun genreDao(): GenreDao
    abstract fun movieDao(): MovieDao
    abstract fun peopleDao(): PeopleDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Cria/retorna UMA instância única da BD (singleton)
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moviepdm.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}