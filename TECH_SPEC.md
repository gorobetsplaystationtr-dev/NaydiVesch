# NaydiVesch — Техническая спецификация проекта

> Живой документ. Обновляется по мере принятия решений.

---

## 1. Цели проекта

✅ **Все базовые цели достигнуты (2026-09-26):**

| Приоритет | Цель | Статус |
|-----------|------|--------|
| 1 | **CI/CD pipeline**: автосборка APK при пуше в `main` | ✅ **Готово** |
| 2 | **Release-APK** (подписанный) для распространения | ✅ **Готово** |
| 3 | Debug-APK для тестирования | ✅ **Готово** |

---

## 2. Текущий стек

| Компонент | Версия / Детали | Примечание |
|-----------|-----------------|------------|
| **Язык** | Kotlin (нативный) | Миграция с Flutter/Dart завершена |
| **UI** | Jetpack Compose | Декларативный UI, Material 3 |
| **Android Gradle Plugin** | 8.5.0 | Совместим с Java 17 |
| **Kotlin Gradle Plugin** | 2.0.0 | JetBrains |
| **compileSdk** | 34 | |
| **minSdk** | 24 | Совместимость с Compose |
| **targetSdk** | 34 | |
| **Java** | 17 (Temurin) | Для CI |
| **Gradle** | 8.7 | Версия подбирается под AGP |
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
│   │   └── SettingsScreen.kt # Настройки
│   └── components/          # Переиспользуемые Compose-компоненты
├── model/                   # Модели данных (пока пусто)
├── data/                    # Data layer (Storage, ...) (пока пусто)
└── util/                    # Утилиты (пока пусто)
```

**Платформы**: Android (основная и единственная)

---

## 4. CI/CD Pipeline — ИТОГОВЫЙ ВАРИАНТ

### 4.1 Workflows

#### `ci-debug.yml` — Debug сборка
- **Триггеры**: `push` / `pull_request` в `main`, `workflow_dispatch`
- **Собирает**: `assembleDebug` → `app-debug.apk`
- **Артефакт**: `NaydiVesch-Debug-APK.zip` (30 дней хранения)

#### `release.yml` — Release сборка
- **Триггеры**: теги `v*` (например, `v123`), `workflow_dispatch`
- **Собирает**: `assembleRelease` + `bundleRelease` → подписанные `app-release.apk` + `app-release.aab`
- **Версионирование**:
  - `versionCode` = количество коммитов в `main` (авто)
  - `versionName` = тег (например, `v123`) или ручной ввод
- **Артефакт**: `NaydiVesch-Release-v{version}.zip` (90 дней)
- **Автоматически создаёт GitHub Release** при пуше тега

### 4.2 Требования к runner'у
- Ubuntu-latest (macOS — известные проблемы)
- Java 17 через `actions/setup-java@v4` (Temurin)
- Android SDK через `android-actions/setup-android@v3` (API 34, build-tools 34.0.0)
- Docker не используется
- Gradle кэш через `actions/setup-java@v4` cache

### 4.3 Ключевые фиксы в пайплайне

| Проблема | Решение |
|----------|---------|
| `git rev-list --count main` падает на tag push (нет локальной ветки main) | Добавлен `git fetch origin main:main 2>/dev/null \|\| git fetch origin refs/heads/main:refs/remotes/origin/main` перед подсчётом |
| Путь к keystore в модуле app | Использован `rootProject.rootDir` вместо `project.rootDir` |
| Конфликт signingConfig (Gradle DSL vs CLI injection) | Убран CLI injection, оставлен `signingConfig` в `build.gradle.kts` с `System.getenv()` |
| Пароль ключа ≠ пароль хранилища | `KEY_PASSWORD` в GitHub Secrets = `STORE_PASSWORD` (совпадают) |

### 4.4 Структура release.yml

```yaml
jobs:
  build-release:
    runs-on: ubuntu-latest
    permissions:
      contents: write
    steps:
      - Checkout (fetch-depth: 0)
      - Setup Java 17 (Temurin, gradle cache)
      - Setup Android SDK (API 34)
      - Calculate versionCode (commits in main)
      - Create local.properties (sdk.dir, versionCode, versionName)
      - Setup Release Keystore (base64 decode from KEYSTORE_BASE64 secret)
      - Build: ./gradlew assembleRelease bundleRelease
      - Verify APK + AAB exist
      - Create ZIP artifact
      - Upload artifact (90 days)
      - Create GitHub Release (softprops/action-gh-release@v1)
