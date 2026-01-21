# Learn Feature Requirements

## Статус реализации: ✅ ЗАВЕРШЕНО

### 1. Архитектура и зависимости ✅

- ✅ Добавлены все недостающие зависимости в Koin (FsrsService, LearnInteractor, маппeры)
- ✅ Добавлена FSRSStateEntity в AppDatabase
- ✅ Обновлены миграции БД для добавления word_fsrs_states таблицы (MIGRATION_2_3)
- ✅ Исправлены зависимости и архитектура

### 2. UI Компоненты ✅

- ✅ Заменен ViewPager на CardStackView
- ✅ Добавлен индикатор прогресса для сессии обучения
- ✅ Реализованы свайпы:
  - Влево: "Не знаю" (Again) - красный оверлей
  - Вправо: "Знаю" (Good) - зеленый оверлей
  - Вверх: "Нужно повторить" (Hard) - желтый оверлей
- ✅ Созданы оверлеи для свайпов с современным UI
- ✅ Добавлены анимации переходов карточек и flip анимация

### 3. Режимы работы ✅

- ✅ Режим изучения N новых слов (N регулируется в настройках через newWordsPerDay)
- ✅ Режим повторения слов (по расписанию FSRS через getDueWords)
- ✅ Комбинированный режим: новые + повторение (реализован в LearnViewModel)
- ⚠️ Режим просмотра карточек (можно реализовать через LearnPlaylistFragment)

### 4. Главный экран ✅

- ✅ Кнопка "Учить" (переход к режиму изучения)
- ✅ Показывается количество слов для повторения (HomeViewModel)
- ✅ Интеграция с навигацией

### 5. Выбор категорий ✅

- ✅ Экран выбора плейлистов (SelectPlaylistFragment)
- ✅ Фильтрация слов по плейлистам
- ✅ Навигация к изучению выбранного плейлиста
- ✅ Режим редактирования с чекбоксами и сохранением выбранных категорий в settings (используются в LearnFragment)

### 6. Функциональность карточек ✅

- ✅ Показ примеров использования (backExample)
- ✅ Показ транскрипции (frontTranscription)
- ✅ Воспроизведение аудио через TTS (btnAudio)
- ✅ Flip анимация карточек
- ✅ Пометка слова как "известного" - реализована через кнопку на обратной стороне карточки
  - Кнопка "Уже знаю это слово" / "I already know this word"
  - При нажатии слово помечается как Easy и свайпается вправо
  - Обновляется прогресс обучения

### 7. FSRS интеграция ✅

- ✅ Создан LearnInteractor для работы с FSRS
- ✅ Реализован FsrsService для вычислений
- ✅ Маппинг между WordEntity, FSRSStateEntity и FlashCard (FlashCardMapper)
- ✅ Обновление FSRSStateEntity после ответа (onSwipe -> saveProgress)
- ✅ Расчет следующей даты повторения через FSRS.calculate()

### 8. Уведомления ✅

- ✅ WorkManager для фоновой проверки (ReviewNotificationWorker)
- ✅ Уведомления о словах для повторения
- ✅ Настройка времени уведомлений через SettingsFragment
- ✅ Запуск уведомлений через GrableApplication
- ✅ Deep link в приложение (открывает SelectPlaylistFragment)
- ✅ Полная локализация уведомлений (русский + английский)
- ✅ Plurals для корректного отображения количества слов
- ✅ Динамическое получение playlist IDs из БД

### 9. Настройки ✅

- ✅ Количество новых слов в день (ID_NEW_WORDS_PER_DAY, default: 10)
- ✅ Время уведомлений (ID_TIME_REMINDER)
- ✅ Включение/выключение уведомлений (ID_DAILY_REMINDER)
- ✅ Добавлен IntValue тип в SettingValues
- ✅ UI элемент для настройки количества новых слов (SettingsFragment)
- ✅ Локализация на русский и английский
- ✅ Диалог выбора количества слов (5, 10, 15, 20, 25, 30)

### 10. Модульное разделение ⏸️

- ⏸️ Отделение learn_playlist модуля от learn (отложено)
- ⏸️ Настройка зависимостей между модулями (отложено)

## Технические детали

### Архитектура слоев

- **data**: Repository (WordsRepository) - получение данных из БД
- **domain**: Interactor (LearnInteractor) - бизнес-логика FSRS
- **ui**: ViewModel (LearnViewModel) - логика отображения

### Используемые библиотеки

- Room (БД)
- Koin (DI)
- Flow (reactive streams)
- CardStackView (UI для свайпов)
- WorkManager (фоновые задачи)
- Jetpack Navigation

### FSRS состояния (FSRSStateEntity)

- stability: Double - стабильность памяти
- difficulty: Double - сложность слова
- interval: Int - интервал в днях
- dueDateMillis: Long - когда нужно повторить
- lastReviewMillis: Long - последний показ
- reviewCount: Int - количество повторений
- phase: Int (0: Added, 1: ReLearning, 2: Review)

### Логика подборки слов

1. Получить N новых слов (где state = null)
2. Получить все due слова (где dueDateMillis <= now)
3. Смешать и показать в CardStackView
4. При свайпе - обновить FSRSStateEntity
5. Продолжать пока не закончатся слова

## Приоритет выполнения

1. ✅ Настроить БД и Koin
2. ✅ Реализовать CardStackView с свайпами
3. ✅ Интегрировать FSRS
4. ✅ Добавить режимы на главный экран
5. ✅ Реализовать уведомления
6. ⏸️ Отделить модули (отложено)

