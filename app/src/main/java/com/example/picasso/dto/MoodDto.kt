package com.example.picasso.dto

import com.google.gson.annotations.SerializedName

data class MoodDto(
    @SerializedName("mood")
    val mood: String
)