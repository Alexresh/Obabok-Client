# OUI

Мультимодовый Gradle-проект: библиотека + тестовый мод в одном репозитории.

## Модули

- `oui` — библиотека **OUI** (`modid: oui`, пакет `ru.obabok.oui`)
  - API-класс: `ru.obabok.oui.OuiApi`
  - Энтрипоинты: `OuiInit` (main), `OuiClientInit` (client)
- `o-client` — тестовый мод (`modid: o-client`, пакет `ru.obabok.o_client`)
  - Зависит от `:oui` через `modImplementation project(':oui')`
  - В `fabric.mod.json` объявлена зависимость `"oui": "*"`

## Версии (как в AreaScanner / ApolloCarpetAdditions)

- Minecraft `26.3`, loader `0.19.5`, loom `1.17-SNAPSHOT`
- Fabric API `0.161.0+26.3`, Java `25`
- Общие версии лежат в корневом `gradle.properties`

## Сборка

```bash
./gradlew build          # оба мода
./gradlew :oui:build     # только библиотека
./gradlew :o-client:build  # только тестовый мод
```

Готовые jar: `oui`, `o-client`.

## Требования к JDK

Нужен полный JDK 25 (с `javac`). Если `java` по умолчанию — JRE без компилятора:

```bash
export JAVA_HOME=/usr/lib/jvm/jdk-25.0.2
./gradlew build
```

## Запуск клиента

```bash
./gradlew :o-client:runClient   # тестмод + библиотека на classpath
./gradlew :oui:runClient           # только библиотека
```
