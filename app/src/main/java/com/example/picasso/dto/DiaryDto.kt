package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiaryDto (
    @SerializedName("title")
    val title:String,

    @SerializedName("content")
    val content:String,

    @SerializedName("mood")
    val mood:String,

    @SerializedName("weather")
    val weather:String,

    @SerializedName("diary_id")
    val diary_id:Int,

):Parcelable