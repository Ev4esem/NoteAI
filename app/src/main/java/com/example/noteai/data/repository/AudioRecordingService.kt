package com.example.noteai.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.noteai.domain.repository.AudioRecordingRepository
import java.io.File

class AudioRecordingService(
    private val context: Context
) : AudioRecordingRepository {

    private var recorder: MediaRecorder? = null

    private var currentFile: File? = null

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun startRecording(outputFile: File) {
        currentFile = outputFile
        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
        createNotification()
    }

    override fun stopRecording() {
        recorder?.stop()
        recorder?.reset()
        sendRecordingSavedBroadcast(currentFile?.absolutePath)
    }

    private fun createNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "audio_recording_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Audio Recording",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Recording in progress")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()

        startForegroundService(notification)
    }

    private fun startForegroundService(notification: Notification) {
        val intent = Intent(context, AudioRecordingService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        startForegroundService(notification)
    }

    private fun sendRecordingSavedBroadcast(filePath: String?) {
        context.sendBroadcast(Intent("com.example.noteai.ACTION_RECORDING_SAVED").apply {
            putExtra("EXTRA_FILE_PATH", filePath)
        })
    }
}
