package com.example.sicreditest.Model


import com.google.gson.annotations.SerializedName

data class People(
    @SerializedName("eventId")
    val eventId: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("picture")
    val picture: String
)