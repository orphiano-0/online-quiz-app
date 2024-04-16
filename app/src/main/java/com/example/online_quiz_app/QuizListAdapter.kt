package com.example.online_quiz_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.online_quiz_app.databinding.QuizItemRecycleRowBinding

class QuizListAdapter(private val quizModelList: List<QuizModel>) :
    RecyclerView.Adapter<QuizListAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: QuizItemRecycleRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: QuizModel) {
            binding.apply {
                quizTitleText.text = model.title
                quizSubtitleText.text = model.subtitle
                quizTimeText.text = model.time + " mins"
                root.setOnClickListener {
                    val intent = Intent(root.context, QuizActivity::class.java)
                    QuizActivity.questionModelList = model.questionList
                    QuizActivity.time = model.time
                    root.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QuizItemRecycleRowBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizModelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(quizModelList[position])
    }
}