package com.example.animation.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animation.R

@Composable
fun MenuGamesScreen(
    onBack: () -> Unit,
    onNavigateToFishingGame: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo del Menú de Juegos
        Image(
            painter = painterResource(id = R.drawable.pantalla_menu_juegos),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Botón de Minijuego Central
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(280.dp, 350.dp) // Proporciones de carta/mando
                .shadow(12.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .border(BorderStroke(6.dp, Color.White), RoundedCornerShape(24.dp))
                .clickable { onNavigateToFishingGame() }
        ) {
            Image(
                painter = painterResource(id = R.drawable.btn_juego_pesca),
                contentDescription = "Minijuego Pesca Equilibrada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Botón para regresar
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
