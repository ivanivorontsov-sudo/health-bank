package org.ivanivorontsov.healthbank.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.ivanivorontsov.healthbank.data.entity.OperationCatalogEntity

@Dao
interface CatalogDao {
    @Query("SELECT * FROM operation_catalog ORDER BY category, title")
    fun observeAll(): Flow<List<OperationCatalogEntity>>

    @Query("SELECT * FROM operation_catalog WHERE category = :category ORDER BY title")
    fun observeByCategory(category: String): Flow<List<OperationCatalogEntity>>

    @Query("SELECT * FROM operation_catalog WHERE id = :id")
    suspend fun getById(id: String): OperationCatalogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<OperationCatalogEntity>)
}
