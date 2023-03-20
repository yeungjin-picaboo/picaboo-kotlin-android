package com.example.picasso

import android.content.Context
import android.graphics.*
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.io.File


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var data: MutableList<String> = mutableListOf()
    private var queue: RequestQueue? = null
    private var context: Context? = null

    //이거 받아온 데이터 저장  한번 불러온 것은 같은 페이지에서 또 불러오지 않음
    private var bitmapData: MutableList<Bitmap?>? = null


    fun setData(data: MutableList<String>, context: Context) {
        this.data = data
        this.context = context
        bitmapData = MutableList<Bitmap?>(data.size, { it -> null })
        notifyDataSetChanged()
    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context)
        }

        holder.image.setOnClickListener{
            // 여기다 페이지 이동 구현하면 됨
            val request = JsonObjectRequest(Request.Method.GET, "http://10.0.2.2:8080/testjson", null, { res ->

                try {
                    Log.d("test", "${res.javaClass.name}")
                    //intent로 보냄 response 그대로 보내면 듯
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, { error ->
                Log.e("tes", "RESPONSE IS $error")
            })
            queue?.add(request)
            Log.d("test", "${position}")
        }

        //이미지 불러옴
        getImage(data[position], holder.image as AppCompatImageView, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getImage(url: String, view: AppCompatImageView, position: Int) {
        //이거 url이미지 이름으로 그거 ㅎ한다

        //aixos를 쓰던 해서 비동기처리를 해보아라
        val path = Environment.getExternalStorageDirectory().path
        val imgFile = File(path + "/Download/test.png")  // 이미지URL + url 해서 넣음
        if (imgFile.exists()) {
            //이미지 라운드 처리
            view.setImageBitmap(createRoundedImageBitmap(imgFile))
        } else {

            Log.d("test", "저장된 이미지 없음 서버에서 받아옴")
            //한번 받아온 데이터를 또 받아오지 않게 한다.
            //처음 받아올때 bitmapData에 저장한다. 데잍터형식 Bitmap

            if (bitmapData!![position] == null) {
                Log.d("test", "통신으로 받아옴")
                var StringRequest = ImageRequest("http:10.0.2.2:8080/NFTtest", // + URL 이런식으로 만듬
                    { bitmap ->
                        bitmapData!![position] = bitmap
                        view.setImageBitmap(bitmap)
                    },
                    0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                    { err ->
                        Log.d("test", "${err} err")
                    }
                )
                queue?.add(StringRequest)

            } else {
                view.setImageBitmap(bitmapData!![position])
            }
        }
    }

    private fun createRoundedImageBitmap(imgFile: File): Bitmap{
        val mbitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
        val RADIUS = 30
        val imageRounded = Bitmap.createBitmap(mbitmap.width, mbitmap.height, mbitmap.config)

        val canvas = Canvas(imageRounded)
        val mpaint = Paint()
        mpaint.setAntiAlias(true)
        mpaint.setShader(BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
        canvas.drawRoundRect(
            RectF(
                (0).toFloat(),
                (0).toFloat(),
                mbitmap.width.toFloat(),
                mbitmap.height.toFloat()
            ),
            RADIUS.toFloat(),
            RADIUS.toFloat(),
            mpaint
        )
        return imageRounded
    }
}