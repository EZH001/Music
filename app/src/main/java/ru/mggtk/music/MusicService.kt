package ru.mggtk.music

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.media.Session2Command
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.app.NotificationCompat

class MusicService: Service() {
    private val myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "Music")
        return myBinder
    }
    inner class MyBinder: Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }
    fun showNotification(){
        val prevIntent: Intent = Intent(baseContext, NotificatioonRecevier::class.java).setAction(AppClass.PREV)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,  0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)




        val notification: Notification = androidx.core.app.NotificationCompat.Builder(baseContext, AppClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.trackPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.trackPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.vinyl))
            .setStyle(NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.prev, "Prev", null)
            .addAction(R.drawable.start, "Start", null)
            .addAction(R.drawable.next, "Next", null)
            .addAction(R.drawable.exit, "Exit", null)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val playbackSpeed = if (PlayerActivity.isPlaying) 1F else 0f
            mediaSession.setMetadata(MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong())
                .build())
            val playBackState = PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                .build()
            mediaSession.setPlaybackState(playBackState)
        }
            startForeground(13, notification)
    }
}