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
include(":network")
include(":libs:tts:api")
include(":libs:tts:embedded")
include(":feature:settings")
include(":feature:settings:api")
include(":feature:settings:impl")
