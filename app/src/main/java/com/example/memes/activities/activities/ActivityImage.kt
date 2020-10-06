package com.example.memes.activities.activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memes.R
import kotlinx.android.synthetic.main.activity_image.*

class ActivityImage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val bitmap: Bitmap = intent.getSerializableExtra(MainActivity.IMG_KEY) as Bitmap
        big_img.setImageBitmap(bitmap)
    }
}