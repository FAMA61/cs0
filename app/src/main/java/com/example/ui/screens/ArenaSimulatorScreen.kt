package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.ClashSquadUiState
import com.example.viewmodel.ClashSquadViewModel

@Composable
fun ArenaSimulatorScreen(
    uiState: ClashSquadUiState,
    viewModel: ClashSquadViewModel,
    modifier: Modifier = Modifier
) {
    val human = uiState.humanPlayer
    val isDeadOrSpectating = human?.isDead == true || human?.isSpectating == true

    Box(modifier = modifier.fillMaxSize()) {
        // 1. Tactical Combat Arena Viewport (Canvas)
        CombatCanvas(
            players = uiState.players,
            humanPlayer = human,
            glooWalls = uiState.glooWalls,
            bulletTracers = uiState.bulletTracers,
            damagePopups = uiState.damagePopups,
            isAimAssistLocked = uiState.isAimAssistLocked,
            lockedTargetId = uiState.lockedTargetId,
            aimAssistSettings = uiState.aimAssistSettings,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Scoreboard & Round Phase Header
        Column(modifier = Modifier.align(Alignment.TopCenter)) {
            ClashSquadScoreHeader(
                matchState = uiState.matchState,
                players = uiState.players,
                playerCash = human?.cash ?: 500,
                onOpenShop = { viewModel.toggleBuyMenu(true) }
            )

            // Aim Assist Status Pill
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Teammates Mini Status Card
                SquadStatusOverlay(
                    players = uiState.players.filter { it.isBlueTeam },
                    humanId = human?.id ?: ""
                )

                // Chest Lock Aim Assist Indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (uiState.isAimAssistLocked) FlameOrange.copy(alpha = 0.9f) else TacticalSurface.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (uiState.isAimAssistLocked) Color.Red else TacticalBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "Aim Assist",
                            tint = if (uiState.isAimAssistLocked) Color.White else TacticalCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (uiState.isAimAssistLocked) "CHEST LOCK: ENGAGED" else "AIM ASSIST: READY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (uiState.isAimAssistLocked) Color.White else TextSecondary
                        )
                    }
                }

                // Combat Killfeed
                CombatFeedOverlay(combatLogs = uiState.combatLogs)
            }
        }

        // 3. Floating Damage Popups
        DamagePopupOverlay(damagePopups = uiState.damagePopups)

        // 4. Center Notification Banners (Round Start, Victory, Elimination)
        RoundBannerOverlay(
            matchState = uiState.matchState,
            onRestartMatch = { viewModel.startNewMatch() }
        )

        // 5. Bottom Left Touch Controls (Floating Joystick, Gloo Wall, Grenade)
        if (!isDeadOrSpectating) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Floating Joystick Gear
                    FloatingJoystick(
                        onJoystickMoved = { x, y -> viewModel.setJoystick(x, y) }
                    )

                    // Tactical Left Quick Drops (Gloo Wall & Frag Grenade)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TacticalActionButton(
                            icon = Icons.Default.Shield,
                            label = "GLOO",
                            onClick = { viewModel.deployGlooWall() },
                            badgeText = "${human?.glooWallCount ?: 0}",
                            buttonColor = GlooWallCyan,
                            testTag = "gloo_wall_button"
                        )
                        TacticalActionButton(
                            icon = Icons.Default.FlashOn,
                            label = "GRENADE",
                            onClick = { /* Grenade throw simulation */ },
                            badgeText = "${human?.grenadeCount ?: 0}",
                            buttonColor = FlameOrange,
                            testTag = "grenade_button"
                        )
                    }
                }
            }
        }

        // 6. Bottom Center Weapon Hotbar & Player HP Bar
        if (!isDeadOrSpectating) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Player Health & Armor Bars
                PlayerHpArmorBar(
                    health = human?.health ?: 200f,
                    maxHealth = human?.maxHealth ?: 200f,
                    armorDurability = human?.armorDurability ?: 0f,
                    helmetLevel = human?.helmetLevel ?: 0,
                    vestLevel = human?.vestLevel ?: 0
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Quick Weapon Swap Hotbar
                QuickWeaponHotbar(
                    player = human,
                    onSwitchSlot = { viewModel.switchWeaponSlot(it) }
                )
            }
        }

        // 7. Bottom Right Touch Controls (Fire, Scope, Jump, Crouch, Prone, Reload)
        if (!isDeadOrSpectating) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Stance Column (Jump, Crouch, Prone)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Jump
                        TacticalActionButton(
                            icon = Icons.Default.KeyboardDoubleArrowUp,
                            label = "JUMP",
                            onClick = { viewModel.setStance(PlayerStance.STAND) },
                            isActive = human?.stance == PlayerStance.STAND,
                            sizeDp = 44,
                            testTag = "jump_button"
                        )
                        // Crouch
                        TacticalActionButton(
                            icon = Icons.Default.AirlineSeatReclineNormal,
                            label = "CROUCH",
                            onClick = { viewModel.setStance(PlayerStance.CROUCH) },
                            isActive = human?.stance == PlayerStance.CROUCH,
                            sizeDp = 44,
                            testTag = "crouch_button"
                        )
                        // Prone
                        TacticalActionButton(
                            icon = Icons.Default.HorizontalRule,
                            label = "PRONE",
                            onClick = { viewModel.setStance(PlayerStance.PRONE) },
                            isActive = human?.stance == PlayerStance.PRONE,
                            sizeDp = 44,
                            testTag = "prone_button"
                        )
                    }

                    // Reload & Aim ADS Scope Column
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Aim/Scope ADS Toggle
                        TacticalActionButton(
                            icon = Icons.Default.CenterFocusStrong,
                            label = "SCOPE",
                            onClick = { viewModel.toggleScope() },
                            isActive = human?.isScoping == true,
                            buttonColor = TacticalCyan,
                            sizeDp = 50,
                            testTag = "scope_button"
                        )

                        // Reload Button
                        TacticalActionButton(
                            icon = Icons.Default.Refresh,
                            label = "RELOAD",
                            onClick = { viewModel.reloadWeapon() },
                            isActive = human?.isReloading == true,
                            sizeDp = 50,
                            testTag = "reload_button"
                        )
                    }

                    // Primary Fire Button (Large)
                    PrimaryFireButton(
                        onFiringChanged = { viewModel.setFiring(it) }
                    )
                }
            }
        }

        // 8. Spectator Mode Overlay if Player is Eliminated
        if (isDeadOrSpectating && uiState.matchState.phase == RoundPhase.COMBAT_PHASE) {
            SpectatorOverlay(
                spectatingPlayerId = uiState.spectatingPlayerId,
                players = uiState.players,
                onCycleSpectator = { viewModel.cycleSpectator() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // 9. Buy Phase Modal (15-second armory)
        if (uiState.isBuyMenuOpen && uiState.matchState.phase == RoundPhase.BUY_PHASE) {
            BuyPhaseModal(
                playerCash = human?.cash ?: 500,
                timeRemainingSeconds = uiState.matchState.phaseTimeRemainingSeconds,
                currentWeapon = human?.activeWeapon ?: WeaponCatalog.USP,
                currentHelmetLevel = human?.helmetLevel ?: 0,
                currentVestLevel = human?.vestLevel ?: 0,
                glooWallCount = human?.glooWallCount ?: 2,
                grenadeCount = human?.grenadeCount ?: 1,
                onBuyWeapon = { viewModel.buyWeapon(it) },
                onBuyArmor = { viewModel.buyArmor(it) },
                onBuyUtility = { viewModel.buyUtility(it) },
                onClose = { viewModel.toggleBuyMenu(false) }
            )
        }
    }
}

@Composable
private fun PlayerHpArmorBar(
    health: Float,
    maxHealth: Float,
    armorDurability: Float,
    helmetLevel: Int,
    vestLevel: Int
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = TacticalDark.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(1.dp, TacticalBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Armor / Gear indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HELMET: ${if (helmetLevel > 0) "Lv.$helmetLevel" else "NONE"}",
                    fontSize = 8.sp,
                    color = if (helmetLevel > 0) ArmorBlue else TextMuted
                )
                Text(
                    text = "•",
                    fontSize = 8.sp,
                    color = TextMuted
                )
                Text(
                    text = "VEST: ${if (vestLevel > 0) "Lv.$vestLevel" else "NONE"}",
                    fontSize = 8.sp,
                    color = if (vestLevel > 0) ArmorBlue else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Health Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "HP",
                    tint = HealthGreen,
                    modifier = Modifier.size(12.dp)
                )
                Box(
                    modifier = Modifier
                        .size(160.dp, 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.DarkGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((health / maxHealth).coerceIn(0f, 1f))
                            .background(HealthGreen)
                    )
                }
                Text(
                    text = "${health.toInt()} / ${maxHealth.toInt()}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = HealthGreen
                )
            }
        }
    }
}

