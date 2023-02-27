package com.example.picasso

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import java.lang.NumberFormatException
import com.example.picasso.databinding.ActivityWeatherMoodBinding

class WeatherMoodActivity : AppCompatActivity(), View.OnClickListener {

    var scaleToBig: Animation? = null
    var scaleToSmall: Animation? = null
    var progress: Int? = null
    var inputDiary: String? = null
    var progressBar: ProgressBar? = null
    var weatherStatus: String? = null
    var moodStatus: String? = null


    private val binding by lazy {
        ActivityWeatherMoodBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setResult(Activity.RESULT_OK)


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
        val back = binding.imageButtonBack
        progressBar = binding.progressBarWeatherMood
        with(intent) {
            progress = getIntExtra("progressBar", 1)
            inputDiary = getStringExtra("inputDiary").toString()
        }
        progressBar!!.progress = 50

        Log.d("progress = ", "$progress")
        Log.d("diary = ", "$inputDiary")
        back.setOnClickListener(this)
        val weatherImageArray = arrayOf<ImageButton>(
            sunny, rainy, snow, cloudy, windy
        )
        val moodImageArray = arrayOf<ImageButton>(
            happy, good, netural, bad, confused, angry, nervous, sad, sick
        )

        for (i in weatherImageArray.indices) {
            weatherImageArray[i].setOnClickListener(this)
        } // 버큰을 클릭했을때 실행되는 리스너
        for (i in moodImageArray.indices) {
            moodImageArray[i].setOnClickListener(this)
        } // 버큰을 클릭했을때 실행되는 리스너

    }
    val textWatcher: TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            TODO("Not yet implemented")
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            TODO("Not yet implemented")
        }

        override fun afterTextChanged(s: Editable?) {
            updateWidgets()
        }

    }

    fun updateWidgets(){
        Log.d("progress : ","${progressBar?.progress == binding.progressBarWeatherMood.max}")
        Log.d("ads" , "${binding.progressBarWeatherMood.max}")
        if(progressBar?.progress == binding.progressBarWeatherMood.max ) {
            binding.nextBtnWeatherMood.isEnabled = true
        }
    }
    var statusWeather: Boolean = false
    var boolean2: Boolean = false
    private fun isSelected(
        imageArray: Array<ImageButton>,
        imageButton: ImageButton,
        status: String
    ) {
        for (i in 0..imageArray.size - 1) {
            if (imageArray[i] == imageButton) { // for문을 통해 i번째의 image버튼과 현재 Imagebutton이 같은경우
                if(status == "weather") {
                    if (!statusWeather)
                        progressBar!!.progress += 25
                    statusWeather = true
                    weatherStatus = imageArray[i].resources.getResourceEntryName(imageArray[i].id)
                }else{
                    if (!boolean2)
                        progressBar!!.progress += 25
                    boolean2 = true
                    moodStatus = imageArray[i].resources.getResourceEntryName(imageArray[i].id)
                }
                imageArray[i].setSelected(true)
                imageArray[i].startAnimation(scaleToBig)
                updateWidgets()
                Log.d("weatherStatus = " , weatherStatus.toString()) // weatherStatus , moodeStatus를 서버로 보내주기만 하면 됨.
                Log.d("moodStatus = " , moodStatus.toString())
            } else                           // for문을 통해 i번째의 image버튼과 현재 Imagebutton이 다른경우 -> 이경우는 어떻게 해도 4번 돔
                if (imageArray[i].isSelected) { // 만약 i번째가 선택되어있다면
                    imageArray[i].isSelected = false
                    Log.d("if else if문", "${i},${imageArray[i].isSelected}")
                    imageArray[i].startAnimation(scaleToSmall)

                }
        }

    } // 추후 리팩터링 필수 !!!!

    private fun btnScale(imageArray: Array<ImageButton>, imageButton: ImageButton): ImageButton? {
        var selectedBtn:ImageButton ?= null
        for(i in imageArray.indices){
            if(imageArray[i] == imageButton) {
                imageArray[i].isSelected = true
                imageArray[i].startAnimation(scaleToBig)
                selectedBtn = imageArray[i]
            }else{
                if(imageArray[i].isSelected){
                    imageArray[i].isSelected = false
                    imageArray[i].startAnimation(scaleToSmall)
                }
            }
        }
        return selectedBtn
        //여기에서는 어떤 버튼이 선택되어있는지 return해주기
    }
    override fun onClick(v: View?) {
        Log.d("on", "ok")
        Log.d("on", "${binding.root}")

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
            val back = binding.imageButtonBack

            val weatherImageArray = arrayOf<ImageButton>(
                sunny, rainy, snow, cloudy, windy
            )

            val moodImageArray = arrayOf<ImageButton>(
                happy, good, netural, bad, confused, angry, nervous, sad, sick
            )
            when (v) {
                snow -> isSelected(weatherImageArray, snow,"weather")
                rainy -> isSelected(weatherImageArray, rainy,"weather")
                sunny -> isSelected(weatherImageArray, sunny,"weather")
                cloudy -> isSelected(weatherImageArray, cloudy,"weather")
                windy -> isSelected(weatherImageArray, windy,"weather")

                happy -> isSelected(moodImageArray, happy,"mood")
                good -> isSelected(moodImageArray, good,"mood")
                netural -> isSelected(moodImageArray, netural,"mood")
                bad -> isSelected(moodImageArray, bad,"mood")
                confused -> isSelected(moodImageArray, confused,"mood")
                angry -> isSelected(moodImageArray, angry,"mood")
                nervous -> isSelected(moodImageArray, nervous,"mood")
                sad -> isSelected(moodImageArray, sad,"mood")
                sick -> isSelected(moodImageArray, sick,"mood")
                back -> {
                    Log.d("clicked", "success")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        } catch (e: NumberFormatException) {
            return
        }

    }

}