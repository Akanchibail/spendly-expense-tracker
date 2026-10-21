package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.Secondary
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.Tertiary
import com.example.ui.theme.TertiaryContainer

data class CategoryVisual(
    val icon: ImageVector,
    val containerColor: Color,
    val iconColor: Color
)

fun getCategoryVisual(category: String, type: String = "expense"): CategoryVisual {
    if (type == "income") {
        return CategoryVisual(
            icon = Icons.Default.Payments,
            containerColor = Color(0xFF6CF8BB).copy(alpha = 0.45f),
            iconColor = Secondary
        )
    }

    return when (category.lowercase()) {
        "food & dining", "food", "cafe", "dining" -> CategoryVisual(
            icon = Icons.Default.Restaurant,
            containerColor = Color(0xFF6CF8BB).copy(alpha = 0.35f),
            iconColor = Secondary
        )
        "groceries", "supermarket" -> CategoryVisual(
            icon = Icons.Default.ShoppingCart,
            containerColor = Color(0xFFD3E4FE),
            iconColor = Color(0xFF0F172A)
        )
        "transport", "transportation", "transit" -> CategoryVisual(
            icon = Icons.Default.DirectionsCar,
            containerColor = Color(0xFFE5EEFF),
            iconColor = Color(0xFF3F465C)
        )
        "shopping", "shopping & lifestyle" -> CategoryVisual(
            icon = Icons.Default.ShoppingBag,
            containerColor = Color(0xFFFFDAD6).copy(alpha = 0.6f),
            iconColor = Tertiary
        )
        "bills & utilities", "housing & utilities", "utilities", "housing" -> CategoryVisual(
            icon = Icons.Default.Home,
            containerColor = Color(0xFFD3E4FE),
            iconColor = Color(0xFF3F465C)
        )
        "entertainment" -> CategoryVisual(
            icon = Icons.Default.Movie,
            containerColor = Color(0xFF6CF8BB).copy(alpha = 0.3f),
            iconColor = Color(0xFF00714D)
        )
        "health", "fitness", "wellness" -> CategoryVisual(
            icon = Icons.Default.FitnessCenter,
            containerColor = Color(0xFFE5EEFF),
            iconColor = Color(0xFF565E74)
        )
        "income" -> CategoryVisual(
            icon = Icons.Default.Payments,
            containerColor = Color(0xFF6CF8BB).copy(alpha = 0.45f),
            iconColor = Secondary
        )
        else -> CategoryVisual(
            icon = Icons.Default.MoreHoriz,
            containerColor = Color(0xFFE5EEFF),
            iconColor = Color(0xFF76777D)
        )
    }
}
