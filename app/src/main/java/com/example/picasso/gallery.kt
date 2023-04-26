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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picasso.api.WeatherService
import com.example.picasso.databinding.ActivityGalleryBinding
import kotlinx.coroutines.launch
import java.time.LocalDate


class gallery : AppCompatActivity() {
    private val binding by lazy {
        ActivityGalleryBinding.inflate(layoutInflater)
    }

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

    //    val nameOfMonth = arrayOf(
//        "Jan. ",
//        "Feb. ",
//        "Mar. ",
//        "Apr. ",
//        "May. ",
//        "Jun. ",
//        "Jul. ",
//        "Aug. ",
//        "Sep. ",
//        "Oct. ",
//        "Nov. ",
//        "Dec. "
//    )
    private val adapter = ChatAdapter3()

    val api = WeatherService

    var dateSetListener: OnDateSetListener =
        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            findViewById<TextView>(R.id.month).text = nameOfMonth[monthOfYear - 1] + " "
            findViewById<TextView>(R.id.year).text = year.toString()
            //통신하는 로직 추가한다
            adapter.setData(
                DataGenerator3.get("${binding.month.text} + ${binding.year.text}"),
                this@gallery
            )
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
//        binding.bottomMenu.


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


        lifecycleScope.launch {
            var month = currentDate.monthValue.toString()
            if (month.length == 1) {
                month = "0" + month
            }
            Log.d("연도", binding.year.text.toString())
            Log.d("월", month)

//            Log.e(
//                "에러내용",
//                api.communicateJwt(this@gallery).getAllDiary("${binding.year.text}", "$month")
//                    .body()!!.toString()
//            )
            adapter.setData(
                api.communicateJwt(this@gallery).getAllDiary("${binding.year.text}", "${month}")
                    .body()!!, this@gallery
            )

        }


        galleryRecyclerView.adapter = adapter

        //---------------------------------------
        // datePicker 꺼내는 함수
        monthTextView.setOnClickListener {
            showDatePicker(dateSetListener)
        }
        yearTextView.setOnClickListener {
            showDatePicker(dateSetListener)
        }
        //------------------------------------------

        val imagebutton = binding.imageButtonCancel
        imagebutton.setOnClickListener{
            if(currentSpan >= 3){
                currentSpan = 1
                setNumOfSpan(currentSpan)
                setLayoutManager(galleryRecyclerView, currentSpan)
            }else{
                currentSpan += 1
                setNumOfSpan(currentSpan)
                setLayoutManager(galleryRecyclerView, currentSpan)
            }
        }

    }

    private fun showDatePicker(listenerFun:OnDateSetListener){
        val pd = YearMonthPickerDialog()
        pd.setListener(listenerFun)
        pd.show(supportFragmentManager, "YearMonthPickerTest")
    }

    private fun setNumOfSpan(NumOfSpan: Int){
        with(sharedPreference.edit()){
            putInt("NumOfSpan", NumOfSpan)
            apply()
        }
    }

    private fun setLayoutManager(recyclerView: RecyclerView, NumOfSpan: Int) {
        recyclerView.layoutManager = GridLayoutManager(this, NumOfSpan)
        val spanCount = NumOfSpan
        val spacing = 30 //px
        val includeEdge = false
        recyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
    }


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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                // Handle logout button click
                val sharedPreferences =
                    getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.remove("JWT")
                editor.apply()
                // Navigate to the login screen or any other appropriate screen
                return true
            }
            // Handle other menu items as needed
            else -> return super.onOptionsItemSelected(item)
        }
    }

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
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left =
                    spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right =
                    (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right =
                    spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f / spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }
}