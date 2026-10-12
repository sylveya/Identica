plugins {
    id("module-api")
}

group = "me.whereareiam.identica"

dependencies {
    api(projects.identicaApi)

    // Schemas are Dialectica tables and Strata migrations; Identica provides both at runtime.
    compileOnlyApi(libs.dialectica)
    compileOnlyApi(libs.strata.adapter.database)
}

toolkitPublish {
    artifactId.set("database-api")

    pom {
        description.set("Database contracts of Identica: the database service and the schemas of its owners")
        name.set("Identica Database API")
    }

    javadoc {
        title.set("Identica Database API")
        windowTitle.set("Identica Database API")
    }
}
