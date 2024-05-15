package com.example.online_quiz_app

import AchievementAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AchievementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var achievementAdapter: AchievementAdapter
    private lateinit var databaseReference: DatabaseReference
    private val achievements = mutableListOf<Achievement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        val topScorerName = intent.getStringExtra("topScorerName") ?: ""

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        val name = intent.getStringExtra("name")

        userNameTextView.text = name ?: "Username"

        backButton.setOnClickListener {
            // Finish the current activity to go back to MainActivity
            finish()
        }

        recyclerView = findViewById(R.id.leaderboardRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        achievementAdapter = AchievementAdapter(achievements)
        recyclerView.adapter = achievementAdapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("10")

        // Listen for changes in the database and update the RecyclerView accordingly
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                achievements.clear()
                for (childSnapshot in snapshot.children) {
                    val title = childSnapshot.child("achievement").getValue(String::class.java) ?: ""
                    val subtitle = childSnapshot.child("subtitle").getValue(String::class.java) ?: ""
                    val iconUrl = childSnapshot.child("iconUrl").getValue(String::class.java) ?: ""
                    val achieved = childSnapshot.child("achieved").getValue(Boolean::class.java) ?: false

                    val achievement = Achievement(title, subtitle, iconUrl, achieved)
                    achievements.add(achievement)
                }

                if (achievements.isNotEmpty()) {
                    achievements[0].achieved = true
                }

                if (name == topScorerName) {
                    // Activate the last achievement
                    if (achievements.isNotEmpty()) {
                        achievements.last().achieved = true
                    }
                }

                achievementAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

    }
}