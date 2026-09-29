package com.example.spendwisestudent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.spendwisestudent.databinding.ActivitySettingsBinding
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var dbHelper: ExpenseDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivitySettingsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dbHelper = ExpenseDbHelper(this)

        loadBudget()


        binding.btnResetBudget.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Reset Budget")
                .setMessage(
                    "Do you want to remove your monthly budget?"
                )
                .setPositiveButton("Reset") { _, _ ->

                    val prefs =
                        getSharedPreferences(
                            BudgetActivity.PREFS_NAME,
                            MODE_PRIVATE
                        )

                    prefs.edit()
                        .remove(BudgetActivity.KEY_BUDGET)
                        .apply()

                    loadBudget()

                    Toast.makeText(
                        this,
                        "Budget reset",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        binding.btnClearExpenses.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Clear All Expenses")
                .setMessage(
                    "This will permanently remove all expense records."
                )
                .setPositiveButton("Clear") { _, _ ->

                    dbHelper.clearAllExpenses()

                    Toast.makeText(
                        this,
                        "All expenses cleared",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        binding.btnBackSettings.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBudget()
    }

    private fun loadBudget() {

        val prefs =
            getSharedPreferences(
                BudgetActivity.PREFS_NAME,
                MODE_PRIVATE
            )

        val budget =
            prefs.getFloat(
                BudgetActivity.KEY_BUDGET,
                0f
            )

        if (budget > 0) {

            binding.tvSettingsBudget.text =
                String.format(
                    Locale.US,
                    "LKR %,.2f",
                    budget
                )

        } else {

            binding.tvSettingsBudget.text =
                "Not set"
        }
    }
}