package com.example.calculadoracr

import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    var subjectCount = 0
    val rowsDict = mutableMapOf<String, Pair<Float, Int>>()  // Dict onde chave = nome da disciplina e valor é par de nota e peso
}