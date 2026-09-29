package com.example.spendwisestudent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spendwisestudent.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var dbHelper: ExpenseDbHelper
    private lateinit var expenseAdapter: ExpenseAdapter

    private val categories = listOf(
        "All Categories",
        "Food",
        "Transport",
        "Education",
        "Bills",
        "Shopping",
        "Entertainment",
        "Health",
        "Other"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityHistoryBinding.inflate(layoutInflater)

        setContentView(binding.root)

        dbHelper = ExpenseDbHelper(this)

        expenseAdapter = ExpenseAdapter(
            emptyList(),

            onClick = { expense ->

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

            onLongClick = {
                // Nothing required here.
            }
        )

        binding.recyclerHistory.layoutManager =
            LinearLayoutManager(this)

        binding.recyclerHistory.adapter =
            expenseAdapter


        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )

        categoryAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        binding.spinnerHistoryCategory.adapter =
            categoryAdapter


        binding.spinnerHistoryCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    loadExpenses(
                        categories[position]
                    )
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }


        binding.btnBackHistory.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        val selectedCategory =
            binding.spinnerHistoryCategory
                .selectedItem
                ?.toString()
                ?: "All Categories"

        loadExpenses(selectedCategory)
    }

    private fun loadExpenses(category: String) {

        val expenses =
            dbHelper.getExpensesByCategory(category)

        expenseAdapter.updateData(expenses)

        binding.tvHistoryCount.text =
            "${expenses.size} expenses"

        binding.tvHistoryEmpty.text =
            if (expenses.isEmpty()) {
                "No expenses found in this category."
            } else {
                ""
            }
    }
}