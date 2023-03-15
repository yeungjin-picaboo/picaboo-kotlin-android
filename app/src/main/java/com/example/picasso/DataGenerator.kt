package com.example.picasso

import android.util.Log


class DataGenerator{
    companion object{
        @JvmStatic
        fun get(type: String):MutableList<String>{
            Log.d("test", type)
            //여기서 이미지 URL전부 가져온다.
            val list = MutableList(31){
                "test"
            }
//            val list = mutableListOf<String>()
//            list.add("test")
            return list
        }
    }
}
