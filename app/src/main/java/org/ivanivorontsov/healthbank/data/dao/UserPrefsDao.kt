package org.ivanivorontsov.healthbank.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.ivanivorontsov.healthbank.data.entity.UserPrefsEntity

@Dao
interface UserPrefsDao {
    @Query("SELECT * FROM user_prefs WHERE id = 1")
    fun observe(): Flow<UserPrefsEntity?>

    @Query("SELECT * FROM user_prefs WHERE id = 1")
    suspend fun get(): UserPrefsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(prefs: UserPrefsEntity)
}