```

---

## 5. Подписание (Release-APK)

| Параметр | Значение | Источник |
|----------|----------|----------|
| **Keystore** | `app/keystore/release.p12` (PKCS12, RSA 2048) | GitHub Secrets (`KEYSTORE_BASE64`) |
| **Key alias** | `naydivesch` | GitHub Secrets (`KEY_ALIAS`) |
| **Store password** | `eexdfWts#^gPXGi4HUCm7%Hf&O*-ed*K` | GitHub Secrets (`STORE_PASSWORD`) |
| **Key password** | `eexdfWts#^gPXGi4HUCm7%Hf&O*-ed*K` | GitHub Secrets (`KEY_PASSWORD`) |

> ⚠️ Keystore **не коммитится** в репозиторий. Только через GitHub Secrets.
> Локальный файл: `app/keystore/RELEASE_CREDENTIALS.txt` (в `.gitignore`).

### Как обновить секреты в GitHub
```bash
# 1. Получить base64 keystore
base64 -i app/keystore/release.p12 | pbcopy  # macOS

# 2. Settings → Secrets and variables → Actions → New repository secret
# Добавить/обновить 4 секрета:
# KEYSTORE_BASE64, KEY_ALIAS, STORE_PASSWORD, KEY_PASSWORD
```

---

## 6. Версионирование

- **versionCode** = количество коммитов в `main` (автоматически, никогда не убывает)
- **versionName** = `v{commits}` (например, `v17`), где `{commits}` = число коммитов в `main`

Пример: 18 коммитов в main → `versionCode=18`, тег `v17` → `versionName=v17`

---

## 7. Публикация

- Только личные APK/AAB для себя / друзей / GitHub Releases
- Google Play **не планируется** — никакой модерации
- Release-сборка подписывается release-keystore (для обновлений на устройствах)

---

## 8. История решений (дополнено 2026-09-26)

| Дата | Решение | Кто принял |
|------|---------|----------|
| 2026-09-26 | **CI/CD полностью настроен и работает** — debug + release пайплайны | Пользователь + Агент |
| 2026-09-26 | **Release v17 успешно собран и опубликован** в GitHub Releases | Пользователь + Агент |
| 2026-09-26 | **Пароль ключа = пароль хранилища** для избежания padding errors | Агент (исправление) |
| 2026-09-26 | **rootProject.rootDir** для путей к keystore из модуля app | Агент (исправление) |
| 2026-09-26 | **git fetch main** перед подсчётом коммитов на tag push | Агент (исправление) |
| 2026-09-26 | **Убран CLI injection подписи**, оставлен signingConfig в build.gradle.kts | Агент (исправление) |

---

## 9. Структура репозитория (актуальная)

```
NaydiVesch/
├── .github/workflows/
│   ├── ci-debug.yml      # Debug CI (push/PR main)
│   └── release.yml       # Release CI (tags v*)
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
├── README.md
└── TECH_SPEC.md                  # Этот файл
```

---

## 10. Prompt для нового ИИ-агента (продолжение разработки)

> **СКОПИРУЙ ЭТОТ ПРОМТ И ОТПРАВЬ НОВОМУ АГЕНТУ:**

---

### ПРОМТ ДЛЯ НОВОГО АГЕНТА

