# План разработки проекта Grable

## Задачи

### 1. Рефакторинг архитектуры

- [x] Перенести navigation в core (core/navigation/api и core/navigation/impl)
- [x] Обновить зависимости в build.gradle.kts файлах
- [x] Обновить импорты во всех модулях

### 2. Оптимизация layout файлов

- [x] Упростить fragment_learn.xml - убрать избыточную вложенность layout-ов (использован ConstraintLayout)
- [x] Упростить fragment_select_playlist.xml - убрать избыточную вложенность (использован ConstraintLayout)
- [x] Использовать ConstraintLayout для группировки элементов вместо вложенных layout-ов
- [x] Убрать хардкод свойства из верстки в styles.xml (добавлены стили для контейнеров, кнопок, текста)
- [x] Применены стили в layout файлах (fragment_main_screen, item_playlist, fragment_progress)

### 3. ViewBinding

- [x] Проверено - везде используется viewBinding

### 4. Локализация

- [x] Добавлена локализация для learn модуля (ru/en)
- [x] Добавлена полная локализация для progress модуля (ru/en)
- [x] Проверено что все строки в layout файлах используют @string/...
- [x] Обновлен код для использования строк из ресурсов с форматированием

### 5. Улучшение визуала

- [x] Улучшен визуал fragment_learn.xml (использован ConstraintLayout, улучшена структура)
- [x] Улучшен визуал fragment_learn_playlist.xml - убран значок редактировать из item_word.xml
- [x] Улучшен fragment_select_playlist.xml - сделана 1 колонка вместо 2, улучшен визуал
- [x] Частично приведено к единой дизайн системе (добавлены стили)

### 6. Дизайн система

- [x] Создана единая система стилей в core/design (добавлены стили для контейнеров, кнопок, текста)
- [x] Убраны хардкод значения из layout файлов (padding, margin, textColor и т.д.)
- [x] Используются dimen и color из core/design
- [x] Применены единые стили к компонентам (fragment_main_screen, item_playlist, fragment_progress)

### 7. TTS - смена голоса

- [x] Изучить текущую реализацию TTS
- [x] Добавить поддержку смены голоса (male/female) в TTSImpl
- [x] Подключить настройки голоса из Settings к TTS
- [x] Реализовано обновление голоса при изменении настроек и при загрузке фрагмента

## Порядок выполнения

1. **Этап 1: Архитектура** (navigation в core) ✅
2. **Этап 2: Layout оптимизация** (упрощение вложенности) ⚠️ В процессе
3. **Этап 3: Дизайн система** (стили, цвета, spacing) ✅
4. **Этап 4: Локализация** (ru/en) ✅
5. **Этап 5: Визуальные улучшения** (learn, learn_playlist, select_playlist) ✅
6. **Этап 6: TTS голос** (реализация смены голоса) ✅

## Текущие задачи

### 8. Исправление ошибок компиляции

- [x] Исправить ошибки в StatsFragment.kt (Unresolved reference 'impl' и 'ProgressR')
- [x] Проверить namespace модулей

### 9. Рефакторинг пакетов

- [x] Заменить все вхождения `com.example` на `ru.sirius` по всему проекту
- [x] Обновить package во всех файлах (libs/di, libs/imageloader, feature/progress)
- [x] Обновить импорты во всех модулях
- [x] Обновить AndroidManifest файлы
- [x] Удалить все папки com.example из проекта

### 10. Очистка app модуля

- [x] Убрать лишние layout файлы из app (fragment_learn, fragment_learn_playlist, fragment_select_playlist, item_word, item_playlist, fragment_main_screen, layout_header, word_card, item_playlist_preview)
- [x] Удалить дубликаты drawable из app (bg_action_card, bg_category_chip, bg_skeleton_rounded, ic_edit_example, ic_play_audio_word, ic_play_sound, period_background, spinner_background)
- [x] Удалить пустые директории (main/, widgets/)
- [x] Очистить strings.xml в app - оставить только app_name
- [x] Оставить только MainActivity и необходимые ресурсы (activity_main.xml, layout_footer.xml, bottom_nav_menu.xml, иконки для меню, launcher иконки, strings.xml с app_name)

### 11. Решение проблемы вложенности layout'ов

- [x] Упростить fragment_main_screen.xml - убрана вложенность с 5 до 2 уровней (использован ConstraintLayout)
- [x] Упростить layout_footer.xml - убрана вложенность с 4 до 2 уровней (использован ConstraintLayout)
- [x] Упростить layout_header.xml - убрана вложенность с 3 до 2 уровней (использован ConstraintLayout)
- [x] Упростить item_day_stat.xml - убрана вложенность LinearLayout, использован ConstraintLayout
- [x] Упростить item_stat.xml - убрана вложенность LinearLayout, использован ConstraintLayout
- [x] Упростить word_card.xml - убрана вложенность FrameLayout + LinearLayout, использован ConstraintLayout
- [x] Упростить item_playlist.xml - убрана вложенность LinearLayout, использован ConstraintLayout
- [x] Упростить fragment_progress.xml - убрана вложенность LinearLayout, использован ConstraintLayout
- [x] Проверить остальные layout файлы - fragment_add_word, dialog_add_example, fragment_add_example используют ConstraintLayout (вложенность 2-3 уровня, что приемлемо)
- [x] Основные проблемные файлы упрощены, вложенность не превышает 2-3 уровней

### 12. Улучшение UX и дизайна

- [x] Добавлена кнопка назад на learn_playlist фрагмент (MaterialToolbar с навигацией)
- [x] Переработан главный экран - улучшена логика кнопок:
  - "Повторение слов" → LEARN (повторение изученных слов)
  - "Учить подборку" → SELECT_PLAYLIST (выбор подборки для изучения)
  - "Добавить слово" → ADD_WORD (добавление новых слов)
- [x] Улучшен дизайн главного экрана - использованы MaterialCardView с правильными стилями
- [x] Приведены все layout'ы к общему стилю (цвета, spacing из core/design)
- [x] Использован Material Design 3 (тема уже настроена)

### 13. Рефакторинг на Koin

- [x] Переделать StatsFragment.kt на использование Koin вместо ViewModelProvider
- [x] Удалить StatisticsViewModelFactory (больше не нужен)
- [x] Проверить другие модули на использование Koin (все модули используют Koin)

### 14. Исправление Navigation Menu

- [x] Исправить отображение текущей вкладки в navigation menu при переходе через кнопки
- [x] Добавить NavigationListener в NavigationRouter для обновления bottom navigation
- [x] Обновлять selectedItemId в MainActivity при программной навигации

### 15. Финальная оптимизация верстки

- [x] Убрать оставшиеся хардкод значения из верстки в стили:
  - fragment_progress.xml: padding, margin в errorLayout и progressBar
  - item_stat.xml: padding, minWidth в period badge
  - item_word.xml: cardElevation
- [x] Добавить стили для ErrorContainer, Error TextView, PeriodBadge
- [x] Проверить вложенность layout-ов (все основные файлы имеют вложенность 2-3 уровня, что приемлемо)

### 16. Финальная оптимизация верстки - skeleton и изображения

- [x] Добавить dimen для skeleton элементов (progress, hint, button, card, header, item)
- [x] Создать стили для skeleton элементов в core/design
- [x] Убрать хардкод значения из skeleton в fragment_learn.xml
- [x] Убрать хардкод значения из skeleton в fragment_select_playlist.xml
- [x] Убрать хардкод значения из fragment_add_word.xml (image height, padding, card corner radius)
- [x] Все хардкод значения вынесены в dimen и стили
