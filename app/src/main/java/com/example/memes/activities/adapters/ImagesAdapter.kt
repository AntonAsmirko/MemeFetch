package com.example.memes.activities.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageSwitcher
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.memes.R
import com.example.memes.activities.activities.MainActivity
import com.example.memes.activities.data.MemWithBitmap

class ImagesAdapter(
    private var memesArray: List<MemWithBitmap>,
    private val context: Activity,
    private val switcher: FragmentSwitcher
) :
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

    fun loadNewMemes(newMemes: List<MemWithBitmap>) {
        memesArray = newMemes
        notifyDataSetChanged()
    }

    inner class ImagesViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val imageView = view.findViewById<ImageView>(R.id.image_recycler_holder)
        fun setData(memWithBitmap: MemWithBitmap) {
            imageView.setImageBitmap(memWithBitmap.bitmap)
            view.setOnClickListener {
                switcher.switchFragments(memWithBitmap.bitmap!!)

            }
        }
    }

    interface FragmentSwitcher {
        fun switchFragments(bitmap: Bitmap)
    }
}