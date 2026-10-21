package com.example.data.repository

import com.example.data.db.BudgetDao
import com.example.data.db.ExpenseDao
import com.example.data.db.SpendlyDatabase
import com.example.data.model.BudgetSettingEntity
import com.example.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class SpendlyRepository(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val database: SpendlyDatabase
) {
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val budgetSetting: Flow<BudgetSettingEntity?> = budgetDao.getBudgetSetting()

    suspend fun insertExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    suspend fun deleteExpenseById(id: Long) {
        expenseDao.deleteById(id)
    }

    suspend fun updateBudgetCap(newCap: Double) {
        val current = budgetDao.getBudgetSettingOnce() ?: BudgetSettingEntity()
        budgetDao.saveBudgetSetting(current.copy(monthlyBudgetCap = newCap))
    }

    suspend fun updateNetBalance(newBalance: Double) {
        val current = budgetDao.getBudgetSettingOnce() ?: BudgetSettingEntity()
        budgetDao.saveBudgetSetting(current.copy(initialNetBalance = newBalance))
    }

    suspend fun resetDefaults() {
        expenseDao.clearAll()
        SpendlyDatabase.populateInitialData(database)
    }
}
