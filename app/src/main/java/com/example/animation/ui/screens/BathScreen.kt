package com.example.animation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animation.R
import com.example.animation.data.model.PetData
import com.example.animation.ui.components.SealPet
import com.example.animation.ui.components.TopStatusBar

@Composable
fun BathScreen(
    petData: PetData
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo del baño
        Image(
            painter = painterResource(id = R.drawable.pantalla_bano), 
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(450.dp)
        ) {
            SealPet(
                isSleeping = petData.sleeping,
                energy = petData.energy,
                funLevel = petData.funLevel,
                health = petData.health,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(600.dp)
                    .padding(bottom = 10.dp)
            )
        }

        // Header y Barra de estados reutilizable
        TopStatusBar(petData = petData)
    }
}
