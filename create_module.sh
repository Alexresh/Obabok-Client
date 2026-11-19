#!/usr/bin/env bash
# Использование: ./new_module.sh o-hud-tools
set -euo pipefail

NAME="$1"                                        # id модуля
CAMEL="$(sed -E 's/(^|-)([a-z])/\U\2/g' <<< "$NAME")"   # o-hud-tools -> OHudTools
PKGNAME="$(tr -d '-' <<< "$NAME")"               # ohudtools
VERSION="${PKGNAME}_version"
PKG="ru/obabok/$PKGNAME"                        # <- замени на свой пакет
DIR="modules/$NAME"
LEVEL="JAVA_21"                                  # поставь как в твоих текущих модулях

[ -d "$DIR" ] && { echo "Модуль $NAME уже существует"; exit 1; }

mkdir -p "$DIR/src/main/java/$PKG/mixin" "$DIR/src/main/resources/assets/$NAME/lang"

cat > "$DIR/build.gradle" <<EOF
plugins {
    id 'net.fabricmc.fabric-loom' version "\${loom_version}"
    id 'maven-publish'
}
version = $VERSION
group = maven_group

base {
	archivesName = '$NAME'
}

loom {
	mods {
		'$NAME' {
			sourceSet sourceSets.main
	  }
  }

	runConfigs.configureEach {
          getGenerateRunConfig().set(false)
  }
}
dependencies {
  minecraft("com.mojang:minecraft:\${minecraft_version}")
	implementation("net.fabricmc:fabric-loader:\${loader_version}")
	implementation("net.fabricmc.fabric-api:fabric-api:\${fabric_api_version}")

	implementation(project(":oui"))
	implementation(project(":o-client"))
}

processResources {
    def version = project.version
    inputs.property "version", version

    filesMatching("fabric.mod.json") {
        expand "version": version
    }
}

jar {
  archiveAppendix = minecraft_version
}
EOF

cat > "$DIR/gradle.properties" <<EOF
$VERSION = 1.0.0
EOF

cat > "$DIR/src/main/resources/fabric.mod.json" <<EOF
{
	"schemaVersion": 1,
	"id": "$NAME",
	"version": "\${version}",
	"name": "$CAMEL",
	"authors": [
  	"Obabok"
  ],
	"environment": "*",
	"entrypoints": {
		"client": ["ru.obabok.$PKGNAME.${CAMEL}ModuleInit"]
	},
	"mixins": [
  		{
  			"config": "$NAME.mixins.json"
  		}
	]
}
EOF

cat > "$DIR/src/main/resources/$NAME.mixins.json" <<EOF
{
	"required": true,
	"package": "ru.obabok.$PKGNAME.mixin",
	"compatibilityLevel": "$LEVEL",
	"mixins": [],
	"client": [],
	"injectors": {
		"defaultRequire": 1
	},
	"overwrites": {
      "requireAnnotations": true
    }
}
EOF

cat > "$DIR/src/main/java/$PKG/${CAMEL}ModuleInit.java" <<EOF
package ru.obabok.$PKGNAME;

import net.fabricmc.api.ClientModInitializer;
import ru.obabok.oui.newConfig.OConfig;
import ru.obabok.oui.newConfig.Product;

public class ${CAMEL}ModuleInit implements ClientModInitializer {
	public static ${CAMEL}Config config;
	@Override
	public void onInitializeClient() {
		config = OConfig.register("${CAMEL}", ${CAMEL}Config.class, Product.ID + ".config.module");
	}
}
EOF

cat > "$DIR/src/main/java/$PKG/${CAMEL}Config.java" <<EOF
package ru.obabok.$PKGNAME;

import ru.obabok.oui.config.OConfigInstance;
import ru.obabok.oui.config.annotation.ConfigValue;
import ru.obabok.oui.config.annotation.Module;

public class ${CAMEL}Config implements OConfigInstance {

	@Module
	public ${CAMEL}Module ${CAMEL} = new ${CAMEL}Module();

	public static class ${CAMEL}Module {

	}
}
EOF

cat > "$DIR/src/main/resources/assets/$NAME/lang/en_us.json" <<EOF
{

}
EOF

echo "Модуль $NAME создан"