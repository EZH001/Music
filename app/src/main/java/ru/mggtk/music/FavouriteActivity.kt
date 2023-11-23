package ru.mggtk.music

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.mggtk.music.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityFavouriteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)

        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}