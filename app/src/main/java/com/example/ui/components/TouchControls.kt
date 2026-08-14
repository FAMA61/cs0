package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun FloatingJoystick(
    onJoystickMoved: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    val maxRadiusPx = 110f

    Box(
        modifier = modifier
            .size(130.dp)
            .testTag("floating_joystick")
            .clip(CircleShape)
            .background(TacticalSurface.copy(alpha = 0.6f))
            .border(2.dp, TacticalBorder, CircleShape)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onJoystickMoved(0f, 0f)
                    },
                    onDragCancel = {
                        knobOffset = Offset.Zero
                        onJoystickMoved(0f, 0f)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = knobOffset + dragAmount
                        val dist = sqrt(newOffset.x * newOffset.x + newOffset.y * newOffset.y)
                        knobOffset = if (dist > maxRadiusPx) {
                            Offset(
                                (newOffset.x / dist) * maxRadiusPx,
                                (newOffset.y / dist) * maxRadiusPx
                            )
                        } else {
                            newOffset
                        }
                        onJoystickMoved(
                            knobOffset.x / maxRadiusPx,
                            knobOffset.y / maxRadiusPx
                        )
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // Inner Cross Guideline
        Box(modifier = Modifier.size(1.dp, 80.dp).background(TacticalBorder.copy(alpha = 0.5f)))
        Box(modifier = Modifier.size(80.dp, 1.dp).background(TacticalBorder.copy(alpha = 0.5f)))

        // Draggable Knob
        Box(
            modifier = Modifier
                .offset { IntOffset(knobOffset.x.roundToInt(), knobOffset.y.roundToInt()) }
                .size(54.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(TacticalCyan, TacticalSurfaceElevated)
                    )
                )
                .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Joystick Gear",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun TacticalActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    badgeText: String? = null,
    buttonColor: Color = TacticalSurfaceElevated,
    sizeDp: Int = 54,
    testTag: String = "tactical_button",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(if (isActive) FlameOrange else buttonColor.copy(alpha = 0.85f))
            .border(
                1.5.dp,
                if (isActive) Color.White else TacticalBorder,
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else TextPrimary,
                modifier = Modifier.size((sizeDp * 0.44).dp)
            )
            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else TextSecondary
                )
            }
        }

        // Badge count
        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(FlameOrange)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = badgeText,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun PrimaryFireButton(
    onFiringChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    LaunchedEffect(isPressed) {
        onFiringChanged(isPressed)
    }

    Box(
        modifier = modifier
            .testTag("fire_button")
            .size(86.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    if (isPressed) listOf(Color.Red, FlameOrange)
                    else listOf(FlameOrange, Color(0xFFD84315))
                )
            )
            .border(3.dp, if (isPressed) Color.White else TacticalGold, CircleShape)
            .clickable(interactionSource = interactionSource, indication = null) { },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.GpsFixed,
                contentDescription = "Fire Button",
                tint = Color.White,
                modifier = Modifier.size(38.dp)
            )
            Text(
                text = "FIRE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun QuickWeaponHotbar(
    player: TacticalPlayer?,
    onSwitchSlot: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (player == null) return

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, TacticalBorder, RoundedCornerShape(12.dp)),
        color = TacticalSurface.copy(alpha = 0.85f)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Slot 0: Primary Weapon
            WeaponSlotCard(
                name = player.primaryWeapon.name,
                ammo = "${player.currentMagAmmo}/${player.reserveAmmo}",
                isSelected = player.activeWeaponSlot == 0,
                category = player.primaryWeapon.category.name,
                onClick = { onSwitchSlot(0) }
            )

            // Slot 1: Secondary Pistol
            WeaponSlotCard(
                name = player.secondaryWeapon?.name ?: "USP",
                ammo = "12/60",
                isSelected = player.activeWeaponSlot == 1,
                category = "PISTOL",
                onClick = { onSwitchSlot(1) }
            )

            // Slot 2: Melee (Pan/Katana)
            WeaponSlotCard(
                name = "Pan",
                ammo = "∞",
                isSelected = player.activeWeaponSlot == 2,
                category = "MELEE",
                onClick = { onSwitchSlot(2) }
            )
        }
    }
}

@Composable
private fun WeaponSlotCard(
    name: String,
    ammo: String,
    isSelected: Boolean,
    category: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TacticalSurfaceElevated else Color.Transparent)
            .border(
                1.5.dp,
                if (isSelected) TacticalCyan else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) TacticalCyan else TextPrimary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = category,
                    fontSize = 8.sp,
                    color = TextMuted
                )
                Text(
                    text = ammo,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TacticalGold
                )
            }
        }
    }
}

@Composable
fun ClashSquadScoreHeader(
    matchState: MatchState,
    players: List<TacticalPlayer>,
    playerCash: Int,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val blueAlive = players.count { it.isBlueTeam && !it.isDead }
    val redAlive = players.count { !it.isBlueTeam && !it.isDead }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TacticalDark.copy(alpha = 0.90f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Team Blue Score & Alive Pips
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "BLUE",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = TeamBlue
                )
                Text(
                    text = "${matchState.blueScore}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )

                // Alive Pips
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    for (i in 0 until 4) {
                        Box(
                            modifier = Modifier
                                .size(7.dp, 12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i < blueAlive) TeamBlue else Color.DarkGray)
                        )
                    }
                }
            }

            // Center: Phase & Round Timer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ROUND ${matchState.currentRound} / 7 (FIRST TO 4)",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                val phaseText = when (matchState.phase) {
                    RoundPhase.BUY_PHASE -> "BUY PHASE (${matchState.phaseTimeRemainingSeconds}s)"
                    RoundPhase.COMBAT_PHASE -> "ELIMINATE ENEMIES (${matchState.phaseTimeRemainingSeconds}s)"
                    RoundPhase.ROUND_END -> "ROUND OVER"
                    RoundPhase.MATCH_VICTORY -> "MATCH FINISHED"
                }
                Text(
                    text = phaseText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (matchState.phase == RoundPhase.BUY_PHASE) TacticalGold else if (matchState.phase == RoundPhase.COMBAT_PHASE) FlameOrange else HealthGreen
                )
            }

            // Right: Team Red Score & Cash Shop Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Shop Button with Cash
                Button(
                    onClick = onOpenShop,
                    colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shop",
                        tint = TacticalGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$$playerCash",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TacticalGold
                    )
                }

                // Red Alive Pips
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    for (i in 0 until 4) {
                        Box(
                            modifier = Modifier
                                .size(7.dp, 12.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (i < redAlive) TeamRed else Color.DarkGray)
                        )
                    }
                }

                Text(
                    text = "${matchState.redScore}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "RED",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = TeamRed
                )
            }
        }
    }
}
