package com.example.picasso

import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.api.ApiService
import com.example.picasso.databinding.ActivityMainBinding
import com.example.picasso.databinding.ActivityUserInfoBinding
import kotlinx.coroutines.launch

class UserInfoActivity : AppCompatActivity() {
    // bindingは遅延評価（lazy）で初期化します。
    // これはActivityUserInfoBindingのインスタンスを生成します。
    private val binding: ActivityUserInfoBinding by lazy {
        ActivityUserInfoBinding.inflate(layoutInflater)
    }
    private var queue: RequestQueue? = null
    private val adapter = UserInfoAdaptor()
    private val api = ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // レイアウトを設定します。
        setContentView(binding.root)
        if (queue == null) {
            // Volleyのリクエストキューを初期化します。
            queue = Volley.newRequestQueue(this)
        }

        // LinearLayoutManagerを作成し、RecyclerViewのレイアウトマネージャーに設定します。
        val manager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = manager
        // RecyclerViewのアダプターにUserInfoAdaptorを設定します。
        binding.recyclerView.adapter = adapter

        // 非同期処理を行います。
        // communicateJwtメソッドを使用してユーザー情報を取得し、アダプターのデータとしてセットします。
        lifecycleScope.launch {
            val userInfo = api.communicateJwt(this@UserInfoActivity).getUserInfo().body()!!
            adapter.setData(userInfo, this@UserInfoActivity)
        }

    }
}