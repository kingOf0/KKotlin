import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id ("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("plugin.serialization") version "1.7.20"
}

group = "com.kingOf0"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://repo.bg-software.com/repository/api/")
}

dependencies {
    //spigot api
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    implementation("com.github.cryptomorin:XSeries:9.2.0")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("de.tr7zw:item-nbt-api-plugin:2.11.1-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.7.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
    relocate("kotlin", "com.kingOf0.kotlin")
    relocate("kotlinx", "com.kingOf0.kotlinx")

    relocate("com.cryptomorin", "com.kingOf0.xseries")
    relocate ("de.tr7zw", "com.kingOf0.tr7zw")

    exclude ( "org/sqlite/native/Linux/**",
        "org/sqlite/native/Mac/**",
        "org/sqlite/native/FreeBSD/**",
        "org/sqlite/native/Linux-Alpine/**",
        "org/sqlite/native/DragonFlyBSD/**",
        "org/sqlite/native/Windows/x86/**",
        "org/sqlite/native/Windows/armv7/**",
        "com/cryptomorin/xseries/particles/**",
        "com/cryptomorin/xseries/XSound.class",
        "javassist/**",
        "io/leangen/**",
        "LICENSE*",
        "META-INF/**"
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
