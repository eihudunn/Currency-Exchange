package com.example.currencyexchange

// MainActivity.kt
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var editTextAmount1: EditText
    private lateinit var editTextAmount2: EditText
    private lateinit var spinnerCurrency1: Spinner
    private lateinit var spinnerCurrency2: Spinner
    private lateinit var textViewRate: TextView

    private var exchangeRates = mapOf(
        "VND" to 1.0,
        "CNY" to 0.000292,
        "USD" to 0.000039,
        "JPY" to 0.006066
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextAmount1 = findViewById(R.id.editTextAmount1)
        editTextAmount2 = findViewById(R.id.editTextAmount2)
        spinnerCurrency1 = findViewById(R.id.spinnerCurrency1)
        spinnerCurrency2 = findViewById(R.id.spinnerCurrency2)
        textViewRate = findViewById(R.id.textViewRate)

        val currencies = exchangeRates.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCurrency1.adapter = adapter
        spinnerCurrency2.adapter = adapter

        spinnerCurrency1.setSelection(adapter.getPosition("VND"))
        spinnerCurrency2.setSelection(adapter.getPosition("JPY"))

        editTextAmount1.addTextChangedListener(amountWatcher1)
        editTextAmount2.addTextChangedListener(amountWatcher2)
        spinnerCurrency1.onItemSelectedListener = currencySelectedListener
        spinnerCurrency2.onItemSelectedListener = currencySelectedListener
    }

    private val amountWatcher1 = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (editTextAmount1.isFocused) {
                calculateAndUpdateAmount2()
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val amountWatcher2 = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (editTextAmount2.isFocused) {
                calculateAndUpdateAmount1()
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val currencySelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updateExchangeRateDisplay()
            if (editTextAmount1.isFocused) {
                calculateAndUpdateAmount2()
            } else if (editTextAmount2.isFocused) {
                calculateAndUpdateAmount1()
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun calculateAndUpdateAmount2() {
        val amount1 = editTextAmount1.text.toString().toDoubleOrNull() ?: 0.0
        val rate1 = exchangeRates[spinnerCurrency1.selectedItem.toString()] ?: 1.0
        val rate2 = exchangeRates[spinnerCurrency2.selectedItem.toString()] ?: 1.0
        val amount2 = amount1 * rate2 / rate1
        editTextAmount2.setText(formatAmount(amount2))
    }

    private fun calculateAndUpdateAmount1() {
        val amount2 = editTextAmount2.text.toString().toDoubleOrNull() ?: 0.0
        val rate1 = exchangeRates[spinnerCurrency1.selectedItem.toString()] ?: 1.0
        val rate2 = exchangeRates[spinnerCurrency2.selectedItem.toString()] ?: 1.0
        val amount1 = amount2 * rate1 / rate2
        editTextAmount1.setText(formatAmount(amount1))
    }

    private fun updateExchangeRateDisplay() {
        val rate1 = spinnerCurrency1.selectedItem.toString()
        val rate2 = spinnerCurrency2.selectedItem.toString()
        val exchangeRate = exchangeRates[rate2]!! / exchangeRates[rate1]!!
        textViewRate.text = "1 $rate1 = ${formatAmount(exchangeRate)} $rate2"
    }

    private fun formatAmount(amount: Double): String {
        return DecimalFormat("#.#######").format(amount)
    }
}
