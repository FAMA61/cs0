package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun CombatCanvas(
    players: List<TacticalPlayer>,
    humanPlayer: TacticalPlayer?,
    glooWalls: List<GlooWall>,
    bulletTracers: List<BulletTracer>,
    damagePopups: List<DamagePopup>,
    isAimAssistLocked: Boolean,
    lockedTargetId: String?,
    aimAssistSettings: AimAssistSettings,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDark)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // Scale from simulation space (1000 x 1000) to actual canvas size
            val scaleX = canvasW / 1000f
            val scaleY = canvasH / 1000f

            fun toScreen(simX: Float, simY: Float): Offset {
                return Offset(simX * scaleX, simY * scaleY)
            }

            // 1. Draw Arena Environment & Grid
            drawArenaMap(canvasW, canvasH, scaleX, scaleY)

            // 2. Draw Gloo Walls
            for (wall in glooWalls) {
                val wallPos = toScreen(wall.posX, wall.posY)
                rotate(degrees = wall.angleDeg, pivot = wallPos) {
                    val wallW = 70f * scaleX
                    val wallH = 14f * scaleY
                    drawRoundRect(
                        color = GlooWallCyan.copy(alpha = 0.85f),
                        topLeft = Offset(wallPos.x - wallW / 2, wallPos.y - wallH / 2),
                        size = Size(wallW, wallH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    drawRoundRect(
                        color = Color.White,
                        topLeft = Offset(wallPos.x - wallW / 2, wallPos.y - wallH / 2),
                        size = Size(wallW, wallH),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                        style = Stroke(width = 2f)
                    )
                }
            }

            // 3. Draw Aim Assist Acquisition Cone & Torso Lock Vector
            if (humanPlayer != null && !humanPlayer.isDead && aimAssistSettings.isEnabled) {
                val pPos = toScreen(humanPlayer.posX, humanPlayer.posY)
                val coneAngle = if (humanPlayer.isScoping) 38f else 28f
                val coneLength = (if (humanPlayer.isScoping) aimAssistSettings.lockRadiusPixels * aimAssistSettings.adsBoostMultiplier else aimAssistSettings.lockRadiusPixels) * 4.5f * scaleX

                // Draw FOV cone
                val leftAngleRad = Math.toRadians((humanPlayer.rotationDeg - coneAngle / 2).toDouble())
                val rightAngleRad = Math.toRadians((humanPlayer.rotationDeg + coneAngle / 2).toDouble())

                val pLeft = Offset(
                    pPos.x + cos(leftAngleRad).toFloat() * coneLength,
                    pPos.y + sin(leftAngleRad).toFloat() * coneLength
                )
                val pRight = Offset(
                    pPos.x + cos(rightAngleRad).toFloat() * coneLength,
                    pPos.y + sin(rightAngleRad).toFloat() * coneLength
                )

                val conePath = Path().apply {
                    moveTo(pPos.x, pPos.y)
                    lineTo(pLeft.x, pLeft.y)
                    lineTo(pRight.x, pRight.y)
                    close()
                }

                drawPath(
                    path = conePath,
                    color = if (isAimAssistLocked) FlameOrange.copy(alpha = 0.12f) else TacticalCyan.copy(alpha = 0.06f)
                )

                // If locked, draw lock ray vector to enemy chest!
                if (isAimAssistLocked && lockedTargetId != null) {
                    val lockedPlayer = players.firstOrNull { it.id == lockedTargetId }
                    if (lockedPlayer != null && !lockedPlayer.isDead) {
                        val targetPos = toScreen(lockedPlayer.posX, lockedPlayer.posY)
                        drawLine(
                            brush = Brush.linearGradient(listOf(FlameOrange, Color.Red)),
                            start = pPos,
                            end = targetPos,
                            strokeWidth = 3f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                        // Torso lock target circle
                        drawCircle(
                            color = Color.Red,
                            radius = 22f * scaleX,
                            center = targetPos,
                            style = Stroke(width = 2.5f)
                        )
                        drawCircle(
                            color = Color.Red.copy(alpha = 0.3f),
                            radius = 12f * scaleX,
                            center = targetPos
                        )
                    }
                }
            }

            // 4. Draw Players (Blue vs Red)
            for (player in players) {
                val pScreen = toScreen(player.posX, player.posY)
                if (player.isDead) {
                    // Draw skull / dead icon
                    drawLine(
                        color = Color.Gray,
                        start = Offset(pScreen.x - 10f, pScreen.y - 10f),
                        end = Offset(pScreen.x + 10f, pScreen.y + 10f),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.Gray,
                        start = Offset(pScreen.x + 10f, pScreen.y - 10f),
                        end = Offset(pScreen.x - 10f, pScreen.y + 10f),
                        strokeWidth = 3f
                    )
                    continue
                }

                val teamColor = if (player.isBlueTeam) TeamBlue else TeamRed
                val isSelf = player.isHuman

                // Player Body Circle
                val playerRadius = if (isSelf) 18f * scaleX else 15f * scaleX

                // Direction / Weapon barrel indicator
                val lookRad = Math.toRadians(player.rotationDeg.toDouble())
                val barrelEnd = Offset(
                    pScreen.x + cos(lookRad).toFloat() * (playerRadius + 16f * scaleX),
                    pScreen.y + sin(lookRad).toFloat() * (playerRadius + 16f * scaleY)
                )

                drawLine(
                    color = if (isSelf) TacticalCyan else teamColor,
                    start = pScreen,
                    end = barrelEnd,
                    strokeWidth = 4f
                )

                // Body Base
                drawCircle(
                    color = if (isSelf) TacticalCyan else teamColor,
                    radius = playerRadius,
                    center = pScreen
                )

                // Stance indicator inner dot
                val innerColor = when (player.stance) {
                    PlayerStance.STAND -> Color.White
                    PlayerStance.CROUCH -> TacticalGold
                    PlayerStance.PRONE -> FlameOrange
                }
                drawCircle(
                    color = innerColor,
                    radius = playerRadius * 0.45f,
                    center = pScreen
                )

                // HP Bar above player
                val barW = 36f * scaleX
                val barH = 5f * scaleY
                val hpRatio = (player.health / player.maxHealth).coerceIn(0f, 1f)

                drawRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    topLeft = Offset(pScreen.x - barW / 2, pScreen.y - playerRadius - 14f * scaleY),
                    size = Size(barW, barH)
                )
                drawRect(
                    color = if (player.isBlueTeam) HealthGreen else Color.Red,
                    topLeft = Offset(pScreen.x - barW / 2, pScreen.y - playerRadius - 14f * scaleY),
                    size = Size(barW * hpRatio, barH)
                )

                // Armor indicator dot
                if (player.vestLevel > 0 || player.helmetLevel > 0) {
                    drawCircle(
                        color = ArmorBlue,
                        radius = 3f * scaleX,
                        center = Offset(pScreen.x + barW / 2 + 5f, pScreen.y - playerRadius - 11f * scaleY)
                    )
                }
            }

            // 5. Draw Bullet Tracers
            for (tracer in bulletTracers) {
                val start = toScreen(tracer.startX, tracer.startY)
                val end = toScreen(tracer.endX, tracer.endY)
                val tracerColor = if (tracer.isHeadshot) Color.Red else if (tracer.isHit) TacticalGold else Color.White.copy(alpha = 0.8f)

                drawLine(
                    color = tracerColor,
                    start = start,
                    end = end,
                    strokeWidth = if (tracer.isHeadshot) 3.5f else 2f
                )
                // Bullet impact flash
                if (tracer.isHit) {
                    drawCircle(
                        color = tracerColor,
                        radius = 8f * scaleX,
                        center = end
                    )
                }
            }

            // 6. Draw Fixed Screen Center Crosshair & Reticle
            val centerOffset = Offset(canvasW / 2f, canvasH / 2f)
            val crosshairColor = if (isAimAssistLocked) Color.Red else Color.White
            val reticleSize = if (humanPlayer?.isScoping == true) 14f * scaleX else 20f * scaleX

            // Fixed Center Red Dot Reticle
            drawCircle(
                color = if (isAimAssistLocked) Color.Red else FlameOrange,
                radius = 3.5f * scaleX,
                center = centerOffset
            )

            // Crosshair tick marks (North, South, East, West)
            val tickLen = 8f * scaleX
            val gap = 6f * scaleX
            // West
            drawLine(color = crosshairColor, start = Offset(centerOffset.x - gap - tickLen, centerOffset.y), end = Offset(centerOffset.x - gap, centerOffset.y), strokeWidth = 2f)
            // East
            drawLine(color = crosshairColor, start = Offset(centerOffset.x + gap, centerOffset.y), end = Offset(centerOffset.x + gap + tickLen, centerOffset.y), strokeWidth = 2f)
            // North
            drawLine(color = crosshairColor, start = Offset(centerOffset.x, centerOffset.y - gap - tickLen), end = Offset(centerOffset.x, centerOffset.y - gap), strokeWidth = 2f)
            // South
            drawLine(color = crosshairColor, start = Offset(centerOffset.x, centerOffset.y + gap), end = Offset(centerOffset.x, centerOffset.y + gap + tickLen), strokeWidth = 2f)

            if (isAimAssistLocked) {
                // Outer lock bracket
                drawCircle(
                    color = Color.Red.copy(alpha = 0.6f),
                    radius = reticleSize * 1.6f,
                    center = centerOffset,
                    style = Stroke(width = 1.8f)
                )
            }
        }
    }
}

