package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BudgetSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget_settings WHERE id = 1 LIMIT 1")
    fun getBudgetSetting(): Flow<BudgetSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBudgetSetting(setting: BudgetSettingEntity)

    @Query("SELECT * FROM budget_settings WHERE id = 1 LIMIT 1")
    suspend fun getBudgetSettingOnce(): BudgetSettingEntity?
}
