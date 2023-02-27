package com.example.picasso

import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.*


class YearMonthPickerDialog : DialogFragment() {
    private var listener: OnDateSetListener? = null
    var cal: Calendar = Calendar.getInstance()
    fun setListener(listener: OnDateSetListener?) {
        this.listener = listener
    }

    var btnConfirm: Button? = null
    var btnCancel: Button? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = requireActivity().layoutInflater
        //val dialog = inflater.inflate(binding, null)
        val dialog = inflater.inflate(R.layout.datepicker , null)
        btnConfirm = dialog.findViewById(R.id.btn_confirm)
        btnCancel = dialog.findViewById(R.id.btn_cancel)
        val monthPicker = dialog.findViewById<NumberPicker>(R.id.picker_month)
        val yearPicker = dialog.findViewById<NumberPicker>(R.id.picker_year)

        btnCancel?.setOnClickListener{
            this.dialog?.cancel()
        }
        btnConfirm?.setOnClickListener{
            listener!!.onDateSet(null, yearPicker.value, monthPicker.value.toInt(), 0)
            this.dialog?.cancel()
        }
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal.get(Calendar.MONTH) + 1
        val year: Int = cal.get(Calendar.YEAR)
        yearPicker.minValue = MIN_YEAR
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year
        builder.setView(dialog)
        
        return builder.create()
    }

    companion object {
        private const val MAX_YEAR = 2099
        private const val MIN_YEAR = 1980
    }
}

private fun AlertDialog.Builder.setView(dialog: Dialog?) {

}
