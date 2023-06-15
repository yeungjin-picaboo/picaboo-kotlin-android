package com.example.picasso.publicClass.detailDiaryFun

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class TransDateFormat

fun transDate(dateString: String): String {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    val outputDateFormat = SimpleDateFormat("MMMM yyyy EEEE", Locale.US)

    val date = inputDateFormat.parse(dateString)
    val outputDateString = outputDateFormat.format(date!!)
    Log.d("dateString", outputDateString)
    return outputDateString
}