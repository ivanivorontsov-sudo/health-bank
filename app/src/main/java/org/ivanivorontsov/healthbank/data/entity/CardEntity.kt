package org.ivanivorontsov.healthbank.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val id: String,
    val name: String,
    val linkedAccountId: String,
    val lastFour: String,
    val colorHex: String,
    val monthlyLimit: Long,
    val spentThisMonth: Long = 0,
    val isFrozen: Boolean = false,
    val cardType: String
)
