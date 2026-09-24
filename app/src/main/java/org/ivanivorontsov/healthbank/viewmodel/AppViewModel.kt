package org.ivanivorontsov.healthbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.ivanivorontsov.healthbank.data.entity.*
import org.ivanivorontsov.healthbank.data.repository.HealthBankRepository

class AppViewModel(private val repo: HealthBankRepository) : ViewModel() {
    val prefs: StateFlow<UserPrefsEntity?> = repo.observePrefs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val accounts: StateFlow<List<AccountEntity>> = repo.observeAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalBalance: StateFlow<Long> = repo.observeTotalBalance()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val transactions: StateFlow<List<TransactionEntity>> = repo.observeTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val catalog: StateFlow<List<OperationCatalogEntity>> = repo.observeCatalog()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cards: StateFlow<List<CardEntity>> = repo.observeCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredTx: StateFlow<List<TransactionEntity>> = _searchQuery
        .flatMapLatest { q -> repo.searchTransactions(q) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _lastReceipt = MutableStateFlow<TransactionEntity?>(null)
    val lastReceipt = _lastReceipt.asStateFlow()

    private val _transferPreview = MutableStateFlow<TransferPreview?>(null)
    val transferPreview = _transferPreview.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()

    fun setSearch(q: String) { _searchQuery.value = q }
    fun clearMessage() { _message.value = null }
    fun clearReceipt() { _lastReceipt.value = null }

    fun completeOnboarding(name: String, pin: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.completeOnboarding(name, pin)
            onDone()
        }
    }

    fun verifyPin(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch { onResult(repo.verifyPin(pin)) }
    }

    fun changePin(old: String, new: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = repo.changePin(old, new)
            _message.value = if (ok) "PIN изменён" else "Неверный текущий PIN"
            onResult(ok)
        }
    }

    fun updateNotify(ops: Boolean, insights: Boolean) {
        viewModelScope.launch { repo.updateNotifyPrefs(ops, insights) }
    }

    fun updateName(name: String) {
        viewModelScope.launch { repo.updateDisplayName(name) }
    }

    fun performOperation(catalogId: String, accountId: String? = null) {
        viewModelScope.launch {
            val tx = repo.performOperation(catalogId, accountId)
            if (tx != null) {
                _lastReceipt.value = tx
                _message.value = "Операция проведена: +${tx.amount} ЗДР"
            } else {
                _message.value = "Не удалось провести операцию"
            }
        }
    }

    fun prepareTransfer(fromId: String, toId: String, amount: Long) {
        viewModelScope.launch {
            val from = repo.getAccount(fromId)
            val to = repo.getAccount(toId)
            if (from == null || to == null) {
                _message.value = "Счёт не найден"
                return@launch
            }
            if (amount <= 0) {
                _message.value = "Укажите сумму"
                return@launch
            }
            if (from.balance < amount) {
                _message.value = "Недостаточно средств на счёте"
                return@launch
            }
            _transferPreview.value = TransferPreview(from, to, amount, from.balance - amount, to.balance + amount)
        }
    }

    fun confirmTransfer(onDone: (TransactionEntity) -> Unit) {
        viewModelScope.launch {
            val p = _transferPreview.value ?: return@launch
            val result = repo.transfer(p.from.id, p.to.id, p.amount)
            if (result != null) {
                _lastReceipt.value = result.first
                _transferPreview.value = null
                _message.value = "Перевод выполнен"
                onDone(result.first)
            } else {
                _message.value = "Ошибка перевода"
            }
        }
    }

    fun cancelTransferPreview() { _transferPreview.value = null }

    fun setCardFrozen(id: String, frozen: Boolean) {
        viewModelScope.launch {
            repo.setCardFrozen(id, frozen)
            _message.value = if (frozen) "Карта заморожена" else "Карта разморожена"
        }
    }

    fun updateCardLimit(id: String, limit: Long) {
        viewModelScope.launch {
            repo.updateCardLimit(id, limit)
            _message.value = "Лимит обновлён"
        }
    }

    fun statementFor(accountId: String, onReady: (String) -> Unit) {
        viewModelScope.launch {
            val acc = repo.getAccount(accountId) ?: return@launch
            val list = transactions.value.filter { it.accountId == accountId || it.counterAccountId == accountId }
            onReady(repo.accountStatementText(acc, list))
        }
    }

    suspend fun insights(from: Long, to: Long): InsightData {
        val list = repo.transactionsInRange(from, to)
        val credits = list.filter { it.type == "CREDIT" }.sumOf { it.amount }
        val transfers = list.filter { it.type == "TRANSFER" }.sumOf { it.amount }
        val byCategory = list.filter { it.type == "CREDIT" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
            .toList()
            .sortedByDescending { it.second }
        return InsightData(credits, transfers, list.size, byCategory)
    }

    data class TransferPreview(
        val from: AccountEntity,
        val to: AccountEntity,
        val amount: Long,
        val fromAfter: Long,
        val toAfter: Long
    )

    data class InsightData(
        val totalCredits: Long,
        val totalTransfers: Long,
        val count: Int,
        val byCategory: List<Pair<String, Long>>
    )

    class Factory(private val repo: HealthBankRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AppViewModel(repo) as T
    }
}
