package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.SpendlyBottomNav
import com.example.ui.components.SpendlyTab
import com.example.ui.components.SpendlyTopBar
import com.example.ui.screens.AddExpenseScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExpensesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Surface
import com.example.viewmodel.SpendlyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SpendlyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SpendlyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SpendlyApp(viewModel: SpendlyViewModel) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(SpendlyTab.DASHBOARD) }

    // If user is on any secondary tab, pressing Back returns to Dashboard
    if (currentTab != SpendlyTab.DASHBOARD) {
        BackHandler {
            currentTab = SpendlyTab.DASHBOARD
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            if (currentTab != SpendlyTab.ADD) {
                val subtitle = when (currentTab) {
                    SpendlyTab.DASHBOARD -> "Dashboard"
                    SpendlyTab.EXPENSES -> "Expense List"
                    SpendlyTab.ANALYTICS -> "Analytics Insights"
                    SpendlyTab.BUDGETS -> "Budgets & Limits"
                    SpendlyTab.ADD -> "Log Expense"
                }
                SpendlyTopBar(
                    subtitle = subtitle,
                    onNotificationClick = {
                        Toast.makeText(context, "All alerts up to date! Budget is healthy.", Toast.LENGTH_SHORT).show()
                    },
                    onProfileClick = {
                        Toast.makeText(context, "Signed in as Sarah • Local Database Active", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        },
        bottomBar = {
            if (currentTab != SpendlyTab.ADD) {
                SpendlyBottomNav(
                    currentTab = currentTab,
                    onTabSelected = { tab -> currentTab = tab }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetTab ->
                when (targetTab) {
                    SpendlyTab.DASHBOARD -> {
                        DashboardScreen(
                            state = state,
                            onNavigateTab = { tab -> currentTab = tab },
                            onSelectWeeklyDay = { dayIdx -> viewModel.selectWeeklyDay(dayIdx) },
                            onUpdateBudgetCap = { cap -> viewModel.setMonthlyBudget(cap) },
                            onSaveExpense = { exp -> viewModel.updateExpense(exp) },
                            onDeleteExpense = { exp -> viewModel.deleteExpense(exp) },
                            onQuickAddCash = { currentTab = SpendlyTab.ADD }
                        )
                    }

                    SpendlyTab.EXPENSES -> {
                        ExpensesScreen(
                            state = state,
                            onSearchChange = { query -> viewModel.updateSearchQuery(query) },
                            onCategoryFilterChange = { cat -> viewModel.setCategoryFilter(cat) },
                            onTimeFilterChange = { filter -> viewModel.setTimeFilter(filter) },
                            onPreviousMonth = { viewModel.previousMonth() },
                            onNextMonth = { viewModel.nextMonth() },
                            onSaveExpense = { exp -> viewModel.updateExpense(exp) },
                            onDeleteExpense = { exp -> viewModel.deleteExpense(exp) }
                        )
                    }

                    SpendlyTab.ADD -> {
                        AddExpenseScreen(
                            onBack = { currentTab = SpendlyTab.DASHBOARD },
                            onSaveSuccess = { title, amount, type, category, dateMillis, paymentMethod, note, isSplit, splitCount ->
                                viewModel.addExpense(
                                    title = title,
                                    amount = amount,
                                    type = type,
                                    category = category,
                                    dateMillis = dateMillis,
                                    paymentMethod = paymentMethod,
                                    note = note,
                                    isSplit = isSplit,
                                    splitCount = splitCount
                                )
                                Toast.makeText(context, "Saved successfully!", Toast.LENGTH_SHORT).show()
                                currentTab = SpendlyTab.EXPENSES
                            }
                        )
                    }

                    SpendlyTab.ANALYTICS -> {
                        AnalyticsScreen(
                            state = state,
                            onPeriodSelected = { period -> viewModel.setPeriod(period) }
                        )
                    }

                    SpendlyTab.BUDGETS -> {
                        BudgetsScreen(
                            state = state,
                            onUpdateBudgetCap = { cap -> viewModel.setMonthlyBudget(cap) },
                            onResetDefaults = { viewModel.resetToDefaults() }
                        )
                    }
                }
            }
        }
    }
}
