package com.example.picasso

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityDetailDiaryBinding
import com.example.picasso.databinding.CalendarAlterDialogBinding
import com.example.picasso.dto.CallDiaryDto
import com.example.picasso.dto.DiariesListDto
import com.example.picasso.dto.DiaryDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.util.*


class DetailDiaryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetailDiaryBinding.inflate(layoutInflater)
    }

    private val api = WeatherService// 통신

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

        with(intent) {
            title = getStringExtra("title").toString()
            content = getStringExtra("content").toString()
            date = getStringExtra("date").toString()
//            date = SimpleDateFormat("mmmm yyyy eeee").format(getStringExtra("date").toString())
            mood = getStringExtra("mood").toString()
            weather = getStringExtra("weather").toString()
            diaryId = getIntExtra("diaryId", 1)
        } // 이전의 액티비티에서 받아오기
        binding.ContentTextView.text = content
        binding.TitleTextView.text = title
        binding.textViewDiaryId.text = diaryId.toString()


        val backBtn = binding.backButton
        val calendarBtn = binding.calendarButton
        val editBtn = binding.writeButton
        val deleteBtn = binding.trashButton

        backBtn.setOnClickListener {
            backPressed()
        }

        binding.textViewDate.text = date

        var result: DiariesListDto? = null
        lifecycleScope.launch {
            val auth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            if (auth != null) {
                auth.let {
                    if (fetchCalendarList(it.email.toString()) != null) {
                        result = fetchCalendarList(it.email.toString())
                        Log.d("OK Btn", "positive")
                    } else {
                        Log.d("알수없는 에러", "코드 확인 요망")
                    }
                }
            } else {
                // 일반유저 로그인
                val email = "melon7256@naver.com"
                if (fetchCalendarList(email) != null) {
                    result = fetchCalendarList(email)
                    Log.d("OK Btn", "positive")
                } else {
                    Log.d("알수없는 에러2", "코드 확인 요망")
                }
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

        //        calendarBtn.setOnClickListener {
//            val dialogView = LayoutInflater.from(this).inflate(R.layout.calendar_alter_dialog, null)
//            val buttonInDialog = dialogView.findViewById<Button>(R.id.ButtonConfirm)
//            val dialogCal = dialogView.findViewById<CalendarView>(R.id.dialogCalendar)
//            val calendarView =
//                dialogView.findViewById<MaterialCalendarView>(R.id.material_calendar_view)
//            var selectedDate:org.threeten.bp.LocalDate ?= null
//            calendarView.setOnDateChangedListener { widget, date, selected ->
//                Log.d("selectedDate", "on")
//                // Do something with the selected date
//                selectedDate = date.date // This is the selected date as a Date object
//                Log.d("selectedDate ", selectedDate.toString())
//            }
//        class DisabledDaysDecorator(private val disabledDates: List<CalendarDay>) :
//            DayViewDecorator {
//            override fun shouldDecorate(day: CalendarDay?): Boolean {
//                return day != null && disabledDates.contains(day)
//            }
//
//            override fun decorate(view: DayViewFacade?) {
//                view?.setDaysDisabled(true)
//                view?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#EEEEEE")))
//            }
//        }
//
//            val dates = ArrayList<CalendarDay>()
//            result?.date?.forEach { dateString ->
//                val dateComponents = dateString.split("-")
//                val (year, month, day) = dateComponents.map { it.toInt() }
////                calendarView.setDateSelected(CalendarDay.from(year, month, day), true)
//                dates.add(CalendarDay.from(year, month, day))
//                val a = DisabledDaysDecorator(dates)
//                calendarView.addDecorator(DisabledDaysDecorator(dates))
//
//            } //! 한번쯤 볼법한 코드임
//
//            Log.d("not null", "$calendarView")
//
//            val alertDialog = AlertDialog.Builder(this)
//                .setView(dialogView)
//                .setCancelable(true)
//                .create()
//
//            buttonInDialog.setOnClickListener {
//
//                lifecycleScope.launch(Dispatchers.Default) {
//                    calendar(selectedDate!!)
//                }
//                alertDialog.dismiss()
//            }
//
//            alertDialog.show()
//        }

        class DisabledDaysDecorator(private val disabledDates: List<CalendarDay>) :
            DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay?): Boolean {
                return day != null && disabledDates.contains(day)
            }

            override fun decorate(view: DayViewFacade?) {
                view?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#EEEEEE")))
            }
        }

        calendarBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.calendar_alter_dialog, null)

            // Initialize dialog components
            val buttonInDialog = dialogView.findViewById<Button>(R.id.ButtonConfirm)
            val calendarView =
                dialogView.findViewById<MaterialCalendarView>(R.id.material_calendar_view)
            var selectedDate: org.threeten.bp.LocalDate? = null

            // Set up listener for date selection
            calendarView.setOnDateChangedListener { widget, date, selected ->
                selectedDate = date.date // This is the selected date as a Date object
                Log.d("selectedDate", selectedDate.toString())
            }

            // Decorate disabled dates in calendar
            result?.date?.forEach { dateString ->
                val dateComponents = dateString.split("-")
                val (year, month, day) = dateComponents.map { it.toInt() }
                val dates = listOf(CalendarDay.from(year, month, day))
                calendarView.addDecorator(DisabledDaysDecorator(dates))
            }
