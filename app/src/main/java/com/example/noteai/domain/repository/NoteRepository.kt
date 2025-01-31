package com.example.noteai.domain.repository

import com.example.noteai.domain.entity.Note

interface NoteRepository {

    suspend fun getAllNotes(): List<Note>
    suspend fun getNote(noteId: Int): Note?
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: Int)
}