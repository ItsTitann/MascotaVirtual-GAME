package com.example.animation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.animation.R
import com.example.animation.data.model.PetData
import com.example.animation.data.repository.PetRepository
import com.example.animation.ui.components.SealPet
import com.example.animation.ui.components.TopStatusBar
import kotlin.math.roundToInt

@Composable
fun LivingRoomScreen(
    petData: PetData,
    petRepository: PetRepository
) {
    var salmonOffset by remember { mutableStateOf(Offset.Zero) }
    var isMouthOpen by remember { mutableStateOf(false) }
    
    // Usamos rememberUpdatedState para que el lambda de pointerInput siempre use el valor más reciente
    val currentPetData by rememberUpdatedState(petData)

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo de la sala
        Image(
            painter = painterResource(id = R.drawable.pantalla_sala),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenedor para foca y comida (Misma estructura que SleepScreen)
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Mascota
            SealPet(
                isSleeping = petData.sleeping,
                energy = petData.energy,
                funLevel = petData.funLevel,
                health = petData.health,
                hygiene = petData.hygiene,
                isMouthOpen = isMouthOpen,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(600.dp)
                    .padding(bottom = 60.dp) // Igual que SleepScreen
            )

            // Salmón Draggable (Posición de botón igual que SleepScreen)
            Image(
                painter = painterResource(id = R.drawable.food_salmon),
                contentDescription = "Salmón",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset { IntOffset(salmonOffset.x.roundToInt(), salmonOffset.y.roundToInt()) }
                    .padding(bottom = 20.dp) // Igual que SleepScreen
                    .size(130.dp) // Tamaño reducido
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                salmonOffset += dragAmount
                                
                                // Detectar si está cerca de la cara de la foca
                                isMouthOpen = salmonOffset.y < -300 && 
                                              salmonOffset.x in -120f..120f
                            },
                            onDragEnd = {
                                if (isMouthOpen) {
                                    // COMER: +25% hambre, -2% higiene
                                    val newHunger = (currentPetData.hunger + 25).coerceIn(0, 100)
                                    val newHygiene = (currentPetData.hygiene - 2).coerceIn(0, 100)
                                    
                                    petRepository.updatePet(currentPetData.id, mapOf(
                                        "hunger" to newHunger,
                                        "hygiene" to newHygiene
                                    ))
                                    // Ganar XP por comer
                                    petRepository.addXp(currentPetData, 5)
                                }
                                // El salmón vuelve a su lugar original para ser infinito
                                salmonOffset = Offset.Zero
                                isMouthOpen = false
                            }
                        )
                    },
                contentScale = ContentScale.Fit
            )
        }

        // Header y Barra de estados reutilizable
        TopStatusBar(petData = petData)
    }
}