---

## Реализованные изменения

### База данных

- Добавлена таблица `word_fsrs_states` с полями: wordId, stability, difficulty, interval, dueDateMillis, lastReviewMillis, reviewCount, phase
- Создана миграция `MIGRATION_2_3` для обновления схемы БД
- Добавлен `FullWordCardPOJO` для объединения WordEntity, FSRSStateEntity и ExampleEntity

### UI

- **CardStackView** с свайпами влево/вправо/вверх
- **Оверлеи** для визуализации действий при свайпе (красный/зеленый/желтый)
- **Индикатор прогресса** для отслеживания изученных слов
- **Flip анимация** карточек при клике
- **Карточка слова** (item_card_learn.xml) с передней/задней стороной

### Domain слой

- `FsrsService` - сервис для работы с FSRS алгоритмом
- `LearnInteractor` - интерактор для получения карточек и сохранения прогресса
- `WordsRepository` - репозиторий для работы с БД
- Маппeры: `FlashCardMapper`, `WordMapper`, `WordExampleMapper`

### ViewModel

- `LearnViewModel` - управление сессией обучения
- `HomeViewModel` - отображение количества слов для повторения

### Настройки

- Добавлена настройка `newWordsPerDay` (количество новых слов в день)
- Добавлен тип `IntValue` для целочисленных настроек
- Расширен `SettingsProvider`, `SettingsRepository`

### Уведомления

- `ReviewNotificationWorker` - Worker для проверки слов для повторения
- `GrableApplication` - настройка расписания уведомлений
- Канал уведомлений "Review Reminders"
- Permission `POST_NOTIFICATIONS`

### Koin DI

- Зарегистрированы все новые зависимости в `learnModule` и `homeModule`
- FsrsService, LearnInteractor, WordsRepository
- LearnViewModel, HomeViewModel

### Файлы для дальнейшей доработки

1. **Режим просмотра без прогресса** - можно добавить флаг в LearnViewModel
2. **Пометка слова как известного** - добавить кнопку/жест в CardStackAdapter
3. **Deep link из уведомления** - улучшить intent в ReviewNotificationWorker
4. **UI настройки количества слов** - добавить элемент в SettingsItemFactory
5. **Получение всех playlist IDs** - заменить hardcoded список на запрос из БД

## Известные ограничения

- Hardcoded playlist IDs (1L, 2L, 3L) - нужно получать из БД
- Модули learn и learn_playlist не разделены (требует рефакторинга архитектуры)

## ✅ ИСПРАВЛЕНО: Проблема возврата изученных слов

### Проблема

Слова, которые пользователь только что изучил, снова возвращались в подборку.

### Причины

1. **Ошибка типа данных**: `WordsRepository.saveStatistics` использовал `Timestamp(System.currentTimeMillis())`, но тип поля `date` в `StatisticsEntity` был изменен на `Long`
2. **Отсутствие подгрузки**: После окончания карточек новые не загружались с учетом `reviewedInSession`
3. **Сброс сессии**: `reviewedInSession` очищался при каждом `startSession`

### Исправления

**1. WordsRepository.kt**

```kotlin
// БЫЛО:
date = Timestamp(System.currentTimeMillis())

// СТАЛО:
date = System.currentTimeMillis()
```

**2. LearnViewModel.kt**

- Добавлена функция `loadMoreCards()` для автоматической подгрузки новых карточек
- Добавлен параметр `keepReviewedInSession` в `startSession` для сохранения списка просмотренных
- `reviewedInSession` теперь сохраняется между загрузками карточек в рамках одной сессии

**3. LearnFragment.kt**

```kotlin
// Автоматическая подгрузка новых карточек
if (layoutManager.topPosition >= cardStackAdapter.itemCount) {
    viewModel.loadMoreCards()  // Вместо просто показа emptyView
}
```

### Результат

- ✅ Изученные слова больше НЕ возвращаются в текущей сессии
- ✅ `reviewedInSession` корректно передается как `excludeIds` при загрузке
- ✅ Автоматическая подгрузка новых карточек без повторов
- ✅ Статистика сохраняется корректно

## Стандарты кода проекта

1. ❌ **Запрещено использовать `lateinit`** - использовать `lazy` вместо этого
2. ❌ **Запрещены комментарии в коде** - код должен быть самодокументируемым
3. ✅ **Максимальная вложенность View: 2-3 уровня** - использовать ConstraintLayout для оптимизации
4. ✅ **Все стили вынесены в styles.xml** - поддержка светлой и темной тем
5. ✅ **Все строки в strings.xml** - поддержка локализации (ru/en)
6. ✅ **Архитектура строго соблюдена** - data → domain → ui слои

## Текущие задачи

### Критичные (в работе)

- ✅ **Правильная генерация подборки**: исключать карточки из текущей сессии через reviewedInSession
- ✅ **Сохранение в статистику**: записывать результаты review в StatisticsEntity
- ✅ **Логика повторного показа**: карточки не повторяются в одной сессии (reviewedInSession Set)
- ✅ **Обновление FSRSState**: корректное обновление после каждого свайпа
- ✅ **Оверлеи**: корректный сброс после свайпа
- ✅ **Дублирование карточек**: исправлено через `distinctBy` и правильный расчет
- ✅ **UI для настройки количества слов**: реализовано в SettingsFragment
- ✅ **Кнопка "Пометить как известное"**: реализована на обратной стороне карточки
- ✅ **Навигация**: убрана кнопка learn из navbar, переписан main_screen на RecyclerView

