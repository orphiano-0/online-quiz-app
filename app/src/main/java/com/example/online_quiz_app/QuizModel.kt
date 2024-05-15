package com.example.online_quiz_app

data class QuizModel(
    val id : String,
    val title : String,
    val subtitle : String,
    val time : String,
    var questionList : List<QuestionModel>
){
    constructor() : this("","","","", emptyList())
}

data class QuestionModel(
    val question : String,
    val options : List<String>,
    val correct : String,
){
    constructor() : this ("", emptyList(),"")
}

data class UserScore(
    val name: String,
    val score: Int
)

data class Achievement(
    val title: String,
    val subtitle: String,
    val iconUrl: String,
    var achieved: Boolean
)
