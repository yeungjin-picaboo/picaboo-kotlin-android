package com.example.picasso

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import com.example.picasso.dto.diary.DiariesListDto
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class DisabledDaysDecorator(private val disabledDates: List<CalendarDay>) :
    DayViewDecorator {
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day != null && disabledDates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#EEEEEE")))
    }
}

fun test(callback: List<DiariesListDto>?, view: MaterialCalendarView) {
        if (callback != null) {
            Log.d("OK Btn", "positive")
            callback.forEach { dateString ->
                Log.e("dateString", dateString.date)
                val dateComponents = dateString.date.split("-")
                val (year, month, day) = dateComponents.map { it.toInt() }
                val dates = listOf(CalendarDay.from(year, month, day))
                view.addDecorator(DisabledDaysDecorator(dates))
            }
        } else {
            Log.d("알수없는 에러", "코드 확인 요망")
        }
}

fun returnTest(callback: List<DiariesListDto>?, view: MaterialCalendarView): List<DiariesListDto>? {
    return if (callback != null) {
        Log.d("OK Btn", "positive")
        callback.forEach { dateString ->

        val dateComponents = dateString.date.split("-")
            val (year, month, day) = dateComponents.map { it.toInt() }
            val dates = listOf(CalendarDay.from(year, month, day))
            view.addDecorator(DisabledDaysDecorator(dates))
        }
        callback
    } else {
        Log.d("알수없는 에러", "코드 확인 요망")
        null
    }
}