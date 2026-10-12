import org.gradle.jvm.tasks.Jar

// A provider is a jar Identica loads from its providers directory; this one packages like the official providers.
plugins {
    id("packaging-shaded-jar")
}

description = "Example Identica provider: players authenticate with a shared passphrase"

tasks.named<Jar>("shadowJar").configure {
    archiveBaseName.set("Example")
    archiveClassifier.set("")
}
