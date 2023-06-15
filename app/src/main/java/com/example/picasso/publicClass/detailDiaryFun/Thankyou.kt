package com.example.picasso.publicClass.detailDiaryFun

import android.app.Activity
import androidx.core.content.res.ResourcesCompat
import com.example.picasso.R
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

fun showThanks(activity: Activity) {
    MotionToast.createToast(
        activity,
        "평가해주셔서 감사합니다 😍",
        "추후 내정보 페이지에 업로드됩니다!",
        MotionToastStyle.SUCCESS,
        MotionToast.GRAVITY_BOTTOM,
        MotionToast.LONG_DURATION,
        ResourcesCompat.getFont(activity, R.font.ubuntu)
    )
}