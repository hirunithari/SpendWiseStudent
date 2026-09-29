package com.example.spendwisestudent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spendwisestudent.databinding.ActivityStatisticsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var dbHelper: ExpenseDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ExpenseDbHelper(this)

        binding.btnBackStatistics.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadStatistics()
    }

    private fun loadStatistics() {

        val monthKey =
            SimpleDateFormat("yyyy-MM", Locale.US).format(Date())

        val monthName =
            SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())

        val monthlySpent = dbHelper.getMonthlyTotal(monthKey)
        val totalSpent = dbHelper.getAllTimeTotal()
        val count = dbHelper.getExpenseCount()

        binding.tvStatisticsMonth.text = monthName

        binding.tvStatisticsMonthlySpent.text =
            String.format(
                Locale.US,
                "LKR %,.2f",
                monthlySpent
            )

        binding.tvStatisticsTotal.text =
            String.format(
                Locale.US,
                "LKR %,.2f",
                totalSpent
            )

        binding.tvStatisticsCount.text =
            "$count expenses"

        val prefs = getSharedPreferences(
            BudgetActivity.PREFS_NAME,
            MODE_PRIVATE
        )

        val budget =
            prefs.getFloat(BudgetActivity.KEY_BUDGET, 0f)
                .toDouble()

        if (budget > 0) {

            val remaining = budget - monthlySpent

            binding.tvStatisticsRemaining.text =
                String.format(
                    Locale.US,
                    "LKR %,.2f",
                    remaining
                )

        } else {

            binding.tvStatisticsRemaining.text =
                "Not set"
        }

        val highest = dbHelper.getHighestExpense()

        if (highest != null) {

            binding.tvStatisticsHighest.text =
                String.format(
                    Locale.US,
                    "%s • LKR %,.2f",
                    highest.title,
                    highest.amount
                )

        } else {

            binding.tvStatisticsHighest.text =
                "No expenses"
        }

        val topCategory = dbHelper.getTopCategory()

        if (topCategory != null) {

            binding.tvStatisticsCategory.text =
                String.format(
                    Locale.US,
                    "%s • LKR %,.2f",
                    topCategory.first,
                    topCategory.second
                )

        } else {

            binding.tvStatisticsCategory.text =
                "No data"
        }
    }
}