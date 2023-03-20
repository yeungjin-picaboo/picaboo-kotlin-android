package com.example.picasso


import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityDiaryBinding
import com.example.picasso.dto.WeatherDto
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Contract
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.function.LongFunction
import kotlin.Error
import java.io.Serializable

class DiaryActivity : AppCompatActivity() {
    var queue: RequestQueue? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val dotenv = dotenv {
        directory = "./assets"
        filename = "env"
        ignoreIfMalformed = true
    }

    private val binding by lazy {
        ActivityDiaryBinding.inflate(layoutInflater)
    }

    private val api = WeatherService
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        var weatherMood: WeatherDto? = null

        val ab = dotenv["MY_ENV_VAR1"]

        try {
            Log.d("a", ab)

        } catch (err: Error) {
            Log.d("Error dotenv", err.toString())

        }


        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val params = mutableMapOf<String, String>()
        if (queue == null) {
            queue = Volley.newRequestQueue(this)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Location 객체

        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("permissionCheck", "권한이 등록되어있음")
        } // 권한 체크를 한 후
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                    Log.d("ACCESS_FINE_LOCATION", "ACCESS_FINE_LOCATION")
                    mFusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener {
                            Log.d("isrun?", "rung")
                            latitude = it.latitude
                            longitude = it.longitude
                            params["latitude"] = latitude.toString()
                            params["longitude"] = (-longitude!!).toString()
                            Log.d("params", "$params")
                        } // 최근 위치를 한번만 가져오는 메서드.
                }
                permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                    Log.d("ACCESS_COARSE_LOCATION", "ACCESS_COARSE_LOCATION")
                    // Only approximate location access granted.
                }
                else -> {
                    // No location access granted.
                }
            }
        }


        locationPermissionRequest.launch(
            arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            )
        )

        //animation 객체 가져오기
        val fake_animation = AnimationUtils.loadAnimation(this, R.anim.updown)
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.updonw_reverse)
        val rolling = AnimationUtils.loadAnimation(this, R.anim.rolling_btn)
        val rolling2 = AnimationUtils.loadAnimation(this, R.anim.rolling_btn_reverse)
        val opacity_cal_reverse = AnimationUtils.loadAnimation(this, R.anim.opacity_cal_reverse)

        val content = binding.textViewContent
        val showCal = binding.showCal
        val whiteView = binding.WhiteView
        val textViewTitle = binding.textViewTitle
        val textViewContent = binding.textViewContent
        val progressBar = binding.progressBar
        val nextBtn = binding.nextBtn
        var state = true
        showCal.bringToFront()

        fun updateWidgets() {
            Log.d("what?", "the fuck?")
            if (textViewContent.text.toString().isNotEmpty()) {
                progressBar.progress = 50
                nextBtn.isEnabled = true
            } else {
                progressBar.progress = -50
                nextBtn.isEnabled = false
            }
        }

        val textWatcher: TextWatcher = object : TextWatcher { // editText에서 쓰이는 이벤트리스너 TextWatcher
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateWidgets()
            }
        }
        textViewContent.addTextChangedListener(textWatcher)
        binding.showCal.setOnClickListener {
            if (state) {
//                var s = AnimationSet(false)
//                s.addAnimation(opacity)
//                s.addAnimation(animation)
//                animationSet객체를 사용하면 xml의 fillAfter이 적용되지 않는 버그가 있음

                state = rollingBtn(showCal as AppCompatImageButton, state, rolling as AnimationSet)
                whiteView.startAnimation(fake_animation) // 캘린더가 내려오는 것 처럼 보이는 애니메이션
                textViewContent.startAnimation(opacity_cal_reverse) // textView를 불투명하게 만들기
                binding.root.removeView(textViewContent)  // 클릭을 방지하기 위해 textView삭제

                Log.d("text : ", "${textViewContent.text.toString().isEmpty()}")
            } else {
                state = rollingBtn(showCal as AppCompatImageButton, state, rolling2 as AnimationSet)

                whiteView.startAnimation(animation2) // 캘린더가 올라가는 것 처럼 보이는 애니메이션
                binding.root.addView(textViewContent) // textView를 보이게
                whiteView.visibility =
                    View.VISIBLE // 불투명했던 것을 보이게 하기 opacity값을 주지 않은 이유는 바로 올라간 것 처럼 보이기 위함

            }
        }


