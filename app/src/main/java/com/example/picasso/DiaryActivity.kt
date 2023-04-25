package com.example.picasso


import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityDiaryBinding
import com.example.picasso.dto.DiariesListDto
import com.example.picasso.dto.WeatherDto
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

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
    var date: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        val params = mutableMapOf<String, String>()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val showCal = binding.showCal
        val whiteView = binding.WhiteView
        val textViewTitle = binding.textViewTitle
        val textViewContent = binding.textViewContent
        val progressBar = binding.progressBar
        val nextBtn = binding.nextBtn
        var state = true
        //animation 객체 가져오기
        showCal.bringToFront()
        val fake_animation = AnimationUtils.loadAnimation(this, R.anim.updown)
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.updonw_reverse)
        val rolling = AnimationUtils.loadAnimation(this, R.anim.rolling_btn)
        val rolling2 = AnimationUtils.loadAnimation(this, R.anim.rolling_btn_reverse)
        val opacity_cal_reverse = AnimationUtils.loadAnimation(this, R.anim.opacity_cal_reverse)
        //============================================================================================================

        binding.calendarView.setOnDateChangedListener { widget, dates, selected ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(dates.year, dates.month - 1, dates.day)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(selectedDate.time)
            Log.d("dateString", dateString)
            date = dateString // 캘린더에서 선택시 들어가는 날짜
            Log.d("params : ", params.toString())
            Toast.makeText(this, dateString, Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            test(fetchCalendarList(), binding.calendarView)
        }

        //================================================================================================
        fun updateWidgets() {
            if (textViewContent.text.toString().isNotEmpty()) {
                progressBar.progress = 50
                nextBtn.isEnabled = true
                nextBtn.setTextColor(
                    ContextCompat.getColor(
                        this@DiaryActivity,
                        R.color.ableButtonFontColor
                    )
                )
                nextBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@DiaryActivity,
                        R.color.ableButtonColor
                    )
                )
                Log.e("isRun?", "running")
            } else {
                progressBar.progress = -50
                nextBtn.isEnabled = false
                nextBtn.setTextColor(
                    ContextCompat.getColor(
                        this@DiaryActivity,
                        R.color.disableButtonFontColor
                    )
                )
                nextBtn.setBackgroundColor(
                    ContextCompat.getColor(
                        this@DiaryActivity,
                        R.color.disableButtonColor
                    )
                )
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
        val arrayTextView = arrayOf(textViewTitle, textViewContent)

        binding.showCal.setOnClickListener {
            // 반응형 뷰를 위한 애니메이션
            val alphaAnimation = AlphaAnimation(1f, 0f).apply {
                duration = 1000
            }
            val alpha2Animation = AlphaAnimation(0f, 1f).apply {
                duration = 1500
            }
            state =
                rollingBtn(showCal as AppCompatImageButton, state, if (state) rolling else rolling2)
            if (state) {
                whiteView.startAnimation(animation2)// 캘린더가 내려오는 것 처럼 보이는 애니메이션
                with(binding.root) {
                    removeView(binding.textLayout)
                    addView(binding.textLayout)
                }
                whiteView.visibility = View.VISIBLE
                binding.textLayout.startAnimation(alpha2Animation)
            } else {
                whiteView.startAnimation(fake_animation)
                textViewContent.startAnimation(opacity_cal_reverse)// textView를 불투명하게 만들기
                binding.textLayout.startAnimation(alphaAnimation)
                binding.root.removeView(binding.textLayout)// 클릭을 방지하기 위해 textView삭제
            }
        }

        if (queue == null) {
            queue = Volley.newRequestQueue(this)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Location 객체
        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
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
                    mFusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY, null
                    ).addOnSuccessListener { location ->
                        Log.d("isrun?", "rung")
                        latitude = location.latitude
                        longitude = location.longitude
                        params["latitude"] = latitude.toString()
                        params["longitude"] = (-longitude).toString()
                        Log.d("params", "$params")
                    }
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


        val isEditing: Boolean = intent.getBooleanExtra("isEditing", false)// 수정되었는지 확인하는 intent
        if (isEditing) {
            binding.showCal.isEnabled = false
            mFusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY, null
            ).addOnSuccessListener {
                Log.d("isrun?", "rung")
                latitude = it.latitude
                longitude = it.longitude
                params["latitude"] = latitude.toString()
                params["longitude"] = (-longitude).toString()
                Log.d("params", "$params")
            }
            val getTitle: String
            val getContent: String
            val getDate: List<String>
            val getDiaryId: Int
            with(intent) {
                getTitle = getStringExtra("title").toString()
                getContent = getStringExtra("content").toString()
                date = getStringExtra("date") // 전역변수
                getDate = getStringExtra("date")?.split("-")!! // 지역변수
//                date = SimpleDateFormat("mmmm yyyy eeee").format(getStringExtra("date").toString())
                getDiaryId = getIntExtra("diaryId", 1)
            } // 이전의 액티비티(Detail Diary)에서 받아오기
            Log.d(
                "이전의 엑티비티에서 받아온 값들",
                "getTitle : $getTitle, getContent : $getContent, getDate : $getDate, getDiaryId : $getDiaryId"
            )
            val getIntentArray = arrayOf(getTitle, getContent)

            for (i in arrayTextView.indices) {
                arrayTextView[i].setText(getIntentArray[i])
            }// DetailDairyActivity에서 받아온 title content를 삽입

            val calView = binding.calendarView
            val calendar = Calendar.getInstance()

            calendar.set(
                getDate[0].toInt(), getDate[1].toInt(), getDate[2].toInt()
            ) //!getDate의 데이터를 확인 후에 수정

//            val timeStamp = calendar.timeInMillis
//            calView.setDate(timeStamp, true, true) // 날짜 설정

            nextBtn.setOnClickListener {
                params["content"] = textViewContent.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    nextBtnClicked(params, isEditing, getDiaryId)
                }
            }
        }// 수정되었을때
        else { // 일반 버전
            locationPermissionRequest.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
                )
            ) // 권한 요청
            nextBtn.setOnClickListener {
                params["content"] = textViewContent.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    nextBtnClicked(params)
                }
            }
        }
    }

    private suspend fun nextBtnClicked(
        params: MutableMap<String, String>, isEditing: Boolean = false, diaryId: Int = 1
    ) {
        try {
            if (params.isNotEmpty()) {
                communicateToNextIntent(params, isEditing, diaryId)
            } else {
                Log.d("nextBtn", "params가 비었음.")
            }
        } catch (e: NumberFormatException) {
            //error 처리
        }
    }

    private suspend fun communicateToNextIntent(
        params: MutableMap<String, String>, isEditing: Boolean, diaryId: Int = 1
    ) {
        Log.d("isRunning?", "Running")
        val intent = Intent(this, WeatherMoodActivity::class.java)
        val weatherMood: WeatherDto? = requestApi(params) // return is weatherDto

        params["title"] = binding.textViewTitle.text.toString()
        val bundle = Bundle().apply {
            putSerializable("map", params as Serializable)
        } // params를 bundle형식으로 포맷(?)후 키값은 map으로 받는다
        Log.d("weatherMood 는 ", weatherMood.toString())
        with(intent) {
            putExtra("weatherMoodDto", weatherMood)
            putExtra("diary", bundle)
            if (date == null) {
                date = LocalDate.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE) // 유저가 글을 처음 작성했는데 날짜를 선택하지 않았을 때
                putExtra("date", date)
            } else
                putExtra("date", date)
        }
        if (isEditing) {
            Log.d("이전 엑티비티에서 받아온 날짜는 ", date.toString())
            Log.d("파람스는", params.toString())
            Log.d("번들은", bundle.toString())
            Log.d("최종 DiaryID는", diaryId.toString())

            with(intent) {
                putExtra("diaryId", diaryId)
                putExtra("isModify", true)
            }
        } else {
            Log.d("파람스는", params.toString())
            Log.d("번들은", bundle.toString())
            Log.d("nextBtn", "$params")
        }
        startActivity(intent)
    }

    private suspend fun requestApi(params: MutableMap<String, String>): WeatherDto? {
        Log.e("requestApi In function", params.toString())
        return withContext(Dispatchers.IO) {
            val response = api.communicateJwt(this@DiaryActivity).getWeatherMood(params).execute()
            if (response.isSuccessful) {
                return@withContext response.body()
            } else {
                Log.d("requestApi", "Failed with error code ${response.code()}")
                return@withContext null
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
        wiget: AppCompatImageButton, state: Boolean, animation: Animation
    ): Boolean {
        return if (state) {
            wiget.startAnimation(animation)
            !state
        } else {
            wiget.startAnimation(animation)
            !state
        }
    }

    private suspend fun fetchCalendarList(): List<DiariesListDto>? {
        return try {
//            binding.calendarButton.isEnabled = false // disable the button
            //로딩이 되기 전까지는 색이 다름
            val calendarList = getCalendarList()
            Log.d("캘린더리스트", calendarList.toString())
//            if (calendarList != null) {
//                binding.calendarButton.isEnabled = true // enable the button again
//                binding.calendarButton.setColorFilter(Color.parseColor("#000000"))
//            }
            calendarList
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Failed to fetch calendar list", e)
            null
        }
    }

    private suspend fun getCalendarList(): List<DiariesListDto>? {
        return try {
            val response = api.communicateJwt(this).getDiariesList()
            Log.d("response Body getCalendarList : ", response.body().toString())
            if (response.isSuccessful) response.body() else null
        } catch (err: Error) {
            Log.d("whereError : getCalendarList / ", err.toString())
            null
        }
    }
}

