package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseEntity
import com.example.viewmodel.DayActivity
import com.example.ui.components.EditExpenseDialog
import com.example.ui.components.SetBudgetDialog
import com.example.ui.components.SpendlyTab
import com.example.ui.components.getCategoryVisual
import com.example.ui.theme.OnSurface
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary
import com.example.ui.theme.PrimaryContainer
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.Surface
import com.example.ui.theme.SurfaceContainer
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.SurfaceContainerLowest
import com.example.ui.theme.TertiaryContainer
import com.example.ui.theme.TertiaryFixedDim
import com.example.viewmodel.SpendlyUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    state: SpendlyUiState,
    onNavigateTab: (SpendlyTab) -> Unit,
    onSelectWeeklyDay: (Int) -> Unit,
    onUpdateBudgetCap: (Double) -> Unit,
    onSaveExpense: (ExpenseEntity) -> Unit,
    onDeleteExpense: (ExpenseEntity) -> Unit,
    onQuickAddCash: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var editingExpense by remember { mutableStateOf<ExpenseEntity?>(null) }
    var showBudgetDialog by remember { mutableStateOf(false) }

    editingExpense?.let { expense ->
        EditExpenseDialog(
            expense = expense,
            onDismiss = { editingExpense = null },
            onSave = { onSaveExpense(it) },
            onDelete = { onDeleteExpense(it) }
        )
    }

    if (showBudgetDialog) {
        SetBudgetDialog(
            currentBudget = state.monthlyBudgetCap,
            onDismiss = { showBudgetDialog = false },
            onSave = { onUpdateBudgetCap(it) }
        )
    }

    val dateFormat = remember { SimpleDateFormat("EEEE, MMM d", Locale.US) }
    val currentDateStr = remember { dateFormat.format(Date()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // 1. Greeting & Date Context
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = currentDateStr,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 13.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFC6C6CD))
                        )
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Synced",
                            tint = Secondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Synced",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = OnSurfaceVariant,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Text(
                        text = "Good morning, Sarah",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = OnSurface,
                            fontSize = 22.sp
                        ),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (state.isBudgetOnTrack) Color(0xFF6CF8BB).copy(alpha = 0.35f) else Color(0xFFFFDAD6)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Budget Status",
                            tint = if (state.isBudgetOnTrack) Color(0xFF005236) else Color(0xFF93000A),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (state.isBudgetOnTrack) "ON TRACK" else "AT RISK",
                            color = if (state.isBudgetOnTrack) Color(0xFF005236) else Color(0xFF93000A),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }

        // 2. Main Balance & Financial Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(PrimaryContainer)
                    .testTag("net_balance_card")
            ) {
                // Subtle Ambient Gradient Accents
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Secondary.copy(alpha = 0.25f), Color.Transparent),
                                radius = 400f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    // Balance Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL NET BALANCE",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF7C839B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Secondary.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = SecondaryFixed,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "+4.2% this month",
                                    color = SecondaryFixed,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Huge Display Currency
                    Text(
                        text = String.format(Locale.US, "$%,.2f", state.totalNetBalance),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Monthly Spend Allowance Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Monthly Spend Allowance",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF7C839B),
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = String.format(Locale.US, "%.1f%% used", state.budgetUsagePercent),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Gauge Bar
                    val animatedProgress by animateFloatAsState(
                        targetValue = (state.budgetUsagePercent / 100f).coerceIn(0f, 1f),
                        label = "progress"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (state.budgetUsagePercent > 90f) TertiaryFixedDim
                                    else SecondaryContainer
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Metrics Split (Monthly Spent & Remaining)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Monthly Spent
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(TertiaryFixedDim)
                                    )
                                    Text(
                                        text = "Monthly Spent",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF7C839B),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", state.monthlySpent),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                )
                                Text(
                                    text = "+$180 vs last wk",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = TertiaryFixedDim,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Remaining Budget (Tap to edit budget cap!)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .clickable { showBudgetDialog = true }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(SecondaryFixed)
                                    )
                                    Text(
                                        text = "Remaining",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF7C839B),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.US, "$%,.2f", state.remainingBudget),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        color = SecondaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    )
                                )
                                Text(
                                    text = "of $${state.monthlyBudgetCap.toInt()} cap ✎",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF7C839B),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Action Shortcuts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionItem(
                    label = "Send",
                    icon = Icons.Default.Send,
                    onClick = {
                        Toast.makeText(context, "Quick Transfer / Send payment ready", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    label = "Add Cash",
                    icon = Icons.Default.AddCard,
                    onClick = onQuickAddCash,
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    label = "Receipt",
                    icon = Icons.Default.DocumentScanner,
                    onClick = {
                        Toast.makeText(context, "Attach receipt enabled in Log Expense", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickActionItem(
                    label = "Split",
                    icon = Icons.Default.CallSplit,
                    onClick = {
                        Toast.makeText(context, "Split with others ready in Add Expense", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Weekly Spending Interactive Chart Card
        item {
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = SurfaceContainerLowest,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "WEEKLY ACTIVITY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Text(
                                text = String.format(Locale.US, "$%,.2f Total", state.weeklyTotal),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OnSurface,
                                    fontSize = 18.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceContainerLow
                        ) {
                            Text(
                                text = String.format(Locale.US, "Avg $%.2f / day", state.weeklyAveragePerDay),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = OnSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 7 Interactive Bars
                    val maxAmount = state.weeklyDays.maxOfOrNull { it.amount }?.coerceAtLeast(10.0) ?: 210.0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        state.weeklyDays.forEachIndexed { index, day ->
                            val isSelected = index == state.selectedWeeklyDayIndex
                            val barHeightFraction = (day.amount / maxAmount).toFloat().coerceIn(0.15f, 1f)

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onSelectWeeklyDay(index) },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                // Floating Tooltip for Selected Bar
                                if (isSelected) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = Primary,
                                        shadowElevation = 4.dp
                                    ) {
                                        Text(
                                            text = String.format(Locale.US, "$%.0f", day.amount),
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // Bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height((100 * barHeightFraction).dp)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            if (isSelected) Primary
                                            else SurfaceContainer
                                        )
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Day Label
                                Text(
                                    text = day.dayLabel,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Primary else OnSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Category Footnote
                    val selectedDay = state.weeklyDays.getOrNull(state.selectedWeeklyDayIndex)
                    if (selectedDay != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SurfaceContainerLow,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalMall,
                                        contentDescription = null,
                                        tint = Secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "${selectedDay.fullDayName} Activity: ${selectedDay.dominantCategory}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = OnSurface,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                                Text(
                                    text = String.format(Locale.US, "$%.2f", selectedDay.amount),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OnSurface,
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Recent Expenses List Preview
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Expenses",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OnSurface,
                        fontSize = 18.sp
                    )
                )

                Row(
                    modifier = Modifier
                        .clickable { onNavigateTab(SpendlyTab.EXPENSES) }
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "See All",
                        tint = Secondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Recent items (Top 4)
        val recentPreview = state.expenses.take(4)
        items(recentPreview, key = { it.id }) { expense ->
            val visual = getCategoryVisual(expense.category, expense.type)
            val isIncome = expense.type == "income"
            val timeFormat = SimpleDateFormat("h:mm a", Locale.US)
            val dateSub = SimpleDateFormat("MMM d", Locale.US).format(Date(expense.dateMillis))

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = SurfaceContainerLowest,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { editingExpense = expense }
                    .testTag("expense_item_${expense.id}")
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
                                    text = "$dateSub • ${timeFormat.format(Date(expense.dateMillis))}",
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
                                    text = expense.paymentMethod,
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
                                color = if (isIncome) Secondary else OnSurface,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = expense.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isIncome) Secondary else OnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Space for floating bottom nav
        }
    }
}

@Composable
private fun QuickActionItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = SurfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = OnSurface,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = OnSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            )
        }
    }
}
