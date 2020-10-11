package com.example.memes.activities.services

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.example.memes.activities.data.MemWithBitmap
import com.example.memes.activities.data.ServerResponse
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FetchMemesServer : Service() {

    inner class LocalBinder : Binder() {
        fun getService(): FetchMemesServer = this@FetchMemesServer
    }

    private lateinit var jsonResponce: ServerResponse
    private lateinit var memes: List<MemWithBitmap>

    private val binder = LocalBinder()
    private var str = true

    fun requestData(): List<MemWithBitmap> {
        if (!::memes.isInitialized) {
            if (!::jsonResponce.isInitialized) {
                val responce = fetchMemes()
                jsonResponce = Gson().fromJson(responce, ServerResponse::class.java)
            }
            val urls = jsonResponce.data.memes.map { it.url }
            memes = FetchImagesForMemesAsyncTask().execute(urls).get()
        }
        return memes
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun fetchMemes(): String {
        return FetchMemesAsyncTask().execute().get()
    }

    class FetchImagesForMemesAsyncTask :
        AsyncTask<List<String>, Unit, List<MemWithBitmap>>() {
        override fun doInBackground(vararg params: List<String>): List<MemWithBitmap> {
            var index = 0
            return params[0].map { MemWithBitmap(getBitmapFromURL(it), index++) }
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

    class FetchMemesAsyncTask : AsyncTask<String, Unit, String>() {
        override fun doInBackground(vararg params: String?): String {
            return URL("https://api.imgflip.com/get_memes").openConnection().run {
                connect()
                getInputStream().bufferedReader().readLines().joinToString("")
            }
        }
    }
}