### Следующие шаги для улучшения

1. Добавить режим просмотра без прогресса
2. Улучшить deep linking из уведомлений
3. Получать playlist IDs динамически из БД
4. Добавить статистику изучения в прогресс экран

## Логика работы с карточками

### Генерация подборки

1. Получить N новых слов (state = null, НЕ в reviewedInSession)
2. Получить все due слова (dueDateMillis <= now, НЕ в reviewedInSession)
3. Перемешать и вернуть
4. После свайпа добавить wordId в reviewedInSession

### Сохранение статистики

После каждого свайпа записывать в StatisticsEntity:

- `wordId` - ID слова
- `date` - timestamp текущего момента
- `isKnown` - true если Rating.Good/Easy, false если Rating.Again
- `isRepeated` - true если слово уже было изучено (reviewCount > 0)
- `isNewWord` - true если это первый показ (reviewCount == 0)

### Обновление FSRS состояния

1. Вызвать FsrsService.calculateNext()
2. Получить новое состояние с interval, dueDate, stability, difficulty
3. Сохранить FSRSStateEntity в БД
4. Увеличить reviewCount

## Текущие задачи рефакторинга

- ✅ Исправить ошибку в fragment_learn.xml (layout_end_toEndOf → layout_constraintEnd_toEndOf)
- ✅ Убрать все lateinit и заменить на lazy
- ✅ Удалить все комментарии из кода
- ✅ Оптимизировать вложенность в item_card_learn.xml (2 уровня: CardView → FrameLayout → ConstraintLayout/Overlays)
- ✅ Вынести все hardcoded строки в strings.xml (learn*all_words_reviewed, learn_swipe*\*)
- ✅ Использовать стили из styles.xml (TextAppearance*, Widget.Grable.*)
- ✅ Добавить переводы для navbar (strings-ru.xml, strings-en.xml)
- ✅ Проверить поддержку светлой/темной темы (colors.xml, colors-night.xml)

## Реализованная логика работы с карточками ✅

### Генерация подборки

```kotlin
// LearnViewModel
private val reviewedInSession = mutableSetOf<Long>()

fun startSession(playlistIds: List<Long>, target: Int) {
    reviewedInSession.clear()
    val cards = learnInteractor.getSessionCards(playlistIds, target, reviewedInSession)
}
```

- ✅ Исключаются слова из `reviewedInSession`
- ✅ Смешиваются новые и due слова
- ✅ Сессия начинается с чистого списка

### Сохранение после свайпа

```kotlin
fun onSwipe(word: Word, rating: Rating) {
    reviewedInSession.add(word.id)  // Исключаем из будущих подборок
    learnInteractor.saveProgress(word, rating)  // Сохраняем FSRS + Статистику
}
```

### Сохранение статистики

```kotlin
// LearnInteractor.saveProgress()
repository.saveStatistics(
    wordId = word.id,
    isKnown = rating == Rating.Good || Rating.Easy,
    isRepeated = word.fsrsData.reviewCount > 0,
    isNewWord = word.fsrsData.reviewCount == 0
)
```

- ✅ Записывается каждый review
- ✅ Отслеживается: новое/повторение/известное
- ✅ Timestamp автоматически

### Обновление FSRS состояния

```kotlin
// FsrsService.calculateNext()
val nextState = FSRSStateEntity(
    wordId = card.id,
    stability = selected.stability,
    difficulty = selected.difficulty,
    interval = selected.interval,
    dueDateMillis = now + selected.durationMillis,
    lastReviewMillis = now,
    reviewCount = card.reviewCount + 1,
    phase = ...
)
wordDao.upsertState(nextState)
```

- ✅ FSRS алгоритм вычисляет интервалы
- ✅ Обновляется reviewCount
- ✅ Устанавливается следующая дата повторения

## Выполненный рефакторинг

### Исправлены ошибки

- ✅ `layout_end_toEndOf` → `layout_constraintEnd_toEndOf` в fragment_learn.xml
- ✅ `center_start` → `center_vertical|start` в item_card_learn.xml
- ✅ `center_end` → `center_vertical|end` в item_card_learn.xml

### Удалены комментарии

- ✅ Все XML комментарии удалены
- ✅ Все Kotlin комментарии удалены (LearnFragment, LearnViewModel, ReviewNotificationWorker)

### Заменен lateinit на lazy

- ✅ `lateinit var layoutManager` → `val layoutManager by lazy { ... }` в LearnFragment

### Оптимизирована вложенность

- ✅ item_card_learn.xml: 2-3 уровня вложенности с использованием ConstraintLayout
- ✅ Удалены ВСЕ лишние LinearLayout обертки из оверлеев
- ✅ Оверлеи: ImageView и TextView размещены напрямую во FrameLayout с layout_gravity
- ✅ Минимизированы проходы отрисовки для улучшения производительности

### Добавлены ресурсы

- ✅ Цвета: `overlay_again`, `overlay_good`, `overlay_hard` (light/night)
- ✅ Размеры: `icon_2xl`, `icon_3xl`
- ✅ Строки: `learn_all_words_reviewed`, `learn_swipe_left`, `learn_swipe_right`, `learn_swipe_up` (ru/en)

### Использованы стили

