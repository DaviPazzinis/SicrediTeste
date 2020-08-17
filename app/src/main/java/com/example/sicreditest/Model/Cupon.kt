package com.example.sicreditest.Model


import com.google.gson.annotations.SerializedName

data class Cupon(
    @SerializedName("discount")
    val discount: Int,
    @SerializedName("eventId")
    val eventId: String,
    @SerializedName("id")
    val id: String
)