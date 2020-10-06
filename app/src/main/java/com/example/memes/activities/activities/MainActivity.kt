package com.example.memes.activities.activities

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memes.R
import com.example.memes.activities.adapters.ImagesAdapter
import com.example.memes.activities.data.Mem
import com.example.memes.activities.data.MemWithBitmap
import com.example.memes.activities.data.ServerResponse
import com.example.memes.activities.viewModel.ImagesViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    companion object {
        const val ACCESS_NETWORK_STATE_PERMISSION_REQUEST_ID = 111
        const val INTERNET_PERMISSION_REQUEST_ID = 101
        const val IMG_KEY = "IMG_KEK"
    }

    private lateinit var imagesAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!checkPermissionAccessNetworkState()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_NETWORK_STATE),
                ACCESS_NETWORK_STATE_PERMISSION_REQUEST_ID
            )
        }
        if (!checkPermissionInternet()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET),
                INTERNET_PERMISSION_REQUEST_ID
            )
        }
        val model: ImagesViewModel by viewModels()
        initRecycler()
        if (model.memesWithImages.value == null) {
            loadData(model)
        } else {
            imagesAdapter.loadNewMemes(model.memesWithImages.value!!)
        }
    }

    private fun loadData(model: ImagesViewModel) {
        val gson = Gson()
        model.memesWithImages.observe(this, Observer {
            imagesAdapter.loadNewMemes(model.memesWithImages.value!!)
        })
        model.memes.observe(this, Observer {
            val objList: ServerResponse =
                gson.fromJson(model.memes.value, ServerResponse::class.java)
            val res = objList.data.memes;
            val query = res.map { Pair(it.url, it.name) }
            model.memesWithImages.value = FetchImagesForMemesAsyncTask().execute(query).get()
        })
        model.memes.value = FetchMemesAsyncTask()
            .execute().get()
    }

    private fun initRecycler() {
        imagesAdapter = ImagesAdapter(listOf(), this)
        val gridLayoutManager = GridLayoutManager(
            this,
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        )
        images_recycler.layoutManager = gridLayoutManager
        images_recycler.adapter = imagesAdapter
    }

    private fun checkPermissionInternet(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.INTERNET
        ) != PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionAccessNetworkState(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_NETWORK_STATE
        ) != PackageManager.PERMISSION_GRANTED
    }

    class FetchMemesAsyncTask : AsyncTask<String, Unit, String>() {
        override fun doInBackground(vararg params: String?): String {
            return URL("https://api.imgflip.com/get_memes").openConnection().run {
                connect()
                getInputStream().bufferedReader().readLines().joinToString("")
            }
        }
    }

    class FetchImagesForMemesAsyncTask :
        AsyncTask<List<Pair<String, String>>, Unit, List<MemWithBitmap>>() {
        override fun doInBackground(vararg params: List<Pair<String, String>>?): List<MemWithBitmap> {
            return params[0]!!.map { MemWithBitmap(getBitmapFromURL(it.first), it.second) }
        }

        private fun getBitmapFromURL(src: String?): Bitmap? {
            return try {
                val url = URL(src)
                val connection: HttpURLConnection = url
                    .openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }
}