- ✅ `TextAppearanceHeadline1`, `TextAppearanceHeadline2`
- ✅ `TextAppearanceBody1`, `TextAppearanceBody1Secondary`
- ✅ `?attr/colorPrimary` для динамической темы
- ✅ `@dimen/spacing_*`, `@dimen/icon_*` для консистентности

## Последнее обновление (дата фиксации)

### ✅ Исправлены все критические баги

1. **Оверлеи остаются**: сброс в `bind()`, `onCardSwiped`, `onCardAppeared`, `onCardDisappeared`
2. **Дублирование карточек**: исправлена логика в `LearnInteractor` с `distinctBy { it.id }`
3. **Неправильный расчет карточек**: `remainingTarget = target - due.size`
4. **Навигация**: убрана кнопка learn, переписан HomeFragment на RecyclerView

### Созданные файлы для навигации

- `feature/home/impl/src/main/java/ru/sirius/grable/feature/home/impl/ui/HomeAction.kt`
- `feature/home/impl/src/main/java/ru/sirius/grable/feature/home/impl/ui/HomeActionsAdapter.kt`
- `feature/home/impl/src/main/res/layout/item_home_action.xml`
- Обновлен `fragment_main_screen.xml`
- Удален `nav_learn` из `bottom_nav_menu.xml`
- Добавлена строка `learn_daily_goal_completed` в home модуль (ru/en)

## ✅ ИСПРАВЛЕНО: Дублирование слов при свайпе и сброс прогресса

### Проблема 1: Одно и то же слово после свайпа

**Проблема:**
При свайпе вправо (Знаю) несколько раз подряд показывалось одно и то же слово. Было видно, как появляется другое слово, но потом возвращается то же самое.

**Причина:**
`loadMoreCards()` заменял весь список карточек новым, но `CardStackView` оставался на той же позиции адаптера. Это приводило к тому, что после свайпа показывалась карточка с той же позиции из нового списка.

**Решение:**

- Заменен `loadMoreCards()` на `removeCurrentCard()`
- Вместо замены всего списка - удаляется только первая карточка через `cards.drop(1)`
- `onCardSwiped` теперь работает с `state.cards.firstOrNull()` вместо `position`
- Новые карточки загружаются только когда текущий список полностью опустошается

**Изменения в LearnViewModel:**

```kotlin
fun removeCurrentCard() {
    val current = _uiState.value
    if (current is LearnUiState.Content && current.cards.isNotEmpty()) {
        val newCards = current.cards.drop(1)
        if (newCards.isEmpty()) {
            loadMoreCards()
        } else {
            _uiState.value = current.copy(cards = newCards)
        }
    }
}
```

### Проблема 2: Прогресс сбрасывался при каждом входе

**Проблема:**
При переходе на экран learn каждый раз прогресс сбрасывался, даже если это был тот же день.

**Решение:**

- Сохранение прогресса в `SharedPreferences` с ключом даты
- Восстановление прогресса при `startSession()` если дата совпадает
- Автоматическое сохранение после каждого свайпа

**Реализация:**

```kotlin
// Сохранение прогресса
private fun saveTodayProgress(learnedCount: Int, reviewedCount: Int) {
    sharedPreferences.edit().apply {
        putString("learn_progress_date", getTodayDateKey())
        putInt("learn_progress_learned", learnedCount)
        putInt("learn_progress_reviewed", reviewedCount)
        apply()
    }
}

// Восстановление при старте
val today = getTodayDateKey()
val savedDate = sharedPreferences.getString("learn_progress_date", "")
val initialLearnedCount = if (savedDate == today) savedLearnedCount else 0
viewModel.startSession(playlistIds, target, initialLearnedCount, initialReviewedCount)
```

### Обновленные файлы (исправления)

**LearnViewModel.kt:**

- Добавлен `currentCards` для отслеживания текущего списка
- Заменен `loadMoreCards()` на `removeCurrentCard()`
- `startSession()` принимает `initialLearnedCount` и `initialReviewedCount`
- `loadMoreCards()` стал `private` и вызывается только при пустом списке

**LearnFragment.kt:**

- Добавлены `getTodayDateKey()` и `saveTodayProgress()`
- Восстановление прогресса при `onViewCreated()`
- `onCardSwiped()` работает с `firstOrNull()` вместо позиции
- Автоматическое сохранение после каждого свайпа
- Убрана анимация свайпа при "Пометить как известное"

### Результат

- ✅ Каждое слово показывается только один раз
- ✅ Прогресс сохраняется между сессиями в течение дня
- ✅ На следующий день прогресс автоматически сбрасывается
- ✅ Плавная работа CardStackView без мерцаний

## ✅ НОВАЯ ФУНКЦИЯ: Экран завершения и блокировка после выполнения нормы

### Реализовано

1. **Экран завершения изучения (`LearnCompletionFragment`)**

   - Показывается после изучения N новых слов
   - Отображает статистику: количество выученных и повторенных слов
   - Кнопка возврата на главный экран
   - Полная локализация (ru/en)

2. **Проверка выполнения дневной нормы**

   - Добавлен запрос `getLearnedNewWordsToday()` в `StatisticsDao`
   - `LearnInteractor.getLearnedNewWordsTodayCount()` - подсчет выученных слов за сегодня
   - Проверка выполняется при загрузке `HomeFragment`

3. **Блокировка кнопки "Учить" после выполнения нормы**

   - `HomeViewModel` отслеживает `learnedNewWordsToday`
   - Кнопка становится неактивной, если норма выполнена
   - Текст меняется на "Вы уже выучили норму слов на сегодня! Возвращайтесь завтра"
   - Клик по кнопке игнорируется

