package com.example.calculadoracr

import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    var subjectCount = 0
    val rowsDict = mutableMapOf<String, Pair<Float, Int>>()  // Dict onde chave = nome da disciplina e valor é par de nota e peso

    fun addSubjectData(name: String, grade: Float, weight: Int) {
        rowsDict[name] = Pair(grade, weight)
        subjectCount++
    }

    fun removeSubjectData(name: String) {
        rowsDict.remove(name)
        subjectCount--
    }
}