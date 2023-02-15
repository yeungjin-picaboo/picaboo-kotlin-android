package com.example.picasso

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import java.lang.NumberFormatException
import com.example.picasso.databinding.ActivityWeatherMoodBinding
class WeatherMoodActivity : AppCompatActivity(), View.OnClickListener {

    var scaleToBig: Animation ?= null
    var scaleToSmall:Animation ?= null
    private val binding by lazy {
        ActivityWeatherMoodBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val snow = binding.imageButtonSnow
        val rainy = binding.imageButtonRainy
        val sunny = binding.imageButtonSunny
        val cloudy = binding.imageButtonCloudy
        val windy = binding.imageButtonWindy
        val happy = binding.imageButtonhappy
        val good = binding.imageButtonGood
        val netural = binding.imageButtonNetural
        val bad = binding.imageButtonBad
        val confused = binding.imageButtonConfused
        val angry = binding.imageButtonAngry
        val nervous = binding.imageButtonNervous
        val sad = binding.imageButtonSad
        val sick = binding.imageButtonSick
        val weatherImageArray = arrayOf<ImageButton>(
            sunny,rainy,snow,cloudy,windy
        )
        val moodImageArray = arrayOf<ImageButton>(
            happy,good,netural,bad,confused,angry,nervous,sad,sick
        )
        for (i in weatherImageArray.indices){
            weatherImageArray[i].setOnClickListener(this)
        }
        for (i in moodImageArray.indices){
            moodImageArray[i].setOnClickListener(this)
        }

    }

    private fun isSelected(imageArray:Array<ImageButton>,imageButton:ImageButton){

        for ( i in 0..imageArray.size-1 ){
            if(imageArray[i] == imageButton){ // for문을 통해 i번째의 image버튼과 현재 Imagebutton이 같은경우
                imageArray[i].setSelected(true)
                imageArray[i].startAnimation(scaleToBig)
            }else                           // for문을 통해 i번째의 image버튼과 현재 Imagebutton이 다른경우 -> 이경우는 어떻게 해도 4번 돔
                if(imageArray[i].isSelected) { // 만약 i번째가 선택되어있다면
                    imageArray[i].isSelected = false
                    Log.d("if else if문" , "${i},${imageArray[i].isSelected}")
                    imageArray[i].startAnimation(scaleToSmall)
                }

        }

    }

    override fun onClick(v: View?) {
        Log.d("on","ok")
        Log.d("on","${binding.root}")

        try {
            scaleToBig = AnimationUtils.loadAnimation(this, R.anim.btn_to_big_scale)
            scaleToSmall = AnimationUtils.loadAnimation(this, R.anim.btn_to_small_scale)
            val snow = binding.imageButtonSnow
            val rainy = binding.imageButtonRainy
            val sunny = binding.imageButtonSunny
            val cloudy = binding.imageButtonCloudy
            val windy = binding.imageButtonWindy
            val happy = binding.imageButtonhappy
            val good = binding.imageButtonGood
            val netural = binding.imageButtonNetural
            val bad = binding.imageButtonBad
            val confused = binding.imageButtonConfused
            val angry = binding.imageButtonAngry
            val nervous = binding.imageButtonNervous
            val sad = binding.imageButtonSad
            val sick = binding.imageButtonSick

            val weatherImageArray = arrayOf<ImageButton>(
                sunny,rainy,snow,cloudy,windy
            )

            val moodImageArray = arrayOf<ImageButton>(
                happy,good,netural,bad,confused,angry,nervous,sad,sick
            )
            when(v){
                snow -> isSelected(weatherImageArray,snow)
                rainy -> isSelected(weatherImageArray,rainy)
                sunny -> isSelected(weatherImageArray,sunny)
                cloudy -> isSelected(weatherImageArray,cloudy)
                windy -> isSelected(weatherImageArray,windy)
                happy -> isSelected(moodImageArray,happy)
                good -> isSelected(moodImageArray,good)
                netural -> isSelected(moodImageArray,netural)
                bad -> isSelected(moodImageArray,bad)
                confused -> isSelected(moodImageArray,confused)
                angry -> isSelected(moodImageArray,angry)
                nervous -> isSelected(moodImageArray,nervous)
                sad -> isSelected(moodImageArray,sad)
                sick -> isSelected(moodImageArray,sick)
            }
        }catch (e:NumberFormatException){
            return
        }

    }

}