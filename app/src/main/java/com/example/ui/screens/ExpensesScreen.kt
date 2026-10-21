package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwipeLeft
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseEntity
import com.example.ui.components.EditExpenseDialog
import com.example.ui.components.getCategoryVisual
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerHigh
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryContainer
import com.example.viewmodel.SpendlyUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpensesScreen(
    state: SpendlyUiState,
    onSearchChange: (String) -> Unit,
    onCategoryFilterChange: (String?) -> Unit,
    onTimeFilterChange: (String) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSaveExpense: (ExpenseEntity) -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }

    editingExpense?.let { expense ->
        EditExpenseDialog(
            expense = expense,
            onDismiss = { editingExpense = null },
            onSave = { onSaveExpense(it) },
            onDelete = { onDeleteExpense(it) }
        )
    }

    val filterChips = listOf(
        "All", "This Month", "Food & Dining", "Shopping", "Transport", "Income"
    )

    // Export CSV Helper
    fun exportTransactions() {
        val sb = StringBuilder("Title,Amount,Type,Category,PaymentMethod,Date\n")
        state.filteredExpenses.forEach { exp ->
            sb.append("\"${exp.title}\",${exp.amount},${exp.type},\"${exp.category}\",\"${exp.paymentMethod}\",\"${Date(exp.dateMillis)}\"\n")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Spendly Expense Export - ${state.currentMonthLabel}")
            putExtra(Intent.EXTRA_TEXT, sb.toString())
        }
        context.startActivity(Intent.createChooser(intent, "Export Transactions"))
    }

    // Group expenses by date group: "Today", "Yesterday", or "MMM dd, yyyy"
    val dayFormat = remember { SimpleDateFormat("yyyyMMdd", Locale.US) }
    val todayKey = remember { dayFormat.format(Date()) }
    val yesterdayKey = remember {
        dayFormat.format(Date(System.currentTimeMillis() - 86400000L))
    }
    val readableDayFormat = remember { SimpleDateFormat("MMM d, yyyy", Locale.US) }

    val grouped = state.filteredExpenses.groupBy { expense ->
        val key = dayFormat.format(Date(expense.dateMillis))
        when (key) {
            todayKey -> "Today — " + SimpleDateFormat("MMM d", Locale.US).format(Date(expense.dateMillis))
            yesterdayKey -> "Yesterday — " + SimpleDateFormat("MMM d", Locale.US).format(Date(expense.dateMillis))
            else -> readableDayFormat.format(Date(expense.dateMillis))
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. Pill Search Bar
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = SurfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BasicTextField(
                        value = state.searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("expense_search_input"),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = OnSurface,
                            fontSize = 14.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (state.searchQuery.isEmpty()) {
                                Text(
                                    text = "Search transactions, stores, notes...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = OnSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    )

                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = OnSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Horizontal Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterChips.forEach { chip ->
                    val isSelected = when (chip) {
                        "All" -> state.selectedCategoryFilter == null && state.selectedTimeFilter == "All"
                        "This Month" -> state.selectedTimeFilter == "This Month"
                        else -> state.selectedCategoryFilter == chip
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = if (isSelected) Primary else SurfaceContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable {
                                if (chip == "All") {
                                    onCategoryFilterChange(null)
                                    onTimeFilterChange("All")
                                } else if (chip == "This Month") {
                                    onTimeFilterChange(if (state.selectedTimeFilter == "This Month") "All" else "This Month")
                                } else {
                                    onCategoryFilterChange(if (state.selectedCategoryFilter == chip) null else chip)
                                }
                            }
                            .testTag("filter_chip_$chip")
                    ) {
                        Text(
                            text = chip,
                            color = if (isSelected) Color.White else OnSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                // Filter icon chip
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = SurfaceContainerHigh,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onCategoryFilterChange(null); onTimeFilterChange("All") }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filters",
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Reset",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = OnSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }
        }

        // 3. Monthly Overview Summary Strip & Month Switcher
        item {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SurfaceContainerLowest,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Month navigation controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainer)
                                    .clickable { onPreviousMonth() }
                                    .testTag("prev_month_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Previous Month",
                                    tint = OnSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = state.currentMonthLabel,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface,
                                    fontSize = 17.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceContainer)
                                    .clickable { onNextMonth() }
                                    .testTag("next_month_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next Month",
                                    tint = OnSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Quick Export Button
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceContainer,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { exportTransactions() }
                                .testTag("export_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = "Export",
                                    tint = OnSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Export",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = OnSurface,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "MONTHLY SPEND",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", state.monthlySpent),
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SecondaryContainer.copy(alpha = 0.35f)
                                ) {
                                    Text(
                                        text = "-4.2%",
                                        color = Secondary,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${state.filteredExpenses.size} total transactions",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 12.sp
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Mini cadence sparkline bars
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Box(modifier = Modifier.width(8.dp).height(18.dp).clip(RoundedCornerShape(4.dp)).background(SurfaceContainer))
                            Box(modifier = Modifier.width(8.dp).height(28.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFD3E4FE)))
                            Box(modifier = Modifier.width(8.dp).height(36.dp).clip(RoundedCornerShape(4.dp)).background(Primary))
                            Box(modifier = Modifier.width(8.dp).height(22.dp).clip(RoundedCornerShape(4.dp)).background(SecondaryContainer))
                        }
                    }
                }
            }
        }

        // 4. Grouped Transaction History
        if (grouped.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SurfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Empty",
                            tint = Secondary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No expenses found",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = OnSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "Try adjusting your search or filter criteria",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        } else {
            grouped.forEach { (dateGroup, itemsInGroup) ->
                val groupTotal = itemsInGroup.sumOf {
                    if (it.type == "income") -it.amount else it.amount
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp, end = 4.dp, top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateGroup,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnSurface,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = if (groupTotal >= 0) String.format(Locale.US, "-$%,.2f", groupTotal)
                            else String.format(Locale.US, "+$%,.2f", -groupTotal),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = OnSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                items(itemsInGroup, key = { it.id }) { expense ->
                    val visual = getCategoryVisual(expense.category, expense.type)
                    val isIncome = expense.type == "income"

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = SurfaceContainerLowest,
                        shadowElevation = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { editingExpense = expense }
                            .testTag("expense_row_${expense.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(visual.containerColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = visual.icon,
                                        contentDescription = expense.category,
                                        tint = visual.iconColor,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = expense.title,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = OnSurface,
                                            fontSize = 15.sp
                                        ),
                                        maxLines = 1
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = expense.paymentMethod,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = OnSurfaceVariant,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Text(
                                            text = "•",
                                            color = OnSurfaceVariant,
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = expense.category,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = OnSurfaceVariant,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = if (isIncome) String.format(Locale.US, "+$%,.2f", expense.amount)
                                    else String.format(Locale.US, "-$%,.2f", expense.amount),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isIncome) Secondary else Color(0xFF40000D),
                                        fontSize = 16.sp
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.SwipeLeft,
                                    contentDescription = "Manage",
                                    tint = OnSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Footer note
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "All caught up for ${state.currentMonthLabel}!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = OnSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
