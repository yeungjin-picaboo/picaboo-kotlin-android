package com.example.picasso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.isGone
import com.example.picasso.databinding.ActivityDiaryBinding
import java.lang.NumberFormatException

class DiaryActivity : AppCompatActivity() {


    private val binding by lazy {
        ActivityDiaryBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //animation 객체 가져오기
        val fake_animation = AnimationUtils.loadAnimation(this, R.anim.updown)
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.updonw_reverse)
        val rolling = AnimationUtils.loadAnimation(this, R.anim.rolling_btn)
        val rolling2 = AnimationUtils.loadAnimation(this, R.anim.rolling_btn_reverse)
        val opacity_cal_reverse = AnimationUtils.loadAnimation(this,R.anim.opacity_cal_reverse)

        val showCal = binding.showCal
        val whiteView = binding.WhiteView
        val textView = binding.textView
        val progressBar = binding.progressBar
        val nextBtn = binding.nextBtn
        var state: Boolean = true

        showCal.bringToFront()

        fun updateWidgets(){
            if(textView.text.toString().isNotEmpty())
                progressBar.progress = 50
            else
                progressBar.progress = -50
        }

        val textWatcher:TextWatcher = object:TextWatcher{ // editText에서 쓰이는 이벤트리스너 TextWatcher
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateWidgets()
            }
        }
        textView.addTextChangedListener(textWatcher)
        binding.showCal.setOnClickListener {
            if (state) {
//                var s = AnimationSet(false)
//                s.addAnimation(opacity)
//                s.addAnimation(animation)
//                animationSet객체를 사용하면 xml의 fillAfter이 적용되지 않는 버그가 있음

                state = rollingBtn(showCal as AppCompatImageButton, state, rolling as AnimationSet)
                whiteView.startAnimation(fake_animation) // 캘린더가 내려오는 것 처럼 보이는 애니메이션
                textView.startAnimation(opacity_cal_reverse) // textView를 불투명하게 만들기
                binding.root.removeView(textView)  // 클릭을 방지하기 위해 textView삭제

                Log.d("text : ", "${textView.text.toString().isEmpty()}")
            } else {
                state = rollingBtn(showCal as AppCompatImageButton, state, rolling2 as AnimationSet)

                whiteView.startAnimation(animation2) // 캘린더가 올라가는 것 처럼 보이는 애니메이션
                binding.root.addView(textView) // textView를 보이게
                whiteView.visibility = View.VISIBLE // 불투명했던 것을 보이게 하기 opacity값을 주지 않은 이유는 바로 올라간 것 처럼 보이기 위함

            }
        }
        nextBtn.setOnClickListener{
            try {
                val intent = Intent(this, WeatherMoodActivity::class.java)
                startActivity(intent)
            }catch (e: NumberFormatException){

            }
        }
    }

    /**
     * 위젯을 한바퀴 돌 수 있게 해주는 함수 캘린더뷰에 사용함.
     * @param wiget 위젯 선택하기
     * @param state 상태
     * @param animation 애니메이션
     * @return {Boolean} 불린값
     */
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