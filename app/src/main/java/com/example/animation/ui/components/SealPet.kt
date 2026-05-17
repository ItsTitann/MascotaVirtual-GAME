package com.example.animation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animation.R
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SealPet(
    isSleeping: Boolean,
    energy: Int,
    funLevel: Int = 100,
    health: Int = 100,
    hygiene: Int = 100,
    isMouthOpen: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // ... frames anteriores ...
    val awakeFrames = listOf(
        R.drawable.seal_open,
        R.drawable.seal_half,
        R.drawable.seal_closed,
        R.drawable.seal_half,
        R.drawable.seal_open
    )

    val sleepFrames = listOf(
        R.drawable.seal_sleep_1,
        R.drawable.seal_sleep_2
    )

    val zeroEnergyFrames = listOf(
        R.drawable.seal_tired,
        R.drawable.seal_yawn_1,
        R.drawable.seal_yawn_2,
        R.drawable.seal_yawn_1,
        R.drawable.seal_tired
    )

    val sadFrames = listOf(
        R.drawable.seal_tired,
        R.drawable.seal_closed
    )

    var frameIndex by remember { mutableIntStateOf(0) }

    // Reiniciar frameIndex cuando cambie el estado
    LaunchedEffect(isSleeping, energy, funLevel, health, isMouthOpen) { 
        frameIndex = 0 
    }

    LaunchedEffect(isSleeping, energy, funLevel, health, isMouthOpen) {
        if (isSleeping) {
            // Animación de sueño PRIORITARIA: seal_sleep_1 y seal_sleep_2
            while (true) {
                frameIndex = 0; delay(600)
                frameIndex = 1; delay(600)
            }
        }

        if (isMouthOpen || health <= 0) return@LaunchedEffect

        if (energy == 0) {
            while (true) {
                frameIndex = 0; delay(1000)
                frameIndex = 1; delay(1000)
            }
        } else {
            while (true) {
                delay(Random.nextLong(2000, 4000))
                frameIndex = 1; delay(80)
                frameIndex = 2; delay(120)
                frameIndex = 3; delay(80)
                frameIndex = 4; delay(50)
                frameIndex = 0
            }
        }
    }

    val currentRes = when {
        isSleeping -> sleepFrames[frameIndex % sleepFrames.size]
        isMouthOpen -> R.drawable.seal_mouth_open
        health <= 0 -> R.drawable.seal_tired
        energy == 0 -> zeroEnergyFrames[frameIndex % zeroEnergyFrames.size]
        funLevel < 15 -> sadFrames[frameIndex % sadFrames.size]
        else -> awakeFrames[frameIndex % awakeFrames.size]
    }

    // Efecto visual de suciedad (Filtro verdoso/café si higiene < 20)
    val colorFilter = if (hygiene < 20) {
        ColorFilter.colorMatrix(ColorMatrix().apply {
            setToScale(0.8f, 0.9f, 0.7f, 1f) // Tinte sucio
        })
    } else null

    Image(
        painter = painterResource(currentRes),
        contentDescription = "Mascota",
        modifier = modifier,
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center,
        colorFilter = colorFilter
    )
}
