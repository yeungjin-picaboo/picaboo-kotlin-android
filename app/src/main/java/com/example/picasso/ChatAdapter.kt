package com.example.picasso

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import java.io.File


class ChatAdapter:RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var data: MutableList<ImageInfo> = mutableListOf()
    private var queue: RequestQueue? = null
    private var context: Context? = null


    fun setData(data: MutableList<ImageInfo>, context: Context){
        this.data = data
        this.context = context
        notifyDataSetChanged()
    }

    class ChatViewHolder(view: View): RecyclerView.ViewHolder(view){

        val image: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        //val item = data[position]
        if (queue == null) {
            queue = Volley.newRequestQueue(context)
        }

        getImage("http://10.0.2.2:8080/NFTtest", holder.image)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getImage(uri: String, image: ImageView){
        val path = Environment.getExternalStorageDirectory().path
        val imgFile = File(path + "/Download/test.jpg")
        if (imgFile.exists()) {
            val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
            image.setImageBitmap(myBitmap)
        }else{
            Log.d("test", "test image")
        }

//        var StringRequest = ImageRequest(uri,
//            {
//                bitmap ->
//                Log.d("test", "${bitmap}")
//                image.setImageBitmap(bitmap)
//            },
//            0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
//            {
//                err->
//                Log.d("test", "${err}")
//            }
//        )
//        queue?.add(StringRequest)
    }
}