@Composable
private fun SquadStatusOverlay(
    players: List<TacticalPlayer>,
    humanId: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = TacticalSurface.copy(alpha = 0.75f),
        border = androidx.compose.foundation.BorderStroke(1.dp, TacticalBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            players.forEach { p ->
                val isSelf = p.id == humanId
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isSelf) "YOU" else p.name,
                        fontSize = 8.sp,
                        fontWeight = if (isSelf) FontWeight.Black else FontWeight.Normal,
                        color = if (p.isDead) Color.Gray else if (isSelf) TacticalCyan else TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp, 3.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(if (p.isDead) Color.DarkGray else HealthGreen)
                    )
                }
            }
        }
    }
}

@Composable
private fun CombatFeedOverlay(combatLogs: List<CombatLog>) {
    Column(
        modifier = Modifier.width(160.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        combatLogs.take(3).forEach { log ->
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = TacticalDark.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, TacticalBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = log.attackerName,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (log.isAttackerBlue) TeamBlue else TeamRed
                    )
                    Text(text = "💥", fontSize = 7.sp)
                    Text(
                        text = log.victimName,
                        fontSize = 8.sp,
                        color = if (log.isVictimBlue) TeamBlue else TeamRed
                    )
                    if (log.isHeadshot) {
                        Text(text = "🎯", fontSize = 7.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DamagePopupOverlay(damagePopups: List<DamagePopup>) {
    Box(modifier = Modifier.fillMaxSize()) {
        damagePopups.forEach { popup ->
            Text(
                text = "${popup.amount}${if (popup.isHeadshot) "!" else ""}",
                fontSize = if (popup.isHeadshot) 16.sp else 13.sp,
                fontWeight = FontWeight.Black,
                color = if (popup.isHeadshot) Color.Red else if (popup.isArmorHit) TacticalGold else Color.White
            )
        }
    }
}

@Composable
private fun SpectatorOverlay(
    spectatingPlayerId: String?,
    players: List<TacticalPlayer>,
    onCycleSpectator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val target = players.firstOrNull { it.id == spectatingPlayerId }
    Surface(
        modifier = modifier
            .padding(bottom = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, FlameOrange, RoundedCornerShape(12.dp)),
        color = TacticalDark.copy(alpha = 0.90f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "ELIMINATED • SPECTATING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = FlameOrange
                )
                Text(
                    text = target?.name ?: "Teammate",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Button(
                onClick = onCycleSpectator,
                colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(text = "SWITCH PLAYER ▶", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TacticalCyan)
            }
        }
    }
}

@Composable
private fun RoundBannerOverlay(
    matchState: MatchState,
    onRestartMatch: () -> Unit
) {
    AnimatedVisibility(
        visible = matchState.phase == RoundPhase.ROUND_END || matchState.phase == RoundPhase.MATCH_VICTORY,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, if (matchState.phase == RoundPhase.MATCH_VICTORY) TacticalGold else FlameOrange, RoundedCornerShape(16.dp)),
                color = TacticalSurface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (matchState.phase == RoundPhase.MATCH_VICTORY) {
                        Text(
                            text = "🏆 BOOYAH! 🏆",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = TacticalGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = matchState.winnerTeamName ?: "MATCH WON",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onRestartMatch,
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                        ) {
                            Text("START NEW 4v4 MATCH", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text(
                            text = "ROUND FINISHED",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = FlameOrange
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${matchState.roundWinner} WON THE ROUND",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Next round begins in ${matchState.phaseTimeRemainingSeconds}s...",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }
}
