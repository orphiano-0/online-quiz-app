package com.example.online_quiz_app

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.online_quiz_app.databinding.ActivityQuizBinding
import com.example.online_quiz_app.databinding.ScoreDialogBinding
import com.google.firebase.database.FirebaseDatabase

class QuizActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        var questionModelList : List<QuestionModel> = listOf()
        var time : String = ""
//        var category: String = ""
    }

    lateinit var binding : ActivityQuizBinding
    var currentQuestionIndex = 0
    var selectedAnswer = ""
    var score = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btn0.setOnClickListener(this@QuizActivity)
            btn1.setOnClickListener(this@QuizActivity)
            btn2.setOnClickListener(this@QuizActivity)
            btn3.setOnClickListener(this@QuizActivity)
            nextBtn.setOnClickListener(this@QuizActivity)
        }

//        category = intent.getStringExtra("Category") ?: ""

        loadQuestions()
        startTimer()
    }

    private fun startTimer(){
        val totalTimeInMillis = time.toInt() * 60 * 1000L
        object : CountDownTimer(totalTimeInMillis,1000L){
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished /1000
                val minutes = seconds/60
                val remainingSeconds = seconds % 60
                binding.timerIndicatorTextview.text = String.format("%02d:%02d", minutes,remainingSeconds)
            }
            override fun onFinish() {
                //Finish the quiz
            }
        }.start()
    }

    private fun loadQuestions() {

        selectedAnswer = ""
        if(currentQuestionIndex == questionModelList.size){
            finishQuiz()
            return
        }

        binding.apply {
            questionIndicatorTextview.text = "Question ${currentQuestionIndex+1} / ${questionModelList.size}"
            questionProgressIndicator.progress =
                ( currentQuestionIndex.toFloat() / questionModelList.size.toFloat() * 100 ).toInt()
            questionTextview.text = questionModelList[currentQuestionIndex].question
            btn0.text = questionModelList[currentQuestionIndex].options[0]
            btn1.text = questionModelList[currentQuestionIndex].options[1]
            btn2.text = questionModelList[currentQuestionIndex].options[2]
            btn3.text = questionModelList[currentQuestionIndex].options[3]
        }
    }

    override fun onClick(view: View?) {

        binding.apply {
            btn0.setBackgroundColor(getColor(R.color.gray))
            btn1.setBackgroundColor(getColor(R.color.gray))
            btn2.setBackgroundColor(getColor(R.color.gray))
            btn3.setBackgroundColor(getColor(R.color.gray))
        }

        val clickedBtn = view as Button
        if(clickedBtn.id==R.id.next_btn){
            //next button is clicked
            if(selectedAnswer.isEmpty()){
                Toast.makeText(applicationContext,"Please select answer to continue",Toast.LENGTH_SHORT).show()
                return;
            }
            if(selectedAnswer == questionModelList[currentQuestionIndex].correct){
                score++
                Log.i("Score of quiz",score.toString())
            }
            currentQuestionIndex++
            loadQuestions()
        } else {
            //options button is clicked
            selectedAnswer = clickedBtn.text.toString()
            clickedBtn.setBackgroundColor(getColor(R.color.orange))
        }
    }

    private fun finishQuiz() {
        val totalQuestions = questionModelList.size
        val percentage = ((score.toFloat() / totalQuestions.toFloat() ) *100 ).toInt()

        val dialogBinding  = ScoreDialogBinding.inflate(layoutInflater)
        dialogBinding.apply {
            scoreProgressIndicator.progress = percentage
            scoreProgressText.text = "$percentage %"
            if(percentage>60){
                scoreTitle.text = "Congratulations! You've passed!"
                scoreTitle.setTextColor(Color.BLUE)
            } else {
                scoreTitle.text = "Failed!"
                scoreTitle.setTextColor(Color.RED)
            }
            scoreSubtitle.text = "$score out of $totalQuestions are correct"
            finishBtn.setOnClickListener {
                finish()
            }
        }

        AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .show()

        getUserName(score)
    }

    private fun saveUserScoreToFirebase(userName: String, score: Int) {
        val userScoreRef = FirebaseDatabase.getInstance().reference.child("user_scores")
        val newUserScoreRef = userScoreRef.push()
        val userScore = UserScore(userName, score)
        newUserScoreRef.setValue(userScore)
    }

    private fun getUserName(score: Int) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.input_dialog, null)
        dialogBuilder.setView(dialogView)

        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val dialogSubtitle = dialogView.findViewById<TextView>(R.id.dialogSubtitle)
        val dialogInput = dialogView.findViewById<EditText>(R.id.dialogInput)

        dialogTitle.text = "Enter your name"
        dialogSubtitle.text = "Please enter your name to record your score!" // Set your subtitle text here


        dialogBuilder.setPositiveButton("OK") { dialog, which ->
            val userName = dialogInput.text.toString()
            // Check if the userName is empty
            if (userName.isNotEmpty()) {
                saveUserScoreToFirebase(userName, score)
            } else {
                // Show an error message if the userName is empty
                Toast.makeText(this, "Your process will not be recorded", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }


}