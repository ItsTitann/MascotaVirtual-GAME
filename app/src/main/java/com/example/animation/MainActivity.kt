package com.example.animation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * REQUISITOS EN drawable:
 * - background_day
 * - background_night
 * - seal_open, seal_half, seal_closed
 * - seal_sleep_1, seal_sleep_2
 * - seal_tired, seal_yawn_1, seal_yawn_2
 * - icon_energy_outline
 * - lamp_on
 * - lamp_off
 */

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainScreen() }
    }
}

@Composable
fun MainScreen() {
    var isNight by remember { mutableStateOf(false) }
    var energy by remember { mutableStateOf(0) } // 0..100

    // ✅ +1 cada 1s si duerme, -1 cada 5s si NO duerme (mantiene energía al alternar)
    LaunchedEffect(isNight) {
        if (isNight) {
            while (isNight) {
                delay(1000)
                if (energy < 100) energy += 1
            }
        } else {
            while (!isNight) {
                delay(5000)
                if (energy > 0) energy -= 1
            }
        }
    }

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

        EnergyIconFill(
            energy = energy,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        )

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // 🦭 FOCA (posición fija)
            SealPet(
                isSleeping = isNight,
                energy = energy,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(600.dp)
                    .padding(bottom = 100.dp) // 👈 controla la altura fija
            )

            // 💡 LÁMPARA (independiente debajo)
            Image(
                painter = painterResource(if (isNight) R.drawable.lamp_off else R.drawable.lamp_on),
                contentDescription = "Lámpara",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(190.dp) // 👈 ahora puedes subir este valor sin mover la foca
                    .scale(lampScale)
                    .clickable { isNight = !isNight }
                    .padding(bottom = 20.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun EnergyIconFill(
    energy: Int,
    modifier: Modifier = Modifier
) {
    val clamped = energy.coerceIn(0, 100)
    val fillFraction = clamped / 100f

    val fillColor = if (clamped == 0) Color(0xFFFF3B30) else Color(0xFF64FF2B)
    val ringColor = if (clamped == 0) Color(0xAAFF3B30) else Color(0xAA64FF2B)
    val baseTint = if (clamped == 0) Color(0x22FF3B30) else Color(0x2200FF00)

    Box(
        modifier = modifier.size(78.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val inset = size.minDimension * 0.12f
            val w = size.width - inset * 2
            val h = size.height - inset * 2

            drawCircle(
                color = baseTint,
                radius = w.coerceAtMost(h) / 2f,
                center = Offset(size.width / 2f, size.height / 2f)
            )

            val fillH = h * fillFraction
            val topLeft = Offset(inset, inset + (h - fillH))

            val r = w.coerceAtMost(h) / 2f
            val cx = size.width / 2f
            val cy = size.height / 2f

            val circlePath = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        left = cx - r,
                        top = cy - r,
                        right = cx + r,
                        bottom = cy + r
                    )
                )
            }

            clipPath(circlePath) {
                drawRect(
                    color = fillColor,
                    topLeft = topLeft,
                    size = Size(w, fillH),
                    style = Fill
                )
            }

            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(w, h),
                style = Stroke(width = size.minDimension * 0.02f, cap = StrokeCap.Round)
            )
        }

        Image(
            painter = painterResource(R.drawable.icon_energy_outline),
            contentDescription = "Energía",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit
        )

        Text(
            text = clamped.toString(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun SealPet(
    isSleeping: Boolean,
    energy: Int,
    modifier: Modifier = Modifier
) {
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

    var frameIndex by remember { mutableStateOf(0) }

    LaunchedEffect(isSleeping, energy) { frameIndex = 0 }

    LaunchedEffect(isSleeping, energy) {
        if (energy == 0) {
            while (true) {
                frameIndex = 0; delay(700)
                frameIndex = 1; delay(250)
                frameIndex = 2; delay(450)
                frameIndex = 3; delay(250)
                frameIndex = 4; delay(650)
            }
        } else if (isSleeping) {
            while (true) {
                frameIndex = 0; delay(450)
                frameIndex = 1; delay(450)
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
        energy == 0 -> zeroEnergyFrames[frameIndex % zeroEnergyFrames.size]
        isSleeping -> sleepFrames[frameIndex % sleepFrames.size]
        else -> awakeFrames[frameIndex % awakeFrames.size]
    }

    Image(
        painter = painterResource(currentRes),
        contentDescription = "Mascota",
        modifier = modifier,
        contentScale = ContentScale.Fit,
        alignment = Alignment.Center
    )
}