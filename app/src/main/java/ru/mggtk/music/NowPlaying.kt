package ru.mggtk.music

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.mggtk.music.databinding.FragmentNowPlayingBinding

class NowPlaying : Fragment() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.startBtn2.setOnClickListener {
            if (PlayerActivity.isPlaying) pauseTrack()
            else playTrack()
        }
        binding.nextBtn2.setOnClickListener {
            setTrackPosition(i = true)
            PlayerActivity.musicService!!.createPlayer()
            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.trackPosition].artUri).apply(
                RequestOptions().placeholder(R.drawable.vinyl)
                    .centerCrop()).into(binding.trackCover3)
            binding.trackTitle3.text = PlayerActivity.musicListPA[PlayerActivity.trackPosition].title
            PlayerActivity.musicService!!.showNotification(R.drawable.pause, 1F)
            playTrack()
        }
        binding.root.setOnClickListener{
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            intent.putExtra("index", PlayerActivity.trackPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if (PlayerActivity.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.trackTitle3.isSelected = true
            Glide.with(this).load(PlayerActivity.musicListPA[PlayerActivity.trackPosition].artUri).apply(
                RequestOptions().placeholder(R.drawable.vinyl)
                .centerCrop()).into(binding.trackCover3)
            binding.trackTitle3.text = PlayerActivity.musicListPA[PlayerActivity.trackPosition].title
            if (PlayerActivity.isPlaying) binding.startBtn2.setIconResource(R.drawable.pause)
            else binding.startBtn2.setIconResource(R.drawable.start)
        }
    }
    private fun playTrack(){
        PlayerActivity.musicService!!.mediaPlayer!!.start()
        binding.startBtn2.setIconResource(R.drawable.pause)
        PlayerActivity.musicService!!.showNotification(R.drawable.pause, 1F)
        PlayerActivity.binding.nextBtn.setIconResource(R.drawable.pause)
        PlayerActivity.isPlaying = true
    }
    private fun pauseTrack(){
        PlayerActivity.musicService!!.mediaPlayer!!.pause()
        binding.startBtn2.setIconResource(R.drawable.start)
        PlayerActivity.musicService!!.showNotification(R.drawable.start, 0F)
        PlayerActivity.binding.nextBtn.setIconResource(R.drawable.start)
        PlayerActivity.isPlaying = false
    }
}