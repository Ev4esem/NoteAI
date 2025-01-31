package com.example.noteai.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.noteai.data.local.model.NoteDbModel

@Database(entities = [NoteDbModel::class], version = 1, exportSchema = false)
abstract class FavouriteDataBase : RoomDatabase() {

    abstract fun favouriteNotesDao(): FavouriteNotesDao

    companion object {

        private const val DB_NAME = "FavouriteDatabase"
        private var INSTANCE: FavouriteDataBase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): FavouriteDataBase {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }

                val database = Room.databaseBuilder(
                    context = context,
                    klass = FavouriteDataBase::class.java,
                    name = DB_NAME
                ).build()

                INSTANCE = database
                return database
            }
        }
    }
}