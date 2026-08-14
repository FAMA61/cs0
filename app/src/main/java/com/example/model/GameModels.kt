package com.example.model

enum class WeaponCategory {
    PISTOL, SMG, AR, SHOTGUN, SNIPER, MELEE
}

data class Weapon(
    val id: String,
    val name: String,
    val category: WeaponCategory,
    val damage: Int,
    val fireRateMs: Long,
    val range: Float,
    val magSize: Int,
    val cost: Int,
    val headshotMultiplier: Float = 2.0f,
    val accuracy: Int, // 1 - 100
    val iconName: String = "gun"
)

enum class ArmorSlot {
    HELMET, VEST
}

data class ArmorItem(
    val id: String,
    val name: String,
    val slot: ArmorSlot,
    val level: Int,
    val damageReduction: Float, // e.g. 0.33, 0.50, 0.66
    val cost: Int,
    val maxDurability: Int = 100
)

enum class UtilityType {
    GLOO_WALL, FRAG_GRENADE, REPAIR_KIT
}

data class UtilityItem(
    val id: String,
    val name: String,
    val type: UtilityType,
    val cost: Int,
    val maxCarry: Int,
    val description: String
)

enum class PlayerStance {
    STAND, CROUCH, PRONE
}

data class TacticalPlayer(
    val id: String,
    val name: String,
    val isHuman: Boolean,
    val isBlueTeam: Boolean,
    var health: Float = 200f,
    var maxHealth: Float = 200f,
    var armorDurability: Float = 0f,
    var helmetLevel: Int = 0,
    var vestLevel: Int = 0,
    var primaryWeapon: Weapon,
    var secondaryWeapon: Weapon? = null,
    var activeWeaponSlot: Int = 0, // 0 = Primary, 1 = Secondary, 2 = Melee
    var currentMagAmmo: Int = 30,
    var reserveAmmo: Int = 120,
    var glooWallCount: Int = 2,
    var grenadeCount: Int = 1,
    var cash: Int = 500,
    var kills: Int = 0,
    var deaths: Int = 0,
    var damageDealt: Int = 0,
    var isKnocked: Boolean = false,
    var isDead: Boolean = false,
    var isSpectating: Boolean = false,
    // Spatial coordinates (0..1000 arena space)
    var posX: Float = 0f,
    var posY: Float = 0f,
    var rotationDeg: Float = 0f,
    var stance: PlayerStance = PlayerStance.STAND,
    var isScoping: Boolean = false,
    var isReloading: Boolean = false,
    var reloadProgress: Float = 0f
) {
    val activeWeapon: Weapon
        get() = if (activeWeaponSlot == 1 && secondaryWeapon != null) secondaryWeapon!! else primaryWeapon
}

enum class RoundPhase {
    BUY_PHASE,
    COMBAT_PHASE,
    ROUND_END,
    MATCH_VICTORY
}

data class MatchState(
    val blueScore: Int = 0,
    val redScore: Int = 0,
    val currentRound: Int = 1,
    val maxWins: Int = 4,
    val phase: RoundPhase = RoundPhase.BUY_PHASE,
    val phaseTimeRemainingSeconds: Int = 15,
    val winnerTeamName: String? = null,
    val roundWinner: String? = null
)

data class AimAssistSettings(
    val isEnabled: Boolean = true,
    val lockRadiusPixels: Float = 140f,
    val lockSmoothing: Float = 0.28f, // Lerp factor per tick
    val chestVerticalOffsetRatio: Float = 0.05f, // Target chest/torso
    val breakThresholdPixels: Float = 220f,
    val adsBoostMultiplier: Float = 1.6f
)

data class GlooWall(
    val id: String,
    val posX: Float,
    val posY: Float,
    val angleDeg: Float,
    var health: Float = 300f,
    val maxHealth: Float = 300f,
    val isBlueTeam: Boolean
)

data class BulletTracer(
    val id: Long,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val isHeadshot: Boolean,
    val isHit: Boolean,
    val createdTimeMs: Long
)

data class DamagePopup(
    val id: Long,
    val amount: Int,
    val isHeadshot: Boolean,
    val isArmorHit: Boolean,
    val posX: Float,
    val posY: Float,
    val createdTimeMs: Long
)

data class CombatLog(
    val id: Long,
    val attackerName: String,
    val isAttackerBlue: Boolean,
    val victimName: String,
    val isVictimBlue: Boolean,
    val weaponName: String,
    val isHeadshot: Boolean,
    val timeFormatted: String
)

object WeaponCatalog {
    val USP = Weapon("usp", "USP", WeaponCategory.PISTOL, damage = 45, fireRateMs = 380, range = 180f, magSize = 12, cost = 0, accuracy = 70)
    val G18 = Weapon("g18", "G18", WeaponCategory.PISTOL, damage = 36, fireRateMs = 120, range = 160f, magSize = 15, cost = 500, accuracy = 65)
    val DESERT_EAGLE = Weapon("deagle", "Desert Eagle", WeaponCategory.PISTOL, damage = 90, fireRateMs = 450, range = 240f, magSize = 7, cost = 800, headshotMultiplier = 2.5f, accuracy = 82)

