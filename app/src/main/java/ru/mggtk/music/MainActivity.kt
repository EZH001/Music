package ru.mggtk.music

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_old)
    }
    var status: Boolean = false
    fun btnStart(view: View){
        val ib: ImageButton = findViewById(R.id.imageButton2)
        if (status == false){
            var anim = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_anim)
            ib.setImageDrawable(anim)
            anim?.start()
            status = true
        }
        else {
            var anim = AnimatedVectorDrawableCompat.create(this, R.drawable.avd_anim2)
            ib.setImageDrawable(anim)
            anim?.start()
            status = false
        }
    }
}