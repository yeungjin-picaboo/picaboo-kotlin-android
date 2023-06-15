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
import com.developer.kalert.KAlertDialog
import com.example.picasso.api.ApiService
import com.example.picasso.databinding.ActivityDiaryBinding
import com.example.picasso.dto.diary.DiariesListDto
import com.example.picasso.dto.diary.make.WeatherDto
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
    var queue: RequestQueue? = null // リクエストキュー
    private lateinit var mFusedLocationClient: FusedLocationProviderClient // 位置情報クライアント
    private val dotenv = dotenv { // dotenvの設定
        directory = "./assets"
        filename = "env"
        ignoreIfMalformed = true
    }

    private val binding by lazy { // View バインディング
        ActivityDiaryBinding.inflate(layoutInflater)
    }

    private val api = ApiService // APIサービス
    var latitude: Double = 0.0 // 緯度
    var longitude: Double = 0.0 // 経度
    var date: String? = null // 日付
    var diaryDto: List<DiariesListDto>? = null // 日記のデータ
    override fun onCreate(savedInstanceState: Bundle?) {
        val params = mutableMapOf<String, String>() // リクエストパラメータ
        super.onCreate(savedInstanceState)
        setContentView(binding.root) // レイアウトの設定
        val showCal = binding.showCal // カレンダー表示ボタン
        val whiteView = binding.WhiteView // ホワイトビュー
        val textViewTitle = binding.textViewTitle // タイトルテキストビュー
        val textViewContent = binding.textViewContent // コンテンツテキストビュー
        val progressBar = binding.progressBar // プログレスバー
        val nextBtn = binding.nextBtn // 次へボタン
        var state = true // 状態フラグ
        // アニメーションオブジェクトの取得
        showCal.bringToFront()
        val fake_animation = AnimationUtils.loadAnimation(this, R.anim.updown) // 上下アニメーション
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.updonw_reverse) // 上下逆アニメーション
        val rolling = AnimationUtils.loadAnimation(this, R.anim.rolling_btn) // ローリングアニメーション
        val rolling2 =
            AnimationUtils.loadAnimation(this, R.anim.rolling_btn_reverse) // ローリング逆アニメーション
        val opacity_cal_reverse =
            AnimationUtils.loadAnimation(this, R.anim.opacity_cal_reverse) // 不透明度逆アニメーション
        //============================================================================================================

        binding.calendarView.setOnDateChangedListener { widget, dates, selected ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(dates.year, dates.month - 1, dates.day)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateString = dateFormat.format(selectedDate.time)
            Log.d("dateString", dateString)
            date = dateString // 選択された日付を設定
            val builder = KAlertDialog(this, KAlertDialog.ERROR_TYPE)
            diaryDto?.forEach {
                if (date == it.date) {
                    builder.setTitleText("間違った入力").setContentText("その日には既に日記があります。他の日付を選択してください。")
                        .setConfirmClickListener(
                            "はい"
                        ) {
                            date = null
                            it.cancel()
                        }
                        .show()
                }
            }
            Log.d("params : ", params.toString())
            Toast.makeText(this, dateString, Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            diaryDto = returnTest(fetchCalendarList(), binding.calendarView)
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

        val textWatcher: TextWatcher = object : TextWatcher { // テキスト変更リスナー
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateWidgets()
            }
        }
        textViewContent.addTextChangedListener(textWatcher)
        val arrayTextView = arrayOf(textViewTitle, textViewContent)

        binding.showCal.setOnClickListener {
            // レスポンシブビューのためのアニメーション
            val alphaAnimation = AlphaAnimation(1f, 0f).apply {
                duration = 1000
            }
            val alpha2Animation = AlphaAnimation(0f, 1f).apply {
                duration = 1500
            }
            state =
                rollingBtn(showCal as AppCompatImageButton, state, if (state) rolling else rolling2)
            if (state) {
                whiteView.startAnimation(animation2) // カレンダーが表示されるように見えるアニメーション
                with(binding.root) {
                    removeView(binding.textLayout)
                    addView(binding.textLayout)
                }
                whiteView.visibility = View.VISIBLE
                binding.textLayout.startAnimation(alpha2Animation)
            } else {
                whiteView.startAnimation(fake_animation)
                textViewContent.startAnimation(opacity_cal_reverse) // テキストビューを不透明にする
                binding.textLayout.startAnimation(alphaAnimation)
                binding.root.removeView(binding.textLayout) // クリックを防止するため、テキストビューを削除
            }
        }

        if (queue == null) {
            queue = Volley.newRequestQueue(this)
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // 位置情報オブジェクト
        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("permissionCheck", "権限が付与されています")
        } // 権限チェック後
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


        val isEditing: Boolean = intent.getBooleanExtra("isEditing", false)// 編集フラグの確認
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
                date = getStringExtra("date") // グローバル変数
                getDate = getStringExtra("date")?.split("-")!! // ローカル変数
//                date = SimpleDateFormat("mmmm yyyy eeee").format(getStringExtra("date").toString())
                getDiaryId = getIntExtra("diaryId", 1)
            } // 前のアクティビティ(Detail Diary)から取得
            Log.d(
                "前のアクティビティから取得した値",
                "getTitle : $getTitle, getContent : $getContent, getDate : $getDate, getDiaryId : $getDiaryId"
            )
            val getIntentArray = arrayOf(getTitle, getContent)

            for (i in arrayTextView.indices) {
                arrayTextView[i].setText(getIntentArray[i])
            }// DetailDairyActivityから受け取ったタイトルとコンテンツを設定

            val calView = binding.calendarView
            val calendar = Calendar.getInstance()

