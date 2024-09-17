import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.codeerror"
version = "1.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:9fbff439e7")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "dev.codeerror.overflow.OverflowLimbo")
    }
}
