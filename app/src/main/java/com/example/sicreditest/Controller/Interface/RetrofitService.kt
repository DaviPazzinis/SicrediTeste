package com.example.sicreditest.Controller.Interface

import com.example.sicreditest.Controller.Common.DefaultResponse
import com.example.sicreditest.Model.EventItem
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    @GET("events")
    fun getEventList(): Call<MutableList<EventItem>>

    @FormUrlEncoded
    @POST("/checkin")
    fun checkIn(
        @Field("eventId") eventId:String,
        @Field("name") name:String,
        @Field("email") email:String
    ):Call<DefaultResponse>
}