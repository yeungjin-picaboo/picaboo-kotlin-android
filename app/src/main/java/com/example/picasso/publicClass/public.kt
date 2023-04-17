package com.example.picasso.publicClass

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.picasso.dto.ResultMessageDto

class Public {

    fun resulting(response: ResultMessageDto, intent: Intent) {
        if (response.message == "success") {
            // gallery로 넘어가면 됨

        } else {
            //에러 메세지 발송
        }
    }
}