package com.example.picasso

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picasso.api.ApiService
import com.example.picasso.databinding.ActivityGalleryBinding
import com.example.picasso.publicClass.detailDiaryFun.showThanks
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*

class gallery : AppCompatActivity() {
    private val binding by lazy {
        ActivityGalleryBinding.inflate(layoutInflater)
    }

    // 月の名前の配列
    val nameOfMonth = arrayOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    private val adapter = ChatAdapter3()

    // APIサービスのインスタンス
    val api = ApiService

    // DatePickerDialogのリスナー
    var dateSetListener: OnDateSetListener =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            var isSourceNull = false

            // 選択された月を2桁に変換する
            var month = monthOfYear.toString()
            if (month.length == 1) {
                month = "0$month"
            }

            // 年と月をログに出力する
            Log.d("年", binding.year.text.toString())
            Log.d("月", month)
            var diaryData: MutableList<Diary>? = null
            // 初回ローディング

            lifecycleScope.launch {
                diaryData = api.communicateJwt(this@gallery).getAllDiary(
                    "${binding.year.text}", month
                ).body()!!
                adapter.setData(diaryData!!, this@gallery)
            }

            val timer = Timer()
            var isFirstRun = true
            val task = object : TimerTask() {
                override fun run() {
                    lifecycleScope.launch {
                        val nullSourceId = diaryData?.find { it.source == null }?.diary_id
                        if (nullSourceId == null && isFirstRun) {
                            cancel()
                            timer.cancel()
                        }
                        if (nullSourceId == null && !isFirstRun) {
                            adapter.setData(diaryData!!, this@gallery)
                            cancel()
                            timer.cancel()
                        } else {
                            diaryData = api.communicateJwt(this@gallery).getAllDiary(
                                "${binding.year.text}", month
                            ).body()!!
                            if (diaryData!!.any { it.diary_id == nullSourceId && it.source != null }) {
                                adapter.setData(diaryData!!, this@gallery)
                                cancel()
                                timer.cancel()
                            }
                        }
                        isFirstRun = false
                    }
                }
            }
            timer.scheduleAtFixedRate(task, 1500L, 3000L)
        }

    private val sharedPreference by lazy {
        getSharedPreferences("image", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val tb: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(tb)
        val galleryRecyclerView = binding.pictureLayout.recycleView
        var currentDate = LocalDate.now()
        val monthTextView = binding.month
        val yearTextView = binding.year
        monthTextView.text = nameOfMonth[currentDate.monthValue - 1] + " "
        yearTextView.text = currentDate.year.toString()

        // ギャラリー画面のボタンリスナーを設定する
        binding.userInfo.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }

        binding.stats.setOnClickListener {
            var intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonWrite.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)
            startActivity(intent)
        }

        var currentSpan = sharedPreference.getInt("NumOfSpan", 2)
        setLayoutManager(galleryRecyclerView, currentSpan)

        // 初回ローディング
        lifecycleScope.launch {
            var isSourceNull = false

            var month = currentDate.monthValue.toString()
            if (month.length == 1) {
                month = "0" + month
            }
            Log.d("年", binding.year.text.toString())
            Log.d("月", month)
            var diaryData: MutableList<Diary>? = null

            lifecycleScope.launch {
                diaryData = api.communicateJwt(this@gallery).getAllDiary(
                    "${binding.year.text}", month
                ).body()!!
                adapter.setData(diaryData!!, this@gallery)
            }

            val timer = Timer()

            val task = object : TimerTask() {
                override fun run() {
                    lifecycleScope.launch {
                        Log.e("runnings2", "runnings2")
                        for (diary in diaryData!!)
                            if (diary.source == null) {
                                Log.e("runnings3", "runnings3")
                                Log.e("フラグをfalseに", "フラグをfalseに")
                                isSourceNull = true
                                break
                            } else {
                                Log.e("フラグをtrueに", "フラグをtrueに")
                                isSourceNull = false
                            }
                        if (!isSourceNull) {
                            Log.e("実行", "実行")
                            adapter.setData(diaryData!!, this@gallery)
                            cancel()
                            timer.cancel()
                        }
                        Log.e("取得中", "取得中")
                        diaryData = api.communicateJwt(this@gallery).getAllDiary(
                            "${binding.year.text}", month
                        ).body()!!
                        Log.e("diaryData", diaryData.toString())
                    }
                    Log.e("running", "running")
                }
            }
            timer.scheduleAtFixedRate(task, 1500L, 3000L)
        }

        galleryRecyclerView.adapter = adapter

        //---------------------------------------
        // DatePickerを表示する関数
        monthTextView.setOnClickListener {
            showDatePicker(dateSetListener)
        }
        yearTextView.setOnClickListener {
            showDatePicker(dateSetListener)
        }
        //------------------------------------------

        val imagebutton = binding.imageButtonCancel
        imagebutton.setOnClickListener {
            if (currentSpan >= 3) {
                currentSpan = 1
                setNumOfSpan(currentSpan)
                setLayoutManager(galleryRecyclerView, currentSpan)
            } else {
                currentSpan += 1
                setNumOfSpan(currentSpan)
                setLayoutManager(galleryRecyclerView, currentSpan)
            }
        }

        // -------------------評価を入力した場合--------------------
        if (intent.getBooleanExtra("isRating", false)) {
            showThanks(this)
        }
    }

    // DatePickerを表示する関数
    private fun showDatePicker(listenerFun: OnDateSetListener) {
        val pd = YearMonthPickerDialog()
        pd.setListener(listenerFun)
        pd.show(supportFragmentManager, "YearMonthPickerTest")
    }

    // 1行の数を設定する関数
    private fun setNumOfSpan(NumOfSpan: Int) {
        with(sharedPreference.edit()) {
            putInt("NumOfSpan", NumOfSpan)
            apply()
        }
    }

    // RecyclerViewのレイアウトマネージャーを設定する関数
    private fun setLayoutManager(recyclerView: RecyclerView, NumOfSpan: Int) {
        recyclerView.layoutManager = GridLayoutManager(this, NumOfSpan)
        val spanCount = NumOfSpan
        val spacing = 30 // px
        val includeEdge = false
        recyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount, spacing, includeEdge
            )
        )
    }

    // オプションメニューを作成する関数
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate: MenuInflater = menuInflater
        inflate.inflate(R.menu.hamburger, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        if (menu != null) {
            onOptionsItemSelected(menu.findItem(R.id.logout))
        }
        return true
    }

    // オプションメニューのアイテムが選択された時の処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                // ログアウトボタンがクリックされた場合の処理
                val sharedPreferences =
                    getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("JWT")
                editor.apply()
                // ログイン画面または適切な画面に遷移する
                return true
            }
            // その他のメニューアイテムの処理
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // GridSpacingItemDecorationクラス
    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // アイテムの位置
            val column = position % spanCount // アイテムの列

            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // 上部エッジ
                    outRect.top = spacing
                }
                outRect.bottom = spacing // アイテムの下部
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f / spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // アイテムの上部
                }
            }
        }
    }
}