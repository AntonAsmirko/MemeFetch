package com.example.memes.activities.fragments

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.example.memes.R

class DisplayBigImageFragment : Fragment() {

    lateinit var imgView: ImageView
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_display_big_image, container, false)
        imgView = view.findViewById(R.id.big_img)
        imgView.setImageBitmap(bitmap)
        return view
    }
}