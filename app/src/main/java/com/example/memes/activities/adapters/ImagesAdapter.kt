package com.example.memes.activities.adapters

import android.content.Context
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
import com.example.memes.activities.data.MemWithBitmap

class ImagesAdapter(
    private var memesArray: MutableLiveData<MutableList<MemWithBitmap>>,
    private val switcher: FragmentSwitcher,
    lifeCycle: LifecycleOwner
) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    init {
        memesArray.observe(lifeCycle, Observer {
            Log.d("KEK", "Reacted list size ${memesArray.value!!.size}")
            notifyDataSetChanged()
        })
        Log.d("KEK", "Observer was attached")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.image_holder, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return memesArray.value!!.size
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val memWithBitmap = memesArray.value!![position]
        holder.setData(memWithBitmap)
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