```
Ты — Android-разработчик, продолжаешь разработку приложения **NaydiVesch** (НайдиВещь) — поиск потерянных вещей.

## Контекст
- Репозиторий: https://github.com/gorobetsplaystationtr-dev/NaydiVesch
- Локально: `/Users/papa/NaydiVesch`
- Стек: Kotlin 2.0, Jetpack Compose (Material 3), minSdk 24, targetSdk 34
- Архитектура: Single Activity + Navigation Component + 3 таба (Home, Search, Settings)
- CI/CD: **Полностью настроен и работает** (GitHub Actions, Ubuntu-latest)

## Что уже готово ✅
1. **Код приложения**: 3 экрана (Home с кнопкой перехода к поиску, Search-заглушка, Settings-заглушка)
2. **Навигация**: Bottom Navigation Bar, Compose Navigation
3. **Тема**: Material 3, светлая/тёмная
4. **Keystore**: PKCS12 (`app/keystore/release.p12`), alias `naydivesch`, пароли в GitHub Secrets
5. **CI/CD**:
   - `ci-debug.yml` — debug APK при push/PR в main
   - `release.yml` — release APK + AAB при пуше тега `v*`
   - Авто-версионирование: versionCode = коммиты в main, versionName = тег
   - GitHub Release создаётся автоматически
6. **Последний релиз**: v17 (versionCode=18) — собран, подписан, загружен в GitHub Releases

## Задача
Разработать **функциональность поиска вещей** (экран SearchScreen):
- NFC-сканирование меток
- QR-код сканирование (CameraX / ZXing)
- Голосовой ввод (SpeechRecognizer)
- Локальное хранение найденных вещей (DataStore / Room)
- Синхронизация с облаком (опционально, позже)

## Как выпустить новый релиз
```bash
# 1. Разработать фичу в ветке, сделать PR в main
# 2. После мерджа в main:
git tag v{N}        # где N = номер следующего релиза (коммитов в main)
git push origin v{N}
# 3. GitHub Actions автоматически:
#    - соберёт signed APK + AAB
#    - создаст GitHub Release с артефактами
#    - APK будет готов к установке
```

## Важные файлы для работы
- `app/build.gradle.kts` — signingConfig использует env vars (STORE_PASSWORD, KEY_PASSWORD)
- `.github/workflows/release.yml` — release пайплайн
- `TECH_SPEC.md` — эта спецификация (актуализируй по ходу дела)
- `app/keystore/RELEASE_CREDENTIALS.txt` — пароли (локально, не в git)

## Правила
- Не ломай CI/CD — он работает
- Менять версионирование не нужно (автоматическое)
- Keystore не коммитить, только через GitHub Secrets
- Писать код на Kotlin + Compose, следовать Material 3
- Обновлять TECH_SPEC.md при принятии архитектурных решений
```

---

## 11. Чек-лист для следующей сессии

- [x] Прочитать этот TECH_SPEC.md
- [x] Клонировать репозиторий
- [x] Проверить, что CI/CD работает (запушить тестовый тег или PR)
- [x] **Добавлены Compose UI экраны (v18):**
  - [x] ThingsScreen — карточки вещей с фото-плейсхолдером, статус-чипами, поиском, FAB
  - [x] LocationsScreen — карточки мест с QR/NFC бейджами, поиском, FAB
  - [x] LinkScreen — ExposedDropdownMenu для выбора вещи/места, тип связи (radiobuttons), заметки
  - [x] 19+ Composable-компонентов в Screens.kt
  - [x] Material 3 тема (цвета, типографика)
  - [x] 5 табов в Bottom Navigation (Главная, Вещи, Поиск, Связи, Настройки)
  - [x] Dialog формы для добавления/редактирования вещей и мест
  - [x] ConfirmDeleteDialog
  - [x] EmptyState для пустых списков
  - [x] Подписанный APK генерируется через GitHub Actions (тег v18)

- [ ] При необходимости: реализовать NFC-сканирование и QR-код сканирование в LocationForm
- [ ] При необходимости: добавить CameraX для QR-сканирования
- [ ] Добавить сохранение данных при рестарте (костыль с MutableStateFlow → реальная работа с Room)

---

*Документ обновлён 2026-10-03 после добавления Compose UI экранов.*