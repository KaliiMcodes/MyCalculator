package com.example.mycalculator

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycalculator.databinding.ActivityMainBinding
import com.example.mycalculator.model.CalculatorViewModel
import com.example.mycalculator.model.HistoryAdapter
import androidx.activity.viewModels
import androidx.lifecycle.Observer


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scrollView)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupListeners()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(viewModel.historyList.value ?: mutableListOf())
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewHistory.adapter = historyAdapter
    }

    private fun setupListeners() {
        with(binding) {
            arrayOf(
                txtZero, txtOne, txtTwo, txtThree, txtFour,
                txtFive, txtSix, txtSeven, txtEight, txtNine,
                txtAdd, txtSubtract, txtMutiply, txtDivide,
                txtOpenBracket, txtClosedBracket, txtPercentage, txtEqual,
                txtClear, txtSin, txtCos, txtLn, txtLog, txtE, txtTan, txtExp, txtPi
            ).forEach { textView ->
                textView.setOnClickListener { viewModel.onButtonClick((textView as TextView).text.toString()) }
            }
            imgBackspace.setOnClickListener { viewModel.onBackspaceClick() }
            imgAutoRotate.setOnClickListener {
                if (scientificButtonsLayout.visibility == View.GONE) {
                    scientificButtonsLayout.visibility = View.VISIBLE
                } else {
                    scientificButtonsLayout.visibility = View.GONE
                }
            }
            imgCurrencyConverter.setOnClickListener {
                Log.d("MainActivity", "Currency Converter Clicked")
                val amountToConvert = viewModel.displayText.value?.toDoubleOrNull()
                if (amountToConvert != null) {
                    Log.d("MainActivity", "Amount to Convert: $amountToConvert")
                    val currencyFrom = "KES"
                    val currencyTo = "USD"
                    viewModel.performCurrencyConversion(amountToConvert, currencyFrom, currencyTo)
                } else {
                    Log.d("MainActivity", "Invalid Amount")
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.displayText.observe(this, Observer {
            binding.txtDisplay.text = it
        })
        viewModel.resultText.observe(this, Observer {
            binding.txtResults.text = it
        })
        viewModel.historyList.observe(this, Observer {
            historyAdapter.updateHistory(it)
            binding.recyclerViewHistory.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        })
    }
}
