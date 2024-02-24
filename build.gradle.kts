import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.codeerror"
version = "1.3"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("net.minestom:minestom-snapshots:7320437640")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")
    implementation("org.slf4j:slf4j-simple:2.0.12")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "dev.codeerror.overflow.OverflowLimbo")
    }
}
