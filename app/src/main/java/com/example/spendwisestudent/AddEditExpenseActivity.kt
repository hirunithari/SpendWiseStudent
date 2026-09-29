package com.example.spendwisestudent

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendwisestudent.databinding.ActivityAddEditExpenseBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditExpenseActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EXPENSE_ID = "expense_id"
    }

    private lateinit var binding: ActivityAddEditExpenseBinding
    private lateinit var dbHelper: ExpenseDbHelper

    private var expenseId: Int = 0
    private val selectedDate = Calendar.getInstance()

    private val categories = listOf(
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

        binding = ActivityAddEditExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = ExpenseDbHelper(this)

        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        expenseId = intent.getIntExtra(EXTRA_EXPENSE_ID, 0)

        if (expenseId != 0) {
            binding.tvFormTitle.text = "Edit Expense"
            binding.btnSave.text = "Update Expense"
            loadExpense(expenseId)
        } else {
            binding.tvFormTitle.text = "Add Expense"
            binding.etDate.setText(today())
        }

        binding.btnSave.setOnClickListener {
            saveExpense()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadExpense(id: Int) {
        val expense = dbHelper.getExpenseById(id) ?: return

        binding.etTitle.setText(expense.title)
        binding.etAmount.setText(expense.amount.toString())
        binding.etDate.setText(expense.date)

        val categoryIndex = categories.indexOf(expense.category)
        if (categoryIndex >= 0) {
            binding.spinnerCategory.setSelection(categoryIndex)
        }
    }

    private fun saveExpense() {
        val title = binding.etTitle.text.toString().trim()
        val amountText = binding.etAmount.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        val date = binding.etDate.text.toString().trim()

        if (title.isBlank()) {
            binding.etTitle.error = "Enter a title"
            binding.etTitle.requestFocus()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "Enter a valid amount"
            binding.etAmount.requestFocus()
            return
        }

        if (date.isBlank()) {
            Toast.makeText(this, "Select a date", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Expense(
            id = expenseId,
            title = title,
            amount = amount,
            category = category,
            date = date
        )

        if (expenseId == 0) {
            dbHelper.insertExpense(expense)
            Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.updateExpense(expense)
            Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun showDatePicker() {
        val current = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                binding.etDate.setText(formatter.format(selectedDate.time))
            },
            current.get(Calendar.YEAR),
            current.get(Calendar.MONTH),
            current.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun today(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US)
            .format(Calendar.getInstance().time)
    }
}
