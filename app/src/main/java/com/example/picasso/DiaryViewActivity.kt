package com.example.picasso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

import com.example.picasso.databinding.ActivityDiaryViewBinding

class DiaryViewActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDiaryViewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }


}