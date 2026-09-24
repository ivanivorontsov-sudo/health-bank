package org.ivanivorontsov.healthbank.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val accountId: String,
    val counterAccountId: String? = null,
    val title: String,
    val category: String,
    val amount: Long,
    val type: String, // CREDIT, DEBIT, TRANSFER
    val status: String, // COMPLETED, PENDING, FAILED
    val icon: String,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null
)
