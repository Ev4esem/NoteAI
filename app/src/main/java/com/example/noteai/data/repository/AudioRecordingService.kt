package com.example.noteai.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileOutputStream

class AudioRecordingService : Service(), KoinComponent {

    private val context by inject<Context>()

    private var recorder: MediaRecorder? = null

    private var currentFile: File? = null

    private val _amplitudes = MutableSharedFlow<Int>(extraBufferCapacity = 64)
    val amplitudes: SharedFlow<Int> = _amplitudes.asSharedFlow()

    private var isRecording = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    fun startRecording(outputFile: File) {
        currentFile = outputFile
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(outputFile).fd)

            prepare()
            start()

            recorder = this
        }
        isRecording = true
        startAmplitudeUpdates()
    }

    private fun startAmplitudeUpdates()  {
        CoroutineScope(Dispatchers.IO).launch {
            while(isRecording) {
                val amp = recorder?.maxAmplitude ?: 0
                _amplitudes.tryEmit(amp)
                delay(30)
            }
        }
    }

    fun getCurrentAudio(): File? = currentFile

    fun stopRecording() {
        recorder?.stop()
        recorder?.reset()
        recorder = null
        isRecording = false
        stopSelf()
    }

    private fun createNotificationChannel() {
        val notificationManager =
            this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Recording in progress")
        .setSmallIcon(android.R.drawable.ic_media_play)
        .build()


    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val CHANNEL_ID = "audio_recording_channel"
        private const val CHANNEL_NAME = "Audio Recording"
        private const val NOTIFICATION_ID = 1

        fun newIntent(context: Context): Intent {
            return Intent(context, AudioRecordingService::class.java)
        }
    }
}
