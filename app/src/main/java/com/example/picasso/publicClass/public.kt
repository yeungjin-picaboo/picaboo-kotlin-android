package com.example.picasso.publicClass

import android.content.Intent
import com.example.picasso.dto.message.ResultMessageDto

class Public {

    fun resulting(response: ResultMessageDto, intent: Intent) {
        if (response.message == "success") {
            // gallery로 넘어가면 됨

        } else {
            //에러 메세지 발송
        }
    }
}