plugins {
    id("module-java")
}

dependencies {
    compileOnly(projects.providerCredentialApi)
    compileOnly(projects.identicaAdapterDatabaseApi)
    compileOnly(libs.jdbi.core)
    compileOnly(libs.jdbi.sqlobject)

    testImplementation(projects.providerCredentialApi)
    testImplementation(projects.identicaAdapterDatabaseCommon)
    testImplementation(testFixtures(projects.identicaAdapterDatabaseCommon))
}
