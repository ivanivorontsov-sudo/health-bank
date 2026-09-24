package org.ivanivorontsov.healthbank.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_prefs")
data class UserPrefsEntity(
    @PrimaryKey val id: Int = 1,
    val displayName: String = "",
    val pinHash: String = "",
    val onboardingDone: Boolean = false,
    val notifyOps: Boolean = true,
    val notifyInsights: Boolean = true
)