    val MP5 = Weapon("mp5", "MP5", WeaponCategory.SMG, damage = 48, fireRateMs = 110, range = 260f, magSize = 30, cost = 1300, accuracy = 75)
    val UMP = Weapon("ump", "UMP", WeaponCategory.SMG, damage = 52, fireRateMs = 125, range = 280f, magSize = 30, cost = 1400, accuracy = 78)
    val MP40 = Weapon("mp40", "MP40", WeaponCategory.SMG, damage = 54, fireRateMs = 90, range = 240f, magSize = 30, cost = 2000, accuracy = 72)
    val VECTOR = Weapon("vector", "Vector Akimbo", WeaponCategory.SMG, damage = 46, fireRateMs = 75, range = 200f, magSize = 25, cost = 1700, accuracy = 68)

    val M4A1 = Weapon("m4a1", "M4A1", WeaponCategory.AR, damage = 62, fireRateMs = 140, range = 420f, magSize = 30, cost = 1400, accuracy = 84)
    val SCAR = Weapon("scar", "SCAR", WeaponCategory.AR, damage = 65, fireRateMs = 150, range = 400f, magSize = 30, cost = 1500, accuracy = 82)
    val AK47 = Weapon("ak47", "AK47", WeaponCategory.AR, damage = 76, fireRateMs = 170, range = 440f, magSize = 30, cost = 1700, headshotMultiplier = 2.4f, accuracy = 76)
    val GROZA = Weapon("groza", "Groza", WeaponCategory.AR, damage = 82, fireRateMs = 130, range = 460f, magSize = 30, cost = 2400, accuracy = 88)

    val M1014 = Weapon("m1014", "M1014", WeaponCategory.SHOTGUN, damage = 140, fireRateMs = 500, range = 140f, magSize = 6, cost = 1400, accuracy = 55)
    val MAG7 = Weapon("mag7", "MAG-7", WeaponCategory.SHOTGUN, damage = 125, fireRateMs = 320, range = 160f, magSize = 8, cost = 1700, accuracy = 62)
    val M1887 = Weapon("m1887", "M1887 (Double Barrel)", WeaponCategory.SHOTGUN, damage = 175, fireRateMs = 600, range = 150f, magSize = 2, cost = 1900, accuracy = 58)

    val KAR98K = Weapon("kar98k", "Kar98k", WeaponCategory.SNIPER, damage = 150, fireRateMs = 1100, range = 600f, magSize = 5, cost = 1800, headshotMultiplier = 2.8f, accuracy = 95)
    val AWM = Weapon("awm", "AWM", WeaponCategory.SNIPER, damage = 190, fireRateMs = 1300, range = 700f, magSize = 5, cost = 2200, headshotMultiplier = 3.0f, accuracy = 98)

    val allWeapons = listOf(
        USP, G18, DESERT_EAGLE,
        MP5, UMP, MP40, VECTOR,
        M4A1, SCAR, AK47, GROZA,
        M1014, MAG7, M1887,
        KAR98K, AWM
    )
}

object ArmorCatalog {
    val HELMET_L1 = ArmorItem("h1", "Helmet Lv.1", ArmorSlot.HELMET, 1, 0.30f, 200)
    val HELMET_L2 = ArmorItem("h2", "Helmet Lv.2", ArmorSlot.HELMET, 2, 0.50f, 400)
    val HELMET_L3 = ArmorItem("h3", "Helmet Lv.3", ArmorSlot.HELMET, 3, 0.70f, 600)

    val VEST_L1 = ArmorItem("v1", "Body Armor Lv.1", ArmorSlot.VEST, 1, 0.33f, 300)
    val VEST_L2 = ArmorItem("v2", "Body Armor Lv.2", ArmorSlot.VEST, 2, 0.50f, 600)
    val VEST_L3 = ArmorItem("v3", "Body Armor Lv.3", ArmorSlot.VEST, 3, 0.66f, 1000)

    val allArmors = listOf(HELMET_L1, HELMET_L2, HELMET_L3, VEST_L1, VEST_L2, VEST_L3)
}

object UtilityCatalog {
    val GLOO_WALL = UtilityItem("gloo", "Gloo Wall", UtilityType.GLOO_WALL, cost = 200, maxCarry = 3, description = "Deploys an instant bulletproof ice shield.")
    val FRAG_GRENADE = UtilityItem("frag", "Frag Grenade", UtilityType.FRAG_GRENADE, cost = 200, maxCarry = 2, description = "Timed explosive dealing heavy AoE damage.")
    val REPAIR_KIT = UtilityItem("repair", "Repair Kit", UtilityType.REPAIR_KIT, cost = 100, maxCarry = 2, description = "Restores 50% durability to Helmet and Vest.")

    val allUtilities = listOf(GLOO_WALL, FRAG_GRENADE, REPAIR_KIT)
}
