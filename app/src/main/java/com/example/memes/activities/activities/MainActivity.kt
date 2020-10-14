package com.example.memes.activities.activities

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memes.R
import com.example.memes.activities.adapters.ImagesAdapter
import com.example.memes.activities.data.MemWithBitmap
import com.example.memes.activities.services.FetchMemesServer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    lateinit var imagesAdapter: ImagesAdapter

    private lateinit var mService: FetchMemesServer
    private var mBound: Boolean = false


    companion object {
        const val ACCESS_NETWORK_STATE_PERMISSION_REQUEST_ID = 111
        const val INTERNET_PERMISSION_REQUEST_ID = 101
        const val IMG_KEY = "IMG_KEK"
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FetchMemesServer.LocalBinder
            mService = binder.getService()
            mBound = true
            initRecycler(mService.memes)
            if (mService.memes.isEmpty()) {
                mService.stickToData(imagesAdapter)
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }


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
        if (!mBound) {
            Intent(this, FetchMemesServer::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun initRecycler(data: List<MemWithBitmap>) {
        if (!::imagesAdapter.isInitialized) {
            imagesAdapter = ImagesAdapter(data, this)
        }
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
}