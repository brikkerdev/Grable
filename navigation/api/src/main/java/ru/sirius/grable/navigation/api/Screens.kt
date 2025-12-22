package ru.sirius.grable.navigation.api

enum class Screens(
    val qualifier: String,
) {
    HOME(
        qualifier = ru.sirius.grable.feature.home.api.Constants.HOME_SCREEN
    ),
    LEARN(
        qualifier = ru.sirius.grable.feature.learn.api.Constants.LEARN_SCREEN
    ),
    SETTINGS(
        qualifier = ru.sirius.grable.feature.settings.api.Constants.SETTINGS_SCREEN
    )
}

