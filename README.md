# NaydiVesch — НайдиВещь

Android-приложение для поиска потерянных вещей. Написано на Kotlin + Jetpack Compose.

## 📱 Стек

| Компонент | Версия |
|-----------|--------|
| Kotlin | 2.0.0 |
| Jetpack Compose | 2024.08.00 (Material 3) |
| Android Gradle Plugin | 8.5.0 |
| Gradle | 8.7 |
| minSdk | 24 |
| targetSdk / compileSdk | 34 |
| Java | 17 |

## 🚀 CI/CD

Два workflow в `.github/workflows/`:

### `ci-debug.yml` — Debug сборка
- Триггеры: `push` / `pull_request` в `main`, `workflow_dispatch`
- Собирает: `assembleDebug` → `app-debug.apk`
- Артефакт: `NaydiVesch-Debug-APK.zip` (30 дней хранения)

### `release.yml` — Release сборка
- Триггеры: теги `v*` (например, `v123`), `workflow_dispatch`
- Собирает: `assembleRelease` + `bundleRelease` → подписанные `app-release.apk` + `app-release.aab`
- Версионирование:
  - `versionCode` = количество коммитов в `main` (авто)
  - `versionName` = тег (например, `v123`) или ручной ввод
- Артефакт: `NaydiVesch-Release-v{version}.zip` (90 дней)
- Автоматически создаёт **GitHub Release** при пуше тега

## 🔐 Подписание (Release)

Keystore: **PKCS12** (`app/keystore/release.p12`), alias `naydivesch`, RSA 2048.

**Не коммитится в репозиторий** — только через GitHub Secrets:

| Secret | Описание |
|--------|----------|
| `KEYSTORE_BASE64` | Base64 содержимое `release.p12` |
| `KEY_ALIAS` | `naydivesch` |
| `STORE_PASSWORD` | Пароль keystore |
| `KEY_PASSWORD` | Пароль ключа |

> **Важно:** пароли keystore сгенерированы случайно и записаны в локальном файле `app/keystore/RELEASE_CREDENTIALS.txt` (в `.gitignore`). Запишите их в безопасном месте.

### Как добавить секреты в GitHub
1. Settings → Secrets and variables → Actions → New repository secret
2. Добавьте все 4 секрета из `app/keystore/RELEASE_CREDENTIALS.txt`

### Как получить `KEYSTORE_BASE64`
```bash
base64 -i app/keystore/release.p12 | pbcopy  # macOS
base64 -w0 app/keystore/release.p12 | clip   # Windows
base64 -w0 app/keystore/release.p12 | xclip  # Linux
```

## 🏗 Локальная сборка

### Debug
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Release (требует keystore)
```bash
# Вариант 1: через local.properties (для тестов)
echo "storePassword=YOUR_STORE_PASS" >> app/local.properties
echo "keyPassword=YOUR_KEY_PASS" >> app/local.properties
./gradlew assembleRelease bundleRelease

# Вариант 2: через gradle.properties (рекомендуется)
```

## 📂 Структура проекта

```
NaydiVesch/
├── .github/workflows/
│   ├── ci-debug.yml      # Debug CI
│   └── release.yml       # Release CI
├── app/
│   ├── src/main/
│   │   ├── java/com/example/naydivesch/
│   │   │   └── MainActivity.kt    # Single Activity + Navigation + 3 таба
│   │   ├── res/
│   │   │   ├── values/strings.xml
│   │   │   ├── values/colors.xml
│   │   │   └── values/themes.xml
│   │   └── AndroidManifest.xml
│   ├── keystore/                 # gitignored, release.p12 + credentials
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── .gitignore
└── TECH_SPEC.md                  # Техническая спецификация (живой документ)
```

## 📋 Функционал (MVP)

- **Главная** — приветствие, кнопка перехода к поиску
- **Поиск** — заглушка для NFC / QR / голосового ввода
- **Настройки** — заглушка настроек
- Навигация: Bottom Navigation Bar (3 таба)
- Material 3 тема, поддержка тёмной/светлой темы

## 📦 Релиз

```bash
# Создать тег и запушить — автоматически соберётся release и создастся GitHub Release
git tag v123
git push origin v123
```

Или вручную: Actions → Release → Run workflow → ввести `version_name` (например, `v124`).

---

*Документация актуализируется вместе с кодом. См. `TECH_SPEC.md` для истории решений.*