package org.ivanivorontsov.healthbank.data.seed

import org.ivanivorontsov.healthbank.data.entity.*

object SeedData {
    const val ACC_MAIN = "acc_main"
    const val ACC_SPORT = "acc_sport"
    const val ACC_PREVENT = "acc_prevent"
    const val ACC_RECOVERY = "acc_recovery"

    val accounts = listOf(
        AccountEntity(ACC_MAIN, "Основной счёт здоровья", "MAIN", 12_500, icon = "favorite", colorHex = "#0D47A1"),
        AccountEntity(ACC_SPORT, "Спортивный капитал", "SPORT", 4_200, icon = "fitness", colorHex = "#2E7D32"),
        AccountEntity(ACC_PREVENT, "Профилактика", "PREVENT", 3_100, icon = "medical", colorHex = "#6A1B9A"),
        AccountEntity(ACC_RECOVERY, "Восстановление / сон-отдых", "RECOVERY", 2_800, icon = "bedtime", colorHex = "#00838F")
    )

    val catalog = listOf(
        // Workouts
        OperationCatalogEntity("op_run", "Бег", "Тренировки", 150, ACC_SPORT, "directions_run", "Кардио-пробежка"),
        OperationCatalogEntity("op_strength", "Силовая тренировка", "Тренировки", 200, ACC_SPORT, "fitness_center", "Силовые упражнения"),
        OperationCatalogEntity("op_yoga", "Йога", "Тренировки", 120, ACC_SPORT, "self_improvement", "Практика йоги"),
        OperationCatalogEntity("op_swim", "Плавание", "Тренировки", 180, ACC_SPORT, "pool", "Занятие в бассейне"),
        OperationCatalogEntity("op_walk", "Ходьба", "Тренировки", 80, ACC_SPORT, "directions_walk", "Активная ходьба"),
        OperationCatalogEntity("op_bike", "Велосипед", "Тренировки", 160, ACC_SPORT, "pedal_bike", "Велопрогулка"),
        // Checkups
        OperationCatalogEntity("op_checkup_m", "Ежемесячный чекап", "Чекапы", 500, ACC_PREVENT, "health_and_safety", "Плановый осмотр"),
        OperationCatalogEntity("op_dispanser", "Ежегодный диспансер", "Чекапы", 1500, ACC_PREVENT, "assignment", "Полная диспансеризация"),
        OperationCatalogEntity("op_dentist", "Стоматолог", "Чекапы", 400, ACC_PREVENT, "dentistry", "Визит к стоматологу"),
        OperationCatalogEntity("op_eye", "Окулист", "Чекапы", 350, ACC_PREVENT, "visibility", "Проверка зрения"),
        OperationCatalogEntity("op_blood", "Анализы крови", "Чекапы", 300, ACC_PREVENT, "bloodtype", "Лабораторные анализы"),
        // Vaccines
        OperationCatalogEntity("op_flu", "Прививка от гриппа", "Прививки", 600, ACC_PREVENT, "vaccines", "Сезонная вакцинация"),
        OperationCatalogEntity("op_covid", "COVID / бустер", "Прививки", 700, ACC_PREVENT, "coronavirus", "Бустерная доза"),
        OperationCatalogEntity("op_tetanus", "Столбняк", "Прививки", 550, ACC_PREVENT, "vaccines", "Ревакцинация"),
        // Other
        OperationCatalogEntity("op_sleep", "Сон 7–8 ч", "Восстановление", 100, ACC_RECOVERY, "bedtime", "Полноценный сон"),
        OperationCatalogEntity("op_water", "Вода — норма", "Восстановление", 50, ACC_RECOVERY, "water_drop", "Достаточное питьё"),
        OperationCatalogEntity("op_meditate", "Медитация", "Восстановление", 90, ACC_RECOVERY, "spa", "Осознанная практика"),
        OperationCatalogEntity("op_stretch", "Растяжка", "Восстановление", 70, ACC_RECOVERY, "accessibility", "Растяжка мышц"),
        OperationCatalogEntity("op_quit", "Отказ от вредных привычек (день)", "Восстановление", 200, ACC_MAIN, "smoke_free", "День без вредных привычек")
    )

    val cards = listOf(
        CardEntity("card_sport", "Карта «Спорт»", ACC_SPORT, "4821", "#2E7D32", 5_000, 1_200, false, "SPORT"),
        CardEntity("card_prevent", "Карта «Профилактика»", ACC_PREVENT, "7390", "#6A1B9A", 8_000, 900, false, "PREVENT"),
        CardEntity("card_recovery", "Карта «Восстановление»", ACC_RECOVERY, "1055", "#00838F", 3_000, 450, false, "RECOVERY")
    )

    private val now = System.currentTimeMillis()
    private val day = 86_400_000L

    val sampleTransactions = listOf(
        TransactionEntity("tx1", ACC_SPORT, null, "Бег", "Тренировки", 150, "CREDIT", "COMPLETED", "directions_run", createdAt = now - day),
        TransactionEntity("tx2", ACC_RECOVERY, null, "Сон 7–8 ч", "Восстановление", 100, "CREDIT", "COMPLETED", "bedtime", createdAt = now - day * 2),
        TransactionEntity("tx3", ACC_PREVENT, null, "Анализы крови", "Чекапы", 300, "CREDIT", "COMPLETED", "bloodtype", createdAt = now - day * 3),
        TransactionEntity("tx4", ACC_MAIN, ACC_SPORT, "Перевод на Спортивный капитал", "Переводы", 500, "TRANSFER", "COMPLETED", "swap_horiz", createdAt = now - day * 4),
        TransactionEntity("tx5", ACC_SPORT, null, "Силовая тренировка", "Тренировки", 200, "CREDIT", "PENDING", "fitness_center", createdAt = now - day / 2, scheduledAt = now + day),
        TransactionEntity("tx6", ACC_MAIN, null, "Отказ от вредных привычек (день)", "Восстановление", 200, "CREDIT", "COMPLETED", "smoke_free", createdAt = now - day * 5)
    )
}
