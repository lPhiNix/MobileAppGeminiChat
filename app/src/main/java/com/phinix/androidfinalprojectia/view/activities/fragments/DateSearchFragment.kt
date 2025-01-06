package com.phinix.androidfinalprojectia.view.activities.fragments

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.phinix.androidfinalprojectia.R
import com.phinix.androidfinalprojectia.view.activities.MainActivity

class DateSearchFragment : Fragment() {
    private lateinit var dateInput : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_date_search, container, false)

        dateInput = view.findViewById(R.id.dateInput)

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        Log.d("DateSearchFragment", "Calendar initialized: $year/$month/$day")

        dateInput.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                R.style.CustomDatePickerStyle,  // Aplicamos el estilo personalizado aquí
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                    dateInput.setText(formattedDate)

                    val mainActivity = activity as? MainActivity
                    mainActivity?.filterMessagesByDate(formattedDate) // Filtrar mensajes por la fecha seleccionada
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        dateInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val mainActivity = activity as? MainActivity
                mainActivity?.filterMessagesByDate("") // Se pasa un texto vacío
            }
        }

        dateInput.addTextChangedListener {
            val text = it.toString()
            val mainActivity = activity as? MainActivity
            mainActivity?.filterMessagesByDate(text)
        }

        return view
    }

    fun getDateInput(): EditText {
        return dateInput
    }
}