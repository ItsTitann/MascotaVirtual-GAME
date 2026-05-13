package com.example.animation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animation.R

@Composable
fun NameScreen(
    onSaveName: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.pantalla_name_pet),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1.2f))

            // Cuadro de Texto Estilizado
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFFFF9C4)) // Beige/Amarillo clarito
                    .border(4.dp, Color(0xFF8B4513), RoundedCornerShape(20.dp))
                    .padding(horizontal = 20.dp, vertical = 15.dp)
            ) {
                if (name.isEmpty()) {
                    Text(
                        text = "Nombre de la mascota...",
                        color = Color(0xFF8B4513).copy(alpha = 0.6f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                BasicTextField(
                    value = name,
                    onValueChange = { name = it },
                    textStyle = TextStyle(
                        color = Color(0xFF8B4513),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón ¡GUARDAR! Estilizado
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(70.dp)
                    .shadow(6.dp, RoundedCornerShape(50.dp))
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFF5252), // Rosa/Rojo
                                Color(0xFFFFD740)  // Amarillo/Naranja
                            )
                        )
                    )
                    .border(4.dp, Color(0xFF8B4513), RoundedCornerShape(50.dp))
                    .clickable { if (name.isNotBlank()) onSaveName(name) }
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¡GUARDAR!",
                        color = Color(0xFF8B4513),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = Color(0xFF8B4513),
                        modifier = Modifier.size(30.dp)
                    )
                }
                
                // Efecto de brillo (Glossy)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.4f)
                        .align(Alignment.TopCenter)
                        .padding(horizontal = 15.dp, vertical = 5.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
