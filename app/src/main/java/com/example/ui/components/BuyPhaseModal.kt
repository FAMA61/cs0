package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun BuyPhaseModal(
    playerCash: Int,
    timeRemainingSeconds: Int,
    currentWeapon: Weapon,
    currentHelmetLevel: Int,
    currentVestLevel: Int,
    glooWallCount: Int,
    grenadeCount: Int,
    onBuyWeapon: (Weapon) -> Unit,
    onBuyArmor: (ArmorItem) -> Unit,
    onBuyUtility: (UtilityItem) -> Unit,
    onClose: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val categories = listOf("WEAPONS", "ARMOR & GEAR", "UTILITIES")

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, FlameOrange, RoundedCornerShape(16.dp)),
            color = TacticalDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header Bar: Clash Squad Shop Title, 15s Timer, Cash Balance, Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Shop",
                            tint = FlameOrange,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "CLASH SQUAD ARMORY",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "BUY PHASE: ${timeRemainingSeconds}s REMAINING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalGold
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TacticalSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Cash",
                                    tint = TacticalGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$$playerCash",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TacticalGold
                                )
                            }
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEachIndexed { index, title ->
                        val isSelected = selectedCategoryIndex == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) FlameOrange else TacticalSurfaceVariant)
                                .clickable { selectedCategoryIndex = index }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Content Grid
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedCategoryIndex) {
                        0 -> {
                            // Weapons Catalog
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(WeaponCatalog.allWeapons) { weapon ->
                                    val isEquipped = currentWeapon.id == weapon.id
                                    val canAfford = playerCash >= weapon.cost
                                    WeaponBuyCard(
                                        weapon = weapon,
                                        isEquipped = isEquipped,
                                        canAfford = canAfford,
                                        onBuy = { onBuyWeapon(weapon) }
                                    )
                                }
                            }
                        }
                        1 -> {
                            // Armor & Helmets
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(ArmorCatalog.allArmors) { armor ->
                                    val isOwned = if (armor.slot == ArmorSlot.HELMET) currentHelmetLevel >= armor.level else currentVestLevel >= armor.level
                                    val canAfford = playerCash >= armor.cost
                                    ArmorBuyCard(
                                        armor = armor,
                                        isOwned = isOwned,
                                        canAfford = canAfford,
                                        onBuy = { onBuyArmor(armor) }
                                    )
                                }
                            }
                        }
                        2 -> {
                            // Utilities (Gloo Wall, Frag Grenade, Repair Kit)
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(UtilityCatalog.allUtilities) { util ->
                                    val currentCount = when (util.type) {
                                        UtilityType.GLOO_WALL -> glooWallCount
                                        UtilityType.FRAG_GRENADE -> grenadeCount
                                        UtilityType.REPAIR_KIT -> 0
                                    }
                                    val isMaxed = currentCount >= util.maxCarry && util.type != UtilityType.REPAIR_KIT
                                    val canAfford = playerCash >= util.cost && !isMaxed
                                    UtilityBuyCard(
                                        utility = util,
                                        currentCount = currentCount,
                                        isMaxed = isMaxed,
                                        canAfford = canAfford,
                                        onBuy = { onBuyUtility(util) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeaponBuyCard(
    weapon: Weapon,
    isEquipped: Boolean,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (isEquipped) TacticalCyan else if (canAfford) TacticalBorder else Color.DarkGray,
                RoundedCornerShape(10.dp)
            ),
        color = TacticalSurface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weapon.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isEquipped) TacticalCyan else TextPrimary
                )
                Text(
                    text = weapon.category.name,
                    fontSize = 8.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Weapon Stats Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "DMG: ${weapon.damage}", fontSize = 9.sp, color = FlameOrange)
                Text(text = "MAG: ${weapon.magSize}", fontSize = 9.sp, color = TextSecondary)
                Text(text = "ACC: ${weapon.accuracy}%", fontSize = 9.sp, color = TacticalGold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onBuy,
                enabled = canAfford && !isEquipped,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEquipped) TacticalCyan else FlameOrange,
                    disabledContainerColor = TacticalSurfaceElevated
                ),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            ) {
                Text(
                    text = if (isEquipped) "EQUIPPED" else if (weapon.cost == 0) "FREE" else "$${weapon.cost}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isEquipped) TacticalDark else if (canAfford) Color.White else TextMuted
                )
            }
        }
    }
}

@Composable
private fun ArmorBuyCard(
    armor: ArmorItem,
    isOwned: Boolean,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (isOwned) ArmorBlue else if (canAfford) TacticalBorder else Color.DarkGray,
                RoundedCornerShape(10.dp)
            ),
        color = TacticalSurface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = armor.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOwned) ArmorBlue else TextPrimary
            )
            Text(
                text = "-${(armor.damageReduction * 100).toInt()}% Damage Reduction",
                fontSize = 9.sp,
                color = HealthGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onBuy,
                enabled = canAfford && !isOwned,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOwned) ArmorBlue else TacticalCyan,
                    disabledContainerColor = TacticalSurfaceElevated
                ),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            ) {
                Text(
                    text = if (isOwned) "OWNED" else "$${armor.cost}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isOwned) Color.White else if (canAfford) TacticalDark else TextMuted
                )
            }
        }
    }
}

@Composable
private fun UtilityBuyCard(
    utility: UtilityItem,
    currentCount: Int,
    isMaxed: Boolean,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                if (canAfford) TacticalBorder else Color.DarkGray,
                RoundedCornerShape(10.dp)
            ),
        color = TacticalSurface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = utility.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GlooWallCyan
            )
            Text(
                text = if (utility.type == UtilityType.REPAIR_KIT) "Restores +50 Armor" else "Hold: $currentCount / ${utility.maxCarry}",
                fontSize = 9.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = onBuy,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FlameOrange,
                    disabledContainerColor = TacticalSurfaceElevated
                ),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            ) {
                Text(
                    text = if (isMaxed) "MAX" else "$${utility.cost}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (canAfford) Color.White else TextMuted
                )
            }
        }
    }
}
