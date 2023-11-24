package ru.mggtk.music

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.app.NotificationCompat

class MusicService: Service() {
    private val myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable
    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "Music")
        return myBinder
    }
    inner class MyBinder: Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }
    //используется для показа в шторке уведомлений
    fun showNotification(startBtn: Int, playbackSpeed: Float){
        val prevIntent: Intent = Intent(baseContext, NotificatioonRecevier::class.java).setAction(AppClass.PREV)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,  0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val startIntent: Intent = Intent(baseContext, NotificatioonRecevier::class.java).setAction(AppClass.PLAY )
        val startPendingIntent = PendingIntent.getBroadcast(baseContext,  0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent: Intent = Intent(baseContext, NotificatioonRecevier::class.java).setAction(AppClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,  0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent: Intent = Intent(baseContext, NotificatioonRecevier::class.java).setAction(AppClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,  0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val imageTrack = getImageTrack(PlayerActivity.musicListPA[PlayerActivity.trackPosition].path)
        val image = if (imageTrack != null){
            BitmapFactory.decodeByteArray(imageTrack, 0, imageTrack.size)
        }
        else{
            BitmapFactory.decodeResource(resources, R.drawable.vinyl)
        }
        val notification: Notification = androidx.core.app.NotificationCompat.Builder(baseContext, AppClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.trackPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.trackPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(image)
            .setStyle(NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.prev, "Prev", prevPendingIntent)
            .addAction(startBtn, "Start", startPendingIntent)
            .addAction(R.drawable.next, "Next", nextPendingIntent)
            .addAction(R.drawable.exit, "Exit", exitPendingIntent)
            .build()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val playbackSpeed = if(PlayerActivity.isPlaying) 1F else 0F
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

     fun createPlayer(){
        try {
            if (PlayerActivity.musicService!!.mediaPlayer == null) PlayerActivity.musicService!!.mediaPlayer = MediaPlayer()
            PlayerActivity.musicService!!.mediaPlayer!!.reset()
            PlayerActivity.musicService!!.mediaPlayer!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.trackPosition].path)
            PlayerActivity.musicService!!.mediaPlayer!!.prepare()
            PlayerActivity.binding.startBtn.setIconResource(R.drawable.pause)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause, 0F)
            PlayerActivity.binding.durStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.durEnd.text = formatDuration(mediaPlayer!!.duration.toLong())
            PlayerActivity.binding.seekBar1.progress = 0
            PlayerActivity.binding.seekBar1.max = mediaPlayer!!.duration
        } catch (e: Exception){return}
    }
    //Управляет прогрессом SeekBar и меняет значение времени у трека соответственно
    fun seekBarSet(){
        runnable = Runnable {
            PlayerActivity.binding.durStart.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.seekBar1.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}