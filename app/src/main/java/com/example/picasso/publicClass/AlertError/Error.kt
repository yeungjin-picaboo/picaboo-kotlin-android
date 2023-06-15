package com.example.picasso.publicClass.AlertError

import android.app.AlertDialog
import android.content.Context
import com.developer.kalert.KAlertDialog

fun errorExpress(context: Context) {
    AlertDialog.Builder(context)
    val builder = KAlertDialog(context, KAlertDialog.ERROR_TYPE)
    builder.setTitleText("에러가 발생했습니다").setContentText("서버와 통신중 에러가 발생하였습니다 죄송합니다.")
        .setConfirmClickListener(
            "예"
        ) {
            it.cancel()
        }
        .show()
}