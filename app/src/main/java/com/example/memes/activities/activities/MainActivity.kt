package com.example.memes.activities.activities

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.memes.R
import com.example.memes.activities.adapters.ImagesAdapter
import com.example.memes.activities.data.Mem
import com.example.memes.activities.data.MemWithBitmap
import com.example.memes.activities.data.ServerResponse
import com.example.memes.activities.fragments.DisplayBigImageFragment
import com.example.memes.activities.fragments.MainFragment
import com.example.memes.activities.services.FetchMemesServer
import com.example.memes.activities.viewModel.ImagesViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity(), ImagesAdapter.FragmentSwitcher {

    private enum class CurrentFragment {
        MAIN_FRAGMENT, DISPLAY_BIG_IMAGE_FRAGMENT
    }

    private lateinit var mainFragment: MainFragment
    private var displayBigImageFragment: DisplayBigImageFragment? = null
    private var curFragment = CurrentFragment.MAIN_FRAGMENT
    private lateinit var mService: FetchMemesServer
    private var mBound: Boolean = false


    companion object {
        const val ACCESS_NETWORK_STATE_PERMISSION_REQUEST_ID = 111
        const val INTERNET_PERMISSION_REQUEST_ID = 101
        const val IMG_KEY = "IMG_KEK"
    }

    val model: ImagesViewModel by viewModels()

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as FetchMemesServer.LocalBinder
            mService = binder.getService()
            mBound = true
            loadData(model)
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
        mainFragment = MainFragment()
        displayBigImageFragment = DisplayBigImageFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment_holder, mainFragment, "kek").commit()
        if (model.memesWithImages.value == null) {
            Intent(this, FetchMemesServer::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        } else {
            updateRecycler(model)
        }
    }

    private fun updateRecycler(model: ImagesViewModel) {
        val newVal = mutableListOf<MemWithBitmap>()
        newVal.addAll(mainFragment.listData.value ?: listOf())
        newVal.addAll(model.memesWithImages.value ?: listOf())
        mainFragment.listData.value = newVal
    }

    private fun loadData(model: ImagesViewModel) {
        val gson = Gson()
        model.memesWithImages.observe(this, Observer {
            updateRecycler(model)
        })
        model.memes.observe(this, Observer {
            val objList: ServerResponse =
                gson.fromJson(model.memes.value, ServerResponse::class.java)
            val res = objList.data.memes
            val query = res.map { Pair(it.url, it.name) }
            model.memesWithImages.value = mService.fetchImages(query)
        })
        model.memes.value = mService.fetchMemes()
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

    override fun switchFragments(bitmap: Bitmap) {
        supportFragmentManager.beginTransaction().remove(mainFragment)
            .add(R.id.fragment_holder, displayBigImageFragment!!, "lol").commit()
        displayBigImageFragment!!.bitmap = bitmap
        curFragment = CurrentFragment.DISPLAY_BIG_IMAGE_FRAGMENT
    }

    override fun onBackPressed() {
        if (curFragment == CurrentFragment.DISPLAY_BIG_IMAGE_FRAGMENT) {
            supportFragmentManager.beginTransaction().remove(displayBigImageFragment!!)
                .add(R.id.fragment_holder, mainFragment, "kek").commit()
        } else {
            super.onBackPressed()
        }
    }
}