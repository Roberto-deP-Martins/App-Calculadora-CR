package com.example.calculadoracr

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.doOnLayout
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

        binding.pesoTextView.setOnClickListener { Toast.makeText(applicationContext,R.string.pesoToast, Toast.LENGTH_SHORT).show() }
        binding.adicionarButton.setOnClickListener { addSubject() }
        binding.calcularButton.setOnClickListener { calculaCR() }
        binding.resetButton.setOnClickListener { reset() }

        /* root é última view a ter layout feito, portanto, dimensões das views que compõem primeira
        linha da tabela já terão sido definidas quando o layout da root tiver sido feita */
        binding.root.doOnLayout {
            if (viewModel.rowsDict.isNotEmpty()) {
                loadAddedSubjects(viewModel.rowsDict)
                binding.calcularButton.visibility = View.VISIBLE
                binding.resetButton.visibility = View.VISIBLE
            }
        }

        if (viewModel.hasCalculated) {
            binding.apresentaCRTextView.visibility = View.VISIBLE
            val result = binding.apresentaCRTextView.text.toString() + " %.2f".format(viewModel.calcutationResult)
            binding.apresentaCRTextView.text = result
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        binding.notaTextView.width = binding.nomeTextView.width
        binding.pesoTextView.width = binding.nomeTextView.width
    }

    private fun loadAddedSubjects(map: MutableMap<String, Pair<Float, Int>>) {
        map.forEach { entry ->
            val newRow = TableRow(this)
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

            viewModel.addSubjectData(subjectName.text.toString(), subjectGrade.text.toString().toFloat(), subjectWeight.text.toString().toInt())

            binding.notaEditText.text.clear()
            binding.nomeEditText.text.clear()
            binding.pesoEditText.text.clear()

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
        viewModel.removeSubjectData(nameRow.text.toString())
        if (viewModel.rowsDict.isEmpty()) binding.calcularButton.visibility = View.GONE
        if (viewModel.subjectCount == 0) binding.resetButton.visibility = View.GONE
    }

    private fun calculaCR() {
        viewModel.hasCalculated = true
        var divisor = 0
        var numerador = 0.0
        for (subjectKey in viewModel.rowsDict) {
            numerador += (subjectKey.value.first * subjectKey.value.second)
            divisor += subjectKey.value.second
        }
        val result: Double = numerador / divisor
        viewModel.calcutationResult = result
        val crText = resources.getString(R.string.apresentaCR) + " %.2f".format(result)
        binding.apresentaCRTextView.text = crText
        binding.apresentaCRTextView.visibility = View.VISIBLE
    }

    private fun reset() {
        val tableChildrenList = binding.subjectsTable.children.toList()
        val subjectRows = tableChildrenList.subList(1, tableChildrenList.lastIndex + 1)
        for (row in subjectRows) { removeSubject(row as TableRow) }
        binding.resetButton.visibility = View.GONE
    }
}