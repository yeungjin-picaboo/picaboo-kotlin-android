package com.example.picasso

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picasso.databinding.ActivityGalleryBinding


class gallery : AppCompatActivity() {
    private val binding by lazy{
        ActivityGalleryBinding.inflate(layoutInflater)
    }

    val nameOfMonth = arrayOf("January", "February", "March", "April", "May", "June","July", "August", "September", "October", "November", "December")

    //
    var d: OnDateSetListener = object : OnDateSetListener {
        override fun onDateSet(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

            findViewById<TextView>(R.id.month).text = nameOfMonth[monthOfYear - 1]
            findViewById<TextView>(R.id.year).text = year.toString()

            Log.d("test", "year = " + year + ", month = " + monthOfYear + ", day = " + dayOfMonth)
        }
    }
    //


    private val sharedPreference by lazy{
        getSharedPreferences("image", Context.MODE_PRIVATE)
    }
    private val adapter = ChatAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //---------------------------------------
        // datePicker 꺼내는 함수
        binding.month.setOnClickListener{
            val pd = YearMonthPickerDialog()
            pd.setListener(d)
            pd.show(supportFragmentManager, "YearMonthPickerTest")
        }
        binding.year.setOnClickListener{
            val pd = YearMonthPickerDialog()
            pd.setListener(d)
            pd.show(supportFragmentManager, "YearMonthPickerTest")
        }
        //------------------------------------------
        var currentSpan = sharedPreference.getInt("NumOfSpan", 2)

        setLayoutManager(currentSpan)
        binding.pictureLayout.recycleView.adapter = adapter
        adapter.setData(DataGenerator.get())

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
}