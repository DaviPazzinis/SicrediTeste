package com.example.sicreditest.Controller.Retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DataRetrofit {
    private var retrofit: Retrofit? = null

    fun getEvents(baseUrl: String) : Retrofit {
        if (retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}