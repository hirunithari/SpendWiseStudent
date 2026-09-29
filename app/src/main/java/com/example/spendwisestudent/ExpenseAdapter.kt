package com.example.spendwisestudent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spendwisestudent.databinding.ItemExpenseBinding
import java.util.Locale

class ExpenseAdapter(
    private var items: List<Expense>,
    private val onClick: (Expense) -> Unit,
    private val onLongClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(
        private val binding: ItemExpenseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvTitle.text = expense.title
            binding.tvCategoryDate.text = "${expense.category} • ${expense.date}"
            binding.tvAmount.text = String.format(Locale.US, "LKR %,.2f", expense.amount)

            binding.root.setOnClickListener {
                onClick(expense)
            }

            binding.root.setOnLongClickListener {
                onLongClick(expense)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Expense>) {
        items = newItems
        notifyDataSetChanged()
    }
}
