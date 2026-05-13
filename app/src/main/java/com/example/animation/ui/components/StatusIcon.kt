package com.example.animation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusIcon(
    iconRes: Int,
    value: Int,
    fillColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .height(22.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.6f))
                .border(1.5.dp, Color.Black.copy(alpha = 0.3f), RoundedCornerShape(50)),
            contentAlignment = Alignment.CenterStart
        ) {
            // Barra de progreso (relleno)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value / 100f)
                    .background(fillColor)
            )
            
            // Icono que sobresale un poco a la izquierda
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .offset(x = (-4).dp),
                contentScale = ContentScale.Fit
            )
        }
        
        // Texto de porcentaje
        Text(
            text = "$value%",
            color = Color.Black,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
