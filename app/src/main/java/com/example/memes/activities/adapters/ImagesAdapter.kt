package com.example.memes.activities.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.memes.R
import com.example.memes.activities.activities.DisplayImageActivity
import com.example.memes.activities.activities.MainActivity
import com.example.memes.activities.data.MemWithBitmap

class ImagesAdapter(
    private var memesArray: List<MemWithBitmap>, private val context: Context) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_holder, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return memesArray.size
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val memWithBitmap = memesArray[position]
        holder.setData(memWithBitmap)
    }

    inner class ImagesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val imageView = view.findViewById<ImageView>(R.id.image_recycler_holder)
        fun setData(memWithBitmap: MemWithBitmap) {
            imageView.setImageBitmap(memWithBitmap.bitmap)
            view.setOnClickListener {
                val i = Intent(context, DisplayImageActivity::class.java)
                i.putExtra(MainActivity.IMG_KEY ,memWithBitmap.index)
                context.startActivity(i)
            }
        }
    }
}