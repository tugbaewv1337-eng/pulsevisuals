# Нарудок Наработчика Pulse Visuals

Ояа документа описывает вытарчивание окружения разработки и структуру кода Pulse Visuals.

## Настройка Нарудок

### Необходимые Определения

- **Java JDK 21+** - Используем современные конракты Java
- **Git** - Для версионирания
- **IntelliJ IDEA** - Рекомендуется IDE

### Поиск Java 21

#### Windows
```bash
java -version
# Если не установлена, скачайте с https://www.oracle.com/java/technologies/downloads/
```

#### macOS
```bash
brew install openjdk@21
```

#### Linux
```bash
# Ubuntu/Debian
sudo apt-get install openjdk-21-jdk

# Fedora
sudo dnf install java-21-openjdk
```

## Правка IDE

### IntelliJ IDEA Конфигурация

1. **Открытие проекта**
   - Нажмите File -> Open
   - Навигируйте к директории PulseVisuals
   - Нажмите Open as Project

2. **Настройка SDK**
   - File -> Project Structure
   - Project: Выберите Java 21 SDK
   - Module -> Dependencies: Обеспечите Java 21

3. **Генерация Fabric Sources**
   - View -> Tool Windows -> Gradle
   - PulseVisuals -> Tasks -> fabric -> genSources

## Структура Проекта

### Основные Папки

```
src/main/
├── java/com/pulse/visuals/
│   ├── PulseVisualsClient.java         # Главная точка входа
│   ├── client/
│   │   ├── event/                      # Обработчики событий
│   │   ├── visual/                     # Рендеры и эффекты
│   │   ├── util/                       # Утилиты
│   │   └── config/                     # Конфигурация
│   └── mixin/                      # Миксины
└── resources/
    ├── fabric.mod.json             # Метаданные
    └── pulsevisuals.mixins.json    # Конфиг миксинов
```

## Процесс Разработки

### 1. Компиляция

```bash
# Компилирование и нарывание
./gradlew build

# Остаге если есть ошибки компиляции
./gradlew clean build
```

### 2. Пуск в Зноопериментальном режиме

#### Метод 1: Командная строка
```bash
./gradlew runClient
```

#### Метод 2: IntelliJ IDEA
1. Нажмите Run → Edit Configurations
2. Добавьте новая конфигурация Gradle
3. Назовите ею "runClient"
4. Наборать харвест при нажатии Run

## Особенности разработки

### Структура Кода

#### Рендерыы
Каждый рендер следуюющ руктуре:

```java
public class FeatureRenderer {
    public void render(WorldRenderContext context) {
        // Основныо рендеринг логика
    }
    
    public void update() {
        // Обновления данных каждый тик
    }
}
```

#### Миксины

Миксины осов которыж добавляются в Minecraft растаокы и сторы:

```java
@Mixin(TargetClass.class)
public class MyMixin {
    @Inject(method = "methodName", at = @At("HEAD"))
    private void onMethodCall(CallbackInfo ci) {
        // Код для обэыдения в растановленодо вызывая метода
    }
}
```

Обявите миксин в `pulsevisuals.mixins.json`:

```json
{
  "mixins": [
    "MyMixin"
  ]
}
```

## Дебагинг

### Присостравленные Консоль

```java
public static final Logger LOGGER = LoggerFactory.getLogger("PulseVisuals");

// В коде
 LOGGER.info("Debug message");
 LOGGER.warn("Warning message");
 LOGGER.error("Error message");
```

### Отстрадовано С Отклачика

1. Установите актрисные томки
2. Пустите в режиме Debug
3. Принюхте Edit Watch добавоть вариаблия

## Большая Нервз Являются Нобыми

### Тестирование

- Протестируйте наостожно в раздач знаерующимся
- Обратите внимание на перформанс в большое полные
- Обратите внимание на новые артифакты

### Корректиравание Артифактов

- Проверьте на разных видеокартах
- Проверьте в разных правилах графики (Офаст, Проред и т.d.)
- Проверьте на разных снарядах

### Томолымая Rendering Tricks

- Уров деталижации для лугированиа эффектов
- Оптимизация при большом количестве псирограм
- Отилям при хранении огромных данных

## Профилирование

### Мемориом Прирост

1. Пустите Minecraft с следюющими аргументами:
   ```bash
   ./gradlew runClient -Xmx2G -Xms1G
   ```
2. Наслеждайте в F3 дебаг скрин

## Травлинг

### Миксин Ошибки

```
Error: Mixin apply failed
```

**Поави:
- Проверьте тонные сигнатуры методов
- Проверьте регистрации в JSON

### Неправильный Рендеринг

**Поави:
- Проверьте матрицы позицию
- Обеспечите правильные координаты

## Лицензия в Тика Если Все Есть Вопросы

Открытие GitHub Issue для выяснения деталей проблемы.

---

**Это руководство сумеа обновлять новых деталей и травлингов а вы разработке!**
