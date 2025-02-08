package com.example.noteai.data.repository

import android.content.Context
import com.example.noteai.data.local.clearUnsentAudioPath
import com.example.noteai.data.local.db.NoteDao
import com.example.noteai.data.local.getUnsentAudioPath
import com.example.noteai.data.local.saveUnsentAudioPath
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.data.network.AudioApiService
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import com.example.noteai.utils.Response
import com.example.noteai.utils.handlerError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class NoteRepositoryImpl : NoteRepository, KoinComponent {

    private val noteDao by inject<NoteDao>()
    private val audioRecordingService by inject<AudioRecordingService>()
    private val context by inject<Context>()
    private val apiService by inject<AudioApiService>()

    override suspend fun uploadAudio(): Flow<Response> = flow {
        // TODO Сделать обработку ошибки https://github.com/Ev4esem/NoteAI/issues/5
        val file = audioRecordingService.getCurrentAudio() ?: throw IllegalArgumentException("Audio file didn't found")
        apiService.uploadAudio(file.toMultipartBody())
            .onEach {
                emit(Response.Loading)
            }
            .catch { e ->
                saveUnsentAudioPath(context, file.absolutePath)
                emit(Response.Error(handlerError(e)))
            }
            .onCompletion {
                clearUnsentAudioPath(context)
                Response.Success(Unit)
            }
    }

    override fun startRecording(outputFile: File) {
        audioRecordingService.startRecording(outputFile)
    }

    override fun stopRecording() {
        audioRecordingService.stopRecording()
    }

    override fun getPendingAudio(): File? {
        return getUnsentAudioPath(context)?.let { File(it) }
    }

    override fun getCurrentAudioFile(): File? {
        return audioRecordingService.getCurrentAudio()
    }

    // TODO Добавить константы https://github.com/Ev4esem/NoteAI/issues/5
    private fun File.toMultipartBody(): MultipartBody.Part {
        val requestBody: RequestBody = RequestBody.create(
            okhttp3.MediaType.parse("audio/*"), this
        )
        return MultipartBody.Part.createFormData("file", this.name, requestBody)
    }

    override suspend fun getAllNotes(): Flow<List<Note>> {
        val notes = noteDao.getAllNotes().map { notes ->
            notes.map { note ->
                note.toDomain()
            }
        }
        return notes
    }

    override suspend fun getNoteById(noteId: Long): Note? {
       return noteDao.getNoteById(noteId)
    }

    override suspend fun addNote(note: Note) {
        noteDao.addNote(note)
    }

    override suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    override suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNote(noteId)
    }
}


