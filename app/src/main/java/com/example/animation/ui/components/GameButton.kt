package com.example.animation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameButton(
    text: String,
    onClick: () -> Unit,
    mainColor: Color,
    secondaryColor: Color,
    borderColor: Color,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.TopEnd
    ) {
        // Cuerpo del botón
        Box(
            modifier = Modifier
                .padding(top = 8.dp, end = 8.dp) // Espacio para el icono que sobresale
                .shadow(4.dp, RoundedCornerShape(50.dp))
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(secondaryColor, mainColor)
                    )
                )
                .border(3.dp, borderColor, RoundedCornerShape(50.dp))
                .padding(horizontal = 32.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        // Icono circular decorativo
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(iconBgColor)
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
