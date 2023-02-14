package com.example.picasso

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest

class ChatAdapter:RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var data: MutableList<ImageInfo> = mutableListOf()
    private var queue: RequestQueue? = null


    fun setData(data: MutableList<ImageInfo>){
        this.data = data
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
        val  item = data[position]
        holder.image.setImageResource(item.image)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getImage(uri: String, image: ImageView){
        val StringRequest = ImageRequest(uri,
            {
                bitmap ->
                image.setImageBitmap(bitmap)
            },
            0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
            {

            }
        )
        queue?.add(StringRequest)
    }
}