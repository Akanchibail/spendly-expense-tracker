package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_settings")
data class BudgetSettingEntity(
    @PrimaryKey
    val id: Int = 1,
    val monthlyBudgetCap: Double = 4000.0,
    val userName: String = "Sarah",
    val initialNetBalance: Double = 14850.40
)
