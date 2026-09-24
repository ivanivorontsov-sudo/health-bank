# Подпись APK — Банк здоровья

## Стабильный keystore (не ephemeral debug.keystore)

| Параметр | Значение |
|----------|----------|
| Файл | `keystore/health-bank-upload.jks` |
| Alias | `healthbank` |
| storePassword / keyPassword | `healthbank2026` (см. `keystore.properties`) |
| Алгоритм | RSA 2048 |
| Срок | ~10000 дней |

## SHA-256 отпечаток сертификата

```
5B:7F:5F:CA:B9:A1:4F:52:C6:A3:3E:F5:62:3C:6B:8B:52:A0:FB:EC:CF:30:15:33:8D:73:12:B8:6E:3F:44:D0
```

Проверка локально:

```bash
keytool -list -v -keystore keystore/health-bank-upload.jks -storepass healthbank2026 -alias healthbank
```

## Gradle

`signingConfigs.upload` подключён и к **debug**, и к **release**, поэтому `./gradlew assembleDebug` и CI всегда используют этот keystore — не `~/.android/debug.keystore`.

Пароли лежат в репозитории намеренно: публичное персональное приложение. Для продакшена вынесите секреты в GitHub Secrets / локальный `keystore.properties` вне git.
