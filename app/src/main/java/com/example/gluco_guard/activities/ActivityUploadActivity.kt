package com.example.gluco_guard.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gluco_guard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityUploadActivity : AppCompatActivity() {

    private lateinit var sleepHoursEditText: EditText
    private lateinit var sleepMinutesEditText: EditText
    private lateinit var activityEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        sleepHoursEditText = findViewById(R.id.act_tvSleep)
        sleepMinutesEditText = findViewById(R.id.act_tvSleep2)
        activityEditText = findViewById(R.id.act_tvActivity)
        weightEditText = findViewById(R.id.act_tvWeight)
        saveButton = findViewById(R.id.act_save_button)

        saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        if (userEmail != null) {
            val firestore = FirebaseFirestore.getInstance()

            val currentDate = SimpleDateFormat("dd_MM_yyyy", Locale.getDefault()).format(Date())

            val sleepHoursText = sleepHoursEditText.text.toString()
            val sleepMinutesText = sleepMinutesEditText.text.toString()
            val activityText = activityEditText.text.toString()
            val weightText = weightEditText.text.toString()

            val sleepHours = sleepHoursText.toIntOrNull()
            val sleepMinutes = sleepMinutesText.toIntOrNull()
            val activityLevel = activityText.toIntOrNull()
            val weight = weightText.toDoubleOrNull()

            val data = hashMapOf<String, Any>()

            // Minute value limitation
            if (sleepHours != null && sleepMinutes != null) {
                if (sleepMinutes > 59) {
                    Toast.makeText(this, "The number of minutes cannot exceed 59", Toast.LENGTH_SHORT).show()
                    return
                }

                // Converting values into minutes
                val totalSleepMinutes = sleepHours * 60 + sleepMinutes
                data["sleep_minutes"] = totalSleepMinutes
            }

            activityLevel?.let { data["activity_level"] = it }
            weight?.let { data["weight"] = it }

            // Update database
            if (data.isNotEmpty()) {
                firestore.collection("activity")
                    .document(userEmail)
                    .collection(currentDate)
                    .document("data")
                    .set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "An error occurred while saving data: $e", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No data entered", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "The user is not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
