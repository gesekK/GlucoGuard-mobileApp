package com.example.gluco_guard.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gluco_guard.R

class HomaIrCalculator : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homair_calculator)


        val insulinText = findViewById<EditText>(R.id.calc_etInsulin)
        val glucoseText = findViewById<EditText>(R.id.calc_etGlucose)
        val calcButton = findViewById<Button>(R.id.btnCalculate)

        calcButton.setOnClickListener {
            val insulin = insulinText.text.toString()
            val glucose = glucoseText.text.toString()

            if (validateInput(insulin, glucose)) {
                val homaIR = (insulin.toFloat() * glucose.toFloat() / 18) / 22.5
                val homaIR2Digits = String.format("%.2f", homaIR).toFloat()
                displayResult(homaIR2Digits)
            }
        }

    }

    private fun validateInput(insulin: String?, glucose: String?): Boolean {

        return when {
            insulin.isNullOrEmpty() -> {
                Toast.makeText(this, "Field is empty", Toast.LENGTH_LONG).show()
                return false
            }

            glucose.isNullOrEmpty() -> {
                Toast.makeText(this, "Field is empty", Toast.LENGTH_LONG).show()
                return false
            }

            else -> {
                return true
            }
        }

    }

    private fun displayResult(homaIR: Float) {
        val resultIndex = findViewById<TextView>(R.id.tvIndex)
        val resultDescription = findViewById<TextView>(R.id.tvDescription)

        resultIndex.text = homaIR.toString()

        var resultText = ""
        var color = 0

        when {
            homaIR < 2.5 -> {
                resultText = "Correct result"
                color = R.color.correct
            }

            homaIR > 2.5 -> {
                resultText = "Insulin resistance"
                color = R.color.incorrect
            }
        }
        resultDescription.setTextColor(ContextCompat.getColor(this, color))
        resultDescription.text = resultText

    }

}