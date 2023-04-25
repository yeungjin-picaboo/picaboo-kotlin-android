package com.example.picasso

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.view.menu.MenuBuilder
import com.example.picasso.databinding.ActivityStatisticsBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import org.json.JSONObject
import java.util.Timer


class StatisticsActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityStatisticsBinding.inflate(layoutInflater)
    }

    var dateSetListener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->  Log.d("test","${year} + ${monthOfYear}")}

    private fun touchButton(touchedButton: Button,touchedButtonBackground: GradientDrawable, button: Button, buttonBackground: GradientDrawable){
        touchedButtonBackground.setColor(Color.parseColor("#686D76"))
        buttonBackground.setColor(Color.parseColor("#EEEEEE"))
        touchedButton.setTextColor(Color.parseColor("#ffffff"))
        button.setTextColor(Color.parseColor("#5f5f5e"))
        touchedButton.bringToFront()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tb: androidx.appcompat.widget.Toolbar = binding.toolbars.toolbarMenu
        setSupportActionBar(tb)


        binding.weather.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.mood.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.toolbars.stats.setOnClickListener {
            var intent = Intent(this, gallery::class.java)
            startActivity(intent)
        }

        binding.toolbars.home.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        val monBackground = binding.monthBtn.background as GradientDrawable
        val annualBackground = binding.annualBtn.background as GradientDrawable

        val btnmonth = binding.monthBtn
        val btnannual = binding.annualBtn
        btnmonth.bringToFront()
        monBackground.setColor(Color.parseColor("#686D76"))
        annualBackground.setColor(Color.parseColor("#EEEEEE"))
        btnmonth.setTextColor(Color.parseColor("#ffffff"))
        btnannual.setTextColor(Color.parseColor("#5f5f5e"))

        var currentButton = true



        val pieChart1: PieChart = binding.test1.piechart
        val pieChart2: PieChart = binding.test2.piechart


//
//        btnmonth.setOnTouchListener{
//            v, e ->
//            if(e.action == MotionEvent.ACTION_DOWN){
//                if(!currentButton){
//                    touchButton(btnmonth, monBackground, btnannual, annualBackground)
//                    currentButton = !currentButton
//                    //데이터 불러오는 함수 그대로 사용하면 될 듯
//                    applyDataToChart(pieChart2, "Weather")
//                    applyDataToChart(pieChart1, "Mood")
//                }
//                false
//            }else{
//                false
//            }
//        }

//        btnannual.setOnTouchListener{
//                v, e ->
//            if(e.action == MotionEvent.ACTION_DOWN){
//                if(currentButton){
//                    touchButton(btnannual, annualBackground, btnmonth, monBackground)
//                    currentButton = !currentButton
//                    //데이터 불러오는 함수 그대로 사용하면 될 듯
//                    applyDataToChart(pieChart2, "Weather2")
//                    applyDataToChart(pieChart1, "Mood2")
//                }
//                false
//            }else{
//                false
//            }
//        }

        btnmonth.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    Log.d("test", "test")
                    startTimer()
                    true
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL,
                -> {
                    stopTimer()
                    if(sec >= 0.5){
                        Log.d("test", "LongTouch")
                        val pd = YearMonthPickerDialog()
                        pd.setListener(dateSetListener)
                        pd.show(supportFragmentManager, "YearMonthPickerTest")
                    }else{
                        Log.d("test", "shortTouch")
                        if(!currentButton){
                            touchButton(btnmonth, monBackground, btnannual, annualBackground)
                            currentButton = !currentButton
                            //데이터 불러오는 함수 그대로 사용하면 될 듯
                            applyDataToChart(pieChart2, "Weather")
                            applyDataToChart(pieChart1, "Mood")
                        }
                    }
                    true
                }
                else -> true
            }
        }

        btnannual.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startTimer()
                    true
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_CANCEL,
                -> {
                    stopTimer()
                    if(sec >= 0.5){
                        Log.d("test", "LongTouch")
                        val pd = YearMonthPickerDialog()
                        pd.setListener(dateSetListener)
                        pd.show(supportFragmentManager, "YearMonthPickerTest")
                    }else{
                        Log.d("test", "shortTouch")
                        if(currentButton){
                            touchButton(btnannual, annualBackground, btnmonth, monBackground)
                            currentButton = !currentButton
                            //데이터 불러오는 함수 그대로 사용하면 될 듯
                            applyDataToChart(pieChart2, "Weather2")
                            applyDataToChart(pieChart1, "Mood2")
                        }
                    }
                    true
                }
                else -> true
            }
        }
        binding.selectdate.setOnClickListener{
            val pd = YearMonthPickerDialog()
            pd.setListener(dateSetListener)
            pd.show(supportFragmentManager, "YearMonthPickerTest")
        }

        applyDataToChart(pieChart1, "Mood")
        applyDataToChart(pieChart2, "Weather")
    }

    private fun applyDataToChart(chart: PieChart, state: String){
        chart.apply {
            data = makePieData(state)
            data.setDrawValues(false)
            setDrawSliceText(false)
            setHoleColor(Color.parseColor("#EEEEEE"))

            description.isEnabled = false
            legend.orientation = Legend.LegendOrientation.VERTICAL
            //legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.textSize = 17f
        }

        chart.notifyDataSetChanged()
        chart.invalidate()
    }



    data class mooddata(val happy: Float,val good: Float,val neutral: Float,val bad: Float,val confused: Float,val angry: Float, val nervous: Float, val sad: Float,val sick: Float,)
    fun getDataMood(): mooddata {
        //http통신
        return mooddata(12.9f, 9.68f, 45.16f, 16.13f, 0f,6.45f,0f,9.68f, 0f)
    }
    fun getListMood(): Array<String>{
        return arrayOf("happy", "good", "neutral", "bad", "confused", "angry", "nervous", "sad", "sick")
    }
    fun getDataMood2(): mooddata{
        return mooddata( 0f, 45.16f, 9.68f, 16.13f, 12.9f,6.45f,0f,9.68f, 0f)
    }

    data class weatherdata(val sunny: Float, val rainy: Float, val snowy: Float, val cloudy: Float, val windy: Float)
    fun getDataWeather(): weatherdata {
        //http통신
        return weatherdata(48.39F, 12.90F, 0F, 35.48F, 3.23F)
    }
    fun getDataWeather2(): weatherdata {
        //http통신48.39F
        return weatherdata(12.90F, 48.39F, 35.48F, 0F, 3.23F)
    }
    fun getListWeater(): Array<String>{
        return arrayOf("sunny", "rainy", "snowy   ", "cloudy", "windy")
    }

    fun makeDataJson(data: Any): JSONObject{
        val jsonString = Gson().toJson(data)
        return JSONObject(jsonString)
    }

    private fun makePieData(type: String):PieData{
        val Piedata = mutableListOf<PieEntry>()

        var list: Array<String> = arrayOf("")
        var jsonData: JSONObject =  JSONObject()

        // 데이터 받아옴   감정이랑  날씨
        if(type == "Mood"){
            list = getListMood()
            jsonData = makeDataJson(getDataMood())
        }else if(type == "Weather"){
            list = getListWeater()
            jsonData = makeDataJson(getDataWeather())
        }else if(type == "Weather2"){
            list = getListWeater()
            jsonData = makeDataJson(getDataWeather2())
        }else if(type == "Mood2"){
            list = getListMood()
            jsonData = makeDataJson(getDataMood2())
        }

        for(item in list){
            Piedata.add(PieEntry(jsonData.getString(item.trim()).toFloat(), item))
        }

        val pieDataset = PieDataSet(Piedata, "")
        // add colors
        val colorsItems = ArrayList<Int>()
        colorsItems.add(Color.parseColor("#B33939"))
        colorsItems.add(Color.parseColor("#CD6133"))
        colorsItems.add(Color.parseColor("#CC8E35"))
        colorsItems.add(Color.parseColor("#CCAE62"))
        colorsItems.add(Color.parseColor("#218C74"))
        colorsItems.add(Color.parseColor("#227093"))
        colorsItems.add(Color.parseColor("#686D76"))
        colorsItems.add(Color.parseColor("#474787"))
        colorsItems.add(Color.parseColor("#2C2C54"))

        colorsItems.add(ColorTemplate.getHoloBlue())

        pieDataset.apply {
            colors = colorsItems
        }
        val pieData: PieData = PieData(pieDataset)
        return pieData
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate: MenuInflater = menuInflater
        inflate.inflate(R.menu.hamburger, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        // 여기서 로그인 판단하고 아이디 가져와라
        // 메뉴 아이템 title 변경
        menu?.findItem(R.id.userId)?.title = "teest"

        return true
    }

    var time = .0
    var timer: Timer? = null
    var sec = 0.0

    private fun startTimer() {
        time = 0.0
        timer = kotlin.concurrent.timer(period = 5) {
            time++
            sec = time / 100
        }
    }

    private fun stopTimer(){
        timer?.cancel()
    }

}