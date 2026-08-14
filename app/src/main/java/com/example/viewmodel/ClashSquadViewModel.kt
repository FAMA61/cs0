package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

data class ClashSquadUiState(
    val matchState: MatchState = MatchState(),
    val players: List<TacticalPlayer> = emptyList(),
    val humanPlayer: TacticalPlayer? = null,
    val glooWalls: List<GlooWall> = emptyList(),
    val bulletTracers: List<BulletTracer> = emptyList(),
    val damagePopups: List<DamagePopup> = emptyList(),
    val combatLogs: List<CombatLog> = emptyList(),
    val aimAssistSettings: AimAssistSettings = AimAssistSettings(),
    val isAimAssistLocked: Boolean = false,
    val lockedTargetId: String? = null,
    val isBuyMenuOpen: Boolean = false,
    val spectatingPlayerId: String? = null,
    val joystickX: Float = 0f,
    val joystickY: Float = 0f,
    val isFiring: Boolean = false,
    val lossStreakBlue: Int = 0,
    val lossStreakRed: Int = 0,
    val activeTab: Int = 0 // 0: Arena Sim, 1: HUD Diagram, 2: Logic Flowchart, 3: Aim Assist Sandbox, 4: Unity C# Code
)

class ClashSquadViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ClashSquadUiState())
    val uiState: StateFlow<ClashSquadUiState> = _uiState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var timerJob: Job? = null
    private var lastFireTimeMs: Long = 0L

    init {
        startNewMatch()
        startGameLoop()
    }

    fun startNewMatch() {
        val initialPlayers = createInitialPlayers()
        val human = initialPlayers.first { it.isHuman }

        _uiState.update {
            it.copy(
                matchState = MatchState(
                    blueScore = 0,
                    redScore = 0,
                    currentRound = 1,
                    phase = RoundPhase.BUY_PHASE,
                    phaseTimeRemainingSeconds = 15,
                    winnerTeamName = null,
                    roundWinner = null
                ),
                players = initialPlayers,
                humanPlayer = human,
                glooWalls = emptyList(),
                bulletTracers = emptyList(),
                damagePopups = emptyList(),
                combatLogs = emptyList(),
                isBuyMenuOpen = true,
                spectatingPlayerId = human.id,
                lossStreakBlue = 0,
                lossStreakRed = 0
            )
        }
        startPhaseTimer(15)
    }

    private fun createInitialPlayers(): List<TacticalPlayer> {
        val bluePlayers = listOf(
            TacticalPlayer(
                id = "blue_1",
                name = "You (Alpha)",
                isHuman = true,
                isBlueTeam = true,
                posX = 150f,
                posY = 500f,
                rotationDeg = 0f,
                primaryWeapon = WeaponCatalog.USP,
                cash = 500,
                currentMagAmmo = WeaponCatalog.USP.magSize,
                reserveAmmo = 60
            ),
            TacticalPlayer(
                id = "blue_2",
                name = "Ghost",
                isHuman = false,
                isBlueTeam = true,
                posX = 120f,
                posY = 350f,
                rotationDeg = 0f,
                primaryWeapon = WeaponCatalog.USP,
                cash = 500,
                currentMagAmmo = WeaponCatalog.USP.magSize,
                reserveAmmo = 60
            ),
            TacticalPlayer(
                id = "blue_3",
                name = "Nova",
                isHuman = false,
                isBlueTeam = true,
                posX = 120f,
                posY = 650f,
                rotationDeg = 0f,
                primaryWeapon = WeaponCatalog.USP,
                cash = 500,
                currentMagAmmo = WeaponCatalog.USP.magSize,
                reserveAmmo = 60
            ),
            TacticalPlayer(
                id = "blue_4",
                name = "Viper",
                isHuman = false,
                isBlueTeam = true,
                posX = 80f,
                posY = 500f,
                rotationDeg = 0f,
                primaryWeapon = WeaponCatalog.USP,
                cash = 500,
                currentMagAmmo = WeaponCatalog.USP.magSize,
                reserveAmmo = 60
            )
        )

        val redPlayers = listOf(
            TacticalPlayer(
                id = "red_1",
                name = "Shadow",
                isHuman = false,
                isBlueTeam = false,
                posX = 850f,
                posY = 500f,
                rotationDeg = 180f,
                primaryWeapon = WeaponCatalog.USP,
                cash = 500,
                currentMagAmmo = WeaponCatalog.USP.magSize,
                reserveAmmo = 60
            ),
            TacticalPlayer(
                id = "red_2",
                name = "Reaper",
                isHuman = false,
                isBlueTeam = false,
                posX = 880f,
                posY = 350f,
                rotationDeg = 180f,
                primaryWeapon = WeaponCatalog.USP,
                cash = 500,
                currentMagAmmo = WeaponCatalog.USP.magSize,
                reserveAmmo = 60
            ),
            TacticalPlayer(
                id = "red_3",
                name = "Blaze",
                isHuman = false,
                isBlueTeam = false,
                posX = 880f,
                posY = 650f,
                rotationDeg = 180f,
                primaryWeapon = WeaponCatalog.USP,
                cash = 500,
                currentMagAmmo = WeaponCatalog.USP.magSize,
                reserveAmmo = 60
            ),
            TacticalPlayer(
                id = "red_4",
                name = "Titan",
                isHuman = false,
                isBlueTeam = false,
                posX = 920f,
                posY = 500f,
                rotationDeg = 180f,
                primaryWeapon = WeaponCatalog.USP,
                cash = 500,
                currentMagAmmo = WeaponCatalog.USP.magSize,
                reserveAmmo = 60
            )
        )

        return bluePlayers + redPlayers
    }

    private fun startPhaseTimer(seconds: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0 && isActive) {
                _uiState.update { it.copy(matchState = it.matchState.copy(phaseTimeRemainingSeconds = remaining)) }
                delay(1000)
                remaining--
            }
            if (!isActive) return@launch

            // Phase transition
            val currentPhase = _uiState.value.matchState.phase
            when (currentPhase) {
                RoundPhase.BUY_PHASE -> {
                    // Equip bots automatically based on their cash balance
                    equipBotsForRound()
                    _uiState.update {
                        it.copy(
                            isBuyMenuOpen = false,
                            matchState = it.matchState.copy(
                                phase = RoundPhase.COMBAT_PHASE,
                                phaseTimeRemainingSeconds = 120
                            )
                        )
                    }
                    startPhaseTimer(120)
                }
                RoundPhase.COMBAT_PHASE -> {
                    // Timeout -> evaluate alive count or tiebreak
                    checkRoundEnd(forceTimeout = true)
                }
                RoundPhase.ROUND_END -> {
                    // Reset to Buy Phase or End Match
                    if (_uiState.value.matchState.phase != RoundPhase.MATCH_VICTORY) {
                        prepareNextRound()
                    }
                }
                RoundPhase.MATCH_VICTORY -> {
                    // Do nothing, wait for user restart
                }
            }
        }
    }

    private fun equipBotsForRound() {
        val round = _uiState.value.matchState.currentRound
        _uiState.update { state ->
            val updated = state.players.map { p ->
                if (!p.isHuman) {
                    val weapon = when {
                        round >= 4 && p.cash >= 2200 -> WeaponCatalog.AWM
                        round >= 3 && p.cash >= 1700 -> listOf(WeaponCatalog.AK47, WeaponCatalog.M4A1, WeaponCatalog.M1014).random()
                        round >= 2 && p.cash >= 1300 -> listOf(WeaponCatalog.MP5, WeaponCatalog.UMP, WeaponCatalog.SCAR).random()
                        else -> WeaponCatalog.DESERT_EAGLE
                    }
                    val vest = if (p.cash > 600) 2 else if (p.cash > 300) 1 else 0
                    val helmet = if (p.cash > 400) 2 else if (p.cash > 200) 1 else 0

                    p.copy(
                        primaryWeapon = weapon,
                        currentMagAmmo = weapon.magSize,
                        reserveAmmo = 120,
                        vestLevel = max(p.vestLevel, vest),
                        helmetLevel = max(p.helmetLevel, helmet),
                        armorDurability = if (vest > 0 || helmet > 0) 100f else p.armorDurability
                    )
                } else p
            }
            state.copy(players = updated)
        }
    }

    private fun prepareNextRound() {
        val nextRound = _uiState.value.matchState.currentRound + 1
        _uiState.update { state ->
            val respawned = state.players.map { p ->
                val (spawnX, spawnY, rot) = if (p.isBlueTeam) {
                    when (p.id) {
                        "blue_1" -> Triple(150f, 500f, 0f)
                        "blue_2" -> Triple(120f, 350f, 0f)
                        "blue_3" -> Triple(120f, 650f, 0f)
                        else -> Triple(80f, 500f, 0f)
                    }
                } else {
                    when (p.id) {
                        "red_1" -> Triple(850f, 500f, 180f)
                        "red_2" -> Triple(880f, 350f, 180f)
                        "red_3" -> Triple(880f, 650f, 180f)
                        else -> Triple(920f, 500f, 180f)
                    }
                }

                p.copy(
                    health = 200f,
                    maxHealth = 200f,
                    armorDurability = if (p.vestLevel > 0 || p.helmetLevel > 0) 100f else 0f,
                    isKnocked = false,
                    isDead = false,
                    isSpectating = false,
                    posX = spawnX,
                    posY = spawnY,
                    rotationDeg = rot,
                    stance = PlayerStance.STAND,
                    currentMagAmmo = p.activeWeapon.magSize,
                    reserveAmmo = 120,
                    glooWallCount = max(p.glooWallCount, 2)
                )
            }
            val human = respawned.first { it.isHuman }

            state.copy(
                players = respawned,
                humanPlayer = human,
                glooWalls = emptyList(),
                bulletTracers = emptyList(),
                damagePopups = emptyList(),
                spectatingPlayerId = human.id,
                isBuyMenuOpen = true,
                matchState = state.matchState.copy(
                    currentRound = nextRound,
                    phase = RoundPhase.BUY_PHASE,
                    phaseTimeRemainingSeconds = 15,
                    roundWinner = null
                )
            )
        }
        startPhaseTimer(15)
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (isActive) {
                delay(33) // ~30 FPS loop for real-time simulation
                updateSimulationTick()
            }
        }
    }

    private fun updateSimulationTick() {
        val state = _uiState.value
        if (state.matchState.phase != RoundPhase.COMBAT_PHASE) return

        val now = System.currentTimeMillis()
        val players = state.players.toMutableList()
        val humanIndex = players.indexOfFirst { it.isHuman }

        // 1. Move Human Player with Joystick
        if (humanIndex != -1 && !players[humanIndex].isDead) {
            val human = players[humanIndex]
            val joyX = state.joystickX
            val joyY = state.joystickY
            val mag = sqrt(joyX * joyX + joyY * joyY)

            if (mag > 0.05f) {
                val speedMultiplier = when (human.stance) {
                    PlayerStance.STAND -> 6.5f
                    PlayerStance.CROUCH -> 4.0f
                    PlayerStance.PRONE -> 2.2f
                }
                var newX = human.posX + joyX * speedMultiplier
                var newY = human.posY + joyY * speedMultiplier

                // Boundaries clamp
                newX = newX.coerceIn(40f, 960f)
                newY = newY.coerceIn(40f, 960f)

                // Update human aim / movement direction if not chest locked
                val moveAngle = Math.toDegrees(atan2(joyY.toDouble(), joyX.toDouble())).toFloat()
                val targetRot = if (state.isAimAssistLocked && state.lockedTargetId != null) {
                    human.rotationDeg // Managed by Aim Assist
                } else {
                    moveAngle
                }

                players[humanIndex] = human.copy(
                    posX = newX,
                    posY = newY,
                    rotationDeg = targetRot
                )
            }
        }

        // 2. Aim Assist & Chest Lock Engine Calculation
        var isLocked = false
        var lockedTarget: TacticalPlayer? = null

        if (humanIndex != -1 && !players[humanIndex].isDead) {
            val human = players[humanIndex]
            val enemies = players.filter { !it.isBlueTeam && !it.isDead }
            val settings = state.aimAssistSettings

            if (settings.isEnabled && enemies.isNotEmpty()) {
                var bestScore = Float.MAX_VALUE

                for (enemy in enemies) {
                    // Chest target location (torso center)
                    val enemyChestX = enemy.posX
                    val enemyChestY = enemy.posY

                    val dx = enemyChestX - human.posX
                    val dy = enemyChestY - human.posY
                    val dist = sqrt(dx * dx + dy * dy)

                    // Angle from human to enemy
                    val angleToEnemy = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                    var angleDiff = abs(angleToEnemy - human.rotationDeg) % 360f
                    if (angleDiff > 180f) angleDiff = 360f - angleDiff

                    // Within Chest Lock Acquisition FOV Cone
                    val effectiveLockRadius = if (human.isScoping) settings.lockRadiusPixels * settings.adsBoostMultiplier else settings.lockRadiusPixels
                    val angularThreshold = if (human.isScoping) 38f else 28f

                    if (dist < effectiveLockRadius * 4.5f && angleDiff < angularThreshold) {
                        val score = dist * 0.4f + angleDiff * 10f
                        if (score < bestScore) {
                            bestScore = score
                            lockedTarget = enemy
                        }
                    }
                }

                if (lockedTarget != null) {
                    isLocked = true
                    // Smoothly pull player crosshair towards enemy chest!
                    val targetAngle = Math.toDegrees(atan2((lockedTarget.posY - human.posY).toDouble(), (lockedTarget.posX - human.posX).toDouble())).toFloat()
                    var diff = targetAngle - human.rotationDeg
                    while (diff > 180f) diff -= 360f
                    while (diff < -180f) diff += 360f

                    val smoothFactor = if (human.isScoping) settings.lockSmoothing * 1.5f else settings.lockSmoothing
                    val newRot = human.rotationDeg + diff * smoothFactor

                    players[humanIndex] = human.copy(rotationDeg = newRot)
                }
            }
        }

        // 3. AI Bot Behaviors (Tactical decision making, pathing, shooting, gloo walls)
        for (i in players.indices) {
            val bot = players[i]
            if (bot.isHuman || bot.isDead) continue

            val enemies = players.filter { it.isBlueTeam != bot.isBlueTeam && !it.isDead }
            if (enemies.isEmpty()) continue

            // Find closest enemy
            val target = enemies.minByOrNull {
                val dx = it.posX - bot.posX
                val dy = it.posY - bot.posY
                dx * dx + dy * dy
            } ?: continue

            val dx = target.posX - bot.posX
            val dy = target.posY - bot.posY
            val dist = sqrt(dx * dx + dy * dy)
            val angleToTarget = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()

            // Tactical movement: advance if far, strafe if in range, deploy gloo wall if low HP
            var moveX = 0f
            var moveY = 0f
            val isLowHp = bot.health < 80f

            if (isLowHp && bot.glooWallCount > 0 && Random.nextFloat() < 0.05f) {
                // Deploy defensive gloo wall
                deployGlooWallInternal(bot.posX + cos(Math.toRadians(angleToTarget.toDouble())).toFloat() * 50f,
                    bot.posY + sin(Math.toRadians(angleToTarget.toDouble())).toFloat() * 50f,
                    angleToTarget + 90f,
                    bot.isBlueTeam)
                players[i] = bot.copy(glooWallCount = bot.glooWallCount - 1)
            } else if (dist > bot.activeWeapon.range * 0.7f) {
                // Advance toward enemy with slight flanking angle
                val flank = if (i % 2 == 0) 25.0 else -25.0
                val moveAng = Math.toRadians((angleToTarget + flank))
                moveX = cos(moveAng).toFloat() * 3.8f
                moveY = sin(moveAng).toFloat() * 3.8f
            } else if (dist < 100f) {
                // Back up slightly
                val backAng = Math.toRadians(angleToTarget.toDouble() + 180.0)
                moveX = cos(backAng).toFloat() * 3.0f
                moveY = sin(backAng).toFloat() * 3.0f
            } else {
                // Tactical strafe
                val strafeAng = Math.toRadians(angleToTarget.toDouble() + if ((now / 1000) % 2 == 0L) 90.0 else -90.0)
                moveX = cos(strafeAng).toFloat() * 2.5f
                moveY = sin(strafeAng).toFloat() * 2.5f
            }

            var nextX = (bot.posX + moveX).coerceIn(50f, 950f)
            var nextY = (bot.posY + moveY).coerceIn(50f, 950f)

            // Bot shoot chance
            if (dist <= bot.activeWeapon.range && Random.nextFloat() < 0.12f) {
                fireWeaponInternal(bot, target, isHeadshotAttempt = Random.nextFloat() < 0.20f)
            }

            players[i] = players[i].copy(
                posX = nextX,
                posY = nextY,
                rotationDeg = angleToTarget
            )
        }

        // 4. Human Auto-Fire if holding fire button
        if (state.isFiring && humanIndex != -1 && !players[humanIndex].isDead) {
            val human = players[humanIndex]
            if (now - lastFireTimeMs >= human.activeWeapon.fireRateMs) {
                lastFireTimeMs = now
                if (human.currentMagAmmo > 0 && !human.isReloading) {
                    // Fire!
                    val target = if (lockedTarget != null) lockedTarget else {
                        // Raycast to closest enemy in front
                        val enemies = players.filter { !it.isBlueTeam && !it.isDead }
                        enemies.minByOrNull {
                            val dx = it.posX - human.posX
                            val dy = it.posY - human.posY
                            val dist = sqrt(dx * dx + dy * dy)
                            val ang = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            var diff = abs(ang - human.rotationDeg) % 360f
                            if (diff > 180f) diff = 360f - diff
                            if (diff < 20f && dist <= human.activeWeapon.range) dist else Float.MAX_VALUE
                        }
                    }

                    val isHeadshot = if (lockedTarget != null) {
                        // Chest lock prioritizes body, small chance for headshot recoil drag
                        Random.nextFloat() < 0.15f
                    } else {
                        Random.nextFloat() < 0.30f
                    }

                    fireWeaponInternal(human, target, isHeadshot)
                    players[humanIndex] = human.copy(currentMagAmmo = human.currentMagAmmo - 1)
                } else if (human.currentMagAmmo <= 0 && !human.isReloading) {
                    reloadWeapon()
                }
            }
        }

        // 5. Clean up old visual tracers & damage numbers
        val currentTracers = state.bulletTracers.filter { now - it.createdTimeMs < 250 }
        val currentDamagePopups = state.damagePopups.filter { now - it.createdTimeMs < 900 }
        val currentGlooWalls = state.glooWalls.filter { it.health > 0 }

        _uiState.update {
            it.copy(
                players = players,
                humanPlayer = players.firstOrNull { p -> p.isHuman },
                isAimAssistLocked = isLocked,
                lockedTargetId = lockedTarget?.id,
                bulletTracers = currentTracers,
                damagePopups = currentDamagePopups,
                glooWalls = currentGlooWalls
            )
        }

        // Check if one squad is wiped out
        checkRoundEnd(forceTimeout = false)
    }

    private fun fireWeaponInternal(attacker: TacticalPlayer, target: TacticalPlayer?, isHeadshotAttempt: Boolean) {
        val now = System.currentTimeMillis()
        val weapon = attacker.activeWeapon

        val startX = attacker.posX
        val startY = attacker.posY

        var endX = startX + cos(Math.toRadians(attacker.rotationDeg.toDouble())).toFloat() * weapon.range
        var endY = startY + sin(Math.toRadians(attacker.rotationDeg.toDouble())).toFloat() * weapon.range
        var isHit = false
        var isHeadshot = false

        if (target != null) {
            val dist = sqrt((target.posX - startX).pow(2) + (target.posY - startY).pow(2))
            if (dist <= weapon.range) {
                // Check if hit
                isHit = true
                isHeadshot = isHeadshotAttempt
                endX = target.posX
                endY = target.posY

                // Calculate damage with Armor & Helmet formulas
                var baseDamage = weapon.damage.toFloat()
                if (isHeadshot) {
                    baseDamage *= weapon.headshotMultiplier
                    // Helmet reduction (Lv1: 30%, Lv2: 50%, Lv3: 70%)
                    val reduction = when (target.helmetLevel) {
                        1 -> 0.30f
                        2 -> 0.50f
                        3 -> 0.70f
                        else -> 0.0f
                    }
                    baseDamage *= (1.0f - reduction)
                } else {
                    // Vest reduction (Lv1: 33%, Lv2: 50%, Lv3: 66%)
                    val reduction = when (target.vestLevel) {
                        1 -> 0.33f
                        2 -> 0.50f
                        3 -> 0.66f
                        else -> 0.0f
                    }
                    baseDamage *= (1.0f - reduction)
                }

                val finalDamage = max(8, baseDamage.roundToInt())

                // Apply damage to target
                applyDamageToPlayer(attacker, target, finalDamage, isHeadshot)

                // Damage popup
                _uiState.update { s ->
                    val popup = DamagePopup(
                        id = now + Random.nextLong(1000),
                        amount = finalDamage,
                        isHeadshot = isHeadshot,
                        isArmorHit = target.vestLevel > 0 || target.helmetLevel > 0,
                        posX = target.posX + Random.nextInt(-15, 15),
                        posY = target.posY - 30f,
                        createdTimeMs = now
                    )
                    s.copy(damagePopups = s.damagePopups + popup)
                }
            }
        }

        // Add bullet tracer
        _uiState.update { s ->
            val tracer = BulletTracer(
                id = now + Random.nextLong(1000),
                startX = startX,
                startY = startY,
                endX = endX,
                endY = endY,
                isHeadshot = isHeadshot,
                isHit = isHit,
                createdTimeMs = now
            )
            s.copy(bulletTracers = s.bulletTracers + tracer)
        }
    }

    private fun applyDamageToPlayer(attacker: TacticalPlayer, victim: TacticalPlayer, damage: Int, isHeadshot: Boolean) {
        val now = System.currentTimeMillis()
        val players = _uiState.value.players.toMutableList()
        val victimIndex = players.indexOfFirst { it.id == victim.id }
        val attackerIndex = players.indexOfFirst { it.id == attacker.id }

        if (victimIndex != -1) {
            val v = players[victimIndex]
            val newHp = max(0f, v.health - damage)
            val isDead = newHp <= 0f

            players[victimIndex] = v.copy(
                health = newHp,
                isDead = isDead,
                isSpectating = isDead && v.isHuman
            )

            if (attackerIndex != -1) {
                val a = players[attackerIndex]
                val killBonus = if (isDead) 1 else 0
                val cashEarned = if (isDead) 200 else 0 // +$200 per kill
                players[attackerIndex] = a.copy(
                    damageDealt = a.damageDealt + damage,
                    kills = a.kills + killBonus,
                    cash = a.cash + cashEarned
                )
            }

            if (isDead) {
                val log = CombatLog(
                    id = now,
                    attackerName = attacker.name,
                    isAttackerBlue = attacker.isBlueTeam,
                    victimName = victim.name,
                    isVictimBlue = victim.isBlueTeam,
                    weaponName = attacker.activeWeapon.name,
                    isHeadshot = isHeadshot,
                    timeFormatted = "${_uiState.value.matchState.phaseTimeRemainingSeconds}s"
                )
                _uiState.update { it.copy(combatLogs = (listOf(log) + it.combatLogs).take(8)) }
            }
        }

        _uiState.update { it.copy(players = players, humanPlayer = players.firstOrNull { p -> p.isHuman }) }
    }

    private fun checkRoundEnd(forceTimeout: Boolean) {
        val state = _uiState.value
        if (state.matchState.phase != RoundPhase.COMBAT_PHASE) return

        val blueAlive = state.players.count { it.isBlueTeam && !it.isDead }
        val redAlive = state.players.count { !it.isBlueTeam && !it.isDead }

        var roundWonByBlue: Boolean? = null

        if (redAlive == 0 && blueAlive > 0) {
            roundWonByBlue = true
        } else if (blueAlive == 0 && redAlive > 0) {
            roundWonByBlue = false
        } else if (forceTimeout) {
            roundWonByBlue = blueAlive >= redAlive
        }

        if (roundWonByBlue != null) {
            handleRoundVictory(roundWonByBlue)
        }
    }

    private fun handleRoundVictory(isBlueWon: Boolean) {
        val state = _uiState.value
        val newBlueScore = if (isBlueWon) state.matchState.blueScore + 1 else state.matchState.blueScore
        val newRedScore = if (!isBlueWon) state.matchState.redScore + 1 else state.matchState.redScore

        val isMatchOver = newBlueScore >= state.matchState.maxWins || newRedScore >= state.matchState.maxWins
        val roundWinnerName = if (isBlueWon) "TEAM BLUE" else "TEAM RED"
        val matchWinnerName = if (isMatchOver) (if (newBlueScore >= state.matchState.maxWins) "TEAM BLUE (VICTORY!)" else "TEAM RED (VICTORY!)") else null

        // Economy payouts:
        // Winner gets $1800
        // Loser gets $1400 + loss streak bonus ($200 per streak, up to $600)
        val newStreakBlue = if (isBlueWon) 0 else min(3, state.lossStreakBlue + 1)
        val newStreakRed = if (!isBlueWon) 0 else min(3, state.lossStreakRed + 1)

        val bluePayout = if (isBlueWon) 1800 else 1400 + (newStreakBlue * 200)
        val redPayout = if (!isBlueWon) 1800 else 1400 + (newStreakRed * 200)

        val updatedPlayers = state.players.map { p ->
            val reward = if (p.isBlueTeam) bluePayout else redPayout
            p.copy(cash = p.cash + reward)
        }

        _uiState.update {
            it.copy(
                players = updatedPlayers,
                humanPlayer = updatedPlayers.firstOrNull { p -> p.isHuman },
                lossStreakBlue = newStreakBlue,
                lossStreakRed = newStreakRed,
                matchState = state.matchState.copy(
                    blueScore = newBlueScore,
                    redScore = newRedScore,
                    phase = if (isMatchOver) RoundPhase.MATCH_VICTORY else RoundPhase.ROUND_END,
                    phaseTimeRemainingSeconds = if (isMatchOver) 999 else 4,
                    roundWinner = roundWinnerName,
                    winnerTeamName = matchWinnerName
                )
            )
        }

        if (!isMatchOver) {
            startPhaseTimer(4)
        }
    }

    fun deployGlooWall() {
        val human = _uiState.value.humanPlayer ?: return
        if (human.isDead || human.glooWallCount <= 0 || _uiState.value.matchState.phase != RoundPhase.COMBAT_PHASE) return

        val deployDist = 48f
        val wallX = human.posX + cos(Math.toRadians(human.rotationDeg.toDouble())).toFloat() * deployDist
        val wallY = human.posY + sin(Math.toRadians(human.rotationDeg.toDouble())).toFloat() * deployDist

        deployGlooWallInternal(wallX, wallY, human.rotationDeg + 90f, human.isBlueTeam)

        // Decrement count
        _uiState.update { state ->
            val updated = state.players.map {
                if (it.id == human.id) it.copy(glooWallCount = it.glooWallCount - 1) else it
            }
            state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
        }
    }

    private fun deployGlooWallInternal(x: Float, y: Float, angle: Float, isBlue: Boolean) {
        val wall = GlooWall(
            id = "gloo_${System.currentTimeMillis()}_${Random.nextInt(1000)}",
            posX = x.coerceIn(40f, 960f),
            posY = y.coerceIn(40f, 960f),
            angleDeg = angle,
            isBlueTeam = isBlue
        )
        _uiState.update { it.copy(glooWalls = it.glooWalls + wall) }
    }

    fun setJoystick(x: Float, y: Float) {
        _uiState.update { it.copy(joystickX = x, joystickY = y) }
    }

    fun setFiring(firing: Boolean) {
        _uiState.update { it.copy(isFiring = firing) }
    }

    fun toggleScope() {
        val human = _uiState.value.humanPlayer ?: return
        val newScope = !human.isScoping
        _uiState.update { state ->
            val updated = state.players.map { if (it.id == human.id) it.copy(isScoping = newScope) else it }
            state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
        }
    }

    fun setStance(newStance: PlayerStance) {
        val human = _uiState.value.humanPlayer ?: return
        val finalStance = if (human.stance == newStance) PlayerStance.STAND else newStance
        _uiState.update { state ->
            val updated = state.players.map { if (it.id == human.id) it.copy(stance = finalStance) else it }
            state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
        }
    }

    fun reloadWeapon() {
        val human = _uiState.value.humanPlayer ?: return
        if (human.isReloading || human.currentMagAmmo == human.activeWeapon.magSize || human.reserveAmmo <= 0) return

        viewModelScope.launch {
            _uiState.update { state ->
                val updated = state.players.map { if (it.id == human.id) it.copy(isReloading = true) else it }
                state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
            }
            delay(1200) // Reload animation delay
            _uiState.update { state ->
                val updated = state.players.map {
                    if (it.id == human.id) {
                        val needed = it.activeWeapon.magSize - it.currentMagAmmo
                        val reloadAmount = min(needed, it.reserveAmmo)
                        it.copy(
                            isReloading = false,
                            currentMagAmmo = it.currentMagAmmo + reloadAmount,
                            reserveAmmo = it.reserveAmmo - reloadAmount
                        )
                    } else it
                }
                state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
            }
        }
    }

    fun switchWeaponSlot(slot: Int) {
        val human = _uiState.value.humanPlayer ?: return
        _uiState.update { state ->
            val updated = state.players.map { if (it.id == human.id) it.copy(activeWeaponSlot = slot) else it }
            state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
        }
    }

    fun buyWeapon(weapon: Weapon) {
        val human = _uiState.value.humanPlayer ?: return
        if (human.cash < weapon.cost) return

        _uiState.update { state ->
            val updated = state.players.map {
                if (it.id == human.id) {
                    it.copy(
                        cash = it.cash - weapon.cost,
                        primaryWeapon = weapon,
                        currentMagAmmo = weapon.magSize,
                        reserveAmmo = 120
                    )
                } else it
            }
            state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
        }
    }

    fun buyArmor(armor: ArmorItem) {
        val human = _uiState.value.humanPlayer ?: return
        if (human.cash < armor.cost) return

        _uiState.update { state ->
            val updated = state.players.map {
                if (it.id == human.id) {
                    val newCash = it.cash - armor.cost
                    if (armor.slot == ArmorSlot.HELMET) {
                        it.copy(cash = newCash, helmetLevel = max(it.helmetLevel, armor.level))
                    } else {
                        it.copy(cash = newCash, vestLevel = max(it.vestLevel, armor.level), armorDurability = 100f)
                    }
                } else it
            }
            state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
        }
    }

    fun buyUtility(utility: UtilityItem) {
        val human = _uiState.value.humanPlayer ?: return
        if (human.cash < utility.cost) return

        _uiState.update { state ->
            val updated = state.players.map {
                if (it.id == human.id) {
                    val newCash = it.cash - utility.cost
                    when (utility.type) {
                        UtilityType.GLOO_WALL -> it.copy(cash = newCash, glooWallCount = min(utility.maxCarry, it.glooWallCount + 1))
                        UtilityType.FRAG_GRENADE -> it.copy(cash = newCash, grenadeCount = min(utility.maxCarry, it.grenadeCount + 1))
                        UtilityType.REPAIR_KIT -> it.copy(cash = newCash, armorDurability = min(100f, it.armorDurability + 50f))
                    }
                } else it
            }
            state.copy(players = updated, humanPlayer = updated.firstOrNull { it.isHuman })
        }
    }

    fun toggleBuyMenu(open: Boolean) {
        _uiState.update { it.copy(isBuyMenuOpen = open) }
    }

    fun cycleSpectator() {
        val aliveBlue = _uiState.value.players.filter { it.isBlueTeam && !it.isDead }
        if (aliveBlue.isEmpty()) return
        val currentSpec = _uiState.value.spectatingPlayerId
        val currentIndex = aliveBlue.indexOfFirst { it.id == currentSpec }
        val nextIndex = (currentIndex + 1) % aliveBlue.size
        _uiState.update { it.copy(spectatingPlayerId = aliveBlue[nextIndex].id) }
    }

    fun updateAimAssistSettings(settings: AimAssistSettings) {
        _uiState.update { it.copy(aimAssistSettings = settings) }
    }

    fun setActiveTab(tabIndex: Int) {
        _uiState.update { it.copy(activeTab = tabIndex) }
    }
}
