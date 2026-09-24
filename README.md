# Банк здоровья

Мобильное приложение в стиле банковского клиента, где «валюта» — **единицы здоровья (ЗДР)**.  
Учёт тренировок, чекапов, прививок и восстановления с номинальной ценностью. **Не является финансовым продуктом** — реальных денег нет.

**Package:** `org.ivanivorontsov.healthbank`  
**Репозиторий:** https://github.com/ivanivorontsov-sudo/health-bank

## Возможности

- Онбординг и PIN (4–6 цифр)
- Главная: общий баланс, быстрые действия, ожидающие операции
- Счета: Основной, Спортивный, Профилактика, Восстановление
- Переводы между своими счетами с подтверждением и квитанцией
- Каталог операций (тренировки, чекапы, прививки, восстановление) — зачисление ЗДР
- Виртуальные карты здоровья: заморозка, лимиты
- История с поиском и фильтрами
- Аналитика капитала за неделю / месяц / год
- Профиль: имя, смена PIN, локальные предпочтения уведомлений

Данные хранятся локально (Room). Разрешение INTERNET не требуется.

## Установка APK

1. Откройте [Actions](https://github.com/ivanivorontsov-sudo/health-bank/actions) → последний успешный workflow **Build APK**.
2. Скачайте артефакт **`health-bank-apk`**.
3. Установите `health-bank.apk` на Android 8.0+ (API 26), разрешив установку из неизвестных источников при необходимости.

Либо соберите локально:

```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

## Подпись

Сборка debug и release подписывается **стабильным** keystore `keystore/health-bank-upload.jks` (не ephemeral runner `debug.keystore`).  
Подробности и SHA-256: [SIGNING.md](SIGNING.md).

## Стек

Kotlin, Jetpack Compose, Material 3, Room, Navigation, minSdk 26 / targetSdk 35.

## Автор

Ivan Vorontsov · ivanivorontsov-sudo
