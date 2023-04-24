package com.example.picasso

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityWeatherMoodBinding
import com.example.picasso.dto.ResultMessageDto
import com.example.picasso.dto.WeatherDto
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WeatherMoodActivity : AppCompatActivity(), View.OnClickListener {

    var scaleToBig: Animation? = null
    var scaleToSmall: Animation? = null
    var progress: Int? = null
    var progressBar: ProgressBar? = null
    var diaryMood: WeatherDto? = null
    var weatherStatus: String? = null
    var moodStatus: String? = null
    var diaryId: Int? = null
    var isModify: Boolean? = null
    var date: String? = null
    private var diary: MutableMap<String, String>? = null

    private val binding by lazy {
        ActivityWeatherMoodBinding.inflate(layoutInflater)
    }
    private val api = WeatherService

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
        val neutral = binding.imageButtonNeutral
        val bad = binding.imageButtonBad
        val confused = binding.imageButtonConfused
        val angry = binding.imageButtonAngry
        val nervous = binding.imageButtonNervous
        val sad = binding.imageButtonSad
        val sick = binding.imageButtonSick
        val back = binding.imageButtonBack
        val next = binding.nextBtnWeatherMood

        progressBar = binding.progressBarWeatherMood

        scaleToBig = AnimationUtils.loadAnimation(this, R.anim.btn_to_big_scale)
        scaleToSmall = AnimationUtils.loadAnimation(this, R.anim.btn_to_small_scale)
        with(intent) {
            isModify = getBooleanExtra("isModify", false)
            Log.d("수정됬나? 74줄", isModify.toString())
            // null값이 올 수도 있기 때문에 타입을 바꿔줘야함
            if (isModify as Boolean) {
                //수정되었을 경우
                val bundle = intent.getBundleExtra("diary")
                diary = bundle?.getSerializable("map") as? MutableMap<String, String>
                Log.d("diary", diary.toString())
                diaryId = getIntExtra("diaryId", 1)

            } else {
                //수정되지 않았을 경우
                val bundle = intent.getBundleExtra("diary")
                diary = bundle?.getSerializable("map") as? MutableMap<String, String>
                Log.d("diary", diary.toString())
            }
            date = getStringExtra("date")
            Log.d("date", date.toString())
            progress = getIntExtra("progressBar", 1)

            val weatherMoodDto =
                intent.getParcelableExtra<WeatherDto>("weatherMoodDto") as WeatherDto // deprecated 이지만 대체할 사용법이 없음

            moodStatus = weatherMoodDto.mood
            weatherStatus = weatherMoodDto.weather
            Log.d("intentMoodWeather", weatherStatus.toString())
        }
        progressBar!!.progress = 50

        when (moodStatus) {
            "positive" -> {
                btnCickedStatus(happy)
                progressBar!!.progress += 25
            }
            "negative" -> btnCickedStatus(bad)
            else -> btnCickedStatus(neutral)
        }

        when (weatherStatus) {
            "Wind" -> btnCickedStatus(windy)
            "Clouds" -> btnCickedStatus(cloudy)
            "Rain" -> btnCickedStatus(rainy)
            "Snow" -> btnCickedStatus(snow)
            "Clear" -> btnCickedStatus(sunny)
        }
        if (weatherStatus != null) {
            progressBar!!.progress += 25
            if (moodStatus != null) progressBar!!.progress += 25
        } else {
            if (moodStatus != null) progressBar!!.progress += 25
        }
        if (progressBar!!.progress == 100) {
            binding.nextBtnWeatherMood.isEnabled = true
        }


        val weatherImageArray = arrayOf<ImageButton>(
            sunny, rainy, snow, cloudy, windy
        )
        val moodImageArray = arrayOf<ImageButton>(
            happy, good, neutral, bad, confused, angry, nervous, sad, sick
        )

        for (i in weatherImageArray.indices) {
            weatherImageArray[i].setOnClickListener(this)
        } // 버큰을 클릭했을때 실행되는 리스너
        for (i in moodImageArray.indices) {
            moodImageArray[i].setOnClickListener(this)
        } // 버큰을 클릭했을때 실행되는 리스너

        next.setOnClickListener(this)
        back.setOnClickListener(this)
    }


    val textWatcher: TextWatcher = object : TextWatcher {
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

    fun updateWidgets() {
        Log.d("progress is: ", "${progressBar?.progress}")
        Log.d("progress : ", "${binding.progressBarWeatherMood.max}")

        if (progressBar?.progress == 100) {
            Log.d("asd", "asd")
            binding.nextBtnWeatherMood.isEnabled = true
        }
    }


    private fun isSelected(
        imageArray: Array<ImageButton>, imageButton: ImageButton, status: String
    ) {

        for (i in imageArray.indices) {

            if (imageArray[i] == imageButton) { // for문을 통해 i번째의 image버튼과 현재 Imagebutton이 같은경우

                if (status == "weather") {
                    progressBar!!.progress += 25
                    weatherStatus =
                        imageArray[i].resources.getResourceEntryName(imageArray[i].id).substring(11)

                } else {

                    progressBar!!.progress += 25
                    moodStatus =
                        imageArray[i].resources.getResourceEntryName(imageArray[i].id).substring(11)
                }
                imageArray[i].isSelected = true
                imageArray[i].startAnimation(scaleToBig)
                updateWidgets()
                Log.d("imageBtn", "$imageButton")
                Log.d("imageBtnStatus : ", status)
                Log.d(
                    "weatherStatus = ", weatherStatus.toString()
                ) // weatherStatus , moodeStatus를 서버로 보내주기만 하면 됨.

                Log.d("moodStatus = ", moodStatus.toString())
            } else                           // for문을 통해 i번째의 image버튼과 현재 Imagebutton이 다른경우 -> 이경우는 어떻게 해도 4번 돔
                if (imageArray[i].isSelected) { // 만약 i번째가 선택되어있다면
                    imageArray[i].isSelected = false
                    imageArray[i].startAnimation(scaleToSmall)

                }
        }

    } // 추후 리팩터링 필수 !!!!

    private fun btnCickedStatus(imageButton: ImageButton) {
        Log.d("error?", imageButton.toString())
        imageButton.isSelected = true
        imageButton.startAnimation(scaleToBig)
        Log.d("clicked", "$imageButton")
    }

    private fun btnScale(imageArray: Array<ImageButton>, imageButton: ImageButton): ImageButton? {
        var selectedBtn: ImageButton? = null
        for (i in imageArray.indices) {
            if (imageArray[i] == imageButton) {
                imageArray[i].isSelected = true
                imageArray[i].startAnimation(scaleToBig)
                selectedBtn = imageArray[i]
            } else {
                if (imageArray[i].isSelected) {
                    imageArray[i].isSelected = false
                    imageArray[i].startAnimation(scaleToSmall)
                }
            }
        }
        return selectedBtn
        //여기에서는 어떤 버튼이 선택되어있는지 return해주기
    }

    override fun onClick(v: View?) {
        try {
            val weatherImageArray = arrayOf(
                binding.imageButtonSnow,
                binding.imageButtonRainy,
                binding.imageButtonSunny,
                binding.imageButtonCloudy,
                binding.imageButtonWindy
            )
            val moodImageArray = arrayOf(
                binding.imageButtonhappy,
                binding.imageButtonGood,
                binding.imageButtonNeutral,
                binding.imageButtonBad,
                binding.imageButtonConfused,
                binding.imageButtonAngry,
                binding.imageButtonNervous,
                binding.imageButtonSad,
                binding.imageButtonSick
            )
            when (v) {
                in weatherImageArray -> isSelected(weatherImageArray, v as ImageButton, "weather")
                in moodImageArray -> isSelected(moodImageArray, v as ImageButton, "mood")
                binding.imageButtonBack -> {
                    Log.d("clicked", "success")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                binding.nextBtnWeatherMood -> {
                    Log.d("clicked next btn1", "clicked")
                    val params = mutableMapOf<String, String>()
                    Log.d("diary : ", diary.toString())
                    params["title"] = diary?.getValue("title")!!
                    params["content"] = diary?.getValue("content")!!
                    params["emotion"] = moodStatus!!
                    params["weather"] = weatherStatus!!
                    if (isModify == true) {
                        // 수정된 경우

                        params["diaryId"] = diaryId.toString()
                        Log.d("넥스트 버튼을 눌렀을 때 ", params.toString())
//                        val jsonObject = JSONObject()
//                        jsonObject.put("content", diary?.getValue("content"))
//                        jsonObject.put("title", diary?.getValue("title"))
//                        jsonObject.put("diaryId", diaryId)
                        CoroutineScope(Dispatchers.IO).launch {
                            val a = sendApiCreate(params, isModify!!)
                            Log.d("result결과", "$a")
                            val intent = Intent(this@WeatherMoodActivity, gallery::class.java)
                            if (a?.ok == true) {
                                startActivity(intent)
                            } else {
                                Log.d("failed", "failed")
                            }
                        }

                    } else {
                        Log.d("clicked next btn2", "clicked")
//                            Log.d("firebase email ", email)
                        params["date"] = date!!
//                            jsonObject.put("email", email) 토큰값에 포함되어 있기 때문에 필요x
                        Log.d("jsonObj diary", diary.toString())

                        Log.d("jsonObj", params.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            val a = sendApiCreate(params)
                            Log.d("result결과", "$a")
                            val intent = Intent(this@WeatherMoodActivity, gallery::class.java)
                            if (a?.ok == true) {
                                startActivity(intent)
                            } else {
                                Log.d("failed", "failed")
                            }
                        }
                    }

                }
            }
        } catch (e: NumberFormatException) {
            return
        }
    }

    private suspend fun sendApiCreate(
        params: MutableMap<String, String>, isModify: Boolean = false
    ): ResultMessageDto? {
        return withContext(Dispatchers.IO) {
            val api = api.communicateJwt(this@WeatherMoodActivity)
            Log.d("sendApiCreate의 params는", params.toString())
            val response = if (isModify) {
                Log.e("다이어리 아이디", params["diaryId"].toString())
                val diaryId: Int = params["diaryId"]!!.toInt()

                params.remove("diaryId")
                api.modifyDiary(diaryId, params)
                // 수정돨 때
            } else {
                api.createDiary(params).execute()
                // 그냥 생성할 때
            }
            response.body().also {
                if (!response.isSuccessful) {
                    // Log the error code for debugging purposes
                    Log.d("requestapi", "failed with error code ${response.code()}")
                }
            }
        }
    }


}
