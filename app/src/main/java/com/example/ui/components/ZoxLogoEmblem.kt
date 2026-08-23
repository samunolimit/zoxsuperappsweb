package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZoxOrangeAccent
import com.example.ui.theme.ZoxOrangeDark
import com.example.ui.theme.ZoxOrangeLight
import com.example.ui.theme.ZoxPurpleContainer
import com.example.ui.theme.ZoxPurpleDark
import com.example.ui.theme.ZoxPurplePrimary

/**
 * Modern Custom Vector "Z" Monogram Emblem with speed-lines motif and electric orange to deep purple gradient.
 */
@Composable
fun ZoxLogoEmblem(
  modifier: Modifier = Modifier,
  size: Dp = 44.dp,
  showGlow: Boolean = true,
  animated: Boolean = false
) {
  val infiniteTransition = rememberInfiniteTransition(label = "logo_anim")
  val pulseScale by if (animated) {
    infiniteTransition.animateFloat(
      initialValue = 0.95f,
      targetValue = 1.05f,
      animationSpec = infiniteRepeatable(
        animation = tween(1400, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "pulse"
    )
  } else {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
  }

  val speedLineAlpha by if (animated) {
    infiniteTransition.animateFloat(
      initialValue = 0.4f,
      targetValue = 0.9f,
      animationSpec = infiniteRepeatable(
        animation = tween(900, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "speed"
    )
  } else {
    androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(0.75f) }
  }

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size * pulseScale)) {
      val w = this.size.width
      val h = this.size.height

      // Background rounded container gradient
      val bgBrush = Brush.linearGradient(
        colors = listOf(
          Color(0xFF26183C),
          Color(0xFF161528)
        ),
        start = Offset(0f, 0f),
        end = Offset(w, h)
      )

      val cornerRadius = w * 0.28f
      drawRoundRect(
        brush = bgBrush,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
      )

      // Outer neon border
      val borderBrush = Brush.sweepGradient(
        listOf(
          ZoxOrangeAccent,
          ZoxPurplePrimary,
          ZoxOrangeLight,
          ZoxPurpleDark,
          ZoxOrangeAccent
        )
      )
      drawRoundRect(
        brush = borderBrush,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
        style = Stroke(width = w * 0.035f)
      )

      if (showGlow) {
        // Soft aura glow
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              ZoxOrangeAccent.copy(alpha = 0.25f),
              Color.Transparent
            ),
            center = Offset(w * 0.35f, h * 0.35f),
            radius = w * 0.45f
          )
        )
      }

      // Speed lines on top left
      val speedLineBrush = Brush.horizontalGradient(
        colors = listOf(
          ZoxOrangeLight.copy(alpha = speedLineAlpha),
          Color.Transparent
        )
      )
      drawLine(
        brush = speedLineBrush,
        start = Offset(w * 0.16f, h * 0.30f),
        end = Offset(w * 0.34f, h * 0.30f),
        strokeWidth = w * 0.045f,
        cap = StrokeCap.Round
      )
      drawLine(
        brush = speedLineBrush,
        start = Offset(w * 0.12f, h * 0.40f),
        end = Offset(w * 0.28f, h * 0.40f),
        strokeWidth = w * 0.04f,
        cap = StrokeCap.Round
      )

      // Geometric Sharp "Z" Monogram Path
      val topBarPath = Path().apply {
        moveTo(w * 0.26f, h * 0.26f)
        lineTo(w * 0.76f, h * 0.26f)
        lineTo(w * 0.70f, h * 0.40f)
        lineTo(w * 0.34f, h * 0.40f)
        close()
      }

      val diagonalPath = Path().apply {
        moveTo(w * 0.76f, h * 0.27f)
        lineTo(w * 0.38f, h * 0.74f)
        lineTo(w * 0.28f, h * 0.74f)
        lineTo(w * 0.66f, h * 0.27f)
        close()
      }

      val bottomBarPath = Path().apply {
        moveTo(w * 0.30f, h * 0.60f)
        lineTo(w * 0.68f, h * 0.60f)
        lineTo(w * 0.76f, h * 0.74f)
        lineTo(w * 0.24f, h * 0.74f)
        close()
      }

      // Dynamic Electric Orange -> Vivid Purple gradient
      val zGradient = Brush.linearGradient(
        colors = listOf(
          ZoxOrangeAccent,
          ZoxOrangeDark,
          Color(0xFFE91E63),
          ZoxPurplePrimary,
          ZoxPurpleDark
        ),
        start = Offset(w * 0.2f, h * 0.2f),
        end = Offset(w * 0.8f, h * 0.8f)
      )

      drawPath(topBarPath, brush = zGradient, style = Fill)
      drawPath(diagonalPath, brush = zGradient, style = Fill)
      drawPath(bottomBarPath, brush = zGradient, style = Fill)

      // Futuristic apex highlight
      drawCircle(
        color = Color(0xFFFFE082),
        radius = w * 0.035f,
        center = Offset(w * 0.77f, h * 0.26f)
      )
    }
  }
}

/**
 * Full ZOX Branding Header with Z Emblem, Title and Enterprise Super App subtitle
 */
@Composable
fun ZoxBrandHeader(
  modifier: Modifier = Modifier,
  logoSize: Dp = 38.dp,
  showTagline: Boolean = true,
  adminBadge: Boolean = false
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    ZoxLogoEmblem(size = logoSize, animated = false)
    Column {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "ZOX",
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          letterSpacing = 1.2.sp
        )
        if (adminBadge) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Brush.horizontalGradient(listOf(ZoxOrangeAccent, Color(0xFFE91E63))))
          ) {
            Text(
              text = "MASTER VAULT",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        } else {
          Text(
            text = "SUPER APPS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ZoxOrangeAccent,
            letterSpacing = 0.8.sp
          )
        }
      }
      if (showTagline) {
        Text(
          text = if (adminBadge) "Autonomous Infrastructure & Governance" else "Mizoram • Mobility • Logistics • RTC",
          fontSize = 10.sp,
          color = Color(0xFFA5A5C0),
          maxLines = 1
        )
      }
    }
  }
}
