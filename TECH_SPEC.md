# NaydiVesch — Техническая спецификация проекта

> Живой документ. Обновляется по мере принятия решений.

---

## 1. Цели проекта

| Приоритет | Цель | Статус |
|-----------|------|--------|
| 1 | **CI/CD pipeline**: автосборка APK при пуше в `main` | 🟡 В процессе |
| 2 | **Release-APK** (подписанный) для распространения | ⏳ Ожидает |
| 3 | Debug-APK для тестирования | ⏳ Ожидает |

---

## 2. Текущий стек

| Компонент | Версия / Детали | Примечание |
|-----------|-----------------|------------|
| **Язык** | Kotlin (нативный) | Миграция с Flutter/Dart завершена |
| **UI** | Jetpack Compose | Декларативный UI, Material 3 |
| **Android Gradle Plugin** | 8.x | Совместим с Java 17 |
| **Kotlin Gradle Plugin** | 2.x | JetBrains |
| **compileSdk** | 34 | |
| **minSdk** | 24 | Совместимость с Compose |
| **targetSdk** | 34 | |
| **Java** | 17 (Temurin) | Для CI |
| **Gradle** | 8.x | Версия подбирается под AGP |
| **OS сборки** | Ubuntu-latest (GitHub Actions) | macOS — нет необходимости (без iOS) |

> **Решение**: нативный Kotlin + Jetpack Compose. Отказ от Flutter, т.к. iOS не нужен, а нативный стек имеет нативную поддержку в GitHub Actions.

---

## 3. Архитектура приложения

```
app/src/main/java/com/example/naydivesch/
├── MainActivity.kt          # Точка входа (Single Activity)
├── ui/
│   ├── theme/               # Тема (Color, Type, Theme)
│   ├── screens/
│   │   ├── HomeScreen.kt    # Главный экран
│   │   ├── SearchScreen.kt  # Поиск вещи
│   │   └── ...
│   └── components/          # Переиспользуемые Compose-компоненты
├── model/                   # Модели данных
├── data/                    # Data layer (Storage, ...)
└── util/                    # Утилиты
```

**Платформы**: Android (основная и единственная)

---

## 4. Требования к CI/CD

### 4.1 Требования к runner'у
- [x] **Ubuntu-latest** (macOS — известные проблемы с Flutter/Dart)
- [x] Docker не используется (проблемы с `flutter:latest` образом)
- [ ] Flutter устанавливается вручную через `wget` + `tar`
- [ ] Android SDK через `android-actions/setup-android@v3`
- [ ] Java 17 через `actions/setup-java@v4`

### 4.2 Шаги пайплайна (для debug-сборки)
1. **Checkout** → `actions/checkout@v4`
2. **Setup Java 17** → `actions/setup-java@v4`
3. **Setup Android SDK** → `android-actions/setup-android@v3` (API 34, NDK)
4. **Create local.properties** → `sdk.dir`, `versionCode` (из количества коммитов в `main`)
5. **Build** → `./gradlew assembleDebug`
6. **Verify APK exists** → `app/build/outputs/apk/debug/app-debug.apk`
7. **Create ZIP** → `NaydiVesch-Debug-APK.zip`
8. **Upload artifact** → `actions/upload-artifact@v4` (retention 30 дней)

### 4.3 Шаги пайплайна (для release-сборки)
1. Те же первые 4 шага (плюс чтение количества коммитов в `main`)
2. **Create local.properties** → `sdk.dir`, `versionCode`, `versionName`
3. **Setup signing** → загрузка `KEYSTORE_BASE64` из GitHub Secrets, распаковка в `android/keystore/release.p12`
4. **Build** → `./gradlew assembleRelease` (APK) + `bundleRelease` (AAB)
5. **Verify APK/AAB exists**
6. **Create ZIP** → `NaydiVesch-Release-APK.zip`
7. **Upload artifact** → `actions/upload-artifact@v4`

### 4.4 Триггеры
- `push` → `main`
- `pull_request` → `main`
- `workflow_dispatch` (ручной запуск)

---

## 5. Подписание (Release-APK) — для цели #2

| Параметр | Значение | Источник |
|----------|----------|----------|
| **Keystore** | `release.keystore` | GitHub Secrets (файл) |
| **Key alias** | `release-key` | GitHub Secrets |
| **Store password** | `STORE_PASSWORD` | GitHub Secrets |
| **Key password** | `KEY_PASSWORD` | GitHub Secrets |
| **signingConfig** | `release` в `android/app/build.gradle` | Настроить при необходимости |

> ⚠️ Keystore **не коммитится** в репозиторий. Только через GitHub Secrets.

---

## 6. Известные проблемы и решения

