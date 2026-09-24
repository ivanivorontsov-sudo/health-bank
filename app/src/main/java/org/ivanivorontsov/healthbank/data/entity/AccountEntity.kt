package org.ivanivorontsov.healthbank.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val balance: Long,
    val currency: String = "ЗДР",
    val icon: String,
    val colorHex: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
