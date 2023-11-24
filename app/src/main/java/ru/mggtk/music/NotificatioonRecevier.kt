package ru.mggtk.music

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class NotificatioonRecevier: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action){
            AppClass.PREV -> prevNextTrack(i = false, context = context!!)
            AppClass.PLAY -> if(PlayerActivity.isPlaying) pauseTrack() else playTrack()
            AppClass.NEXT -> prevNextTrack(i = true, context = context!!)
            AppClass.EXIT -> {
                exitApp()
            }
        }
    }
    //старт
    private fun playTrack(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause, 1F)
        PlayerActivity.binding.startBtn.setIconResource(R.drawable.pause)
        NowPlaying.binding.startBtn2.setIconResource(R.drawable.pause)
    }

    //пауза
    private fun pauseTrack(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        PlayerActivity.musicService!!.showNotification(R.drawable.start, 0F)
        PlayerActivity.binding.startBtn.setIconResource(R.drawable.start)
        NowPlaying.binding.startBtn2.setIconResource(R.drawable.start)
    }

    private fun prevNextTrack(i: Boolean, context: Context){
        setTrackPosition(i = i)
        PlayerActivity.musicService!!.createPlayer()
        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.trackPosition].artUri).apply(
            RequestOptions().placeholder(R.drawable.vinyl)
            .centerCrop()).into(PlayerActivity.binding.trackCover2)
        PlayerActivity.binding.trackTitle2.text = PlayerActivity.musicListPA[PlayerActivity.trackPosition].title
        Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.trackPosition].artUri).apply(
            RequestOptions().placeholder(R.drawable.vinyl)
                .centerCrop()).into(NowPlaying.binding.trackCover3)
        NowPlaying.binding.trackTitle3.text = PlayerActivity.musicListPA[PlayerActivity.trackPosition].title
        playTrack()
        PlayerActivity.fIndex = favCheck(PlayerActivity.musicListPA[PlayerActivity.trackPosition].id)
        if (PlayerActivity.isFav) PlayerActivity.binding.favBtn.setImageResource(R.drawable.heart_red)
        else PlayerActivity.binding.favBtn.setImageResource(R.drawable.heart)
    }
}