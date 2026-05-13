package com.example.animation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animation.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import com.example.animation.ui.components.GameButton
import com.example.animation.ui.components.SealPet

@Composable
fun StartScreen(
    onPlayClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.pantalla_inicio),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1.4f)) // Empuja hacia abajo para centrar

            SealPet(
                isSleeping = false,
                energy = 100,
                modifier = Modifier.size(380.dp)
            )

            Spacer(modifier = Modifier.weight(0.8f)) // Espacio entre foca y botones

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameButton(
                    text = "¡JUGAR!",
                    onClick = onPlayClick,
                    mainColor = Color(0xFF4CAF50),      // Verde fuerte
                    secondaryColor = Color(0xFF8BC34A), // Verde claro
                    borderColor = Color(0xFFFFD700),    // Dorado/Amarillo
                    icon = Icons.Default.Star,
                    iconColor = Color(0xFFFFD700),
                    iconBgColor = Color(0xFFFFF176)
                )

                GameButton(
                    text = "CONTINUAR",
                    onClick = onContinueClick,
                    mainColor = Color(0xFFFB8C00),      // Naranja fuerte
                    secondaryColor = Color(0xFFFFB74D), // Naranja claro
                    borderColor = Color(0xFF0288D1),    // Azul
                    icon = Icons.Default.Refresh,
                    iconColor = Color.White,
                    iconBgColor = Color(0xFF29B6F6)
                )
            }
        }
    }
}