//            calendar.set(
//                getDate[0].toInt(), getDate[1].toInt(), getDate[2].toInt()
//            ) //!getDateのデータを確認して修正

//            val timeStamp = calendar.timeInMillis
//            calView.setDate(timeStamp, true, true) // 日付を設定

            nextBtn.setOnClickListener {
                params["content"] = textViewContent.text.toString()
                CoroutineScope(Dispatchers.IO).launch {
                    nextBtnClicked(params, isEditing, getDiaryId)
                }
            }
        }// 編集時
        else { // 通常バージョン
            locationPermissionRequest.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION
                )
            ) // 権限リクエスト
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
                Log.d("nextBtn", "paramsが空です。")
            }
        } catch (e: NumberFormatException) {
            // エラー処理
        }
    }

    private suspend fun communicateToNextIntent(
        params: MutableMap<String, String>, isEditing: Boolean, diaryId: Int = 1
    ) {
        val intent = Intent(this, WeatherMoodActivity::class.java)
        val weatherMood: WeatherDto? = requestApi(params) // 天気DTOを取得

        params["title"] = binding.textViewTitle.text.toString()
        val bundle = Bundle().apply {
            putSerializable("map", params as Serializable)
        } // パラメータをバンドル形式に変換し、キーを "map" としてバンドルに格納
        Log.d("weatherMoodは", weatherMood.toString())
        with(intent) {
            putExtra("weatherMoodDto", weatherMood)
            putExtra("diary", bundle)
            if (date == null) {
                date = LocalDate.now()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE) // ユーザーが初めて記事を作成したが、日付を選択しなかった場合
                putExtra("date", date)
            } else
                putExtra("date", date)
        }
        if (isEditing) {
            Log.d("前のアクティビティから受け取った日付は", date.toString())
            Log.d("パラメータは", params.toString())
            Log.d("バンドルは", bundle.toString())
            Log.d("最終的な DiaryID は", diaryId.toString())

            with(intent) {
                putExtra("diaryId", diaryId)
                putExtra("isModify", true)
            }
        } else {
            Log.d("パラメータは", params.toString())
            Log.d("バンドルは", bundle.toString())
            Log.d("nextBtn", "$params")
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
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
     * ウィジェットを回転させるための関数。カレンダービューで使用。
     * @param wiget ウィジェットの選択
     * @param state 状態
     * @param animation アニメーション
     * @return {Boolean} ブール値
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
//            binding.calendarButton.isEnabled = false // ボタンを無効化
            // ローディングが完了するまで色が異なる
            val calendarList = getCalendarList()
            Log.d("カレンダーリスト", calendarList.toString())
//            if (calendarList != null) {
//                binding.calendarButton.isEnabled = true // ボタンを再度有効化
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
