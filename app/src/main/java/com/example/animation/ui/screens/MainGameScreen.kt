package com.example.animation.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animation.R
import com.example.animation.data.model.PetData
import com.example.animation.data.repository.PetRepository

@Composable
fun MainGameScreen(
    petData: PetData,
    petRepository: PetRepository,
    onEnergyChange: (Int) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            // Barra de navegación personalizada estilo juego
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color(0xFFD1C4E9).copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp, start = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    GameNavItem(
                        iconRes = R.drawable.btn_nav_diversion,
                        isSelected = selectedTab == 2,
                        onClick = { selectedTab = 2 }
                    )
                    GameNavItem(
                        iconRes = R.drawable.btn_nav_sala,
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 }
                    )
                    GameNavItem(
                        iconRes = R.drawable.btn_nav_dormir,
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 }
                    )
                    GameNavItem(
                        iconRes = R.drawable.btn_nav_bano,
                        isSelected = selectedTab == 3,
                        onClick = { selectedTab = 3 }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> LivingRoomScreen(petData = petData, petRepository = petRepository)
                1 -> SleepScreen(petData = petData, petRepository = petRepository, onEnergyChange = onEnergyChange)
                2 -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { 
                    Text("Pantalla de Diversión") 
                }
                3 -> BathScreen(petData = petData)
            }
        }
    }
}

@Composable
fun GameNavItem(
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val size by animateDpAsState(
        targetValue = if (isSelected) 85.dp else 70.dp,
        animationSpec = tween(200),
        label = "navItemSize"
    )
    
    val paddingBottom by animateDpAsState(
        targetValue = if (isSelected) 15.dp else 0.dp,
        animationSpec = tween(200),
        label = "navItemPadding"
    )

    Box(
        modifier = Modifier
            .padding(bottom = paddingBottom)
            .size(size)
            .shadow(if (isSelected) 8.dp else 2.dp, CircleShape)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFFB2EBF2) else Color(0xFFCFD8DC))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.7f),
            contentScale = ContentScale.Fit
        )
    }
}
