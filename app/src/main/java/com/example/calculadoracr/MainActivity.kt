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
import com.example.calculadoracr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var subjectCount = 0
    private val rowsDict = mutableMapOf<String, Pair<Float, Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
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

    private fun addSubject() {

        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }

        if (binding.notaEditText.text.toString().toFloatOrNull() != null &&
            binding.pesoEditText.text.toString().toIntOrNull() != null) {
            val newRow = TableRow(this)

            val subjectName = TextView(this)
            subjectName.text =
                if (binding.nomeEditText.text.toString() == "") {
                    "❌ ${resources.getString(R.string.disciplinaText)} ${subjectCount + 1}"
                }
                else {
                    "❌" + binding.nomeEditText.text
                }
            subjectName.width = binding.nomeHeader.width
            subjectName.textAlignment = View.TEXT_ALIGNMENT_CENTER
            subjectName.textSize = resources.getDimension(R.dimen.textSize) / resources.displayMetrics.scaledDensity

            val subjectGrade = TextView(this)
            subjectGrade.text = binding.notaEditText.text
            subjectGrade.width = binding.notaHeader.width
            subjectGrade.textAlignment = View.TEXT_ALIGNMENT_CENTER
            subjectGrade.textSize = resources.getDimension(R.dimen.textSize) / resources.displayMetrics.scaledDensity

            val subjectWeight = TextView(this)
            subjectWeight.text = binding.pesoEditText.text
            subjectWeight.width = binding.pesoHeader.width
            subjectWeight.textAlignment = View.TEXT_ALIGNMENT_CENTER
            subjectWeight.textSize = resources.getDimension(R.dimen.textSize) / resources.displayMetrics.scaledDensity

            newRow.addView(subjectName)
            newRow.addView(subjectGrade)
            newRow.addView(subjectWeight)
            newRow.setOnClickListener { removeSubject(newRow) }
            binding.subjectsTable.addView(newRow)

            rowsDict[subjectName.text.toString()] =
                Pair(subjectGrade.text.toString().toFloat(), subjectWeight.text.toString().toInt())

            binding.notaEditText.text.clear()
            binding.nomeEditText.text.clear()
            binding.pesoEditText.text.clear()
            subjectCount += 1

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
        rowsDict.remove(nameRow.text.toString())
        if (rowsDict.isEmpty()) binding.calcularButton.visibility = View.GONE
        if (subjectCount == 0) binding.resetButton.visibility = View.GONE
    }

    private fun calculaCR() {
        var divisor = 0
        var numerador = 0.0
        for (i in rowsDict) {
            numerador += (i.value.first * i.value.second)
            divisor += i.value.second
        }
        Log.i("Number Info" ,numerador.toString())
        Log.i("Number Info" ,divisor.toString())
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