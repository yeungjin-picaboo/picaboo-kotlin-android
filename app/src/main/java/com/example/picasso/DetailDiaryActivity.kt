package com.example.picasso

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityDetailDiaryBinding
import com.example.picasso.dto.DiariesListDto
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File


class DetailDiaryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetailDiaryBinding.inflate(layoutInflater)
    }

    private val api = WeatherService// 통신
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (queue == null) {
            queue = Volley.newRequestQueue(this)
        }

        val requiredPermission = READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this, requiredPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(requiredPermission), 1)
        } else {
            // Permission is already granted, continue with file access
        }
        // intent 받을 변수를 위한 변수들
        var title: String?
        var content: String?
        var mood: String? = null
        var weather: String? = null
        var date: String?
        var diaryId: Int?
        var source: String

        with(intent) {
            title = getStringExtra("title").toString()
            content = getStringExtra("content").toString()
            date = getStringExtra("date").toString()
//            date = SimpleDateFormat("mmmm yyyy eeee").format(getStringExtra("date").toString())
            mood = getStringExtra("emotion").toString()
            weather = getStringExtra("weather").toString()
            diaryId = getIntExtra("diaryId", 26)
            source = getStringExtra("source").toString()
            Log.d("Mood는", mood.toString())
            getMood(getMoodDrawable(mood!!))
            getWeather(getWeatherDrawable(weather!!))
        } // 이전의 액티비티에서 받아오기
        binding.ContentTextView.text = content
        binding.TitleTextView.text = title


        binding.imageViewPicture.clipToOutline = true

        Log.d("test", "통신으로 받아옴")

        var StringRequest = ImageRequest(source, // + URL 이런식으로 만듬
            { bitmap ->
                binding.imageViewPicture.setImageBitmap(bitmap)
            }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, { err ->
                Log.d("test", "${err} err")
            })
        queue?.add(StringRequest)

        val backBtn = binding.backButton
        val calendarBtn = binding.calendarButton
        val editBtn = binding.writeButton
        val deleteBtn = binding.trashButton

        backBtn.setOnClickListener {
            backPressed()
        }

        binding.textViewDate.text = date
        var result: List<DiariesListDto>? = null
        lifecycleScope.launch {

            // 일반유저 로그인
            if (fetchCalendarList() != null) {
                result = fetchCalendarList()
                Log.d("OK Btn", "positive")
            } else {
                Log.d("알수없는 에러", "코드 확인 요망")
            }

        }

        if (ContextCompat.checkSelfPermission(
                this, READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 1)
        }
        val path = Environment.getExternalStorageDirectory().path
        val imgFile = File("$path/Download/test.png")  // 이미지URL + url 해서 넣음

        Log.d("asd", "$imgFile")



        if (ContextCompat.checkSelfPermission(
                this, READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val imgFile = File("$path/Download/test.png")  // 이미지URL + url 해서 넣음
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            binding.imageViewPicture.setImageBitmap(myBitmap)
        }
        //이미지 라운드 처리


        calendarBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.calendar_alter_dialog, null)

            // Initialize dialog components
            val buttonInDialog = dialogView.findViewById<Button>(R.id.ButtonConfirm)
            val calendarView =
                dialogView.findViewById<MaterialCalendarView>(R.id.material_calendar_view)
            var selectedDate: LocalDate? = null

            // Set up listener for date selection
            calendarView.setOnDateChangedListener { widget, date, selected ->
                selectedDate = date.date // This is the selected date as a Date object
                Log.d("selectedDate", selectedDate.toString())
            }

            result?.forEach { dateString ->
                val dateComponents = dateString.date.split("-")
                val (year, month, day) = dateComponents.map { it.toInt() }
                val dates = listOf(CalendarDay.from(year, month, day))
                calendarView.addDecorator(DisabledDaysDecorator(dates))
            }
            val alertDialog =
                AlertDialog.Builder(this).setView(dialogView).setCancelable(true).create()

            buttonInDialog.setOnClickListener {
                // Launch coroutine to handle selected date
                lifecycleScope.launch(Dispatchers.Default) {

                    Log.d("buttonInDialog Btn", "clicked")
                    result?.forEach {
                        if (it.date == selectedDate.toString()) {
                            Log.d(
                                "받아온값",
                                api.communicateJwt(this@DetailDiaryActivity).getDetailedDiary(it.id)
                                    .toString()
                            )
                            Log.d(
                                "받아온값",
                                api.communicateJwt(this@DetailDiaryActivity).getDetailedDiary(it.id)
                                    .body().toString()
                            )
                            Log.d("Id는", it.id.toString())
                            calendar(it.id, selectedDate!!)
                        }
                    }
                    Log.d("선택한 날", selectedDate.toString())

                }
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        editBtn.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)
            with(intent) {
                putExtra("title", binding.TitleTextView.text)
                putExtra("content", binding.ContentTextView.text)
                putExtra("date", binding.textViewDate.text)
                putExtra("diaryId", diaryId)
                Log.d("디테일 페이지의 다이어리 아이디", diaryId.toString())
                putExtra("isEditing", true)
            }
            startActivity(intent)
        }// 수정 버튼

        deleteBtn.setOnClickListener {
            Log.d("deleteBtnClicked", "clicked")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("정말로 삭제하시겠어요?").setMessage("현재 일기가 삭제됩니다. 일기는 복구할 수 없으나 다시 작성하실수 있습니다")
                .setPositiveButton(
                    "예"
                ) { _, _ ->
                    Log.d("diaryId", "$diaryId")
                    lifecycleScope.launch {
                        val result = deleteDiary(diaryId!!)
                        Log.e("결과", result.toString())
                        if (result == true) {
//                            backPressed()
                            intent = Intent(this@DetailDiaryActivity, gallery::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            Log.d("OK Btn", "positive")
                        } else {
                            Log.d("알수없는 에러", "코드 확인 요망")
                        }
                    }
                }.setNegativeButton(
                    "아니요"
                ) { dialog, _ ->
                    Log.d("No Btn", "Negative")
                    dialog?.dismiss()
                }
            builder.show()
        }
    }

    // 내가 현재 만들고 싶은 함수
    fun backPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    } // 뒤로가기 버튼

    // 캘린더에서 확인 눌렀을 때
    private suspend fun calendar(id: Int, date: LocalDate) {
        val imgArrayDiary =
            arrayOf(binding.TitleTextView, binding.ContentTextView)

        // Local user login implementation
        try {
            val diary = api.communicateJwt(this@DetailDiaryActivity).getDetailedDiary(id).body()
            diary.let {
                if (it != null) {
                    val diaryArray = arrayOf(it.diary_id, it.title, it.content)
                    for (i in imgArrayDiary.indices) {
                        updateText(imgArrayDiary[i], diaryArray[i])
                    }
                    val dateFormat: String =
                        DateTimeFormatter.ofPattern("MMMM-yyyy-dd").format(date)  // 사용자가 클릭한 날짜
                    Log.d("dataFormat", dateFormat)
                    binding.textViewDate.text = dateFormat //date
                    Log.d("mood는", it.weather)
                    val moodDrawable = getMoodDrawable(it.mood)
                    val weatherDrawable = getWeatherDrawable(it.weather)
                    getPicture("https://picaboodiaryimage.s3.ap-northeast-2.amazonaws.com/${it.source}")
                    getMood(moodDrawable)
                    getWeather(weatherDrawable)
                }
            }
        } catch (err: Error) {
            Log.d("err", err.toString())
            Toast.makeText(this@DetailDiaryActivity, "An error occurred.", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun getMoodDrawable(mood: String): Int {
        return when (mood) {
            "positive" -> R.drawable.ic_emotion_good
            "negative" -> R.drawable.ic_emotion_bad
            else -> R.drawable.ic_emotion_netural
        }
    }


    private fun getWeatherDrawable(weather: String): Int {
        return when (weather) {
            "Wind" -> R.drawable.weather_ic_wind
            "Clouds" -> R.drawable.weather_ic_cloud
            "Rain" -> R.drawable.weather_ic_cloud_rain
            "Snow" -> R.drawable.weather_ic_cloud_snow
            else -> R.drawable.weather_ic_sun
        }
    }
    private fun updateText(textView: TextView, newText: Any) { // 텍스트 업데이트하기
        Log.d("updateText", "Running")
        textView.text = newText.toString()
    }

    private fun updateImg(imageView: ImageView, imgUrl: Int) {
        imageView.setImageResource(imgUrl)
    }

    private fun getMood(mood: Int) {
        //서버에서 오는 btn의 값에 따라 값이 달라져야함.
        val resourceId = resources.getIdentifier("ic_emotion_$mood", "drawable", this.packageName)
        Log.d("resourceId", resourceId.toString())
        Log.d("resourceId2", mood.toString())
        runOnUiThread {
            binding.imageViewWeather.setImageResource(mood)
        }

    }

    private fun getPicture(source: String) {
        val StringRequest = ImageRequest(source, // + URL 이런식으로 만듬
            { bitmap ->
                binding.imageViewPicture.setImageBitmap(bitmap)
            }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, { err ->
                Log.d("test", "${err} err")
            })
        queue?.add(StringRequest)
    }

    private fun getWeather(weather: Int) {
        Log.d("weather: ", weather.toString())
        val resourceId =
            resources.getIdentifier("weather_ic_$weather", "drawable", this.packageName)
        Log.d("resourceIdWeather", resourceId.toString())
        runOnUiThread {
            binding.imageViewMood.setImageResource(weather)
        }

    }
    private suspend fun deleteDiary(diaryId: Int): Boolean? {
        return try {
            val response = api.communicateJwt(this).deleteDiary(diaryId)
            Log.d("response Body DeleteDiary : ", response.toString())
            if (response.isSuccessful) response.body()?.ok else null
        } catch (err: Error) {
            false
        }

    }

    private suspend fun fetchCalendarList(): List<DiariesListDto>? {
        return try {
            binding.calendarButton.isEnabled = false // disable the button
            //로딩이 되기 전까지는 색이 다름
            val calendarList = getCalendarList()
            Log.d("캘린더리스트", calendarList.toString())
            if (calendarList != null) {
                binding.calendarButton.isEnabled = true // enable the button again
                binding.calendarButton.setColorFilter(Color.parseColor("#000000"))
            }
            calendarList
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch calendar list", e)
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