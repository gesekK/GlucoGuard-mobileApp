package com.example.gluco_guard.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.gluco_guard.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    private lateinit var btnSave: Button
    private lateinit var etUserName: EditText
    private lateinit var etDateOfBirth: EditText
    private lateinit var etSurname: EditText
    private lateinit var imgBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        btnSave = findViewById(R.id.btnSave)
        etUserName = findViewById(R.id.etUserName)
        etDateOfBirth = findViewById(R.id.etDateOfBirth)
        etSurname = findViewById(R.id.etSurname)
        imgBack = findViewById(R.id.imgBack)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    etUserName.setText(it.userName)
                    etUserName.hint = it.userName

                    etDateOfBirth.setText(it.dateOfBirth)
                    etDateOfBirth.hint = it.dateOfBirth

                    etSurname.setText(it.surname)
                    etSurname.hint = it.surname

                }
            }
        })

        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        val updatedUserName = etUserName.text.toString().trim()
        val updatedDateOfBirth = etDateOfBirth.text.toString().trim()
        val updatedSurname = etSurname.text.toString().trim()

        val currentUserName = etUserName.hint.toString()
        val currentDateOfBirth = etDateOfBirth.hint.toString()
        val currentSurname = etSurname.hint.toString()

        val updatedData: HashMap<String, Any> = hashMapOf()

        if (updatedUserName.isNotEmpty() && updatedUserName != currentUserName && isValidUsername(updatedUserName)) {
            updatedData["userName"] = updatedUserName
        } else if (updatedUserName.isNotEmpty() && updatedUserName != currentUserName && !isValidUsername(updatedUserName)) {
            Toast.makeText(
                applicationContext,
                "Invalid username format. Please use only letters",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (updatedSurname.isNotEmpty() && updatedSurname != currentSurname && isValidSurname(updatedSurname)) {
            updatedData["surname"] = updatedSurname
        } else if (updatedSurname.isNotEmpty() && updatedSurname != currentSurname && !isValidSurname(updatedSurname)) {
            Toast.makeText(
                applicationContext,
                "Invalid surname format. Please use only letters",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (updatedDateOfBirth.isNotEmpty() && updatedDateOfBirth != currentDateOfBirth && isValidDate(updatedDateOfBirth)) {
            updatedData["dateOfBirth"] = updatedDateOfBirth
        } else if (updatedDateOfBirth.isNotEmpty() && updatedDateOfBirth != currentDateOfBirth && !isValidDate(updatedDateOfBirth)) {
            Toast.makeText(
                applicationContext,
                "Invalid date format. Please use DD-MM-YYYY",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (updatedData.isNotEmpty()) {
            databaseReference.updateChildren(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Changes saved successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Failed to save changes: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(
                applicationContext,
                "No changes made",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isValidDate(date: String): Boolean {
        val regex = """^\d{2}-\d{2}-\d{4}$""".toRegex()
        return regex.matches(date)
    }

    private fun isValidUsername(username: String): Boolean {
        val regex = """^[a-zA-Z]+$""".toRegex()
        return regex.matches(username)
    }

    private fun isValidSurname(surname: String): Boolean {
        val regex = """^[a-zA-Z]+$""".toRegex()
        return regex.matches(surname)
    }
}
