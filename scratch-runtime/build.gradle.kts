plugins {
    id("java")
}

group = "io.github.gaming32.scratch2jvm"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.1"
val jomlVersion = "1.10.5"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    for (library in listOf("lwjgl", "lwjgl-glfw", "lwjgl-nanovg", "lwjgl-opengl")) {
        implementation("org.lwjgl", library)
        for (natives in listOf(
            "linux", "linux-arm64", "linux-arm32",
            "macos", "macos-arm64",
            "windows", "windows-x86", "windows-arm64"
        )) {
            implementation("org.lwjgl", library, classifier = "natives-$natives")
        }
    }

    implementation("org.joml", "joml", jomlVersion)
}
