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
import com.example.animation.ui.screens.MenuGamesScreen
import com.example.animation.ui.screens.NameScreen
import com.example.animation.ui.screens.StartScreen
import com.example.animation.ui.utils.NotificationHelper
import com.google.firebase.database.ServerValue

class MainActivity : ComponentActivity() {
    private val petRepository = PetRepository()
    private lateinit var prefsManager: PrefsManager
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = PrefsManager(this)
        notificationHelper = NotificationHelper(this)
        
        setContent {
            // Pedir permiso de notificaciones en Android 13+
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
                    androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
                ) { isGranted -> }
                LaunchedEffect(Unit) {
                    launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            AppNavigation(petRepository, prefsManager, notificationHelper)
        }
    }
}

@Composable
fun AppNavigation(petRepository: PetRepository, prefsManager: PrefsManager, notificationHelper: NotificationHelper) {
    val navController = rememberNavController()
    
    // Estados globales del juego
    var petData by remember { mutableStateOf<PetData?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Control de notificaciones (para no enviarlas repetidamente)
    var lastHealthAlert by remember { mutableLongStateOf(0L) }
    var lastHungerAlert by remember { mutableLongStateOf(0L) }
    var lastFunAlert by remember { mutableLongStateOf(0L) }
    var lastEnergyAlert by remember { mutableLongStateOf(0L) }

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

    // Loop Global del Juego (Optimizado con tiempos reales)
    val petIdForLoop = petData?.id
    LaunchedEffect(petIdForLoop) {
        if (petIdForLoop == null) return@LaunchedEffect
        
        var ticks = 0
        while (true) {
            kotlinx.coroutines.delay(5000) // Ciclo base: 5 segundos
            ticks++
            
            val current = petData ?: continue
            val updates = mutableMapOf<String, Any>()
            
            // --- 1. ENERGÍA (-1% cada 3 min = 36 ticks) ---
            if (current.sleeping) {
                // RECUPERAR: +1% cada 5 segundos (1 tick)
                if (current.energy < 100) {
                    val newEnergy = (current.energy + 1).coerceAtMost(100)
                    updates["energy"] = newEnergy
                    // Bono XP si llega al 100% por dormir
                    if (newEnergy == 100) petRepository.addXp(current, 10)
                }
            } else {
                // BAJAR: -1% cada 3 minutos (36 ticks)
                if (ticks % 36 == 0 && current.energy > 0) updates["energy"] = current.energy - 1
            }

            // --- 2. HAMBRE (-1% cada 4 min = 48 ticks) ---
            if (ticks % 48 == 0 && current.hunger > 0) updates["hunger"] = current.hunger - 1

            // --- 3. HIGIENE (-1% cada 6 min = 72 ticks) ---
            if (ticks % 72 == 0 && current.hygiene > 0) updates["hygiene"] = current.hygiene - 1

            // --- 4. DIVERSIÓN (-1% cada 5 min = 60 ticks) ---
            if (ticks % 60 == 0 && current.funLevel > 0) updates["funLevel"] = current.funLevel - 1

            // --- 5. SALUD (Lógica especial cada 1 min = 12 ticks) ---
            if (ticks % 12 == 0) {
                // REGENERAR: +1% si está al 100% en todo
                if (current.hunger >= 100 && current.funLevel >= 100 && current.energy >= 100) {
                    if (current.health < 100) {
                        updates["health"] = current.health + 1
                        // XP por subir vida o mantener todo al 100%
                        petRepository.addXp(current, 10) 
                    } else {
                        // XP por mantener todo al 100% por un tiempo
                        petRepository.addXp(current, 15)
                    }
                }
                
                // DEGRADAR: -1% si hay estados críticos
                if (current.hunger < 20 || current.hygiene < 20 || current.funLevel < 15 || current.energy < 15) {
                    if (current.health > 0) updates["health"] = current.health - 1
                }
            }

            // --- 6. NOTIFICACIONES (Revisar cada 1 min = 12 ticks) ---
            if (ticks % 12 == 0) {
                val now = System.currentTimeMillis()
                val cooldown = 60000L * 30 // 30 minutos de cooldown para no molestar

                if (current.health < 10 && now - lastHealthAlert > cooldown) {
                    notificationHelper.showNotification("${current.name} Salud critica", "¡Tu mascota necesita atención urgente!", 1)
                    lastHealthAlert = now
                }
                if (current.hunger < 20 && now - lastHungerAlert > cooldown) {
                    notificationHelper.showNotification("${current.name} Tiene mucha hambre", "¡Dale de comer pronto!", 2)
                    lastHungerAlert = now
                }
                if (current.funLevel < 15 && now - lastFunAlert > cooldown) {
                    notificationHelper.showNotification("${current.name} esta aburrida", "¡Juega con ella!", 3)
                    lastFunAlert = now
                }
                if (current.energy < 15 && now - lastEnergyAlert > cooldown) {
                    notificationHelper.showNotification("${current.name} esta muy cansado", "Es hora de dormir un poco.", 4)
                    lastEnergyAlert = now
                }
            }

            if (updates.isNotEmpty()) {
                petRepository.updatePet(current.id, updates)
            }
            
            // Reiniciar ticks en un múltiplo común o simplemente dejar que suba
            if (ticks >= 720) ticks = 0 // Reiniciar cada hora aprox
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
                        },
                        onNavigateToMenuGames = {
                            navController.navigate("menu_games")
                        }
                    )
                }
            }
            composable("menu_games") {
                MenuGamesScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
