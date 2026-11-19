# Obabok Client

A multi-module Fabric project: a client mod, the **OUI** library and a set of modules bundled into a single jar.

## Structure

| Project | Description |
| --- | --- |
| `oui` | **OUI** library (`modid: oui`, package `ru.obabok.oui`) — UI widgets, configs, API (`ru.obabok.oui.OuiApi`) |
| `o-client` | Client mod (`modid: o-client`), depends on `:oui` |
| `o-base` | Combined mod (`modid: o-base`): fat-jar with all project modules |
| `modules/` | Standalone modules, each with its own `fabric.mod.json` |

All modules are picked up by the build automatically: `settings.gradle` scans directories containing a `build.gradle`, and `o-base` includes every module into the fat-jar.

## Versions

- Minecraft `26.3`, loader `0.19.5`, loom `1.17-SNAPSHOT`
- Fabric API `0.161.0+26.3`, Java `25`
- Shared versions live in the root `gradle.properties`

## Build

```bash
./gradlew build            # everything, jars go to build/libs/
./gradlew :o-base:build    # only the combined jar
```

Built jars: `build/libs/` (all modules + `o-base`).

Every push and pull request is built in CI, and the `o-base` fat-jar is uploaded as an artifact.

## Running the client

```bash
./gradlew :o-base:runClient
```

## New module

```bash
./create_module.sh o-my-module
```

Creates a module scaffold in `modules/o-my-module` (build.gradle, fabric.mod.json, mixins, config).
