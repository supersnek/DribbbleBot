import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30"
    kotlin("kapt") version "1.3.30"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "DribbbleBot"
version = "1.0"


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.telegram:telegrambots:4.2")
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("com.squareup.moshi:moshi:1.8.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.8.0")

    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("junit:junit:4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<ShadowJar> {
    manifest {
        attributes(mapOf("Main-Class" to "MainKt"))
    }
    baseName = "bot"
    classifier = ""
    version = ""
}