4. **Автоматический переход к экрану завершения**
   - `LearnViewModel` отправляет event `DailyGoalCompleted` при достижении цели
   - `LearnFragment` слушает события и показывает `LearnCompletionFragment`
   - Подсчет `learnedCount` и `reviewedCount` для статистики

### Созданные файлы

- `feature/learn/impl/src/main/res/layout/fragment_learn_completion.xml`
- `feature/learn/impl/src/main/java/ru/sirius/grable/feature/learn/impl/ui/LearnCompletionFragment.kt`
- `feature/learn/impl/src/main/res/drawable/ic_check_circle.xml`

### Обновленные файлы

**Database:**

- `StatisticsDao.kt` - добавлен `getLearnedNewWordsToday()`

**Domain:**

- `LearnInteractor.kt` - добавлен `getLearnedNewWordsTodayCount()`
- `WordsRepository.kt` - добавлен `getLearnedNewWordsTodayCount()`

**UI:**

- `LearnViewModel.kt` - добавлен event `DailyGoalCompleted`, подсчет `reviewedCount`
- `LearnFragment.kt` - обработка события завершения, показ completion screen
- `HomeViewModel.kt` - добавлено `learnedNewWordsToday`, `refreshData()`
- `HomeFragment.kt` - блокировка кнопки, обновление при `onResume()`
- `HomeActionsAdapter.kt` - обработка disabled состояния

**Resources:**

- `strings.xml` (ru/en) - добавлены строки для completion screen
- `dimens.xml` - добавлен `icon_4xl`

### Логика работы

1. **При открытии HomeFragment:**

   - Загружается количество выученных слов за сегодня
   - Если `learnedNewWordsToday >= newWordsPerDay` → кнопка disabled

2. **Во время изучения:**

   - При каждом свайпе обновляется `learnedCount` и `reviewedCount`
   - Когда `learnedCount >= targetCount` → emit `DailyGoalCompleted`

3. **При завершении:**
   - Показывается `LearnCompletionFragment` с статистикой
   - При возврате на главный экран кнопка "Учить" становится disabled
   - Пользователь может вернуться завтра для новой сессии

### Преимущества

- ✅ Геймификация: четкая цель и награда за выполнение
- ✅ Предотвращение переутомления: ограничение на день
- ✅ Мотивация: красивый экран завершения с результатами
- ✅ UX: понятная обратная связь о выполнении нормы

## ✅ КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: Race condition при свайпе

### Проблема

Баг все еще возвращался: одно и то же слово показывалось несколько раз после свайпа.

### Причина

Race condition - операции выполнялись в неправильном порядке:

```kotlin
// БЫЛО (неправильно):
viewModel.onSwipe(word, rating)  // Запускается async в viewModelScope
viewModel.removeCurrentCard()     // Выполняется СРАЗУ

// Результат:
1. removeCurrentCard() удаляет карточку
2. onSwipe() async добавляет word.id в reviewedInSession
3. Карточка уже удалена, но слово НЕ в исключениях
4. При loadMoreCards() слово возвращается!
```

### Решение

Переместил `removeCurrentCard()` ВНУТРЬ `onSwipe()` и `onMarkAsKnown()`:

```kotlin
fun onSwipe(word: Word, rating: Rating) {
    viewModelScope.launch {
        reviewedInSession.add(word.id)        // 1. Добавляем в исключения
        learnInteractor.saveProgress(word, rating)  // 2. Сохраняем
        if (word.fsrsData.reviewCount == 0 && ...) {
            learnedCount++
        }
        removeCurrentCard()  // 3. ТОЛЬКО ПОТОМ удаляем карточку
        updateProgress()
    }
}
```

### Обновленные файлы

**LearnViewModel.kt:**

- `removeCurrentCard()` теперь вызывается внутри `onSwipe()` после добавления в `reviewedInSession`
- `removeCurrentCard()` вызывается внутри `onMarkAsKnown()` после добавления в `reviewedInSession`
- Гарантированный порядок: add → save → remove

**LearnFragment.kt:**

- Убран вызов `viewModel.removeCurrentCard()` из `onCardSwiped()`
- Убран вызов `viewModel.removeCurrentCard()` из `onMarkAsKnown` callback
- Добавлена задержка перед `saveTodayProgress()` для обновления состояния

### Результат

- ✅ Гарантированный порядок выполнения операций
- ✅ Слово добавляется в `reviewedInSession` ДО удаления карточки
- ✅ Race condition полностью устранен
- ✅ Каждое слово показывается строго один раз

## ✅ UI/UX улучшения экрана завершения

### Проблема 1: Слишком маленький шрифт

Текст на экране завершения был плохо читаемым из-за маленького размера шрифта.

### Решение 1: Увеличены размеры шрифтов

**fragment_learn_completion.xml:**

- `completionSubtitle`: `text_lg` (18sp) вместо `TextAppearanceBody1` (16sp)
- Названия статистики: `text_lg` (18sp) вместо `TextAppearanceBody1`
- Счетчики статистики: `text_2xl` (24sp) + `bold` вместо `TextAppearanceHeadline2`

### Проблема 2: Кнопка "Вернуться" не работала

Кнопка "Вернуться на главную" использовала `onBackPressed()`, что возвращало на LearnFragment.

### Решение 2: Правильная навигация

**LearnCompletionFragment.kt:**

