package com.example.picasso

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picasso.databinding.ActivityGalleryBinding
import java.time.LocalDate
import kotlin.reflect.typeOf


class gallery : AppCompatActivity() {
    private val binding by lazy{
        ActivityGalleryBinding.inflate(layoutInflater)
    }

    //val nameOfMonth = arrayOf("January", "February", "March", "April", "May", "June","July", "August", "September", "October", "November", "December")
    val nameOfMonth = arrayOf("Jan. ", "Feb. ", "Mar. ", "Apr. ", "May. ", "Jun. ", "Jul. ", "Aug. ", "Sep. ", "Oct. ", "Nov. ", "Dec. ")
    private val adapter = ChatAdapter()


    //
    var dateSetListener: OnDateSetListener = object : OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
            findViewById<TextView>(R.id.month).text = nameOfMonth[monthOfYear - 1] + " "
            findViewById<TextView>(R.id.year).text = year.toString()

            //통신하는 로직 추가한다
            adapter.setData(DataGenerator.get("${binding.month.text} + ${binding.year.text}"), this@gallery)
        }
    }
    //


    private val sharedPreference by lazy{
        getSharedPreferences("image", Context.MODE_PRIVATE)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tb: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(tb)

        var currentDate = LocalDate.now()
        findViewById<TextView>(R.id.month).text = nameOfMonth[currentDate.monthValue - 1] + " "
        findViewById<TextView>(R.id.year).text = currentDate.year.toString()

        binding.stats.setOnClickListener{
            var intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener{
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        //---------------------------------------
        // datePicker 꺼내는 함수
        binding.month.setOnClickListener{
            val pd = YearMonthPickerDialog()
            pd.setListener(dateSetListener)
            pd.show(supportFragmentManager, "YearMonthPickerTest")
        }
        binding.year.setOnClickListener{
            val pd = YearMonthPickerDialog()
            pd.setListener(dateSetListener)
            pd.show(supportFragmentManager, "YearMonthPickerTest")
        }

        //------------------------------------------
        var currentSpan = sharedPreference.getInt("NumOfSpan", 2)

        setLayoutManager(currentSpan)
        binding.pictureLayout.recycleView.adapter = adapter
        adapter.setData(DataGenerator.get("${binding.month.text} + ${binding.year.text}"), this)

        val imagebutton = binding.imageButton
        imagebutton.setOnClickListener{
            if(currentSpan >= 3){
                currentSpan = 1
                setNumOfSpan(currentSpan)
                setLayoutManager(currentSpan)
            }else{
                currentSpan += 1
                setNumOfSpan(currentSpan)
                setLayoutManager(currentSpan)
            }
        }

    }

    private fun setNumOfSpan(NumOfSpan: Int){
        with(sharedPreference.edit()){
            putInt("NumOfSpan", NumOfSpan)
            apply()
        }
    }
    private fun setLayoutManager(NumOfSpan: Int){
        binding.pictureLayout.recycleView.layoutManager = GridLayoutManager(this, NumOfSpan)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate: MenuInflater = menuInflater
        inflate.inflate(R.menu.hamburger, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        return true
    }
}