plugins {
    base
}

tasks.register("exampleModules") {
    group = "build"
    description = "Builds the example addon and the example provider."

    dependsOn(
        ":example-addon:build",
        ":example-provider:build"
    )
}
