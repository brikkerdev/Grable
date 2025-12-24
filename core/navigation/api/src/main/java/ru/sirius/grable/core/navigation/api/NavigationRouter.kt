package ru.sirius.grable.core.navigation.api

import android.os.Bundle
import androidx.fragment.app.Fragment

interface NavigationRouter {
    fun navigateToScreen(screen: Screens)
    
    /**
     * Navigate to a screen by its qualifier string.
     * Useful for navigating to non-modularized screens or screens registered in Koin.
     */
    fun navigateToScreenByQualifier(qualifier: String)
    
    /**
     * Navigate to a screen by qualifier with arguments.
     * @param qualifier Screen qualifier from feature API Constants
     * @param arguments Optional bundle with arguments for the fragment
     */
    fun navigateToScreenByQualifier(qualifier: String, arguments: Bundle?)
    
    /**
     * Navigate to a fragment directly.
     * Useful for navigating to non-modularized fragments that are not registered in Koin.
     */
    fun navigateToFragment(fragment: Fragment)
}

