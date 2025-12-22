package ru.sirius.grable.feature.learn.api

/**
 * Argument keys for LearnFragment navigation.
 * These constants are used when navigating to Learn screen with arguments.
 * 
 * Example usage:
 * ```
 * val args = Bundle().apply {
 *     putLong(LearnArgs.PLAYLIST_ID, playlistId)
 * }
 * navigationRouter.navigateToScreenByQualifier(Constants.LEARN_SCREEN, args)
 * ```
 */
object LearnArgs {
    /**
     * Key for playlist ID argument.
     */
    const val PLAYLIST_ID = "playlist_id"
}

