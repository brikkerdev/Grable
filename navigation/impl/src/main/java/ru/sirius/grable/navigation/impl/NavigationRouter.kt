package ru.sirius.grable.navigation.impl

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.get
import ru.sirius.grable.navigation.api.NavigationRouter as NavigationRouterApi
import ru.sirius.grable.navigation.api.Screens

class NavigationRouter(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) : NavigationRouterApi {
    override fun navigateToScreen(screen: Screens) {
        navigateToScreenByQualifier(screen.qualifier)
    }
    
    override fun navigateToScreenByQualifier(qualifier: String) {
        val fragmentType = get(Class::class.java, named(qualifier)) as? Class<out Fragment>

        fragmentType?.let {
            fragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.slide_out_right
                )
                .replace(containerId, it.newInstance(), null)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
    
    override fun navigateToFragment(fragment: Fragment) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.slide_out_right
            )
            .replace(containerId, fragment, null)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}


