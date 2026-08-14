package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.ClashSquadViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ClashSquadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Suggest landscape orientation for mobile shooter ergonomics
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

        setContent {
            MyApplicationTheme {
                ClashSquadMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ClashSquadMainApp(viewModel: ClashSquadViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val activeTab = uiState.activeTab

    val tabs = listOf(
        TabItem(0, "4v4 ARENA SIM", Icons.Default.SportsEsports),
        TabItem(1, "HUD WIREFRAME", Icons.Default.Screenshot),
        TabItem(2, "LOGIC FLOWCHART", Icons.Default.AccountTree),
        TabItem(3, "AIM ASSIST SANDBOX", Icons.Default.GpsFixed),
        TabItem(4, "UNITY C# CODE", Icons.Default.Code)
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("clash_squad_main_scaffold"),
        containerColor = TacticalDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sleek Top App Bar with Tab Navigation Strip
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                color = TacticalDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, TacticalBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Logo Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FlameOrange
                        ) {
                            Text(
                                text = "4v4",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = "CLASH SQUAD",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TextPrimary
                        )
                    }

                    // Navigation Tab Buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = activeTab == tab.index
                            Button(
                                onClick = { viewModel.setActiveTab(tab.index) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) FlameOrange else TacticalSurfaceVariant
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) Color.White else TextSecondary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tab.title,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Screen Content Host
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (activeTab) {
                    0 -> ArenaSimulatorScreen(uiState = uiState, viewModel = viewModel)
                    1 -> HUDLayoutDiagramScreen()
                    2 -> FlowchartScreen()
                    3 -> AimAssistSandboxScreen(viewModel = viewModel)
                    4 -> UnityCodeViewerScreen()
                }
            }
        }
    }
}

data class TabItem(
    val index: Int,
    val title: String,
    val icon: ImageVector
)
