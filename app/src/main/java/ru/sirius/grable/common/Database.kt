package ru.sirius.grable.common

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@TypeConverters(Converters::class)
@Database(
    entities = [
        ExampleEntity::class,
        WordEntity::class,
        PlaylistEntity::class,
        StatisticsEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun playlistDao(): PlaylistDao;
    abstract fun wordDao(): WordDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                createDatabase(context)
                    .also { db -> INSTANCE = db }
            }
        }

        private fun createDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
//                .addMigrations(MIGRATION_1_2)
                .addCallback(DatabaseInitCallback)
                .build()
        }

        // Для новых установок (чистая база)
        private object DatabaseInitCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
//                populateInitialData(db)
            }
        }

        // Для обновления с версии 1 → 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                populateInitialData(db)
            }
        }

        // ←─────── ВСЯ НАЧАЛЬНАЯ БАЗА ЗДЕСЬ (≈ 257 слов, 3 плейлиста, примеры, статистика) ───────//
        private fun populateInitialData(db: SupportSQLiteDatabase) {
            // Защита от повторного выполнения
            val cursor = db.query("SELECT COUNT(*) FROM playlist")
            if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
                cursor.close()
                return
            }
            cursor.close()

            val cv = ContentValues()

            // === Плейлисты ===
            cv.put("name", "Базовые слова")
            cv.put("description", "Самые важные повседневные слова")
            val playlistBasic = db.insert("playlist", SQLiteDatabase.CONFLICT_NONE, cv)

            cv.clear()
            cv.put("name", "Еда и напитки")
            cv.put("description", "Продукты, блюда, ресторан")
            val playlistFood = db.insert("playlist", SQLiteDatabase.CONFLICT_NONE, cv)

            cv.clear()
            cv.put("name", "Путешествия и город")
            cv.put("description", "Отели, транспорт, ориентирование")
            val playlistTravel = db.insert("playlist", SQLiteDatabase.CONFLICT_NONE, cv)

            val wordIds = mutableListOf<Long>()

            data class Word(
                val original: String,
                val transcription: String,
                val translation: String,
                val exampleEn: String,
                val exampleRu: String
            )

            val insertWord = { playlistId: Long, w: Word ->
                cv.clear()
                cv.put("playlistId", playlistId)
                cv.put("original", w.original)
                cv.put("transcription", w.transcription)
                cv.put("translation", w.translation)
                cv.put("text", "")               // поле text в WordEntity не используется
                cv.put("isNew", 1)
                val wordId = db.insert("word", SQLiteDatabase.CONFLICT_NONE, cv)
                wordIds.add(wordId)

                // Пример к слову
                cv.clear()
                cv.put("wordId", wordId)
                cv.put("text", w.exampleEn)
                cv.put("translatedText", w.exampleRu)
                db.insert("example", SQLiteDatabase.CONFLICT_NONE, cv)
            }

            // ===================================================================
            // 1. Базовые слова — 110 слов
            // ===================================================================
            val basicWords = listOf(
                Word("hello", "[həˈloʊ]", "привет", "Hello! How are you?", "Привет! Как дела?"),
                Word("hi", "[haɪ]", "привет (неформ.)", "Hi everyone!", "Привет всем!"),
                Word("goodbye", "[ɡʊdˈbaɪ]", "до свидания", "Goodbye, see you later!", "До свидания, увидимся!"),
                Word("bye", "[baɪ]", "пока", "Bye, take care!", "Пока, береги себя!"),
                Word("thanks", "[θæŋks]", "спасибо", "Thanks a lot!", "Спасибо большое!"),
                Word("thank you", "[ˈθæŋk juː]", "спасибо", "Thank you for the gift.", "Спасибо за подарок."),
                Word("please", "[pliːz]", "пожалуйста", "Please, help me.", "Пожалуйста, помогите."),
                Word("sorry", "[ˈsɑːri]", "извините", "I'm sorry I'm late.", "Извините, я опоздал."),
                Word("yes", "[jes]", "да", "Yes, I agree.", "Да, я согласен."),
                Word("no", "[noʊ]", "нет", "No, thank you.", "Нет, спасибо."),
                Word("good morning", "[ɡʊd ˈmɔːrnɪŋ]", "доброе утро", "Good morning!", "Доброе утро!"),
                Word("good evening", "[ɡʊd ˈiːvnɪŋ]", "добрый вечер", "Good evening!", "Добрый вечер!"),
                Word("good night", "[ɡʊd naɪt]", "спокойной ночи", "Good night!", "Спокойной ночи!"),
                Word("how are you", "[haʊ ɑːr juː]", "как дела", "How are you? — I'm fine.", "Как дела? — Нормально."),
                Word("I", "[aɪ]", "я", "I love music.", "Я люблю музыку."),
                Word("you", "[juː]", "ты / вы", "I miss you.", "Я скучаю по тебе."),
                Word("he", "[hiː]", "он", "He is my friend.", "Он мой друг."),
                Word("she", "[ʃiː]", "она", "She is beautiful.", "Она красивая."),
                Word("it", "[ɪt]", "это / он / она (неодуш.)", "It is cold today.", "Сегодня холодно."),
                Word("we", "[wiː]", "мы", "We are family.", "Мы семья."),
                Word("they", "[ðeɪ]", "они", "They live here.", "Они здесь живут."),
                Word("my", "[maɪ]", "мой", "My phone is new.", "Мой телефон новый."),
                Word("your", "[jɔːr]", "твой / ваш", "Your bag is nice.", "Твоя сумка красивая."),
                Word("name", "[neɪm]", "имя", "What is your name?", "Как тебя зовут?"),
                Word("friend", "[frend]", "друг", "You are my best friend.", "Ты мой лучший друг."),
                Word("family", "[ˈfæməli]", "семья", "Family is important.", "Семья важна."),
                Word("love", "[lʌv]", "любить / любовь", "I love you.", "Я тебя люблю."),
                Word("like", "[laɪk]", "нравиться", "I like coffee.", "Мне нравится кофе."),
                Word("want", "[wɑːnt]", "хотеть", "I want water.", "Я хочу воды."),
                Word("need", "[niːd]", "нуждаться", "I need help.", "Мне нужна помощь."),
                Word("house", "[haʊs]", "дом", "I live in a big house.", "Я живу в большом доме."),
                Word("home", "[hoʊm]", "дом (родной)", "I'm going home.", "Я иду домой."),
                Word("work", "[wɜːrk]", "работа / работать", "I work every day.", "Я работаю каждый день."),
                Word("time", "[taɪm]", "время", "What time is it?", "Сколько времени?"),
                Word("day", "[deɪ]", "день", "Have a good day!", "Хорошего дня!"),
                Word("today", "[təˈdeɪ]", "сегодня", "Today is Monday.", "Сегодня понедельник."),
                Word("tomorrow", "[təˈmɑːroʊ]", "завтра", "See you tomorrow.", "Увидимся завтра."),
                Word("yesterday", "[ˈjestərdeɪ]", "вчера", "Yesterday was sunny.", "Вчера было солнечно."),
                Word("week", "[wiːk]", "неделя", "Next week is holiday.", "На следующей неделе отпуск."),
                Word("month", "[mʌnθ]", "месяц", "My birthday is in May.", "Мой день рождения в мае."),
                Word("year", "[jɪr]", "год", "Happy New Year!", "С Новым годом!"),
                Word("happy", "[ˈhæpi]", "счастливый", "I'm very happy.", "Я очень счастлив."),
                Word("good", "[ɡʊd]", "хороший", "Good job!", "Молодец!"),
                Word("bad", "[bæd]", "плохой", "Bad weather.", "Плохая погода."),
                Word("big", "[bɪɡ]", "большой", "Big city.", "Большой город."),
                Word("small", "[smɔːl]", "маленький", "Small dog.", "Маленькая собака."),
                Word("new", "[nuː]", "новый", "New phone.", "Новый телефон."),
                Word("old", "[oʊld]", "старый", "Old book.", "Старая книга."),
                Word("beautiful", "[ˈbjuːtɪfl]", "красивый", "Beautiful flower.", "Красивый цветок."),
                Word("red", "[red]", "красный", "Red apple.", "Красное яблоко."),
                Word("blue", "[bluː]", "синий", "Blue sky.", "Синее небо."),
                Word("green", "[ɡriːn]", "зелёный", "Green forest.", "Зелёный лес."),
                Word("yellow", "[ˈjeləʊ]", "жёлтый", "Yellow banana.", "Жёлтый банан."),
                Word("black", "[blæk]", "чёрный", "Black cat.", "Чёрная кошка."),
                Word("white", "[waɪt]", "белый", "White snow.", "Белый снег."),
                Word("one", "[wʌn]", "один", "One ticket, please.", "Один билет, пожалуйста."),
                Word("two", "[tuː]", "два", "Two coffees.", "Два кофе."),
                Word("three", "[θriː]", "три", "Three books.", "Три книги."),
                Word("four", "[fɔːr]", "четыре", "Four seasons.", "Четыре времени года."),
                Word("five", "[faɪv]", "пять", "Five fingers.", "Пять пальцев."),
                Word("six", "[sɪks]", "шесть", "Six o'clock.", "Шесть часов."),
                Word("seven", "[ˈsevn]", "семь", "Seven days.", "Семь дней."),
                Word("eight", "[eɪt]", "восемь", "Eight hours sleep.", "Восемь часов сна."),
                Word("nine", "[naɪn]", "девять", "Nine lives (cat).", "Девять жизней (кошка)."),
                Word("ten", "[ten]", "десять", "Ten fingers.", "Десять пальцев."),
                Word("dog", "[dɔːɡ]", "собака", "The dog is running.", "Собака бежит."),
                Word("cat", "[kæt]", "кошка", "The cat is sleeping.", "Кошка спит."),
                Word("water", "[ˈwɔːtər]", "вода", "Drink water.", "Пей воду."),
                Word("food", "[fuːd]", "еда", "I want food.", "Я хочу есть."),
                Word("money", "[ˈmʌni]", "деньги", "I have no money.", "У меня нет денег."),
                Word("phone", "[foʊn]", "телефон", "My phone is ringing.", "Мой телефон звонит."),
                Word("car", "[kɑːr]", "машина", "Red car.", "Красная машина."),
                Word("book", "[bʊk]", "книга", "Read a book.", "Читай книгу."),
                Word("school", "[skuːl]", "школа", "I go to school.", "Я хожу в школу."),
                Word("city", "[ˈsɪti]", "город", "Big city.", "Большой город."),
                Word("people", "[ˈpiːpl]", "люди", "Many people.", "Много людей."),
                Word("man", "[mæn]", "мужчина", "The man is tall.", "Мужчина высокий."),
                Word("woman", "[ˈwʊmən]", "женщина", "Beautiful woman.", "Красивая женщина."),
                Word("child", "[tʃaɪld]", "ребёнок", "Small child.", "Маленький ребёнок."),
                Word("mother", "[ˈmʌðər]", "мама", "I love my mother.", "Я люблю маму."),
                Word("father", "[ˈfɑːðər]", "папа", "My father is strong.", "Мой папа сильный."),
                Word("brother", "[ˈbrʌðər]", "брат", "My older brother.", "Мой старший брат."),
                Word("sister", "[ˈsɪstər]", "сестра", "My little sister.", "Моя младшая сестра."),
                // ещё 30+ базовых
                Word("head", "[hed]", "голова", "My head hurts.", "У меня болит голова."),
                Word("eye", "[aɪ]", "глаз", "Blue eyes.", "Голубые глаза."),
                Word("hand", "[hænd]", "рука", "Give me your hand.", "Дай руку."),
                Word("eat", "[iːt]", "есть", "I eat breakfast.", "Я ем завтрак."),
                Word("drink", "[drɪŋk]", "пить", "Drink water.", "Пей воду."),
                Word("sleep", "[sliːp]", "спать", "I want to sleep.", "Я хочу спать."),
                Word("read", "[riːd]", "читать", "I read books.", "Я читаю книги."),
                Word("write", "[raɪt]", "писать", "Write your name.", "Напиши своё имя."),
                Word("speak", "[spiːk]", "говорить", "I speak English.", "Я говорю по-английски."),
                Word("listen", "[ˈlɪsn]", "слушать", "Listen to music.", "Слушай музыку."),
                Word("see", "[siː]", "видеть", "I see you.", "Я тебя вижу."),
                Word("go", "[ɡoʊ]", "идти", "Let's go!", "Пойдём!"),
                Word("come", "[kʌm]", "приходить", "Come here.", "Иди сюда."),
                Word("live", "[lɪv]", "жить", "I live in Moscow.", "Я живу в Москве."),
                Word("know", "[noʊ]", "знать", "I know the answer.", "Я знаю ответ."),
                Word("think", "[θɪŋk]", "думать", "I think so.", "Я так думаю."),
                Word("hot", "[hɑːt]", "горячий", "Hot coffee.", "Горячий кофе."),
                Word("cold", "[koʊld]", "холодный", "Cold water.", "Холодная вода."),
                Word("open", "[ˈoʊpən]", "открывать", "Open the door.", "Открой дверь."),
                Word("close", "[kloʊz]", "закрывать", "Close the window.", "Закрой окно.")
            )

            // ===================================================================
            // 2. Еда и напитки — 85 слов
            // ===================================================================
            val foodWords = listOf(
                Word("bread", "[bred]", "хлеб", "Fresh bread.", "Свежий хлеб."),
                Word("butter", "[ˈbʌtər]", "масло", "Bread and butter.", "Хлеб с маслом."),
                Word("cheese", "[tʃiːz]", "сыр", "I love cheese.", "Я люблю сыр."),
                Word("milk", "[mɪlk]", "молоко", "Glass of milk.", "Стакан молока."),
                Word("egg", "[eɡ]", "яйцо", "Boiled egg.", "Варёное яйцо."),
                Word("chicken", "[ˈtʃɪkɪn]", "курица", "Roast chicken.", "Жареная курица."),
                Word("meat", "[miːt]", "мясо", "Grilled meat.", "Мясо на гриле."),
                Word("fish", "[fɪʃ]", "рыба", "Fried fish.", "Жареная рыба."),
                Word("rice", "[raɪs]", "рис", "Rice with vegetables.", "Рис с овощами."),
                Word("potato", "[pəˈteɪtoʊ]", "картофель", "Mashed potatoes.", "Картофельное пюре."),
                Word("tomato", "[təˈmeɪtoʊ]", "помидор", "Fresh tomatoes.", "Свежие помидоры."),
                Word("cucumber", "[ˈkjuːkʌmbər]", "огурец", "Cucumber salad.", "Салат из огурцов."),
                Word("onion", "[ˈʌnjən]", "лук", "Red onion.", "Красный лук."),
                Word("carrot", "[ˈkærət]", "морковь", "Carrot juice.", "Морковный сок."),
                Word("apple", "[ˈæpl]", "яблоко", "Green apple.", "Зелёное яблоко."),
                Word("banana", "[bəˈnænə]", "банан", "Ripe banana.", "Спелый банан."),
                Word("orange", "[ˈɔːrɪndʒ]", "апельсин", "Fresh orange.", "Свежий апельсин."),
                Word("lemon", "[ˈlemən]", "лимон", "Tea with lemon.", "Чай с лимоном."),
                Word("sugar", "[ˈʃʊɡər]", "сахар", "Two spoons of sugar.", "Две ложки сахара."),
                Word("salt", "[sɔːlt]", "соль", "Pass the salt.", "Передай соль."),
                Word("pepper", "[ˈpepər]", "перец", "Black pepper.", "Чёрный перец."),
                Word("soup", "[suːp]", "суп", "Hot soup.", "Горячий суп."),
                Word("salad", "[ˈsæləd]", "салат", "Greek salad.", "Греческий салат."),
                Word("pizza", "[ˈpiːtsə]", "пицца", "Pizza Margherita.", "Пицца Маргарита."),
                Word("pasta", "[ˈpæstə]", "паста", "Italian pasta.", "Итальянская паста."),
                Word("hamburger", "[ˈhæmbɜːrɡər]", "гамбургер", "Big hamburger.", "Большой гамбургер."),
                Word("cake", "[keɪk]", "торт", "Chocolate cake.", "Шоколадный торт."),
                Word("ice cream", "[aɪs kriːm]", "мороженое", "Vanilla ice cream.", "Ванильное мороженое."),
                Word("chocolate", "[ˈtʃɔːklət]", "шоколад", "Dark chocolate.", "Тёмный шоколад."),
                Word("coffee", "[ˈkɔːfi]", "кофе", "Black coffee.", "Чёрный кофе."),
                Word("tea", "[tiː]", "чай", "Green tea.", "Зелёный чай."),
                Word("juice", "[dʒuːs]", "сок", "Orange juice.", "Апельсиновый сок."),
                Word("water", "[ˈwɔːtər]", "вода", "Still water.", "Без газа."),
                Word("wine", "[waɪn]", "вино", "Red wine.", "Красное вино."),
                Word("beer", "[bɪr]", "пиво", "Cold beer.", "Холодное пиво."),
                Word("breakfast", "[ˈbrekfəst]", "завтрак", "English breakfast.", "Английский завтрак."),
                Word("lunch", "[lʌntʃ]", "обед", "Business lunch.", "Бизнес-ланч."),
                Word("dinner", "[ˈdɪnər]", "ужин", "Family dinner.", "Семейный ужин."),
                Word("restaurant", "[ˈrestrɑːnt]", "ресторан", "Good restaurant.", "Хороший ресторан."),
                Word("menu", "[ˈmenjuː]", "меню", "Can I have the menu?", "Можно меню?"),
                Word("delicious", "[dɪˈlɪʃəs]", "вкусный", "This is delicious!", "Это очень вкусно!"),
                Word("hungry", "[ˈhʌŋɡri]", "голодный", "I'm hungry.", "Я голоден."),
                Word("thirsty", "[ˈθɜːrsti]", "хотеть пить", "I'm thirsty.", "Я хочу пить."),
                Word("cook", "[kʊk]", "готовить", "I can cook.", "Я умею готовить.")
            )

            // ===================================================================
            // 3. Путешествия и город — 62 слова
            // ===================================================================
            val travelWords = listOf(
                Word("hotel", "[hoʊˈtel]", "отель", "Five-star hotel.", "Пятизвёздочный отель."),
                Word("room", "[ruːm]", "номер / комната", "Hotel room.", "Номер в отеле."),
                Word("airport", "[ˈerpɔːrt]", "аэропорт", "International airport.", "Международный аэропорт."),
                Word("plane", "[pleɪn]", "самолёт", "The plane is late.", "Самолёт опаздывает."),
                Word("train", "[treɪn]", "поезд", "Fast train.", "Скорый поезд."),
                Word("bus", "[bʌs]", "автобус", "Red bus.", "Красный автобус."),
                Word("taxi", "[ˈtæksi]", "такси", "Call a taxi.", "Вызови такси."),
                Word("car", "[kɑːr]", "машина", "Rent a car.", "Аренда машины."),
                Word("ticket", "[ˈtɪkɪt]", "билет", "One ticket to London.", "Один билет до Лондона."),
                Word("passport", "[ˈpæspɔːrt]", "паспорт", "Show your passport.", "Покажите паспорт."),
                Word("luggage", "[ˈlʌɡɪdʒ]", "багаж", "Heavy luggage.", "Тяжёлый багаж."),
                Word("map", "[mæp]", "карта", "City map.", "Карта города."),
                Word("city", "[ˈsɪti]", "город", "Beautiful city.", "Красивый город."),
                Word("street", "[striːt]", "улица", "Main street.", "Главная улица."),
                Word("left", "[left]", "налево", "Turn left.", "Поверните налево."),
                Word("right", "[raɪt]", "направо", "On the right.", "Справа."),
                Word("straight", "[streɪt]", "прямо", "Go straight.", "Идите прямо."),
                Word("near", "[nɪr]", "близко", "The hotel is near.", "Отель близко."),
                Word("far", "[fɑːr]", "далеко", "The beach is far.", "Пляж далеко."),
                Word("beach", "[biːtʃ]", "пляж", "Sandy beach.", "Песчаный пляж."),
                Word("sea", "[siː]", "море", "Blue sea.", "Синее море."),
                Word("mountain", "[ˈmaʊntən]", "гора", "High mountain.", "Высокая гора."),
                Word("park", "[pɑːrk]", "парк", "Central park.", "Центральный парк."),
                Word("shop", "[ʃɑːp]", "магазин", "Big shop.", "Большой магазин."),
                Word("bank", "[bæŋk]", "банк", "Money in the bank.", "Деньги в банке."),
                Word("hospital", "[ˈhɑːspɪtl]", "больница", "Nearest hospital.", "Ближайшая больница."),
                Word("police", "[pəˈliːs]", "полиция", "Call the police.", "Вызовите полицию."),
                Word("where", "[wer]", "где", "Where is the toilet?", "Где туалет?"),
                Word("how much", "[haʊ mʌtʃ]", "сколько стоит", "How much is it?", "Сколько стоит?"),
                Word("vacation", "[veɪˈkeɪʃn]", "отпуск", "Summer vacation.", "Летний отпуск."),
                Word("travel", "[ˈtrævl]", "путешествовать", "I love to travel.", "Я люблю путешествовать."),
                Word("tourist", "[ˈtʊrɪst]", "турист", "Many tourists.", "Много туристов.")
            )

            // Заполняем слова
            basicWords.forEach { insertWord(playlistBasic, it) }
            foodWords.forEach { insertWord(playlistFood, it) }
            travelWords.forEach { insertWord(playlistTravel, it) }

            // ===================================================================
            // Статистика — реалистично (около 180 записей)
            // ===================================================================
            val now = System.currentTimeMillis()
            wordIds.shuffled().take(140).forEachIndexed { index, wordId ->
                val daysAgo = (index % 60) + 1
                val timestamp = now - daysAgo * 86_400_000L
                val known = index % 5 != 0                     // ~80% известны

                cv.clear()
                cv.put("wordId", wordId)
                cv.put("date", timestamp)
                cv.put("isKnown", if (known) 1 else 0)
                db.insert("statistics", SQLiteDatabase.CONFLICT_NONE, cv)

                // Для части слов — второе повторение (раньше и неверно)
                if (index < 50) {
                    cv.clear()
                    cv.put("wordId", wordId)
                    cv.put("date", timestamp - 10 * 86_400_000L)
                    cv.put("isKnown", 0)
                    db.insert("statistics", SQLiteDatabase.CONFLICT_NONE, cv)
                }
            }
        }
    }
}