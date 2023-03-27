package com.example.picasso

import android.app.Activity
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
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityWeatherMoodBinding
import com.example.picasso.dto.WeatherDto
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class WeatherMoodActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var dataText: TextView
    var queue: RequestQueue? = null

    var scaleToBig: Animation? = null
    var scaleToSmall: Animation? = null
    var progress: Int? = null
    var inputDiary: String? = null
    var progressBar: ProgressBar? = null
    var intentWeather: String? = null
    var intentMood: String? = null
    var diaryMood: WeatherDto? = null
    var weatherStatus: String? = null
    var moodStatus: String? = null
    var diaryId: Int? = null
    var isModify: Boolean? = null

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
            val defaultValue = false // a default value to use if the extra is not found
            val extraKey = "isModify" // the key used to pass the boolean extra
            isModify = getBooleanExtra(extraKey, defaultValue)
            diaryId = getIntExtra("diaryId", 1)
            progress = getIntExtra("progressBar", 1)
            diaryMood = intent.extras?.getParcelable<WeatherDto>("weatherMood")
            Log.d("with : ", diaryMood.toString())
            inputDiary = getStringExtra("inputDiary").toString()
            intentMood = diaryMood?.mood
            intentWeather = diaryMood?.weather
            Log.d("intentMoodWeather", intentWeather.toString())
            Log.d("intentMoodWeather", intentMood.toString())
        }
        progressBar!!.progress = 50
        Log.d("intentMoodWeatherOutside", intentWeather.toString())
        Log.d("intentMoodWeatherOutside", intentMood.toString())
        when (intentMood) {
            "positive" -> {
                btnCickedStatus(happy)
                progressBar!!.progress += 25
            }
            "negative" -> btnCickedStatus(bad)
            else -> btnCickedStatus(neutral)
        }

        when (intentWeather) {
            "Wind" -> btnCickedStatus(windy)
            "Clouds" -> btnCickedStatus(cloudy)
            "Rain" -> btnCickedStatus(rainy)
            "Snow" -> btnCickedStatus(snow)
            "Clear" -> btnCickedStatus(sunny)
        }
        if (intentWeather != null) {
            progressBar!!.progress += 25
            if (intentMood != null)
                progressBar!!.progress += 25
        } else {
            if (intentMood != null)
                progressBar!!.progress += 25
        }
        if(progressBar!!.progress == 100){
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
        Log.d("progress is: ","${progressBar?.progress}")
        Log.d("progress : ","${binding.progressBarWeatherMood.max}")

        if (progressBar?.progress == 100) {
            Log.d("asd","asd")
            binding.nextBtnWeatherMood.isEnabled = true
        }
    }


    private fun isSelected(
        imageArray: Array<ImageButton>,
        imageButton: ImageButton,
        status: String
    ) {

        for (i in imageArray.indices) {

            if (imageArray[i] == imageButton) { // for문을 통해 i번째의 image버튼과 현재 Imagebutton이 같은경우

                if (status == "weather") {
                    progressBar!!.progress += 25
                    weatherStatus =
                        imageArray[i].resources.getResourceEntryName(imageArray[i].id)

                } else {

                    progressBar!!.progress += 25
                    moodStatus = imageArray[i].resources.getResourceEntryName(imageArray[i].id)
                }
                imageArray[i].isSelected = true
                imageArray[i].startAnimation(scaleToBig)
                updateWidgets()
                Log.d("imageBtn", "$imageButton")
                Log.d("imageBtnStatus : ", status)
                Log.d(
                    "weatherStatus = ",
                    weatherStatus.toString()
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

            val weatherImageArray = arrayOf<ImageButton>(
                sunny, rainy, snow, cloudy, windy
            )

            val moodImageArray = arrayOf<ImageButton>(
                happy, good, neutral, bad, confused, angry, nervous, sad, sick
            )
            when (v) {
                snow -> isSelected(weatherImageArray, snow, "weather")
                rainy -> isSelected(weatherImageArray, rainy, "weather")
                sunny -> isSelected(weatherImageArray, sunny, "weather")
                cloudy -> isSelected(weatherImageArray, cloudy, "weather")
                windy -> isSelected(weatherImageArray, windy, "weather")

                happy -> isSelected(moodImageArray, happy, "mood")
                good -> isSelected(moodImageArray, good, "mood")
                neutral -> isSelected(moodImageArray, neutral, "mood")
                bad -> isSelected(moodImageArray, bad, "mood")
                confused -> isSelected(moodImageArray, confused, "mood")
                angry -> isSelected(moodImageArray, angry, "mood")
                nervous -> isSelected(moodImageArray, nervous, "mood")
                sad -> isSelected(moodImageArray, sad, "mood")
                sick -> isSelected(moodImageArray, sick, "mood")
                back -> {
                    Log.d("clicked", "success")
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                next -> {
                    Log.d("clicked next btn1", "clicked")
                    val auth = FirebaseAuth.getInstance().currentUser
                    if (isModify == true) {
                        // 수정된경우
                        if (auth != null) {
                            val email = auth.email.toString()
                            Log.d("firebase email ", email)

                            val jsonObject = JSONObject()
                            jsonObject.put("email", email)
                            jsonObject.put("content", inputDiary)
                            jsonObject.put("diaryId", diaryId)
                            CoroutineScope(Dispatchers.IO).launch {
                                val a = sendApiCreate(jsonObject, isModify!!)
                                Log.d("result결과", "$a")
                            } // firebase 유저인 경우
                        } else {
                            // 웹 로그인 유저인 경우
                        }

                    }

                    if (auth != null) {
                        val email = auth.email.toString()
                        Log.d("clicked next btn2", "clicked")
                        Log.d("firebase email ", email)
                        val jsonObject = JSONObject()
                        jsonObject.put("email", email)
                        jsonObject.put("content", inputDiary)
                        CoroutineScope(Dispatchers.IO).launch {
                            val a = sendApiCreate(jsonObject, isModify!!)
                            Log.d("result결과", "$a")
                        } // firebase 유저인 경우
                    } else {
                        // 웹 로그인유저
                    }


                }
            }
        } catch (e: NumberFormatException) {
            return
        }

    }

    //    private suspend fun sendApiCreate(jsonObject: JSONObject, isModify: Boolean): Boolean? {
//        return withContext(Dispatchers.IO) {
//            var response: retrofit2.Response<Boolean>? = null
//            response = if (isModify) {
//                api.communicate().modifyDiary(jsonObject).execute()
//            } else {
//                api.communicate().createDiary(jsonObject).execute()
//            }
//            if (response?.isSuccessful!!) {
//                return@withContext response.body()
//            } else {
//                Log.d("requestApi", "Failed with error code ${response.code()}")
//                return@withContext response.body()
//            }
//        }
//    }
    private suspend fun sendApiCreate(jsonObject: JSONObject, isModify: Boolean): Boolean? {
        return withContext(Dispatchers.IO) {
            val api = api.communicate()
            val diaryId: Int = jsonObject.get("id") as Int
            jsonObject.remove("id")
            val response = if (isModify) {
                api.modifyDiary(diaryId, jsonObject).execute()
                // 수정돨 때
            } else {
                api.createDiary(jsonObject).execute()
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

    fun postConnection(whatIwant: JSONObject) {
        val url: String = "http://10.0.2.2:3000/google-user"

        val request = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener { response ->
                dataText.text = "Response: $response"
            }, Response.ErrorListener { error ->
                dataText.text = "Error: $error"
            }) {

            override fun getParams(): MutableMap<String, String>? {
                val params = mutableMapOf<String, String>()
                params["email"] = "${whatIwant["email"]}"
                params["nick_name"] = "${whatIwant["nickName"]}"
                return params
            }
        }
        queue?.add(request)
    }
}
