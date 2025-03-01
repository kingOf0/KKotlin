import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.0-RC3"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("plugin.serialization") version "2.0.0-RC3"
}

group = "com.kingOf0"
version = "1.2.6"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.bg-software.com/repository/api/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")

    api("org.jetbrains.kotlin:kotlin-stdlib:2.0.0-RC3")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0-RC")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")

    implementation("com.github.cryptomorin:XSeries:12.1.0")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("de.tr7zw:item-nbt-api-plugin:2.14.1-SNAPSHOT")
}

tasks.processResources {
    expand("version" to project.version)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
    archiveFileName.set("KKotlin-${project.version}.jar")

    relocate("kotlin", "com.kingOf0.kotlin")
    relocate("kotlinx", "com.kingOf0.kotlinx")
    relocate("com.cryptomorin", "com.kingOf0.xseries")
    relocate("de.tr7zw.changeme", "com.kingOf0.tr7zw")

    exclude(
        "org/sqlite/native/Linux/**",
        "org/sqlite/native/Mac/**",
        "org/sqlite/native/FreeBSD/**",
        "org/sqlite/native/Linux-Alpine/**",
        "org/sqlite/native/DragonFlyBSD/**",
        "org/sqlite/native/Windows/x86/**",
        "org/sqlite/native/Windows/armv7/**",
        "com/cryptomorin/xseries/XSound.class",
        "javassist/**",
        "io/leangen/**",
        "LICENSE*"
    )

    exclude("META-INF/**")
    include("META-INF/services/**")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}