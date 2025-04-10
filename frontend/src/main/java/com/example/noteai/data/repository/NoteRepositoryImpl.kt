package com.example.noteai.data.repository

import com.example.noteai.data.local.db.NoteDao
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.data.mapper.toStatusNote
import com.example.noteai.data.model.AudioResponse
import com.example.noteai.data.network.NoteApi
import com.example.noteai.data.service.AudioRecordingService
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.entity.StatusNote
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class NoteRepositoryImpl : NoteRepository, KoinComponent {

    private val noteDao by inject<NoteDao>()
    private val audioRecordingService by inject<AudioRecordingService>()
    private val noteApi by inject<NoteApi>()

    override fun uploadAudio(): Flow<AudioResponse> = flow {
        val file = audioRecordingService.getCurrentAudio()
            ?: return@flow

        val requestFile = file
            .asRequestBody("audio/mpeg".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Part.createFormData(
            name = "file",
            filename = file.name,
            body = requestFile
        )

        val response = noteApi.uploadAudio(multipartBody)
        emit(response)
    }

    override suspend fun getCurrentNotesStatus(): Flow<List<Note>> = flow {
        noteDao.getAllNotes()
            .map { notes ->
                notes
                    .map { it.toDomain() }
            }.collect { notes ->
                val waitNotes = notes.filter { it.status == StatusNote.WAIT }
                if (waitNotes.isEmpty()) {
                    emit(notes)
                    return@collect
                }
                val ids = waitNotes.map { it.id }
                val response = noteApi.getTasksByNoteIds(ids)
                val waitNotesMap = waitNotes.associateBy { it.id }

                for (task in response) {
                    val currentNote = waitNotesMap[task.audioId] ?: continue
                    if (currentNote.status != task.statusNote.toStatusNote()) {
                        val title = task.content.string
                            .lineSequence()
                            .firstOrNull()
                            ?.take(30) ?: ""
                        val updated = currentNote.copy(
                            title = title,
                            status = task.statusNote.toStatusNote(),
                            description = task.content.string,
                        )
                        noteDao.addNote(updated)
                    }
                }
                emit(notes)
            }
    }


    override fun startRecording(outputFile: File) {
        audioRecordingService.startRecording(outputFile)
    }

    override fun stopRecording() {
        audioRecordingService.stopRecording()
    }

    override fun getCurrentAudioFile(): File? {
        return audioRecordingService.getCurrentAudio()
    }

    override suspend fun getAllNotes(): Flow<List<Note>> {
        val notes = noteDao.getAllNotes().map { notes ->
            notes.map { it.toDomain() }
        }
        return notes
    }

    override suspend fun getNoteById(noteId: String): Note? {
        return noteDao.getNoteById(noteId)
    }

    override suspend fun searchNotes(query: String): Flow<List<Note>> {
        val notes = noteDao.searchNotes(query).map { notes ->
            notes.map { it.toDomain() }
        }
        return notes
    }

    override fun observeAmplitude(): SharedFlow<Int> {
        return audioRecordingService.amplitudes
    }

    override suspend fun addNote(note: Note) {
        noteDao.addNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    override suspend fun deleteNote(noteId: String) {
        noteDao.deleteNote(noteId)
    }
}
