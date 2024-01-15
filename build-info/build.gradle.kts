plugins {
    id("net.kyori.blossom") version "2.1.0"                     // Placeholder injection
}

sourceSets {
    main {
        blossom {
            val variables = mapOf(
                "name" to rootProject.name,
                "version" to "${project.version}",
                "description" to project.description,
                "git_short" to (env["GIT_COMMIT_SHORT_HASH"] ?: "unknown"),
                "git_full" to (env["GIT_COMMIT_LONG_HASH"] ?: "unknown")
            )

            kotlinSources {
                variables.forEach(this::property)
            }

            resources {
                variables.forEach(this::property)
            }
        }
    }
}