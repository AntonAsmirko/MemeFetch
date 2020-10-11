package com.example.memes.activities.activities

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.example.memes.R
import com.example.memes.activities.services.FetchMemesServer
import kotlinx.android.synthetic.main.activity_display_image.*

class DisplayImageActivity : AppCompatActivity() {

    private lateinit var mService: FetchMemesServer
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as FetchMemesServer.LocalBinder
            mService = binder.getService()
            mBound = true
            val pos = intent.getIntExtra("LLL", 0)
            val data = mService.requestData()
            big_img.setImageBitmap(data[pos].bitmap)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)
        if (!mBound) {
            Intent(this, FetchMemesServer::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }
}