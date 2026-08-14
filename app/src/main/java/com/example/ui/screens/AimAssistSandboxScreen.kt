package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AimAssistSettings
import com.example.ui.theme.*
import com.example.viewmodel.ClashSquadViewModel

@Composable
fun AimAssistSandboxScreen(
    viewModel: ClashSquadViewModel,
    modifier: Modifier = Modifier
) {
    val currentSettings = viewModel.uiState.collectAsState().value.aimAssistSettings
    var isEnabled by remember { mutableStateOf(currentSettings.isEnabled) }
    var lockRadius by remember { mutableFloatStateOf(currentSettings.lockRadiusPixels) }
    var lockSmoothing by remember { mutableFloatStateOf(currentSettings.lockSmoothing) }
    var adsBoost by remember { mutableFloatStateOf(currentSettings.adsBoostMultiplier) }

    fun syncSettings() {
        viewModel.updateAimAssistSettings(
            AimAssistSettings(
                isEnabled = isEnabled,
                lockRadiusPixels = lockRadius,
                lockSmoothing = lockSmoothing,
                adsBoostMultiplier = adsBoost
            )
        )
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDark)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Interactive Aim Assist Tuner Sliders
        Surface(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalBorder, RoundedCornerShape(14.dp)),
            color = TacticalSurface
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "AIM ASSIST TUNER",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = FlameOrange
                            )
                            Text(
                                text = "Chest-Lock Parameters",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = {
                                isEnabled = it
                                syncSettings()
                            }
                        )
                    }
                }

                item { Divider(color = TacticalBorder) }

                // 1. Lock Radius Slider
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Acquisition Radius (FOV)", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "${lockRadius.toInt()} px", fontSize = 10.sp, color = TacticalCyan, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = lockRadius,
                            onValueChange = {
                                lockRadius = it
                                syncSettings()
                            },
                            valueRange = 80f..260f
                        )
                    }
                }

                // 2. Lock Smoothing Lerp Slider
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Torso Pull Smoothing (Lerp)", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "${(lockSmoothing * 100).toInt()}%", fontSize = 10.sp, color = TacticalGold, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = lockSmoothing,
                            onValueChange = {
                                lockSmoothing = it
                                syncSettings()
                            },
                            valueRange = 0.10f..0.60f
                        )
                    }
                }

                // 3. ADS Scope Boost Multiplier
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "ADS Scope Lock Multiplier", fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(text = "${String.format("%.1f", adsBoost)}x", fontSize = 10.sp, color = FlameOrange, fontFamily = FontFamily.Monospace)
                        }
                        Slider(
                            value = adsBoost,
                            onValueChange = {
                                adsBoost = it
                                syncSettings()
                            },
                            valueRange = 1.0f..2.5f
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            lockRadius = 140f
                            lockSmoothing = 0.28f
                            adsBoost = 1.6f
                            isEnabled = true
                            syncSettings()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("RESET TO COMPETITIVE ESPORTS DEFAULTS", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Right Column: Algorithm Breakdown & Vector Math Specs
        Surface(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalBorder, RoundedCornerShape(14.dp)),
            color = TacticalSurface
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "CHEST-LOCK (BODY AIM) SYSTEM DESIGN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TacticalCyan
                    )
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TacticalSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "1. Torso Acquisition Cone",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameOrange
                            )
                            Text(
                                text = "Calculates angle between camera forward vector and Vector3(Enemy.ChestBone - Camera.Position). If within Angular FOV Cone (28° Hipfire / 38° ADS) and distance < MaxRange, target is marked as Candidate.",
                                fontSize = 10.sp,
                                color = TextPrimary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TacticalSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "2. Chest Lock Smooth Interpolation (Lerp)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalGold
                            )
                            Text(
                                text = "To avoid unnatural instant snapping (hard aimbot), camera rotation smoothly interpolates toward target using: targetRotation = Quaternion.Slerp(camera.rotation, targetChestLookRotation, smoothSpeed * Time.deltaTime).",
                                fontSize = 10.sp,
                                color = TextPrimary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = TacticalSurfaceElevated,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "3. Recoil Drag-Up & Headshot Breakaway",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HealthGreen
                            )
                            Text(
                                text = "Chest Lock firmly grips the torso, but allows skilled mobile players to manually swipe UP on the fire button to break away from chest-lock and land headshots (Free Fire Drag-Shot mechanic).",
                                fontSize = 10.sp,
                                color = TextPrimary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                item {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TacticalBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "4. Line of Sight & Gloo Wall Occlusion",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = TacticalCyan
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Physics.Linecast(CameraPos, ChestPos, ObstacleMask) immediately cancels lock if enemy breaks behind concrete walls, crates, or Gloo Wall shields.",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
