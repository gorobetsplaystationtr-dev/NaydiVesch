# Integration Log: NaydiVesch Android App v0.8.0

**Дата:** 2026-10-03
**Релиз:** v0.8.0
**Статус:** [STATUS: READY_FOR_INTEGRATION]

---

## TL;DR

Android-приложение NaydiVesch (НайдиВещь) v0.8.0 с нативным Kotlin + Jetpack Compose, Room, Retrofit, DataStore. Синхронизация с Flask-backend (v0.7.1) на Synology. 5 экранов: Home, Things, Locations, Link, Settings. CI/CD через GitHub Actions, подписанный APK/AAB.

---

## Архитектура

### Стек
- **Язык**: Kotlin 2.0
- **UI**: Jetpack Compose (Material 3)
- **БД**: Room 2.6.1 (локальная) + PostgreSQL (удалённая, через Retrofit)
- **Сеть**: Retrofit 2.11 + OkHttp 4.12
- **Кэш**: DataStore Preferences 1.1.2
- **Компоненты**: Navigation 2.8.3, ViewModel 2.8.0

### Схема БД (Room)

```kotlin
@Entity
data class Thing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String? = null,
    val category: String? = null,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String? = null,
    val nfcUid: String? = null,
    val qrCodeUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class ThingLocationLink(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val thingId: Int,
    val locationId: Int,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
```

### API Endpoints (Flask-backend)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/things` | Список вещей |
| POST | `/things` | Добавить вещь |
| GET | `/things/<id>` | Детали вещи |
| PUT | `/things/<id>` | Обновить вещь |
| DELETE | `/things/<id>` | Удалить вещь |
| GET | `/locations` | Список мест |
| POST | `/locations` | Добавить место |
| GET | `/locations/<id>/qr` | QR-код места |
| POST | `/link` | Привязка вещи к месту |
| GET | `/links` | Активные связи |

---

## Файлы проекта

```
app/src/main/java/com/example/naydivesch/
├── MainActivity.kt              # Точка входа, навигация (5 табов)
├── data/
│   ├── api/
│   │   ├── ApiClient.kt         # Retrofit клиент (BASE_URL=http://192.168.2.12:8080/api/)
│   │   └── NaydiVeschApi.kt     # API интерфейс
│   ├── dao/
│   │   ├── ThingDao.kt
│   │   ├── LocationDao.kt
│   │   └── LinkDao.kt
│   ├── db/
│   │   ├── AppDatabase.kt
│   │   └── converters/DateConverter.kt
│   ├── model/
│   │   ├── Thing.kt
│   │   ├── Location.kt
│   │   └── Link.kt
│   ├── repository/
│   │   └── NaydiVeschRepository.kt
│   └── store/
│       ├── SettingsDataStore.kt
│       └── SyncDataStore.kt
├── ui/
│   ├── screens/
│   │   ├── ThingsScreen.kt      # Список вещей с фото
│   │   ├── LocationsScreen.kt   # Список мест с NFC/QR
│   │   ├── LinkScreen.kt        # Привязка вещи к месту
│   │   └── EmptyState.kt
│   ├── viewmodel/
│   │   ├── ThingsViewModel.kt
│   │   ├── LocationsViewModel.kt
│   │   └── LinkViewModel.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Typography.kt
```

---

## CI/CD

### Workflows
- `ci-debug.yml` — Debug APK при push/PR в main
- `release.yml` — Release APK + AAB при пуше тега `v*`

### Secrets (GitHub)
- `KEYSTORE_BASE64` — Base64 PKCS12 keystore
- `STORE_PASSWORD` — пароль хранилища
- `KEY_PASSWORD` — пароль ключа (= STORE_PASSWORD)
- `KEY_ALIAS` — alias ключа (`naydivesch`)

### Версионирование
- `versionCode` = количество коммитов в main
- `versionName` = тег (например, `v19`)

---

## Зависимости (build.gradle.kts)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("kotlin-android")
    id("kotlin-kapt")  // Важно! Для Room
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.08.00"))
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.navigation:navigation-compose:2.8.3")
    
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Network
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Data
    implementation("androidx.datastore:datastore-preferences:1.1.2")
}
```

---

## Деплой

```bash
# Клонирование
git clone git@github.com:gorobetsplaystationtr-dev/NaydiVesch.git

# Создание тега для release
git tag v19
git push origin v19

# Проверка GitHub Actions
# https://github.com/gorobetsplaystationtr-dev/NaydiVesch/actions
```

APK будет доступен в GitHub Releases после завершения workflow.

---

## Известные ограничения

1. **HTTP cleartext** — `usesCleartextTraffic="true"` для работы с локальным Flask-backend (без HTTPS)
2. **NFC** — требует физического устройства (не работает на эмуляторе)
3. **QR-коды** — генерируются на Flask-side, сохраняются в `/data/photos/`

---

## Метки

- v0.8.0
- Kotlin + Jetpack Compose
- Room + Retrofit + DataStore
- GitHub Actions CI/CD
- Synology Flask-backend

---

[STATUS: READY_FOR_INTEGRATION]
