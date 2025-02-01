package com.example.noteai.domain.repository

import com.example.noteai.domain.entity.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun getAllNotes(): Flow<List<Note>>

    suspend fun getNote(noteId: Int): Note?

    suspend fun addNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(noteId: Int)
}