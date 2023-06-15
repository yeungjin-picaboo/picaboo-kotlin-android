package com.example.picasso.publicClass.detailDiaryFun

import android.content.Context
import android.util.Log
import com.example.picasso.api.ApiService
import com.example.picasso.dto.diary.detailDairyDto.RatingStarDto
import com.example.picasso.publicClass.AlertError.errorExpress
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * TODO
 * ImageButton＆BackButtonを押したときにサーバーと通信する関数
 * @param rate
 * @param context
 * @return
 */
suspend fun backPressRating(ratingStarDto: RatingStarDto, context: Context): Boolean {
    val api = ApiService
    val deferred = CompletableDeferred<Boolean>()
    Log.d("Running", "run")
    CoroutineScope(Dispatchers.Main).launch {
        try {
            Log.d("Running2", "run")
            Log.d("rate", ratingStarDto.toString())

            val response = api.communicateJwt(context).ratingUser(ratingStarDto).body()

            Log.d("response", "$response")

            if (response?.ok == true) {
                deferred.complete(true)
            } else {
                deferred.complete(false)
                errorExpress(context)
            }
        } catch (err: java.lang.Error) {
            deferred.completeExceptionally(err)
        }
    }
    return deferred.await()
}