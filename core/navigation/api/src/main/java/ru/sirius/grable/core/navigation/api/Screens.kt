package ru.sirius.grable.core.navigation.api

enum class Screens(
    val qualifier: String,
) {
    HOME(
        qualifier = ru.sirius.grable.feature.home.api.Constants.HOME_SCREEN
    ),
    LEARN(
        qualifier = ru.sirius.grable.feature.learn.api.Constants.LEARN_SCREEN
    ),

    LEARN_PLAYLIST(
        qualifier = ru.sirius.grable.feature.learn.api.Constants.LEARN_PLAYLIST_SCREEN
    ),

    SETTINGS(
        qualifier = ru.sirius.grable.feature.settings.api.Constants.SETTINGS_SCREEN
    ),

    STATS(
        qualifier = ru.sirius.grable.feature.progress.api.Constants.STATS_SCREEN
    ),
    ADD_WORD(
        qualifier = ru.sirius.grable.feature.add_word.api.Constants.ADD_WORD_SCREEN
    ),

    SELECT_PLAYLIST(
        qualifier = ru.sirius.grable.feature.playlist.api.Constants.SELECT_PLAYLIST_SCREEN
    ),
}

