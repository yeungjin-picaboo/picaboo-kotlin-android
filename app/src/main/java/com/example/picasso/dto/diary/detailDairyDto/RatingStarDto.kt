package com.example.picasso.dto.diary.detailDairyDto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RatingStarDto(
    @SerializedName("rate")
    val rate: Float,

    @SerializedName("diary_id")
    val id: Int
) : Parcelable



