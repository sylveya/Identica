plugins {
    id("module-java")
}

// Shaded like bStats was: bStats refuses to run unrelocated, and FastStats tells Identica's errors
// from other plugins' by the class loader that defined them.
dependencies {
    implementation(libs.statistica.adapter.jdbc)
    implementation(libs.statistica.common)

    compileOnly(projects.featureVerificationApi)
    compileOnly(projects.identicaAdapterDatabaseApi)

    testImplementation(projects.identicaAdapterDatabaseCommon)
    testImplementation(testFixtures(projects.identicaAdapterDatabaseCommon))
}
