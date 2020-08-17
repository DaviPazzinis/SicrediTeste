package com.example.sicreditest.View

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sicreditest.Controller.Adapter.MyAdapter
import com.example.sicreditest.Controller.Common.Common
import com.example.sicreditest.Controller.Interface.RetrofitService
import com.example.sicreditest.Model.EventItem
import com.example.sicreditest.R
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    lateinit var mService : RetrofitService
    lateinit var layoutManager: LinearLayoutManager


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mService = Common.retrofitService
        eventRecyclerView.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(this)
        eventRecyclerView.layoutManager = layoutManager

        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }else if(isOnline()){
            getAllEventList()
        }

    }

    private fun getAllEventList() {

        mService.getEventList().enqueue(object : Callback<MutableList<EventItem>> {
            override fun onFailure(call: Call<MutableList<EventItem>>, t: Throwable) {
            }
            override fun onResponse(call: Call<MutableList<EventItem>>, response: Response<MutableList<EventItem>?>?) {
                response?.body()?.let {
                    val events: List<EventItem>? = it
                    configureList(events)
                }

            }

        })

    }

    private fun configureList(events: List<EventItem>?){
        val recyclerView = eventRecyclerView
        recyclerView.adapter = MyAdapter(events!!) {
                events : EventItem -> onItemClick(events)
            recyclerView.scheduleLayoutAnimation()
        }
    }

    private fun onItemClick(event: EventItem){
        val showDetailActivityIntent = Intent(this, DetailActivity::class.java)
        showDetailActivityIntent
            .putExtra("Lat", event.latitude.toDouble())
            .putExtra("Long", event.longitude.toDouble())
            .putExtra("description", event.description)
            .putExtra("title", event.title)
            .putExtra("price", event.price)
            .putExtra("date", event.date)
            .putExtra("image", event.image)
            .putExtra("eventId", event.id)

        startActivity(showDetailActivityIntent)
    }

    fun isOnline(): Boolean {
        val cm =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }

    override fun onPause() {
        super.onPause()
        if (!isOnline()){
            Common.noConection(applicationContext)
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isOnline()){
            Common.noConection(this)
            this.finish()
        }
    }




}

