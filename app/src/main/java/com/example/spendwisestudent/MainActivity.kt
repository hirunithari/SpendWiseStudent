package com.example.spendwisestudent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendwisestudent.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: ExpenseDbHelper
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ExpenseDbHelper(this)

        // RecyclerView adapter
        adapter = ExpenseAdapter(
            items = emptyList(),

            onClick = { expense ->

                // Open Edit Expense screen
                val intent =
                    Intent(
                        this,
                        AddEditExpenseActivity::class.java
                    )

                intent.putExtra(
                    AddEditExpenseActivity.EXTRA_EXPENSE_ID,
                    expense.id
                )

                startActivity(intent)
            },

            onLongClick = { expense ->

                // Show delete confirmation
                showDeleteDialog(expense)
            }
        )

        binding.recyclerExpenses.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerExpenses.adapter =
            adapter


        // -----------------------------
        // ADD EXPENSE BUTTON
        // -----------------------------
        binding.btnAddExpense.setOnClickListener {

            val intent =
                Intent(
                    this,
                    AddEditExpenseActivity::class.java
                )

            startActivity(intent)
        }


        // -----------------------------
        // SET BUDGET BUTTON
        // -----------------------------
        binding.btnBudget.setOnClickListener {

            val intent =
                Intent(
                    this,
                    BudgetActivity::class.java
                )

            startActivity(intent)
        }


        // -----------------------------
        // STATISTICS BUTTON
        // -----------------------------
        binding.btnStatistics.setOnClickListener {

            val intent =
                Intent(
                    this,
                    StatisticsActivity::class.java
                )

            startActivity(intent)
        }


        // -----------------------------
        // HISTORY BUTTON
        // -----------------------------
        binding.btnHistory.setOnClickListener {

            val intent =
                Intent(
                    this,
                    HistoryActivity::class.java
                )

            startActivity(intent)
        }


        // -----------------------------
        // SETTINGS BUTTON
        // -----------------------------
        binding.btnSettings.setOnClickListener {

            val intent =
                Intent(
                    this,
                    SettingsActivity::class.java
                )

            startActivity(intent)
        }


        // -----------------------------
        // SEARCH EXPENSES
        // -----------------------------
        binding.etSearch.doAfterTextChanged {

            val keyword =
                it?.toString().orEmpty()

            loadExpenses(keyword)
        }
    }


    // Runs whenever user returns to dashboard
    override fun onResume() {
        super.onResume()

        loadExpenses(
            binding.etSearch.text
                ?.toString()
                .orEmpty()
        )

        updateDashboard()
    }


    // -----------------------------
    // LOAD / SEARCH EXPENSES
    // -----------------------------
    private fun loadExpenses(keyword: String) {

        val expenses =
            dbHelper.searchExpenses(keyword)

        adapter.updateData(expenses)

        binding.tvEmpty.text =
            if (expenses.isEmpty()) {

                "No expenses found."

            } else {

                ""
            }
    }


    // -----------------------------
    // UPDATE DASHBOARD
    // -----------------------------
    private fun updateDashboard() {

        // Current month example: 2026-08
        val month =
            SimpleDateFormat(
                "yyyy-MM",
                Locale.US
            ).format(Date())


        // Get total spent during current month
        val spent =
            dbHelper.getMonthlyTotal(month)


        // Get saved monthly budget
        val prefs =
            getSharedPreferences(
                BudgetActivity.PREFS_NAME,
                MODE_PRIVATE
            )

        val budget =
            prefs.getFloat(
                BudgetActivity.KEY_BUDGET,
                0f
            ).toDouble()


        // Show monthly spent amount
        binding.tvMonthlySpent.text =
            String.format(
                Locale.US,
                "LKR %,.2f",
                spent
            )


        // Check if user has set a budget
        if (budget > 0) {

            val remaining =
                budget - spent


            // Show remaining budget
            binding.tvBudgetRemaining.text =
                String.format(
                    Locale.US,
                    "LKR %,.2f",
                    remaining
                )


            // Show budget status
            binding.tvBudgetStatus.text =
                when {

                    remaining < 0 -> {
                        "Budget exceeded"
                    }

                    remaining < budget * 0.2 -> {
                        "Almost at your budget limit"
                    }

                    else -> {
                        "You are within budget"
                    }
                }

        } else {

            // No budget saved
            binding.tvBudgetRemaining.text =
                "Not set"

            binding.tvBudgetStatus.text =
                "Set a monthly budget to track spending"
        }
    }


    // -----------------------------
    // DELETE EXPENSE
    // -----------------------------
    private fun showDeleteDialog(
        expense: Expense
    ) {

        AlertDialog.Builder(this)
            .setTitle("Delete Expense")

            .setMessage(
                "Are you sure you want to delete '${expense.title}'?"
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                dbHelper.deleteExpense(
                    expense.id
                )

                // Refresh expense list
                loadExpenses(
                    binding.etSearch.text
                        ?.toString()
                        .orEmpty()
                )

                // Refresh dashboard values
                updateDashboard()
            }

            .setNegativeButton(
                "Cancel",
                null
            )

            .show()
    }
}