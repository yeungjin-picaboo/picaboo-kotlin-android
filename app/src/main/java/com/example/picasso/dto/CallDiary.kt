package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallDiary(
    @SerializedName("date")
    val date:String,

    @SerializedName("user_id")
    val user_id:String

):Parcelable
