package com.example.picasso.dto

import com.google.gson.annotations.SerializedName

data class SendDiaryDto (
    @SerializedName("content")
    val content:String,

    @SerializedName("mood")
    val mood:String,


)