//            calendarView.addDecorator(SundayDecorator)
            // Initialize and show alert dialog
            val alertDialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            buttonInDialog.setOnClickListener {
                // Launch coroutine to handle selected date
                lifecycleScope.launch(Dispatchers.Default) {
                    Log.d("buttonInDialog Btn", "clicked")
                    calendar(selectedDate!!)
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
                putExtra("diaryId", binding.textViewDiaryId.text)
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
                        val result = deleteDiary(diaryId!!, isGoogleUser()?.email.toString())
                        if (result == true) {
                            backPressed()
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
    private suspend fun calendar(selectDate: org.threeten.bp.LocalDate) {
        val imgArrayDiary =
            arrayOf(binding.textViewDiaryId, binding.TitleTextView, binding.ContentTextView)
        val selectDateYear = DateTimeFormatter.ofPattern("yyyy")
            .format(selectDate)
        val selectDateMonth = DateTimeFormatter.ofPattern("mm")
            .format(selectDate)
        val auth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        auth?.let { firebaseUser ->
            val userId = firebaseUser.displayName.toString()

            val params = CallDiaryDto(userId)
            try {
                val diary = getDiary(params, selectDateYear, selectDateMonth)
                diary?.let {
                    val diaryArray = arrayOf(it.diary_id, it.title, it.content)
                    for (i in imgArrayDiary.indices) {
                        updateText(imgArrayDiary[i], diaryArray[i])
                    }
                    val dateFormat: String =
                        DateTimeFormatter.ofPattern("MMMM-yyyy-dd")
                            .format(selectDate)  // 사용자가 클릭한 날짜
                    Log.d("dataFormat", dateFormat)
                    binding.textViewDate.text = dateFormat //date
                    val moodDrawable = getMoodDrawable(it.mood)
                    val weatherDrawable = getWeatherDrawable(it.weather)
                    getMood(moodDrawable)
                    getWeather(weatherDrawable)
                }
            } catch (err: Error) {
                Log.d("err", err.toString())
                Toast.makeText(this@DetailDiaryActivity, "An error occurred.", Toast.LENGTH_SHORT)
                    .show()
            }
        } ?: run {
            // Local user login implementation
            val params = CallDiaryDto(selectDate.toString())
            try {
                val diary = getDiary(params, selectDateYear, selectDateMonth)
                diary?.let {
                    val diaryArray = arrayOf(it.diary_id, it.title, it.content)
                    for (i in imgArrayDiary.indices) {
                        updateText(imgArrayDiary[i], diaryArray[i])
                    }
                    val dateFormat: String =
                        DateTimeFormatter.ofPattern("MMMM-yyyy-dd")
                            .format(selectDate)  // 사용자가 클릭한 날짜
                    Log.d("dataFormat", dateFormat)
                    binding.textViewDate.text = dateFormat //date
                    val moodDrawable = getMoodDrawable(it.mood)
                    val weatherDrawable = getWeatherDrawable(it.weather)
                    getMood(moodDrawable)
                    getWeather(weatherDrawable)
                }
            } catch (err: Error) {
                Log.d("err", err.toString())
                Toast.makeText(this@DetailDiaryActivity, "An error occurred.", Toast.LENGTH_SHORT)
                    .show()
            }
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

    suspend fun getDiary(params: CallDiaryDto, year: String, month: String): DiaryDto? {
        Log.d("getDiary", "Running")
        val response = api.communicateJwt(this).getDiary(year, month, params)
        Log.d("response : ", response.body().toString())
        return if (response.isSuccessful) response.body() else null// response.body()에는 응답이 포함되어 있음.
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

    private fun getWeather(weather: Int) {
        Log.d("weather: ", weather.toString())
        val resourceId =
            resources.getIdentifier("weather_ic_$weather", "drawable", this.packageName)
        Log.d("resourceIdWeather", resourceId.toString())
        runOnUiThread {
            binding.imageViewMood.setImageResource(weather)
        }

    }

    private fun editContent() {

    }

    private suspend fun deleteDiary(diaryId: Int,userId:String): Boolean? {
        return try {
            val response = api.communicateJwt(this).deleteDiary(diaryId, userId)
            Log.d("response Body DeleteDiary : ", response.toString())
            if (response.isSuccessful) response.body() else null
        } catch (err: Error) {
            false
        }

    }

    private suspend fun fetchCalendarList(userId: String): DiariesListDto? {
        return try {
            binding.calendarButton.isEnabled = false // disable the button
            //로딩이 되기 전까지는 색이 다름
            val calendarList = getCalendarList(userId)
            if (calendarList != null) {
                binding.calendarButton.setColorFilter(Color.parseColor("#000000"))

                binding.calendarButton.isEnabled = true // enable the button again
            }
            calendarList
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch calendar list", e)
            null
        }
    }

    private suspend fun getCalendarList(userId: String): DiariesListDto? {
        return try {
            val response = api.communicateJwt(this).getDiariesList(userId)
            Log.d("response Body getCalendarList : ", response.body().toString())
            if (response.isSuccessful) response.body() else null
        } catch (err: Error) {
            Log.d("whereError : getCalendarList / ", err.toString())
            null
        }
    }

    private fun isGoogleUser(): FirebaseUser? {
        val auth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        return auth
    }
}