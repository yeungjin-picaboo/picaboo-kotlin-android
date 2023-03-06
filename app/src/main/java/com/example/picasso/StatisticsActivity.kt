package com.example.picasso

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.picasso.databinding.ActivityStatisticsBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import org.json.JSONObject


class StatisticsActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityStatisticsBinding.inflate(layoutInflater)
    }

    private fun touchButton(touchedButton: Button,touchedButtonBackground: GradientDrawable, button: GradientDrawable){
        touchedButtonBackground.setColor(Color.parseColor("#686D76"))
        button.setColor(Color.parseColor("#EEEEEE"))
        touchedButton.bringToFront()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tb: androidx.appcompat.widget.Toolbar = binding.toolbar
        setSupportActionBar(tb)


        binding.weather.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.mood.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        binding.stats.setOnClickListener{
            var intent = Intent(this, gallery::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener{
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

        btnmonth.setOnTouchListener{
            v, e ->
            if(e.action == MotionEvent.ACTION_DOWN){
                if(!currentButton){
                    touchButton(btnmonth, monBackground, annualBackground)
                    btnmonth.setTextColor(Color.parseColor("#ffffff"))
                    btnannual.setTextColor(Color.parseColor("#5f5f5e"))
                    currentButton = !currentButton
                }
                //데이터 불러오는 함수 그대로 사용하면 될 듯
                true
            }else{
                false
            }
        }

        btnannual.setOnTouchListener{
                v, e ->
            if(e.action == MotionEvent.ACTION_DOWN){
                if(currentButton){
                    touchButton(btnannual, annualBackground, monBackground)
                    btnmonth.setTextColor(Color.parseColor("#5f5f5e"))
                    btnannual.setTextColor(Color.parseColor("#ffffff"))
                    currentButton = !currentButton
                }
                //데이터 불러오는 함수 그대로 사용하면 될 듯
                true
            }else{
                false
            }
        }



        val pieChart1: PieChart = binding.test1.piechart
        val pieChart2: PieChart = binding.test2.piechart

        //차트 디자인 설정
        pieChart1.apply {
            data = makePieData("Mood")
            data.setDrawValues(false)
            setDrawSliceText(false)
            setHoleColor(Color.parseColor("#EEEEEE"))

            description.isEnabled = false
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.textSize = 17f
        }

        pieChart2.apply {
            data = makePieData("Weather")
            data.setDrawValues(false)
            setDrawSliceText(false)
            setHoleColor(Color.parseColor("#EEEEEE"))

            description.isEnabled = false
            //legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.textSize = 17f
        }

    }


    data class mooddata(val happy: Float,val good: Float,val neutral: Float,val bad: Float,val confused: Float,val angry: Float, val nervous: Float, val sad: Float,val sick: Float,)
    fun getDataMood(): mooddata {
        //http통신
        return mooddata(12.9f, 9.68f, 45.16f, 16.13f, 0f,6.45f,0f,9.68f, 0f)
    }
    fun getListMood(): Array<String>{
        return arrayOf("happy", "good", "neutral", "bad", "confused", "angry", "nervous", "sad", "sick")
    }


    data class weatherdata(val sunny: Float, val rainy: Float, val snowy: Float, val cloudy: Float, val windy: Float)
    fun getDataWeather(): weatherdata {
        //http통신
        return weatherdata(48.39F, 12.90F, 0F, 35.48F, 3.23F)
    }
    fun getListWeater(): Array<String>{
        return arrayOf("sunny", "rainy", "snowy", "cloudy", "windy")
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
        }

        for(item in list){
            Piedata.add(PieEntry(jsonData.getString(item).toFloat(), item))
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

        return true
    }
}