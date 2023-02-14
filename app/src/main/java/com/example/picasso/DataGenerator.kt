package com.example.picasso

import kotlin.random.Random
data class ChatRoomInfo(val name:String, val time:String){
    override fun toString(): String {
        return "$name, $time"
    }
}

data class ImageInfo(val image: Int){

}

class DataGenerator{
    companion object{
        @JvmField
        val images = arrayOf(
            R.drawable.one, R.drawable.two , R.drawable.three,
            R.drawable.four, R.drawable.five, R.drawable.six
        )

        @JvmStatic
        fun get():MutableList<ImageInfo>{
            val list = MutableList(100){
                val imageIndex = Random.nextInt(images.size)
                ImageInfo(images[imageIndex])
            }

            return list
        }
    }
}
