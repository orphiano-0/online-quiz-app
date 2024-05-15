package com.example.online_quiz_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LeaderBoardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)

        val backButton = findViewById<ImageButton>(R.id.backButton)
        val database = FirebaseDatabase.getInstance()
        val leaderboardRef = database.getReference("user_scores")

        leaderboardRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val entries = mutableListOf<UserScore>()
                for (snapshot in dataSnapshot.children) {
                    val name = snapshot.child("name").getValue(String::class.java) ?: ""
                    val score = snapshot.child("score").getValue(Int::class.java) ?: 0
                    val entry = UserScore(name, score)
                    entries.add(entry)
                }
                // sorting users scores in descending order
                val sortedEntries = entries.sortedByDescending { it.score }
                displayLeaderboard(sortedEntries)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("LeaderboardActivity", "Error getting leaderboard data", databaseError.toException())
            }
        })

        // Set onClickListener for the back button
        backButton.setOnClickListener {
            // Finish the current activity to go back to MainActivity
            finish()
        }
    }
    private fun displayLeaderboard(entries: List<UserScore>) {
        val recyclerView: RecyclerView = findViewById(R.id.leaderboardRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val adapter = LeaderboardAdapter(entries)
        recyclerView.adapter = adapter

        // Set item click listener
        adapter.setOnItemClickListener(object : LeaderboardAdapter.OnItemClickListener {
            override fun onItemClick(name: String) {

                val topScorerName = if (entries.isNotEmpty()) entries[0].name else ""

                // Start AchievementActivity and pass necessary data
                val intent = Intent(this@LeaderBoardActivity, AchievementActivity::class.java)
                intent.putExtra("name", name)
                intent.putExtra("topScorerName", topScorerName)
                startActivity(intent)
            }
        })
    }
}

class LeaderboardAdapter(private val entries: List<UserScore>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    // Listener interface for item clicks
    interface OnItemClickListener {
        fun onItemClick(name: String)
    }

    private var listener: OnItemClickListener? = null

    // Method to set the listener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        // Declare views in your leaderboard item layout
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
        val rankingIcon: ImageView = itemView.findViewById(R.id.iconImageView)
        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val entry = entries[position]
                listener?.onItemClick(entry.name)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.nameTextView.text = entry.name
        holder.scoreTextView.text = entry.score.toString()

        when (position) {
            0 -> holder.rankingIcon.setImageResource(R.drawable.first) // 1st place
            1 -> holder.rankingIcon.setImageResource(R.drawable.second) // 2nd place
            2 -> holder.rankingIcon.setImageResource(R.drawable.third) // 3rd place
            else -> {
                // Hide the ranking icon for other positions
                holder.rankingIcon.setImageResource(R.drawable.icon_tt)
            }
        }
    }

    override fun getItemCount(): Int {
        return entries.size
    }


}

