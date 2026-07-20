plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.9"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    // PlaceholderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    // LuckPerms (optionale Abhängigkeit)
    maven("https://repo.lucko.me/")
    // ProtocolLib (optionale Abhängigkeit, für teambasierte Glow-Sichtbarkeit)
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    // Optionale Abhängigkeiten (soft-depend, siehe plugin.yml)
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.luckperms:api:5.4")
    // Wird benötigt, damit der Glow-Effekt WIRKLICH nur für das eigene Team sichtbar
    // ist (Paketfilterung). Ohne ProtocolLib auf dem Server bleibt Glow deaktiviert,
    // damit niemals ein Spieler eines fremden Clans durch Wände sichtbar wird.
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")

    implementation("com.zaxxer:HikariCP:5.1.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.withType<JavaCompile> {
    // Verhindert, dass javac für String-Konkatenation "invokedynamic"-Bytecode
    // (StringConcatFactory) erzeugt. Das Shadow-Plugin (com.gradleup.shadow) hat
    // beim Relozieren von Klassen mit dieser Art von Bytecode einen bekannten Bug
    // (MissingMethodException: Remapper.mapValue ... Handle ...). Mit diesem Flag
    // kompiliert javac stattdessen klassische StringBuilder-Konkatenation.
    options.compilerArgs.add("-XDstringConcat=inline")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.10")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version, "description" to project.description)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")

        // Eingebundene Bibliotheken relozieren, um Klassenkonflikte mit anderen
        // Plugins zu vermeiden, die ebenfalls HikariCP nutzen könnten.
        // Hinweis: org.sqlite wird bewusst NICHT reloziert, da der SQLite-JDBC-Treiber
        // native Bibliotheken (JNI) lädt, deren Ladepfad beim Relozieren brechen kann.
        relocate("com.zaxxer.hikari", "de.Z7534.clansystem.libs.hikari")
    }

    build {
        dependsOn(shadowJar)
    }
}
