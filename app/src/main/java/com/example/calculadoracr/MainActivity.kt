package com.example.calculadoracr

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.calculadoracr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (viewModel.rowsDict.isNotEmpty()) {
            loadAddedSubjects(viewModel.rowsDict)
        }
        binding.pesoTextView.setOnClickListener { Toast.makeText(applicationContext,R.string.pesoToast, Toast.LENGTH_SHORT).show() }
        binding.adicionarButton.setOnClickListener { addSubject() }
        binding.calcularButton.setOnClickListener { calculaCR() }
        binding.resetButton.setOnClickListener { reset() }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        binding.notaTextView.width = binding.nomeTextView.width
        binding.pesoTextView.width = binding.nomeTextView.width
    }

    private fun loadAddedSubjects(map: MutableMap<String, Pair<Float, Int>>) {
        val newRow = TableRow(this)
        map.forEach { entry ->
            newRow.addView(setUpRowTextView(entry.key, binding.nomeHeader.width))  // Nome
            newRow.addView(setUpRowTextView(entry.value.first.toString(), binding.notaHeader.width))  // Nota
            newRow.addView(setUpRowTextView(entry.value.second.toString(), binding.pesoHeader.width))  // Peso
            newRow.setOnClickListener { removeSubject(newRow) }
            binding.subjectsTable.addView(newRow)
        }
    }

    private fun setUpRowTextView(text: String, width: Int) : TextView {
        val newTextView = TextView(this)
        newTextView.text = text
        newTextView.width = width
        newTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        newTextView.textSize = resources.getDimension(R.dimen.textSize) / resources.displayMetrics.scaledDensity
        return newTextView
    }

    private fun addSubject() {

        this.currentFocus?.let { view ->  // Esconde teclado
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        if (binding.notaEditText.text.toString().toFloatOrNull() != null &&
            binding.pesoEditText.text.toString().toIntOrNull() != null) {
            val newRow = TableRow(this)

            val subjectNameText =
                if (binding.nomeEditText.text.toString() == "") {
                    "❌ ${resources.getString(R.string.disciplinaText)} ${viewModel.subjectCount + 1}"
                }
                else {
                    "❌ " + binding.nomeEditText.text
                }

            val subjectName = setUpRowTextView(subjectNameText, binding.nomeHeader.width)
            val subjectGrade = setUpRowTextView(binding.notaEditText.text.toString(), binding.notaHeader.width)
            val subjectWeight = setUpRowTextView( binding.pesoEditText.text.toString(), binding.pesoHeader.width)

            newRow.addView(subjectName)
            newRow.addView(subjectGrade)
            newRow.addView(subjectWeight)
            newRow.setOnClickListener { removeSubject(newRow) }
            binding.subjectsTable.addView(newRow)

                viewModel.rowsDict[subjectName.text.toString()] =
                Pair(subjectGrade.text.toString().toFloat(), subjectWeight.text.toString().toInt())

            binding.notaEditText.text.clear()
            binding.nomeEditText.text.clear()
            binding.pesoEditText.text.clear()
                viewModel.subjectCount += 1

            binding.calcularButton.visibility = View.VISIBLE
            binding.resetButton.visibility = View.VISIBLE
        }
        else {
            Toast.makeText(applicationContext,R.string.noValues, Toast.LENGTH_LONG).show()
        }
    }

    private fun removeSubject(row: TableRow) {
        binding.subjectsTable.removeView(row)
        val rowViews = row.children.toList()
        val nameRow = rowViews[0] as TextView
        viewModel.rowsDict.remove(nameRow.text.toString())
        if (viewModel.rowsDict.isEmpty()) binding.calcularButton.visibility = View.GONE
        if (viewModel.subjectCount == 0) binding.resetButton.visibility = View.GONE
    }

    private fun calculaCR() {
        var divisor = 0
        var numerador = 0.0
        for (i in viewModel.rowsDict) {
            numerador += (i.value.first * i.value.second)
            divisor += i.value.second
        }
        binding.CRTextView.text = "%.2f".format(numerador / divisor)
        binding.ApresentaCRTextView.visibility = View.VISIBLE
        binding.CRTextView.visibility = View.VISIBLE
    }

    private fun reset() {
        val tableChildrenList = binding.subjectsTable.children.toList()
        val subjectRows = tableChildrenList.subList(1, tableChildrenList.lastIndex + 1)
        for (row in subjectRows) { removeSubject(row as TableRow) }
        binding.resetButton.visibility = View.GONE
    }
}