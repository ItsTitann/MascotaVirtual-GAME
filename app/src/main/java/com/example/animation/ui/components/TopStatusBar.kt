package com.example.animation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animation.R
import com.example.animation.data.model.PetData

@Composable
fun TopStatusBar(
    petData: PetData,
    onSettingsClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header con Nombre y Nivel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.border_icons),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = petData.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Nivel ${petData.level}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = Color(0xFF444444),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // Barra de estados (Iconos)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusIcon(
                iconRes = R.drawable.icon_health, 
                value = petData.health, 
                fillColor = Color(0xFFFF5252)
            )
            StatusIcon(
                iconRes = R.drawable.icon_energy_outline, 
                value = petData.energy, 
                fillColor = Color(0xFF64FF2B)
            )
            StatusIcon(
                iconRes = R.drawable.icon_fun, 
                value = petData.funLevel, 
                fillColor = Color(0xFFFFEB3B)
            )
            StatusIcon(
                iconRes = R.drawable.icon_hunger, 
                value = petData.hunger, 
                fillColor = Color(0xFFFF9800)
            )
        }
    }
}
