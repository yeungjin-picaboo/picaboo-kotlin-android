package com.example.picasso.retorfit

import com.android.volley.Response

object ResponseDataHolder {
    var responseData:Response<String>? = null
    // 응답데이터를 저장할 싱글톤 패턴
}