private fun DrawScope.drawArenaMap(canvasW: Float, canvasH: Float, scaleX: Float, scaleY: Float) {
    // Background Grid lines
    val gridCount = 10
    val stepX = canvasW / gridCount
    val stepY = canvasH / gridCount

    for (i in 0..gridCount) {
        drawLine(
            color = TacticalSurfaceVariant.copy(alpha = 0.4f),
            start = Offset(i * stepX, 0f),
            end = Offset(i * stepX, canvasH),
            strokeWidth = 1f
        )
        drawLine(
            color = TacticalSurfaceVariant.copy(alpha = 0.4f),
            start = Offset(0f, i * stepY),
            end = Offset(canvasW, i * stepY),
            strokeWidth = 1f
        )
    }

    // Spawn Base Zones
    // Blue Base (West)
    drawRect(
        color = TeamBlueDark.copy(alpha = 0.2f),
        topLeft = Offset(20f * scaleX, 150f * scaleY),
        size = Size(180f * scaleX, 700f * scaleY)
    )
    drawRect(
        color = TeamBlue.copy(alpha = 0.6f),
        topLeft = Offset(20f * scaleX, 150f * scaleY),
        size = Size(180f * scaleX, 700f * scaleY),
        style = Stroke(width = 2f)
    )

    // Red Base (East)
    drawRect(
        color = TeamRedDark.copy(alpha = 0.2f),
        topLeft = Offset(800f * scaleX, 150f * scaleY),
        size = Size(180f * scaleX, 700f * scaleY)
    )
    drawRect(
        color = TeamRed.copy(alpha = 0.6f),
        topLeft = Offset(800f * scaleX, 150f * scaleY),
        size = Size(180f * scaleX, 700f * scaleY),
        style = Stroke(width = 2f)
    )

    // Central Tactical Plaza
    drawCircle(
        color = TacticalSurfaceElevated.copy(alpha = 0.7f),
        radius = 160f * scaleX,
        center = Offset(500f * scaleX, 500f * scaleY)
    )
    drawCircle(
        color = TacticalCyan.copy(alpha = 0.3f),
        radius = 160f * scaleX,
        center = Offset(500f * scaleX, 500f * scaleY),
        style = Stroke(width = 2f)
    )

    // Tactical Concrete Obstacles / Crates
    val obstacles = listOf(
        Pair(350f, 320f), Pair(350f, 680f),
        Pair(650f, 320f), Pair(650f, 680f),
        Pair(500f, 250f), Pair(500f, 750f)
    )

    for ((ox, oy) in obstacles) {
        val oW = 60f * scaleX
        val oH = 40f * scaleY
        drawRoundRect(
            color = TacticalBorder,
            topLeft = Offset(ox * scaleX - oW / 2, oy * scaleY - oH / 2),
            size = Size(oW, oH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = TextSecondary.copy(alpha = 0.4f),
            topLeft = Offset(ox * scaleX - oW / 2, oy * scaleY - oH / 2),
            size = Size(oW, oH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
            style = Stroke(width = 1.5f)
        )
    }
}
