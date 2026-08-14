package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.*

data class HUDHotspot(
    val id: String,
    val title: String,
    val zone: String,
    val description: String,
    val dimensions: String,
    val anchor: String,
    val touchBehavior: String
)

@Composable
fun HUDLayoutDiagramScreen(modifier: Modifier = Modifier) {
    var selectedHotspotId by remember { mutableStateOf("joystick") }
    var showErgonomicsHeatmap by remember { mutableStateOf(true) }

    val hotspots = listOf(
        HUDHotspot(
            id = "joystick",
            title = "Floating Analog Joystick",
            zone = "Bottom-Left Thumb Zone",
            description = "Dynamic or fixed thumbstick for 360° omnidirectional movement, auto-sprint lock gate, and diagonal strafing.",
            dimensions = "120dp × 120dp (Knob: 54dp)",
            anchor = "Bottom-Left (16dp offset)",
            touchBehavior = "Drag with deadzone & threshold clamp"
        ),
        HUDHotspot(
            id = "fire_btn",
            title = "Primary Fire Trigger",
            zone = "Bottom-Right Thumb Cluster",
            description = "Largest touch target. Holds continuous auto-fire for automatic weapons, tap for single-fire, drag down for manual recoil compensation.",
            dimensions = "86dp × 86dp (Circular)",
            anchor = "Bottom-Right (16dp offset)",
            touchBehavior = "Pointer press / continuous hold"
        ),
        HUDHotspot(
            id = "aim_scope",
            title = "Aim / ADS Scope Toggle",
            zone = "Right Mid Thumb Zone",
            description = "Activates Red Dot optical zoom. Amplifies Chest-Lock aim assist sensitivity multiplier and reduces bullet spread.",
            dimensions = "50dp × 50dp",
            anchor = "Right Mid-High",
            touchBehavior = "Tap toggle / Hold-to-ADS option"
        ),
        HUDHotspot(
            id = "stance_cluster",
            title = "Stance Selector (Jump/Crouch/Prone)",
            zone = "Right Vertical Arc",
            description = "Ergonomically curved column. Jump enables bunny-hopping, Crouch increases accuracy by 25%, Prone drops silhouette by 70%.",
            dimensions = "44dp × 44dp each",
            anchor = "Right Inner Arc",
            touchBehavior = "Instant tap transition"
        ),
        HUDHotspot(
            id = "crosshair",
            title = "Fixed Center Red Dot & Chest Lock",
            zone = "Exact Screen Center (50%, 50%)",
            description = "Pixel-exact center reticle. When an enemy enters the Torso Acquisition Radius, reticle snaps and highlights in Red.",
            dimensions = "24dp × 24dp (Center Dot: 4dp)",
            anchor = "Absolute Screen Center",
            touchBehavior = "Passive HUD indicator"
        ),
        HUDHotspot(
            id = "gloo_grenade",
            title = "Gloo Wall Quick-Deploy",
            zone = "Left Tactical Sub-Cluster",
            description = "One-tap defensive wall placement directly in front of the player's facing direction. Essential for instant cover.",
            dimensions = "52dp × 52dp",
            anchor = "Left Inner (Above Joystick)",
            touchBehavior = "Instant tap deploy"
        ),
        HUDHotspot(
            id = "weapon_hotbar",
            title = "Quick Weapon Switcher",
            zone = "Bottom Center Zone",
            description = "Horizontal hotbar switching Primary AR/SMG/Sniper, Secondary Pistol, and Melee Pan.",
            dimensions = "240dp × 42dp",
            anchor = "Bottom Center",
            touchBehavior = "Tap slot index"
        ),
        HUDHotspot(
            id = "top_scoreboard",
            title = "Scoreboard, Timer & Armory",
            zone = "Top Full-Width Safe Area",
            description = "Displays Team Blue vs Red round count (First to 4), 15s Buy Phase timer, alive squad pips, and current cash balance.",
            dimensions = "Full Width × 36dp",
            anchor = "Top Inset Anchor",
            touchBehavior = "Interactive shop tap"
        )
    )

    val currentHotspot = hotspots.first { it.id == selectedHotspotId }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDark)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Side: Interactive Mobile Landscape HUD Wireframe Diagram
        Surface(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalBorder, RoundedCornerShape(14.dp)),
            color = TacticalSurface
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MOBILE LANDSCAPE HUD WIREFRAME",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TacticalCyan
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Thumb Reach Heatmap", fontSize = 10.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = showErgonomicsHeatmap,
                            onCheckedChange = { showErgonomicsHeatmap = it },
                            modifier = Modifier.height(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Simulated Phone Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F141C))
                        .border(2.dp, if (showErgonomicsHeatmap) TacticalGold.copy(alpha = 0.5f) else TacticalBorder, RoundedCornerShape(12.dp))
                ) {
                    // Top Safe Area (Scoreboard)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(30.dp)
                            .background(if (selectedHotspotId == "top_scoreboard") FlameOrange.copy(alpha = 0.3f) else TacticalSurfaceElevated.copy(alpha = 0.7f))
                            .border(1.dp, if (selectedHotspotId == "top_scoreboard") FlameOrange else TacticalBorder)
                            .clickable { selectedHotspotId = "top_scoreboard" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "[TOP SCOREBOARD: BLUE (3) - RED (2) • 15s BUY TIMER • $2,400]", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TacticalGold)
                    }

                    // Left Thumb Zone (Joystick)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 14.dp, bottom = 12.dp)
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(if (selectedHotspotId == "joystick") TacticalCyan.copy(alpha = 0.4f) else TacticalSurfaceVariant.copy(alpha = 0.8f))
                            .border(2.dp, if (selectedHotspotId == "joystick") TacticalCyan else TacticalBorder, CircleShape)
                            .clickable { selectedHotspotId = "joystick" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "JOYSTICK\n(LEFT THUMB)", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Left Tactical Drops (Gloo & Grenade)
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 100.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (selectedHotspotId == "gloo_grenade") GlooWallCyan.copy(alpha = 0.4f) else TacticalSurfaceVariant)
                            .border(1.5.dp, if (selectedHotspotId == "gloo_grenade") GlooWallCyan else TacticalBorder, CircleShape)
                            .clickable { selectedHotspotId = "gloo_grenade" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "GLOO", fontSize = 8.sp, fontWeight = FontWeight.Black, color = GlooWallCyan)
                    }

                    // Screen Center Crosshair Target
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (selectedHotspotId == "crosshair") Color.Red.copy(alpha = 0.3f) else Color.Transparent)
                            .border(1.5.dp, if (selectedHotspotId == "crosshair") Color.Red else Color.White.copy(alpha = 0.5f), CircleShape)
                            .clickable { selectedHotspotId = "crosshair" },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.Red))
                    }

                    // Bottom Center Weapon Hotbar
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 6.dp)
                            .width(160.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selectedHotspotId == "weapon_hotbar") TacticalCyan.copy(alpha = 0.3f) else TacticalSurfaceElevated)
                            .border(1.dp, if (selectedHotspotId == "weapon_hotbar") TacticalCyan else TacticalBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedHotspotId = "weapon_hotbar" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "PRIMARY | SECONDARY | MELEE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    // Right Thumb Zone: Primary Fire
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 14.dp, bottom = 12.dp)
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (selectedHotspotId == "fire_btn") FlameOrange.copy(alpha = 0.4f) else FlameOrange.copy(alpha = 0.8f))
                            .border(2.dp, if (selectedHotspotId == "fire_btn") Color.White else TacticalGold, CircleShape)
                            .clickable { selectedHotspotId = "fire_btn" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "FIRE\n(RIGHT THUMB)", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }

                    // Right Scope ADS
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 92.dp, bottom = 20.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (selectedHotspotId == "aim_scope") TacticalCyan.copy(alpha = 0.4f) else TacticalSurfaceElevated)
                            .border(1.5.dp, if (selectedHotspotId == "aim_scope") TacticalCyan else TacticalBorder, CircleShape)
                            .clickable { selectedHotspotId = "aim_scope" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "ADS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TacticalCyan)
                    }

                    // Right Stance Cluster (Jump/Crouch/Prone)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 92.dp, bottom = 68.dp)
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedHotspotId == "stance_cluster") TacticalGold.copy(alpha = 0.4f) else TacticalSurfaceElevated)
                            .border(1.5.dp, if (selectedHotspotId == "stance_cluster") TacticalGold else TacticalBorder, RoundedCornerShape(8.dp))
                            .clickable { selectedHotspotId = "stance_cluster" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "STANCE\nJ/C/P", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }

        // Right Side: Selected Hotspot Details & Ergonomics Breakdown
        Surface(
            modifier = Modifier
                .weight(1f)
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
                        text = "COMPONENT SPECIFICATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = currentHotspot.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = FlameOrange
                    )
                    Text(
                        text = currentHotspot.zone,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
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
                            SpecRow("Dimensions", currentHotspot.dimensions)
                            SpecRow("Screen Anchor", currentHotspot.anchor)
                            SpecRow("Touch Event", currentHotspot.touchBehavior)
                        }
                    }
                }

                item {
                    Text(
                        text = "FUNCTIONAL UX BEHAVIOR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = TextSecondary
                    )
                    Text(
                        text = currentHotspot.description,
                        fontSize = 11.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                }

                item {
                    Divider(color = TacticalBorder)
                    Text(
                        text = "SELECT HUD COMPONENT TO INSPECT:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("joystick" to "Joystick", "fire_btn" to "Fire", "aim_scope" to "Scope", "crosshair" to "Center Dot").forEach { (id, label) ->
                            Button(
                                onClick = { selectedHotspotId = id },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedHotspotId == id) FlameOrange else TacticalSurfaceElevated
                                ),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", fontSize = 10.sp, color = TextMuted)
        Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
