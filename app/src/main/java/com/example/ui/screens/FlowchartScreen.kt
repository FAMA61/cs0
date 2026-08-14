package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class FlowStep(
    val stepNumber: Int,
    val title: String,
    val phaseName: String,
    val icon: ImageVector,
    val description: String,
    val technicalLogic: String,
    val stateTransition: String
)

@Composable
fun FlowchartScreen(modifier: Modifier = Modifier) {
    var activeStepIndex by remember { mutableIntStateOf(0) }

    val flowSteps = listOf(
        FlowStep(
            stepNumber = 1,
            title = "Match Initialization & Spawn",
            phaseName = "INIT_MATCH",
            icon = Icons.Default.Flag,
            description = "Assigns 4 players to Blue Team (West Spawns) and 4 players to Red Team (East Spawns). Sets starting economy to $500 per player.",
            technicalLogic = "Initialize MatchState(blueScore=0, redScore=0, currentRound=1). Instantiate 8 player entities with 200 HP and default USP pistol.",
            stateTransition = "Transition to -> [Phase: BUY_PHASE, Timer: 15s]"
        ),
        FlowStep(
            stepNumber = 2,
            title = "15-Second Buy Phase (Armory)",
            phaseName = "BUY_PHASE",
            icon = Icons.Default.ShoppingCart,
            description = "Interactive shop UI opens. Players purchase Pistols, SMGs, ARs, Shotguns, Snipers, Helmets Lv1-3, Body Armor Lv1-3, and Gloo Walls.",
            technicalLogic = "Spawn barriers prevent base exit. Validates player.cash >= item.cost. Deducts cash and updates player inventory.",
            stateTransition = "On Timer Expire (0s) -> Lower Barriers -> [Phase: COMBAT_PHASE, Timer: 120s]"
        ),
        FlowStep(
            stepNumber = 3,
            title = "Tactical Combat & Chest-Lock Aim Assist",
            phaseName = "COMBAT_PHASE",
            icon = Icons.Default.GpsFixed,
            description = "Real-time engagement. Chest-Lock Aim Assist dynamically acquires nearest enemy within FOV and interpolates crosshair smoothly onto the enemy torso bone.",
            technicalLogic = "Aim Assist Raycast checks target bone distance and FOV angle: `rot += (targetAngle - rot) * smoothFactor`. Weapon fire applies Armor Mitigation: `finalDmg = baseDmg * (1 - armorReduction)`.",
            stateTransition = "On damage >= player.health -> Trigger KNOCKED / ELIMINATED"
        ),
        FlowStep(
            stepNumber = 4,
            title = "Elimination & Spectator Transition",
            phaseName = "SPECTATOR_ENGINE",
            icon = Icons.Default.Visibility,
            description = "Eliminated players cannot respawn during active combat. Camera automatically switches to surviving teammates with cycle controls.",
            technicalLogic = "Set victim `isDead = true`, `isSpectating = true`. Shift camera transform to `aliveTeammates[nextIndex].transform`. Attacker receives +$200 kill reward.",
            stateTransition = "Continuously poll: `blueAlive == 0 || redAlive == 0`"
        ),
        FlowStep(
            stepNumber = 5,
            title = "Round Elimination Evaluation",
            phaseName = "CHECK_ROUND_OVER",
            icon = Icons.Default.CheckCircle,
            description = "When all 4 members of one team are eliminated (or 120s timeout expires), the round immediately ends.",
            technicalLogic = "If `redAlive == 0`: Increment `blueScore++`. If `blueAlive == 0`: Increment `redScore++`.",
            stateTransition = "Transition to -> [Phase: ROUND_END, Timer: 4s]"
        ),
        FlowStep(
            stepNumber = 6,
            title = "Economy Payout & Loss Streak Calculation",
            phaseName = "ECONOMY_PAYOUT",
            icon = Icons.Default.MonetizationOn,
            description = "Winning team receives $1800. Losing team receives $1400 base + $200 per consecutive loss streak (capped at +$600).",
            technicalLogic = "Winner: `cash += 1800`, `lossStreak = 0`. Loser: `lossStreak = min(3, lossStreak + 1)`, `cash += 1400 + (lossStreak * 200)`.",
            stateTransition = "Check Match Victory Condition"
        ),
        FlowStep(
            stepNumber = 7,
            title = "Match Win Condition (First to 4 Wins)",
            phaseName = "WIN_CONDITION_CHECK",
            icon = Icons.Default.EmojiEvents,
            description = "Checks if Blue Score >= 4 or Red Score >= 4. If true, match terminates with Victory BOOYAH banner and post-game stats.",
            technicalLogic = "If `blueScore >= 4 || redScore >= 4` -> Set `phase = MATCH_VICTORY`. Else proceed to Respawn Engine.",
            stateTransition = "If Score < 4 -> Trigger RESPAWN ENGINE -> Next Round"
        ),
        FlowStep(
            stepNumber = 8,
            title = "Fresh Round Respawn & State Reset",
            phaseName = "RESPAWN_ENGINE",
            icon = Icons.Default.RestartAlt,
            description = "All 8 players respawn at fresh designated spawn points with 200 HP full health, restored armor durability, and retained purchased weaponry.",
            technicalLogic = "Set `health = 200`, `isDead = false`, `isSpectating = false`, `posX = spawnBaseX`, `posY = spawnBaseY`. Clear active gloo walls.",
            stateTransition = "Increment `currentRound++` -> Transition to -> [BUY_PHASE: 15s]"
        )
    )

    val currentStep = flowSteps[activeStepIndex]

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDark)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: Step-by-Step Flowchart List
        Surface(
            modifier = Modifier
                .weight(1.1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalBorder, RoundedCornerShape(14.dp)),
            color = TacticalSurface
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "CLASH SQUAD LOGIC FLOWCHART",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TacticalGold
                )
                Text(
                    text = "Step-by-step Round & Respawn State Machine",
                    fontSize = 9.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    itemsIndexed(flowSteps) { index, step ->
                        val isSelected = activeStepIndex == index
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) FlameOrange else TacticalBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { activeStepIndex = index },
                            color = if (isSelected) TacticalSurfaceElevated else TacticalSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) FlameOrange else TacticalSurfaceElevated),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${step.stepNumber}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.White else TacticalCyan
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = step.title,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) FlameOrange else TextPrimary
                                    )
                                    Text(
                                        text = step.phaseName,
                                        fontSize = 8.sp,
                                        color = TextMuted
                                    )
                                }

                                Icon(
                                    imageVector = step.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) FlameOrange else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Active Step Deep Dive & Technical Architecture
        Surface(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalBorder, RoundedCornerShape(14.dp)),
            color = TacticalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(FlameOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = currentStep.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "STEP ${currentStep.stepNumber}: ${currentStep.title}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = FlameOrange
                        )
                        Text(
                            text = "STATE: ${currentStep.phaseName}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalCyan
                        )
                    }
                }

                Divider(color = TacticalBorder)

                Text(
                    text = "FUNCTIONAL OVERVIEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = TextSecondary
                )
                Text(
                    text = currentStep.description,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = TacticalSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "TECHNICAL LOGIC & CODE FLOW",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = TacticalCyan
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentStep.technicalLogic,
                            fontSize = 11.sp,
                            color = TextPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TacticalGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "TRANSITION TRIGGER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = TacticalGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentStep.stateTransition,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Navigation Buttons (Prev / Next Step)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (activeStepIndex > 0) activeStepIndex-- },
                        enabled = activeStepIndex > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("◀ PREVIOUS STEP", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { if (activeStepIndex < flowSteps.size - 1) activeStepIndex++ },
                        enabled = activeStepIndex < flowSteps.size - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("NEXT STEP ▶", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
