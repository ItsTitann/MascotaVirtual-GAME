package com.example.animation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animation.R

@Composable
fun EnergyIconFill(
    energy: Int,
    modifier: Modifier = Modifier,
) {
    val clamped = energy.coerceIn(0, 100)
    val fillFraction = clamped / 100f

    val fillColor = if (clamped == 0) Color(0xFFFF3B30) else Color(0xFF64FF2B)
    val ringColor = if (clamped == 0) Color(0xAAFF3B30) else Color(0xAA64FF2B)
    val baseTint = if (clamped == 0) Color(0x22FF3B30) else Color(0x2200FF00)

    Box(
        modifier = modifier.size(78.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val inset = size.minDimension * 0.12f
            val w = size.width - (inset * 2)
            val h = size.height - (inset * 2)

            drawCircle(
                color = baseTint,
                radius = w.coerceAtMost(h) / 2f,
                center = Offset(size.width / 2f, size.height / 2f)
            )

            val fillH = h * fillFraction
            val topLeft = Offset(inset, inset + (h - fillH))

            val r = w.coerceAtMost(h) / 2f
            val cx = size.width / 2f
            val cy = size.height / 2f

            val circlePath = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        left = cx - r,
                        top = cy - r,
                        right = cx + r,
                        bottom = cy + r
                    )
                )
            }

            clipPath(circlePath) {
                drawRect(
                    color = fillColor,
                    topLeft = topLeft,
                    size = Size(w, fillH),
                    style = Fill
                )
            }

            drawArc(
                color = ringColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(w, h),
                style = Stroke(width = size.minDimension * 0.02f, cap = StrokeCap.Round)
            )
        }

        Image(
            painter = painterResource(R.drawable.icon_energy_outline),
            contentDescription = "Energía",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Fit
        )

        Text(
            text = clamped.toString(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
    }
}
