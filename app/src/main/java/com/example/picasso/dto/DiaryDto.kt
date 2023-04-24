package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiaryDto (
    @SerializedName("title")
    val title:String,

    @SerializedName("content")
    val content: String,

    @SerializedName("mood")
    val mood: String,

    @SerializedName("weather")
    val weather: String,

    @SerializedName("source")
    val source: String,

    @SerializedName("id")
    val diary_id: Int,

    @SerializedName("date")
    val date: String

):Parcelable