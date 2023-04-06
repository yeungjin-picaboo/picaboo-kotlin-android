package com.example.picasso.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DiariesListDto(
    @SerializedName("date")
    val date: Array<String>,

    ) : Parcelable