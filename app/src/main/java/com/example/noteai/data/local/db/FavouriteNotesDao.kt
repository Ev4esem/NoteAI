package com.example.noteai.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteai.data.local.model.NoteDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteNotesDao {

    @Query("SELECT * FROM favourite_notes")
    fun getFavouriteNotes(): Flow<List<NoteDbModel>>

    @Query("SELECT EXISTS (SELECT * FROM favourite_notes WHERE id=:noteId)")
    fun observeIsFavourite(noteId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun addToFavourite(noteDbModel: NoteDbModel)

    @Query("DELETE FROM favourite_notes WHERE id=:noteId")
    suspend fun removeFromFavourite(noteId: Int)
}