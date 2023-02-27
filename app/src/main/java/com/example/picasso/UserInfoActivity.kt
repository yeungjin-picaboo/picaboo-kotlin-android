package com.example.picasso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.picasso.databinding.ActivityMainBinding

class UserInfoActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.importUserInfo)


    }
}