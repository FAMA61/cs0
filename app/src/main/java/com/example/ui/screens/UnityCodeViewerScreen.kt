package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun UnityCodeViewerScreen(modifier: Modifier = Modifier) {
    var selectedFileIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val codeFiles = listOf(
        "ChestLockAimAssist.cs" to chestLockAimAssistCode,
        "ClashSquadRoundManager.cs" to roundManagerCode,
        "ClashSquadEconomy.cs" to economyCode,
        "TouchInputController.cs" to touchControllerCode
    )

    val currentFileName = codeFiles[selectedFileIndex].first
    val currentFileCode = codeFiles[selectedFileIndex].second

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(TacticalDark)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Left Column: File Selector
        Surface(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalBorder, RoundedCornerShape(14.dp)),
            color = TacticalSurface
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = "UNITY C# SCRIPTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TacticalCyan
                )
                Text(
                    text = "Production Implementation",
                    fontSize = 8.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(codeFiles.size) { index ->
                        val (fileName, _) = codeFiles[index]
                        val isSelected = selectedFileIndex == index
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) FlameOrange else TacticalBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedFileIndex = index },
                            color = if (isSelected) TacticalSurfaceElevated else TacticalSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = if (isSelected) FlameOrange else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = fileName,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) FlameOrange else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Right Column: Syntax Code Viewer
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(14.dp))
                .border(1.5.dp, TacticalBorder, RoundedCornerShape(14.dp)),
            color = TacticalSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Header Bar: File Name & Copy Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(HealthGreen)
                        )
                        Text(
                            text = currentFileName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        )
                    }

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Unity C# Code", currentFileCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied $currentFileName to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalSurfaceElevated),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = TacticalGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "COPY SCRIPT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TacticalGold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Monospace Code Container
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF090D14))
                        .border(1.dp, TacticalBorder, RoundedCornerShape(8.dp)),
                    color = Color(0xFF090D14)
                ) {
                    val horizScroll = rememberScrollState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .horizontalScroll(horizScroll)
                    ) {
                        item {
                            Text(
                                text = currentFileCode,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.5.sp,
                                color = Color(0xFFD1D5DB),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- C# Script Templates ----------------

private val chestLockAimAssistCode = """
using UnityEngine;
using System.Collections.Generic;

/// <summary>
/// Clash Squad Chest-Lock (Torso Aim Assist) Engine for Mobile.
/// Automatically acquires nearest enemy in screen FOV and smoothly pulls camera toward Torso/Chest Bone.
/// </summary>
public class ChestLockAimAssist : MonoBehaviour
{
    [Header("Aim Assist FOV & Range")]
    [SerializeField] private float hipfireFovRadius = 140f; // Screen pixels
    [SerializeField] private float adsFovRadius = 220f;
    [SerializeField] private float maxLockDistance = 75f; // In-game meters
    
    [Header("Smoothing & Torso Lock Tuning")]
    [SerializeField] private float hipfireSmoothing = 0.28f; // Lerp factor
    [SerializeField] private float adsSmoothing = 0.45f;
    [SerializeField] private Vector3 chestBoneLocalOffset = new Vector3(0f, 1.25f, 0f);
    
    [Header("Layers & Occlusion")]
    [SerializeField] private LayerMask enemyLayerMask;
    [SerializeField] private LayerMask obstacleLayerMask;
    
    [Header("References")]
    [SerializeField] private Camera playerCamera;
    [SerializeField] private Transform playerTransform;
    
    public bool IsAimAssistLocked { get; private set; }
    public Transform LockedEnemyChest { get; private set; }
    public bool IsScoping { get; set; }

    void Update()
    {
        ExecuteChestLockAimAssist();
    }

    private void ExecuteChestLockAimAssist()
    {
        Transform bestTarget = FindNearestEnemyTorsoInFov();
        
        if (bestTarget != null)
        {
            IsAimAssistLocked = true;
            LockedEnemyChest = bestTarget;
            
            // Calculate target look direction toward chest bone
            Vector3 targetChestWorldPos = bestTarget.position + chestBoneLocalOffset;
            Vector3 directionToChest = (targetChestWorldPos - playerCamera.transform.position).normalized;
            Quaternion targetRotation = Quaternion.LookRotation(directionToChest);
            
            // Smoothly interpolate camera/player rotation (Body Aim)
            float currentSmoothing = IsScoping ? adsSmoothing : hipfireSmoothing;
            playerCamera.transform.rotation = Quaternion.Slerp(
                playerCamera.transform.rotation,
                targetRotation,
                currentSmoothing * Time.deltaTime * 30f
            );
        }
        else
        {
            IsAimAssistLocked = false;
            LockedEnemyChest = null;
        }
    }

    private Transform FindNearestEnemyTorsoInFov()
    {
        Vector2 screenCenter = new Vector2(Screen.width * 0.5f, Screen.height * 0.5f);
        float currentLockRadius = IsScoping ? adsFovRadius : hipfireFovRadius;
        
        Collider[] hitColliders = Physics.OverlapSphere(playerTransform.position, maxLockDistance, enemyLayerMask);
        Transform closestEnemy = null;
        float shortestScreenDistance = float.MaxValue;

        foreach (var col in hitColliders)
        {
            Vector3 chestWorldPos = col.transform.position + chestBoneLocalOffset;
            Vector3 screenPos = playerCamera.WorldToScreenPoint(chestWorldPos);

            // Must be in front of camera
            if (screenPos.z <= 0) continue;

            float screenDistance = Vector2.Distance(screenCenter, new Vector2(screenPos.x, screenPos.y));

            if (screenDistance <= currentLockRadius && screenDistance < shortestScreenDistance)
            {
                // Occlusion linecast check (Gloo Walls, Crates, Buildings)
                if (!Physics.Linecast(playerCamera.transform.position, chestWorldPos, obstacleLayerMask))
                {
                    shortestScreenDistance = screenDistance;
                    closestEnemy = col.transform;
                }
            }
        }

        return closestEnemy;
    }
}
""".trimIndent()

private val roundManagerCode = """
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Events;

public enum RoundPhase
{
    BuyPhase,
    CombatPhase,
    RoundEnd,
    MatchVictory
}

/// <summary>
/// 4v4 Clash Squad Round Controller & Win Condition Manager.
/// Manages 15s Buy Phase, Elimination monitoring, Respawning, and First-to-4 Wins.
/// </summary>
public class ClashSquadRoundManager : MonoBehaviour
{
    [Header("Match Rules")]
    [SerializeField] private int roundsToWin = 4;
    [SerializeField] private float buyPhaseDuration = 15f;
    [SerializeField] private float combatPhaseDuration = 120f;
    
    [Header("Spawn Points")]
    [SerializeField] private Transform[] blueSpawnPoints = new Transform[4];
    [SerializeField] private Transform[] redSpawnPoints = new Transform[4];

    [Header("State")]
    public int blueRoundsWon = 0;
    public int redRoundsWon = 0;
    public int currentRound = 1;
    public RoundPhase currentPhase = RoundPhase.BuyPhase;
    public float phaseTimeRemaining = 15f;

    [Header("Events")]
    public UnityEvent<RoundPhase, float> onPhaseChanged;
    public UnityEvent<string> onRoundFinished;
    public UnityEvent<string> onMatchWon;

    private List<PlayerHealth> blueTeamPlayers = new List<PlayerHealth>();
    private List<PlayerHealth> redTeamPlayers = new List<PlayerHealth>();

    void Start()
    {
        StartNewMatch();
    }

    public void StartNewMatch()
    {
        blueRoundsWon = 0;
        redRoundsWon = 0;
        currentRound = 1;
        StartCoroutine(ExecuteBuyPhase());
    }

    private IEnumerator ExecuteBuyPhase()
    {
        currentPhase = RoundPhase.BuyPhase;
        phaseTimeRemaining = buyPhaseDuration;
        
        // 1. Respawn all 8 players at fresh base spawns with 200 HP full health
        RespawnAllPlayers();

        // 2. Open Armory UI & enable spawn barriers
        onPhaseChanged?.Invoke(RoundPhase.BuyPhase, buyPhaseDuration);

        while (phaseTimeRemaining > 0)
        {
            yield return new WaitForSeconds(1f);
            phaseTimeRemaining--;
        }

        // 3. Close Shop & Lower spawn barriers
        StartCoroutine(ExecuteCombatPhase());
    }

    private IEnumerator ExecuteCombatPhase()
    {
        currentPhase = RoundPhase.CombatPhase;
        phaseTimeRemaining = combatPhaseDuration;
        onPhaseChanged?.Invoke(RoundPhase.CombatPhase, combatPhaseDuration);

        while (phaseTimeRemaining > 0 && currentPhase == RoundPhase.CombatPhase)
        {
            yield return new WaitForSeconds(0.5f);
            phaseTimeRemaining -= 0.5f;

            // Check team wipeout elimination
            int blueAlive = CountAlivePlayers(blueTeamPlayers);
            int redAlive = CountAlivePlayers(redTeamPlayers);

            if (redAlive == 0 && blueAlive > 0)
            {
                OnRoundEnded("BLUE");
                yield break;
            }
            else if (blueAlive == 0 && redAlive > 0)
            {
                OnRoundEnded("RED");
                yield break;
            }
        }
    }

    private void OnRoundEnded(string winnerTeam)
    {
        currentPhase = RoundPhase.RoundEnd;

        if (winnerTeam == "BLUE") blueRoundsWon++;
        else redRoundsWon++;

        onRoundFinished?.Invoke(winnerTeam);

        // Check Match Victory (First to 4 rounds)
        if (blueRoundsWon >= roundsToWin)
        {
            currentPhase = RoundPhase.MatchVictory;
            onMatchWon?.Invoke("TEAM BLUE VICTORY (BOOYAH!)");
        }
        else if (redRoundsWon >= roundsToWin)
        {
            currentPhase = RoundPhase.MatchVictory;
            onMatchWon?.Invoke("TEAM RED VICTORY!");
        }
        else
        {
            currentRound++;
            StartCoroutine(WaitAndStartNextRound());
        }
    }

    private IEnumerator WaitAndStartNextRound()
    {
        yield return new WaitForSeconds(4f);
        StartCoroutine(ExecuteBuyPhase());
    }

    private void RespawnAllPlayers()
    {
        for (int i = 0; i < blueTeamPlayers.Count; i++)
        {
            blueTeamPlayers[i].Respawn(blueSpawnPoints[i].position, blueSpawnPoints[i].rotation);
        }
        for (int i = 0; i < redTeamPlayers.Count; i++)
        {
            redTeamPlayers[i].Respawn(redSpawnPoints[i].position, redSpawnPoints[i].rotation);
        }
    }

    private int CountAlivePlayers(List<PlayerHealth> team)
    {
        int count = 0;
        foreach (var p in team) if (p.IsAlive) count++;
        return count;
    }
}
""".trimIndent()

private val economyCode = """
using UnityEngine;

/// <summary>
/// Clash Squad Economy & Armory Payout System.
/// Handles Cash Rewards: Round Win ($1800), Loss ($1400 + Streak bonus), Kills ($200).
/// </summary>
public class ClashSquadEconomy : MonoBehaviour
{
    private const int BASE_WIN_REWARD = 1800;
    private const int BASE_LOSS_REWARD = 1400;
    private const int LOSS_STREAK_BONUS = 200;
    private const int MAX_LOSS_BONUS = 600;
    private const int KILL_REWARD = 200;

    public int blueLossStreak = 0;
    public int redLossStreak = 0;

    public void ProcessRoundPayout(bool blueWon, PlayerInventory[] bluePlayers, PlayerInventory[] redPlayers)
    {
        if (blueWon)
        {
            blueLossStreak = 0;
            redLossStreak = Mathf.Min(3, redLossStreak + 1);

            int blueCash = BASE_WIN_REWARD;
            int redCash = BASE_LOSS_REWARD + (redLossStreak * LOSS_STREAK_BONUS);

            RewardTeam(bluePlayers, blueCash);
            RewardTeam(redPlayers, redCash);
        }
        else
        {
            redLossStreak = 0;
            blueLossStreak = Mathf.Min(3, blueLossStreak + 1);

            int redCash = BASE_WIN_REWARD;
            int blueCash = BASE_LOSS_REWARD + (blueLossStreak * LOSS_STREAK_BONUS);

            RewardTeam(redPlayers, redCash);
            RewardTeam(bluePlayers, blueCash);
        }
    }

    public void OnPlayerKill(PlayerInventory killer)
    {
        killer.AddCash(KILL_REWARD);
    }

    private void RewardTeam(PlayerInventory[] team, int amount)
    {
        foreach (var player in team)
        {
            player.AddCash(amount);
        }
    }
}
""".trimIndent()

private val touchControllerCode = """
using UnityEngine;
using UnityEngine.EventSystems;

/// <summary>
/// Mobile Landscape Touch Controller for 4v4 Tactical Shooter.
/// Manages Floating Joystick, Fire Hold, Aim Scope ADS, Jump, Crouch, and Reload.
/// </summary>
public class TouchInputController : MonoBehaviour
{
    [Header("Input Values")]
    public Vector2 MovementInput { get; private set; }
    public bool IsFiring { get; private set; }
    public bool IsScoping { get; private set; }

    [Header("References")]
    [SerializeField] private PlayerMovement playerMovement;
    [SerializeField] private PlayerWeaponHandler weaponHandler;
    [SerializeField] private ChestLockAimAssist aimAssist;

    public void OnJoystickMoved(Vector2 delta)
    {
        MovementInput = delta;
        playerMovement.SetMoveVector(delta);
    }

    public void OnFireButtonDown()
    {
        IsFiring = true;
        weaponHandler.StartFiring();
    }

    public void OnFireButtonUp()
    {
        IsFiring = false;
        weaponHandler.StopFiring();
    }

    public void OnScopeToggle()
    {
        IsScoping = !IsScoping;
        aimAssist.IsScoping = IsScoping;
        playerMovement.SetAdsState(IsScoping);
    }

    public void OnJumpPressed() => playerMovement.TriggerJump();
    public void OnCrouchPressed() => playerMovement.ToggleCrouch();
    public void OnPronePressed() => playerMovement.ToggleProne();
    public void OnReloadPressed() => weaponHandler.Reload();
    public void OnDeployGlooWall() => weaponHandler.DeployGlooWall();
}
""".trimIndent()
