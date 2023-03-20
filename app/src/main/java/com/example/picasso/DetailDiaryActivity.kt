package com.example.picasso

import android.app.Activity
import android.app.AlertDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityDetailDiaryBinding
import com.example.picasso.databinding.CalendarAlterDialogBinding
import com.example.picasso.dto.CallDiary
import com.example.picasso.dto.DiaryDto
import com.example.picasso.dto.WeatherDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.callback.Callback

class DetailDiaryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDetailDiaryBinding.inflate(layoutInflater)
    }

    private val bindingDialog by lazy {
        CalendarAlterDialogBinding.inflate(layoutInflater)
    }
    private val api = WeatherService// 통신

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        // intent 받을 변수를 위한 변수들
        var title: String? = null
        var content: String? = null
        var mood: String? = null
        var weather: String? = null
        var date: String? = null
        var diaryId: Int? = null

        with(intent) {
            title = getStringExtra("title").toString()
            content = getStringExtra("content").toString()
            date = getStringExtra("date").toString()
            date = SimpleDateFormat("mmmm yyyy eeee").format(getStringExtra("date").toString())
            mood = getStringExtra("date").toString()
            weather = getStringExtra("date").toString()
            diaryId = getIntExtra("diaryId", 1)
        } // 이전의 액티비티에서 받아오기

        val backBtn = binding.backButton
        val calendarBtn = binding.calendarButton
        val editBtn = binding.writeButton
        val trashBtn = binding.trashButton
//        val calendar = LayoutInflater.from(this).inflate(R.layout.calendar_alter_dialog, null)
//        val a= findViewById(R.id.dialogCalendar)

//        val calendar = dialogView.findViewById<CalendarView>(R.id.dialogCalendar)
        backBtn.setOnClickListener{
            Log.d("clicked", "success")
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.textViewDate.text = date

//        binding.imageViewMood.setImageResource()

        calendarBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                calendar(
                    bindingDialog
                )
            }
        }// 캘린더를 눌렀을 때 나오는 버튼

        editBtn.setOnClickListener {

        }


    }

    // 내가 현재 만들고 싶은 함수

    fun backPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    } // 뒤로가기 버튼

    // 캘린더에서 날짜 눌렀을 때
    private suspend fun calendar(view: CalendarAlterDialogBinding) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.calendar_alter_dialog, null)
        val builder = AlertDialog.Builder(this).setView(dialogView)
        val dialog = builder.create()
        builder.show()
        // alterDialog를 보여줌
        // 사용자 id와 날짜로 검색하면됨
        val date: String =
            SimpleDateFormat("yyyy-mm-dd").format(view.dialogCalendar.date.toString())  // 사용자가 클릭한 날짜

        val imgArrayDiary = arrayOf(
            binding.textViewDiaryId, binding.TitleTextView, binding.ContentTextView
        )
        val imgArrayMoodWeather = arrayOf(
            binding.imageViewMood, binding.imageViewWeather
        )
        // Login with Firebase
        view.ButtonConfirm.setOnClickListener {
            val auth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            if (auth != null) {
                // 파이어베이스 유저를 통한 로그인 확인
                val user_id: String = auth.displayName.toString()
                val params = CallDiary(user_id, date)
                GlobalScope.launch {
                    val diary = getDiary(params)

                    val diaryArray = arrayOf(
                        diary?.diary_id, diary?.title, diary?.content
                    )

                    for (i in imgArrayDiary.indices) {
                        updateText(imgArrayDiary[i], diaryArray[i]!!)
                    }
//                    binding.TitleTextView.text = diary?.title
//                    binding.ContentTextView.text = diary?.content -> 기존의 중복된 코드를 수정
                    binding.textViewDate.text = date
                    // 여기까지 title content DiaryId
                    if (diary != null) {
                        getMood(diary.mood)
                        getWeather(diary.weather)
                    }
                    // 여기까지 Mood weather


                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                    }
                }
            } else {
                // 로컬유저 로그인 구현
            }
        }

//        val dateFormat:DateFormat = SimpleDateFormat("mmmm yyyy eeee")
//        val date: Date = Date(calendarView.date)

    }

    suspend fun getDiary(params: CallDiary): DiaryDto? {
        val response = api.communicate().getDiary(params)
        return if (response.isSuccessful) response.body() else null// response.body()에는 응답이 포함되어 있음.
    }

    private fun updateText(textView: TextView, newText: String) { // 텍스트 업데이트하기
        textView.text = newText
    }

    private fun updateImg(imageView: ImageView, imgUrl: Int) {
        imageView.setImageResource(imgUrl)
    }

    private fun getMood(mood: String) {
        //서버에서 오는 btn의 값에 따라 값이 달라져야함.
        val resourceId = resources.getIdentifier("ic_emotion_$mood", "drawable", this.packageName)
        binding.imageViewMood.setImageResource(resourceId)
    }

    private fun getWeather(weather: String){
        val resourceId = resources.getIdentifier("ic_emotion_$weather", "drawable", this.packageName)
        binding.imageViewWeather.setImageResource(resourceId)
    }

}