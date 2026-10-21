package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BudgetSettingEntity
import com.example.data.model.ExpenseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(
    entities = [ExpenseEntity::class, BudgetSettingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SpendlyDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: SpendlyDatabase? = null

        fun getDatabase(context: Context): SpendlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpendlyDatabase::class.java,
                    "spendly_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: SpendlyDatabase) {
            val expenseDao = database.expenseDao()
            val budgetDao = database.budgetDao()

            if (budgetDao.getBudgetSettingOnce() == null) {
                budgetDao.saveBudgetSetting(
                    BudgetSettingEntity(
                        id = 1,
                        monthlyBudgetCap = 4000.0,
                        userName = "Sarah",
                        initialNetBalance = 14850.40
                    )
                )
            }

            if (expenseDao.getCount() == 0) {
                val now = System.currentTimeMillis()
                val calendar = Calendar.getInstance()

                // Helper to get relative millis
                fun timeAgo(daysAgo: Int, hour: Int, minute: Int): Long {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = now
                    cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)
                    cal.set(Calendar.SECOND, 0)
                    return cal.timeInMillis
                }

                val initialItems = listOf(
                    // Today
                    ExpenseEntity(
                        title = "Sweetgreen",
                        amount = 48.50,
                        type = "expense",
                        category = "Food & Dining",
                        dateMillis = timeAgo(0, 14, 15),
                        paymentMethod = "Chase Visa",
                        note = "Lunch with Alex at Sweetgreen"
                    ),
                    ExpenseEntity(
                        title = "Blue Bottle Coffee",
                        amount = 6.75,
                        type = "expense",
                        category = "Food & Dining",
                        dateMillis = timeAgo(0, 9, 30),
                        paymentMethod = "Apple Pay",
                        note = "Morning iced latte"
                    ),
                    ExpenseEntity(
                        title = "Subway Transit Pass",
                        amount = 35.70,
                        type = "expense",
                        category = "Transport",
                        dateMillis = timeAgo(0, 8, 15),
                        paymentMethod = "Apple Pay",
                        note = "Weekly metro pass refill"
                    ),

                    // Yesterday
                    ExpenseEntity(
                        title = "Target Superstore",
                        amount = 114.20,
                        type = "expense",
                        category = "Shopping",
                        dateMillis = timeAgo(1, 18, 45),
                        paymentMethod = "Chase Visa",
                        note = "Household & Kitchen supplies"
                    ),
                    ExpenseEntity(
                        title = "Netflix Subscription",
                        amount = 19.99,
                        type = "expense",
                        category = "Entertainment",
                        dateMillis = timeAgo(1, 13, 0),
                        paymentMethod = "Debit Card",
                        note = "Monthly premium subscription"
                    ),
                    ExpenseEntity(
                        title = "Lime Scooter",
                        amount = 30.01,
                        type = "expense",
                        category = "Transport",
                        dateMillis = timeAgo(1, 11, 20),
                        paymentMethod = "Apple Pay",
                        note = "Downtown rides"
                    ),

                    // 2 Days Ago
                    ExpenseEntity(
                        title = "Equinox Fitness",
                        amount = 185.00,
                        type = "expense",
                        category = "Health",
                        dateMillis = timeAgo(2, 16, 10),
                        paymentMethod = "Debit Card",
                        note = "Gym membership & personal trainer"
                    ),
                    ExpenseEntity(
                        title = "Trader Joe's",
                        amount = 92.40,
                        type = "expense",
                        category = "Groceries",
                        dateMillis = timeAgo(2, 12, 30),
                        paymentMethod = "Debit Card",
                        note = "Weekly grocery stock"
                    ),
                    ExpenseEntity(
                        title = "Venmo Payout (Dinner Split)",
                        amount = 42.00,
                        type = "income",
                        category = "Income",
                        dateMillis = timeAgo(2, 10, 0),
                        paymentMethod = "Deposit",
                        note = "Weekend dinner reimbursement"
                    ),

                    // 3 Days Ago (Thu peak in design)
                    ExpenseEntity(
                        title = "Whole Foods Market",
                        amount = 84.20,
                        type = "expense",
                        category = "Groceries",
                        dateMillis = timeAgo(3, 14, 15),
                        paymentMethod = "Credit Card",
                        note = "Organic produce & snacks"
                    ),
                    ExpenseEntity(
                        title = "Uber Ride",
                        amount = 24.50,
                        type = "expense",
                        category = "Transport",
                        dateMillis = timeAgo(3, 23, 20),
                        paymentMethod = "Debit Card",
                        note = "Late night ride home"
                    ),
                    ExpenseEntity(
                        title = "Freelance Design Payout",
                        amount = 1200.00,
                        type = "income",
                        category = "Income",
                        dateMillis = timeAgo(3, 9, 0),
                        paymentMethod = "Direct Deposit",
                        note = "Brand identity sprint milestone"
                    ),

                    // Earlier in month
                    ExpenseEntity(
                        title = "Apartment Utilities",
                        amount = 250.00,
                        type = "expense",
                        category = "Bills & Utilities",
                        dateMillis = timeAgo(6, 10, 0),
                        paymentMethod = "Debit Card",
                        note = "Electric & high-speed fiber internet"
                    ),
                    ExpenseEntity(
                        title = "Zara Fashion",
                        amount = 135.00,
                        type = "expense",
                        category = "Shopping",
                        dateMillis = timeAgo(8, 15, 30),
                        paymentMethod = "Chase Visa",
                        note = "Autumn jacket & accessories"
                    ),
                    ExpenseEntity(
                        title = "AMC Theatres",
                        amount = 45.00,
                        type = "expense",
                        category = "Entertainment",
                        dateMillis = timeAgo(10, 20, 0),
                        paymentMethod = "Apple Pay",
                        note = "Movie tickets & popcorn"
                    ),
                    ExpenseEntity(
                        title = "Pharmacy & Skincare",
                        amount = 65.00,
                        type = "expense",
                        category = "Health",
                        dateMillis = timeAgo(12, 11, 45),
                        paymentMethod = "Cash",
                        note = "Vitamins and skincare"
                    ),
                    ExpenseEntity(
                        title = "Monthly Salary Direct Deposit",
                        amount = 3500.00,
                        type = "income",
                        category = "Income",
                        dateMillis = timeAgo(15, 8, 30),
                        paymentMethod = "Direct Deposit",
                        note = "Payroll"
                    )
                )

                expenseDao.insertAll(initialItems)
            }
        }
    }
}
