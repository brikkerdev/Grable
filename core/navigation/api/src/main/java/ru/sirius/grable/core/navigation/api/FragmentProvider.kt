package ru.sirius.grable.core.navigation.api

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Provider interface for creating fragments with optional arguments.
 * Implementations can define custom creation logic without relying on specific method names.
 * 
 * Example usage in ModuleInitializer:
 * ```
 * factory<FragmentProvider>(named(Constants.MY_SCREEN)) {
 *     object : FragmentProvider {
 *         override fun create(arguments: Bundle?): Fragment {
 *             val id = arguments?.getLong("id", 0L) ?: 0L
 *             return MyFragment.newInstance(id)
 *         }
 *     }
 * }
 * ```
 * 
 * Then navigate with arguments:
 * ```
 * val args = Bundle().apply {
 *     putLong("id", playlistId)
 * }
 * navigationRouter.navigateToScreenByQualifier(Constants.MY_SCREEN, args)
 * ```
 */
interface FragmentProvider {
    /**
     * Creates a fragment instance with optional arguments.
     * @param arguments Optional bundle with arguments for the fragment
     * @return Fragment instance ready to be displayed
     */
    fun create(arguments: Bundle? = null): Fragment
}

