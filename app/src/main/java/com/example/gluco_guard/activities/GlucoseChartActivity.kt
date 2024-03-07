package com.example.gluco_guard.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gluco_guard.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GlucoseChartActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentDayChart: LineChart
    private lateinit var weeklyChart: BarChart
    private lateinit var chartDataLoader: ChartDataLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucose_chart)

        val add_glucose_btn: FloatingActionButton = findViewById(R.id.add_glucose_btn)
        add_glucose_btn.setOnClickListener{
            val intent = Intent(this, GlucoseUploadActivity::class.java)
            startActivity(intent)
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize graphs
        currentDayChart = findViewById(R.id.Chart_day)
        weeklyChart = findViewById(R.id.Chart_week)

        // Initialize ChartDataLoader
        chartDataLoader = ChartDataLoader(db, auth, this)

        loadCurrentDayData()
        loadWeekData()
    }

    private fun loadCurrentDayData() {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email ?: ""
        val currentDate = chartDataLoader.getCurrentDate()

        chartDataLoader.loadCurrentDayData(userEmail, currentDate, currentDayChart)
    }

    private fun loadWeekData() {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email ?: ""

        chartDataLoader.loadWeekData(userEmail, weeklyChart)
    }


}
