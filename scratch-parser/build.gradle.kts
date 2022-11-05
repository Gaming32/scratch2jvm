import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "io.github.gaming32.scratch2jvm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    explicitApiWarning()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10")

    compileOnly("org.jetbrains:annotations:23.0.0")

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.20")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}