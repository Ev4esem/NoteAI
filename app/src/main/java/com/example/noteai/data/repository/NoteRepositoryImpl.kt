package com.example.noteai.data.repository

import android.content.Context
import android.util.Log
import com.example.noteai.data.local.db.NoteDao
import com.example.noteai.data.local.getUnsentAudioPath
import com.example.noteai.data.mapper.toDomain
import com.example.noteai.domain.entity.Note
import com.example.noteai.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.IOException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

class NoteRepositoryImpl : NoteRepository, KoinComponent {

    private val noteDao by inject<NoteDao>()
    private val audioRecordingService by inject<AudioRecordingService>()
    private val context by inject<Context>()
    private val okHttp by inject<OkHttpClient>()

    override suspend fun uploadAudio() {
        // TODO Сделать обработку ошибки https://github.com/Ev4esem/NoteAI/issues/5
        val file = audioRecordingService.getCurrentAudio() ?: throw IllegalArgumentException("Audio file didn't found")
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                file.name,
                RequestBody.create("audio/mpeg".toMediaTypeOrNull(), file)
        ).build()

        val request = Request.Builder()
            .url("http://10.0.2.2:8005/upload/audio")
            .post(requestBody)
            .build()

        okHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("NoteRepositoryImpl","Ошибка: ${e.message}")
            }

            override fun onResponse(call: Call, response: okhttp3.Response) {
                Log.d("NoteRepositoryImpl","\"Ответ: ${response.request.body.toString()}\"")
            }
        }
        )
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


