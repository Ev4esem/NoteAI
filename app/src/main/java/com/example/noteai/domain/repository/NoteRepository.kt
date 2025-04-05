package com.example.noteai.domain.repository

import com.example.noteai.domain.entity.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import java.io.File

interface NoteRepository {

    suspend fun getAllNotes(): Flow<List<Note>>

    suspend fun getNoteById(noteId: Long): Note?

    suspend fun searchNotes(query: String): Flow<List<Note>>

    fun observeAmplitude(): SharedFlow<Int>

    fun uploadAudio(): Flow<String>

    fun startRecording(outputFile: File)

    fun stopRecording()

    fun getCurrentAudioFile(): File?

    suspend fun addNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(noteId: Long)
}