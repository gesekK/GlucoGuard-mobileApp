package com.example.gluco_guard.activities

import android.graphics.Color
import android.util.Log
import android.widget.Toast
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context

class ChartDataLoader(private val db: FirebaseFirestore, private val auth: FirebaseAuth, private val context: Context) {

    fun loadCurrentDayData(userEmail: String, currentDate: String, lineChart: LineChart) {
        val currentDayDataRef = db.collection("results").document(userEmail)
            .collection(currentDate)

        currentDayDataRef.get().addOnSuccessListener { currentDayData ->
            if (currentDayData.isEmpty) {
                Toast.makeText(context, "No data available for the current day", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            val currentDayEntries = mutableListOf<Entry>()
            val sortedData = currentDayData.sortedBy { it.id }
            var previousHour = 0f
            var previousGlucose = 0f

            for (document in sortedData) {
                val glucose = document.getDouble("glucose")?.toFloat() ?: 0f
                val hour = document.id.toFloatOrNull() ?: 0f

                if (hour >= 10f) {
                    if (previousHour != 0f && previousGlucose != 0f) {
                        currentDayEntries.add(Entry(previousHour, previousGlucose))
                    }
                    currentDayEntries.add(Entry(hour, glucose))
                }

                previousHour = hour
                previousGlucose = glucose
            }

            val thresholdValue = 99f
            val thresholdEntries = mutableListOf<Entry>()
            thresholdEntries.add(Entry(currentDayEntries.firstOrNull()?.x ?: 0f, thresholdValue))
            thresholdEntries.add(Entry(currentDayEntries.lastOrNull()?.x ?: 0f, thresholdValue))

            val dataSet = LineDataSet(currentDayEntries, null)
            val thresholdDataSet = LineDataSet(thresholdEntries, "Threshold")
            thresholdDataSet.color = Color.WHITE
            thresholdDataSet.setDrawCircles(false)
            thresholdDataSet.setDrawValues(false)

            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
            lineChart.axisLeft.setDrawLabels(true)
            lineChart.axisRight.setDrawLabels(false)
            lineChart.axisLeft.setDrawGridLines(false)
            lineChart.axisRight.setDrawGridLines(false)
            lineChart.xAxis.setDrawGridLines(true)
            lineChart.legend.isEnabled = false

            val data = LineData(dataSet, thresholdDataSet)
            lineChart.data = data
            lineChart.invalidate()
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting current day data: ", exception)
            Toast.makeText(context, "Failed to load current day data", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadWeekData(userEmail: String, weeklyChart: BarChart) {
        val calendar = Calendar.getInstance()
        val weekEntries = mutableListOf<BarEntry>()
        val daysOfWeek = listOf(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY)

        // Count a number of successful operations
        var finishedOperations = 0

        for ((index, dayOfWeek) in daysOfWeek.withIndex()) {
            calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek)
            val previousDate = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(calendar.time)
            val previousDayDataRef = db.collection("results").document(userEmail)
                .collection(previousDate)

            previousDayDataRef.get().addOnSuccessListener { previousDayData ->
                val previousDayEntries = mutableListOf<BarEntry>()
                if (previousDayData.isEmpty) {
                    // Add 0 if there is no data for the given day
                    weekEntries.add(BarEntry((index + 1).toFloat(), 0f))
                } else {
                    for (document in previousDayData) {
                        val glucose = document.getDouble("glucose")?.toFloat() ?: 0f
                        val hour = document.id.toFloatOrNull() ?: 0f
                        previousDayEntries.add(BarEntry(hour, glucose))
                    }

                    val glucoseSum = previousDayEntries.sumByDouble { it.y.toDouble() }
                    val glucoseCount = previousDayEntries.size
                    val averageGlucose = if (glucoseCount > 0) (glucoseSum / glucoseCount).toFloat() else 0f

                    weekEntries.add(BarEntry((index + 1).toFloat(), averageGlucose))
                }

                finishedOperations++

                // Check if all operations have been completed
                if (finishedOperations == daysOfWeek.size) {
                    updateWeeklyChart(weekEntries, weeklyChart)
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Error getting data for previous day: ", exception)
                finishedOperations++

                // Check if all operations have been completed
                if (finishedOperations == daysOfWeek.size) {
                    updateWeeklyChart(weekEntries, weeklyChart)
                }
            }
        }
    }

    private fun updateWeeklyChart(weekEntries: List<BarEntry>, weeklyChart: BarChart) {
        val weeklyDataSet = BarDataSet(weekEntries, "Last 7 days")
        weeklyDataSet.color = Color.BLUE
        weeklyDataSet.valueTextSize = 12f

        val xAxisLabels = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
        val xAxisFormatter = IndexAxisValueFormatter(xAxisLabels)
        weeklyChart.xAxis.valueFormatter = xAxisFormatter
        weeklyChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        weeklyChart.xAxis.setDrawGridLines(false)
        weeklyChart.xAxis.setAvoidFirstLastClipping(true)
        weeklyChart.xAxis.labelCount = xAxisLabels.size

        val weeklyData = BarData(weeklyDataSet)

        weeklyChart.data = weeklyData
        weeklyChart.invalidate()

        weeklyChart.axisRight.isEnabled = false
        weeklyChart.axisLeft.setDrawLabels(false)
        weeklyChart.legend.isEnabled = false
    }

    internal fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault())
        return dateFormat.format(Date())
    }

    companion object {
        private const val TAG = "ChartDataLoader"
    }
}
