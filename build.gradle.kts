import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "dev.codeerror"
version = "1.1.1"

apply(plugin = "com.github.johnrengelman.shadow")

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Minestom:Minestom:18c46481f4")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "dev.codeerror.overflow.OverflowLimbo")
    }
}