//        fun jsonObj() {
//            val jsonObject = JSONObject()
//            Log.d("weather", weather.toString())
//            jsonObject.put("content", textViewContent.text)
//            jsonObject.put("title",textViewTitle.text)
//            jsonObject.put("weather", weather)
//            Log.d("send", "$jsonObject")
//            postConnection(jsonObject, textViewContent,textViewTitle, weather!!)
//        }
        nextBtn.setOnClickListener {
            params["content"] = content.text.toString()
            try {
                if (params.isNotEmpty()) {
                    api.communicate().getWeatherMood(params).enqueue(object : Callback<WeatherDto> {
                        override fun onResponse(
                            call: Call<WeatherDto>,
                            response: retrofit2.Response<WeatherDto>
                        ) {
                            Log.d("call", call.request().toString()) // 보낸 객체
                            weatherMood = response.body()
                            Log.d("response", response.body()?.weather.toString()) // 받은 객체
                            Log.d("response", response.body()?.mood.toString()) // 받은 객체
                            Log.d("responseMod", weatherMood.toString())
                        }
                        override fun onFailure(call: Call<WeatherDto>, t: Throwable) {
                            Log.d("latitude onclicklistener", longitude.toString())
                            Log.d("Fail", t.message.toString())
                        }
                    })
                    val intent = Intent(this,WeatherMoodActivity::class.java)
                    Log.d("weatherMood 는 ", weatherMood.toString())
                    intent.putExtra("weatherMood",weatherMood)
                    startActivity(intent)
                    Log.d("nextBtn", "$params")
                } else {
                    Log.d("nextBtn", "$params")
                }

            } catch (e: NumberFormatException) {
                //error 처리
            }

        }


        binding.buttonTest.setOnClickListener {

//            api.communicate().getWeather(params).enqueue(object : Callback<WeatherDto> {
//                override fun onResponse(
//                    call: Call<WeatherDto>,
//                    response: retrofit2.Response<WeatherDto>
//                ) {
//                    Log.d("call", call.request().toString()) // 보낸 객체
//                    weather = response.body()
//                    Log.d("response", response.body()?.weather.toString()) // 받은 객체
//
//                }
//
//                override fun onFailure(call: Call<WeatherDto>, t: Throwable) {
//                    Log.d("latitude onclicklistener", longitude.toString())
//                    Log.d("Fail", t.message.toString())
//                }
//            })
            // api는 WeatherService Interface의 companion object 객체를 가져와서
            // 사용하는 것 call은 우리가 보내는 객체이고, response는 응답을 받아오는 것
            // 또한, getWeather에서 우리가 보내고싶은 객체 또는 변수를 넣어주고 그 타입은
            // Weather 서비스에서 정의한 타입과 똑같다.

        }

    }


//    private fun postConnection(whatIwant: JSONObject) {
//        val url: String = "http://10.0.2.2:3000/weather"
//        val responsed:JSONObject
//        val request = object : StringRequest(
//            Request.Method.POST,
//            url,
//            Response.Listener { response ->
//                responsed = response
//            }, Response.ErrorListener { error ->
//                Log.d("error :", "$error")
//            }) {
//            override fun getParams(): MutableMap<String, String>? {
//                val params = mutableMapOf<String, String>()
//                params["diary"] = "${whatIwant["diary"]}"
//                Log.d("params", "$params")
//                return params
//            }
//        }
//
//        queue?.add(request)
//    }

// Before you perform the actual permission request, check whether your app
// already has the permissions, and whether your app needs to show a permission
// rationale dialog. For more details, see Request permissions.
    /**
     *
     */
//    private fun postConnection(whatIwant: JSONObject, textViewTitle: TextView,textViewContent: TextView, weather: WeatherDto) {
//        val url: String = "http://10.0.2.2:3000/emotion"
//        Log.d("called", "called")
//        val request = object : StringRequest(
//            Request.Method.POST,
//            url,
//            Response.Listener { response ->
//                val mood = changeMood(response)
//                val intent = Intent(this, WeatherMoodActivity::class.java)
//                intent.putExtra("progress", 50) //progress
//                intent.putExtra("inputTitle", textViewTitle.text.toString()) //diary
//                intent.putExtra("inputContent", textViewContent.text.toString()) //diary
//                intent.putExtra("mood", mood) // 서버와 통신 후 가져온 Mood
//                intent.putExtra("weather", weather.weather) // 서버와 통신 후 가져온 weather
//                startActivity(intent)
//            }, Response.ErrorListener { error ->
//                Log.d("error :", "$error")
//            }) {
//            override fun getParams(): MutableMap<String, String>? {
//                val params = mutableMapOf<String, String>()
//                params["title"] = "${whatIwant["title"]}"
//                params["content"] = "${whatIwant["content"]}"
//                Log.d("params", "$params")
//                return params
//            }
//        }
//
//        queue?.add(request)
//    }

//    fun changeMood(response: String): String {
//        when (response) {
//            "positive" -> return "happy"
//            "negative" -> return "bad"
//            else -> return "neutral"
//        }
//    }

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

