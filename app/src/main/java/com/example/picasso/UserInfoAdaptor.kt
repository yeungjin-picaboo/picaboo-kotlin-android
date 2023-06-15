package com.example.picasso

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.dto.userInfo.UserInfoDiaryDto
import com.willy.ratingbar.ScaleRatingBar
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UserInfoAdaptor : RecyclerView.Adapter<UserInfoAdaptor.UserViewHolder>() {
    // ViewHolderクラスは各アイテムのビューを作成します。
    // findViewByIdを使用して各要素にアクセスできます。
    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewEmotion: TextView = view.findViewById(R.id.textViewEmotion)
        val textViewDate: TextView = view.findViewById(R.id.UserInfoDate)
        val imageView: ImageView = view.findViewById(R.id.UserInfoImgView)
        val ratingBar: ScaleRatingBar = view.findViewById(R.id.RatingBar)
    }

    private var data: MutableList<UserInfoDiaryDto> = mutableListOf()
    private var queue: RequestQueue? = null
    private var context: Context? = null

    // setData関数は新しいデータリストとコンテキストをセットします。
    fun setData(data: MutableList<UserInfoDiaryDto>, context: Context) {
        this.data = data
        this.context = context
        notifyDataSetChanged()
    }

    // onCreateViewHolderは新しいViewHolderを作成します。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_feels_list, parent, false)
        return UserViewHolder(view)
    }

    // getItemCountはアイテムの数を返します。
    override fun getItemCount(): Int {
        return data.size
    }

    // onBindViewHolderは特定の位置にデータをバインドします。
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        if (queue == null) {
            queue = Volley.newRequestQueue(context)
        }

        val item = data[position]
        val StringRequest =
            ImageRequest("https://picaboodiaryimage.s3.ap-northeast-2.amazonaws.com/${item.source}",
                { bitmap ->
                    Log.d("item : ", item.toString())
                    holder.imageView.setImageBitmap(createRoundedImageBitmap(bitmap))
                    holder.textViewEmotion.text = item.emotion
                    val dateString = item.date
                    val date = LocalDate.parse(dateString)
                    val formatter = DateTimeFormatter.ofPattern("MMM dd")
                    val formattedDate = date.format(formatter)
                    holder.textViewDate.text = formattedDate
                    holder.ratingBar.rating = item.rate!!
                }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, { err ->
                    Log.d("test", "${err} err")
                })
        queue?.add(StringRequest)
    }

    // convertImageFileToBitmapは画像ファイルをビットマップに変換します。
    private fun convertImageFileToBitmap(imgFile: File): Bitmap {
        return BitmapFactory.decodeFile(imgFile.absolutePath)
    }

    // createRoundedImageBitmapはビットマップ画像を丸い形に加工します。
    private fun createRoundedImageBitmap(img: Bitmap): Bitmap {
        val RADIUS = 30
        val imageRounded = Bitmap.createBitmap(img.width, img.height, img.config)
        val canvas = Canvas(imageRounded)
        val mpaint = Paint()
        mpaint.isAntiAlias = true
        mpaint.shader = BitmapShader(img, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawRoundRect(
            RectF(
                (0).toFloat(),
                (0).toFloat(),
                img.width.toFloat(),
                img.height.toFloat()
            ),
            RADIUS.toFloat(),
            RADIUS.toFloat(),
            mpaint
        )
        return imageRounded
    }
}
