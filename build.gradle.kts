import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

group = "dev.codeerror"
version = "1.1.2"

apply(plugin = "com.github.johnrengelman.shadow")

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Minestom:Minestom:aebf72de90")
    implementation("net.kyori:adventure-text-minimessage:4.12.0")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "dev.codeerror.overflow.OverflowLimbo")
    }
}
