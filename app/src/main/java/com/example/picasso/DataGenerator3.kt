package com.example.picasso

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.picasso.api.WeatherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Diary(
    val id: Int,
    val title: String,
    val content: String,
    val date: String,
    val emotion: String,
    val weather: String,
    val source: String?
)

class DataGenerator3 {

    companion object {
        @JvmStatic
        fun get(type: String): MutableList<Diary> {
            Log.d("test", type)
            //여기서 이미지 URL전부 가져온다.

            val list = MutableList(31) {
                Diary(
                    1,
                    "testTitle",
                    "testContent",
                    "2023-04-13",
                    "positive",
                    "Clear",
                    "https://picaboonftimage.s3.ap-northeast-2.amazonaws.com/avocado.jpg"
                )
            }

//            val list = mutableListOf<String>()
//            list.add("test")
            return list
        }
    }
}
