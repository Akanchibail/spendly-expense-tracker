package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OnSurfaceVariant
import com.example.ui.theme.Primary

enum class SpendlyTab {
    DASHBOARD,
    EXPENSES,
    ADD,
    ANALYTICS,
    BUDGETS
}

@Composable
fun SpendlyBottomNav(
    currentTab: SpendlyTab,
    onTabSelected: (SpendlyTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Dock
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(34.dp),
                    ambientColor = Color(0x1F0B1C30),
                    spotColor = Color(0x2E0B1C30)
                ),
            shape = RoundedCornerShape(34.dp),
            color = Color.White.copy(alpha = 0.96f),
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Home
                NavTabItem(
                    label = "Home",
                    icon = Icons.Default.Dashboard,
                    isSelected = currentTab == SpendlyTab.DASHBOARD,
                    testTag = "nav_home",
                    onClick = { onTabSelected(SpendlyTab.DASHBOARD) },
                    modifier = Modifier.weight(1f)
                )

                // Expenses
                NavTabItem(
                    label = "Expenses",
                    icon = Icons.Default.ReceiptLong,
                    isSelected = currentTab == SpendlyTab.EXPENSES,
                    testTag = "nav_expenses",
                    onClick = { onTabSelected(SpendlyTab.EXPENSES) },
                    modifier = Modifier.weight(1f)
                )

                // Center Spacer for elevated (+) button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(68.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Elevated Center (+) Button
                    Box(
                        modifier = Modifier
                            .offset(y = (-14).dp)
                            .size(52.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = CircleShape,
                                ambientColor = Color(0x40000000),
                                spotColor = Color(0x50000000)
                            )
                            .clip(CircleShape)
                            .background(Primary)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onTabSelected(SpendlyTab.ADD)
                            }
                            .testTag("nav_add_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log Expense",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Analytics
                NavTabItem(
                    label = "Analytics",
                    icon = Icons.Default.InsertChart,
                    isSelected = currentTab == SpendlyTab.ANALYTICS,
                    testTag = "nav_analytics",
                    onClick = { onTabSelected(SpendlyTab.ANALYTICS) },
                    modifier = Modifier.weight(1f)
                )

                // Budgets
                NavTabItem(
                    label = "Budgets",
                    icon = Icons.Default.AccountBalanceWallet,
                    isSelected = currentTab == SpendlyTab.BUDGETS,
                    testTag = "nav_budgets",
                    onClick = { onTabSelected(SpendlyTab.BUDGETS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (isSelected) Primary else OnSurfaceVariant
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = fontWeight
            ),
            maxLines = 1
        )
    }
}