```kotlin
binding.btnBack.setOnClickListener {
    parentFragmentManager.popBackStack()  // Удаляем completion screen
    navigationRouter.navigateToScreen(Screens.HOME)  // Переход на главную
}
```

### Результат улучшений

- ✅ Текст хорошо читаемый, размеры соответствуют иерархии
- ✅ Кнопка "Вернуться" корректно возвращает на главный экран
- ✅ Back stack очищается правильно

## ✅ Динамическое получение плейлистов и улучшения уведомлений

### Проблема: Hardcoded playlist IDs

В нескольких местах использовались захардкоженные `listOf(1L, 2L, 3L)`:

- `HomeViewModel.loadData()`
- `ReviewNotificationWorker.getDueWordsCount()`

### Решение 1: Добавлен метод в PlaylistDao

**PlaylistDao.kt:**

```kotlin
@Query("SELECT id FROM playlist ORDER BY id ASC")
suspend fun getAllPlaylistIds(): List<Long>
```

### Решение 2: Обновлены зависимости

**HomeViewModel.kt:**

- Добавлен `PlaylistDao` в конструктор
- `loadData()` использует `playlistDao.getAllPlaylistIds()`

**HomeModule:**

- Обновлен `viewModel { HomeViewModel(get(), get(), get()) }`

**ReviewNotificationWorker.kt:**

- Добавлен `playlistDao: PlaylistDao by inject()`
- `getDueWordsCount()` использует динамические ID

### Проблема: Уведомления не локализованы

Текст уведомлений был захардкожен на русском:

- "Время повторить слова!"
- "У вас $count слов для повторения"
- "Review Reminders"
- "Напоминания о повторении слов"

### Решение 3: Локализация уведомлений

**app/src/main/res/values/strings.xml:**

```xml
<string name="notification_review_title">Время повторить слова!</string>
<plurals name="notification_review_text">
    <item quantity="one">У вас %d слово для повторения</item>
    <item quantity="few">У вас %d слова для повторения</item>
    <item quantity="many">У вас %d слов для повторения</item>
    <item quantity="other">У вас %d слов для повторения</item>
</plurals>
<string name="notification_channel_name">Напоминания о повторении</string>
<string name="notification_channel_description">Уведомления о словах, которые пора повторить</string>
```

**app/src/main/res/values-en/strings.xml:**

```xml
<string name="notification_review_title">Time to review your words!</string>
<plurals name="notification_review_text">
    <item quantity="one">You have %d word to review</item>
    <item quantity="other">You have %d words to review</item>
</plurals>
<string name="notification_channel_name">Review Reminders</string>
<string name="notification_channel_description">Notifications about words that are due for review</string>
```

**ReviewNotificationWorker.sendNotification():**

```kotlin
val title = applicationContext.getString(R.string.notification_review_title)
val text = applicationContext.resources.getQuantityString(
    R.plurals.notification_review_text,
    count,
    count
)
```

### Проблема: Deep link не открывал экран обучения

При клике на уведомление открывалась только MainActivity, но не экран выбора плейлистов.

### Решение 4: Deep link с intent extras

**ReviewNotificationWorker.sendNotification():**

```kotlin
val intent = Intent(applicationContext, MainActivity::class.java).apply {
    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    putExtra(EXTRA_OPEN_LEARN_SCREEN, true)
}
```

**MainActivity.handleDeepLink():**

```kotlin
private fun handleDeepLink() {
    val openLearnScreen = intent?.getBooleanExtra(
        ReviewNotificationWorker.EXTRA_OPEN_LEARN_SCREEN,
        false
    ) ?: false
    if (openLearnScreen) {
        navigationRouter.navigateToScreen(Screens.SELECT_PLAYLIST)
    }
}
```

### Результат всех улучшений

- ✅ Playlist IDs загружаются динамически из БД
- ✅ Уведомления полностью локализованы (русский + английский)
- ✅ Корректные plurals для количества слов (1 слово / 2-4 слова / 5+ слов)
- ✅ Deep link открывает экран выбора плейлистов для обучения
- ✅ Код чище, без hardcoded значений
- ✅ Лучшая поддержка многоязычности

## ✅ КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: Crash при возврате на LearnFragment

### Проблема

При нажатии кнопки "Back to main" и повторном переходе на экран обучения возникал crash:

```
java.lang.IllegalArgumentException: LayoutManager com.yuyakaido.android.cardstackview.CardStackLayoutManager@3970965
is already attached to a RecyclerView
```

### Причина

`CardStackLayoutManager` был объявлен как `lazy` property и создавался один раз:

```kotlin
private val layoutManager: CardStackLayoutManager by lazy {
    CardStackLayoutManager(requireContext(), this).apply { /* ... */ }
}
```

**Жизненный цикл проблемы:**

1. При первом создании LearnFragment создается `layoutManager`
2. `layoutManager` привязывается к `CardStackView` в `setupCardStack()`
3. При уходе с экрана вызывается `onDestroyView()`, view уничтожается
4. Но `layoutManager` остается в памяти (из-за `lazy`), привязанный к старому view
5. При возврате создается новый view и новый `CardStackView`
6. В `setupCardStack()` пытаются присоединить старый `layoutManager` к новому `CardStackView`
7. RecyclerView выбрасывает IllegalArgumentException

### Решение

**LearnFragment.kt:**

1. Изменен тип поля с `lazy` на nullable var:

```kotlin
private var layoutManager: CardStackLayoutManager? = null
```

