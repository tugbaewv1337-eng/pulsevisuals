# Pulse Visuals

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-brightgreen)]()
[![Fabric](https://img.shields.io/badge/Fabric-Loader-blue)]()
[![Java](https://img.shields.io/badge/Java-21%2B-orange)]()
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

Pulse Visuals — это мод для Minecraft 1.21.4 на базе Fabric, который добавляет визуальные эффекты для PvP, вдохновленный мотором [Pulse Visuals](https://www.curseforge.com/minecraft/mods/pulse-visuals).

## 🎨 Основные возможности

### 1. **Hit Particles** — Частицы при попадании
- Кастомные частицы при ударе по блокам и игрокам
- Разные цвета для обычных ударов (золотой) и критических ударов (красный)
- Анимированное исчезновение частиц
- Настраиваемый размер и время жизни

### 2. **Target HUD** — Информация о цели
- Отображение HP и максимального HP целевого игрока
- Показ уровня брони
- Ник игрока
- Расстояние до цели
- Автоматическое обновление при наведении

### 3. **Damage Numbers** — Числа урона
- Отображение нанесённого урона в виде всплывающих чисел
- Разные цвета:
  - 💛 Золотой: обычный урон
  - 🔴 Красный: критический урон
  - 💚 Зелёный: исцеление
- Плавная анимация всплытия
- Исчезновение с эффектом прозрачности

### 4. **Critical Hit Effect** — Эффект крита
- Визуальный эффект при критическом ударе
- Специальная анимация и цветовое выделение
- Чёткое различие обычного и критического урона

### 5. **Trajectory Prediction** — Предсказание траектории
- Линия предсказания траектории для стрел и других снарядов
- Учёт гравитации и воздушного сопротивления
- Помощь в целеполаганию при стрельбе

## 📋 Системные требования

- **Minecraft версия**: 1.21.4
- **Fabric Loader**: 0.16.9 или выше
- **Java**: 21 или выше
- **IDE**: IntelliJ IDEA (рекомендуется)

## 🚀 Установка и настройка

### 1. Клонирование репозитория

```bash
git clone https://github.com/Lopata12958/PulseVisuals.git
cd PulseVisuals
```

### 2. Сборка проекта

```bash
# Windows
.\gradlew build

# Linux/Mac
./gradlew build
```

Локация собранного JAR файла: `build/libs/pulsevisuals-1.0.0.jar`

### 3. Установка мода

1. Скопируйте JAR файл в папку `mods` вашего Minecraft клиента
2. Запустите Minecraft с Fabric профилем
3. Мод будет автоматически загружен

## 📁 Структура проекта

```
PulseVisuals/
├── src/main/
│   ├── java/com/pulse/visuals/
│   │   ├── PulseVisualsClient.java          # Главная точка входа
│   │   ├── client/
│   │   │   ├── event/
│   │   │   │   └── ClientEventHandler.java  # Обработчик событий
│   │   │   ├── visual/
│   │   │   │   ├── HitParticle.java
│   │   │   │   ├── HitParticleRenderer.java
│   │   │   │   ├── TargetHUD.java
│   │   │   │   ├── DamageNumber.java
│   │   │   │   ├── DamageNumberRenderer.java
│   │   │   │   └── TrajectoryRenderer.java
│   │   │   ├── util/
│   │   │   │   └── RenderUtils.java        # Утилиты для рендеринга
│   │   │   └── config/
│   │   │       └── ModConfig.java           # Конфигурация
│   │   └── mixin/
│   │       ├── ClientPlayerInteractionMixin.java
│   │       └── WorldRendererMixin.java
│   └── resources/
│       ├── fabric.mod.json                   # Метаданные мода
│       └── pulsevisuals.mixins.json         # Конфигурация миксинов
├── gradle.properties                         # Версии зависимостей
├── build.gradle                              # Конфигурация сборки
└── README.md                                 # Этот файл
```

## 🔧 Основные компоненты

### PulseVisualsClient

Главная точка входа мода. Инициализирует все рендеры и обработчики событий.

```java
public class PulseVisualsClient implements ClientModInitializer {
    public void onInitializeClient() {
        // Инициализация всех визуальных компонентов
    }
}
```

### ClientEventHandler

Обработчик событий, который слушает события игрового цикла и рендеринга.

```java
public void onClientTick(MinecraftClient client) {
    // Обновления каждый тик
}

public void onWorldRenderEnd(WorldRenderContext context) {
    // Рендеринг визуальных эффектов
}
```

### HitParticleRenderer

Отвечает за создание и рендеринг частиц при попадании.

### TargetHUD

Отслеживает и отображает информацию о целевом игроке.

### DamageNumberRenderer

Рендерит всплывающие числа урона.

### TrajectoryRenderer

Предсказывает и отображает траекторию снарядов.

## ⚙️ Конфигурация

Все настройки находятся в классе `ModConfig.java`:

```java
// Включить/отключить компоненты
public static boolean ENABLE_HIT_PARTICLES = true;
public static boolean ENABLE_TARGET_HUD = true;
public static boolean ENABLE_DAMAGE_NUMBERS = true;
public static boolean ENABLE_TRAJECTORY_PREDICTION = true;

// Масштаб и размеры
public static float HIT_PARTICLE_SCALE = 1.0f;
public static float DAMAGE_NUMBER_SCALE = 1.0f;

// Время жизни эффектов (в тиках)
public static int HIT_PARTICLE_LIFETIME = 25;
public static int DAMAGE_NUMBER_LIFETIME = 40;
```

## 🎮 Использование

### Добавление Hit Particles

```java
PulseVisualsClient.getHitParticleRenderer()
    .addHitParticle(position, isCritical);
```

### Добавление Damage Numbers

```java
PulseVisualsClient.getDamageNumberRenderer()
    .addDamageNumber(position, damage, isCritical);
```

### Работа с Target HUD

```java
TargetHUD targetHUD = PulseVisualsClient.getTargetHUD();
if (targetHUD.hasTarget()) {
    float health = targetHUD.getTargetHealth();
    String name = targetHUD.getTargetName();
}
```

## 🛠️ Расширение функциональности

### Добавление новых эффектов

1. Создайте новый класс в папке `client/visual/`
2. Наследуйте от базовых классов частиц/эффектов
3. Добавьте инициализацию в `PulseVisualsClient.java`
4. Зарегистрируйте рендеринг в `ClientEventHandler.java`

### Создание миксинов

1. Создайте класс, аннотированный `@Mixin`
2. Используйте `@Inject` для внедрения кода
3. Зарегистрируйте в `pulsevisuals.mixins.json`

## 📝 Примеры

### Пример 1: Создание кастомной частицы

```java
public class CustomParticle {
    private Vec3d position;
    private int lifetime;
    
    public CustomParticle(Vec3d position, int lifetime) {
        this.position = position;
        this.lifetime = lifetime;
    }
    
    public void update() {
        lifetime--;
    }
}
```

### Пример 2: Регистрация события

```java
ClientTickEvents.END_CLIENT_TICK.register(client -> {
    // Ваш код
});
```

## 🐛 Известные проблемы

- Частицы могут не отображаться правильно на некоторых видеокартах
- Траектория может быть неточна на высоких пингах
- Target HUD может мерцать при быстром движении камеры

## 📞 Поддержка

Если вы столкнулись с проблемой:

1. Проверьте консоль для ошибок
2. Убедитесь, что версия Minecraft и Fabric совместимы
3. Откройте Issue на GitHub

## 🤝 Вклад

Приветствуются любые Pull Requests! Пожалуйста:

1. Форкните репозиторий
2. Создайте ветку для вашей фичи
3. Коммитьте изменения
4. Отправьте Pull Request

## 📄 Лицензия

Проект лицензирован под MIT License — см. [LICENSE](LICENSE) для подробностей.

## 🙏 Благодарности

- Спасибо сообществу Fabric за превосходный фреймворк
- Вдохновение от оригинального Pulse Visuals мода
- Все контрибьюторы проекта

## 🔗 Ссылки

- [GitHub Repository](https://github.com/Lopata12958/PulseVisuals)
- [Fabric Documentation](https://fabricmc.net/)
- [Minecraft Wiki](https://minecraft.wiki/)
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)

---

**Версия**: 1.0.0  
**Последнее обновление**: December 8, 2025  
**Автор**: Your Name
