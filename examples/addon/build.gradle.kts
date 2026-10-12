// An addon is an ordinary Velocity plugin that compiles against Identica's API; Identica provides it at runtime.
plugins {
    java
}

description = "Example Velocity addon built on Identica's API"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

dependencies {
    compileOnly(projects.identicaApi)
    compileOnly(libs.velocity)

    annotationProcessor(libs.velocity)
}
