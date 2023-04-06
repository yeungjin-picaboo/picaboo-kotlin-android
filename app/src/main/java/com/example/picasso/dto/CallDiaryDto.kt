package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CallDiaryDto(
    @SerializedName("user_email")
    val user_id:String

):Parcelable
