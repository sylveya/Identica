plugins {
    id("module-feature")
}

dependencies {
    api(projects.featureVerificationApi)

    implementation(projects.featureVerificationCommon)
    implementation(projects.featureVerificationDatabase)

    compileOnly(projects.identicaAdapterDatabaseApi)
}

toolkitPublish {
    artifactId.set("verification")
}
