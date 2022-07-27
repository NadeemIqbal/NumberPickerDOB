package com.nadeem.demo_numberpickerdob

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.nadeem.demo_numberpickerdob.databinding.ActivityMainBinding
import com.shawnlin.numberpicker.NumberPicker
import java.time.LocalDate

class MainActivity : AppCompatActivity(), NumberPicker.OnValueChangeListener {

    private lateinit var binding: ActivityMainBinding

    lateinit var numberPickerDay: NumberPicker
    lateinit var numberPickerMonth: NumberPicker
    lateinit var numberPickerYear: NumberPicker

    private val months: Array<String> = arrayOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    )
    private var years: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.apply {
            numberPickerDay = this.pickerDay1
            numberPickerMonth = this.pickerMonth1
            numberPickerYear = this.pickerYear1

            numberPickerYear.apply {
                this.setSelectedTypeface(Typeface.create(Typeface.SANS_SERIF, 1000, false))
                years = getYears()
                this.maxValue = years!!.size
                this.displayedValues = years!!
                this.setOnValueChangedListener(this@MainActivity)
            }

            numberPickerMonth.apply {
                this.setSelectedTypeface(Typeface.create(Typeface.SANS_SERIF, 1000, false))
                setAllMonthValues()
                this.setOnValueChangedListener(this@MainActivity)
            }

            numberPickerDay.apply {
                this.setSelectedTypeface(Typeface.create(Typeface.SANS_SERIF, 1000, false))
                setDayValues(1, 1, 1)
                this.setOnValueChangedListener(this@MainActivity)
            }
        }

        setDayAndMonthValues()

        binding.fab.setOnClickListener { view ->
            val day = numberPickerDay.value
            val month = months[numberPickerMonth.value - 1]
            val year = years!![numberPickerYear.value - 1]

            Snackbar.make(
                view,
                "Valid Age? = ${isValidAge()}, Selected Date: $day/$month/$year",
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null).show()
        }
    }

    private fun setAllMonthValues() {
        numberPickerMonth.maxValue = months.size
        numberPickerMonth.displayedValues = months
    }

    private fun setDayValues(day: Int, month: Int, year: Int) {
        val days = getDays(day, month, year)
        numberPickerDay.maxValue = days!!.size
        numberPickerDay.displayedValues = days
    }

    private fun getYears(): Array<String> {
        val arrayList: ArrayList<String> = arrayListOf()

        val currentMinus18 = LocalDate.now().year - 18
        val currentMinus118 = LocalDate.now().year - 118

        for (i in currentMinus18 downTo currentMinus118) {
            arrayList.add(i.toString())
        }

        return arrayList.toTypedArray()
    }


    private fun getDays(day: Int, month: Int, year: Int): Array<String>? {
        val arrayList: ArrayList<String> = arrayListOf()
        val lengthOfMonth =
            LocalDate.of(years!![year - 1].toInt(), month, 1).lengthOfMonth()
        for (i in 1..lengthOfMonth) {
            arrayList.add(String.format("%02d", i))
        }
        return arrayList.toTypedArray()
    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {

        when (picker!!.id) {
            numberPickerMonth.id -> {
                // month
                Log.e(
                    "TAG",
                    "Picker = Month::: oldVal:${months[oldVal - 1]}, newVal:${months[newVal - 1]}"
                )

                if (oldVal != newVal) {
                    if (numberPickerYear.displayedValues[numberPickerYear.value - 1] == years!![0]) {
                        setDayAndMonthValues()
                    } else {
                        setDayValues(numberPickerDay.value, newVal, numberPickerYear.value)
                        setAllMonthValues()
                    }
                }
            }

            numberPickerYear.id -> {
                // year
                Log.e(
                    "TAG",
                    "Picker = Year::: oldVal:${years!![oldVal - 1]}, newVal:${years!![newVal - 1]}"
                )
                if (oldVal != newVal) {
                    if (numberPickerYear.displayedValues[numberPickerYear.value - 1] == years!![0]) {
                        setDayAndMonthValues()
                    } else {
                        setDayValues(numberPickerDay.value, numberPickerMonth.value, newVal)
                        setAllMonthValues()
                    }
                }
            }

            numberPickerDay.id -> {
                // day
                Log.e("TAG", "Picker = Day::: oldVal:${oldVal}, newVal:${newVal}")
                if (oldVal != newVal) {
//                    setDayValues(numberPickerDay.value, binding.pickerMonth1.value, newVal)
                    if (numberPickerYear.displayedValues[numberPickerYear.value - 1] == years!![0]) {
                        setDayAndMonthValues()
                    } else {
                        setDayValues(numberPickerDay.value, numberPickerMonth.value, newVal)
                        setAllMonthValues()
                    }
                }
            }
        }

        isValidAge()
    }

    private fun setDayAndMonthValues() {
        val arrayList: ArrayList<String> = arrayListOf()
        val now = LocalDate.now()
        val other = LocalDate.of(now.year, now.month, now.dayOfMonth)

        val monthValue = other.monthValue

        for (i in 0 until monthValue)
            arrayList.add(months[i])

        val months = arrayList.toTypedArray()

        numberPickerMonth.maxValue = months.size
        numberPickerMonth.displayedValues = months

        arrayList.clear()

        val days = if (numberPickerMonth.value == now.monthValue) {
            for (i in 1..other.dayOfMonth) {
                arrayList.add(String.format("%02d", i))
            }
            arrayList.toTypedArray()
        } else {
            getDays(day = 1, month = numberPickerMonth.value, year = numberPickerYear.value)
        }


        numberPickerDay.maxValue = days!!.size
        numberPickerDay.displayedValues = days
    }

    private fun isValidAge(): Boolean {
        val day = numberPickerDay.value
        val month = numberPickerMonth.value
        val year = years!![numberPickerYear.value - 1].toInt()

        val isValid = LocalDate.of(year, month, day)
            .isBefore(LocalDate.now().minusYears(18))

        Log.e(
            "TAG",
            "age is valid? = $isValid :::::: selected = ${
                LocalDate.of(
                    year,
                    month,
                    day
                )
            }, current = ${LocalDate.now()}"
        )

        return isValid
    }

}