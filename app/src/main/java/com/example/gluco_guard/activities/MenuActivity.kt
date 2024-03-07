package com.example.gluco_guard.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.gluco_guard.R
import com.example.gluco_guard.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MenuActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val username = findViewById<TextView>(R.id.tvUserName)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)
        val user = FirebaseAuth.getInstance().currentUser!!

        findViewById<TextView>(R.id.tvEmail).text = user?.email ?: "email"

        auth = FirebaseAuth.getInstance()
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(user.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                username.text = user?.userName

                if (user?.profileImage == "") {
                    imgProfile.setImageResource(R.drawable.profile_image)
                } else {
                    Glide.with(this@MenuActivity).load(user?.profileImage).into(imgProfile)
                }
            }
        })

        val profileBtn: TextView = findViewById(R.id.profileBtn)
        profileBtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val glucoseBtn: TextView = findViewById(R.id.glucoseBtn)
        glucoseBtn.setOnClickListener {
            val intent = Intent(this, GlucoseChartActivity::class.java)
            startActivity(intent)
        }

        val activeBtn: TextView = findViewById(R.id.activBtn)
        activeBtn.setOnClickListener {
            val intent = Intent(this, ActivityChartsActivity::class.java)
            startActivity(intent)
        }
        val chatBtn: TextView = findViewById(R.id.chatBtn)
        chatBtn.setOnClickListener {
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)
        }

        val homairBtn: TextView = findViewById(R.id.homairBtn)
        homairBtn.setOnClickListener {
            val intent = Intent(this, HomaIrCalculator::class.java)
            startActivity(intent)
        }

        val logoutBtn : TextView = findViewById(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    }
}




