import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.10"
    id ("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("plugin.serialization") version "1.8.10"
}

group = "com.kingOf0"
version = "1.1.5"

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
    //bungee
    compileOnly("net.md-5:bungeecord-api:1.16-R0.4-SNAPSHOT")

    implementation("com.github.cryptomorin:XSeries:9.3.0")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("de.tr7zw:item-nbt-api-plugin:2.11.2-SNAPSHOT")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
}

tasks.processResources {
    expand("version" to project.version)
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
    archiveFileName.set("KKotlin-${project.version}.jar")

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
