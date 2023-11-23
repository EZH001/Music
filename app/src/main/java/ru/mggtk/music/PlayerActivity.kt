 package ru.mggtk.music

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.mggtk.music.databinding.ActivityPlayerBinding

 class PlayerActivity : AppCompatActivity(), ServiceConnection {
     companion object{
         lateinit var musicListPA: ArrayList<Music>
         var trackPosition: Int = 0
         var isPlaying: Boolean = false
         var musicService: MusicService? = null
     }
    private lateinit var binding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent: Intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        startService(intent)
        initLayout()
        binding.startBtn.setOnClickListener{
            if (isPlaying) pauseTrack()
            else playTrack()
        }
        binding.prevBtn.setOnClickListener { choiceTrack(i = false) }
        binding.nextBtn.setOnClickListener { choiceTrack(i = true) }


    }
     fun setCover(){
         Glide.with(this).load(musicListPA[trackPosition].artUri).apply(RequestOptions().placeholder(R.drawable.vinyl)
             .centerCrop()).into(binding.trackCover2)
         binding.trackTitle2.text = musicListPA[trackPosition].title
     }
     fun createPlayer(){
         try {
             if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
             musicService!!.mediaPlayer!!.reset()
             musicService!!.mediaPlayer!!.setDataSource(musicListPA[trackPosition].path)
             musicService!!.mediaPlayer!!.prepare()
             musicService!!.mediaPlayer!!.start()
             isPlaying = true
             binding.startBtn.setIconResource(R.drawable.pause)
         } catch (e: Exception){return}
     }
     fun initLayout(){
         trackPosition = intent.getIntExtra("index", 0)
         when(intent.getStringExtra("class")){
             "MusicAdapter" -> {
                 musicListPA = ArrayList()
                 musicListPA.addAll(MainActivity.MusicListMA)
                 setCover()

             }
             "MainActivity" -> {
                 musicListPA = ArrayList()
                 musicListPA.addAll(MainActivity.MusicListMA)
                 musicListPA.shuffle()
                 setCover()

             }
         }
     }
     fun playTrack(){
         binding.startBtn.setIconResource(R.drawable.pause)
         isPlaying = true
         musicService!!.mediaPlayer!!.start()
     }
     fun pauseTrack(){
         binding.startBtn.setIconResource(R.drawable.start)
         isPlaying = false
         musicService!!.mediaPlayer!!.pause()
     }

     private fun choiceTrack(i: Boolean){
         if(i)
         {
             setTrackPosition(i = true)
             setCover()
             createPlayer()
         }
         else{
             setTrackPosition(i = true)
             setCover()
             createPlayer()
         }
     }
     fun setTrackPosition(i: Boolean){
         if (i){
            if (musicListPA.size - 1 == trackPosition)
                trackPosition = 0
             else ++trackPosition
         }
         else{
             if (0 == trackPosition)
                 trackPosition = musicListPA.size - 1
             else --trackPosition
         }
     }

     override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
         val binder = service as MusicService.MyBinder
         musicService = binder.currentService()
         createPlayer()
     }

     override fun onServiceDisconnected(name: ComponentName?) {
         musicService = null
     }
 }
 //    var status: Boolean = false
//    fun btnStart(view: View){
//        val ib: ImageButton = findViewById(R.id.imageButton2)
//        if (status == false){
//            var anim = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_anim)
//            ib.setImageDrawable(anim)
//            anim?.start()
//            status = true
//        }
//        else {
//            var anim = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_anim2)
//            ib.setImageDrawable(anim)
//            anim?.start()
//            status = false
//        }
 //}
