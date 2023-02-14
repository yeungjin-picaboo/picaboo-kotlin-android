package com.example.picasso

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.picasso.databinding.ActivityPiechartBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS


class PiechartActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityPiechartBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pieChart: PieChart = binding.piechart

        val visitors = mutableListOf<PieEntry>()
        visitors.add(PieEntry(12.9f, "happy"))
        visitors.add(PieEntry(9.68f, "good"))
        visitors.add(PieEntry(45.16f, "neutral"))
        visitors.add(PieEntry(16.13f, "bad"))
        visitors.add(PieEntry(0f, "confused"))
        visitors.add(PieEntry(6.45f, "angry"))
        visitors.add(PieEntry(0f, "nervous"))
        visitors.add(PieEntry(9.68f, "sad"))
        visitors.add(PieEntry(0f, "sick"))

        val pieDataset = PieDataSet(visitors, "")
        // add a lot of colors
        val colorsItems = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
        for (c in COLORFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())

        pieDataset.apply {
            colors = colorsItems
        }

        val pieData: PieData = PieData(pieDataset)
        pieChart.apply {
            data = pieData
            data.setDrawValues(false)
            setDrawSliceText(false)
            setHoleColor(Color.LTGRAY)
            //색설명 없앰?
            //이거 시발 내가 만들어야되는거같은데 아닌가
            description.isEnabled = false
            //legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.textSize = 17f
        }


    }
}
