package com.example.online_quiz_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.online_quiz_app.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var quizModelList: MutableList<QuizModel>
    lateinit var adapter: QuizListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizModelList = mutableListOf()
        getDataFromFirebase()

        val leaderboardButton = findViewById<ImageButton>(R.id.leaderboardbutton)

        leaderboardButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LeaderBoardActivity::class.java)
            startActivity(intent)
        }
    }
    private fun setupRecyclerView() {
        adapter = QuizListAdapter(quizModelList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    private fun getDataFromFirebase() {
        FirebaseDatabase.getInstance().reference
            .limitToFirst(10)
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null) {
                            quizModel.questionList = quizModel.questionList.toMutableList().apply { shuffle() }
                            quizModelList.add(quizModel)
                        }
                    }
                }
                setupRecyclerView()
            }
    }
}