pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url= "https://jitpack.io")
    }
}

rootProject.name = "Grable"
include(":app")
include(":libs:di")
include(":libs:imageloader:api")
include(":libs:imageloader:coil")
include(":libs:network")
include(":libs:tts:api")
include(":libs:tts:embedded")
include(":core:design")
include(":core:database")
include(":navigation:api")
include(":navigation:impl")
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:add_word:api")
include(":feature:learn:api")
include(":feature:learn:impl")
include(":feature:progress:api")
include(":feature:settings:api")
include(":feature:settings:impl")
include(":feature:progress:impl")
