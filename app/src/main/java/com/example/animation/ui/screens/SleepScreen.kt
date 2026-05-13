package com.example.animation.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animation.R
import com.example.animation.data.model.PetData
import com.example.animation.data.repository.PetRepository
import com.example.animation.ui.components.SealPet
import com.example.animation.ui.components.TopStatusBar
import kotlinx.coroutines.delay

@Composable
fun SleepScreen(
    petData: PetData,
    petRepository: PetRepository,
    onEnergyChange: (Int) -> Unit
) {
    // Usamos un estado local para que la respuesta visual sea instantánea al pulsar
    var localIsSleeping by remember { mutableStateOf(petData.sleeping) }
    
    // Sincronizamos el estado local cuando petData cambie (desde Firebase)
    LaunchedEffect(petData.sleeping) {
        localIsSleeping = petData.sleeping
    }

    val isNight = localIsSleeping

    val backgroundRes = if (isNight) R.drawable.background_night else R.drawable.background_day

    val overlayAlpha by animateFloatAsState(
        targetValue = if (isNight) 0.45f else 0f,
        animationSpec = tween(900),
        label = "overlayAlpha"
    )

    val lampScale by animateFloatAsState(
        targetValue = if (isNight) 1.02f else 1f,
        animationSpec = tween(180),
        label = "lampScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SealPet(
                isSleeping = isNight,
                energy = petData.energy,
                health = petData.health,
                funLevel = petData.funLevel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(600.dp)
                    .padding(bottom = 60.dp)
            )

            // Lámpara interactiva envuelta en un Box para asegurar el área táctil
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .size(190.dp)
                    .clickable {
                        val newState = !localIsSleeping
                        localIsSleeping = newState // Cambio visual inmediato
                        petRepository.updatePet(petData.id, mapOf("sleeping" to newState))
                    },
                contentAlignment = Alignment.Center
            ) {
                val lampResource = if (isNight) R.drawable.lamp_off else R.drawable.lamp_on
                Image(
                    painter = painterResource(id = lampResource),
                    contentDescription = "Interruptor Lámpara",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(lampScale),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Header y Barra de estados reutilizable
        TopStatusBar(petData = petData)
    }
}