| Проблема | Решение | Статус |
|----------|---------|--------|
| macOS runner + Flutter = "Bad CPU type" | Использовать только Ubuntu | ✅ Принято |
| `flutter:latest` Docker pull denied | Не использовать Docker, ставить Flutter вручную | ✅ Принято |
| Android SDK licenses not accepted | Явный шаг `echo "..." > licenses/` | ✅ В пайплайне |
| `flutter.gradle` plugin not found | `gradlePluginPortal()` в `settings.gradle` | ✅ Уже в проекте |
| Неверный `local.properties` | Генерировать в CI с правильными путями | ✅ В пайплайне |

---

## 7. Секреты GitHub (для настройки)

| Secret | Назначение | Обязателен для |
|--------|------------|----------------|
| `KEYSTORE_BASE64` | Base64 release.keystore | Release-APK |
| `KEY_ALIAS` | Алиас ключа | Release-APK |
| `STORE_PASSWORD` | Пароль keystore | Release-APK |
| `KEY_PASSWORD` | Пароль ключа | Release-APK |

---

## 8. Чек-лист следующих шагов

- [ ] Определить UI-фреймворк (Compose / XML)
- [ ] Переписать приложение на Kotlin (нативный Android)
- [ ] Создать `.github/workflows/ci.yml` с пайплайном (debug-сборка)
- [ ] Запустить workflow, проверить артефакт
- [ ] Добавить release-сборку с подписью (APK + AAB)
- [ ] Настроить GitHub Secrets для keystore
- [ ] Протестировать release-APK/AAB на устройстве

---

## 9. История решений

| Дата | Решение | Кто принял |
|------|---------|----------|
| 2026-09-25 | Убрать macOS runner, использовать Ubuntu + ручная установка Flutter | Пользователь |
| 2026-09-25 | Не использовать Docker-образ flutter:latest | Пользователь |
| 2026-09-25 | Цель: сначала CI/CD (debug), потом release-APK | Пользователь |
| 2026-09-25 | **Переход на нативный Kotlin** (Android only, без iOS) | Пользователь |
| 2026-09-25 | **Не ставить ПО на Mac** — только облачные решения GitHub | Пользователь |
| 2026-09-25 | **Keystore**: сгенерирован новый PKCS12 (`android/keystore/release.p12`) | Пользователь |
| 2026-09-25 | **Сборка**: APK **и** AAB («оба») | Пользователь |
| 2026-09-25 | **Версионирование**: versionCode = число коммитов в `main` (авто) | Пользователь |
| 2026-09-25 | **UI-фреймворк**: Jetpack Compose (декларативный UI, Material 3) | Пользователь |
| 2026-09-25 | **Стек**: нативный Kotlin + Jetpack Compose, Android only | Пользователь |
| 2026-09-25 | **versionName**: формат `v{commits}` (например, `v123`) — авто-счётчик коммитов в `main` | Пользователь |
| 2026-09-25 | **Публикация**: только APK/AAB для личного использования / друзей / GitHub Releases (без Google Play) | Пользователь |

---

## 10. Открытые вопросы

*(все ключевые вопросы решены — раздел оставлен для будущих решений)*

---

## 11. Решённые вопросы (архив)

### UI-фреймворк (2026-09-25)
- Выбран: **Jetpack Compose** (декларативный UI, Material 3)
- Причина: современный стандарт для новых Android-проектов, меньше кода, активная поддержка Google

### Keystore (2026-09-25)
- Файл: `android/keystore/release.p12` (PKCS12, RSA 2048)
- Alias: `naydivesch`
- Срок: до 18.09.2053
- Пароли: случайные (32 симв.), в `android/keystore/RELEASE_CREDENTIALS.txt` (gitignored)
- `.gitignore` защищает `android/keystore/`, `*.p12`, `*.jks`, `RELEASE_CREDENTIALS*`
- ⚠️ Показаны пользователю в чате 2026-09-25 — записаны вручную

### AAB / APK (2026-09-25)
- Собираем **оба**: `assembleRelease` (APK) для прямого распространения + `bundleRelease` (AAB) для Google Play
- APK и AAB подписываются одним release-keystore

### Версионирование (2026-09-25)
- **versionCode** = количество коммитов в `main` (автоматически, никогда не убывает, не требует ручных действий)
- **versionName** = `v{commits}` (например, `v123`), где `{commits}` = число коммитов в `main` — авто-счётчик

### Публикация (2026-09-25)
- Только личные APK/AAB для себя / друзей / GitHub Releases
- Google Play **не планируется** — никаких AAB в маркет, никакой модерации
- Release-сборка всё равно подписывается release-keystore (для обновлений на устройствах)

---

*Документ обновляется после каждого принятия решения.*