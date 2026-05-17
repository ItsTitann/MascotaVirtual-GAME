package com.example.animation.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animation.R
import com.example.animation.data.model.PetData
import com.example.animation.data.repository.PetRepository
import com.example.animation.ui.components.SealPet
import com.example.animation.ui.components.TopStatusBar

@Composable
fun FunScreen(
    petData: PetData,
    petRepository: PetRepository,
    onNavigateToMenuGames: () -> Unit
) {
    val context = LocalContext.current
    var isLaughing by remember { mutableStateOf(false) }
    
    // Sensor de movimiento para detectar agitación
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        
        val sensorEventListener = object : SensorEventListener {
            private var lastUpdate: Long = 0
            private var lastShakeTime: Long = 0
            private var lastX: Float = 0f
            private var lastY: Float = 0f
            private var lastZ: Float = 0f

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    val curTime = System.currentTimeMillis()
                    // Reaccionar cada 100ms
                    if ((curTime - lastUpdate) > 100) {
                        val diffTime = curTime - lastUpdate
                        lastUpdate = curTime

                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]

                        // Calculamos la diferencia de movimiento comparando con la posición anterior
                        // Esto elimina el efecto de la gravedad constante
                        val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                        lastX = x
                        lastY = y
                        lastZ = z

                        if (speed > 1200) { // Umbral aumentado para mayor precisión
                            isLaughing = true
                            lastShakeTime = curTime
                            
                            // Aumentar diversión cada vez que se detecta un "shake" fuerte
                            // Limitamos la frecuencia de actualización a Firebase para no saturar
                            if (curTime % 1000 < 100) { // Aproximadamente cada segundo de agitación
                                val newFun = (petData.funLevel + 5).coerceAtMost(100)
                                if (newFun != petData.funLevel) {
                                    petRepository.updatePet(petData.id, mapOf("funLevel" to newFun))
                                    petRepository.addXp(petData, 2)
                                }
                            }
                        } else {
                            // Si no se ha agitado en los últimos 500ms, dejar de reír
                            if (curTime - lastShakeTime > 500) {
                                isLaughing = false
                            }
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de Diversión
        Image(
            painter = painterResource(id = R.drawable.pantalla_diversion),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenedor de la Mascota y el Botón (Misma estructura exacta que SleepScreen)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            SealPet(
                isSleeping = petData.sleeping,
                energy = petData.energy,
                health = petData.health,
                funLevel = petData.funLevel,
                hygiene = petData.hygiene,
                isLaughing = isLaughing, // Pasar el estado de risa
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(600.dp)
                    .padding(bottom = 60.dp)
            )

            // Botón del mando envuelto en un Box
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .size(190.dp)
                    .clickable {
                        onNavigateToMenuGames()
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.item_control),
                    contentDescription = "Menu de Juegos",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // Header superior
        TopStatusBar(petData = petData)
    }
}
