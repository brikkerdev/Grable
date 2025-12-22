package ru.sirius.grable.navigation.impl

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.get
import ru.sirius.grable.navigation.api.FragmentProvider
import ru.sirius.grable.navigation.api.NavigationRouter as NavigationRouterApi
import ru.sirius.grable.navigation.api.Screens

class NavigationRouter(
    private val fragmentManager: FragmentManager,
    private val containerId: Int
) : NavigationRouterApi {
    override fun navigateToScreen(screen: Screens) {
        navigateToScreenByQualifier(screen.qualifier, null)
    }
    
    override fun navigateToScreenByQualifier(qualifier: String) {
        navigateToScreenByQualifier(qualifier, null)
    }
    
    override fun navigateToScreenByQualifier(qualifier: String, arguments: Bundle?) {
        val fragmentProvider: FragmentProvider? = try {
            get(FragmentProvider::class.java, named(qualifier)) as FragmentProvider
        } catch (e: Exception) {
            null
        }
        
        val fragment = if (fragmentProvider != null) {
            fragmentProvider.create(arguments)
        } else {
            val fragmentType = try {
                get(Class::class.java, named(qualifier)) as? Class<out Fragment>
            } catch (e: Exception) {
                null
            }
            
            fragmentType?.let { createFragmentFromClass(it, arguments) } ?: return
        }

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
    
    private fun createFragmentFromClass(fragmentClass: Class<out Fragment>, arguments: Bundle?): Fragment {
        return try {
            val methodWithBundle = fragmentClass.getMethod("newInstance", Bundle::class.java)
            methodWithBundle.invoke(null, arguments) as Fragment
        } catch (e: NoSuchMethodException) {
            try {
                val methodNoParams = fragmentClass.getMethod("newInstance")
                val fragment = methodNoParams.invoke(null) as Fragment
                arguments?.let { fragment.arguments = it }
                fragment
            } catch (e: NoSuchMethodException) {
                val fragment = fragmentClass.newInstance()
                arguments?.let { fragment.arguments = it }
                fragment
            }
        } catch (e: Exception) {
            val fragment = fragmentClass.newInstance()
            arguments?.let { fragment.arguments = it }
            fragment
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


