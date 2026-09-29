package com.example.spendwisestudent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendwisestudent.databinding.ActivityBudgetBinding

class BudgetActivity : AppCompatActivity() {

    companion object {
        const val PREFS_NAME = "spendwise_prefs"
        const val KEY_BUDGET = "monthly_budget"
    }

    private lateinit var binding: ActivityBudgetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val currentBudget = prefs.getFloat(KEY_BUDGET, 0f)

        if (currentBudget > 0) {
            binding.etBudget.setText(currentBudget.toString())
        }

        binding.btnSaveBudget.setOnClickListener {
            val value = binding.etBudget.text.toString().trim().toFloatOrNull()

            if (value == null || value <= 0) {
                binding.etBudget.error = "Enter a valid monthly budget"
                return@setOnClickListener
            }

            prefs.edit()
                .putFloat(KEY_BUDGET, value)
                .apply()

            Toast.makeText(this, "Budget saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnCancelBudget.setOnClickListener {
            finish()
        }
    }
}
