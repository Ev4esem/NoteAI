package com.example.noteai.data.repository

import android.content.Context
import com.example.noteai.data.local.db.NoteDao
import com.example.noteai.data.local.getUnsentAudioPath
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.data.service.AudioRecordingService
import com.example.noteai.domain.entity.AudioResponse
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class NoteRepositoryImpl : NoteRepository, KoinComponent {

    private val noteDao by inject<NoteDao>()
    private val audioRecordingService by inject<AudioRecordingService>()
    private val context by inject<Context>()
    private val okHttp by inject<OkHttpClient>()

    override fun uploadAudio(): Flow<String> = flow {
        val file = audioRecordingService.getCurrentAudio()
            ?: return@flow

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8005/upload/audio")
            .post(requestBody)
            .build()

        val response = okHttp.newCall(request).execute()
        val jsonResponse = response.body?.string() ?: "Не тот текст"

        val audioResponse = Gson().fromJson(jsonResponse, AudioResponse::class.java)
        emit(audioResponse.message)
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

    override suspend fun getAllNotes(): Flow<List<Note>> {
        val notes = noteDao.getAllNotes().map { notes ->
            notes.map { it.toDomain() }
        }
        return notes
    }

    override suspend fun getNoteById(noteId: Long): Note? {
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

    override suspend fun deleteNote(noteId: Long) {
        noteDao.deleteNote(noteId)
    }
}
