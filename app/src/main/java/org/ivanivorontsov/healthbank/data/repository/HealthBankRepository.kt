package org.ivanivorontsov.healthbank.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.ivanivorontsov.healthbank.data.db.AppDatabase
import org.ivanivorontsov.healthbank.data.entity.*
import java.security.MessageDigest
import java.util.UUID

class HealthBankRepository(private val db: AppDatabase) {
    private val accounts = db.accountDao()
    private val txs = db.transactionDao()
    private val catalog = db.catalogDao()
    private val cards = db.cardDao()
    private val prefs = db.userPrefsDao()

    fun observeAccounts() = accounts.observeAll()
    fun observeTotalBalance() = accounts.observeTotalBalance().map { it ?: 0L }
    fun observeTransactions() = txs.observeAll()
    fun observeTransactionsByAccount(id: String) = txs.observeByAccount(id)
    fun searchTransactions(q: String) = if (q.isBlank()) txs.observeAll() else txs.search(q)
    fun observeCatalog() = catalog.observeAll()
    fun observeCards() = cards.observeAll()
    fun observePrefs() = prefs.observe()

    suspend fun getAccount(id: String) = accounts.getById(id)
    suspend fun getTransaction(id: String) = txs.getById(id)
    suspend fun getPrefs() = prefs.get() ?: UserPrefsEntity()

    suspend fun completeOnboarding(displayName: String, pin: String) {
        prefs.upsert(
            UserPrefsEntity(
                displayName = displayName.trim(),
                pinHash = hashPin(pin),
                onboardingDone = true
            )
        )
    }

    suspend fun changePin(oldPin: String, newPin: String): Boolean {
        val p = getPrefs()
        if (p.pinHash != hashPin(oldPin)) return false
        prefs.upsert(p.copy(pinHash = hashPin(newPin)))
        return true
    }

    suspend fun verifyPin(pin: String): Boolean =
        getPrefs().pinHash == hashPin(pin)

    suspend fun updateNotifyPrefs(ops: Boolean, insights: Boolean) {
        val p = getPrefs()
        prefs.upsert(p.copy(notifyOps = ops, notifyInsights = insights))
    }

    suspend fun updateDisplayName(name: String) {
        val p = getPrefs()
        prefs.upsert(p.copy(displayName = name.trim()))
    }

    /** Провести операцию из каталога — зачисляет номинал на целевой счёт */
    suspend fun performOperation(catalogId: String, accountId: String? = null): TransactionEntity? {
        val op = catalog.getById(catalogId) ?: return null
        val targetAccount = accountId ?: op.defaultAccountId
        val acc = accounts.getById(targetAccount) ?: return null
        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            accountId = targetAccount,
            title = op.title,
            category = op.category,
            amount = op.nominalValue,
            type = "CREDIT",
            status = "COMPLETED",
            icon = op.icon,
            note = "Операция каталога: ${op.title}"
        )
        accounts.adjustBalance(targetAccount, op.nominalValue)
        txs.insert(tx)
        return tx
    }

    /** Перевод между своими счетами */
    suspend fun transfer(fromId: String, toId: String, amount: Long, note: String? = null): Pair<TransactionEntity, Long>? {
        if (fromId == toId || amount <= 0) return null
        val from = accounts.getById(fromId) ?: return null
        val to = accounts.getById(toId) ?: return null
        if (from.balance < amount) return null
        val tx = TransactionEntity(
            id = UUID.randomUUID().toString(),
            accountId = fromId,
            counterAccountId = toId,
            title = "Перевод: ${from.name} → ${to.name}",
            category = "Переводы",
            amount = amount,
            type = "TRANSFER",
            status = "COMPLETED",
            icon = "swap_horiz",
            note = note
        )
        accounts.adjustBalance(fromId, -amount)
        accounts.adjustBalance(toId, amount)
        txs.insert(tx)
        val newFromBalance = from.balance - amount
        return tx to newFromBalance
    }

    suspend fun setCardFrozen(cardId: String, frozen: Boolean) {
        cards.setFrozen(cardId, frozen)
    }

    suspend fun updateCardLimit(cardId: String, limit: Long) {
        val c = cards.getById(cardId) ?: return
        cards.update(c.copy(monthlyLimit = limit))
    }

    suspend fun transactionsInRange(from: Long, to: Long) = txs.getInRange(from, to)

    fun accountStatementText(account: AccountEntity, list: List<TransactionEntity>): String {
        val sb = StringBuilder()
        sb.appendLine("Выписка: ${account.name}")
        sb.appendLine("Баланс: ${account.balance} ЗДР")
        sb.appendLine("—".repeat(28))
        list.forEach { t ->
            val sign = when (t.type) {
                "DEBIT" -> "-"
                "TRANSFER" -> if (t.accountId == account.id) "-" else "+"
                else -> "+"
            }
            sb.appendLine("${formatDate(t.createdAt)}  $sign${t.amount} ЗДР  ${t.title}  [${t.status}]")
        }
        return sb.toString()
    }

    private fun hashPin(pin: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(pin.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    private fun formatDate(ms: Long): String {
        val sdf = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale("ru"))
        return sdf.format(java.util.Date(ms))
    }
}
