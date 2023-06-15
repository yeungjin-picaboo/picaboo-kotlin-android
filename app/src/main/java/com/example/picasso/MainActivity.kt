package com.example.picasso

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.picasso.api.ApiService
import com.example.picasso.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var queue: RequestQueue? = null
    lateinit var dataText: TextView

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val api = ApiService

        lifecycleScope.launch {
            val a = api.communicateJwt(this@MainActivity).validateLogin()
            if (a.isSuccessful) {
                //로그인 성공시 ( JWT 토큰이 유효할 때 )
                Log.d("JWTLoginResultSuccess", a.toString())
                val intent = Intent(this@MainActivity, gallery::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d("JWTLoginResultSuccess", a.toString())
            }
        }

        binding.ButtonRegister.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }


        //초기화
        if (queue == null) {
            queue = Volley.newRequestQueue(this)
        }
    }

}