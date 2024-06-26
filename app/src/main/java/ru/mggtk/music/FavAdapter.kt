package ru.mggtk.music

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ru.mggtk.music.databinding.FavViewBinding

class FavAdapter(private val context: Context, private var musicList: ArrayList<Music>)
    : RecyclerView.Adapter<FavAdapter.MyHolder>() {

    class MyHolder(binding: FavViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.trackCoverFav
        val title = binding.trackTitleFav
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FavViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
holder.title.text = musicList[position].title
        Glide.with(context).load(musicList[position].artUri).apply(
            RequestOptions().placeholder(R.drawable.vinyl)
            .centerCrop()).into(holder.image)
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "FavAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
}