package org.ivanivorontsov.healthbank

import android.app.Application
import org.ivanivorontsov.healthbank.data.db.AppDatabase
import org.ivanivorontsov.healthbank.data.repository.HealthBankRepository

class HealthBankApp : Application() {
    lateinit var repository: HealthBankRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.get(this)
        repository = HealthBankRepository(db)
    }
}