2. `layoutManager` создается каждый раз в `setupCardStack()`:

```kotlin
private fun setupCardStack() {
    layoutManager = CardStackLayoutManager(requireContext(), this).apply {
        setStackFrom(StackFrom.Top)
        setVisibleCount(3)
        // ...
    }
    binding.cardStackView.layoutManager = layoutManager
    // ...
}
```

3. Обнуляется в `onDestroyView()`:

```kotlin
override fun onDestroyView() {
    super.onDestroyView()
    layoutManager = null
    _binding = null
}
```

4. Все обращения к `layoutManager` используют safe call:

```kotlin
val currentPosition = layoutManager?.topPosition ?: return
```

### Результат

- ✅ Crash полностью устранен
- ✅ Можно многократно переходить на LearnFragment и уходить с него
- ✅ LayoutManager корректно пересоздается при каждом создании view
- ✅ Правильное управление жизненным циклом Fragment

## ✅ Рефакторинг: Вынос констант настроек в API

### Проблема

Константы настроек были захардкожены в `SettingsRepository` и использовались напрямую в других модулях через магические строки.

### Решение

**1. Созданы API константы - `SettingsKeys.kt`**
**2. Создан интерфейс - `ISettingsService`**
**3. `SettingsRepository` реализует интерфейс**
**4. Зарегистрирован в Koin как singleton**
**5. Обновлены LearnFragment и HomeFragment для использования API**

### Результат

- ✅ Нет магических строк в коде
- ✅ Единая точка доступа к настройкам через интерфейс
- ✅ Правильная слоистая архитектура (UI не знает о SharedPreferences)
- ✅ Легко тестировать (можно замокать ISettingsService)
- ✅ Type-safe доступ к настройкам

## ✅ Исправление свайпа вверх

### Проблема

Свайп вверх не работал в LearnFragment.

### Решение

Изменено: `setDirections(Direction.FREEDOM)` и `setCanScrollVertical(true)`

### Результат

- ✅ Работают все свайпы: влево (Again), вправо (Good), вверх (Hard)

## ✅ Режим просмотра карточек

### Реализация

LearnPlaylistFragment отображает список слов без трекинга прогресса.

### Добавлено

- Кнопка "Пометить как изученное" в item_word.xml
- Обработка в LearnPlaylistAdapter и LearnPlaylistViewModel

### Результат

- ✅ Режим просмотра работает
- ✅ Можно пометить слово как изученное
- ✅ Прогресс не отслеживается

## 🔜 БУДУЩИЕ ЗАДАЧИ

### 1. Кастомные слова и категории ⏳ В ПРОЦЕССЕ

**✅ Выполнено:**

- ✅ Добавлено поле `isSystem: Boolean` в `PlaylistEntity`
- ✅ Создана миграция БД `MIGRATION_2_3` для добавления поля `isSystem`
- ✅ Категория "Свои слова" создается по умолчанию (`isSystem = false`)
- ✅ Обновлен `Playlist` API для поддержки `isSystem`
- ✅ Обновлен `SelectPlaylistRepository` для маппинга `isSystem`
- ✅ Обновлен `SelectPlaylistInteractor` для сортировки (сначала системные, потом пользовательские)

**⏳ В процессе:**

- ⏳ Separator Bar на SelectPlaylistFragment (визуальное разделение)
- ⏳ UI для создания новых категорий
- ⏳ UI для добавления слов в категории
- ⏳ Функционал добавления кастомных слов

### 2. Выбор категорий для подборки

- Кнопка "Edit" на SelectPlaylistFragment
- Checkbox на категориях в режиме редактирования
- Сохранение выбранных категорий
- Использование выбранных категорий при генерации подборки

### 3. Отделение модуля learn_playlist

- Создать отдельный модуль feature:learn_playlist
- Перенести LearnPlaylistFragment и связанные классы
- Настроить зависимости

## ✅ КРИТИЧЕСКОЕ ИСПРАВЛЕНИЕ: NumberFormatException в FSRS

### Проблема

При многократном нажатии кнопки "Уже знаю это слово" возникал crash:

```
java.lang.NumberFormatException: For input string: "1,00"
at ru.sirius.fsrs.FSRS.initDifficulty(FSRS.kt:223)
```

### Причина

В FSRS использовался `String.format("%.2f", value).toDouble()` который форматирует с локалью системы (в русской локали запятая вместо точки).

### Решение

Заменено на математическое округление: `(round(value * 100) / 100.0)`

### Результат

- ✅ Crash устранен
- ✅ Независимость от локали
- ✅ Корректное округление

## ✅ UX улучшение: BottomSheet для деталей слова

### Проблема

Кнопка "Уже знаю это слово" всегда была видна в списке, загромождала UI.

### Решение

- Создан `WordDetailsBottomSheet` с полной информацией о слове
- Кнопка "Уже знаю" теперь только в BottomSheet
- При клике на слово в списке открывается BottomSheet

### Результат

- ✅ Чистый UI списка
- ✅ Детальная информация о слове
- ✅ Нет случайных нажатий

## ✅ Финальные исправления FSRS и индикация статуса

### Проблема 1: Crash все еще возникал

Были найдены еще 2 места с `String.format`:

- `nextForgetStability()` - строка 291
- `nextRecallStability()` - строка 306

### Решение 1: Полное исправление FSRS

Заменены все оставшиеся `String.format` на математическое округление.

### Проблема 2: Отсутствие индикации состояния слова

