package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String = "expense", // "expense", "income", "transfer"
    val category: String, // "Food & Dining", "Groceries", "Transport", "Shopping", "Bills & Utilities", "Entertainment", "Health", "Income", "Other"
    val dateMillis: Long,
    val paymentMethod: String = "Chase Visa",
    val note: String = "",
    val isSplit: Boolean = false,
    val splitCount: Int = 2
)
