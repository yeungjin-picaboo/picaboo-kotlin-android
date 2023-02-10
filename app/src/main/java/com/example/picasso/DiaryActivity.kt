package com.example.picasso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isGone
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
        val opacity_cal_reverse = AnimationUtils.loadAnimation(this,R.anim.opacity_cal_reverse)

        val calendarView = binding.calendarView
        val showCal = binding.showCal
        val whiteView = binding.WhiteView
        val textView = binding.EditText02
//        testView.visibility = View.INVISIBLE
        var state: Boolean = true

        showCal.bringToFront()
        binding.showCal.setOnClickListener {
            if (state) {
//                var s = AnimationSet(false)
//                s.addAnimation(opacity)
//                s.addAnimation(animation)
//                animationSet객체를 사용하면 xml의 fillAfter이 적용되지 않는 버그가 있음

                state = rollingBtn(showCal as AppCompatImageButton, state, rolling as AnimationSet)
                whiteView.startAnimation(animation)
                textView.startAnimation(opacity_cal_reverse)
                binding.root.removeView(textView)


            } else {
                state = rollingBtn(showCal as AppCompatImageButton, state, rolling2 as AnimationSet)
                Log.d("state","$state")
                whiteView.startAnimation(animation2)
                binding.root.addView(textView)
                whiteView.visibility = View.VISIBLE

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
            Log.d("se","$state")
            return !state
        } else {
            wiget.startAnimation(animation)
            Log.d("sef","$state")
            return !state
        }
    }
}