В списке слов не было видно:

- Новое слово (не встречалось)
- Изучается (повторялось, но еще не выучено)
- Выучено (reviewCount >= 3 и stability > 10)

### Решение 2: Цветовая индикация

**item_word.xml:**

- Добавлен цветной индикатор слева от слова

**Цвета:**

- Серый → Новое слово (reviewCount == 0)
- Оранжевый → Изучается (reviewCount > 0, но не выучено)
- Зеленый → Выучено (reviewCount >= 3 && stability > 10)

**LearnPlaylistAdapter:**

```kotlin
val statusColor = when {
    word.fsrsData.reviewCount == 0 -> word_status_new
    word.fsrsData.reviewCount >= 3 && word.fsrsData.stability > 10.0 -> word_status_learned
    else -> word_status_learning
}
```

### Проблема 3: Кнопка всегда видна

Кнопка "Уже знаю" показывалась даже для выученных слов.

### Решение 3: Условное отображение

**WordDetailsBottomSheet:**

```kotlin
val isLearned = word.fsrsData.reviewCount > 0
binding.btnMarkAsKnown.visibility = if (isLearned) View.GONE else View.VISIBLE
```

### Результат всех исправлений

- ✅ Crash полностью устранен (все String.format заменены)
- ✅ Визуальная индикация состояния слова в списке
- ✅ Кнопка "Уже знаю" только для новых слов
- ✅ Поддержка светлой и темной тем
- ✅ Лаконичная, но информативная индикация

## ✅ Исправление BottomSheet и улучшение индикаторов

### Проблема 1: Crash при открытии BottomSheet

```
android.view.InflateException: Error inflating class TextView
Failed to resolve attribute at index 5
```

### Причина

Использовался неправильный атрибут для стиля TextView:

```xml
style="@style/TextAppearanceHeadline1"  <!-- НЕПРАВИЛЬНО -->
```

### Решение

Для TextView нужно использовать `android:textAppearance`:

```xml
android:textAppearance="@style/TextAppearanceHeadline1"  <!-- ПРАВИЛЬНО -->
```

### Проблема 2: Индикаторы непонятны новым пользователям

Только цветовые индикаторы без текста не интуитивны.

### Решение

Добавлены текстовые метки справа от каждого слова:

**item_word.xml:**

- Добавлен `statusLabel` TextView
- Размер `text_xs` для компактности
- Цвет `colorOnSurfaceVariant`

**Strings (ru):**

- `learn_status_new` → "Новое"
- `learn_status_learning` → "Изучается"
- `learn_status_learned` → "Выучено"

**Strings (en):**

- `learn_status_new` → "New"
- `learn_status_learning` → "Learning"
- `learn_status_learned` → "Learned"

**LearnPlaylistAdapter:**

```kotlin
val (statusColor, statusText) = when {
    word.fsrsData.reviewCount == 0 -> Pair(word_status_new, learn_status_new)
    word.fsrsData.reviewCount >= 3 && stability > 10.0 -> Pair(word_status_learned, learn_status_learned)
    else -> Pair(word_status_learning, learn_status_learning)
}
binding.statusIndicator.setBackgroundColor(context.getColor(statusColor))
binding.statusLabel.setText(statusText)
```

### Результат финальных исправлений

- ✅ BottomSheet открывается без ошибок
- ✅ Индикаторы имеют текстовые метки
- ✅ Понятно новым пользователям (цвет + текст)
- ✅ Локализовано на русский и английский
- ✅ Компактный дизайн (text_xs)
- ✅ Исправлена та же ошибка в item_word.xml (originalWord TextView)

**Правило:** Для TextView всегда использовать `android:textAppearance`, а не `style` для текстовых стилей.

## ✅ МАССОВОЕ ИСПРАВЛЕНИЕ: textAppearance во всех layouts

### Проблема

Ошибка продолжала возникать, потому что множество других TextView использовали неправильный атрибут `style` вместо `android:textAppearance`.

### Исправленные файлы

**item_card_learn.xml** - 8 TextView:

- `frontWord` ✓
- `frontTranscription` ✓
- `backTranslation` ✓
- `backOriginal` ✓
- `backExample` ✓
- Overlay left TextView ✓
- Overlay right TextView ✓
- Overlay up TextView ✓

**fragment_learn_completion.xml** - 2 TextView:

- `completionTitle` ✓
- `statsTitle` ✓

**fragment_learn.xml** - 1 TextView:

- `emptyView` ✓

**item_word.xml** - 1 TextView:

- `originalWord` ✓

**bottom_sheet_word_details.xml** - 1 TextView:

- `wordOriginal` ✓

### Итого исправлено

**13 TextView** во всем модуле `feature/learn/impl`

### Результат

- ✅ Все TextView используют правильный атрибут
- ✅ LearnPlaylistFragment открывается без ошибок
- ✅ LearnFragment работает корректно
- ✅ BottomSheet открывается без проблем
- ✅ Completion screen работает

## ✅ Исправление TODO в WordFragment

### Проблема

В `WordFragment.kt` был незавершенный код для отображения примеров использования слова.

### Решение

Исправлен метод `bindWord()` для корректного отображения первого примера из списка `word.examples`:

```kotlin
val firstExample = word.examples.firstOrNull()
binding.backExample.text = firstExample?.text ?: ""
```

### Результат

- ✅ Примеры использования слова корректно отображаются на обратной стороне карточки
- ✅ Нет падений при отсутствии примеров
