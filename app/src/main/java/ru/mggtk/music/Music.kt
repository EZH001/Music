package ru.mggtk.music

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(val id: String, val title: String, val album: String, val artist: String, val duration: Long = 0,  val path: String, val artUri: String)
//Используется для формата длительности трека
fun formatDuration(duration: Long):String{
    val min = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val sec = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) - min * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", min, sec)
}
//Возвращает обложку трека
fun getImageTrack(path: String): ByteArray?{
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}
//Смена трека
fun setTrackPosition(i: Boolean){
    if (!PlayerActivity.repeat){
        if (i){
            if (PlayerActivity.musicListPA.size - 1 == PlayerActivity.trackPosition)
                PlayerActivity.trackPosition = 0
            else ++PlayerActivity.trackPosition
        }
        else{
            if (0 == PlayerActivity.trackPosition)
                PlayerActivity.trackPosition = PlayerActivity.musicListPA.size - 1
            else --PlayerActivity.trackPosition
        }
    }}
    fun exitApp(){
        if (!PlayerActivity.isPlaying && PlayerActivity.musicService != null){
            PlayerActivity.musicService!!.stopForeground(true)
            PlayerActivity.musicService!!.mediaPlayer!!.release()
            PlayerActivity.musicService = null
            exitProcess(1)
    }
}
    fun favCheck(id: String): Int{
        PlayerActivity.isFav = false
        FavouriteActivity.favTracks.forEachIndexed { index, music ->
            if (id == music.id){
             PlayerActivity.isFav = true
                return index
            }
        }
        return -1
    }
