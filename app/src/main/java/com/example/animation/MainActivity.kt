package com.example.animation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.animation.data.local.PrefsManager
import com.example.animation.data.model.PetData
import com.example.animation.data.repository.PetRepository
import com.example.animation.ui.screens.MainGameScreen
import com.example.animation.ui.screens.NameScreen
import com.example.animation.ui.screens.StartScreen
import com.google.firebase.database.ServerValue

class MainActivity : ComponentActivity() {
    private val petRepository = PetRepository()
    private lateinit var prefsManager: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = PrefsManager(this)
        
        setContent {
            AppNavigation(petRepository, prefsManager)
        }
    }
}

@Composable
fun AppNavigation(petRepository: PetRepository, prefsManager: PrefsManager) {
    val navController = rememberNavController()
    
    // Estados globales del juego
    var petData by remember { mutableStateOf<PetData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Al iniciar, escuchamos los cambios de Firebase en tiempo real
    LaunchedEffect(Unit) {
        val savedId = prefsManager.getPetId()
        if (savedId != null) {
            petRepository.observePet(savedId) { data ->
                petData = data
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    // Loop Global del Juego (Movido aquí para evitar reinicios de navegación)
    // Usamos el ID de la mascota como clave única del efecto
    val petIdForLoop = petData?.id
    LaunchedEffect(petIdForLoop) {
        if (petIdForLoop == null) return@LaunchedEffect
        
        var ticks = 0
        while (true) {
            kotlinx.coroutines.delay(5000)
            ticks++
            
            // IMPORTANTE: Obtenemos la versión más reciente de petData en cada ciclo
            val current = petData ?: continue
            val updates = mutableMapOf<String, Any>()
            
            if (current.sleeping) {
                android.util.Log.d("GameLoop", "Pet is sleeping. Energy: ${current.energy}")
                // MODO SUEÑO: Sube energía y PAUSA todo desgaste de energía
                if (current.energy < 100) {
                    updates["energy"] = current.energy + 1
                    android.util.Log.d("GameLoop", "Adding energy update to map")
                }
            } else {
                // MODO DESPIERTO: Baja energía cada 10 segundos
                if (ticks % 2 == 0 && current.energy > 0) {
                    updates["energy"] = current.energy - 1
                }
            }

            if (ticks % 2 == 0 && current.hunger > 0) updates["hunger"] = current.hunger - 1
            if (ticks % 3 == 0 && current.hygiene > 0) updates["hygiene"] = current.hygiene - 1
            if (ticks % 2 == 0 && current.funLevel > 0) updates["funLevel"] = current.funLevel - 1

            if (ticks % 2 == 0) {
                if (current.hunger < 20 || current.hygiene < 20 || current.energy < 15) {
                    if (current.health > 0) updates["health"] = current.health - 1
                }
            }

            if (updates.isNotEmpty()) {
                petRepository.updatePet(current.id, updates)
            }
            if (ticks > 12) ticks = 0
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController, startDestination = "start") {
            composable("start") {
                StartScreen(
                    onPlayClick = { 
                        if (petData == null) {
                            navController.navigate("name_entry")
                        }
                    },
                    onContinueClick = { 
                        if (petData != null) {
                            navController.navigate("main_game")
                        }
                    }
                )
            }
            composable("name_entry") {
                val context = androidx.compose.ui.platform.LocalContext.current
                NameScreen(
                    onSaveName = { name ->
                        petRepository.savePet(name) { id ->
                            if (id != null) {
                                prefsManager.savePetId(id)
                                petRepository.observePet(id) { data ->
                                    petData = data
                                    navController.navigate("main_game") {
                                        popUpTo("start") { inclusive = true }
                                    }
                                }
                            } else {
                                android.widget.Toast.makeText(context, "Error al conectar con Firebase.", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
            }
            composable("main_game") {
                petData?.let { data ->
                    MainGameScreen(
                        petData = data,
                        petRepository = petRepository,
                        onEnergyChange = { newEnergy: Int ->
                            petRepository.updatePet(data.id, mapOf("energy" to newEnergy))
                        }
                    )
                }
            }
        }
    }
}
