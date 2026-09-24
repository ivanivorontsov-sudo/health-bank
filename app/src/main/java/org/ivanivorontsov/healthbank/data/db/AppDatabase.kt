package org.ivanivorontsov.healthbank.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ivanivorontsov.healthbank.data.dao.*
import org.ivanivorontsov.healthbank.data.entity.*
import org.ivanivorontsov.healthbank.data.seed.SeedData

@Database(
    entities = [
        AccountEntity::class,
        TransactionEntity::class,
        OperationCatalogEntity::class,
        CardEntity::class,
        UserPrefsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun transactionDao(): TransactionDao
    abstract fun catalogDao(): CatalogDao
    abstract fun cardDao(): CardDao
    abstract fun userPrefsDao(): UserPrefsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "health_bank.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                get(context).seed()
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }

    private suspend fun seed() {
        accountDao().insertAll(SeedData.accounts)
        catalogDao().insertAll(SeedData.catalog)
        cardDao().insertAll(SeedData.cards)
        transactionDao().insertAll(SeedData.sampleTransactions)
        userPrefsDao().upsert(UserPrefsEntity())
    }
}
