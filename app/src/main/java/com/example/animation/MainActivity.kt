package com.example.animation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@Composable
fun MainScreen() {

    var isNight by remember { mutableStateOf(false) }

    val backgroundRes = if (isNight)
        R.drawable.background_night
    else
        R.drawable.background_day

    // Transición suave del overlay
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isNight) 0.45f else 0f,
        animationSpec = tween(900),
        label = "overlayAlpha"
    )

    // Pequeña animación de botón
    val buttonScale by animateFloatAsState(
        targetValue = if (isNight) 1.05f else 1f,
        animationSpec = tween(250),
        label = "buttonScale"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // FONDO
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = "Fondo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // OVERLAY OSCURO SUAVE
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
        )

        // MASCOTA
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            SealBlinkingPet(
                modifier = Modifier
                    .size(380.dp)
                    .padding(bottom = 90.dp)
            )
        }

        // BOTÓN DORMIR / DESPERTAR
        Button(
            onClick = { isNight = !isNight },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .scale(buttonScale),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isNight)
                    Color(0xFF2C2F4A)
                else
                    Color(0xFF6FB7FF)
            )
        ) {
            Text(
                text = if (isNight)
                    "Despertar ☀️"
                else
                    "Dormir 🌙"
            )
        }
    }
}

@Composable
fun SealBlinkingPet(modifier: Modifier = Modifier) {

    val frames = listOf(
        R.drawable.seal_open,
        R.drawable.seal_half,
        R.drawable.seal_closed,
        R.drawable.seal_half,
        R.drawable.seal_open
    )

    var frameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(2000, 4000))
            frameIndex = 1; delay(80)
            frameIndex = 2; delay(120)
            frameIndex = 3; delay(80)
            frameIndex = 4; delay(50)
            frameIndex = 0
        }
    }

    Image(
        painter = painterResource(frames[frameIndex]),
        contentDescription = "Mascota",
        modifier = modifier,
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center
    )
}