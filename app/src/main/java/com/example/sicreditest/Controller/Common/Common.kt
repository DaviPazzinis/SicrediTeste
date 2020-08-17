package com.example.sicreditest.Controller.Common

import android.content.Context
import android.widget.Toast
import com.example.sicreditest.Controller.Interface.RetrofitService
import com.example.sicreditest.Controller.Retrofit.DataRetrofit


object Common {
    private val BASE_URL = "http://5b840ba5db24a100142dcd8c.mockapi.io/api/"

    val retrofitService: RetrofitService
        get() = DataRetrofit.getEvents(BASE_URL).create(RetrofitService::class.java)


    fun noConection(context: Context){
        Toast.makeText(context,"Sem conexão a internet", Toast.LENGTH_SHORT).show()
    }
}