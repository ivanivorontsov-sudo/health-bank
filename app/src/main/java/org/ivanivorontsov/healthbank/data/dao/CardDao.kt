package org.ivanivorontsov.healthbank.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.ivanivorontsov.healthbank.data.entity.CardEntity

@Dao
interface CardDao {
    @Query("SELECT * FROM cards ORDER BY name")
    fun observeAll(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getById(id: String): CardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CardEntity>)

    @Update
    suspend fun update(card: CardEntity)

    @Query("UPDATE cards SET isFrozen = :frozen WHERE id = :id")
    suspend fun setFrozen(id: String, frozen: Boolean)
}
