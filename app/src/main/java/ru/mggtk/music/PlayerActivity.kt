 package ru.mggtk.music

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.audiofx.AudioEffect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore.Audio
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import ru.mggtk.music.databinding.ActivityPlayerBinding
import kotlin.system.exitProcess

 class PlayerActivity : AppCompatActivity(), ServiceConnection, OnCompletionListener {
     companion object{
         lateinit var musicListPA: ArrayList<Music>
         var trackPosition: Int = 0
         var isPlaying: Boolean = false
         var musicService: MusicService? = null
         @SuppressLint("StaticFieldLeak")
         lateinit var binding: ActivityPlayerBinding
         var repeat: Boolean = false
         var min15: Boolean = false
         var min30: Boolean = false
         var min60: Boolean = false
         var isFav: Boolean = false
         var fIndex: Int = -1
     }

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initLayout()
        binding.backBtn.setOnClickListener{
            finish()
        }
        binding.startBtn.setOnClickListener{
            if (isPlaying) pauseTrack()
            else playTrack()
        }
        //выбор трека(назад, вперед)
        binding.prevBtn.setOnClickListener { choiceTrack(i = false) }
        binding.nextBtn.setOnClickListener { choiceTrack(i = true) }
        //Позволяет перематывать трек с помощью нажатия на SeekBar
        binding.seekBar1.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) musicService!!.mediaPlayer!!.seekTo(progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        //Кнопка повтора
        binding.repeatBtnPA.setOnClickListener {
            if (!repeat){
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.for_btn))
            }
            else {
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.md_theme_light_outline))
            }
        }
        //Эквалайзер
        binding.equalizerBtnPA.setOnClickListener {
            try {
                val equIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                equIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mediaPlayer!!.audioSessionId)
                equIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
                equIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(equIntent, 1)
            }
            catch (e: Exception){
                Toast.makeText(this, "Функция эквалайзера не поддерживается", Toast.LENGTH_SHORT).show()
            }
        }
        binding.timerBtnPA.setOnClickListener {
            val timer = min15 || min30 || min60
            if(!timer) showBottomTab()
            else {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("Выключить таймер")
                    .setMessage("Хотите выключить таймер?")
                    .setPositiveButton("Да"){ _, _ ->
                        min15 = false
                        min30 = false
                        min60 = false
                        binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.md_theme_light_outline))
                    }
                    .setNegativeButton("Нет"){dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
            }
        }
        binding.favBtn.setOnClickListener {
            if (isFav){
                isFav = false
                binding.favBtn.setImageResource(R.drawable.heart)
                FavouriteActivity.favTracks.removeAt(fIndex)
            }
            else{
                isFav = true
                binding.favBtn.setImageResource(R.drawable.heart_red)
                FavouriteActivity.favTracks.add(musicListPA[trackPosition])
            }
        }
    }
     //задает такую же обложку у треков, как ту, которая стоит в файловой системе
     private fun setCover(){
         fIndex = favCheck(musicListPA[trackPosition].id)
         Glide.with(this).load(musicListPA[trackPosition].artUri).apply(RequestOptions().placeholder(R.drawable.vinyl)
             .centerCrop()).into(binding.trackCover2)
         binding.trackTitle2.text = musicListPA[trackPosition].title
         if (repeat) binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.for_btn))
         if (min15 || min30 || min60) binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.for_btn))
         if (isFav) binding.favBtn.setImageResource(R.drawable.heart_red)
         else binding.favBtn.setImageResource(R.drawable.heart)
     }
     //Плеер
     private fun createPlayer(){
         try {
             if (musicService!!.mediaPlayer == null) musicService!!.mediaPlayer = MediaPlayer()
             musicService!!.mediaPlayer!!.reset()
             musicService!!.mediaPlayer!!.setDataSource(musicListPA[trackPosition].path)
             musicService!!.mediaPlayer!!.prepare()
             musicService!!.mediaPlayer!!.start()
             isPlaying = true
             binding.startBtn.setIconResource(R.drawable.pause)
             musicService!!.showNotification(R.drawable.pause, 1F)
             binding.durStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
             binding.durEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
             binding.seekBar1.progress = 0
             binding.seekBar1.max = musicService!!.mediaPlayer!!.duration
             musicService!!.mediaPlayer!!.setOnCompletionListener(this)
         } catch (e: Exception){return}
     }
     private fun initLayout(){
         trackPosition = intent.getIntExtra("index", 0)
         when(intent.getStringExtra("class")){
             "FavMix" -> {
                 val intent = Intent(this, MusicService::class.java)
                 bindService(intent, this, BIND_AUTO_CREATE)
                 startService(intent)
                 musicListPA = ArrayList()
                 musicListPA.addAll(FavouriteActivity.favTracks)
                 musicListPA.shuffle()
                 setCover()
             }
             "FavAdapter" -> {
                 val intent = Intent(this, MusicService::class.java)
                 bindService(intent, this, BIND_AUTO_CREATE)
                 startService(intent)
                 musicListPA = ArrayList()
                 musicListPA.addAll(FavouriteActivity.favTracks)
                 setCover()
             }
             "NowPlaying" -> {
                 setCover()
                 binding.durStart.text = formatDuration(musicService!!.mediaPlayer!!.currentPosition.toLong())
                 binding.durEnd.text = formatDuration(musicService!!.mediaPlayer!!.duration.toLong())
                 binding.seekBar1.progress = musicService!!.mediaPlayer!!.currentPosition
                 binding.seekBar1.max = musicService!!.mediaPlayer!!.duration
             }
             "MusicAdapter" -> {
                 //при запуске вызывается сервис
                 val intent = Intent(this, MusicService::class.java)
                 bindService(intent, this, BIND_AUTO_CREATE)
                 startService(intent)
                 musicListPA = ArrayList()
                 musicListPA.addAll(MainActivity.MusicListMA)
                 setCover()

             }
             "MainActivity" -> {
                 val intent = Intent(this, MusicService::class.java)
                 bindService(intent, this, BIND_AUTO_CREATE)
                 startService(intent)
                 musicListPA = ArrayList()
                 musicListPA.addAll(MainActivity.MusicListMA)
                 musicListPA.shuffle()
                 setCover()

             }
         }
     }
     //Старт
     private fun playTrack(){
         binding.startBtn.setIconResource(R.drawable.pause)
         musicService!!.showNotification(R.drawable.pause, 1F)
         isPlaying = true
         musicService!!.mediaPlayer!!.start()
     }
     //Пауза
     private fun pauseTrack(){
         binding.startBtn.setIconResource(R.drawable.start)
         musicService!!.showNotification(R.drawable.start, 0F)
         isPlaying = false
         musicService!!.mediaPlayer!!.pause()
     }
        //Выбор трека
     private fun choiceTrack(i: Boolean){
         if(i)
         {
             setTrackPosition(i = true)
             setCover()
             createPlayer()
         }
         else{
             setTrackPosition(i = false)
             setCover()
             createPlayer()
         }
     }

    //Подключение и отключени сервиса
     override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
         val binder = service as MusicService.MyBinder
         musicService = binder.currentService()
         createPlayer()
        musicService!!.seekBarSet()

     }

     override fun onServiceDisconnected(name: ComponentName?) {
         musicService = null
     }

     //Когда песня заканчивается, то начнется другая. Без этой функции, после завершения песни будет тишина
     override fun onCompletion(mp: MediaPlayer?) {
         setTrackPosition(i = true)
         createPlayer()
         try {
             setCover()
         }
         catch (e: Exception){
             return
         }
     }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == 1 || resultCode == RESULT_OK)
             return
     }
     //Показать нижнее окно с таймером сна
     private fun showBottomTab(){
         val dialog = BottomSheetDialog(this)
         dialog.setContentView(R.layout.bottom_tab)
         dialog.show()
         dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
             Toast.makeText(this, "Музыка выключится через 15 минут", Toast.LENGTH_SHORT).show()
             binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.for_btn))
             min15 = true
             Thread{Thread.sleep(15 * 60000)
             if (min15) exitApp()}.start()
             dialog.dismiss()
         }
         dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
             Toast.makeText(this, "Музыка выключится через 30 минут", Toast.LENGTH_SHORT).show()
             binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.for_btn))
             min30 = true
             Thread{Thread.sleep(30 * 60000)
                 if (min30) exitApp()}.start()
             dialog.dismiss()
         }
         dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
             Toast.makeText(this, "Музыка выключится через 60 минут", Toast.LENGTH_SHORT).show()
             binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.for_btn))
             min60 = true
             Thread{Thread.sleep(60 * 60000)
                 if (min60) exitApp()}.start()
             dialog.dismiss()
         }
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
