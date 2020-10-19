package com.example.memes.activities.services

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import com.example.memes.activities.adapters.ImagesAdapter
import com.example.memes.activities.data.MemWithBitmap
import com.example.memes.activities.data.ServerResponse
import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class FetchMemesServer : Service(){

    inner class LocalBinder : Binder() {
        fun getService(): FetchMemesServer = this@FetchMemesServer
    }

    private lateinit var jsonResponce: ServerResponse
    var memes = mutableListOf<MemWithBitmap>()
    private val binder = LocalBinder()
    private var isLoading = false
    private lateinit var fetchImagesForMemesAsyncTask: FetchImagesForMemesAsyncTask

    fun stickToData(imagesAdapter: ImagesAdapter) {
        if (!::jsonResponce.isInitialized) {
            val responce = fetchMemes()
            jsonResponce = Gson().fromJson(responce, ServerResponse::class.java)
        }
        val urls = jsonResponce.data.memes.map { it.url }
        fetchImagesForMemesAsyncTask = FetchImagesForMemesAsyncTask(
            memes,
            { this.isLoading = false },
            imagesAdapter
        )
        fetchImagesForMemesAsyncTask.execute(urls)
    }

    fun continueStickingToData(imagesAdapter: ImagesAdapter) {
        fetchImagesForMemesAsyncTask.imagesAdapter = imagesAdapter
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun fetchMemes(): String {
        return FetchMemesAsyncTask().execute().get()
    }

    class FetchImagesForMemesAsyncTask(
        private var storage: MutableList<MemWithBitmap>?,
        private val isLoadingNotifier: () -> Unit,
        var imagesAdapter: ImagesAdapter?
    ) :
        AsyncTask<List<String>, MemWithBitmap, Unit>(){
        override fun doInBackground(vararg params: List<String>) {
            params[0].forEachIndexed { i, it ->
                publishProgress(
                    MemWithBitmap(
                        getBitmapFromURL(it),
                        i
                    )
                )
            }
        }

        override fun onProgressUpdate(vararg values: MemWithBitmap?) {
            super.onProgressUpdate(*values)
            storage?.add(values[0]!!)
            imagesAdapter!!.notifyDataSetChanged()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            isLoadingNotifier()
            storage = null
            imagesAdapter = null
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