plugins {
    id("java")
}

group = "io.github.gaming32.scratch2jvm"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:-unused")
}
