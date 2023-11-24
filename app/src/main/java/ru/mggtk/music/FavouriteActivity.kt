package ru.mggtk.music

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ru.mggtk.music.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityFavouriteBinding
    private lateinit var  adapter: FavAdapter
    companion object{
        var favTracks: ArrayList<Music> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtnFav.setOnClickListener {
            finish()
        }
        binding.trackListFav.setHasFixedSize(true)
        //Устанавливает количество закадровых представлений, которые необходимо сохранить
        binding.trackListFav.setItemViewCacheSize(13)
        //Для размещения своих дочерних элементов в RecycleView
        binding.trackListFav.layoutManager = GridLayoutManager(this@FavouriteActivity, 4)
        adapter = FavAdapter(this@FavouriteActivity, favTracks)
        binding.trackListFav.adapter = adapter
        if (favTracks.size < 1) binding.mixBtn2.visibility = View.INVISIBLE
        binding.mixBtn2.setOnClickListener {
            val intent: Intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "FavMix")
            startActivity(intent)
        }
    }
}