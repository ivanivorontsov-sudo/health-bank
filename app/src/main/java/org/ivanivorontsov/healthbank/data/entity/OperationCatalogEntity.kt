package org.ivanivorontsov.healthbank.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operation_catalog")
data class OperationCatalogEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String,
    val nominalValue: Long,
    val defaultAccountId: String,
    val icon: String,
    val description: String = ""
)
