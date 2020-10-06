package com.example.memes.activities.fragments

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memes.R
import com.example.memes.activities.adapters.ImagesAdapter
import com.example.memes.activities.data.MemWithBitmap

class MainFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    lateinit var imagesAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_main, container, false)
        recyclerView = rootView.findViewById(R.id.images_recycler)
        return rootView
    }

    override fun onStart() {
        super.onStart()
        initRecycler()
    }

    private fun initRecycler() {
        if(!::imagesAdapter.isInitialized){
            imagesAdapter = ImagesAdapter(listOf(), activity as FragmentActivity, activity as ImagesAdapter.FragmentSwitcher)
        }
        val gridLayoutManager = GridLayoutManager(
            activity,
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        )
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = imagesAdapter
    }
}