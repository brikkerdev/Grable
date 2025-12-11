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
include(":feature:text_to_speech:api")
include(":feature:text_to_speech:embedded")
include(":network")
