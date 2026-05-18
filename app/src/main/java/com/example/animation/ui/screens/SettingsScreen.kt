package com.example.animation.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animation.R
import com.example.animation.ui.utils.MusicManager

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var musicVol by remember { mutableFloatStateOf(MusicManager.musicVolume) }
    var sfxVol by remember { mutableFloatStateOf(MusicManager.sfxVolume) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo (Usamos el de la sala o uno genérico)
        Image(
            painter = painterResource(id = R.drawable.pantalla_sala),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay oscuro para legibilidad
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AJUSTES",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF4A148C)
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // Control Música
                    Text("Música", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Slider(
                        value = musicVol,
                        onValueChange = { 
                            musicVol = it
                            MusicManager.musicVolume = it
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Control Efectos
                    Text("Efectos de Sonido", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Slider(
                        value = sfxVol,
                        onValueChange = { 
                            sfxVol = it
                            MusicManager.sfxVolume = it
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onBack,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A148C))
                    ) {
                        Text("Cerrar", color = Color.White, modifier = Modifier.padding(horizontal = 24.dp))
                    }
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White, modifier = Modifier.size(36.dp))
        }
    }
}
