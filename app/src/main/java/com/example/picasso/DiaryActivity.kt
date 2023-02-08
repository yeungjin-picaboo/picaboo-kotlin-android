package com.example.picasso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageButton
import com.example.picasso.databinding.ActivityDiaryBinding

class DiaryActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityDiaryBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //animation 객체 가져오기
        val animation = AnimationUtils.loadAnimation(this, R.anim.updown)
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.updonw_reverse)
        val rolling = AnimationUtils.loadAnimation(this, R.anim.rolling_btn)
        val rolling2 = AnimationUtils.loadAnimation(this, R.anim.rolling_btn_reverse)

        val testView = binding.testView
        val showCal = binding.showCal

        testView.visibility = View.INVISIBLE
        var state: Boolean = true

        binding.showCal.setOnClickListener {
            if (state) {
                state = rollingBtn(showCal as AppCompatImageButton, state, rolling as AnimationSet)
                testView.visibility = View.VISIBLE
                testView.startAnimation(animation)
            } else {
                state = rollingBtn(showCal as AppCompatImageButton, state, rolling2 as AnimationSet)
                testView.startAnimation(animation2)
                testView.visibility = View.INVISIBLE
            }
        }
    }

    fun rollingBtn(
        wiget: AppCompatImageButton,
        state: Boolean,
        animation: AnimationSet
    ): Boolean {
        if (state) {
            wiget.startAnimation(animation)
            return !state
        } else {
            wiget.startAnimation(animation)
            return !state
        }
    }
}