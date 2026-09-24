package org.ivanivorontsov.healthbank.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.ivanivorontsov.healthbank.data.entity.TransactionEntity

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE accountId = :accountId OR counterAccountId = :accountId ORDER BY createdAt DESC")
    fun observeByAccount(accountId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE status = :status ORDER BY createdAt DESC")
    fun observeByStatus(status: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE title LIKE '%' || :q || '%' OR category LIKE '%' || :q || '%' ORDER BY createdAt DESC")
    fun search(q: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: String): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE createdAt >= :from AND createdAt <= :to ORDER BY createdAt DESC")
    suspend fun getInRange(from: Long, to: Long): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<TransactionEntity>)

    @Update
    suspend fun update(tx: TransactionEntity)
}
