import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.7.20"
}

group = "io.github.gaming32.scratch2jvm"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("io.github.gaming32.scratch2jvm.compiler.MainKt")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    from({
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        excludes += "module-info.class"
        configurations.runtimeClasspath
            .get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

kotlin {
    explicitApiWarning()
}

dependencies {
    implementation(project(":scratch-parser"))
    implementation(project(":scratch-runtime"))

    implementation("com.github.char:Koffee:3a78d8a437")

    implementation("org.ow2.asm:asm:9.4")
    implementation("org.ow2.asm:asm-tree:9.4")
    testImplementation("org.ow2.asm:asm-util:9.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
