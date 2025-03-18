# Room Database в Android

## Описание
Проект демонстрирует использование библиотеки **Room** в Android для работы с локальной базой данных SQLite.
Room предоставляет удобный ORM-слой, упрощающий доступ к данным без необходимости написания сложных SQL-запросов.

## Технологии
- **Java** — основной язык разработки
- **Android Room** — библиотека для работы с базой данных
- **LiveData** — реактивное обновление UI
- **ViewModel** — управление данными

## Установка
1. Добавьте зависимости в `build.gradle`:
   
   implementation(libs.room.runtime)
   annotationProcessor(libs.room.compiler)

2. Создайте **Entity (Таблицу)**:

   ```java
   @Entity(tableName = "word_table")
   public class Word {
       @PrimaryKey
       @NonNull
       private String word;
   }
   ```

3. Определите **DAO (Data Access Object)**:

   ```java
   @Dao
   public interface WordDao {
       @Insert
       void insert(Word word);
       
       @Query("SELECT * FROM word_table ORDER BY word ASC")
       LiveData<List<Word>> getAllWords();
   }
   ```

4. Создайте **Room Database**:

   ```java
   @Database(entities = {Word.class}, version = 1)
   public abstract class WordRoomDatabase extends RoomDatabase {
       public abstract WordDao wordDao();
   }
   ```

5. Реализуйте **ViewModel**:

   ```java
   public class WordViewModel extends AndroidViewModel {
       private final WordRepository repository;
       private final LiveData<List<Word>> allWords;
   }
   ```



