package com.example.noteai.domain.repository

import com.example.noteai.domain.entity.Note
import com.example.noteai.utils.Response
import kotlinx.coroutines.flow.Flow
import java.io.File

interface NoteRepository {

    suspend fun getAllNotes(): Flow<List<Note>>

    suspend fun getNoteById(noteId: Long): Note?

    suspend fun uploadAudio()

    fun startRecording(outputFile: File)

    fun stopRecording()

    fun getPendingAudio(): File?

    // TODO Удалить https://github.com/Ev4esem/NoteAI/issues/6
    fun getCurrentAudioFile(): File?

    suspend fun addNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(noteId: Long)
}