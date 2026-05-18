package com.example.animation.ui.screens

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animation.R
import com.example.animation.data.model.PetData
import com.example.animation.data.repository.PetRepository
import com.example.animation.ui.utils.MusicManager
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random

enum class GameObjectType { FISH, JELLYFISH, ENERGY }
data class GameObject(
    val id: Long = Random.nextLong(),
    val type: GameObjectType,
    var x: Float,
    var y: Float,
    val speed: Float
)

@Composable
fun PescaEquilibradaScreen(
    petData: PetData,
    petRepository: PetRepository,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp.toFloat()
    val screenHeight = config.screenHeightDp.toFloat()

    // --- ESTADOS DEL JUEGO ---
    var focaX by remember { mutableStateOf(screenWidth / 2) }
    var gameActive by remember { mutableStateOf(true) }
    var showGameOver by remember { mutableStateOf(false) }
    
    // Stats de la partida
    var score by remember { mutableIntStateOf(0) }
    var energyRemaining by remember { mutableFloatStateOf(100f) }
    var startTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var totalTime by remember { mutableLongStateOf(0L) }
    
    val gameObjects = remember { mutableStateListOf<GameObject>() }
    var isHit by remember { mutableStateOf(false) }
    var difficultyMultiplier by remember { mutableFloatStateOf(1f) }

    // Forzar orientación LANDSCAPE y manejar música
    DisposableEffect(Unit) {
        val activity = context as? Activity
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        // Cambiar a música de pesca
        MusicManager.playMusic(context, R.raw.bgm_fishing)
        
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            // Volver a música general al salir
            MusicManager.playMusic(context, R.raw.bgm_main)
        }
    }

    // --- SENSOR DE INCLINACIÓN (Acelerómetro) ---
    DisposableEffect(gameActive) {
        if (!gameActive) return@DisposableEffect onDispose {}
        
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // En modo landscape, el eje Y del acelerómetro controla el movimiento lateral
                val tilt = event.values[1] 
                val sensitivity = 8f
                val newX = focaX + (tilt * sensitivity)
                focaX = newX.coerceIn(50f, screenWidth - 50f)
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        onDispose { sensorManager.unregisterListener(listener) }
    }

    // --- LOOP PRINCIPAL DEL JUEGO ---
    LaunchedEffect(gameActive) {
        if (!gameActive) return@LaunchedEffect
        
        while (gameActive) {
            delay(16) // ~60 FPS
            
            // 1. Disminuir energía/tiempo gradualmente
            energyRemaining -= 0.05f * difficultyMultiplier
            if (energyRemaining <= 0) {
                energyRemaining = 0f
                gameActive = false
                showGameOver = true
                totalTime = (System.currentTimeMillis() - startTime) / 1000
                
                // Actualizar Stats Globales al perder
                val updates = mutableMapOf<String, Any>()
                val newFun = (petData.funLevel + 15).coerceAtMost(100)
                updates["funLevel"] = newFun
                petRepository.updatePet(petData.id, updates)
                petRepository.addXp(petData, 8)
            }

            // 2. Aumentar dificultad
            difficultyMultiplier += 0.0005f

            // 3. Generar objetos
            if (Random.nextFloat() < 0.03f * difficultyMultiplier) {
                val type = when (Random.nextFloat()) {
                    in 0f..0.7f -> GameObjectType.FISH
                    in 0.7f..0.9f -> GameObjectType.JELLYFISH
                    else -> GameObjectType.ENERGY
                }
                gameObjects.add(GameObject(
                    type = type,
                    x = Random.nextFloat() * (screenWidth - 60f) + 30f,
                    y = -50f,
                    speed = (Random.nextFloat() * 2f + 1.5f) * difficultyMultiplier
                ))
            }

            // 4. Mover objetos y detectar colisiones
            val iterator = gameObjects.iterator()
            while (iterator.hasNext()) {
                val obj = iterator.next()
                obj.y += obj.speed

                // Detectar colisión con la foca
                // Foca está en focaX, base de la pantalla
                val distanceX = abs(obj.x - focaX)
                val distanceY = abs((screenHeight - 80f) - obj.y)

                // Umbral general de colisión
                var collisionThreshold = 65f
                
                // Si es una medusa, hacemos la caja de colisión más pequeña para ser más justos
                if (obj.type == GameObjectType.JELLYFISH) {
                    collisionThreshold = 45f 
                }

                if (distanceX < collisionThreshold && distanceY < collisionThreshold) {
                    when (obj.type) {
                        GameObjectType.FISH -> {
                            score++
                            energyRemaining = (energyRemaining + 2f).coerceAtMost(100f)
                            MusicManager.playSound(context, R.raw.sfx_eat)
                        }
                        GameObjectType.JELLYFISH -> {
                            energyRemaining -= 15f
                            isHit = true
                            MusicManager.playSound(context, R.raw.sfx_hit)
                        }
                        GameObjectType.ENERGY -> {
                            energyRemaining = (energyRemaining + 20f).coerceAtMost(100f)
                            MusicManager.playSound(context, R.raw.sfx_energy)
                        }
                    }
                    iterator.remove()
                } else if (obj.y > screenHeight + 50f) {
                    iterator.remove()
                }
            }
            
            if (isHit) {
                delay(200)
                isHit = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo
        Image(
            painter = painterResource(id = R.drawable.pantalla_juego_pesca),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // UI Superior: Score y Energía
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Marcador de Peces
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.item_pescado), null, Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("$score", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // Barra de Energía (Tiempo)
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(energyRemaining / 100f)
                        .background(if (energyRemaining > 30) Color.Green else Color.Red)
                )
            }
        }

        // --- OBJETOS CAYENDO ---
        gameObjects.forEach { obj ->
            Image(
                painter = painterResource(
                    id = when (obj.type) {
                        GameObjectType.FISH -> R.drawable.item_pescado
                        GameObjectType.JELLYFISH -> R.drawable.item_medusa
                        GameObjectType.ENERGY -> R.drawable.item_energia
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .offset(x = obj.x.dp, y = obj.y.dp)
                    .size(65.dp)
            )
        }

        // --- LA FOCA ---
        val focaRes = if (isHit) R.drawable.seal_hit else R.drawable.seal_open
        Image(
            painter = painterResource(id = focaRes),
            contentDescription = "Foca",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = focaX.dp - 60.dp, y = (-20).dp)
                .size(120.dp)
                .graphicsLayer {
                    // Pequeña inclinación visual según movimiento
                }
        )

        // Botón Salir (Solo si no está en Game Over)
        if (!showGameOver) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }

        // --- VENTANA DE GAME OVER ---
        if (showGameOver) {
            AlertDialog(
                onDismissRequest = {},
                confirmButton = {
                    Button(onClick = onBack) { Text("Volver al Menú") }
                },
                title = { Text("¡Fin de la partida!", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Tiempo sobrevivido: $totalTime s")
                        Text("Peces recolectados: $score")
                        Text("XP conseguida: +8")
                        Text("Diversión: +15%")
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}
