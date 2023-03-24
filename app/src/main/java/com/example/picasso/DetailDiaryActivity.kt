package com.example.picasso

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityDetailDiaryBinding
import com.example.picasso.databinding.CalendarAlterDialogBinding
import com.example.picasso.dto.CallDiaryDto
import com.example.picasso.dto.DiaryDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


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


        if (ContextCompat.checkSelfPermission(
                this, READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 1)
        }
        val path = Environment.getExternalStorageDirectory().path
        val imgFile = File("$path/Download/test.png")  // 이미지URL + url 해서 넣음

        Log.d("asd", "$imgFile")

        val files = imgFile.listFiles()
        Log.d("files", "$files")
        val myBitmap = BitmapFactory.decodeFile(imgFile.path)
        binding.imageViewPicture.setImageBitmap(myBitmap)
        if (ContextCompat.checkSelfPermission(
                this, READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val path = Environment.getExternalStorageDirectory().path
            val imgFile = File("$path/Download/test.png")  // 이미지URL + url 해서 넣음
            imgFile.exists()
            Log.d("asd", "$imgFile")

            val files = imgFile.listFiles()
            Log.d("files", "$files")
            val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
            binding.imageViewPicture.setImageBitmap(myBitmap)
        }
        //이미지 라운드 처리


        calendarBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(this).inflate(R.layout.calendar_alter_dialog, null)
            val builder = AlertDialog.Builder(this).setView(dialogView)
            val button = dialogView.findViewById<Button>(R.id.ButtonConfirm)
            val dialogCal = dialogView.findViewById<CalendarView>(R.id.dialogCalendar)
            val alertDialog = builder.show()
            // binding으로 처리시 해결이 안되는 에러가 있음..
            dialogCal.setOnDateChangeListener { view, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                val dateClicked = calendar.time
                Log.d("dateFormat", date.toString())
                val dateFormat: String =
                    SimpleDateFormat("MMMM-yyyy-dd").format(dateClicked)  // 사용자가 클릭한 날짜
                Log.d("dataFormat", dateFormat)
                binding.textViewDate.text = dateFormat //date

            }
            button.setOnClickListener {
                Log.d("clicked", "onClickListener")
                lifecycleScope.launch {
                    withContext(Dispatchers.Default) {
                        calendar(bindingDialog)
                    }
                    alertDialog.dismiss()
                }


            }
            alertDialog.show()
        }// 캘린더를 눌렀을 때 나오는 버튼

        editBtn.setOnClickListener {
            var intent = Intent(this, DiaryActivity::class.java)
            intent.putExtra("title", binding.TitleTextView.text)
            intent.putExtra("content", binding.ContentTextView.text)
            intent.putExtra("date", binding.textViewDate.text)
            intent.putExtra("diaryId", binding.textViewDiaryId.text)
            intent.putExtra("isEditing", true)
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
                    val result = deleteDiary(diaryId!!)
                    if (result == true) {
                        backPressed()
                        Log.d("OK Btn", "positive")
                    } else {
                        Log.d("알수없는 에러", "코드 확인 요망")
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

    // 캘린더에서 날짜 눌렀을 때
    private suspend fun calendar(view: CalendarAlterDialogBinding) {
        // alterDialog를 보여줌
        // 사용자 id와 날짜로 검색하면됨

        Log.d("date : ", view.dialogCalendar.date.toString())

        val imgArrayDiary = arrayOf(
            binding.textViewDiaryId, binding.TitleTextView, binding.ContentTextView
        )

        Log.d("clicked", "clicked")
        val auth: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (auth != null) {
            // 파이어베이스 유저를 통한 로그인 확인
            val userId: String = auth.displayName.toString()
            val params = CallDiaryDto(view.dialogCalendar.date.toString(), userId)
            lifecycleScope.launch {
                val diary = getDiary(params)
                Log.d("diary", diary.toString())

                try {
                    if (diary != null) {
                        val diaryArray = arrayOf(
                            diary.diary_id, diary.title, diary.content
                        )

                        for (i in imgArrayDiary.indices) {
                            updateText(imgArrayDiary[i], diaryArray[i])
                        }

                        // 여기까지 title content DiaryId
                        // 이미지를 받아오는 함수만 추가하면 됨

                        val moodDrawable = when (diary.mood) {
                            "positive" -> R.drawable.ic_emotion_good
                            "negative" -> R.drawable.ic_emotion_bad
                            else -> R.drawable.ic_emotion_netural
                        }
                        getMood(moodDrawable)
                        val weatherDrawable = when (diary.weather) {
                            "Wind" -> R.drawable.weather_ic_wind
                            "Clouds" -> R.drawable.weather_ic_cloud
                            "Rain" -> R.drawable.weather_ic_cloud_rain
                            "Snow" -> R.drawable.weather_ic_cloud_snow
                            else -> R.drawable.weather_ic_sun
                        }
                        getWeather(weatherDrawable)
                    }
                } catch (err: Error) {
                    Log.d("err", err.toString())
                    Toast.makeText(
                        this@DetailDiaryActivity, "Error 가 발생했습니다.", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            // 로컬유저 로그인 구현
        }


//        val dateFormat:DateFormat = SimpleDateFormat("mmmm yyyy eeee")
//        val date: Date = Date(calendarView.date)

    }

    suspend fun getDiary(params: CallDiaryDto): DiaryDto? {
        Log.d("getDiary", "Running")
        val response = api.communicate().getDiary(params)
        Log.d("response : ", response.body().toString())
        return if (response.isSuccessful) response.body() else null// response.body()에는 응답이 포함되어 있음.
    }

    private fun updateText(textView: TextView, newText: String) { // 텍스트 업데이트하기
        Log.d("updateText", "Running")
        textView.text = newText
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

    private fun deleteDiary(diaryId: Int): Boolean? {
        try {
            val response = api.communicate().deleteDiary(diaryId).execute()
            return if (response.isSuccessful) response.body() else null
        } catch (err: Error) {
            return false
        }

    }
}