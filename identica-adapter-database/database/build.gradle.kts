plugins {
    id("module-java")
}

// Wires the database family into Identica's injector; the implementation is in database-common.
dependencies {
    implementation(projects.identicaAdapterDatabaseCommon)

    compileOnly(projects.identicaAdapterDatabaseApi)
    compileOnly(libs.jdbi.core)
    compileOnly(libs.jdbi.sqlobject)

    testImplementation(projects.identicaAdapterDatabaseApi)
    testImplementation(libs.jdbi.core)
}
