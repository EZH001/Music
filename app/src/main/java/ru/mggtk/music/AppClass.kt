package ru.mggtk.music

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

class AppClass: Application() {
    companion object{
        const val CHANNEL_ID = "channel"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREV = "prev"
        const val EXIT = "exit"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val notificationChannel = NotificationChannel(CHANNEL_ID, "Playing_Song", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.description = "Это важное окно для показа песни"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
    }}
}