package com.example.memes.activities.services

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import com.example.memes.activities.data.MemWithBitmap
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FetchMemesServer : Service() {

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): FetchMemesServer = this@FetchMemesServer
    }

    fun fetchImages(query: List<Pair<String, String>>): List<MemWithBitmap> {
        return FetchImagesForMemesAsyncTask().execute(query).get()
    }

    fun fetchMemes(): String {
        return FetchMemesAsyncTask().execute().get()
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

    class FetchMemesAsyncTask : AsyncTask<String, Unit, String>() {
        override fun doInBackground(vararg params: String?): String {
            return URL("https://api.imgflip.com/get_memes").openConnection().run {
                connect()
                getInputStream().bufferedReader().readLines().joinToString("")
            }
        }
    }
}