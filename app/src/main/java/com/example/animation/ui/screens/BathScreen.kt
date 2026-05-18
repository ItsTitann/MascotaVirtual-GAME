package com.example.animation.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.animation.R
import com.example.animation.data.model.PetData
import com.example.animation.data.repository.PetRepository
import com.example.animation.ui.components.SealPet
import com.example.animation.ui.components.TopStatusBar
import com.example.animation.ui.utils.MusicManager
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun BathScreen(
    petData: PetData,
    petRepository: PetRepository,
    onSettingsClick: () -> Unit
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    
    // --- ESTADOS ---
    var soapOffset by remember { mutableStateOf(Offset.Zero) }
    var showerOffset by remember { mutableStateOf(Offset.Zero) }
    var isShowerOn by remember { mutableStateOf(false) }
    
    // Gestor de sonido para la regadera
    val showerMediaPlayer = remember {
        MediaPlayer.create(context, R.raw.sfx_shower).apply {
            isLooping = true
            setVolume(MusicManager.sfxVolume, MusicManager.sfxVolume)
        }
    }

    // Actualizar volumen de la regadera si cambia el sfxVolume global
    LaunchedEffect(MusicManager.sfxVolume) {
        showerMediaPlayer.setVolume(MusicManager.sfxVolume, MusicManager.sfxVolume)
    }

    // Limpiar el reproductor al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            showerMediaPlayer.stop()
            showerMediaPlayer.release()
        }
    }

    // Controlar el inicio/fin del sonido basado en isShowerOn
    LaunchedEffect(isShowerOn) {
        if (isShowerOn) {
            showerMediaPlayer.start()
        } else {
            if (showerMediaPlayer.isPlaying) {
                showerMediaPlayer.pause()
                showerMediaPlayer.seekTo(0) // Reiniciar para la próxima vez
            }
        }
    }
    
    val foamParticles = remember { mutableStateListOf<Offset>() }
    val waterDrops = remember { mutableStateListOf<WaterDrop>() }

    // Convertimos las unidades de calibración de DP a Píxeles para que sean iguales en todos los teléfonos
    val xOffsetPx = with(density) { (-100).dp.toPx() } // Ajuste horizontal (-200f aprox)
    val yStartPx = with(density) { 550.dp.toPx() }     // Origen del agua (1560f aprox)
    val yCollisionPx = with(density) { 650.dp.toPx() } // Ajuste colisión (1810f aprox)

    // --- LÓGICA DE ACTUALIZACIÓN (LOOP) ---
    LaunchedEffect(isShowerOn, showerOffset) {
        if (isShowerOn) {
            while (isShowerOn) {
                // 1. Generar gotas basadas en píxeles reales del dispositivo
                repeat(6) {
                    waterDrops.add(WaterDrop(
                        x = showerOffset.x + xOffsetPx + Random.nextFloat() * 40f - 20f,
                        y = showerOffset.y + yStartPx, 
                        speed = Random.nextFloat() * 20f + 15f
                    ))
                }
                
                // 2. Mover gotas y detectar colisiones
                val iterator = waterDrops.iterator()
                while (iterator.hasNext()) {
                    val drop = iterator.next()
                    drop.y += drop.speed
                    
                    if (drop.y > 2500f) { 
                        iterator.remove()
                    } else {
                        val foamIterator = foamParticles.iterator()
                        var foamRemoved = false
                        while (foamIterator.hasNext()) {
                            val foamPos = foamIterator.next()
                            
                            val dx = (drop.x - xOffsetPx) - foamPos.x
                            val dy = (drop.y - yCollisionPx) - foamPos.y
                            
                            if (dx * dx + dy * dy < 4000f) { 
                                foamIterator.remove()
                                foamRemoved = true
                                break
                            }
                        }
                        
                        if (foamRemoved && petData.hygiene < 100) {
                            petRepository.updatePet(petData.id, mapOf("hygiene" to (petData.hygiene + 1).coerceAtMost(100)))
                        }
                    }
                }
                delay(16)
            }
        } else {
            waterDrops.clear()
        }
    }

    // --- INTERFAZ DE USUARIO ---
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Fondo
        Image(
            painter = painterResource(id = R.drawable.pantalla_bano),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Contenedor de la Mascota y Espuma
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
                hygiene = petData.hygiene,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(600.dp)
                    .padding(bottom = 10.dp)
            )

            // Espuma encima de la mascota
            Canvas(modifier = Modifier.fillMaxSize()) {
                foamParticles.forEach { pos ->
                    drawFoam(pos)
                }
            }
        }

        // 3. Agua encima de todo (Capa Frontal)
        Canvas(modifier = Modifier.fillMaxSize()) {
            waterDrops.forEach { drop ->
                drawCircle(
                    color = Color(0xFF2196F3).copy(alpha = 0.9f),
                    radius = 8f,
                    center = Offset(size.width / 2 + drop.x, drop.y)
                )
            }
        }

        // 4. Objetos Interactivos
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // REGADERA
            Box(
                modifier = Modifier
                    .offset { IntOffset(showerOffset.x.roundToInt(), showerOffset.y.roundToInt()) }
                    .size(110.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { isShowerOn = true },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                showerOffset += dragAmount
                            },
                            onDragEnd = {
                                showerOffset = Offset.Zero
                                isShowerOn = false
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.item_regadera),
                    contentDescription = "Regadera",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // JABÓN
            Box(
                modifier = Modifier
                    .offset { IntOffset(soapOffset.x.roundToInt(), soapOffset.y.roundToInt()) }
                    .size(110.dp)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                soapOffset += dragAmount
                                
                                // Si el jabón está sobre la foca (Zona de colisión)
                                if (soapOffset.y < -50 && soapOffset.y > -400 && soapOffset.x in -180f..180f) {
                                    if (foamParticles.size < 70) {
                                        foamParticles.add(Offset(
                                            x = soapOffset.x + Random.nextFloat() * 60f - 30f,
                                            y = soapOffset.y + Random.nextFloat() * 60f - 30f
                                        ))
                                    }
                                }
                            },
                            onDragEnd = { soapOffset = Offset.Zero }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.item_jabon),
                    contentDescription = "Jabón",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 5. Barra de Estado Superior
        TopStatusBar(petData = petData, onSettingsClick = onSettingsClick)
    }
}

// Función para dibujar espuma suave estilo "Pou"
fun DrawScope.drawFoam(offset: Offset) {
    val centerX = size.width / 2 + offset.x
    val centerY = size.height / 2 + offset.y + 110f
    
    // Círculo principal
    drawCircle(color = Color.White, radius = 30f, center = Offset(centerX, centerY))
    // Brillo/Sombra suave
    drawCircle(color = Color.White.copy(alpha = 0.4f), radius = 42f, center = Offset(centerX, centerY))
}

class WaterDrop(var x: Float, var y: Float, val speed: Float)
